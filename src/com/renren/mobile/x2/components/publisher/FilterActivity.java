package com.renren.mobile.x2.components.publisher;

import Pinguo.Android.SDK.Image360JNI;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.FileUtil;
import com.renren.mobile.x2.utils.img.ImageUtil;
import com.renren.mobile.x2.utils.img.TransformImageView;
import com.renren.mobile.x2.utils.log.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 滤镜类
 *
 * @author zhenning.yang
 */
public class FilterActivity extends Activity {
    private final static String CAMERA360_SDK_KEY = "273719ECF170994E969C400D4FEC72B8";
    private final static int FILTER_PREVIEW_SIZE = 100;
    private final static Rect FILTER_PREVIEW_RECT = new Rect(0, 0, FILTER_PREVIEW_SIZE, FILTER_PREVIEW_SIZE);
    private final static int MIN_GLOBAL_BUFFER_SIZE = FILTER_PREVIEW_SIZE * FILTER_PREVIEW_SIZE;
    private final static int MAX_PIXELS = 500000;
    private final static Logger log = new Logger("FilterActivity");

    private final static String[] NEEDED_EXIF = {
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
    };

    private TransformImageView contentImageView;
    private Image360JNI originalImage360JNI;
    private Bitmap originalBitmap;
    private Bitmap filteredBitmap;
    private byte[] globalBuffer;
    private Map<String, String> exifInfo = null;

    private Matrix matrix = new Matrix();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publisher_filter);
        contentImageView = (TransformImageView) findViewById(R.id.filter_processedImage);
        final LinearLayout filterPreviewContainer = (LinearLayout) findViewById(R.id.filter_container);
        final Button finishButton = (Button) findViewById(R.id.filter_finish);
        final Button backButton = (Button) findViewById(R.id.filter_back);
        final ImageButton clockwiseRotateButton = (ImageButton) findViewById(R.id.filter_clockwise_rotate);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                String path = getIntent().getStringExtra("destination");
                saveFilteredImage(path, false);
                data.putExtra("success", true);
                setResult(PublisherFragment.REQUESTCODE_FILTER, data);
                finish();
            }
        });
        clockwiseRotateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                matrix.postRotate(90, contentImageView.getWidth() / 2, contentImageView.getHeight() / 2);
                contentImageView.matrix.postRotate(90, contentImageView.getWidth() / 2, contentImageView.getHeight() / 2);
                contentImageView.invalidate();
                int preViewCount = filterPreviewContainer.getChildCount();
                for (int i = 0; i < preViewCount; i++) {
                    View inner = filterPreviewContainer.getChildAt(i);
                    TransformImageView img = (TransformImageView) inner.findViewWithTag("img");
                    img.matrix.postRotate(90, img.getWidth() / 2, img.getHeight() / 2);
                    img.invalidate();

                }

            }
        });
        // 原始图片
        getOriginalBitmap();
        // 预览
        final List<Filter> mFilterList = getFiltersPreview(originalBitmap);
        // 初始化滤镜
        iniFilters();
        log.printd("iniFilters() OK, imageHeight=%d", originalImage360JNI.GetImageHeight());
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        log.printd("init filtered bitmap is mutable %b", filteredBitmap.isMutable());

        setImage360JNIFilterToBitmap(originalImage360JNI, "effect=renren_hdpro", filteredBitmap);
        log.d("getFiltersPreview() OK");

        LayoutInflater inflater = getLayoutInflater();
        int filterCount = mFilterList.size();
        for (int i = 0; i < filterCount; i++) {
            final Filter f = mFilterList.get(i);
            View v = inflater.inflate(R.layout.filter_preview, null);
            final LinearLayout imgContainer = (LinearLayout) v.findViewById(R.id.preview_container);
            imgContainer.setTag("bg");
            if (i != 0) {
                imgContainer.setBackgroundColor(Color.TRANSPARENT);
            }
            TransformImageView img = (TransformImageView) v.findViewById(R.id.preview_image);
            TextView text = (TextView) v.findViewById(R.id.filter_name);
            img.setImageBitmap(f.preview);
            img.setTag("img");
            text.setText(f.name);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL;
            v.setLayoutParams(lp);
            filterPreviewContainer.addView(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeFilter(f);
                    //切换滤镜选中效果
                    int preViewCount = filterPreviewContainer.getChildCount();
                    for (int i = 0; i < preViewCount; i++) {
                        View inner = filterPreviewContainer.getChildAt(i);
                        View bg = inner.findViewWithTag("bg");
                        if (bg != null) {
                            bg.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    imgContainer.setBackgroundResource(R.drawable.publish_filter_previea_select);
                }
            });
        }
        contentImageView.setImageBitmap(originalBitmap);
    }

    private void changeFilter(Filter filter) {
        boolean changeFilteredBitmapResult = setImage360JNIFilterToBitmap(originalImage360JNI, filter.effectName, filteredBitmap);
        log.printd("change filtered bitmap %b", changeFilteredBitmapResult);
        contentImageView.setImageBitmap(filteredBitmap);
    }

    private boolean saveFilteredImage(String imagePath, boolean saveExif) {
        Bitmap bitmap = filteredBitmap;
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }

        try {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            final FileOutputStream fos = new FileOutputStream(imagePath);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)) {
                FileUtil.justClose(fos);
                if (saveExif) {
                    saveExif(imagePath, bitmap.getWidth(), bitmap.getHeight());
                }
                return true;
            } else {
                FileUtil.justClose(fos);
                log.w("compress bitmap to jpg failed.");
            }
        } catch (FileNotFoundException e) {
            log.w("save filtered image failed.", e);
        }
        return false;
    }

    /**
     * 得到原始图片，并且对图片进行了预处理
     */
    private void getOriginalBitmap() {
        final String originalBitmapPath = getOriginalBitmapPath();
        log.d("path--------->" + originalBitmapPath);
        if (originalBitmapPath != null) {
            collectExif(originalBitmapPath);
            originalBitmap = ImageUtil.decodeFile(originalBitmapPath, MAX_PIXELS * 2);
        }

        if (originalBitmap != null) {
            final int oldW = originalBitmap.getWidth();
            final int oldH = originalBitmap.getHeight();
            final int currentPixels = oldW * oldH;
            log.printd("originalBitmap, currentPixels %d", currentPixels);
            if (currentPixels > MAX_PIXELS) {
                final Bitmap old = originalBitmap;
                final int newW = oldW * MAX_PIXELS / currentPixels;
                final int newH = oldH * MAX_PIXELS / currentPixels;
                originalBitmap = Bitmap.createBitmap(newW, newH, Bitmap.Config.ARGB_8888);

                final Canvas canvas = new Canvas(originalBitmap);
                canvas.drawBitmap(old, null, new Rect(0, 0, newW, newH), null);
                old.recycle();
            } else if (originalBitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                final Bitmap old = originalBitmap;
                originalBitmap = old.copy(Bitmap.Config.ARGB_8888, false);
                old.recycle();
            }
        }

        if (originalBitmap != null) {
            final int currentW = originalBitmap.getWidth();
            final int currentH = originalBitmap.getHeight();
            final int currentPixels = currentW * currentH;

            log.printd("new originalBitmap: w=%d, h=%d, pixels=%d", currentW, currentH, currentPixels);
            globalBuffer = new byte[4 * Math.max(MIN_GLOBAL_BUFFER_SIZE, currentPixels)];
            log.d("ini global buffer");
        }
    }

    private String getOriginalBitmapPath() {
        final String path = getIntent().getStringExtra("path");
        if (path != null) {
            return path;
        }
        final Uri uri = getIntent().getData();
        if (uri != null) {
            log.d("uri is not null");
            final Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (cursor != null) {
                log.d("cursor is not null");
                try {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            }
        }

        return null;
    }

    /**
     * 初始化滤镜
     */
    private void iniFilters() {
        originalImage360JNI = createImage360JNI("original");
        boolean setImageResult = setBitmapToImage360JNI(originalBitmap, originalImage360JNI);
        log.printd("initFilters, SetImageFromRgbaIntArray %b", setImageResult);
    }

    private List<Filter> getFiltersPreview(Bitmap original) {
        final Image360JNI thumbImage360JNI = createImage360JNI("thumb");
        final Bitmap thumb = getThumb(original);

        final List<Filter> mFilters = new ArrayList<Filter>();
        final Filter originalFilter = new Filter();
        final String[] names = this.getResources().getStringArray(R.array.camera_filter_names);
        final String[] effects = this.getResources().getStringArray(R.array.camera_filter_effects);

        originalFilter.name = names[0];
        originalFilter.effectName = effects[0];
        originalFilter.preview = thumb;
        log.d("add original");
        mFilters.add(originalFilter);

        if (setBitmapToImage360JNI(thumb, thumbImage360JNI)) {
            assert names.length == effects.length;

            for (int i = 1, length = names.length; i < length; i++) {
                final Filter filter = new Filter();
                filter.name = names[i];
                filter.effectName = effects[i];

                final int[] data = thumbImage360JNI.ProcessEffectImage(filter.effectName, false);
                final int thumbWidth = thumbImage360JNI.GetImageWidth();
                final int thumbHeight = thumbImage360JNI.GetImageHeight();
                filter.preview = Bitmap.createBitmap(data, 0, thumbWidth, thumbWidth, thumbHeight, Bitmap.Config.ARGB_8888);

                log.printd("thumb %s created", filter.effectName);
                mFilters.add(filter);
            }
            thumbImage360JNI.FreeImage();
        }
        return mFilters;
    }

    /**
     * 对原始图片进行缩放得到缩略图
     */
    private Bitmap getThumb(Bitmap original) {
        if (original == null) {
            return null;
        }

        final int oriW = original.getWidth();
        final int oriH = original.getHeight();

        int tx=0, ty=0, tw=0, th=0;
        if(oriW > oriH) {
            tw = th = oriH;
            tx = (oriW - oriH) / 2;
        } else if(oriH > oriW) {
            tw = th = oriW;
            ty = (oriH - oriW) / 2;
        } else {
            tw = oriW;
            th = oriH;
        }

        try {
            final Bitmap thumb = Bitmap.createBitmap(FILTER_PREVIEW_SIZE, FILTER_PREVIEW_SIZE, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(thumb);
            canvas.drawBitmap(original, new Rect(tx, ty, tx+tw, ty+th), FILTER_PREVIEW_RECT, null);
            return thumb;
        } catch (OutOfMemoryError e) {
            log.w("create thumb failed.", e);
            return null;
        }
    }

    /**
     * 创建Image360JNI
     */
    private Image360JNI createImage360JNI(String logPrefix) {
        final Image360JNI jni = new Image360JNI();

        boolean testResult = 100 == jni.test(10);
        assert testResult;

        boolean verifyResult = jni.Verify(CAMERA360_SDK_KEY);
        assert verifyResult;

        return jni;
    }

    private boolean setBitmapToImage360JNI(Bitmap bitmap, Image360JNI image360JNI) {
        if (bitmap == null || image360JNI == null) {
            log.w("null bitmap or image360jni");
            return false;
        }

        final Bitmap.Config cfg = bitmap.getConfig();
        if (cfg != Bitmap.Config.ARGB_8888) {
            log.printw("bitmap config is not ARGB_8888, is %s", cfg);
            return false;
        }

        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        final int pixelCount = w * h;

        final ByteBuffer buffer = ByteBuffer.wrap(globalBuffer);
        bitmap.copyPixelsToBuffer(buffer);

        return image360JNI.SetImageFromRgbaIntArray(buffer.array(), pixelCount, w, h);
    }

    private boolean setImage360JNIFilterToBitmap(Image360JNI image360JNI, String param, Bitmap bitmap) {
        if (image360JNI == null || bitmap == null) {
            log.w("jni or bitmap is null");
            return false;
        }

        if (bitmap.isRecycled()) {
            log.w("bitmap is recycled");
            return false;
        }

        if (!bitmap.isMutable()) {
            log.w("bitmap is not mutable");
            return false;
        }

        final int bmpW = bitmap.getWidth();
        final int jniW = image360JNI.GetImageWidth();
        if (bmpW != jniW) {
            log.printw("bmpW(%d) != jniW(%d)", bmpW, jniW);
            return false;
        }

        final int bmpH = bitmap.getHeight();
        final int jniH = image360JNI.GetImageHeight();
        if (bmpH != jniH) {
            log.printw("bmpH(%d) != jniH(%d)", bmpH, jniH);
            return false;
        }

        final int[] data = image360JNI.ProcessEffectImage(param, false);
        bitmap.setPixels(data, 0, bmpW, 0, 0, bmpW, bmpH);
        return true;
    }

    private void collectOneExif(ExifInterface exif, String attr) {
        assert exif != null;
        assert attr != null;
        String value = exif.getAttribute(attr);
        if (value != null) {
            exifInfo.put(attr, value);
        }
    }

    private boolean collectExif(String path) {
        try {
            final ExifInterface exif = new ExifInterface(path);
            exifInfo = new HashMap<String, String>();
            for (String tag : NEEDED_EXIF) {
                collectOneExif(exif, tag);
            }
        } catch (IOException e) {
            log.w("collect exif failed.", e);
        }
        return false;
    }

    private boolean saveExif(String path, int width, int height) {
        if (exifInfo == null || exifInfo.isEmpty()) {
            return true;
        }

        try {
            final ExifInterface exif = new ExifInterface(path);
            for (Map.Entry<String, String> oneExif : exifInfo.entrySet()) {
                exif.setAttribute(oneExif.getKey(), oneExif.getValue());
            }
            exif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, Integer.toString(width));
            exif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, Integer.toString(height));
            exif.saveAttributes();
        } catch (IOException e) {
            log.w("collect exif failed.", e);
        }
        return false;
    }

    /**
     * 释放
     */
    public void finalizeFilter() {
        originalImage360JNI.FreeImage();
        if (originalBitmap != null && !originalBitmap.isRecycled()) {
            originalBitmap.recycle();
        }
        if (filteredBitmap != null && !filteredBitmap.isRecycled()) {
            filteredBitmap.recycle();
        }
        System.gc();
    }
}

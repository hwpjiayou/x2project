package com.renren.mobile.x2.utils.img;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.img.branch.SDK12;
import com.renren.mobile.x2.utils.img.branch.SDK7;
import com.renren.mobile.x2.utils.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片工具类
 */
public class ImageUtil {
    private static final Logger log = new Logger("ImageUtil");
    private static final int MAX_QUALITY = 720;
    private static final Matrix IDENTITY = new Matrix();

    public static Bitmap decodeInputStream(InputStream inputStream, int maxWidth, int maxHeight) {
        if (inputStream == null) {
            return null;
        }

        if (inputStream.markSupported()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream.mark(1024 * 1024); // mark 1M
            BitmapFactory.decodeStream(inputStream, null, options);
            try {
                inputStream.reset();
            } catch (IOException e) {
                log.d("input stream, reset failed.");
                return null;
            }
            return BitmapFactory.decodeStream(inputStream, null, generalOptions(options, maxWidth, maxHeight));
        } else {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            /* echo */
            {
                final byte[] buffer = new byte[4096];
                int read = 0;
                try {
                    while ((read = inputStream.read(buffer)) >= 0) {
                        if (read > 0) {
                            bos.write(buffer, 0, read);
                        }
                    }
                } catch (IOException e) {
                    log.d("save input stream data, read failed.", e);
                }
            }

            final byte[] data = bos.toByteArray();
            return decodeByteArray(data, 0, data.length, maxWidth, maxHeight);
        }
    }

    public static Bitmap decodeInputStream(InputStream inputStream, int maxPixels) {
        if (inputStream == null) {
            return null;
        }

        if (inputStream.markSupported()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream.mark(1024 * 1024); // mark 1M
            BitmapFactory.decodeStream(inputStream, null, options);
            try {
                inputStream.reset();
            } catch (IOException e) {
                log.d("input stream, reset failed.");
                return null;
            }
            return BitmapFactory.decodeStream(inputStream, null, generalOptions(options, maxPixels));
        } else {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            /* echo */
            {
                final byte[] buffer = new byte[4096];
                int read = 0;
                try {
                    while ((read = inputStream.read(buffer)) >= 0) {
                        if (read > 0) {
                            bos.write(buffer, 0, read);
                        }
                    }
                } catch (IOException e) {
                    log.d("save input stream data, read failed.", e);
                }
            }

            final byte[] data = bos.toByteArray();
            return decodeByteArray(data, 0, data.length, maxPixels);
        }
    }

    public static Bitmap decodeFile(String path, int maxWidth, int maxHeight) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return BitmapFactory.decodeFile(path, generalOptions(options, maxWidth, maxHeight));
    }

    public static Bitmap decodeFile(String path, int maxPixels) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return BitmapFactory.decodeFile(path, generalOptions(options, maxPixels));
    }

    public static Bitmap decodeResource(int resId, int maxWidth, int maxHeight) {
        return decodeResource(RenrenChatApplication.getApplication().getResources(), resId, maxWidth, maxHeight);
    }

    public static Bitmap decodeResource(Resources resources, int resId, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        return BitmapFactory.decodeResource(resources, resId, generalOptions(options, maxWidth, maxHeight));
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, int maxWidth, int maxHeight) {
        if (data == null) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length);
        return BitmapFactory.decodeByteArray(data, offset, length, generalOptions(options, maxWidth, maxHeight));
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, int maxPixels) {
        if (data == null) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length);
        return BitmapFactory.decodeByteArray(data, offset, length, generalOptions(options, maxPixels));
    }

    public static Bitmap decodeInputStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        final DisplayMetrics dm = RenrenChatApplication.getApplication().getResources().getDisplayMetrics();
        return decodeInputStream(inputStream, dm.widthPixels, dm.heightPixels);
    }

    public static Bitmap decodeFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        final DisplayMetrics dm = RenrenChatApplication.getApplication().getResources().getDisplayMetrics();
        return decodeFile(path, dm.widthPixels, dm.heightPixels);
    }

    public static Bitmap decodeResource(int resId) {
        final DisplayMetrics dm = RenrenChatApplication.getApplication().getResources().getDisplayMetrics();
        return decodeResource(resId, dm.widthPixels, dm.heightPixels);
    }

    public static Bitmap decodeResource(Resources resources, int resId) {
        final DisplayMetrics dm = RenrenChatApplication.getApplication().getResources().getDisplayMetrics();
        return decodeResource(resources, resId, dm.widthPixels, dm.heightPixels);
    }

    public static Bitmap decodeByteArray(byte[] data) {
        if (data == null) {
            return null;
        }

        final DisplayMetrics dm = RenrenChatApplication.getApplication().getResources().getDisplayMetrics();
        return decodeByteArray(data, 0, data.length, dm.widthPixels, dm.heightPixels);
    }

    public static BitmapFactory.Options generalOptions(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = 1;

        if (options.outWidth > maxWidth || options.outHeight > maxHeight) {
            if (options.outWidth > options.outHeight) {
                options.inSampleSize = (int) Math.ceil((float) options.outHeight / (float) maxHeight);
            } else {
                options.inSampleSize = (int) Math.ceil((float) options.outWidth / (float) maxWidth);
            }
        }
        return options;
    }

    public static BitmapFactory.Options generalOptions(BitmapFactory.Options options, int maxPixels) {
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = 1;

        final int currentPixels = options.outWidth * options.outHeight;
        if (currentPixels > maxPixels) {
            options.inSampleSize = (int) Math.ceil((float) currentPixels / (float) maxPixels);
        }
        return options;
    }

    public static BitmapDrawable createSafeBitmapDrawable(Bitmap bitmap) {
        return new BitmapDrawable(RenrenChatApplication.getApplication().getResources(), bitmap) {
            @Override
            public void draw(Canvas canvas) {
                final Bitmap bitmap = getBitmap();
                if (!(bitmap == null || bitmap.isRecycled())) {
                    super.draw(canvas);
                }
            }
        };
    }

    public static void setBitmapToImageView(Bitmap bitmap, ImageView imageView) {
        assert imageView != null;
        assert Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId();
        imageView.setImageDrawable(new SafeBitmapDrawable(RenrenChatApplication.getApplication().getResources(), bitmap));
    }

    public static boolean isImageViewBitmapValid(ImageView imageView) {
        assert imageView != null;
        final Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return false;
        }

        if (drawable instanceof BitmapDrawable) {
            final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmap != null && !bitmap.isRecycled();
        }

        return true;
    }

    public static Bitmap transformBitmap(Bitmap origin, Matrix matrix, int width, int height) {
        if (origin == null) {
            return null;
        }
        Bitmap transformed;
        try {
            transformed = Bitmap.createBitmap(width, height, origin.getConfig());
        } catch (OutOfMemoryError ignored) {
            return null;
        }

        final Canvas canvas = new Canvas(transformed);
        if (matrix != null) {
            canvas.drawBitmap(origin, matrix, null);
        } else {
            canvas.drawBitmap(origin, IDENTITY, null);
        }
        return transformed;
    }

    public static Bitmap transformBitmap(Bitmap origin, TRS trs, int width, int height) {
        if (origin == null) {
            return null;
        }
        Bitmap transformed;
        try {
            transformed = Bitmap.createBitmap(width, height, origin.getConfig());
        } catch (OutOfMemoryError ignored) {
            return null;
        }

        final Canvas canvas = new Canvas(transformed);
        if (trs != null) {
            trs.transformCanvas(canvas);
        }
        canvas.drawBitmap(origin, IDENTITY, null);
        return transformed;
    }

    public static int getBitmapByteCount(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }

        if (Build.VERSION.SDK_INT >= 12) {
            return SDK12.getByteCount(bitmap);
        } else {
            return SDK7.getByteCount(bitmap);
        }
    }

    public static boolean uploadBitmap(Bitmap bitmap, INetResponse response, boolean highQuality) {
        assert bitmap != null;
        assert response != null;

        final int oldW = bitmap.getWidth();
        final int oldH = bitmap.getHeight();
        final float ratio = oldW > oldH ? (oldW / (float) oldH) : (oldH / (float) oldW);
        int newW = 0;
        int newH = 0;
        boolean needScale = false;

        if (ratio > 3) {
            final int min = highQuality ? 490 : 1024;
            final int currentMin = Math.min(oldW, oldH);
            if (currentMin > min) {
                needScale = true;
                newW = oldW * min / currentMin;
                newH = oldH * min / currentMin;
            }
        } else {
            final int max = 1024;
            final int currentMax = Math.max(oldW, oldH);
            if (currentMax > max) {
                needScale = true;
                newW = oldW * max / currentMax;
                newH = oldH * max / currentMax;
            }
        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean wroteOk;
        int bitmapQuality = 80;//highQuality ? 100 : 80;
        if (needScale) {
            final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
            try {
                wroteOk = scaled.compress(Bitmap.CompressFormat.JPEG, bitmapQuality, outputStream);
            } finally {
                scaled.recycle();
            }
        } else {
            wroteOk = bitmap.compress(Bitmap.CompressFormat.JPEG, bitmapQuality, outputStream);
        }

        if (wroteOk) {
            HttpMasService.getInstance().uploadPhoto(response, outputStream.toByteArray());
        }

        return wroteOk;
    }

    public static Bitmap CreateOvalBitmap(Bitmap input) {
        if(input == null) {
            return null;
        }

        final int w = input.getWidth();
        final int h = input.getHeight();

        final Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT);

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new BitmapShader(input, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        canvas.drawOval(new RectF(0, 0, w, h), paint);
        return result;
    }

    /**
     * @param filePath       存储路径
     * @param maxNumOfPixels
     * @return 长宽最大的位图
     *         <p/>
     *         description 目前本地选取图片、转发功能在使用
     */
    public static Bitmap image_Compression(String filePath, float maxNumOfPixels) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        /* update by yuchao.zhang 更新上传图片的分辨率压缩策略，最大长宽为720 */
//		opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, maxNumOfPixels);
        int widthRatio = (int) Math.ceil(opts.outWidth / MAX_QUALITY);
        int heightRatio = (int) Math.ceil(opts.outHeight / MAX_QUALITY);
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        opts.inJustDecodeBounds = false;

//		opts.inPreferredConfig = Bitmap.Config.ARGB_4444;

        Bitmap bmp = BitmapFactory.decodeFile(filePath, opts);

        bmp = scaleImg(bmp, MAX_QUALITY);

        return bmp;
    }

    /**
     * @param filePath 存储路径
     * @param dm       DisplayMetrics
     * @return 长宽最大为720的位图
     *         <p/>
     *         description 目前拍照功能在使用
     * @author yuchao.zhang(更新为新方法)
     */
    public static Bitmap image_Compression(String filePath, DisplayMetrics dm) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        /* update by yuchao.zhang 更新上传图片的分辨率压缩策略，最大长宽为720 */
//        int intScreenX=dm.widthPixels;
//        int intScreenY=dm.heightPixels;
//        int intWidth=intScreenX-40;
//        int intHeight=intScreenY-80;
//
//		int bmHeight=opts.outHeight;
//		int bmWidth=opts.outWidth;
//
//		int scaleW=bmWidth/intWidth;
//		int scaleH=bmHeight/intHeight;
//		int scale=scaleW>scaleH?scaleW:scaleH;
//		if(scale<=0)
//			scale=1;
//		opts.inSampleSize = scale;
        int widthRatio = (int) Math.ceil(opts.outWidth / MAX_QUALITY);
        int heightRatio = (int) Math.ceil(opts.outHeight / MAX_QUALITY);
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }

        opts.inJustDecodeBounds = false;

//		opts.inPreferredConfig = Bitmap.Config.ARGB_4444;

        Bitmap bmp = BitmapFactory.decodeFile(filePath, opts);

        bmp = scaleImg(bmp, MAX_QUALITY);

        return bmp;
    }

    private static Bitmap scaleImg(Bitmap img, float quality) {

        if (img == null) {
            return null;
        }
        float sHeight = 0;
        float sWidth = 0;

        Bitmap result = img;

        if (img.getWidth() >= img.getHeight() && img.getWidth() > quality) {

            sWidth = quality;
            sHeight = (quality * img.getHeight()) / img.getWidth();
            result = Bitmap.createScaledBitmap(img, (int) sWidth, (int) sHeight, true);

            if (null != img && !img.isRecycled() && !img.equals(result)) {
                img.recycle();
            }
        } else if (img.getHeight() > img.getWidth() && img.getHeight() > quality) {

            sHeight = quality;
            sWidth = (quality * img.getWidth()) / img.getHeight();
            result = Bitmap.createScaledBitmap(img, (int) sWidth, (int) sHeight, true);

            if (null != img && !img.isRecycled() && !img.equals(result)) {
                img.recycle();
            }
        }

        return result;
    }
}

class SafeBitmapDrawable extends BitmapDrawable {
    @Deprecated
    public SafeBitmapDrawable() {
        super();
    }

    public SafeBitmapDrawable(Resources res) {
        super(res);
    }

    @Deprecated
    public SafeBitmapDrawable(Bitmap bitmap) {
        super(bitmap);
    }

    public SafeBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    @Deprecated
    public SafeBitmapDrawable(String filepath) {
        super(filepath);
    }

    public SafeBitmapDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    @Deprecated
    public SafeBitmapDrawable(InputStream is) {
        super(is);
    }

    public SafeBitmapDrawable(Resources res, InputStream is) {
        super(res, is);
    }

    @Override
    public void draw(Canvas canvas) {
        final Bitmap bitmap = getBitmap();
        if (!(bitmap == null || bitmap.isRecycled())) {
            super.draw(canvas);
        }
    }
}

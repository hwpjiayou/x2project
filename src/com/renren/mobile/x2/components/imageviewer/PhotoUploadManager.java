package com.renren.mobile.x2.components.imageviewer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;

/**
 * author yuchao.zhang
 * <p/>
 * description 作为上传图片的管理类，提供了如下信息：
 * 1.发送意图需要携带的requestCode（区分为拍照、本地选取图片、裁切等）
 * 2.弹出的Dialog，使用createUploadDialog前，需要初始化一些数据，详情参照该方法的介绍
 * 3.拍照、本地选取图片、裁切图片的意图
 * 4.裁切前后的图片本地Uri，如果需要取得裁切后的，需要调用getContentValues（）后，插入数据，详情参考该方法
 */
public class PhotoUploadManager {

    private static PhotoUploadManager sInstance = new PhotoUploadManager();

    public static PhotoUploadManager getInstance() {

        return sInstance;
    }

    /**
     * 类型：头像拍照上传
     */
    public static final int REQUEST_CODE_HEAD_TAKE_PHOTO = 100;

    /**
     * 类型：头像本地上传
     */
    public static final int REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY = 101;

    /**
     * 类型：裁切图片
     */
    public static final int REQUEST_CODE_CUT = 102;

    /**
     * 裁切图片的Action
     */
    public static final String ACTION_CAMERA_CROP = "com.android.camera.action.CROP";

    /**
     * 创建上传图片的Dialog
     *
     * @param context             Dialog展示的activity
     * @param title               Dialog的标题
     * @param items               Dialog的条目内容
     * @param dialogClickListener Dialog的点击事件
     *                            <p/>
     *                            使用方法：调用方法前，一定要初始化title、items、dialogClickListener，否则Dialog无内容
     *                            注意：items的长度，要与dialogClickListener中事件个数 相同并且一一对应
     */
    public void createUploadDialog(Context context, String title, String[] items, DialogInterface.OnClickListener dialogClickListener) {

        new AlertDialog.Builder(context)

                .setTitle(title)

                .setItems(items, dialogClickListener).show();
    }

    /**
     * 通过拍照得到的Uri
     */
    public Uri mUri;

    /**
     * 得到系统拍照的意图
     * <p/>
     * 使用方法：拍照后，可以在onActivityResult中直接拿到mUri，获取地址
     *
     * @param path     SD卡的路径
     * @param fileName 文件名（如Renren_ + 系统当时时间）
     * @param format   文件格式（如.jpg）
     * @return Intent
     */
    public Intent getTakePhotoIntent(String path, String fileName, String format) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String sdFilePath = Environment.getExternalStorageDirectory() + path;
        File filePath = new File(sdFilePath);
        if (!filePath.exists()) {

            filePath.mkdir();
        }
        File mFile = new File(filePath, fileName + format);
        mUri = Uri.fromFile(mFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        return intent;
    }

    /**
     * 得到系统本地选取图片的意图
     *
     * @return Intent
     */
    public Intent getChooseFromGalleryIntent() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
    }

    /**
     * 得到系统裁切图片的意图
     *
     * @param uri 通过拍照、本地选取图片后得到的Uri或data.getData()
     * @return Intent
     */
    public Intent getPhotoCutIntent(Uri uri) {

        Intent intent = new Intent(ACTION_CAMERA_CROP);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        if ("MEIZU MX".equals(Build.MODEL) || "M9".equals(Build.MODEL)) {

            intent.putExtra("outputX", 255);
            intent.putExtra("outputY", 255);
        } else {

            intent.putExtra("outputX", 360);
            intent.putExtra("outputY", 360);
        }
        intent.putExtra("return-data", true);
        return intent;
    }

    /**
     * 注意：得到ContentValues后，需要调用如下语句：
     * activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
     *
     * @param data                通过拍照或本地选取图片后，得到的Intent
     * @param fileName            文件名（通常使用前缀Renren_ + 当时系统时间）
     * @param format              文件格式
     * @param descriptionForImage 文件描述
     * @return 返回ContentValues
     */
    public ContentValues getContentValues(Intent data, String path, String fileName, String format, String descriptionForImage) {

        Bundle extras = data.getExtras();

        if (extras != null) {

            Bitmap photo = extras.getParcelable("data");
            try {

                ContentValues values = new ContentValues();
                String sdFilePath = Environment.getExternalStorageDirectory() + path;
                File filePath = new File(sdFilePath);
                if (!filePath.exists()) {

                    filePath.mkdir();
                }
                File mCutFile = new File(filePath, fileName + format);
                if (mCutFile != null) {

                    FileOutputStream fo = new FileOutputStream(mCutFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, fo);
                    fo.close();
                    values.put(MediaStore.Images.Media.DATA, mCutFile.getPath());
                    values.put(MediaStore.Images.Media.DESCRIPTION,
                            descriptionForImage);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                }
                return values;
            } catch (Exception e) {

                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 得到裁切后图片的Uri
     *
     * @param path     文件路径
     * @param fileName 文件名（通常使用前缀Renren_ + 系统当时时间）
     * @param format   文件格式（如.jpg）
     * @return 返回Uri
     */
    public Uri getUri(String path, String fileName, String format) {

        File mCutFile = null;

        try {

            String sdFilePath = Environment.getExternalStorageDirectory() + path;
            File filePath = new File(sdFilePath);
            if (!filePath.exists()) {

                filePath.mkdir();
            }
            mCutFile = new File(filePath, fileName + format);
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return Uri.fromFile(mCutFile);
    }

    //TODO 下面的代码需要优化，产品文档确定后即可开工！（需要做：旋转、缩放、取比例值、联调接口）

    public byte[] bytes;
    public Bitmap bitmap;
    /**
     * 当前压缩比例
     */
    public int currentOptionsSampleSize;
    /**
     * 通过Uri得到图片
     * update by yuchao.zhang 添加OOM的catch，如果溢出，则压缩比例增加
     *
     * @param context
     * @param uri     图片Uri
     * @return
     */
    public Bitmap getBitmap(Context context, Uri uri) {

        InputStream is = null;
        try {

            is = context.getContentResolver().openInputStream(uri);
            bytes = Methods.toByteArray(is);

            if(is != null){
                is.close();
                is = null;
            }
        } catch (OutOfMemoryError ex){/*  */

            CommonUtil.toast("");
        } catch (Exception e) {

            e.printStackTrace();
        }

        try{

            bitmap = optimizeBitmap(bytes, maxQuality, maxQuality, false);
        } catch(OutOfMemoryError oom){

            if(null != bitmap && !bitmap.isRecycled()){

//                bitmap.recycle();
//                System.gc();
            }
            currentOptionsSampleSize += 1;
            bitmap = optimizeBitmap(bytes, maxQuality, maxQuality, true);
        } catch (Exception e){

            e.printStackTrace();
        }

        return bitmap;
    }
    

    /**
     * 最大宽度
     */
    private static final int maxQuality = 1024;

    /**
     * 用目前市面上最常用的方法，创建图片
     * 优点：安全性高，此方法会大大降低OutOfMemory的出现几率
     * update by yuchao.zhang 添加OOM的catch，如果溢出，则压缩比例增加
     *
     * @param resource
     * @param maxWidth
     * @param maxHeight
     * @param reOptimize 是否需要重新生成
     * @return
     */
    private Bitmap optimizeBitmap(byte[] resource, int maxWidth, int maxHeight, boolean reOptimize) {

        if(!reOptimize){

            currentOptionsSampleSize = 1;
        }
        Bitmap result = null;
        int length = resource.length;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
        	result = BitmapFactory.decodeByteArray(resource, 0, length, options);
        } catch (Exception ee) {
        	
        }
        int widthRatio = (int) Math.ceil(options.outWidth / maxWidth);
        int heightRatio = (int) Math.ceil(options.outHeight / maxHeight);
        if(widthRatio < 1){
            widthRatio = 1;
        }
        if(heightRatio < 1){
            heightRatio = 1;
        }
        if(reOptimize){

            options.inSampleSize = currentOptionsSampleSize;
        } else {

            if (widthRatio > 1 || heightRatio > 1) {
                if (widthRatio > heightRatio) {
                    options.inSampleSize = widthRatio;
                } else {
                    options.inSampleSize = heightRatio;
                }
                currentOptionsSampleSize = options.inSampleSize;
            } else {

                currentOptionsSampleSize = 1;
            }
        }

        options.inJustDecodeBounds = false;
        try {
	        result = BitmapFactory.decodeByteArray(resource, 0, length, options);
	        result = scaleImg(result, maxQuality);
	    } catch (Exception ee) {
	    }
        return result;
    }

    /**
     * 压缩图片质量，按比例缩放图片
     *
     * @param img     原图
     * @param quality 最大宽度
     * @return
     */
    private Bitmap scaleImg(Bitmap img, float quality) {

        if (img == null) {
            return null;
        }
        float sHeight = 0;
        float sWidth = 0;

        Bitmap result = img;

        if (img.getWidth()>= img.getHeight() && img.getWidth() > quality) {

            sWidth = quality;
            sHeight = (quality * img.getHeight()) / img.getWidth();
            result = Bitmap.createScaledBitmap(img, (int)sWidth, (int)sHeight, true);

            if(null != img && !img.isRecycled() && !img.equals(result)){
//                img.recycle();
            }
        } else if(img.getHeight()> img.getWidth() && img.getHeight() > quality){

            sHeight = quality;
            sWidth = (quality * img.getWidth()) / img.getHeight();
            result = Bitmap.createScaledBitmap(img, (int)sWidth, (int)sHeight, true);

            if(null != img && !img.isRecycled() && !img.equals(result)){
//                img.recycle();
            }
        }

        return result;
    }
}

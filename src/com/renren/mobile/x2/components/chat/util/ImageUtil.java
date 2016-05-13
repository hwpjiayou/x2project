package com.renren.mobile.x2.components.chat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;


/**
 * @author dingwei.chen
 * @说明 图片工具类
 * */
public final class ImageUtil {

	
	//通过字节码来创建图片
	public final static Bitmap createImageByBytes(byte[] bytes){
		Bitmap img = null;
		try {
			img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (Exception e) {
			recycleBitmap(img);
			img = null;
		} catch (OutOfMemoryError e) {
			recycleBitmap(img);
			img = null;
		}
		return img;
	}
	
	//释放图片
	public static void recycleBitmap(Bitmap img) {
		if (img != null && !img.isRecycled()) {
			img.recycle();
			img = null;
		}
	}
	/*
	 * 动态图片压缩方法
	 * minSideLength  最小边长度
	 * maxNumOfPixels 最大像素
	 * 
	 * 使用方法
	 	BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, 400 * 800);
		opts.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, opts);
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

    /**
     * @author yuchao.zhang
     * 图片最大长宽
     */
    private static final int MAX_QUALITY = 720;

    /**
     * @author yuchao.zhang
     * @param filePath 存储路径
     * @param maxNumOfPixels
     * @return 长宽最大的位图
     *
     * description 目前本地选取图片、转发功能在使用
     */
	public static Bitmap image_Compression(String filePath ,int maxNumOfPixels) {
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
	 * add by xiangchao.fan
     * @author yuchao.zhang(更新为新方法)
	 * @param filePath 存储路径
	 * @param dm DisplayMetrics
	 * @return 长宽最大为720的位图
     *
     * description 目前拍照功能在使用
	 */
	public static Bitmap image_Compression(String filePath , DisplayMetrics dm) {
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
            result = Bitmap.createScaledBitmap(img, (int)sWidth, (int)sHeight, true);

            if(null != img && !img.isRecycled() && !img.equals(result)){
                img.recycle();
            }
        } else if(img.getHeight() > img.getWidth() && img.getHeight() > quality){

            sHeight = quality;
            sWidth = (quality * img.getWidth()) / img.getHeight();
            result = Bitmap.createScaledBitmap(img, (int)sWidth, (int)sHeight, true);

            if(null != img && !img.isRecycled() && !img.equals(result)){
                img.recycle();
            }
        }

        return result;
    }

	public static Bitmap image_Compression(byte[] imgData,int maxNumOfPixels) {
		BitmapFactory.Options opts_ = new BitmapFactory.Options();
		opts_.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imgData, 0, imgData.length, opts_);
		opts_.inSampleSize = ImageUtil.computeSampleSize(opts_, -1, maxNumOfPixels);
		opts_.inJustDecodeBounds = false;
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, opts_);
		return bitmap;
	}
	
	
	
  
}

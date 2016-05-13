package com.renren.mobile.x2.components.imageviewer;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.utils.CommonUtil;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class RotateBitmap {
	
    private Bitmap mBitmap;

    private int mRotation;
    private int mScreenHight = RenrenChatApplication.getScreenHeight();
    private int mScreenWidth = RenrenChatApplication.getScreenWidth();

//    private boolean mAutoRecycle = true;

    public RotateBitmap(Bitmap bitmap) {
        this(bitmap, 0);
    }


    public RotateBitmap(Bitmap bitmap, int rotation) {
        setBitmap(bitmap);
        setRotation(rotation % 360);
    }

//    public void recycle() {
//        if (mAutoRecycle && mBitmap != null && !mBitmap.isRecycled()) {
//            mBitmap.recycle();
//        }
//        mBitmap = null;
//    }

    public void setRotation(int rotation) {
        mRotation = rotation;
    }

    public int getRotation() {
        return mRotation;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

 

    public void setBitmap(Bitmap bitmap) {
    	CommonUtil.log("lu","set1");
        if (mBitmap != bitmap) {

            mBitmap = bitmap;
        }
        bitmapWidth = bitmap == null ? 0 : bitmap.getWidth();
        CommonUtil.log("lu","width:" + bitmapWidth);
        bitmapHeight = bitmap == null ? 0 : bitmap.getHeight();
        CommonUtil.log("lu","width:" + bitmapHeight);
        if(bitmapWidth!=0&&bitmapHeight!=0){
        	float scaleWidth = mScreenWidth / bitmapWidth;
        	float scaleHeight = mScreenHight/bitmapHeight;
        	CommonUtil.log("lu","scaleWidth:" + scaleWidth);
        	CommonUtil.log("lu","scaleHeight:" + scaleHeight);
        	if(scaleWidth < scaleHeight){
            	mWidth = mScreenWidth;
            	mHeight = ( bitmapHeight/ bitmapWidth ) * mWidth;
            }else{
            	mHeight = mScreenHight;
            	mWidth = (bitmapWidth/bitmapHeight) * mHeight;
            }
            CommonUtil.log("lu","mWidth"+mWidth);
            CommonUtil.log("lu","mHeight"+mHeight);
            }
        }
        

    private float mWidth;

    private float mHeight;
    private float bitmapWidth;

    private float bitmapHeight;

    public void setBitmap(Bitmap bitmap, int width, int height) {
        if (mBitmap != bitmap) {
            mBitmap = bitmap;
        }

        mWidth = width;
        mHeight = height;
    }

    public Matrix getRotateMatrix() {
        // By default this is an identity matrix.
        Matrix matrix = new Matrix();
        int intrinsicWidth = mBitmap.getWidth();
        int intrinsicHeight = mBitmap.getHeight();
        int cx = intrinsicWidth / 2;
        int cy = intrinsicHeight / 2;

        // handle orientation
        if (mRotation != 0) {
            // We want to do the rotation at origin, but since the bounding
            // rectangle will be changed after rotation, so the delta values
            // are based on old & new width/height respectively.
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(mRotation);
            if (isOrientationChanged()) {
                matrix.postTranslate(cy, cx);
            } else {
                matrix.postTranslate(cx, cy);
            }
        }
        // handle zoom factor
        if (isOrientationChanged()) {
            matrix.postScale((mHeight + 0F) / intrinsicHeight, (mWidth + 0F) / intrinsicWidth);
        } else {
            matrix.postScale((mWidth + 0F) / intrinsicWidth, (mHeight + 0F) / intrinsicHeight);
        }

        return matrix;
    }

    public boolean isOrientationChanged() {
        return (mRotation / 90) % 2 != 0;
    }

    public float getHeight() {
        return isOrientationChanged() ? mWidth : mHeight;
    }

    public float getWidth() {
        return isOrientationChanged() ? mHeight : mWidth;
    }

}

package com.renren.mobile.x2.components.imageviewer;


import com.renren.mobile.x2.utils.CommonUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

//This multi touch view does not handle any key events.
//Please handle key events in the parent view group and call the view's corresponding functions to do zooming.
//This view supports GIF with animation.

public class MultiTouchView extends View {

    public static final int FIT_INSIDE = 0;

    public static final int FIT_OUTSIDE = 1;

    public static final int FIT_RAW = 2;

    private int currentMode = FIT_INSIDE;

    private Handler mHandler = new Handler();

    public MultiTouchView(Context context) {
        super(context);
        init();
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiTouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setClickable(false);
        setLongClickable(false);
    }

//    public void recycle() {
//        mBitmapDisplayed.recycle();
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mThisWidth = right - left;
        mThisHeight = bottom - top;
        if (mBitmapDisplayed.getBitmap() != null) {
            getProperBaseMatrix(mBaseMatrix);
            currentMode = isSmallPic ? FIT_RAW : FIT_INSIDE;
            setViewMatrix();
        }
        mMaxZoom = getMaxZoomInt();
    }

    protected Matrix mBaseMatrix = new Matrix();

    protected Matrix mSuppMatrix = new Matrix();

    // This is the final matrix which is computed as the concatentation
    // of the base matrix and the supplementary matrix.
    private final Matrix mDisplayMatrix = new Matrix();

    // Temporary buffer used for getting the values out of a matrix.
    private final float[] mMatrixValues = new float[9];

    // The current bitmap being displayed.
    protected RotateBitmap mBitmapDisplayed = new RotateBitmap(null);

    // The image's original width and height, taking orientation into account.
    int mImageRawWidth = -1, mImageRawHeight = -1;

    int mThisWidth = -1, mThisHeight = -1;

    float mMaxZoom;

    public float getMaxScale() {
        return mMaxZoom;
    }

    private class Options {
    	
        public boolean inJustGetMatrix;

        public Matrix inSuppMatrix;

        public Matrix outSuppMatrix;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));

        if (mBitmapDisplayed.getBitmap() == null)
            return;

        int saveCount = canvas.getSaveCount();
        canvas.save();
        if (mDisplayMatrix != null) {
            canvas.concat(mDisplayMatrix);
        }
        canvas.drawBitmap(mBitmapDisplayed.getBitmap(), 0, 0, null);
        canvas.restoreToCount(saveCount);
    }

    public void setBitmap(Bitmap bitmap) {
        setBitmap(bitmap, 0);
    }

    public void setBitmap(Bitmap bitmap, int orientation) {
    	CommonUtil.log("lu","set");
        setBitmap(bitmap, bitmap == null ? 0 : bitmap.getWidth(), bitmap == null ? 0 : bitmap
                .getHeight(), orientation);
    }

    public void setBitmap(Bitmap bitmap, int imageRawWidth, int imageRawHeight, int orientation) {
        mBaseMatrix.reset();
        mSuppMatrix.reset();

        mImageRawWidth = imageRawWidth;
        mImageRawHeight = imageRawHeight;
        mBitmapDisplayed.setBitmap(bitmap);
        mBitmapDisplayed.setRotation(orientation);
        if (mBitmapDisplayed.isOrientationChanged()) {
            int temp = mImageRawWidth;
            mImageRawWidth = mImageRawHeight;
            mImageRawHeight = temp;
        }
    }

    public boolean isVerticalOut() {
        if (getScale() <= 1F)
            return false;
        Matrix m = getViewMatrix();

        RectF rect = new RectF(0, 0, mBitmapDisplayed.getBitmap().getWidth(), mBitmapDisplayed
                .getBitmap().getHeight());

        m.mapRect(rect);

        return ((int) rect.top) < 0 || ((int) rect.bottom) > getHeight();
    }

    public int getTopExceedOffset() {
        if (mBitmapDisplayed.getBitmap() == null)
            return 0;
        Matrix m = getViewMatrix();

        RectF rect = new RectF(0, 0, mBitmapDisplayed.getBitmap().getWidth(), mBitmapDisplayed
                .getBitmap().getHeight());

        m.mapRect(rect);

        return Math.max((int) -rect.top, 0);
    }

    public int getBottomExceedOffset() {
        if (mBitmapDisplayed.getBitmap() == null)
            return 0;
        Matrix m = getViewMatrix();

        RectF rect = new RectF(0, 0, mBitmapDisplayed.getBitmap().getWidth(), mBitmapDisplayed
                .getBitmap().getHeight());

        m.mapRect(rect);

        return Math.max((int) rect.bottom - getHeight(), 0);
    }

    public float getLeftExceedOffset() {
        if (mBitmapDisplayed.getBitmap() == null)
            return 0;
        Matrix m = getViewMatrix();

        RectF rect = new RectF(0, 0, mBitmapDisplayed.getBitmap().getWidth(), mBitmapDisplayed
                .getBitmap().getHeight());

        m.mapRect(rect);

        return Math.max(-rect.left, 0);
    }

    public float getRightExceedOffset() {
        if (mBitmapDisplayed.getBitmap() == null)
            return 0;
        Matrix m = getViewMatrix();

        RectF rect = new RectF(0, 0, mBitmapDisplayed.getBitmap().getWidth(), mBitmapDisplayed
                .getBitmap().getHeight());

        m.mapRect(rect);
        float viewWidth = getWidth();

        return Math.max(rect.right - viewWidth, 0);
    }

    // Center as much as possible in one or both axis. Centering is
    // defined as follows: if the image is scaled down below the
    // view's dimensions then center it (literally). If the image
    // is scaled larger than the view and is translated out of view
    // then translate it back into view (i.e. eliminate black bars).
    public void center(boolean horizontal, boolean vertical, Options opt) {
        if (mBitmapDisplayed.getBitmap() == null) {
            if (opt == null || !opt.inJustGetMatrix) {
                setViewMatrix();
                invalidate();
            }
            return;
        }

        Matrix m;
        if (opt != null && opt.inJustGetMatrix) {
            m = new Matrix();
            m.set(mBaseMatrix);
            m.postConcat(opt.outSuppMatrix);
        } else {
            m = getViewMatrix();
        }

        RectF rect = new RectF(0, 0, mBitmapDisplayed.getBitmap().getWidth(), mBitmapDisplayed
                .getBitmap().getHeight());

        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int viewHeight = getHeight();
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int viewWidth = getWidth();
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }
        if (opt != null && opt.inJustGetMatrix) {
            opt.outSuppMatrix.postTranslate(deltaX, deltaY);
        } else {
            postTranslate(deltaX, deltaY);
            setViewMatrix();
            invalidate();
        }

    }

    public void adjustLeftRight(boolean leftAttachView) {
        if (mBitmapDisplayed.getBitmap() == null) {
            return;
        }

        Matrix m = getViewMatrix();

        RectF rect = new RectF(0, 0, mBitmapDisplayed.getBitmap().getWidth(), mBitmapDisplayed
                .getBitmap().getHeight());

        m.mapRect(rect);

        if (leftAttachView) {
            postTranslate(-rect.left, 0);
        } else {
            int viewWidth = getWidth();
            postTranslate(viewWidth - rect.right, 0);
        }

        setViewMatrix();
        invalidate();
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    // Get the scale factor out of the matrix.
    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    public float getScale() {
        return getScale(mSuppMatrix);
    }

    private boolean isSmallPic;

    // Setup the base matrix so that the image is centered and scaled properly.
    private void getProperBaseMatrix(Matrix matrix) {
               
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float bitmapWidth = mBitmapDisplayed.getWidth();
        float bitmapHeight = mBitmapDisplayed.getHeight();
        
        matrix.reset();
        isSmallPic = false;
        matrix.postConcat(mBitmapDisplayed.getRotateMatrix());
        
        float scaleRate = 1;
        float scaleWidth = viewWidth / bitmapWidth;   
        float scaleHeight = viewHeight / bitmapHeight;
        if(bitmapWidth <= 250 && bitmapHeight <= 250){       	
        }else{
        	scaleRate = Math.min(scaleWidth, scaleHeight);
        }  
        matrix.postScale(scaleRate, scaleRate);
        float locX = (viewWidth - bitmapWidth * scaleRate) / 2f;
        float locY = (viewHeight - bitmapHeight * scaleRate) / 2f;
        matrix.postTranslate(locX, locY);

    }
    
    protected Matrix getFitMatrix() {
    	return null;
    }

    // Combine the base matrix and the supp matrix to make the final matrix.
    protected Matrix getViewMatrix() {
        setViewMatrix();
        return mDisplayMatrix;
    }

    protected void setViewMatrix() {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
    }

    // Sets the maximum zoom, which is a scale relative to the base matrix. It
    // is calculated to show the image at 400% zoom regardless of screen or
    // image orientation. If in the future we decode the full 3 megapixel image,
    // rather than the current 1024x768, this should be changed down to 200%.
    protected float getMaxZoomInt() {
        if (mBitmapDisplayed.getBitmap() == null) {
            return 1F;
        }

        float fw = (float) mImageRawHeight / (float) getWidth();
        float fh = (float) mImageRawHeight / (float) getHeight();
        float max = Math.max(fw, fh) * 4;
        return Math.max(Math.max(max, 1F), 1F / fw);
    }

    public void zoomTo(float scale, float centerX, float centerY, Options opt) {
        if (opt != null && opt.inJustGetMatrix) {
            float oldScale = getScale(opt.inSuppMatrix);
            opt.outSuppMatrix = new Matrix();
            opt.outSuppMatrix.set(opt.inSuppMatrix);
            float deltaScale = scale / oldScale;
            opt.outSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        } else {
            float oldScale = getScale();
            float deltaScale = scale / oldScale;
            mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        }

        center(true, true, opt);
    }

    public void zoomTo(final float scale, final float centerX, final float centerY,
            final float durationMs) {
        final float incrementPerMs = (scale - getScale()) / durationMs;
        final float oldScale = getScale();
        final long startTime = System.currentTimeMillis();

        mHandler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float target = oldScale + (incrementPerMs * currentMs);
                zoomTo(target, centerX, centerY, null);

                if (currentMs < durationMs) {
                    mHandler.post(this);
                }
            }
        });
    }
    
    public void zoomTo(float scale, Options opt) {
        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        zoomTo(scale, cx, cy, opt);
    }

    public void zoomTo(float scale, final float durationMs) {
        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        zoomTo(scale, cx, cy, durationMs);
    }
    
    public void refreshOrientation(int orientation) {
    	// rotate to left and right
    	float scale = getScale();
    	
    	int sOrientation = mBitmapDisplayed.getRotation(); // 初始角度
    	int eOrientation = (sOrientation + 360 + orientation) % 360;
        mBitmapDisplayed.setRotation(eOrientation);
        getProperBaseMatrix(mBaseMatrix);
        mSuppMatrix.reset();
        setViewMatrix();
        mMaxZoom = getMaxZoomInt();

        invalidate();
        
        zoomTo(scale, null);
    }

    protected void postTranslate(float dx, float dy) {
        mSuppMatrix.postTranslate(dx, dy);
    }

    protected void transition(Matrix fromSuppMatrix, Matrix toSuppMatrix) {
        mSuppMatrix.set(toSuppMatrix);
        setViewMatrix();
        invalidate();
    }

    protected void transition(final Matrix fromSuppMatrix, final Matrix toSuppMatrix,
            final float durationMs) {
        float temp[] = new float[9];
        temp[Matrix.MTRANS_X] = (getValue(toSuppMatrix, Matrix.MTRANS_X) - getValue(fromSuppMatrix,
                Matrix.MTRANS_X))
                / durationMs;
        temp[Matrix.MTRANS_Y] = (getValue(toSuppMatrix, Matrix.MTRANS_Y) - getValue(fromSuppMatrix,
                Matrix.MTRANS_Y))
                / durationMs;
        temp[Matrix.MSCALE_X] = (getValue(toSuppMatrix, Matrix.MSCALE_X) - getValue(fromSuppMatrix,
                Matrix.MSCALE_X))
                / durationMs;

        final long startTime = System.currentTimeMillis();
        final float f[] = temp;
        mHandler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float x = getValue(fromSuppMatrix, Matrix.MTRANS_X) + f[Matrix.MTRANS_X]
                        * currentMs;
                float y = getValue(fromSuppMatrix, Matrix.MTRANS_Y) + f[Matrix.MTRANS_Y]
                        * currentMs;
                float sx = getValue(fromSuppMatrix, Matrix.MSCALE_X) + f[Matrix.MSCALE_X]
                        * currentMs;
                mSuppMatrix.reset();
                mSuppMatrix.postTranslate(x, y);
                mSuppMatrix.postScale(sx, sx);
                setViewMatrix();
                invalidate();
                if (currentMs < durationMs) {
                    mHandler.post(this);
                }
            }
        });
    }

    public void panBy(float dx, float dy) {
        postTranslate(dx, dy);
        setViewMatrix();
        invalidate();
    }
}

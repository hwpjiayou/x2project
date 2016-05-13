package com.renren.mobile.x2.components.photoupload;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * @author yuchao.zhang
 * <p/>
 * description 自定义的ImageView，目前支持的功能有：
 * 1.多点缩放（倍数限制）
 * 2.单指移动（边界限制）
 * 3.手势（单击、双击）
 */
public class HeadEditImageView extends ImageView {

    // This is the base transformation which is used to show the image
    // initially. The current computation for this shows the image in
    // it's entirety, letterboxing as needed. One could choose to
    // show the image as cropped instead.
    //
    // This matrix is recomputed when we go from the thumbnail image to
    // the full size image.
    protected Matrix mBaseMatrix = new Matrix();

    // This is the supplementary transformation which reflects what
    // the user has done in terms of zooming and panning.
    //
    // This matrix remains the same when we go from the thumbnail image
    // to the full size image.
    protected Matrix mSuppMatrix = new Matrix();

    // This is the final matrix which is computed as the concatentation
    // of the base matrix and the supplementary matrix.
    private final Matrix mDisplayMatrix = new Matrix();

    // Temporary buffer used for getting the values out of a matrix.
    private final float[] mMatrixValues = new float[9];
    /**
     * 当前显示的位图
     */
    private Bitmap mBitmap = null;
    /**
     *
     */
    int mThisWidth = -1, mThisHeight = -1;
    /**
     * 最大缩放比例
     */
    float mMaxZoom;
    /**
     * 最小缩放比例
     */
    float mMinZoom;
    /**
     * 图片的原始宽度
     */
    private int imageWidth = 0;
    /**
     * 图片的原始高度
     */
    private int imageHeight = 0;
    /**
     * 图片为了适应屏幕，而缩放的比例
     */
    private float scaleRate;
    /**
     * 屏幕的宽度
     */
    private float screenWidth;
    /**
     * 屏幕的高度
     */
    private float screenHeight;
    /**
     * 当前显示框的宽高长度
     */
    private float density = 0f;
    /**
     * 未初始化 模式
     */
    private static final int NONE = 0;
    /**
     * 单指 拖拽模式
     */
    private static final int DRAG = 1;
    /**
     * 双指 缩放模式
     */
    private static final int ZOOM = 2;
    /**
     * 当前模式
     */
    private int mode = NONE;
    /**
     * 手指所在的坐标点
     */
    private PointF start = new PointF();
    /**
     * 手势
     */
    private GestureDetector gestureScanner = new GestureDetector(new MySimpleGesture());
    /**
     * 多点缩放时：两指间的距离
     */
    float baseValue;
    /**
     * 多点缩放时：缩放的倍数
     */
    float originalScale;
    /**
     * 用于计算缩放倍数的原始框尺寸
     */
    public static final float ORIGINAL_DENSITY = 1.5f;
    /**
     * 初始值大小
     */
    public static final int ORIGINAL_SIZE = 268;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 是否正在加载图片
     */
    public static boolean isLoadingBitmap = true;
    /**
     * 上传的数据
     */
    private byte[] bytes;

    public HeadEditImageView(Context context, int imageWidth, int imageHeight) {
        super(context);
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        init();
        mContext = context;
    }

    public HeadEditImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mContext = context;
    }

    /**
     * 使用Matrix方式
     */
    private void init() {
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
            if (getScale() > 1.0f) {
                // If we're zoomed in, pressing Back jumps out to show the
                // entire image, otherwise Back returns the user to the gallery.
                zoomTo(1.0f);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    protected Handler mHandler = new Handler();

    @Override
    public void setImageBitmap(Bitmap bitmap) {

        super.setImageBitmap(bitmap);
        if(mBitmap != null && !mBitmap.isRecycled()){
        	mBitmap.recycle();
        	mBitmap = null;
        }
        mBitmap = bitmap;
        // 计算适应屏幕的比例
        arithScaleRate();
        //缩放到屏幕大小
//        zoomTo(scaleRate, screenWidth / 2f, screenHeight / 2f);
    }

    /**
     * 计算图片要适应屏幕需要缩放的比例
     */
    private void arithScaleRate() {

        float scaleWidth;
        float scaleHeight;

        scaleRate = density / ORIGINAL_DENSITY;

        /* 图片宽高变为缩放后的宽高 */
        imageWidth = (int) (imageWidth * scaleRate);
        imageHeight = (int) (imageHeight * scaleRate);

//        CommonUtil.log("HeadUpload", "scaled width = " + imageWidth + " scaled height = " + imageHeight);

        /* 图片可缩小的比例 */
        scaleWidth = density * ORIGINAL_SIZE / imageWidth;
        scaleHeight = density * ORIGINAL_SIZE / imageHeight;

        mMaxZoom = scaleRate;
        mMinZoom = Math.max(scaleWidth, scaleHeight);

//        CommonUtil.log("HeadUpload", "max = "+mMaxZoom+" min = "+mMinZoom);

        zoomTo(scaleRate, screenWidth / 2f, screenHeight / 2f);

//        CommonUtil.log("HeadUpload", "current ScaleRate = "+getScaleRate()+" current Scale = "+getScale());

//        scaleWidth = screenWidth / (float) imageWidth;
//        scaleHeight = screenHeight / (float) imageHeight;
//        scaleRate = Math.min(scaleWidth, scaleHeight);
//        mMinZoom = scaleRate;
//        mMaxZoom = scaleRate * 4.0f;
    }

    // Center as much as possible in one or both axis. Centering is
    // defined as follows: if the image is scaled down below the
    // view's dimensions then center it (literally). If the image
    // is scaled larger than the view and is translated out of view
    // then translate it back into view (i.e. eliminate black bars).
    protected void center(boolean horizontal, boolean vertical) {
        // if (mBitmapDisplayed.getBitmap() == null) {
        // return;
        // }
        if (mBitmap == null) {
            return;
        }
        Matrix m = getImageViewMatrix();

        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
//		RectF rect = new RectF(0, 0, imageWidth*getScale(), imageHeight*getScale());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

//        if (vertical) {
////            int viewHeight = getHeight();
//            if (height <= screenHeight) {
//                deltaY = (screenHeight - height) / 2 - rect.top;
//            } else if (rect.top > 0) {
//                deltaY = -rect.top;
//            } else if (rect.bottom < screenHeight) {
//                deltaY = screenHeight - rect.bottom;
//            }
//        }
        /* update by yuchao.zhang 图片居中算法更新
        原算法为：1.当图片高度小于等于屏幕高度时，居中 2.当图片某一边进入屏幕内，移动图片
        新算法为：1.当图片高度小于等于遮罩框高度时，居中 2.当图片某一边进入遮罩框内，移动图片
        新算法好处：符合目前策略，不会因为调整头像大小而发生图片自行跳动居中
        新算法坏处：初始化时不会居中图片 */
        float currentFrameSize = ORIGINAL_SIZE*density;
        float verticalTopEndLine = (screenHeight - ORIGINAL_SIZE*density)/2;
        float verticalBottomEndLine = (screenHeight + ORIGINAL_SIZE*density)/2;
//        CommonUtil.log("HeadEditImageView", "currentFrameSize = "+currentFrameSize);
//        CommonUtil.log("HeadEditImageView", "verticalTopEndLine = "+verticalTopEndLine+" rect top = "+rect.top+" "+(rect.top > verticalTopEndLine));
//        CommonUtil.log("HeadEditImageView", "verticalBottomEndLine = "+verticalBottomEndLine+" rect bottom = "+rect.bottom+" "+(rect.bottom < verticalBottomEndLine));
        if (vertical) {
//            int viewHeight = getHeight();
            if (height <= currentFrameSize) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > verticalTopEndLine) {
                deltaY = -(rect.top - verticalTopEndLine );
            } else if (rect.bottom < verticalBottomEndLine) {
                deltaY = verticalBottomEndLine - rect.bottom;
            }
        }

//        if (horizontal) {
////            int viewWidth = getWidth();
//            if (width <= screenWidth) {
//                deltaX = (screenWidth - width) / 2 - rect.left;
//            } else if (rect.left > 0) {
//                deltaX = -rect.left;
//            } else if (rect.right < screenWidth) {
//                deltaX = screenWidth - rect.right;
//            }
//        }
        /* update by yuchao.zhang 图片居中算法更新
        原算法为：1.当图片宽度小于等于屏幕宽度时，居中 2.当图片某一边进入屏幕内，移动图片
        新算法为：1.当图片宽度小于等于遮罩框宽度时，居中 2.当图片某一边进入遮罩框内，移动图片
        新算法好处：符合目前策略，不会因为调整头像大小而发生图片自行跳动居中
        新算法坏处：初始化时不会居中图片 */
        float horizontalLeftEndLine = (screenWidth - ORIGINAL_SIZE*density)/2;
        float horizontalRightEndLine = (screenWidth + ORIGINAL_SIZE*density)/2;

        if (horizontal) {
//            int viewWidth = getWidth();
            if (width <= currentFrameSize) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > horizontalLeftEndLine) {
                deltaX = -(rect.left - horizontalLeftEndLine );
            } else if (rect.right < horizontalRightEndLine) {
                deltaX = horizontalRightEndLine - rect.right;
            }
        }

        postTranslate(deltaX, deltaY);
        setImageMatrix(getImageViewMatrix());
    }

//    /**
//     * 设置图片居中显示
//     */
//    public void layoutToCenter()
//    {
//        //正在显示的图片实际宽高
//        float width = imageWidth*getScale();
//        float height = imageHeight*getScale();
//
//        //空白区域宽高
//        float fill_width = PhotoNew.screenWidth - width;
//        float fill_height = PhotoNew.screenHeight - height;
//
//        //需要移动的距离
//        float tran_width = 0f;
//        float tran_height = 0f;
//
//        if(fill_width>0)
//            tran_width = fill_width/2;
//        if(fill_height>0)
//            tran_height = fill_height/2;
//
//
//        postTranslate(tran_width, tran_height);
////        setImageMatrix(getImageViewMatrix());
//    }

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

    // Combine the base matrix and the supp matrix to make the final matrix.
    protected Matrix getImageViewMatrix() {
        // The final matrix is computed as the concatentation of the base matrix
        // and the supplementary matrix.
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }

    static final float SCALE_RATE = 1.25F;

    // Sets the maximum zoom, which is a scale relative to the base matrix. It
    // is calculated to show the image at 400% zoom regardless of screen or
    // image orientation. If in the future we decode the full 3 megapixel image,
    // rather than the current 1024x768, this should be changed down to 200%.
    protected float maxZoom() {
        if (mBitmap == null) {
            return 1F;
        }

        float fw = (float) mBitmap.getWidth() / (float) mThisWidth;
        float fh = (float) mBitmap.getHeight() / (float) mThisHeight;
        float max = Math.max(fw, fh) * 4;
        return max;
    }

    public void zoomTo(float scale, float centerX, float centerY) {
        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        } else if (scale < mMinZoom) {
            scale = mMinZoom;
        }

        float oldScale = getScale();
        float deltaScale = scale / oldScale;

        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
        center(true, true);
    }

    protected void zoomTo(final float scale, final float centerX, final float centerY, final float durationMs) {
        final float incrementPerMs = (scale - getScale()) / durationMs;
        final float oldScale = getScale();
        final long startTime = System.currentTimeMillis();

        mHandler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float target = oldScale + (incrementPerMs * currentMs);
                zoomTo(target, centerX, centerY);
                if (currentMs < durationMs) {
                    mHandler.post(this);
                }
            }
        });
    }


    protected void zoomTo(float scale) {
        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        zoomTo(scale, cx, cy);
    }

    protected void zoomToPoint(float scale, float pointX, float pointY) {
        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        panBy(cx - pointX, cy - pointY);
        zoomTo(scale, cx, cy);
    }

    protected void zoomIn() {
        zoomIn(SCALE_RATE);
    }

    protected void zoomOut() {
        zoomOut(SCALE_RATE);
    }

    protected void zoomIn(float rate) {
        if (getScale() >= mMaxZoom) {
            return; // Don't let the user zoom into the molecular level.
        } else if (getScale() <= mMinZoom) {
            return;
        }
        if (mBitmap == null) {
            return;
        }

        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        mSuppMatrix.postScale(rate, rate, cx, cy);
        setImageMatrix(getImageViewMatrix());
    }

    protected void zoomOut(float rate) {
        if (mBitmap == null) {
            return;
        }

        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        // Zoom out to at most 1x.
        Matrix tmp = new Matrix(mSuppMatrix);
        tmp.postScale(1F / rate, 1F / rate, cx, cy);

        if (getScale(tmp) < 1F) {
            mSuppMatrix.setScale(1F, 1F, cx, cy);
        } else {
            mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
        }
        setImageMatrix(getImageViewMatrix());
        center(true, true);
    }

    public void postTranslate(float dx, float dy) {
        mSuppMatrix.postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }

    float _dy = 0.0f;

    protected void postTranslateDur(final float dy, final float durationMs) {
        _dy = 0.0f;
        final float incrementPerMs = dy / durationMs;
        final long startTime = System.currentTimeMillis();
        mHandler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);

                postTranslate(0, incrementPerMs * currentMs - _dy);
                _dy = incrementPerMs * currentMs;

                if (currentMs < durationMs) {
                    mHandler.post(this);
                }
            }
        });
    }

    protected void panBy(float dx, float dy) {
        postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }

    float v[] = new float[9];
    Matrix m = getImageMatrix();

    // 图片实时的上下左右坐标
    float currentLeft, currentRight;
    float currentTop, currentBottom;

    // 图片的实时宽，高
    float currentWidth, currentHeight;

    private void getCurrentPosition() {

        m.getValues(v);

        currentWidth = getScale() * getImageWidth() / getScaleRate();
        currentHeight = getScale() * getImageHeight() / getScaleRate();

        currentLeft = v[Matrix.MTRANS_X];
        currentTop = v[Matrix.MTRANS_Y];
        currentBottom = currentTop + currentHeight;
        currentRight = currentLeft + currentWidth;

        Rect r = new Rect();
        getGlobalVisibleRect(r);
    }

    public float getMoveHorizontal() {

        getCurrentPosition();

        return currentLeft;
    }

    public float getMoveVertical() {

        getCurrentPosition();

        return currentTop;
    }

    /**
     * 遮罩框距离屏幕左侧距离
     */
    float marginLeft;
    /**
     * 遮罩框距离屏幕右侧距离
     */
    float marginRight;
    /**
     * 遮罩框距离屏幕上侧距离
     */
    float marginTop;
    /**
     * 遮罩框距离屏幕下侧距离
     */
    float marginBottom;

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isLoadingBitmap) {
            return true;
        }

        float distanceX = 0;
        float distanceY = 0;

        gestureScanner.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                mode = DRAG;
                baseValue = 0;
                originalScale = getScale();
//                CommonUtil.log("HeadUpload", "onTouchDown originalScale = "+originalScale);
                if (event.getPointerCount() == 1) {

                    start.set(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:// 多点缩放

                mode = ZOOM;
                baseValue = 0;
                originalScale = getScale();

                if (event.getPointerCount() == 2) {

                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);

                    float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离

                    if (baseValue == 0) {

                        baseValue = value;
                    }

                    return true;
                } else if (event.getPointerCount() == 1) {

                    return true;
                }
            case MotionEvent.ACTION_MOVE:

                distanceX = event.getX() - start.x;
                distanceY = event.getY() - start.y;

                if (mode == ZOOM) {

                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);
                    float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
                    if (baseValue == 0) {

                        baseValue = value;
                    } else {

                        float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                        zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));
                    }
                    return true;
                } else if (mode == DRAG) {

                    float v[] = new float[9];
                    Matrix m = getImageMatrix();
                    m.getValues(v);

                    // 图片实时的上下左右坐标
                    float left, right;
                    float top, bottom;

                    // 图片的实时宽，高
                    float width, height;

                    width = getScale() * getImageWidth() / getScaleRate();
                    height = getScale() * getImageHeight() / getScaleRate();

                    if ((int) width <= getDensity() * ORIGINAL_SIZE && (int) height <= getDensity() * ORIGINAL_SIZE) { /* 图片当前宽高 小于 框的宽高 */

                    } else {

                        left = v[Matrix.MTRANS_X];
                        top = v[Matrix.MTRANS_Y];
                        bottom = top + height;
                        right = left + width;

                        Rect r = new Rect();
                        getGlobalVisibleRect(r);

                        float dY = Math.abs(distanceY);
                        float dX = Math.abs(distanceX);

                        if ((int) width > getDensity() * ORIGINAL_SIZE) {

                            if (distanceX <= 0) { /* 向左移动 */

                                if (right <= (screenWidth - marginRight)) {

                                    /* 右边界卡住，无可向左移动距离 */
                                } else {

                                    /* 限制横向移动距离 */
                                    if (Math.abs(distanceX) > (right - screenWidth + marginRight)) {
                                        distanceX = -(right - screenWidth + marginRight);
                                    }
                                    postTranslate(distanceX, 0);
                                }
                            } else if (distanceX >= 0) {/* 向右移动 */
                                if (left >= (0 + marginLeft)) {

                                    /* 左边界卡住，已无可向右移动距离 */
                                } else {

                                    /* 限制横向移动距离 */
                                    if (distanceX > marginLeft - left) {
                                        distanceX = marginLeft - left;
                                    }
                                    postTranslate(distanceX, 0);
                                }
                            }
                        }

                        if ((int) height > getDensity() * ORIGINAL_SIZE) {

                            if (distanceY <= 0) {/* 向上移动 */

                                if (bottom <= (screenHeight - marginBottom)) {

                                    /* 向上移动，下边界卡住 */
                                } else {

                                    if (Math.abs(distanceY) > (bottom - (screenHeight - marginBottom))) {

                                        distanceY = -(bottom - (screenHeight - marginBottom));
                                    }

                                    postTranslate(0, distanceY);
                                }
                            } else if (distanceY >= 0) {/* 向下移动 */

                                if (top >= (0 + marginTop)) {
                                    /* 向下移动，上边界卡住 */
                                } else {

                                    if (distanceY > marginTop - top) {

                                        distanceY = marginTop - top;
                                    }

                                    postTranslate(0, distanceY);
                                }
                            }
                        }
                        start.set(event.getX(), event.getY());

                    }
                    return true;
                }

            case MotionEvent.ACTION_UP:
                if (baseValue != 0) {

                    baseValue = 0;
                    originalScale = getScale();
                }
//                        if(isTouchDown && (System.currentTimeMillis() - touchDownTime) < 1000) {
//                            if (mIsShowInfo) {
//                                mIsShowInfo = false;
//                                mLayoutPhotoInfo.startAnimation(AnimationUtils.loadAnimation(PhotoNew.this, R.anim.view_disappear));
//                                mLayoutPhotoInfo.setVisibility(View.GONE);
//                            } else {
//                                mIsShowInfo = true;
//                                mLayoutPhotoInfo.setVisibility(View.VISIBLE);
//                            }
//                        }

//                        isTouchDown = false;
                mode = NONE;
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                if (baseValue != 0) {

                    baseValue = 0;
                    originalScale = getScale();
                }
                mode = NONE;

//                        if(isTouchDown && (System.currentTimeMillis() - touchDownTime) < 1000) {
//                            if (mIsShowInfo) {
//                                mIsShowInfo = false;
//                                mLayoutPhotoInfo.startAnimation(AnimationUtils.loadAnimation(PhotoNew.this, R.anim.view_disappear));
//                                mLayoutPhotoInfo.setVisibility(View.GONE);
//                            } else {
//                                mIsShowInfo = true;
//                                mLayoutPhotoInfo.setVisibility(View.VISIBLE);
//                            }
//                        }
//
//                        isTouchDown = false;
                return true;
        }
        return false;

    }

    private class MySimpleGesture extends GestureDetector.SimpleOnGestureListener {

        // 按两下的第二下Touch down时触发
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            if (isLoadingBitmap) {
                return true;
            }

            if (getScale() > getScaleRate()) {
                zoomTo(getScaleRate(), e.getX(), e.getY(), 200f);
                // imageView.layoutToCenter();
            } else {
                zoomTo(getScaleRate() * 4, e.getX(), e.getY(), 200f);
            }
            // return super.onDoubleTap(e);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (isLoadingBitmap) {
                return true;
            }

            return true;
        }

    }

    public float getScreenWidth() {

        return screenWidth;
    }

    public void setScreenWidth(float width) {

        screenWidth = width;
    }

    public float getScreenHeight() {

        return screenHeight;
    }

    public void setScreenHeight(float height) {

        screenHeight = height;
    }

    public float getScaleRate() {
        return scaleRate;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float currentDensity) {
        this.density = currentDensity;
    }

    public Bitmap getBitmap() {

        return this.mBitmap;
    }

    public void setBytes(byte[] bytes) {
    	this.bytes = null;
        this.bytes = bytes;
    }

    public byte[] getBytes() {

        bytes = Bitmap2Bytes(mBitmap);
        return this.bytes;
    }

    /**
     * 销毁Bitmap
     */
    public void setBitmapNull() {

        mBitmap.recycle();
        mBitmap = null;
    }

    /**
     * 旋转角度
     */
    public static final float TRANS_ROTATION = 90f;

    Matrix matrix;

    /**
     * 图片向左转
     */
    public void turnLeft() {

        if (isLoadingBitmap) {
            return;
        }

        matrix = new Matrix();
        matrix.postRotate(-TRANS_ROTATION);
        updateBitmap();
    }

    /**
     * 图片向右转
     */
    public void turnRight() {

        if (isLoadingBitmap) {
            return;
        }

        matrix = new Matrix();
        matrix.postRotate(TRANS_ROTATION);
        updateBitmap();
    }

    private void updateBitmap() {
        // 产生新的bitmap
        try {
            Bitmap result = null;
            result = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
            if ( mBitmap != null && !mBitmap.isRecycled() && !mBitmap.equals(result) ) {

                mBitmap.recycle();
            }
//            bytes = Bitmap2Bytes(mBitmap);
            imageWidth = result.getWidth();
            imageHeight = result.getHeight();
            setImageBitmap(result);
        } catch (OutOfMemoryError ex) {
            // We have no memory to rotate.
        }
    }

    /**
     * 图片转化为Byte数组
     *
     * @param bitmap 需要转化为数据的位图
     * @return 上传给服务器的数据
     */
    private byte[] Bitmap2Bytes(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try{

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            return bytes;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

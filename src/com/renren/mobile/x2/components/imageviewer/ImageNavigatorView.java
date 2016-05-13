package com.renren.mobile.x2.components.imageviewer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

public class ImageNavigatorView extends Gallery implements GestureDetector.OnDoubleTapListener {
	
	// 缩放参数对
	// (0.8~1.25)
	// (0.5~2.0)
	
	private static final float ZOOM_IN_RATE = 1.25f; // 1.5  
	
	private static final float ZOOM_OUT_RATE = 0.8f; // 0.6
	
	private static final float ZOOM_ANIMATION_DURATION = 120f;
	
	private static final float ZOOM_DOUBLE_CLICK_DURATION = 200f;
	
	private Handler mHandler = new Handler();
	
    public ImageNavigatorView(Context context) {
        super(context, null, 0);
    }

    public ImageNavigatorView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ImageNavigatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // We can be in one of these 3 states
    static final int NONE = 0;

    static final int DRAG = 1;

    static final int ZOOM = 2;

    int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();

    private PointF lastMoveStart = new PointF();

    private PointF mid = new PointF();

    private float oldDist = 1f;

    MultiTouchView curChild = null;
    // MultiTouchView curChild = (MultiTouchView) getSelectedView();
    
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // work around for flinging no more than one screen

        if (e1 == null)
            return false;
        float leftExceed = curChild == null ? 0 : curChild.getLeftExceedOffset();
        float rightExceed = curChild == null ? 0 : curChild.getRightExceedOffset();
        if (velocityX < 0) {
            // 向左滑
            if (rightExceed > 10) {
                return false;
            }

        } else {
            // 向右滑
            if (leftExceed > 10) {
                return false;
            }
        }

        int kEvent;

        if (isScrollingLeft(e1, e2)) { // Check if scrolling left
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else { // Otherwise scrolling right
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getX(), event.getY());
                lastMoveStart.set(event.getX(), event.getY());
                curChild = (MultiTouchView) getSelectedView();
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;

                curChild = (MultiTouchView) getSelectedView();

                if (curChild != null) {
                    // 加点动态过程
                    curChild.center(true, true, null);
                }
                // 根据手指移动速度，加点阻尼动态过程
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (curChild != null) {
                    final float scale = curChild.getScale();
                    final float maxScale = curChild.getMaxScale();
                    if (scale < 1.0) {
                        curChild.zoomTo(1F, ZOOM_ANIMATION_DURATION);
                    } else if (scale > maxScale) {
                        curChild.zoomTo(maxScale, ZOOM_ANIMATION_DURATION);
                    }
                }

                mode = NONE;

                break;
            case MotionEvent.ACTION_MOVE:
                if (curChild != null) {

                    if (mode == DRAG) {
                        float dx = event.getX() - lastMoveStart.x;
                        float dy = event.getY() - lastMoveStart.y;
                        final float leftExceed = curChild.getLeftExceedOffset();
                        final float rightExceed = curChild.getRightExceedOffset();
                        lastMoveStart.set(event.getX(), event.getY());

                        boolean vOut = curChild.isVerticalOut();
                        if (!vOut) {
                            dy = 0;
                        } else {
                            // 当图片滑出上下边界时，模仿苹果手机的做法，上下方向的移动速度减半
                            int dBottom = curChild.getBottomExceedOffset();
                            int dTop = curChild.getTopExceedOffset();
                            if ((dy > 0 && dTop == 0) || (dy < 0 && dBottom == 0)) {
                                dy /= 2;
                            }
                        }

                        float pan = 0;
                        if (dx > 0) {
                            // drag right
                            if (leftExceed > 0 && getRightMargin(curChild) == 0) {
                                // 如果图片左部越界，并且右边顶屏幕，则需要pan内部图片
                                pan = Math.min(leftExceed, dx);
                                dx -= pan; // 同时减少gallery的scroll量
                            }
                            curChild.panBy(pan, dy);

                        } else {
                            // drag left
                            if (rightExceed > 0 && getLeftMargin(curChild) == 0) {
                                // 如果图片右部越界，并且左边顶屏幕，则需要pan内部图片
                                pan = Math.min(rightExceed, -dx);
                                dx += pan;// 同时减少gallery的scroll量
                            }
                            curChild.panBy(-pan, dy);

                        }
                        super.onScroll(null, null, -dx, 0);

                    } else if (mode == ZOOM) {
                        if (event.getX() > curChild.getRight() || event.getX() < curChild.getLeft()) {
                            return true;
                        }

                        float newDist = spacing(event);

                        if (newDist > 10f) {// 防止手指抖动
                            final int leftMargin = getLeftMargin(curChild);
                            final int rightMargin = getRightMargin(curChild);

                            float rate = newDist / oldDist;
                            oldDist = newDist;
                            float oldScale = curChild.getScale();

                            curChild.zoomTo(curChild.getScale() * rate, mid.x, mid.y, null);

                            // Well, newScale is not necessarily equal to
                            // oldScale * rate, since we have maxZoom limitation
                            // in MultiTouchImageView. 

                            // Be sure to call MultiTouchImageView.getScale() to
                            // get the real scale value
                            float newScale = curChild.getScale();

                            float scrollX = 0;
                            if (rightMargin > 0) {
                                // ImageView grows to right

                                scrollX = Math.min(rightMargin, curChild.getWidth() * (newScale / oldScale - 1F));

                                scrollX = -scrollX; // Take care

                            } else if (leftMargin > 0) {
                                // ImageView grows to left

                                scrollX = Math.min(leftMargin, curChild.getWidth() * (newScale / oldScale - 1F));

                            }
                            super.onScroll(null, null, scrollX, 0);

                            // scroll之后，如果左边或者右边仍然有margin，需要调整Image
                            adjustViewLeftRight(curChild);

                        }

                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    
    // 放大
    public void zoomIn() {
    	zoom(ZOOM_IN_RATE);
    }
    
    // 缩小
    public void zoomOut() {
    	zoom(ZOOM_OUT_RATE);
    }
    
    private void zoom(float rate) {
    	
    	curChild = (MultiTouchView) getSelectedView();
    	curChild.zoomTo(curChild.getScale() * rate, ZOOM_ANIMATION_DURATION);
    	
    	mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (curChild != null) {
		            final float scale = curChild.getScale();
		            final float maxScale = curChild.getMaxScale();
		            if (scale < 1.0) {
		                curChild.zoomTo(1F, ZOOM_ANIMATION_DURATION);
		            } else if (scale > maxScale) {
		                curChild.zoomTo(maxScale, ZOOM_ANIMATION_DURATION);
		            }
		        }
			}
		}, (int) ZOOM_ANIMATION_DURATION);
    	
        mode = NONE;
    }
    
    public void rotateToLeft() {
    	curChild = (MultiTouchView) getSelectedView();
    	curChild.refreshOrientation(-90);
    }
    
    public void rotateToRight() {
    	curChild = (MultiTouchView) getSelectedView();
    	curChild.refreshOrientation(90);
    }
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    protected int getLeftMargin(View child) {

        return Math.max(child.getLeft(), 0);
    }

    protected int getRightMargin(View child) {

        return Math.max(getWidth() - child.getRight(), 0);
    }

    private void adjustViewLeftRight(MultiTouchView view) {
        int leftMargin = getLeftMargin(curChild);
        int rightMargin = getRightMargin(curChild);
        if (rightMargin > 0) {
            curChild.adjustLeftRight(false);
        } else if (leftMargin > 0) {
            curChild.adjustLeftRight(true);
        }
    }

    /*public void refreshOrientation(int orientation) {
        curChild = (MultiTouchView) getSelectedView();
        if (curChild != null) {
            curChild.refreshOrientation(orientation);
        }

    }*/

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

//    public void recycle() {
//        int count = getCount();
//        for (int i = 0; i < count; ++i) {
//            MultiTouchView v = (MultiTouchView) this.getChildAt(i);
//            if (v != null)
//                v.recycle();
//        }
//    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
    	float centerX = e.getX();
    	float centerY = e.getY();
    	if (curChild != null) {
            final float scale = curChild.getScale();
            final float maxScale = curChild.getMaxScale();
            if (scale > 1.0) {
            	curChild.zoomTo(1f, centerX, centerY, ZOOM_DOUBLE_CLICK_DURATION);
            } else {
                curChild.zoomTo(maxScale, centerX, centerY, ZOOM_DOUBLE_CLICK_DURATION);
            }
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mActivity != null)
            mActivity.switchMode();
        return true;
    }

    // private ImageViewActivity mActivity;

    private IModeSwitchable mActivity;

    public void setHostActivity(IModeSwitchable activity) {
        mActivity = activity;
    }
    
    public void changeBackgroundColorTo(int color){
    	this.setBackgroundColor(color);
    }
}

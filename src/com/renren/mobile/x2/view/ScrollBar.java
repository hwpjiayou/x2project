package com.renren.mobile.x2.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import com.renren.mobile.x2.R;

/**
 *
 */
class ScrollBar extends View {
  private static final String TAG = "ScrollBar";
  private static final int FRAME_ANIMATION_DURATION = 1000 / 60;
  private static final int MSG_ANIMATE_DYING = 1000;
  private static final int MSG_ANIMATE_AWAKEN = 1001;
  private static final int ALPHA_VELOCITY = 10;
  private static final float TRANSPARENT = 0f;
  private static final float OPAQUE = 1f;

  private final int kVelocity;

  private long mLastAnimationTime;
  private long mCurrentAnimatingTime;
  private float mAnimatingAlpha;
  private int mAnimatingAlphaVelocity;
  private boolean mAnimating;

  /**
   *
   */
  private Drawable mDrawable;

  /**
   *
   */
  private int mDrawableWidth;

  /**
   *
   */
  private int mDrawableHeight;

  /**
   *
   */
  private float mPercent;

  /**
   *
   */
  private float mAlpha;

  /**
   * Handle animation loop.
   */
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(final Message msg) {
      switch (msg.what) {
        case MSG_ANIMATE_AWAKEN:
          computeAwaken();
          break;
        case MSG_ANIMATE_DYING:
          computeDying();
          break;
        default:
          break;
      }
    }
  };

  /**
   * @param context Context
   */
  ScrollBar(final Context context) {
    this(context, null);
  }

  /**
   * @param context Context
   * @param attrs   AttributeSet
   */
  ScrollBar(final Context context, final AttributeSet attrs) {
    super(context, attrs);

    final Resources r = getResources();
    final float density = r.getDisplayMetrics().density;

    kVelocity = (int) (0.5f + ALPHA_VELOCITY * density);

    TypedArray ta =
        context.obtainStyledAttributes(attrs, R.styleable.ScrollBar);

    mDrawable = ta.getDrawable(R.styleable.ScrollBar_scroll_bar_drawable);

    if (mDrawable == null) {
      mDrawable = new ShapeDrawable(new RectShape());
    }

    mDrawableWidth =
        ta.getDimensionPixelSize(R.styleable.ScrollBar_scroll_bar_width, 0);

    mDrawableHeight =
        ta.getDimensionPixelSize(R.styleable.ScrollBar_scroll_bar_height, 0);
  }

  /**
   * @param width The pixel width of the scroll bar.
   */
  public final void setWidth(final int width) {
    mDrawableWidth = width;
    requestLayout();
    invalidate();
  }

  /**
   * @param height The pixel height of the scroll bar.
   */
  public final void setHeight(final int height) {
    mDrawableHeight = height;
    requestLayout();
    invalidate();
  }

  /**
   * @param drawable The scroll bar drawable.
   */
  public final void setDrawable(final Drawable drawable) {
    mDrawable = drawable;
    requestLayout();
    invalidate();
  }

  @Override
  protected void onMeasure(final int widthMeasureSpec,
                           final int heightMeasureSpec) {
    setMeasuredDimension(mDrawableWidth,
        heightMeasureSpec & ~(0x3 << 30));
  }

  @Override
  protected void onDraw(final Canvas canvas) {
    mDrawable.setAlpha((int) (0.5 + Byte.MAX_VALUE * mAlpha));
    mDrawable.draw(canvas);
  }

  /**
   *
   */
  private void animateAwaken() {
    final long now = SystemClock.uptimeMillis();
    mLastAnimationTime = now;
    mCurrentAnimatingTime = now + FRAME_ANIMATION_DURATION;

    mAnimating = true;
    mAnimatingAlpha = TRANSPARENT;
    mAnimatingAlphaVelocity = kVelocity;
    mHandler.removeMessages(MSG_ANIMATE_AWAKEN);
    mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_AWAKEN),
        mCurrentAnimatingTime);
  }

  /**
   *
   */
  private void computeAwaken() {
    final long now = SystemClock.uptimeMillis();
    final float t = (now - mLastAnimationTime) / 1000f;
    mLastAnimationTime = now;
    mCurrentAnimatingTime = now + FRAME_ANIMATION_DURATION;

    mAnimatingAlpha += mAnimatingAlphaVelocity * t;

    if (mAnimatingAlpha >= OPAQUE) {
      mAnimating = false;
      mAlpha = OPAQUE;
    } else {
      mAlpha = mAnimatingAlpha;
      mHandler.removeMessages(MSG_ANIMATE_AWAKEN);
      mHandler.removeMessages(MSG_ANIMATE_DYING);
      mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_AWAKEN),
          mCurrentAnimatingTime);
    }

    invalidate();
  }

  /**
   *
   */
  private void animateDying() {
    final long now = SystemClock.uptimeMillis();
    mLastAnimationTime = now;
    mCurrentAnimatingTime = now + FRAME_ANIMATION_DURATION;

    mAnimating = true;
    mAnimatingAlpha = OPAQUE;
    mAnimatingAlphaVelocity = -kVelocity;
    mHandler.removeMessages(MSG_ANIMATE_AWAKEN);
    mHandler.removeMessages(MSG_ANIMATE_DYING);
    mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_DYING),
        mCurrentAnimatingTime);
  }

  /**
   *
   */
  private void computeDying() {
    final long now = SystemClock.uptimeMillis();
    final float t = (now - mLastAnimationTime) / 1000f;
    mLastAnimationTime = now;
    mCurrentAnimatingTime = now + FRAME_ANIMATION_DURATION;

    mAnimatingAlpha += mAnimatingAlphaVelocity * t;

    if (mAnimatingAlpha <= TRANSPARENT) {
      mAnimating = false;
      mAlpha = TRANSPARENT;
    } else {
      mAlpha = mAnimatingAlpha;
      mHandler.removeMessages(MSG_ANIMATE_DYING);
      mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_DYING),
          mCurrentAnimatingTime);
    }

    invalidate();
  }

}

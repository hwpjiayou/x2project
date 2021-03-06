package com.renren.mobile.x2.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import com.renren.mobile.x2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of HorizontalFlipLayout interface and the interceptions for host view methods.
 */
public class HorizontalFlipLayout extends FrameLayout implements IHorizontalFlip {
  private static final String TAG = "HorizontalFlipLayout";

  private final static int MSG_ANIMATE_LEFT = -100;
  private final static int MSG_ANIMATE_RIGHT = -101;
  private final static int MSG_ANIMATE_LEFT_OPEN = -104;
  private final static int MSG_ANIMATE_RIGHT_OPEN = -105;

  private final static int TRACK_DIRECTION_LEFT_MASK = 0x10 << 2;
  private final static int TRACK_DIRECTION_RIGHT_MASK = 0x10 << 1;
  private final static int TRACK_DIRECTION_HORIZONTAL_MASK = 0x10;

  private final static int TAP_THRESHOLD = 35;


  private static final Interpolator sInterpolator = new Interpolator() {
    public float getInterpolation(float t) {
      t -= 1.0f;
      return t * t * t * t * t + 1.0f;
    }
  };

  private final int kRightShadowWidth;
  private final int kLeftShadowWidth;

  private final Drawable kRightShadow;
  private final Drawable kLeftShadow;

  private float mLeftOffset;
  private float mRightOffset;

  private int mLeftTranslate;

  private final int mInterceptTouchEventThreshold;

  private boolean mLeftTapBack;
  private boolean mRightTapBack;

  private int mTrackDirection = 0x00;

  private int mPositionState;

  private final Rect mLeftFrameForTap = new Rect();
  private final Rect mRightFrameForTap = new Rect();
  private final Paint mBackgroundPaint;

  private int mLastDownX;
  private int mLastDownY;
  private int mLastMoveX;
  private boolean mLastMoveXBeenSet;

  private final AnimationHandler mHandler;
  private final Animator mAnimator;
  private final Tracker mTracker;

  private OnLeftAnimationListener mOnLeftAnimationListener;
  private OnRightAnimationListener mOnRightAnimationListener;
  private final List<OnOpenAnimationListener> mOnOpenAnimationListener =
      new ArrayList<OnOpenAnimationListener>();
  private OnLeftTrackListener mOnLeftTrackListener;
  private OnRightTrackListener mOnRightTrackListener;
  private OnHorizontalTrackListener mOnHorizontalTrackListener;

  public HorizontalFlipLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    mHandler = new AnimationHandler();
    mAnimator = new Animator();
    mTracker = new Tracker();
    mPositionState = STATE_EXPAND;

    Resources res = getResources();

    kRightShadowWidth = (int) (0.5f
        + res.getDimension(R.dimen.home_content_right_shadow_width));
    kRightShadow = res.getDrawable(R.drawable.v1_home_content_right_shadow);

    kLeftShadowWidth = (int) (0.5f
        + res.getDimension(R.dimen.home_content_left_shadow_width));
    kLeftShadow = res.getDrawable(R.drawable.v1_home_content_left_shodow);
    mBackgroundPaint = new Paint();
    mBackgroundPaint.setColor(0xFFFFFFFF);

    float density = res.getDisplayMetrics().density;
    mInterceptTouchEventThreshold = (int) (TAP_THRESHOLD * density + 0.5);

    loadAttrs(attrs);
  }

  /**
   * Load xml attributes
   *
   * @param attrs the attributes set from xml
   */
  private void loadAttrs(AttributeSet attrs) {
    TypedArray a =
        getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalFlipLayout);

    mLeftOffset =
        a.getDimension(R.styleable.HorizontalFlipLayout_left_offset, -1f);
    mRightOffset =
        a.getDimension(R.styleable.HorizontalFlipLayout_right_offset, -1f);

    parseTrack(a);
    parseTapBackArea(a);

    a.recycle();

    setClickable(true);
  }

  private void parseTrack(TypedArray a) {
    final String track = a.getString(R.styleable.HorizontalFlipLayout_track);
    if (track != null && track.length() > 0) {
      final String[] tracks = track.split("\\|");
      for (String s : tracks) {
        if (mLeftOffset != -1 && mRightOffset != -1 &&
            HORIZONTAL.equals(s) && (mTrackDirection & 0xF0) == 0) {
          Log.d(TAG, "@parseTrack horizontal");
          mTrackDirection |= TRACK_DIRECTION_HORIZONTAL_MASK;
        }
        if ((mRightOffset != -1) && RIGHT.equals(s) &&
            (mTrackDirection & 0xF0) == 0) {
          Log.d(TAG, "@parseTrack right");
          mTrackDirection |= TRACK_DIRECTION_RIGHT_MASK;
        }
        if ((mLeftOffset != -1) && LEFT.equals(s) &&
            (mTrackDirection & 0xF0) == 0) {
          Log.d(TAG, "@parseTrack left");
          mTrackDirection |= TRACK_DIRECTION_LEFT_MASK;
        }
      }
    }
  }

  private void parseTapBackArea(TypedArray a) {
    final String tapBackArea =
        a.getString(R.styleable.HorizontalFlipLayout_tap_back_area);
    if (tapBackArea != null && tapBackArea.length() > 0) {
      final String[] taps = tapBackArea.split("\\|");
      for (String s : taps) {
        Log.d(TAG, "@loadAttrs tap area " + s);
        if (LEFT.equals(s) && mLeftOffset != -1) {
          mLeftTapBack = true;
        } else if (RIGHT.equals(s) && mRightOffset != -1) {
          mRightTapBack = true;
        } else {
          Log.d(TAG, "@loadAttrs tap_back_area value illegal");
        }
      }
    }
  }

// --------------------- Interface IHorizontalFlip ---------------------


  /**
   * The offset value when flip to mLeft.
   *
   * @return the mLeft offset
   */
  @Override
  public int getLeftOffset() {
    return (int) mLeftOffset;
  }

  /**
   * The offset value when flip to right.
   *
   * @return the mTop offset
   */
  @Override
  public int getRightOffset() {
    return (int) mRightOffset;
  }

  /**
   * tap left offset area to flip back.
   *
   * @return true if allow tap back
   */
  @Override
  public boolean isLeftTapBack() {
    return mLeftTapBack;
  }

  /**
   * tap right offset area to flip back.
   *
   * @return true if allow tap back
   */
  @Override
  public boolean isRightTapBack() {
    return mRightTapBack;
  }

  /**
   * Set left offset area could tap back.
   *
   * @param tapBack tap back
   */
  @Override
  public void setLeftTapBack(boolean tapBack) {
    mLeftTapBack = tapBack;
  }

  /**
   * Set right offset area could tap back.
   *
   * @param tapBack tap back
   */
  @Override
  public void setRightTapBack(boolean tapBack) {
    mRightTapBack = tapBack;
  }

  /**
   * Flip left immediately, without animation.
   */
  @Override
  public void left() {
    mLeftTranslate = (int) (mLeftOffset - getMeasuredWidth());
    mPositionState = STATE_COLLAPSE_LEFT;
    invalidate();
  }

  /**
   * Flip right immediately, without animation.
   */
  @Override
  public void right() {
    mLeftTranslate = (int) (getMeasuredWidth() - mRightOffset);
    mPositionState = STATE_COLLAPSE_RIGHT;
    invalidate();
  }

  /**
   * Open host view when flipped.
   */
  @Override
  public void open() {
    mLeftTranslate = 0;
    mPositionState = STATE_EXPAND;
    invalidate();
  }

  /**
   * Animation version of flipping
   */
  @Override
  public void animateLeft() {
    if (canLeft()) {
      mAnimator.animateLeft(-mAnimator.kVelocity);
    }
  }

  /**
   * Animation version of flipping
   */
  @Override
  public void animateRight() {
    if (canRight()) {
      mAnimator.animateRight(mAnimator.kVelocity);
    }
  }

  /**
   * Animation version of flipping
   */
  @Override
  public void animateOpen() {
    switch (mPositionState) {
      case STATE_COLLAPSE_LEFT:
        mAnimator.animateLeftOpen(mAnimator.kVelocity);
        break;
      case STATE_COLLAPSE_RIGHT:
        mAnimator.animateRightOpen(-mAnimator.kVelocity);
        break;
      default:
        break;
    }
  }

  /**
   * Get flipping state
   *
   * @return the state
   */
  @Override
  public int getState() {
    return mPositionState;
  }

  @Override
  public void setLeftAnimationListener(OnLeftAnimationListener listener) {
    mOnLeftAnimationListener = listener;
  }

  @Override
  public void setRightAnimationListener(OnRightAnimationListener listener) {
    mOnRightAnimationListener = listener;
  }

  @Override
  public void addOpenAnimationListener(OnOpenAnimationListener listener) {
    mOnOpenAnimationListener.add(listener);
  }

  @Override
  public void removeOpenAnimationListener(OnOpenAnimationListener listener) {
    mOnOpenAnimationListener.remove(listener);
  }

  @Override
  public void setLeftTrackListener(OnLeftTrackListener listener) {
    mOnLeftTrackListener = listener;
  }

  @Override
  public void setRightTrackListener(OnRightTrackListener listener) {
    mOnRightTrackListener = listener;
  }

  @Override
  public void setHorizontalTrackListener(OnHorizontalTrackListener listener) {
    mOnHorizontalTrackListener = listener;
  }

  private boolean canLeft() {
    return mLeftOffset != -1 && mPositionState == STATE_EXPAND;
  }

  private boolean canRight() {
    return mRightOffset != -1 && mPositionState == STATE_EXPAND;
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    canvas.save();
    canvas.translate(mLeftTranslate, 0);
    Log.d(TAG, "@dispatchDraw " + mLeftTranslate);
    canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mBackgroundPaint);
    kRightShadow.draw(canvas);
    kLeftShadow.draw(canvas);
    super.dispatchDraw(canvas);
    canvas.restore();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return mAnimator.iAnimating || super.dispatchTouchEvent(ev);
  }

  public int getLeftTranslate() {
    return mLeftTranslate;
  }

  public boolean isAnimating() {
    return mAnimator.iAnimating;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    ev.offsetLocation(-mLeftTranslate, 0);
    final int action = ev.getAction() & MotionEvent.ACTION_MASK;
    final int x = (int) ev.getX();
    final int y = (int) ev.getY();

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mLastDownX = x;
        mLastDownY = y;
        break;
      case MotionEvent.ACTION_MOVE:
        Log.d(TAG, "@interceptInterceptTouchEvent");

        if (mPositionState == STATE_EXPAND) {
          if (y < mLastDownY - mInterceptTouchEventThreshold ||
              y > mLastDownY + mInterceptTouchEventThreshold) {
            return false;
          }

          if ((mTrackDirection & 0xF0) > 0 &&
              (x < mLastDownX - mInterceptTouchEventThreshold ||
                  x > mLastDownX +
                      mInterceptTouchEventThreshold)) {
            switch (mTrackDirection & 0xF0) {
              case TRACK_DIRECTION_LEFT_MASK:
                return mTracker.prepareLeftTrack();
              case TRACK_DIRECTION_RIGHT_MASK:
                return mTracker.prepareRightTrack();
              case TRACK_DIRECTION_HORIZONTAL_MASK:
                return mTracker.prepareHorizontalTrack(x - mLastDownX);
              default:
                break;
            }
          }
          return false;
        } else {
          switch (mTrackDirection & 0xF0) {
            case TRACK_DIRECTION_LEFT_MASK:
              return mTracker.prepareLeftTrack();
            case TRACK_DIRECTION_RIGHT_MASK:
              return mTracker.prepareRightTrack();
            case TRACK_DIRECTION_HORIZONTAL_MASK:
              return mTracker.prepareHorizontalTrack(x - mLastDownX);
          }
        }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        if (mLastDownX - mInterceptTouchEventThreshold < x &&
            x < mLastDownX + mInterceptTouchEventThreshold) {
          if (mLeftTapBack && mLeftFrameForTap.contains(x, y) &&
              mPositionState == STATE_COLLAPSE_LEFT) {
            mAnimator.animateLeftOpen(mAnimator.kVelocity);
          } else if (mRightTapBack && mRightFrameForTap.contains(x, y) &&
              mPositionState == STATE_COLLAPSE_RIGHT) {
            mAnimator.animateRightOpen(-mAnimator.kVelocity);
          } else {
            return false;
          }
          return true;
        }
      default:
        break;
    }
    return false;
  }

  /**
   */
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    final int width = r - l;
    final int height = b - t;

    kRightShadow.setBounds(width, 0, width + kRightShadowWidth, height);
    kLeftShadow.setBounds(-kLeftShadowWidth, 0, 0, height);

    if (changed) {
      if (mLeftOffset != -1) {
        mLeftFrameForTap.set((int) (r - mLeftOffset), t, r, b);
      }

      if (mRightOffset != -1) {
        mRightFrameForTap.set(l, t, (int) (l + mRightOffset), b);
      }
    }

    if (!mAnimator.iAnimating && !mTracker.tracking) {
      offset();
    }
  }

  /**
   * The offset will be reset after every layout, so we do an additional offset when layout based on
   * the position state.
   */
  private void offset() {
    switch (mPositionState) {
      case STATE_EXPAND:
        mLeftTranslate = 0;
        invalidate();
        break;
      case STATE_COLLAPSE_LEFT:
        mLeftTranslate = (int) (mLeftOffset - getMeasuredWidth());
        invalidate();
        break;
      case STATE_COLLAPSE_RIGHT:
        mLeftTranslate = (int) (getMeasuredWidth() - mRightOffset);
        invalidate();
        break;
    }
  }

  /**
   */
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);

    //check the offsets' sizes are not larger than the view's dimension
    assert widthSize >= mLeftOffset :
        "left offset should not be larger than the view's width";
    assert widthSize >= mRightOffset :
        "right offset should not be larger than the view's width";
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    final int x = (int) ev.getX();
    final int y = (int) ev.getY();
    //Log.d(TAG, String.format("@interceptTouch x %d, y %d", x, y));
    final int action = ev.getAction() & MotionEvent.ACTION_MASK;

    switch (action) {
      case MotionEvent.ACTION_MOVE:
        if (mTracker.tracking) {
          if (!mLastMoveXBeenSet) {
            if (mPositionState != STATE_EXPAND) {
              mLastMoveX = mLastDownX + mLeftTranslate;
              mLastMoveXBeenSet = true;
            } else if (x > mLastDownX) {
              mLastMoveX = mLastDownX + mInterceptTouchEventThreshold;
              mLastMoveXBeenSet = true;
            } else {
              mLastMoveX = mLastDownX - mInterceptTouchEventThreshold;
              mLastMoveXBeenSet = true;
            }
          }

          mTracker.move(mLastMoveX - x);
          mLastMoveX = x;
          //ev.offsetLocation(mLeftTranslate, 0);
          mTracker.velocityTracker.addMovement(ev);
        }
        break;
      case MotionEvent.ACTION_UP:
        Log.d(TAG, "@onTouchEvent up");
        if (mPositionState != STATE_EXPAND) {
          mTracker.stopTracking();
          mLastMoveXBeenSet = false;
          if (mLeftTapBack && mPositionState == STATE_COLLAPSE_LEFT) {
            Log.d(TAG, "@onTouchEvent left open");
            mAnimator.animateLeftOpen(mAnimator.kVelocity);
          } else if (mRightTapBack && mPositionState == STATE_COLLAPSE_RIGHT) {
            Log.d(TAG, "@onTouchEvent right open");
            mAnimator.animateRightOpen(-mAnimator.kVelocity);
          }
          return true;
        }
        if (mTracker.tracking) {
          Log.d(TAG, "@onTouchEvent");
          mTracker.stopTracking();
          mTracker.fling();
          mLastMoveXBeenSet = false;
          return true;
        }
      default:
        return false;
    }
    return true;
  }

  private class AnimationHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      if (!mAnimator.iAnimating) {
        return;
      }
      switch (msg.what) {
        case MSG_ANIMATE_LEFT:
          mAnimator.computeLeftAnimation();
          break;
        case MSG_ANIMATE_RIGHT:
          mAnimator.computeRightAnimation();
          break;
        case MSG_ANIMATE_LEFT_OPEN:
          mAnimator.computeLeftOpenAnimation();
          break;
        case MSG_ANIMATE_RIGHT_OPEN:
          mAnimator.computeRightOpenAnimation();
          break;
        default:
          break;
      }
    }
  }

  /**
   * Tracker can handle the dragging of the host view.
   */
  private class Tracker {
    static final int VELOCITY_UNIT = 200;
    static final float MIN_VELOCITY = 500;

    VelocityTracker velocityTracker;

    boolean tracking;
    int direction;
    final int velocityUnit;
    final int minVelocity;

    Tracker() {
      float density = getContext().getResources().getDisplayMetrics().density;
      velocityUnit = (int) (VELOCITY_UNIT * density + 0.5f);
      minVelocity = (int) (MIN_VELOCITY * density + 0.5f);
    }

    void prepareTracking() {
      velocityTracker = VelocityTracker.obtain();
      tracking = true;
    }

    void stopTracking() {
      tracking = false;
    }

    boolean prepareLeftTrack() {
      if (mPositionState != STATE_EXPAND &&
          mPositionState != STATE_COLLAPSE_LEFT) {
        return false;
      }
      prepareTracking();
      direction = TRACK_DIRECTION_LEFT_MASK;
      return true;
    }

    boolean prepareRightTrack() {
      if (mPositionState != STATE_EXPAND &&
          mPositionState != STATE_COLLAPSE_RIGHT) {
        return false;
      }
      prepareTracking();
      direction = TRACK_DIRECTION_RIGHT_MASK;
      return true;
    }

    boolean prepareHorizontalTrack(int d) {
      prepareTracking();
      direction = TRACK_DIRECTION_HORIZONTAL_MASK;
      if (mOnHorizontalTrackListener != null) {
        final OnHorizontalTrackListener listener = mOnHorizontalTrackListener;
        listener.onHorizontalTrackListener(d);
      }
      return true;
    }

    void move(int xOffset) {
      if (!tracking) {
        return;
      }
      final int left = mLeftTranslate;
      switch (direction) {
        case TRACK_DIRECTION_LEFT_MASK:
          Log.d(TAG, "@move left");
          if (left > mLeftOffset - getMeasuredWidth() && left < 0) {
            mLeftTranslate -= xOffset;
            invalidate();
          }
          break;
        case TRACK_DIRECTION_RIGHT_MASK:
          Log.d(TAG, "@move right");
          if (left < getMeasuredWidth() - mRightOffset && left > 0) {
            mLeftTranslate -= xOffset;
            invalidate();
          }
          break;
        case TRACK_DIRECTION_HORIZONTAL_MASK:
          Log.d(TAG, "@move horizontal xOffset=" + xOffset);
          if (left >= mLeftOffset - getMeasuredWidth() + xOffset
              && left <= getMeasuredWidth() - mRightOffset + xOffset) {
            mLeftTranslate -= xOffset;
            invalidate();
          }
          break;
        default:
          break;
      }
    }

    /**
     * 拖动结束之后，我们会根据速度和位置把View动画到合适的位置。
     */
    private void fling() {
      velocityTracker.computeCurrentVelocity(velocityUnit);
      float xVelocity = velocityTracker.getXVelocity();

      Log.d(TAG, "@fling x " + xVelocity);

      if (xVelocity < 0) {
        xVelocity = Math.min(xVelocity, -minVelocity);
      } else {
        xVelocity = Math.max(xVelocity, minVelocity);
      }

      switch (direction) {
        case TRACK_DIRECTION_HORIZONTAL_MASK:
          horizontalFling(xVelocity);
          break;
        case TRACK_DIRECTION_LEFT_MASK:
          leftFling(xVelocity);
          break;
        case TRACK_DIRECTION_RIGHT_MASK:
          rightFling(xVelocity);
          break;
        default:
          break;
      }

      velocityTracker.recycle();
      velocityTracker = null;
    }

    private void horizontalFling(float velocity) {
      Log.d(TAG, "@horizontalFling");
      final int left = mLeftTranslate;
      if (left <= 0 && left >= mLeftOffset - getMeasuredWidth()) {
        if (velocity < 0) {
          mAnimator.animateLeft(velocity);
        } else {
          mAnimator.animateLeftOpen(velocity);
        }
      } else if (left >= 0 && left <= getMeasuredWidth() - mRightOffset) {
        if (velocity < 0) {
          mAnimator.animateRightOpen(velocity);
        } else {
          mAnimator.animateRight(velocity);
        }
      }
    }

    private void leftFling(float velocity) {
      Log.d(TAG, "@leftFling");
      if (velocity < 0) {
        Log.d(TAG, "@leftFling animateLeft " + velocity);
        mAnimator.animateLeft(velocity);
      } else {
        Log.d(TAG, "@leftFling animateLeftOpen " + velocity);
        mAnimator.animateLeftOpen(velocity);
      }
    }

    private void rightFling(float velocity) {
      Log.d(TAG, "@rightFling");
      if (velocity < 0) {
        mAnimator.animateRightOpen(velocity);
      } else {
        mAnimator.animateRight(velocity);
      }
    }
  }

  private class Animator {
    static final String TAG = "IAwesomeImpl$Animator";

    static final int FRAME_ANIMATION_DURATION = 1000 / 60;

    static final int VELOCITY = 600;
    static final int MIN_VELOCITY = 300;

    final float kVelocity;
    final float kMinVelocity;

    float iAnimatingPosition;
    float iAnimatingVelocity;
    float iAnimationDistance;
    float iAnimationStart;
    long iLastAnimationTime;
    long iCurrentAnimationTime;

    boolean iAnimating;

    Animator() {
      final float density =
          getContext().getResources().getDisplayMetrics().density;
      kVelocity = VELOCITY * density;
      kMinVelocity = MIN_VELOCITY * density;
    }

    private void compute() {
      final long now = SystemClock.uptimeMillis();
      final float t = (now - iLastAnimationTime) / 1000f;
      iAnimatingPosition += iAnimatingVelocity * t;
      iLastAnimationTime = now;
      iCurrentAnimationTime += FRAME_ANIMATION_DURATION;
    }

    void computeLeftAnimation() {
      compute();
      if (iAnimatingPosition <= iAnimationDistance) {
        final OnLeftAnimationListener listener = mOnLeftAnimationListener;
        if (listener != null) {
          listener.onLeftAnimationEnd();
        }
        iAnimating = false;
        mPositionState = STATE_COLLAPSE_LEFT;
        offset();
      } else {
        float offset = iAnimationDistance *
            sInterpolator.getInterpolation(
                iAnimatingPosition / iAnimationDistance);
        Log.d(TAG, "@computeLeftAnimation " + offset);
        mLeftTranslate = (int) (offset + iAnimationStart);
        invalidate();
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_LEFT), iCurrentAnimationTime);
      }
    }

    void computeRightAnimation() {
      compute();
      if (iAnimatingPosition >= iAnimationDistance) {
        final OnRightAnimationListener listener = mOnRightAnimationListener;
        if (listener != null) {
          listener.onRightAnimationEnd();
        }
        iAnimating = false;
        mPositionState = STATE_COLLAPSE_RIGHT;
        offset();
      } else {
        float offset = iAnimationDistance *
            sInterpolator.getInterpolation(
                iAnimatingPosition / iAnimationDistance);
        mLeftTranslate = (int) (offset + iAnimationStart);
        invalidate();
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_RIGHT), iCurrentAnimationTime);
      }
    }

    void computeLeftOpenAnimation() {
      compute();
      if (iAnimatingPosition >= iAnimationDistance) {
        for (final OnOpenAnimationListener listener : mOnOpenAnimationListener) {
          if (listener != null) {
            listener.onOpenAnimationEnd();
          }
        }
        iAnimating = false;
        mPositionState = STATE_EXPAND;
        offset();
      } else {
        float offset = iAnimationDistance *
            sInterpolator.getInterpolation(
                iAnimatingPosition / iAnimationDistance);
        mLeftTranslate = (int) (offset + iAnimationStart);
        invalidate();
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_LEFT_OPEN), iCurrentAnimationTime);
      }
    }

    void computeRightOpenAnimation() {
      compute();
      if (iAnimatingPosition <= iAnimationDistance) {
        for (final OnOpenAnimationListener listener : mOnOpenAnimationListener) {
          if (listener != null) {
            listener.onOpenAnimationEnd();
          }
        }
        iAnimating = false;
        mPositionState = STATE_EXPAND;
        offset();
      } else {
        float offset = iAnimationDistance *
            sInterpolator.getInterpolation(
                iAnimatingPosition / iAnimationDistance);
        mLeftTranslate = (int) (offset + iAnimationStart);
        invalidate();
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_RIGHT_OPEN), iCurrentAnimationTime);
      }
    }

    void animateLeftOpen(float velocity) {
      for (final OnOpenAnimationListener listener : mOnOpenAnimationListener) {
        if (listener != null) {
          listener.onOpenAnimationStart();
        }
      }
      iAnimating = true;
      final long now = SystemClock.uptimeMillis();
      iLastAnimationTime = now;
      iCurrentAnimationTime = now + FRAME_ANIMATION_DURATION;
      iAnimatingVelocity = velocity;
      iAnimatingPosition = 0;
      iAnimationDistance = 0 - mLeftTranslate;
      iAnimationStart = mLeftTranslate;
      mHandler.removeMessages(MSG_ANIMATE_LEFT_OPEN);
      Log.d(TAG, "@animateLeftOpen " + iAnimationDistance);
      Log.d(TAG, "@animateLeftOpen " + velocity);
      mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_LEFT_OPEN), iCurrentAnimationTime);
    }

    void animateRightOpen(float velocity) {
      for (final OnOpenAnimationListener listener : mOnOpenAnimationListener) {
        if (listener != null) {
          listener.onOpenAnimationStart();
        }
      }
      iAnimating = true;
      final long now = SystemClock.uptimeMillis();
      iLastAnimationTime = now;
      iCurrentAnimationTime = now + FRAME_ANIMATION_DURATION;
      iAnimatingVelocity = velocity;
      iAnimatingPosition = 0;
      iAnimationDistance = 0 - mLeftTranslate;
      iAnimationStart = mLeftTranslate;
      Log.d(TAG, "@animateRightOpen " + iAnimationDistance);
      Log.d(TAG, "@animateRightOpen " + velocity);
      mHandler.removeMessages(MSG_ANIMATE_RIGHT_OPEN);
      mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_RIGHT_OPEN), iCurrentAnimationTime);
    }

    void animateLeft(float velocity) {
      final OnLeftAnimationListener listener = mOnLeftAnimationListener;
      if (listener != null) {
        listener.onLeftAnimationStart();
      }
      iAnimating = true;
      final long now = SystemClock.uptimeMillis();
      iLastAnimationTime = now;
      iCurrentAnimationTime = now + FRAME_ANIMATION_DURATION;
      iAnimatingVelocity = velocity;
      iAnimatingPosition = 0;
      iAnimationDistance = -getMeasuredWidth() + mLeftOffset - mLeftTranslate;
      iAnimationStart = mLeftTranslate;
      Log.d(TAG, "@animateLeft " + iAnimationDistance);
      Log.d(TAG, "@animateLeft " + velocity);
      mHandler.removeMessages(MSG_ANIMATE_LEFT);
      mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_LEFT), iCurrentAnimationTime);
    }

    void animateRight(float velocity) {
      final OnRightAnimationListener listener = mOnRightAnimationListener;
      if (listener != null) {
        listener.onRightAnimationStart();
      }
      iAnimating = true;
      final long now = SystemClock.uptimeMillis();
      iLastAnimationTime = now;
      iCurrentAnimationTime = now + FRAME_ANIMATION_DURATION;
      iAnimatingVelocity = velocity;
      iAnimatingPosition = 0;
      iAnimationDistance = (getMeasuredWidth() - mRightOffset) - mLeftTranslate;
      iAnimationStart = mLeftTranslate;
      Log.d(TAG, "@animateRight " + iAnimationDistance);
      Log.d(TAG, "@animateRight " + velocity);
      mHandler.removeMessages(MSG_ANIMATE_RIGHT);
      mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_RIGHT), iCurrentAnimationTime);
    }
  }
}

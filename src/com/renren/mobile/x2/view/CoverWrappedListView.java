package com.renren.mobile.x2.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.refresh.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AbsListView.OnScrollListener;

/**
 * A pull down to refresh view component with a cover.
 */
public class CoverWrappedListView extends ListView implements OnScrollListener {
  private static final String TAG = "CoverWrappedListView";
  private static final int MSG_ANIMATE_PULL_BACK = 1000;
  private static final int MSG_ANIMATE_SHOW_PINNED_TOP = 1100;
  private static final int MSG_ANIMATE_HIDE_PINNED_TOP = 1200;
  private static final int PINNED_TOP_STATE_SHOW = 10000;
  private static final int PINNED_TOP_STATE_HIDE = 10001;
  private static final int FRAME_ANIMATION_DURATION = 1000 / 60;
  private static final int MOVE_THRESHOLD = 10;
  private static final float SECOND = 1000;
  private static final Interpolator sInterpolator = new Interpolator() {
    public float getInterpolation(float t) {
      t -= 1.0f;

      return t * t * t * t * t + 1.0f;
    }
  };
  private final List<AbsListView.OnScrollListener> mOnScrollListeners =
      new ArrayList<AbsListView.OnScrollListener>();
  private final int[] mTempLocation = new int[2];
  private final int[] mPinnedTopLocation = new int[2];
  private ImageView mCoverImage;
  private ImageView mShadow;
  private Header mHeader;
  private View mTop;
  private View mPinnedTop;
  private final int kHeaderHeight;
  private final int kCoverHeight;
  private final int kRefreshThreshold;
  private final int kMaxYOffset;
  private int mLastDownY;
  private int mYOffset;
  private int mPinnedTopOffset;
  private int mAbsY;
  private final AnimateHandler mHandler;
  private final PinnedTopAnimator mPinnedTopAnimator;
  private final Animator mAnimator;
  private OnRefreshListener mOnRefreshListener;
  private RefreshAsyncTask mRefreshAsyncTask;
  private int mPinnedTopMeasuredHeight;
  private int mLastMoveY;
  private final Paint mBackgroundPaint;
  private boolean mShouldDrawHeader;
  private boolean mShouldDrawBackground;
  private boolean mShouldDrawPinnedTop;
  private int mPinnedTopState = PINNED_TOP_STATE_HIDE;
  private final int kMoveThreshold;

  private Drawable mScrollBar;

  private final OnScrollListener mInnerScrollListener =
      new OnScrollListener() {
        @Override
        public void onScrollStateChanged(final AbsListView view,
                                         final int scrollState) {
        }

        @Override
        public void onScroll(final AbsListView view, final int firstVisibleItem,
                             final int visibleItemCount,
                             final int totalItemCount) {
          mShouldDrawHeader = firstVisibleItem == 0;

          if (mPinnedTopOffset > 0) {
            mPinnedTop.getLocationOnScreen(mPinnedTopLocation);

            if (mPinnedTopLocation[1] < mAbsY + kHeaderHeight
                - mPinnedTopMeasuredHeight + mPinnedTopOffset) {
              mShouldDrawPinnedTop = true;
            } else {
              mPinnedTopOffset = 0;
              mShouldDrawPinnedTop = false;
            }
          }

          View childAt = getChildAt(0);
          if (childAt != null) {
            childAt.getLocationOnScreen(mTempLocation);
          }
        }
      };

  public CoverWrappedListView(final Context context) {
    this(context, null, 0);
  }

  public CoverWrappedListView(final Context context,
                              final AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CoverWrappedListView(final Context context,
                              final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);
    mHandler = new AnimateHandler();
    mAnimator = new Animator();
    mPinnedTopAnimator = new PinnedTopAnimator();
    Resources res = getResources();
    final float density = res.getDisplayMetrics().density;
    kHeaderHeight = (int) (0.5f
        + res.getDimension(R.dimen.cover_refresher_header_height));
    kCoverHeight = (int) (0.5f
        + res.getDimension(R.dimen.cover_refresher_cover_height));
    kRefreshThreshold = (int) (0.5f
        + res.getDimension(R.dimen.cover_refresher_refresh_threshold));
    kMoveThreshold = (int) (0.5f + density * MOVE_THRESHOLD);
    kMaxYOffset = kCoverHeight - kHeaderHeight;
    mBackgroundPaint = new Paint();
    mBackgroundPaint.setColor(0xFFFFFFFF);
    setOnScrollListener(this);
    setCacheColorHint(0x00FFFFFF);
    mHeader = new Header(context);
    mHeader.setLayoutParams(
        new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
            kHeaderHeight));
    addHeaderView(mHeader);
    addOnScrollListener(mInnerScrollListener);
    mScrollBar = new ShapeDrawable(new RectShape());
  }

  public void addOnScrollListener(final OnScrollListener listener) {
    mOnScrollListeners.add(listener);
  }

  //  --------------------- Interface OnScrollListener ---------------------
  @Override
  public final void onScrollStateChanged(final AbsListView view,
                                         final int scrollState) {
    for (final OnScrollListener listener : mOnScrollListeners) {
      listener.onScrollStateChanged(view, scrollState);
    }
  }

  @Override
  public final void onScroll(final AbsListView view,
                             final int firstVisibleItem,
                             final int visibleItemCount,
                             final int totalItemCount) {
    for (final OnScrollListener listener : mOnScrollListeners) {
      listener.onScroll(view, firstVisibleItem,
          visibleItemCount, totalItemCount);
    }
  }

  public void clearOnScrollListener() {
    mOnScrollListeners.clear();
  }

  @Override
  protected final void dispatchDraw(final Canvas canvas) {
    final long drawingTime = getDrawingTime();
    final int first = getFirstVisiblePosition();
    final int visible = getLastVisiblePosition() - first;

    canvas.save();

    if (mShouldDrawHeader) {
      drawChild(canvas, mHeader, drawingTime);
    }

    canvas.translate(0, (kHeaderHeight - kCoverHeight) + (mYOffset >> 1));

    if (mShouldDrawBackground) {
      canvas.drawRect(0, kCoverHeight, getMeasuredWidth(),
          getMeasuredHeight(),
          mBackgroundPaint);
    }

    for (int i = (first == 0) ? 1 : 0; i <= visible; i++) {
      drawChild(canvas, getChildAt(i), drawingTime);
    }

    canvas.restore();

    if (mShouldDrawPinnedTop) {
      canvas.save();
      canvas.translate(0,
          -mPinnedTop.getTop() - mPinnedTopMeasuredHeight
              + mPinnedTopOffset);
      drawChild(canvas, mPinnedTop, drawingTime);
      canvas.restore();
    }

    //mScrollBar.draw(canvas);
  }

  @Override
  public final boolean dispatchTouchEvent(final MotionEvent event) {
    if ((mPinnedTopOffset > 0) && (event.getY() < mPinnedTopOffset)) {
      Log.d(TAG, "@dispatchTouchEvent to pinned top");
      mPinnedTop.invalidate();

      return mPinnedTop.dispatchTouchEvent(event) | true;
    }

    event.offsetLocation(0, kCoverHeight - kHeaderHeight);

    final int action = event.getAction() & MotionEvent.ACTION_MASK;
    final int y = (int) event.getY();

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mLastDownY = y;
        mLastMoveY = y;
        mHandler.removeMessages(MSG_ANIMATE_PULL_BACK);
        break;

      case MotionEvent.ACTION_MOVE:
        final int firstVisiblePosition = getFirstVisiblePosition();

        if (firstVisiblePosition == 0) {
          final View childAt = getChildAt(0);

          if (childAt != null) {
            childAt.getLocationOnScreen(mTempLocation);

            if ((mTempLocation[1] == mAbsY) && (y > mLastDownY)) {
              mYOffset += y - mLastMoveY;
              mYOffset = Math.min(mYOffset, kMaxYOffset);
            }
          } else if (y < mLastDownY) {
            mYOffset = 0;
          }
        }


        if (firstVisiblePosition > 0 && !mPinnedTopAnimator.animating) {
          if (y - mLastMoveY > kMoveThreshold
              && mPinnedTopState == PINNED_TOP_STATE_HIDE) {
            mPinnedTopAnimator.animateShowPinnedTop();
          } else if (y - mLastMoveY < -kMoveThreshold
              && mPinnedTopState == PINNED_TOP_STATE_SHOW) {
            mPinnedTopAnimator.animateHidePinnedTop();
          }
        }

        mLastMoveY = y;

        break;

      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        if ((mHeader.getTop() == getTop()) && (y > mLastDownY)) {
          if (y - mLastDownY >= kRefreshThreshold) {
            if ((mRefreshAsyncTask == null)
                || (mRefreshAsyncTask.getStatus()
                != AsyncTask.Status.RUNNING)) {
              mRefreshAsyncTask = new RefreshAsyncTask();
              mRefreshAsyncTask.execute((Void[]) null);
            }
          }
        }

        mAnimator.animatePullBack();

        break;
      default:
        break;
    }

    invalidate();
    mHeader.invalidate();

    return (mYOffset > 0) || super.dispatchTouchEvent(event);
  }

  /**
   * @return this view instance
   */
  public final ListView getListView() {
    return this;
  }

  @Override
  protected final void onLayout(final boolean changed, final int l,
                                final int t, final int r, final int b) {
    super.onLayout(changed, l, t, r, b + kCoverHeight - kHeaderHeight);
    getLocationOnScreen(mTempLocation);
    mAbsY = mTempLocation[1];

    mShouldDrawBackground = getChildCount() <= 2;

    mScrollBar.setBounds(r - 30, 0, r - 15, 100);
  }

  @Override
  protected final void onMeasure(final int widthMeasureSpec,
                                 final int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec,
        heightMeasureSpec + kCoverHeight - kHeaderHeight);
    measureChild(mHeader, widthMeasureSpec,
        kCoverHeight + MeasureSpec.EXACTLY);
  }

  public final void removeOnScrollListener(final OnScrollListener listener) {
    mOnScrollListeners.remove(listener);
  }

  public final void setCoverImageBitmap(final Bitmap bitmap) {
    mCoverImage.setImageBitmap(bitmap);
  }

  public final void setCoverImageDrawable(final Drawable drawable) {
    mCoverImage.setImageDrawable(drawable);
  }

  public final void setCoverImageResource(final int resource) {
    mCoverImage.setImageResource(resource);
  }

  public void setOnRefreshListener(final OnRefreshListener refreshListener) {
    mOnRefreshListener = refreshListener;
  }

  public void setTabHostView(final View view) {
    if (view != null) {
      mHeader.removeView(mPinnedTop);
      mPinnedTop = view;
      mHeader.addView(mPinnedTop,
          new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
              LayoutParams.WRAP_CONTENT));
    }
  }

  public void setTopView(final View view) {
    if (view != null) {
      mHeader.removeView(mTop);
      mTop = view;
      mHeader.addView(mTop,
          new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
              LayoutParams.WRAP_CONTENT));
    }
  }

  private class AnimateHandler extends Handler {
    @Override
    public void handleMessage(final Message msg) {
      switch (msg.what) {
        case MSG_ANIMATE_PULL_BACK:
          mAnimator.computePullBack();
          break;
        case MSG_ANIMATE_SHOW_PINNED_TOP:
          mPinnedTopAnimator.computeShowPinnedTop();
          break;
        case MSG_ANIMATE_HIDE_PINNED_TOP:
          mPinnedTopAnimator.computeHidePinnedTop();
        default:
          break;
      }
    }
  }

  private class PinnedTopAnimator {
    static final int VELOCITY = 200;
    private boolean animating;
    private long lastAnimationTime;
    private long currentAnimatingTime;
    private final int kVelocity;
    private int animatingVelocity;
    private int animatingPosition;

    public PinnedTopAnimator() {
      kVelocity = (int) (getResources().getDisplayMetrics().density
          * VELOCITY + 0.5f);
    }

    void computeShowPinnedTop() {
      final long now = SystemClock.uptimeMillis();
      final float t = (now - lastAnimationTime) / SECOND;

      animatingPosition += animatingVelocity * t;

      if (animatingPosition >= mPinnedTopMeasuredHeight) {
        mPinnedTopOffset = mPinnedTopMeasuredHeight;
        animating = false;
        mPinnedTopState = PINNED_TOP_STATE_SHOW;
      } else {
        mPinnedTopOffset = animatingPosition;
        lastAnimationTime = now;
        currentAnimatingTime = now + FRAME_ANIMATION_DURATION;
        mHandler.removeMessages(MSG_ANIMATE_SHOW_PINNED_TOP);
        mHandler.sendMessageAtTime(
            mHandler.obtainMessage(MSG_ANIMATE_SHOW_PINNED_TOP),
            currentAnimatingTime);
      }
      invalidate(0, 0, getMeasuredWidth(), mPinnedTopMeasuredHeight);
    }

    void animateShowPinnedTop() {
      final long now = SystemClock.uptimeMillis();

      lastAnimationTime = now;
      currentAnimatingTime = now + FRAME_ANIMATION_DURATION;
      animating = true;
      animatingPosition = mPinnedTopOffset;
      animatingVelocity = kVelocity;
      mHandler.removeMessages(MSG_ANIMATE_SHOW_PINNED_TOP);
      mHandler.sendMessageAtTime(
          mHandler.obtainMessage(MSG_ANIMATE_SHOW_PINNED_TOP),
          currentAnimatingTime);
    }

    void computeHidePinnedTop() {
      final long now = SystemClock.uptimeMillis();
      final float t = (now - lastAnimationTime) / SECOND;

      animatingPosition += animatingVelocity * t;

      if (animatingPosition <= 0) {
        mPinnedTopOffset = 0;
        animating = false;
        mPinnedTopState = PINNED_TOP_STATE_HIDE;
      } else {
        mPinnedTopOffset = animatingPosition;
        lastAnimationTime = now;
        currentAnimatingTime = now + FRAME_ANIMATION_DURATION;
        mHandler.removeMessages(MSG_ANIMATE_HIDE_PINNED_TOP);
        mHandler.sendMessageAtTime(
            mHandler.obtainMessage(MSG_ANIMATE_HIDE_PINNED_TOP),
            currentAnimatingTime);
      }
      invalidate(0, 0, getMeasuredWidth(), mPinnedTopMeasuredHeight);
    }

    void animateHidePinnedTop() {
      final long now = SystemClock.uptimeMillis();

      lastAnimationTime = now;
      currentAnimatingTime = now + FRAME_ANIMATION_DURATION;
      animating = true;
      animatingPosition = mPinnedTopOffset;
      animatingVelocity = -kVelocity;
      mHandler.removeMessages(MSG_ANIMATE_HIDE_PINNED_TOP);
      mHandler.sendMessageAtTime(
          mHandler.obtainMessage(MSG_ANIMATE_HIDE_PINNED_TOP),
          currentAnimatingTime);
    }
  }

  private class Animator {
    static final int VELOCITY = 400;
    private boolean animating;
    private long lastAnimationTime;
    private long currentAnimatingTime;
    private final int kVelocity;
    private int animatingVelocity;
    private int animatingPosition;
    private int animationDistance;

    public Animator() {
      kVelocity = (int) (getResources().getDisplayMetrics().density
          * VELOCITY + 0.5);
    }

    void computePullBack() {
      final long now = SystemClock.uptimeMillis();
      final float t = (now - lastAnimationTime) / SECOND;

      animatingPosition += animatingVelocity * t;

      if (animatingPosition >= animationDistance) {
        mYOffset = 0;
        animating = false;
      } else {
        mYOffset = (int) (animationDistance
            * (1 - sInterpolator.getInterpolation(animatingPosition
            / (float) animationDistance)));
        lastAnimationTime = now;
        currentAnimatingTime = now + FRAME_ANIMATION_DURATION;
        mHandler.removeMessages(MSG_ANIMATE_PULL_BACK);
        mHandler.sendMessageAtTime(
            mHandler.obtainMessage(MSG_ANIMATE_PULL_BACK),
            currentAnimatingTime);
      }

      invalidate();
      mHeader.invalidate();
    }

    void animatePullBack() {
      final long now = SystemClock.uptimeMillis();

      lastAnimationTime = now;
      currentAnimatingTime = now + FRAME_ANIMATION_DURATION;
      animationDistance = mYOffset;
      animating = true;
      animatingPosition = 0;
      animatingVelocity = kVelocity;
      mHandler.removeMessages(MSG_ANIMATE_PULL_BACK);
      mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE_PULL_BACK),
          currentAnimatingTime);
    }

  }


  private class Header extends ViewGroup {
    public Header(final Context context) {
      super(context);
      mCoverImage = new ImageView(context);
      mCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
      mCoverImage.setImageResource(R.drawable.v1_cover_default);
      addView(mCoverImage, new LayoutParams(LayoutParams.MATCH_PARENT,
          LayoutParams.MATCH_PARENT));
      mShadow = new ImageView(context);
      mShadow.setScaleType(ImageView.ScaleType.CENTER_CROP);
      mShadow.setImageResource(R.drawable.v1_cover_shadow);
      addView(mShadow, new LayoutParams(LayoutParams.MATCH_PARENT,
          LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec,
                             final int heightMeasureSpec) {
      final int widthSize = widthMeasureSpec & ~(0x3 << 30);
      final int heightSize = heightMeasureSpec & ~(0x3 << 30);

      measureChild(mCoverImage, widthMeasureSpec,
          kCoverHeight + MeasureSpec.EXACTLY);

      measureChild(mShadow, widthMeasureSpec,
          kCoverHeight + MeasureSpec.EXACTLY);

      if (mTop != null) {
        measureChild(mTop, widthSize + MeasureSpec.AT_MOST,
            heightSize + MeasureSpec.AT_MOST);
      }

      if (mPinnedTop != null) {
        measureChild(mPinnedTop, widthMeasureSpec,
            heightSize + MeasureSpec.AT_MOST);
        mPinnedTopMeasuredHeight = mPinnedTop.getMeasuredHeight();
      }

      setMeasuredDimension(widthSize, kCoverHeight);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t,
                            final int r, final int b) {
      final int w = r - l;
      final int h = b - t;
      int btm = h;

      if (mPinnedTop != null) {
        mPinnedTop.layout(0,
            btm - mPinnedTop.getMeasuredHeight(), w, btm);
        btm -= mPinnedTop.getMeasuredHeight();
      }

      if (mTop != null) {
        mTop.layout((w - mTop.getMeasuredWidth()) / 2,
            btm - mTop.getMeasuredHeight(), (w + mTop.getMeasuredWidth()) / 2,
            btm);
      }

      mCoverImage.layout(0,
          (h - mCoverImage.getMeasuredHeight()) >> 1, w,
          (h + mCoverImage.getMeasuredHeight()) >> 1);

      mShadow.layout(0,
          (h - mShadow.getMeasuredHeight()) >> 1, w,
          (h + mShadow.getMeasuredHeight()) >> 1);
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {
      final long drawingTime = getDrawingTime();
      final int dy =
          (((kHeaderHeight - kCoverHeight) + (mYOffset >> 1)) >> 1) + 1;

      canvas.save();
      canvas.translate(0, dy);
      drawChild(canvas, mCoverImage, drawingTime);
      drawChild(canvas, mShadow, drawingTime);
      canvas.translate(0, dy);

      if (mTop != null) {
        drawChild(canvas, mTop, drawingTime);
      }

      if (mPinnedTop != null) {
        drawChild(canvas, mPinnedTop, drawingTime);
      }

      canvas.restore();
    }
  }


  private class RefreshAsyncTask extends AsyncTask<Void, Void, Void> {
    private final OnRefreshListener mListener;

    RefreshAsyncTask() {
      mListener = mOnRefreshListener;
    }

    @Override
    protected void onPreExecute() {
      mListener.onPreRefresh();
    }

    @Override
    protected Void doInBackground(final Void... params) {
      if (mListener != null) {
        mListener.onRefreshData();
      }

      return null;
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
      if (mListener != null) {
        mListener.onRefreshUI();
      }
    }
  }
}

package com.renren.mobile.x2.view;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView.OnScrollListener;
import android.widget.*;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.refresh.OnRefreshListener;

/**
 * A pull down to refresh view component with a cover
 */
public class RefresherListView extends ViewGroup {
    private static final String       TAG                      = "RefresherListView";
    private static final int          MSG_ANIMATE              = 1000;
    private static final int          FRAME_ANIMATION_DURATION = 1000 / 60;
    private static final Interpolator sInterpolator            = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;

            return t * t * t * t * t + 1.0f;
        }
    };
    private final String      kPullDownToRefresh;
    private final String      kReleaseToRefresh;
    private final String      kRefreshing;
    private View              mCover;
    private ListView          mListView;
    private TextView          mCoverText;
    private final int         kSpaceHeight;
    private final int         kCoverHeight;
    private final int         kRefreshThreshold;
    private final int         kMaxYOffset;
    private int               mLastDownY;
    private int               mYOffset;
    private int               mBackPosition;
    private AnimateHandler    mHandler;
    private Animator          mAnimator;
    private OnRefreshListener mOnRefreshListener;
    private RefreshAsyncTask  mRefreshAsyncTask;
    private View              mEmptyView;

    public RefresherListView(Context context) {
        this(context, null, 0);
    }

    public RefresherListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefresherListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHandler           = new AnimateHandler();
        mAnimator          = new Animator();
        kPullDownToRefresh = context.getString(R.string.refresher_pull_down_to_refresh);
        kReleaseToRefresh  = context.getString(R.string.refresher_release_to_refresh);
        kRefreshing        = context.getString(R.string.refresher_releasing);

        final float density = getResources().getDisplayMetrics().density;

        kSpaceHeight      = (int) (0.5f + 10 * density);
        kCoverHeight      = (int) (0.5f + 300 * density);
        kRefreshThreshold = (int) (0.5f + 150 * density);
        kMaxYOffset       = kCoverHeight - kSpaceHeight;
        mListView         = new ListView(context, attrs);
        mListView.setVerticalFadingEdgeEnabled(false);
        mListView.setBackgroundColor(0xFFFFFFFF);
        mListView.setDrawingCacheEnabled(false);
        mListView.setScrollingCacheEnabled(false);
        mCover     = View.inflate(context, R.layout.refresher, null);
        mCoverText = (TextView) mCover.findViewById(R.id.text);
        mCover.setBackgroundColor(0x33000000);
        mEmptyView = new ProgressBar(context);
        addView(mEmptyView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mListView.setEmptyView(mEmptyView);
        addView(mCover, new LayoutParams(LayoutParams.FILL_PARENT, kCoverHeight));
        addView(mListView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightSize = heightMeasureSpec & ~(0x3 << 30);

        measureChild(mListView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mCover, widthMeasureSpec, kCoverHeight + MeasureSpec.EXACTLY);
        measureChild(mEmptyView, widthMeasureSpec, (heightSize - kSpaceHeight) + MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec & ~(0x3 << 30), heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width  = r - l;
        final int height = b - t;

        mListView.layout(0, 0, width, height);
        mEmptyView.layout((width - mEmptyView.getMeasuredWidth()) / 2, (height - mEmptyView.getMeasuredHeight()) / 2,
                          (width + mEmptyView.getMeasuredWidth() / 2), (height + mEmptyView.getMeasuredHeight()) / 2);
        mCover.layout(0, (kSpaceHeight - kCoverHeight) / 2, width, (kSpaceHeight + kCoverHeight) / 2);
    }

    public ListView getListView() {
        return mListView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        final int y      = (int) ev.getY();

        switch (action) {
        case MotionEvent.ACTION_DOWN :
            mLastDownY = y;

            break;

        case MotionEvent.ACTION_MOVE :
            final View childAt = mListView.getChildAt(mListView.getFirstVisiblePosition());
            if ((childAt != null) && (childAt.getTop() == 0) && (y > mLastDownY)) {
                mCoverText.setText(kPullDownToRefresh);
                mYOffset = Math.min(y - mLastDownY, kMaxYOffset);
                invalidate();

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final int y      = (int) event.getY();

        switch (action) {
        case MotionEvent.ACTION_MOVE :
            mYOffset = Math.max(0, Math.min(y - mLastDownY, kMaxYOffset));

            if (mYOffset > kRefreshThreshold) {
                mCoverText.setText(kReleaseToRefresh);
            } else {
                mCoverText.setText(kPullDownToRefresh);
            }

            invalidate();

            break;

        case MotionEvent.ACTION_UP :
        case MotionEvent.ACTION_CANCEL :
            if (mYOffset > kRefreshThreshold) {
                if ((mRefreshAsyncTask == null) || (mRefreshAsyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
                    mBackPosition     = kRefreshThreshold;
                    mRefreshAsyncTask = new RefreshAsyncTask();
                    mRefreshAsyncTask.execute((Void[]) null);
                }
            } else {
                mBackPosition = 0;
            }

            mAnimator.animate();

            break;
        }

        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final long drawingTime = getDrawingTime();

        canvas.save();
        if (mYOffset != 0) {
            canvas.translate(0, mYOffset / 4);
            drawChild(canvas, mCover, drawingTime);
        }
        canvas.translate(0, mYOffset / 4);
        drawChild(canvas, mEmptyView, drawingTime);
        drawChild(canvas, mListView, drawingTime);
        canvas.restore();

        if (mYOffset > 0) {
            invalidate();
        }
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        mOnRefreshListener = refreshListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mListView.setOnScrollListener(listener);
    }

    private class AnimateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_ANIMATE) {
                mAnimator.compute();
            }
        }
    }


    private class Animator {
        final static int VELOCITY = 600;
        boolean          animating;
        long             lastAnimationTime;
        long             currentAnimatingTime;
        final int        kVelocity;
        int              animatingVelocity;
        int              animatingPosition;
        int              animationDistance;

        public Animator() {
            kVelocity = (int) (getResources().getDisplayMetrics().density * VELOCITY + 0.5);
        }

        void compute() {
            final long  now = SystemClock.uptimeMillis();
            final float t   = (now - lastAnimationTime) / 1000f;

            animatingPosition += animatingVelocity * t;
            Log.d(TAG, "@computePullBack animating position " + animatingPosition);

            if (animatingPosition >= animationDistance) {
                mYOffset  = mBackPosition;
                animating = false;
            } else {
                mYOffset = mBackPosition
                           + (int) (animationDistance
                                    * (1 - sInterpolator.getInterpolation(animatingPosition
                                        / (float) animationDistance)));
                lastAnimationTime    = now;
                currentAnimatingTime = now + FRAME_ANIMATION_DURATION;
                mHandler.removeMessages(MSG_ANIMATE);
                mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), currentAnimatingTime);
            }

            invalidate();
        }

        void animate() {
            final long now = SystemClock.uptimeMillis();

            lastAnimationTime    = now;
            currentAnimatingTime = now + FRAME_ANIMATION_DURATION;
            animationDistance    = mYOffset - mBackPosition;
            Log.d(TAG, "@animatePullBack animating distance " + animationDistance);
            animating         = true;
            animatingPosition = 0;
            animatingVelocity = kVelocity;
            mHandler.removeMessages(MSG_ANIMATE);
            mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), currentAnimatingTime);
        }
    }


    private class RefreshAsyncTask extends AsyncTask<Void, Void, Void> {
        final OnRefreshListener mListener;

        RefreshAsyncTask() {
            mListener = mOnRefreshListener;
        }

        @Override
        protected void onPreExecute() {
            mCoverText.setText(kRefreshing);

            if (mListener != null) {
                mListener.onPreRefresh();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mListener != null) {
                mListener.onRefreshData();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mListener != null) {
                mListener.onRefreshUI();
            }

            mBackPosition = 0;
            mAnimator.animate();
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com

package com.renren.mobile.x2.components.home;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.view.IHorizontalFlip;

/**
 * @author lu.yu
 */
public class HomeLayout extends ViewGroup
    implements IHorizontalFlip.OnLeftAnimationListener, IHorizontalFlip.OnRightAnimationListener,
    IHorizontalFlip.OnOpenAnimationListener {

  private static final String TAG = "HomeLayout";
  private final Rect mFrame = new Rect();
  private ContentLayout mContent;
  private MenuLayout mMenu;
  private FrameLayout mSide;
  private boolean mMenuVisibility;
  private boolean mSideVisibility;
  private final int kRightOffset;
  private final int kLeftOffset;
  private int mLastLeft;
  private boolean mDispatchToSide;
  private boolean mDispatchToMenu;

  public HomeLayout(Context context) {
    this(context, null, 0);
  }

  public HomeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HomeLayout(final Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    setBackgroundResource(R.drawable.v1_common_side_bg);

    LayoutParams params =
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    mMenu = new MenuLayout(context);
    mMenu.setId(R.id.menu);
    addView(mMenu, params);
    mSide = new FrameLayout(context);
    mSide.setId(R.id.side);
    addView(mSide, params);
    mContent =
        (ContentLayout) View.inflate(context, R.layout.home_content, null);
    addView(mContent, params);

    Resources resources = getResources();

    kLeftOffset =
        (int) (0.5f + resources.getDimension(R.dimen.home_content_left_offset));
    kRightOffset = (int) (0.5f
        + resources.getDimension(R.dimen.home_content_right_offset));
  }

  @Override
  public final void onLeftAnimationStart() {
    mSideVisibility = true;
    mMenuVisibility = false;
    invalidate();
  }

  @Override
  public void onLeftAnimationEnd() {
  }

  @Override
  public void onOpenAnimationStart() {
  }

  @Override
  public final void onOpenAnimationEnd() {
    mSideVisibility = false;
    mMenuVisibility = false;
    invalidate();
  }

  @Override
  public final void onRightAnimationStart() {
    mSideVisibility = false;
    mMenuVisibility = true;
    invalidate();
  }

  @Override
  public void onRightAnimationEnd() {
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final int width = r - l;
    final int height = b - t;

    mMenu.layout(0, 0, width - kRightOffset, height);
    mSide.layout(kLeftOffset, 0, width, height);
    mContent.layout(0, 0, width, height);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    measureChild(mMenu,
        (widthSize - kRightOffset) + MeasureSpec.EXACTLY, heightMeasureSpec);
    measureChild(mSide,
        (widthSize - kLeftOffset) + MeasureSpec.EXACTLY, heightMeasureSpec);
    measureChild(mContent, widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(widthSize, heightSize);
  }

  // -------------------------- OTHER METHODS --------------------------
  @Override
  protected void dispatchDraw(Canvas canvas) {
    final long drawingTime = getDrawingTime();

    if (mMenuVisibility) {
      drawChild(canvas, mMenu, drawingTime);
    }

    if (mSideVisibility) {
      drawChild(canvas, mSide, drawingTime);
    }

    drawChild(canvas, mContent, drawingTime);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (mContent.isAnimating()) {
      return true;
    }

    final int action = ev.getAction() & MotionEvent.ACTION_MASK;
    final int x = (int) ev.getX();
    final int y = (int) ev.getY();

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        switch (mContent.getState()) {
          case IHorizontalFlip.STATE_COLLAPSE_LEFT:
            mSide.getHitRect(mFrame);

            if (mFrame.contains(x, y)) {
              mDispatchToSide = true;
              mDispatchToMenu = false;
              ev.offsetLocation(-kLeftOffset, 0);

              return mSide.dispatchTouchEvent(ev);
            } else {
              mDispatchToSide = false;
              mDispatchToMenu = false;
            }

            break;

          case IHorizontalFlip.STATE_COLLAPSE_RIGHT:
            mMenu.getHitRect(mFrame);

            if (mFrame.contains(x, y)) {
              mDispatchToMenu = true;
              mDispatchToSide = false;

              return mMenu.dispatchTouchEvent(ev);
            } else {
              mDispatchToSide = false;
              mDispatchToMenu = false;
            }

            break;

          default:
            if (!mContent.isAnimating()) {
              mMenuVisibility = true;
              mSideVisibility = true;
              invalidate();
            }

            mDispatchToMenu = false;
            mDispatchToSide = false;

            break;
        }

        break;

      case MotionEvent.ACTION_MOVE:
        final int left = mContent.getLeftTranslate();

        if ((left < 0) && (mLastLeft >= 0)) {
          mSideVisibility = true;
          mMenuVisibility = false;
          invalidate();
        } else if ((left > 0) && (mLastLeft <= 0)) {
          mMenuVisibility = true;
          mSideVisibility = false;
          invalidate();
        }

        mLastLeft = left;
      default:
        if (mDispatchToMenu) {
          return mMenu.dispatchTouchEvent(ev);
        } else if (mDispatchToSide) {
          ev.offsetLocation(-kLeftOffset, 0);

          return mSide.dispatchTouchEvent(ev);
        }

        break;
    }

    return super.dispatchTouchEvent(ev);
  }

}


//~ Formatted by Jindent --- http://www.jindent.com

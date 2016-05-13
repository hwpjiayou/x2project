package com.renren.mobile.x2.components.home;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.view.HorizontalFlipLayout;

public class ContentLayout extends HorizontalFlipLayout implements View.OnClickListener {
  private ImageView mMoreButton;
  private ImageView mFriendsButton;
  private ImageView mPublisherButton;
  private ImageView mPublisherBackground;
  private final int kPublisherButtonWidth;
  private final int kPublisherButtonHeight;
  private final int kButtonWidth;
  private final int kButtonHeight;
  private final int kPublisherBackgroundHeight;
  private OnMenuButtonClickListener mOnMenuButtonClickListener;
  private OnFriendsButtonClickListener mOnFriendsButtonClickListener;
  private OnPublisherButtonClickListener mOnPublisherButtonClickListener;

  public ContentLayout(Context context, AttributeSet attrs) {
    super(context, attrs);

    mPublisherButton = new ImageView(context);
    mPublisherButton.setImageResource(R.drawable.v1_home_publicher_btn_selector);
    mPublisherButton.setOnClickListener(this);
    mPublisherBackground = new ImageView(context);
    mPublisherBackground.setImageResource(R.drawable.v1_home_publisher_background);
    mPublisherBackground.setScaleType(ImageView.ScaleType.FIT_XY);

    mPublisherBackground.setClickable(true);
    mMoreButton = new ImageView(context);
    mMoreButton.setImageResource(R.drawable.v1_home_more_btn_selector);
    mMoreButton.setOnClickListener(this);
    mFriendsButton = new ImageView(context);
    mFriendsButton.setImageResource(R.drawable.v1_home_friends_btn_selector);
    mFriendsButton.setOnClickListener(this);

    FrameLayout.LayoutParams lp =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    addView(mPublisherBackground, lp);
    addView(mMoreButton, lp);
    addView(mFriendsButton, lp);
    addView(mPublisherButton, lp);

    Resources res = getResources();

    kPublisherButtonWidth =
        (int) (0.5f + res.getDimension(R.dimen.home_publisher_button_width));
    kPublisherButtonHeight =
        (int) (0.5f + res.getDimension(R.dimen.home_publisher_button_height));
    kButtonWidth = (int) (0.5f + res.getDimension(R.dimen.home_button_width));
    kButtonHeight = (int) (0.5f + res.getDimension(R.dimen.home_button_height));
    kPublisherBackgroundHeight = (int) (0.5f
        + res.getDimension(R.dimen.home_publisher_background_height));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    measureChild(mMoreButton,
        kButtonWidth + MeasureSpec.EXACTLY,
        kButtonHeight + MeasureSpec.EXACTLY);
    measureChild(mFriendsButton,
        kButtonWidth + MeasureSpec.EXACTLY,
        kButtonHeight + MeasureSpec.EXACTLY);
    measureChild(mPublisherButton, kPublisherButtonWidth + MeasureSpec.EXACTLY,
        kPublisherButtonHeight + MeasureSpec.EXACTLY);
    measureChild(mPublisherBackground, widthMeasureSpec,
        kPublisherBackgroundHeight + MeasureSpec.EXACTLY);
  }

  @Override
  protected final void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    final int width = r - l;
    final int height = b - t;

    mMoreButton.layout(0, height - mMoreButton.getMeasuredHeight(),
        mMoreButton.getMeasuredWidth(), height);
    mFriendsButton.layout(width - mFriendsButton.getMeasuredWidth(),
        height - mFriendsButton.getMeasuredHeight(),
        width, height);
    mPublisherButton.layout((width - mPublisherButton.getMeasuredWidth()) / 2,
        height - mPublisherButton.getMeasuredHeight(),
        (width + mPublisherButton.getMeasuredWidth()) / 2, height);
    mPublisherBackground.layout(0, height - kPublisherBackgroundHeight,
        width, height);
  }

  @Override
  public void onClick(final View v) {
    if (v.equals(mPublisherButton)) {
      if (mOnPublisherButtonClickListener != null) {
        final OnPublisherButtonClickListener listener =
            mOnPublisherButtonClickListener;

        listener.onPublisherClick(v);
      }
    } else if (v.equals(mMoreButton)) {
      animateRight();

      if (mOnMenuButtonClickListener != null) {
        final OnMenuButtonClickListener listener = mOnMenuButtonClickListener;

        listener.onMenuClick(v);
      }
    } else if (v.equals(mFriendsButton)) {
      animateLeft();

      if (mOnFriendsButtonClickListener != null) {
        final OnFriendsButtonClickListener listener =
            mOnFriendsButtonClickListener;

        listener.onFriendsClick(v);
      }
    }
  }

  public void setOnMenuButtonClickListener(OnMenuButtonClickListener listener) {
    mOnMenuButtonClickListener = listener;
  }

  public void setOnPublisherButtonClickListener(OnPublisherButtonClickListener listener) {
    mOnPublisherButtonClickListener = listener;
  }

  public void setOnFriendsButtonClickListener(OnFriendsButtonClickListener listener) {
    mOnFriendsButtonClickListener = listener;
  }

  public interface OnFriendsButtonClickListener {
    /**
     * @param view the show friends button.
     */
    void onFriendsClick(View view);
  }


  public interface OnMenuButtonClickListener {
    void onMenuClick(View view);
  }


  public interface OnPublisherButtonClickListener {
    void onPublisherClick(View view);
  }
}


//~ Formatted by Jindent --- http://www.jindent.com

package com.renren.mobile.x2.view;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.renren.mobile.x2.R;

/**
 * 标题栏控件
 */
public class TitleBar extends ViewGroup implements ITitleBar {
  private static final String TAG = "TitleBar";
  private final int kButtonPadding;
  private int mMode;
  private boolean mShow;
  private TextView mTitle;
  private ImageView mLeft;
  private ImageView mRight;
  private ImageView mIcon;
  private boolean mLeftVisibility;
  private boolean mRightVisibility;

  public TitleBar(Context context) {
    this(context, null, 0);
  }

  public TitleBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TitleBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    kButtonPadding = (int) (0.5f
        + getResources().getDimension(R.dimen.title_bar_button_padding));
    setBackgroundResource(R.drawable.v1_titlebar_bg);
    mMode = ITitleBar.MODE_TITLE;
    mShow = true;
    mTitle = new TextView(context);
    mTitle.setTextAppearance(context, R.style.T11);
    mLeft = new ImageView(context);
    mRight = new ImageView(context);
    mIcon = new ImageView(context);

    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);

    addView(mTitle, lp);
    addView(mLeft, lp);
    addView(mRight, lp);
    addView(mIcon, lp);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (mShow) {
      final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
      final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

      switch (mMode) {
        case MODE_ICON:
          measureChild(mIcon,
              widthSize + MeasureSpec.AT_MOST,
              heightSize + MeasureSpec.AT_MOST);

          break;

        case MODE_TITLE:
          measureChild(mTitle,
              widthSize + MeasureSpec.AT_MOST,
              heightSize + MeasureSpec.AT_MOST);

          break;
      }

      if (mLeftVisibility) {
        measureChild(mLeft,
            widthSize + MeasureSpec.AT_MOST, heightSize + MeasureSpec.AT_MOST);
      }

      if (mRightVisibility) {
        measureChild(mRight,
            widthSize + MeasureSpec.AT_MOST, heightSize + MeasureSpec.AT_MOST);
      }

      measureChild(mTitle, widthMeasureSpec, heightMeasureSpec);
      setMeasuredDimension(widthSize, heightSize);
    } else {
      setMeasuredDimension(0, 0);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (mShow) {
      final int width = r - l;
      final int height = b - t;

      if (mLeftVisibility) {
        mLeft.layout(kButtonPadding, (height - mLeft.getMeasuredHeight()) / 2,
            kButtonPadding + mLeft.getMeasuredWidth(),
            (height + mLeft.getMeasuredHeight()) / 2);
      }

      if (mRightVisibility) {
        mRight.layout(width - mRight.getMeasuredWidth() - kButtonPadding,
            (height - mRight.getMeasuredHeight()) / 2, width - kButtonPadding,
            (height + mRight.getMeasuredHeight()) / 2);
      }

      switch (mMode) {
        case MODE_ICON:
          mIcon.layout(
              (width - mIcon.getMeasuredWidth()) / 2,
              (height - mIcon.getMeasuredHeight()) / 2,
              (width + mIcon.getMeasuredWidth()) / 2,
              (height + mIcon.getMeasuredHeight()) / 2);

          break;

        case MODE_TITLE:
          mTitle.layout(
              (width - mTitle.getMeasuredWidth()) / 2,
              (height - mTitle.getMeasuredHeight()) / 2,
              (width + mTitle.getMeasuredWidth()) / 2,
              (height + mTitle.getMeasuredHeight()) / 2);

          break;
      }
    }
  }

  @Override
  public int getMode() {
    return mMode;
  }

  @Override
  public void setMode(int mode) {
    mMode = mode;
    requestLayout();
    invalidate();
  }

  @Override
  public void setTitle(String title) {
    mMode = MODE_TITLE;
    mTitle.setText(title);
    requestLayout();
    invalidate();
  }

  @Override
  public void setTitle(CharSequence title) {
    mMode = MODE_TITLE;
    mTitle.setText(title);
    requestLayout();
    invalidate();
  }

  @Override
  public void setIcon(Drawable icon, OnClickListener listener) {
    mMode = MODE_ICON;
    mIcon.setImageDrawable(icon);
    mIcon.setOnClickListener(listener);
    requestLayout();
    invalidate();
  }

  @Override
  public void setICon(int id, OnClickListener listener) {
    mMode = MODE_ICON;
    mIcon.setImageResource(id);
    mIcon.setOnClickListener(listener);
    requestLayout();
    invalidate();
  }

  @Override
  public void show() {
    mShow = true;
    requestLayout();
    invalidate();
  }

  @Override
  public void hide() {
    mShow = false;
    requestLayout();
    invalidate();
  }

  @Override
  public void setLeftAction(Drawable drawable, OnClickListener listener) {
    mLeftVisibility = true;
    mLeft.setImageDrawable(drawable);
    mLeft.setOnClickListener(listener);
    requestLayout();
    invalidate();
  }

  @Override
  public void setLeftAction(int id, OnClickListener listener) {
    setLeftAction(getContext().getResources().getDrawable(id), listener);
  }

  @Override
  public void setRightAction(Drawable drawable, OnClickListener listener) {
    mRightVisibility = true;
    mRight.setImageDrawable(drawable);
    mRight.setOnClickListener(listener);
    requestLayout();
    invalidate();
  }

  @Override
  public void setRightAction(int id, OnClickListener listener) {
    setRightAction(getContext().getResources().getDrawable(id), listener);
  }

  @Override
  public void showLeft() {
    mLeftVisibility = true;
    requestLayout();
    invalidate();
  }

  @Override
  public void showRight() {
    mRightVisibility = true;
    requestLayout();
    invalidate();
  }

  @Override
  public void hideLeft() {
    mLeftVisibility = false;
    requestLayout();
    invalidate();
  }

  @Override
  public void hideRight() {
    mRightVisibility = false;
    requestLayout();
    invalidate();
  }

  @Override
  public void setLeftEnable(boolean enable) {
    mLeft.setEnabled(enable);
  }

  @Override
  public void setRightEnable(boolean enable) {
    mRight.setEnabled(enable);
  }
}


//~ Formatted by Jindent --- http://www.jindent.com

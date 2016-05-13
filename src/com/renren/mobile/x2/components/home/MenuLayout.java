package com.renren.mobile.x2.components.home;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.renren.mobile.x2.R;

/**
 *
 */
public class MenuLayout extends ViewGroup implements View.OnClickListener {
  private static final String TAG = "MenuLayout";
  private static final int ROW_SIZE = 3;
  private MenuButton[] mMenuButtons;
  private final int kButtonVerticalPadding;
  private final int kButtonHorizontalPadding;
  private final int kButtonWidth;
  private final int kButtonHeight;
  private final int kDivisionPosition;
  private final int kMenuTopPadding;
  private ImageView mLogo;
  private final Drawable kSeparator;
  private final int kSeparatorHeight;

  private OnMenuItemClickListener mOnMenuItemClickListener;

  public MenuLayout(Context context) {
    this(context, null, 0);
  }

  public MenuLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MenuLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT);

    mLogo = new ImageView(context);
    mLogo.setImageResource(R.drawable.v1_home_menu_logo);
    addView(mLogo, lp);

    mMenuButtons = new MenuButton[Config.TABS.length];

    for (int i = 0, length = mMenuButtons.length; i < length; i++) {
      mMenuButtons[i] = new MenuButton(context);
      mMenuButtons[i].setOnClickListener(this);
      addView(mMenuButtons[i], lp);
    }

    Resources r = getResources();
    kButtonWidth = (int) (0.5f
        + r.getDimension(R.dimen.home_menu_button_width));
    kButtonHeight = (int) (0.5f
        + r.getDimension(R.dimen.home_menu_button_height));
    kButtonVerticalPadding = (int) (0.5f
        + r.getDimension(R.dimen.home_menu_button_vertical_padding));
    kButtonHorizontalPadding = (int) (0.5f
        + r.getDimension(R.dimen.home_menu_button_horizontal_padding));
    kDivisionPosition = (int) (0.5f
        + r.getDimension(R.dimen.home_menu_division));
    kMenuTopPadding = (int) (0.5f
        + r.getDimension(R.dimen.home_menu_top_padding));
    kSeparatorHeight = (int) (0.5f
        + r.getDimension(R.dimen.home_menu_separator_height));
    kSeparator = r.getDrawable(R.drawable.v1_home_menu_separator);
  }

  public void setOnMenuItemClickListener(final OnMenuItemClickListener listener) {
    mOnMenuItemClickListener = listener;
  }

  @Override
  public final void onClick(final View v) {
    if (mOnMenuItemClickListener != null) {
      OnMenuItemClickListener listener = mOnMenuItemClickListener;

      for (int i = 0, length = mMenuButtons.length; i < length; i++) {
        if (v.equals(mMenuButtons[i])) {
          listener.onMenuClick(i, mMenuButtons[i]);
        }
      }
    }
  }

  @Override
  protected final void onLayout(final boolean changed, final int l, final int t,
                                final int r, final int b) {
    final int w = r - l;
    final int h = b - t;
    final int rowWidth =
        ROW_SIZE * kButtonWidth + (ROW_SIZE - 1) * kButtonHorizontalPadding;

    int left = (w - rowWidth) / 2;
    int top = kDivisionPosition + kMenuTopPadding;

    mLogo.layout((w - mLogo.getMeasuredWidth()) / 2,
        (kDivisionPosition - mLogo.getMeasuredHeight()) / 2,
        (w + mLogo.getMeasuredWidth()) / 2,
        (kDivisionPosition + mLogo.getMeasuredHeight()) / 2);

    kSeparator.setBounds(0, kDivisionPosition, w,
        kDivisionPosition + kSeparatorHeight);


    for (int i = 0; i < 1 + mMenuButtons.length / ROW_SIZE; i++) {
      for (int j = 0; j < ROW_SIZE; j++) {
        int index = i * ROW_SIZE + j;
        if (index >= mMenuButtons.length) {
          return;
        }
        mMenuButtons[index].layout(left, top, left + kButtonWidth,
            top + kButtonHeight);
        left = left + kButtonWidth + kButtonHorizontalPadding;
      }
      left = (w - rowWidth) / 2;
      top = top + kButtonHeight + kButtonVerticalPadding;
    }
  }

  @Override
  protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    for (MenuButton button : mMenuButtons) {
      measureChild(button,
          kButtonWidth + MeasureSpec.EXACTLY,
          kButtonHeight + MeasureSpec.EXACTLY);
    }

    measureChild(mLogo, widthSize + MeasureSpec.AT_MOST,
        heightSize + MeasureSpec.AT_MOST);

    setMeasuredDimension(widthSize, heightSize);
  }

  public void setIconDrawable(int position, Drawable icon) {
    mMenuButtons[position].setIcon(icon);
  }

  public void setText(int position, String text) {
    mMenuButtons[position].setText(text);
  }

  public void setBubbleCount(int position, int count) {
    mMenuButtons[position].setBubbleCount(count);
  }

  public interface OnMenuItemClickListener {
    void onMenuClick(int position, View v);
  }

  @Override
  protected final void dispatchDraw(final Canvas canvas) {
    super.dispatchDraw(canvas);
    kSeparator.draw(canvas);
  }
}


//~ Formatted by Jindent --- http://www.jindent.com

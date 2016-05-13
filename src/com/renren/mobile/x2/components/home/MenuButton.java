package com.renren.mobile.x2.components.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.renren.mobile.x2.R;

public class MenuButton extends ViewGroup {
  private final int kBubblePadding;
  private TextView mText;
  private ImageView mIcon;
  private TextView mBubble;

  public MenuButton(final Context context) {
    this(context, null);
  }

  public MenuButton(final Context context, final AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MenuButton(final Context context, final AttributeSet attrs,
                    final int defStyle) {
    super(context, attrs);
    mText = new TextView(context, attrs);
    mText.setGravity(Gravity.CENTER);
    mText.setTextAppearance(context, R.style.T19);
    mText.setShadowLayer(2, 0, 2, 0xFFFFFFFF);
    mIcon = new ImageView(context, attrs);
    mBubble = new TextView(context, attrs);
    addView(mIcon);
    addView(mBubble);
    addView(mText);

    final Resources r = getResources();
    kBubblePadding = (int) (0.5f
        + r.getDimension(R.dimen.home_menu_button_bubble_padding));
  }

  @Override
  protected final void onMeasure(final int widthMeasureSpec,
                                 final int heightMeasureSpec) {
    final int w = widthMeasureSpec & ~(0x3 << 30);
    final int h = heightMeasureSpec & ~(0x3 << 30);

    measureChild(mIcon, w + MeasureSpec.AT_MOST, h + MeasureSpec.AT_MOST);
    measureChild(mText, w + MeasureSpec.AT_MOST, h + MeasureSpec.AT_MOST);
    measureChild(mBubble, w + MeasureSpec.AT_MOST, h + MeasureSpec.AT_MOST);

    setMeasuredDimension(w, h);
  }

  @Override
  protected final void onLayout(final boolean changed, final int l, final int t,
                                final int r, final int b) {
    final int w = r - l;
    final int h = b - t;

    mIcon.layout(0, 0, w, mIcon.getMeasuredHeight());
    mText.layout((w - mText.getMeasuredWidth()) / 2,
        h - mText.getMeasuredHeight(), (w + mText.getMeasuredWidth()) / 2, h);
    mBubble.layout(r - kBubblePadding - mBubble.getMeasuredWidth(), 0,
        r - kBubblePadding, mBubble.getMeasuredHeight());
  }

  public final void setText(final String text) {
    mText.setText(text);
  }

  public final void setBubbleCount(final int count) {
    mBubble.setText(String.valueOf(count));
  }

  public final void setIcon(final Drawable drawable) {
    mIcon.setImageDrawable(drawable);
  }
}

package com.renren.mobile.x2.components.home.feed;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.renren.mobile.x2.R;

/**
 */
public class FeedHeaderLayout extends ViewGroup {
  private static final String TAG = "FeedHeaderLayout";

  private static final int PORTRAIT_WIDTH = 54;
  private static final int PORTRAIT_HEIGHT = 54;
  private static final int ROUND_MASK_WIDTH = 60;
  private static final int ROUND_MASK_HEIGHT = 64;
  private static final int PADDING_BOTTOM = 28;
  private static final int TEXT_PADDING_LEFT = 10;

  private final int kPortraitWidth;
  private final int kPortraitHeight;
  private final int kRoundMaskWidth;
  private final int kRoundMaskHeight;
  private final int kPaddingBottom;
  private final int kTextPaddingLeft;

  private ImageView mRoundMask;
  private ImageView mPortrait;
  private TextView mText;

  public FeedHeaderLayout(Context context) {
    this(context, null);
  }

  public FeedHeaderLayout(Context context, AttributeSet attrs) {
    super(context, attrs);

    final Resources r = getResources();
    final float density = r.getDisplayMetrics().density;

    kPortraitWidth = (int) (0.5f + density * PORTRAIT_WIDTH);
    kPortraitHeight = (int) (0.5f + density * PORTRAIT_HEIGHT);
    kPaddingBottom = (int) (0.5f + density * PADDING_BOTTOM);
    kRoundMaskWidth = (int) (0.5f + density * ROUND_MASK_WIDTH);
    kRoundMaskHeight = (int) (0.5f + density * ROUND_MASK_HEIGHT);
    kTextPaddingLeft = (int) (0.5f + density * TEXT_PADDING_LEFT);

    mRoundMask = new ImageView(context);
    mRoundMask.setScaleType(ImageView.ScaleType.CENTER_CROP);
    mRoundMask.setImageResource(R.drawable.cover_head_bg);
    mPortrait = new ImageView(context);
    mPortrait.setId(R.id.cover_head_img);
    mText = new TextView(context);
    mText.setId(R.id.cover_head_username);
    mText.setBackgroundResource(R.drawable.cover_head_username_bg);
    mText.setTextAppearance(context, R.style.T3);
    mText.setGravity(Gravity.CENTER);
    mText.setPadding(kTextPaddingLeft, 0, 0, 0);

    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT);
    addView(mText, lp);
    addView(mPortrait, lp);
    addView(mRoundMask, lp);
  }

  @Override
  protected void onMeasure(final int widthMeasureSpec,
                           final int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    final int heightSize = heightMeasureSpec & ~(0x3 << 30);


    measureChild(mText, widthSize + MeasureSpec.AT_MOST,
        heightSize + MeasureSpec.AT_MOST);

    measureChild(mText,
        mText.getMeasuredWidth() + 2 * kTextPaddingLeft + MeasureSpec.EXACTLY,
        heightSize + MeasureSpec.AT_MOST);

    measureChild(mPortrait, kPortraitWidth + MeasureSpec.EXACTLY,
        kPortraitHeight + MeasureSpec.EXACTLY);

    measureChild(mRoundMask, kRoundMaskWidth + MeasureSpec.EXACTLY,
        kRoundMaskHeight + MeasureSpec.EXACTLY);

    setMeasuredDimension(
        mPortrait.getMeasuredWidth() + mText.getMeasuredWidth(),
        kRoundMaskHeight + kPaddingBottom);
  }

  @Override
  protected final void onLayout(boolean changed, int l, int t, int r, int b) {
    final int w = r - l;
    final int h = b - t;


    mText.layout(mPortrait.getMeasuredWidth() - 10,
        (h - kPaddingBottom - mText.getMeasuredHeight()) / 2,
        mPortrait.getMeasuredWidth() - 10 + mText.getMeasuredWidth(),
        (h - kPaddingBottom + mText.getMeasuredHeight()) / 2);


    mRoundMask.layout(0, 0, mRoundMask.getMeasuredWidth(),
        mRoundMask.getMeasuredHeight());

    mPortrait.layout(
        (mRoundMask.getMeasuredWidth() - mPortrait.getMeasuredWidth()) / 2,
        (mRoundMask.getMeasuredHeight() - mPortrait.getMeasuredHeight()) / 2,
        (mRoundMask.getMeasuredWidth() + mPortrait.getMeasuredWidth()) / 2,
        (mRoundMask.getMeasuredHeight() + mPortrait.getMeasuredHeight()) / 2);
  }
}

package com.renren.mobile.x2.base.box;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Consider the Box as a lightweight version of android's View
 */
public class Box {

  /**
   * @see android.view.View#VISIBLE
   */
  public static final int VISIBLE = View.VISIBLE;

  /**
   * @see android.view.View#GONE
   */
  public static final int GONE = View.GONE;

  /**
   * @see android.view.View#INVISIBLE
   */
  public static final int INVISIBLE = View.INVISIBLE;

  /**
   *
   */
  final Rect mRect = new Rect();

  /**
   *
   */
  Drawable mBackground;

  /**
   *
   */
  Drawable mForeground;

  /**
   *
   */
  CharSequence mText;

  /**
   *
   */
  Drawable mImage;

  /**
   *
   */
  boolean isDirty;

  /**
   */
  int mVisibility;

  /**
   */
  int mDebug;

  /**
   * The box.
   */
  public Box() {

  }

  /**
   * @param background the background drawable.
   */
  public Box(final Drawable background) {
    mBackground = background;
  }

  /**
   * @param background the background drawable.
   * @param foreground the foreground drawable.
   */
  public Box(final Drawable background, final Drawable foreground) {
    this(background);
    mForeground = foreground;
  }

  /**
   * @param background the background drawable.
   * @param foreground the foreground drawable.
   * @param text       the text.
   */
  public Box(final Drawable background, final Drawable foreground,
             final String text) {
    this(background, foreground);
    mText = text;
  }

  /**
   * @param background the background drawable.
   * @param foreground the foreground drawable.
   * @param image      the image.
   */
  public Box(final Drawable background, final Drawable foreground,
             final Drawable image) {
    this(background, foreground);
    mImage = image;
  }

  /**
   * @param drawable the drawable to be set.
   */
  public void setBackground(final Drawable drawable) {
    mBackground = drawable;
    mBackground.setBounds(mRect);
    isDirty = true;
  }

  /**
   * @param color an integer represents the color.
   */
  public void setBackgroundColor(final int color) {
    mBackground = new ColorDrawable(color);
    mBackground.setBounds(mRect);
    isDirty = true;
  }

  /**
   * @param drawable the drawable to be set to foreground.
   */
  public void setForeground(final Drawable drawable) {
    mForeground = drawable;
    mForeground.setBounds(mRect);
    isDirty = true;
  }

  /**
   * @param color an integer represents the color of the foreground.
   */
  public void setForegroundColor(final int color) {
    mForeground = new ColorDrawable(color);
    mForeground.setBounds(mRect);
    isDirty = true;
  }

  /**
   * @return the left of the box relative to its parent.
   */
  public int getLeft() {
    return mRect.left;
  }

  /**
   * @return the top of the box relative to its parent.
   */
  public int getTop() {
    return mRect.top;
  }

  /**
   * @return the right of the box relative to its parent.
   */
  public int getRight() {
    return mRect.right;
  }

  /**
   * @return the bottom of the box relative to its parent.
   */
  public int getBottom() {
    return mRect.bottom;
  }
}


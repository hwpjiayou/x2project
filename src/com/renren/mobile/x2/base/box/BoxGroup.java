package com.renren.mobile.x2.base.box;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 */
public class BoxGroup extends Box {
  /**
   */
  private static final String TAG = "BoxGroup";

  /**
   */
  private static final int CAPACITY = 24;

  /**
   */
  private ArrayList<Box> mChildren;

  /**
   */
  public BoxGroup() {
  }

  /**
   * @param background the background.
   */
  public BoxGroup(final Drawable background) {
    super(background);
    init();
  }

  /**
   * @param background the background.
   * @param foreground the foreground.
   */
  public BoxGroup(final Drawable background, final Drawable foreground) {
    super(background, foreground);
    init();
  }

  /**
   * @param background the background.
   * @param foreground the foreground.
   * @param text       the text.
   */
  public BoxGroup(final Drawable background, final Drawable foreground,
                  final String text) {
    super(background, foreground, text);
    init();
  }

  /**
   * @param background the background.
   * @param foreground the foreground.
   * @param image      the image.
   */
  public BoxGroup(final Drawable background, final Drawable foreground,
                  final Drawable image) {
    super(background, foreground, image);
    init();
  }

  /**
   */
  private void init() {
    mChildren = new ArrayList<Box>(CAPACITY);
  }

  /**
   * @param box the box to be added.
   */
  public final void addBox(final Box box) {
    if (mChildren.size() == CAPACITY) {
      throw new Error();
    }
    mChildren.add(box);
  }

  /**
   * @param index the index in the array.
   * @param box the box to be added.
   */
  public final void addBox(final int index, final Box box) {
    if (mChildren.size() == CAPACITY) {
      throw new Error();
    }
    mChildren.add(index, box);
  }

  /**
   * @param box the box to be removed.
   */
  public final void removeBox(final Box box) {
    mChildren.remove(box);
  }

  /**
   * @param index the index of the box to be removed.
   */
  public final void removeBox(final int index) {
    mChildren.remove(index);
  }
}

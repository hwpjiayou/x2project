package com.renren.mobile.x2.base.box;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Wrap box system to Android view.
 */
public final class BoxView extends SurfaceView {
  /**
   */
  SurfaceHolder mHolder;

  /**
   * @param context the context
   */
  public BoxView(final Context context) {
    this(context, null);
  }

  /**
   * @param context the context
   * @param attrs   the attribute set.
   */
  public BoxView(final Context context, final AttributeSet attrs) {
    super(context, attrs);

    mHolder = getHolder();
  }

  /**
   * @param boxes the box.
   */
  public void setContent(final BoxGroup boxes) {
    getHolder().addCallback(new BoxRenderer(boxes));
  }
}

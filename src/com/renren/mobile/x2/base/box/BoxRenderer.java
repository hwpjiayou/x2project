package com.renren.mobile.x2.base.box;

import android.view.SurfaceHolder;

/**
 * Render to Surface
 */
public class BoxRenderer implements SurfaceHolder.Callback2 {
  /**
   */
  private BoxGroup mBoxGroup;

  /**
   * @param boxGroup the boxes
   */
  BoxRenderer(final BoxGroup boxGroup) {
    mBoxGroup = boxGroup;
  }

  @Override
  public void surfaceRedrawNeeded(final SurfaceHolder holder) {
  }

  @Override
  public void surfaceCreated(final SurfaceHolder holder) {
  }

  @Override
  public void surfaceChanged(final SurfaceHolder holder, final int format,
                             final int width, final int height) {
  }

  @Override
  public void surfaceDestroyed(final SurfaceHolder holder) {
  }
}

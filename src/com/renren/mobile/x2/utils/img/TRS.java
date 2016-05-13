package com.renren.mobile.x2.utils.img;

import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * at 下午4:37, 12-11-6 <br />
 * TRS = Translate & Rotate & Scale
 *
 * @author afpro
 */
public class TRS {
    /**
     * {angle, dx, dy}, degree
     */
    public final float[] rotate = {0, 0, 0};

    /**
     * {x, y}
     */
    public final float[] translate = {0, 0};

    /**
     * {x, y}
     */
    public final float[] scale = {1, 1};

    public void transformMatrix(Matrix matrix) {
        if (matrix == null) {
            return;
        }

        matrix.postTranslate(translate[0], translate[1]);
        matrix.postRotate(rotate[0], rotate[1], rotate[2]);
        matrix.postScale(scale[0], scale[1]);
    }

    public void transformCanvas(Canvas canvas) {
        if (canvas == null) {
            return;
        }

        canvas.translate(translate[0], translate[1]);
        canvas.rotate(rotate[0], rotate[1], rotate[2]);
        canvas.scale(scale[0], scale[1]);
    }
}

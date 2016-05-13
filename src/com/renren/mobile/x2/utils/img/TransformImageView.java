package com.renren.mobile.x2.utils.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * at 下午4:31, 12-11-6
 *
 * @author afpro
 */
public class TransformImageView extends ImageView {
    public final Matrix matrix = new Matrix();

    public TransformImageView(Context context) {
        super(context);
    }

    public TransformImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransformImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        if (drawable instanceof BitmapDrawable) {
            final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
        }

        int saveCount = canvas.getSaveCount();
        canvas.save();
        try {
            canvas.concat(matrix);
            super.onDraw(canvas);
        } finally {
            canvas.restoreToCount(saveCount);
        }
    }
}

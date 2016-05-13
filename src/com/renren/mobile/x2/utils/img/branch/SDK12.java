package com.renren.mobile.x2.utils.img.branch;

import android.graphics.Bitmap;

/**
 * at 下午5:09, 12-10-31
 *
 * @author afpro
 */
public class SDK12 {
    public static int getByteCount(Bitmap bitmap) {
        return bitmap == null ? 0 : bitmap.getByteCount();
    }
}

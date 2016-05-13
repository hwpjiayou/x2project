package com.renren.mobile.x2.utils.img.branch;

import android.graphics.Bitmap;

/**
 * at 下午5:09, 12-10-31
 *
 * @author afpro
 */
public class SDK7 {
    public static int getByteCount(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }

        int pixels = bitmap.getWidth() * bitmap.getHeight();
        int bytesPerPixel = 1;
        switch (bitmap.getConfig()) {
            case ARGB_4444:
            case RGB_565:
                bytesPerPixel = 2;
                break;
            case ARGB_8888:
                bytesPerPixel = 4;
                break;
        }

        return pixels * bytesPerPixel;
    }
}

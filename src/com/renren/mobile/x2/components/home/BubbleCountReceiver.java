package com.renren.mobile.x2.components.home;

//~--- non-JDK imports --------------------------------------------------------

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.renren.mobile.x2.RenrenChatApplication;

public class BubbleCountReceiver extends BroadcastReceiver {
    public static final String ACTION        = RenrenChatApplication.PACKAGE_NAME + ".action.BUBBLE_COUNT";
    public static final String KEY_INDEX     = "index";
    public static final String KEY_COUNT     = "count";
    public static final String KEY_CLEAR     = "clear";
    public static final String KEY_NOTIFY    = "notify";
    private Callback           mCallback;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mCallback != null) {
            final Callback callback = mCallback;

            if (intent.getAction().equals(ACTION)) {
                if (intent.getBooleanExtra(KEY_CLEAR, false)) {
                    callback.onClearAll();
                } else {
                    callback.onUpdateCount(intent.getIntExtra(KEY_INDEX, -1), intent.getIntExtra(KEY_COUNT, 0));
                }

                callback.onNotify(intent.getBooleanExtra(KEY_NOTIFY, false));
            }
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public static interface Callback {
        void onClearAll();

        void onUpdateCount(int index, int count);

        void onNotify(boolean show);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com

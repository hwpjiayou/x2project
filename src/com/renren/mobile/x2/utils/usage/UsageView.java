package com.renren.mobile.x2.utils.usage;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public class UsageView extends ViewGroup {
    private static final String TAG = "UsageView";

    private static final int UPDATE_DURATION = 2000;

    private TextView mCpu;
    private TextView mMemory;

    private Handler mHandler;

    private UpdateListener mUpdateListener;

    public UsageView(Context context) {
        this(context, null, 0);
    }

    public UsageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UsageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mCpu = new TextView(context);
        mMemory = new TextView(context);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == UPDATE_DURATION) {
                    if (mUpdateListener != null) {
                        mUpdateListener.onUpdate();
                    }
                }
            }
        };
    }

    public void setOnUpdateListener(UpdateListener listener) {
        mUpdateListener = listener;
    }

    public interface UpdateListener {
        void onUpdate();
    }

    public void setCpuText(String cpu) {
        mCpu.setText(cpu);
    }

    public void setMemory(String memory) {
        mMemory.setText(memory);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }
}

package com.renren.mobile.x2.components.chat.view;


import com.renren.mobile.x2.emotion.Emotion;
import com.renren.mobile.x2.emotion.EmotionPool;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class GifImageView extends ImageView {
	private final static int MESSAGE_RUN = 0x1000;
	private final static int MESSAGE_LOAD = 0x1001;
	private boolean isRun = false;
	private String emotioncode;
	private Bitmap mBitmap;
	private long nextAnimatiingtime = 0;
	private long nullintervaltime = 50;
	private long intervaltime = 200;
	private int index =0;
	private Emotion emotion;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_RUN:
				nextAnimaiton();
				setImageBitmap(mBitmap);
				break;
			case MESSAGE_LOAD:
				nullBitmapMessage();
				break;
			default:
				break;
			}
		};
	};

	public GifImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
	}

	public GifImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GifImageView(Context context) {
		this(context, null, 0);
	}

	private void nextAnimaiton() {
		// Log.d("emotion", "emotioncode " + emotioncode);
		if (emotion == null) {
			emotion = EmotionPool.getInstance().getCoolEmotionbycode(
					emotioncode);
		}
		final long now = SystemClock.uptimeMillis();
		if (emotion == null) {
			nextAnimatiingtime = now + nullintervaltime;
			Log.d("emotion", "emotion is null");
			handler.sendMessageAtTime(handler.obtainMessage(MESSAGE_LOAD), nextAnimatiingtime);
		} else {
			nextAnimatiingtime = now + intervaltime;
			index++;
			index %= emotion.mFrameSize;
			mBitmap = emotion.get(index);
			if (!isRun) {
				Log.d("emotion", "returned ");
				emotion = null;
				mBitmap = null;
				return;
			}
			handler.sendMessageAtTime(handler.obtainMessage(MESSAGE_RUN),
					nextAnimatiingtime);
		}
	}
	private void nullBitmapMessage(){
		handler.sendMessage(handler.obtainMessage(MESSAGE_RUN));
	}
	public void start(String code) {
		this.emotioncode = code;
		this.isRun = true;
		final long now = SystemClock.uptimeMillis();
		nextAnimatiingtime = now + intervaltime;
		handler.sendMessage(handler.obtainMessage(MESSAGE_RUN));
	}
	public void stop(){
		emotion = null;
		mBitmap = null;
		isRun = false;
	}

}

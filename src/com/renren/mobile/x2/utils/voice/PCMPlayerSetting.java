package com.renren.mobile.x2.utils.voice;

import android.media.AudioManager;

/**
 * @author dingwei.chen
 * @说明 PCM属性配置类
 * */
public final class PCMPlayerSetting {

	public static int CHANNEL_NUMMBER = 1;//单声道
	public static int SIMPLE_RATE = 8000;
	public static int STREAM_TYPE= AudioManager.STREAM_MUSIC;
	
	public static void switchStreamType(int mode) {
		STREAM_TYPE = mode;
	}
}

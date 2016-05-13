package com.renren.mobile.x2.utils.voice;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
/**
 * @author dingwei.chen
 * @说明 语音参数
 * */
final class AudioParams {
	
	public static final int SAMPLE_RATE = 8000;
	public static final int FRAME_SIZE = 160;
	public static final int ENCODING_PCM_NUM = 160*10;
	public static final int FRAME_SIZE_IN_SHORTS = 160;
	public static final int ENCODING_PCM_NUM_BITS = AudioFormat.ENCODING_PCM_16BIT;	
	
	
	public static int getRecorderBufferSize(int sample){
		return Math.max(
				sample, 
				ceil(AudioRecord.getMinBufferSize(
						sample, 
						AudioFormat.CHANNEL_IN_MONO, 
						ENCODING_PCM_NUM_BITS)));
	}
	
	public static int getTrackBufferSize(int sample){
		return Math.max(
				sample, 
				ceil(AudioRecord.getMinBufferSize(
						sample, 
						AudioFormat.CHANNEL_OUT_MONO, 
						ENCODING_PCM_NUM_BITS)));
	};
	
	public static int getRecorderBufferSteroSize(int sample){
		return Math.max(
				sample, 
				ceil(AudioRecord.getMinBufferSize(
						sample, 
						AudioFormat.CHANNEL_IN_STEREO, 
						ENCODING_PCM_NUM_BITS)));
	}
	
	public static int getTrackBufferSteroSize(int sample){
		return Math.max(
				sample, 
				ceil(AudioTrack.getMinBufferSize(
						sample, 
						AudioFormat.CHANNEL_OUT_STEREO, 
						ENCODING_PCM_NUM_BITS)));
	};
	
	private static int ceil(int size) {
		return (int) Math.ceil( ( (double) size / FRAME_SIZE )) * FRAME_SIZE;
	}
		
}

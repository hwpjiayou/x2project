package com.renren.mobile.x2.components.chat.util;



/**
 * @author dingwei.chen
 * 可播放接口
 * */
public interface Playable {
	
	public static interface PLAY_STATE{
		int PLAYING = 0;
		int STOP = 1;
	}
	
	public void play();
	public void stop();
	
}

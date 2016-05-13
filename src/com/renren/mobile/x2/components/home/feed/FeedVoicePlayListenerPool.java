package com.renren.mobile.x2.components.home.feed;

import java.util.ArrayList;
import java.util.List;

import com.renren.mobile.x2.utils.voice.PlayerThread.OnPlayerListenner;

/**
 * 控制语音播放的监听队列
 * @author jia.xia
 *
 */
public class FeedVoicePlayListenerPool {

	public static final FeedVoicePlayListenerPool sInstance = new FeedVoicePlayListenerPool();
	
	/**
	 * 放置语音播放监听器
	 */
	private List<OnPlayerListenner> voicePlayPool = new ArrayList<OnPlayerListenner>();
	/**
	 * 当前监听器，只有当前的监听器是处于活动状态
	 */
	private OnPlayerListenner mCurrentPlayListener  = null;
	
	private FeedVoicePlayListenerPool(){
		
	}
	
	public static FeedVoicePlayListenerPool getInstance(){
		return  sInstance;
	}
	/**
	 * 
	 * @param listener:OnPlayerListenner
	 */
	public void addVoicePlayListener(OnPlayerListenner listener){
		if(listener == null) return;
		for(OnPlayerListenner l:voicePlayPool){
			if(l.hashCode() == listener.hashCode()){  // 去重
				return;
			}
		}
		
		voicePlayPool.add(listener);
		if(voicePlayPool.size() == 1){
			mCurrentPlayListener = listener;
		}
	}
	
	public void clearAll(){
		voicePlayPool.clear();
	}
	/**关闭播放
	 * @param listener:OnPlayerListenner    ->null 关闭当前,无其他操作
	 * 										->not null   关闭当前，播放现在
	 */
	public void onStop(OnPlayerListenner listener){
//		for(OnPlayerListenner listener:voicePlayPool){
//			listener.onPlayStop();
//		}
		if(mCurrentPlayListener!=null)
			mCurrentPlayListener.onPlayStop();
		if(listener !=null){
			mCurrentPlayListener = listener;
			mCurrentPlayListener.onPlayStart();
		}
	}
	
	public void onPlay(OnPlayerListenner listener){
		listener.onPlayStart();
	}
	/**
	 * 
	 * @param url
	 */
	public void getPlayListener(String url){
		
	}
	
}

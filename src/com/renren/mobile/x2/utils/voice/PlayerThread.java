package com.renren.mobile.x2.utils.voice;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.media.AudioTrack;
import android.util.Log;

import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.log.Logger;


/**
 * @author dingwei.chen
 * @说明 播放线程
 * */
public final class PlayerThread extends Thread {

	private AudioTrack mPlayer  = null;
	private AtomicBoolean mIsExit = new AtomicBoolean(false);
	private AtomicBoolean mIsPlay = new AtomicBoolean(true);
	private Ogg2PcmDecoder mDecoder = null;
	private List<PlayRequest> mPlayRequests = new LinkedList<PlayRequest>();
	private List<PlayRequest> mPlayDelayRequests = new LinkedList<PlayRequest>();
	private PlayRequest mCurrentPlayRequest = null;
	byte[] mLock = new byte[0];
	
	private static PlayerThread sInstance  = new PlayerThread();
	private PlayerThread(){}
	
	public static PlayerThread getInstance(){
		return sInstance;
	}
		
	public void run(){
		while(!isExit()){
			if (Logger.mDebug) {
				Logger.mark("进入播放线程");
			}
			mCurrentPlayRequest = this.obtainPlayRequest();
			if(mCurrentPlayRequest!=null){
				mDecoder = new Ogg2PcmDecoder(mCurrentPlayRequest.mAbsVoiceFileName);
			}else{
				continue;
			}
			mIsPlay.set(true);
			FramesPool.getInstance().clearCache();
			mDecoder.run();
			mCurrentPlayRequest.onPlayPrepare();
			this.onSwitchStart();
			try {
				
				while(isPlaying()){
					//向帧池申请一个帧用于播放
					PCMFrame frame = FramesPool.getInstance()
									.obtainFrame();
					//用PCM播放器播放数据
					if(frame.mPcmFrame!=null){
						try {
							//申请一个播放器（!切换听筒也在这个类中实现,修正配置）
							mPlayer = PCMPlayerPool.getPool()
									.obtainPlayer(
											PCMPlayerSetting.CHANNEL_NUMMBER,
											PCMPlayerSetting.SIMPLE_RATE,
											PCMPlayerSetting.STREAM_TYPE);
							mPlayer.write(frame.mPcmFrame, 
									 0, 
									 frame.mPcmFrame.length);
						} catch (Exception e) {
							if (Logger.mDebug) {
								Logger.errord("播放器错误="+e.toString());
								e.printStackTrace();
							}
						}
					}else{
						throw new Exception();
					}
					
				}
			} catch (Exception e) {
				if(Logger.mDebug){
					Logger.errord("播放异常:"+e.toString());
					e.printStackTrace();
				}
				mCurrentPlayRequest.onPlayStop();
				mCurrentPlayRequest = null;
				mDecoder.stopDecoder();//停止解码器
				if(mPlayer!=null){
					PCMPlayerPool.getPool().putPlayer(mPlayer);
				}
				/*清空帧池中的数据*/
				FramesPool.getInstance()
						.clearCache();
				
			}
		}
	}
	
	
	
	
	public boolean isExit(){
		return mIsExit.get();
	}
	public boolean isPlaying(){
		return mIsPlay.get();
	}
	public void addToPlay(PlayRequest request){
		if(this.getState() == State.NEW){
			this.start();
		}
		synchronized (mPlayRequests) {
			for(PlayRequest r:mPlayRequests){
				if(r.mAbsVoiceFileName.equals(request.mAbsVoiceFileName))
				{return;}
			}
			mPlayRequests.add(request);
			mPlayRequests.notify();
		}
	}
	/*删除播放请求*/
	public void removePlayRequest(String url){
		synchronized (mPlayRequests) {
			for(PlayRequest r:mPlayRequests){
				if(r.mAbsVoiceFileName.equals(url)){
					mPlayRequests.remove(r);
					return;
				}
			}
			try {
				if(mCurrentPlayRequest!=null){
					mCurrentPlayRequest.mAbsVoiceFileName.equals(url);
					this.stopCurrentPlay();
				}
			} catch (Exception e) {}
			
		}
	}
	/*将播放次序放到队列头部*/
	public void addToPlayFromHead(PlayRequest request){
		if(this.getState() == State.NEW){
			this.start();
		}
		synchronized (mPlayRequests) {
			int k = 0;
			for(PlayRequest r:mPlayRequests){
				r = mPlayRequests.get(k);
				if(r!=null&&
						r.mAbsVoiceFileName.equals
						(request.mAbsVoiceFileName)
				)
				{return;}
			}
			mPlayRequests.add(0,request);
			mPlayRequests.notify();
		}
	}
	
	/*强制播放语音*/
	public boolean forceToPlay(PlayRequest request){
		this.clearRequests();//清空播放队列
		if(mCurrentPlayRequest!=null){
			if(!mCurrentPlayRequest.mAbsVoiceFileName.equals(request.mAbsVoiceFileName)){
				this.addToPlay(request);
				this.stopCurrentPlay();
				return true;
			}
			this.stopCurrentPlay();
			return false;
		}else{
			this.addToPlay(request);
			return true;
		}
	}
	public void stopCurrentPlay(){
		FramesPool.getInstance()
					.clearCache();
		FramesPool.getInstance()
			.addFrame2Pool(PCMFrame.createTailFrame());
	}
	public void stopAllPlay(){
		this.clearRequests();
		this.clearAllDelayRequests();
		this.stopCurrentPlay();
	}
	public void clearRequests(){
		synchronized (mPlayRequests) {
			mPlayRequests.clear();
		}
	}
	public PlayRequest obtainPlayRequest(){
		PlayRequest request = null;
		try {
			synchronized (mPlayRequests) {
				while(true){
					if(mPlayRequests.size()==0){
						this.onSwitchEnd();
						mPlayRequests.wait();
					}else{
						request = mPlayRequests.remove(0);
						break;
					}
				}
			}
		} catch (Exception e) {}
		return request;
	}
	
	
	
	public static class PlayRequest{
		public String mAbsVoiceFileName = null;
		public OnPlayerListenner mPlayListenner = null;
		public PlayRequest clone(){
			PlayRequest request = new PlayRequest();
			request.mAbsVoiceFileName = mAbsVoiceFileName;
			request.mPlayListenner = mPlayListenner;
			return request; 
		}
		public void onPlayPrepare(){
			if(mPlayListenner!=null){
				mPlayListenner.onPlayStart();
			}
		}
		public void onPlayPlaying(){
			if(mPlayListenner!=null){
				mPlayListenner.onPlayPlaying();
			}
		}
		public void onPlayStop(){
			if(mPlayListenner!=null){
				mPlayListenner.onPlayStop();
			}
		}
		
	}
	public static interface OnPlayerListenner{
		public void onPlayStart();
		public void onPlayPlaying();
		public void onPlayStop();
	}
	public static interface OnSwitchPlayModeListenner{
		public void onOpen();
		public void onClose();
	}
	public void onSwitchStart(){
		if(this.mSwitchPlayerListenner!=null){
			mSwitchPlayerListenner.onOpen();
		}
	}
	public void onSwitchEnd(){
		if(this.mSwitchPlayerListenner!=null){
			mSwitchPlayerListenner.onClose();
		}
	}
	
	
	
	private  OnSwitchPlayModeListenner mSwitchPlayerListenner = null;
	public void setOnSwitchPlayerListenner(OnSwitchPlayModeListenner listenner){
		mSwitchPlayerListenner = listenner;
	}
	/*!可能不安全*/
	public void unregistorSwitchPlayerListenner(OnSwitchPlayModeListenner listenner){
		if(mSwitchPlayerListenner== listenner){
			mSwitchPlayerListenner = null;
		}
	}
	public void replay(){
		if(mCurrentPlayRequest!=null){
			this.addToPlayFromHead(this.mCurrentPlayRequest);
			this.stopCurrentPlay();
		}
	}
	public void addToDelayPlay(PlayRequest request){
		if(!mPlayDelayRequests.contains(request)){
			mPlayDelayRequests.add(request);
		}
	}
	public void playDelay(){
		for(int k = 0;k<mPlayDelayRequests.size();k++){
			this.addToPlay(mPlayDelayRequests.get(k));
		}
		mPlayDelayRequests.clear();
	}
	public void clearAllDelayRequests(){
		this.mPlayDelayRequests.clear();
	}
	OnAddPlayListener mListener = null;
	public void setOnAddPlayListener(OnAddPlayListener listener){
		mListener = listener;
	}
	public void unregistorPlayListener(){
		this.mListener = null;
	}
	public void onAddPlay(PlayRequest request){
		if(this.mListener!=null){
			this.mListener.onAddPlay(request);
		}
	}
	public static interface OnAddPlayListener{
		public void onAddPlay(PlayRequest request);
	}
	
	
}

package com.renren.mobile.x2.components.chat.util;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.components.chat.command.Command;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Voice;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PlayerThread;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnPlayerListenner;
import com.renren.mobile.x2.network.mas.INetResponse;



/**
 * @author dingwei.chen
 * @说明 语音下载队列
 * */
public class VoiceDownloadThread extends Thread{

	private List<DownloadReuqest> mVoiceDownloadQueue 
							= new LinkedList<DownloadReuqest>();
	
	private AtomicBoolean mIsExit = new AtomicBoolean(false);
	private static DownloadReuqest mCurrentDownloadRequest = null;
	private static VoiceDownloadThread sInstance = new VoiceDownloadThread();
	private static byte[] mLock = new byte[0];
	
	public static VoiceDownloadThread getInstance(){
		if(sInstance.getState() == State.NEW){
			sInstance.start();
		}else if(sInstance.getState() == State.TERMINATED ){
			sInstance = new VoiceDownloadThread();
			sInstance.start();
		}
		return sInstance;
	} 
	public void stopToAutoPlay(){
		this.stopToAutoPlay(null);
	}
	public void stopAllPlay(){
		PlayerThread.getInstance().stopAllPlay();
	}
	
	
	
	public void stopToAutoPlay(ChatMessageWarpper_Voice message ){
			try {
				if(mCurrentDownloadRequest!=null){
					if(message!=null&&mCurrentDownloadRequest.mMessage.mMessageId!=message.mMessageId){
						if(mCurrentDownloadRequest.mMessage.mMessageReceiveTime<message.mMessageReceiveTime){
							mCurrentDownloadRequest.mIsDelayToPlay = false;
							mCurrentDownloadRequest.mIsAutoToPlay = false;
						}else{
							mCurrentDownloadRequest.mIsDelayToPlay = true;
							mCurrentDownloadRequest.mIsAutoToPlay = true;
						}
					}else{
						if(message==null){
							mCurrentDownloadRequest.mIsAutoToPlay = false;
							mCurrentDownloadRequest.mIsDelayToPlay = false;
							mCurrentDownloadRequest.mMessage.mIsAutoPlay = false;
						}
					}
				}
			} catch (Exception e) {}
			boolean flag = false;
			synchronized (mVoiceDownloadQueue) {
				for(DownloadReuqest r:mVoiceDownloadQueue){
					if(message!=null){
						if((r.mMessage.mMessageId == message.mMessageId)||(r.mMessage.mMessageReceiveTime > message.mMessageReceiveTime)){
							flag = true;
						}
					}
					if(!flag){
						r.mIsDelayToPlay = r.mIsAutoToPlay?true:false;
						if(message==null){
							r.mIsDelayToPlay = false;
						}
						r.mIsAutoToPlay = false;
						r.mMessage.mIsAutoPlay = false;
						r.mMessage.mIsDelayPlay = false;
					}else{
						
						r.mIsAutoToPlay = true;
						r.mIsDelayToPlay = true;
						r.mMessage.mIsAutoPlay = true;
					}
					
				}
			}
	}
	
	public void run(){
		while(!isExit()){
			mCurrentDownloadRequest = 
					this.obtainDownloadRequest();
			onDownloadStart();
			synchronized (mLock) {
//				McsServiceProvider.getProvider().getVoice(mCurrentDownloadRequest.mVoiceUrl
//										,new DownloadResponse(mCurrentDownloadRequest));
				HttpMasService.getInstance().getVoice(mCurrentDownloadRequest.mVoiceUrl, new DownloadResponse(mCurrentDownloadRequest));
				try {
					mLock.wait();
					mCurrentDownloadRequest = null;
				} catch (InterruptedException e) {}
			}
		}
	}
	private DownloadReuqest obtainDownloadRequest(){
		DownloadReuqest request = null;
		while(true){
			synchronized (mVoiceDownloadQueue) {
				if(mVoiceDownloadQueue.size()==0){
					try {
						mVoiceDownloadQueue.wait();
					} catch (InterruptedException e) {}
				}else{
					request =  mVoiceDownloadQueue.remove(0);
					break;
				}
			}
		}
		return request;
	}
	
	/**
	 * 做重复请求拦截
	 * */
	public void addDownloadRequest(DownloadReuqest request){
		/*存储空间拦截*/
		if(!DeviceUtil.getInstance().isMountSDCard()){
			request.mListenner.onDownloadError();
			return;
		}
		if(!DeviceUtil.getInstance().isSDCardHasEnoughSpace()){
			request.mListenner.onDownloadError();
			return;
		}
		synchronized (mVoiceDownloadQueue) {
			DownloadReuqest containRequest = isContainRequest(request.mVoiceUrl);
			if(containRequest!=null){
				boolean flag = (containRequest.mListenner!=request.mListenner);
				if(flag){
					if(request.mListenner!=null){
						containRequest.mListenner = request.mListenner;
						request.mListenner.onDownloadPrepare();
					}
				}
				return ;
			}
			
			if(request.mListenner!=null){
				request.mListenner.onDownloadPrepare();
				request.mListenner.onDownloadStart();
			}
			mVoiceDownloadQueue.add(request);
			mVoiceDownloadQueue.notify();
		}
	}
	
	public void removeDownloadRequest(String voiceUrl){
		synchronized (mVoiceDownloadQueue) {
			for(DownloadReuqest request:mVoiceDownloadQueue){
				if(request.mVoiceUrl.equals(voiceUrl)){
					mVoiceDownloadQueue.remove(request);
					break;
				}
			}
			if(mCurrentDownloadRequest!=null){
				try {
					if(mCurrentDownloadRequest.mVoiceUrl.equals(voiceUrl)){
						mCurrentDownloadRequest.mIsAutoToPlay = false;
						mCurrentDownloadRequest.mIsDelayToPlay = false;
					}
				} catch (Exception e) {}
			}
		}
	}
	
	
	/**
	 * 去重判定
	 * */
	public DownloadReuqest isContainRequest(String voiceUrl){
		synchronized (mVoiceDownloadQueue) {
			for(DownloadReuqest r:mVoiceDownloadQueue){
				if(r.mVoiceUrl.equals(voiceUrl)){
					return r;
				}
			}
		}
		try {
			if(mCurrentDownloadRequest!=null){
				if(mCurrentDownloadRequest.mVoiceUrl.equals(voiceUrl)){
					return mCurrentDownloadRequest;
				}
			}
		} catch (Exception e) {}
		
		return null;
	}
	
	
	
	public boolean isExit(){
		return mIsExit.get();
	}
	
	
	
	public static class DownloadReuqest{
		public String mVoiceUrl ;//语音Url
		public String mSaveFilePath;//语音存储路径
		public boolean mIsAutoToPlay;//下载后是否自动播放
		public OnDownloadListenner mListenner ;//下载回调
		public OnPlayerListenner mPlayListenner;
		public long mToChatId;
		public boolean mIsDelayToPlay = false;//延迟播放
		public ChatMessageWarpper_Voice mMessage;
	}
	public static interface OnDownloadListenner{
		public void onDownloadPrepare();
		public void onDownloadStart();
		public void onDownloadSuccess(DownloadReuqest request);
		public void onDownloadError();
	}
	private void onDownloadStart(){
		if(mCurrentDownloadRequest.mListenner!=null){
			mCurrentDownloadRequest.mListenner.onDownloadStart();
		}
	}
	
	public static class DownloadResponse implements INetResponse{
		
		DownloadReuqest mRequest = null;
		public DownloadResponse(DownloadReuqest request){
			mRequest = request ;
		}
		@Override
		public void response(INetRequest req, JSONObject data) {
			if(mRequest!=null&&mRequest.mListenner!=null){
				if(data!=null){
					if(Methods.checkNoError(req, data)){
						if (Logger.mDebug) {
							Logger.logd(Logger.GET_VOICE,"语音下载成功 ");
						}
						byte[] bytes =null;
						try {
							bytes = (byte[])data.get(VOICE_DATA);
						} catch (JSONException e1) {
							if(Logger.mDebug){
								Logger.errord(e1.toString());
								e1.printStackTrace();
							}
							
						}
						if(bytes==null){
							if (Logger.mDebug) {
								Logger.logd(Logger.GET_VOICE,"转化失败 ");
							}
							return ;
						}else{
							if (Logger.mDebug) {
								Logger.logd(Logger.GET_VOICE,"下载语音长度  ="+bytes.length);
							}
						}
						try {
							if(!DeviceUtil.getInstance().isSDcardEnable()){
								throw new Exception();
							}
							if(mRequest.mSaveFilePath!=null){
								ChatUtil.getUserAudioFromOutToLocal(mRequest.mToChatId);
								FileOutputStream fos = new FileOutputStream(mRequest.mSaveFilePath);
								fos.write(bytes);
								fos.close();
							}
						} catch (Exception e) {
							if(mRequest.mListenner!=null){
								mRequest.mListenner.onDownloadError();
							}
							synchronized (mLock) {
								mLock.notify();
							}
							return;
						}
						if(mRequest.mListenner!=null){
							mRequest.mListenner.onDownloadSuccess(
									mRequest);
						}
						synchronized (mLock) {
							mLock.notify();
						}
						return ;
					}
				}
				if (Logger.mDebug) {
					Logger.errord(Logger.GET_VOICE,"语音下载失败");
				}
				mRequest.mListenner.onDownloadError();
				synchronized (mLock) {
					mLock.notify();
				}
			}
			
		}
	}
	
	
	
	
	
}

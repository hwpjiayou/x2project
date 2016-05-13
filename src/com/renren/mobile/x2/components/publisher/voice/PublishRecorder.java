package com.renren.mobile.x2.components.publisher.voice;

import java.util.Map;

import android.R.integer;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.command.Command;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.ThreadPool;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.publisher.PublisherFragment;
import com.renren.mobile.x2.components.publisher.voice.PublishVoiceView.MonitorListener;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.VoiceManager;
import com.renren.mobile.x2.utils.voice.RecordThread.OnRecordListenner;

public class PublishRecorder extends Command implements MonitorListener{
	
	private static final long MIN_RECORD_TIME = 1500L;
	private static final long MAX_RECORD_TIME = 60000L;
	private static final long WARN_RECORD_TIME = 56000L;
	private static final long SEND_STATE_MESSAGE_TIME = 1000l;
	private static final float STEP = 360/60;
	PublisherFragment mChatActivity = null;
	private boolean mIsStart = false;
	long mPreClickStartTime = 0L;
	public PublishRecorder(PublisherFragment chatActivity) {
		mChatActivity = chatActivity;
	}
	@Override
	public void onStartCommand() {
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE, "onStartCommand");
			Logger.traces(Logger.PLAY_VOICE);
		}
		mIsStart = DeviceUtil.getInstance().isSDCardHasEnoughSpace();
		if(!mIsStart){
			DeviceUtil.getInstance().toastNotEnoughSpace();
			return;
		}
		if((System.currentTimeMillis()-mPreClickStartTime)<MIN_RECORD_TIME){
			CommonUtil.toast(R.string.chat_record_rest); //休息一会儿
			
			mIsStart = false;
			return;
		}
		VoiceDownloadThread.getInstance().stopToAutoPlay();//不让语音自动播放
		VoiceManager.getInstance().setRecordListener(createRecordListenner());
		VoiceManager.getInstance().record(ChatUtil
				.createRecordFileName(Long.parseLong(LoginManager.getInstance().getLoginInfo().mUserId)));
		VoiceManager.getInstance().stopAllPlay();
		mChatActivity.keepScreenOn();//不锁屏
	}

	
	@Override
	public void onEndCommand(Map<String, Object> returnData) {
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE, "onEndCommand");
		}
		if(!mIsStart){
			return;
		}
		mPreClickStartTime = System.currentTimeMillis();
		VoiceManager.getInstance().stopRecord(true);
		mChatActivity.stopKeepScreenOn();
	}
	private long mStartTime = 0L;
	
	public OnRecordListenner createRecordListenner(){
		OnRecordListenner listenner =  new OnRecordListenner() {
			
			public void onEncoderEnd(final String absFileName, byte[] data,final boolean isEncodeSuccess) {
				if(Logger.mDebug){
					Logger.logd(Logger.PLAY_VOICE, "onEncoderEnd isEncodeSuccess="+isEncodeSuccess+"#len="+data.length);
				}
				long processTime = System.currentTimeMillis()-mStartTime;
				mStartTime = 0;
//				if(processTime>=MAX_RECORD_TIME){
//					processTime = MAX_RECORD_TIME;
//				}
				final int tmpTime = (int)((processTime)/1000);
				try {
					if(Logger.mDebug){
						Logger.logd(Logger.PLAY_VOICE, "录音文件的位置 ="+tmpTime+"S#"+absFileName);
					}
					ThreadPool.obtain().executeMainThread(new Runnable() {
						@Override
						public void run() {
							mChatActivity.mVoiceContainer.endEncorderEnd(isEncodeSuccess,absFileName,tmpTime);
						}
					});
				} catch (Exception e) {
					if(Logger.mDebug){
						Logger.errord(Logger.PLAY_VOICE, e.getMessage());
						Logger.errord(e.getMessage());
						e.printStackTrace();
					}
				}
				
			}
			
			
			public void onRecording(final int vsize) {
				if(Logger.mDebug){
					Logger.logd(Logger.RECORD, "onRecording vsize="+vsize);
				}
				ThreadPool.obtain().executeMainThread(new Runnable() {
					@Override
					public void run() {
						mChatActivity.mVoiceContainer.setVolumn(vsize);
					}
				});
			}
			
			public void onRecordStart(String fileName) {
				mStartTime = System.currentTimeMillis();
				if(Logger.mDebug){
					Logger.logd(Logger.PLAY_VOICE, "onRecordStart mStartTime="+mStartTime);
				}
				ThreadPool.obtain().executeMainThread(new Runnable() {
					@Override
					public void run() {
						mChatActivity.mVoiceContainer.initStartRecordState();
					}
				});
				
			}
			public void onRecordEnd(String absFileName) {
				if(Logger.mDebug){
					Logger.logd(Logger.PLAY_VOICE, "onRecordStart mStartTime="+mStartTime);
				}
			}
			public boolean isCanRecord() {
				return System.currentTimeMillis()-mStartTime<=MAX_RECORD_TIME;
			}
		};
		return listenner;
	}
	
	public void onVoiceStart() {
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE, "onVoiceStart");
		}
		this.onStartCommand();
		mStartTimeShowCancel = 2000;
	}
	public int onGetTime() {
		if(mStartTime==0){
			return 0;
		}
		return (int)((System.currentTimeMillis()-mStartTime)/1000);
	}
	public int onGetAngle() {
		if(mStartTime==0){
			return 0;
		}
		return (int)(STEP*((System.currentTimeMillis()-mStartTime)/1000.0));
	}
	public boolean onIsLessMinTime() {
		return (System.currentTimeMillis()-mStartTime)<MIN_RECORD_TIME;
	}
	/**
	 * @param isSuccess is abondon
	 * */
	public void onVoiceEnd() {
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE, "onVoiceEnd");
		}
		this.onEndCommand(null);
	}
	private int mStartTimeShowCancel = 2000;
	private static final int OFFSET = 15000;
	public boolean onIsShowMoveToCancel() {
		// TODO Auto-generated method stub
		long time = System.currentTimeMillis()-mStartTime;
		if(time<MAX_RECORD_TIME){
			if(time>mStartTimeShowCancel){
				mStartTimeShowCancel+=OFFSET;
				return true;
			}
		}
		return false;
	}
	
	
	
}
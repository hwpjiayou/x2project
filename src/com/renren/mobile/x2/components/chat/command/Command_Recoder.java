package com.renren.mobile.x2.components.chat.command;

import java.util.Map;


import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Voice;
import com.renren.mobile.x2.components.chat.message.StateMessageSender;
import com.renren.mobile.x2.components.chat.net.ChatMessageSender;
import com.renren.mobile.x2.components.chat.util.ChatDataHelper;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.StateMessageModel;
import com.renren.mobile.x2.components.chat.util.ThreadPool;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread;
import com.renren.mobile.x2.components.chat.view.VoiceView.MonitorListener;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.RecordThread.OnRecordListenner;
import com.renren.mobile.x2.utils.voice.VoiceManager;

/**
 * @author dingwei.chen
 * @说明 录音命令
 * */
public class Command_Recoder extends Command implements MonitorListener{
	
	private static final long MIN_RECORD_TIME = 1500L;
	private static final long MAX_RECORD_TIME = 60000L;
	private static final long SEND_STATE_MESSAGE_TIME = 1000l;
	private static final float STEP = 360/60;
	RenRenChatActivity mChatActivity = null;
	private boolean mIsStart = false;
	private boolean mIsSendState = false;
	public boolean mIsSend = true;
	long mPreClickStartTime = 0L;
	public Command_Recoder(RenRenChatActivity chatActivity) {
		mChatActivity = chatActivity;
	}
	@Override
	public void onStartCommand() {
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
				.createRecordFileName(mChatActivity.getmToChatUser().getUId()));
		VoiceManager.getInstance().stopAllPlay();
		mChatActivity.keepScreenOn();//不锁屏
	}

	
	@Override
	public void onEndCommand(Map<String, Object> returnData) {
		if(!mIsStart){
			return;
		}
		mPreClickStartTime = System.currentTimeMillis();
		VoiceManager.getInstance().stopRecord(true);
		mChatActivity.stopKeepScreenOn();
	}
	private long mStartTime = 0L;
	private boolean mIsSuccess = true;
	public OnRecordListenner createRecordListenner(){
		OnRecordListenner listenner =  new OnRecordListenner() {
			
			public void onEncoderEnd(String absFileName, byte[] data,boolean isEncodeSuccess) {
				if(!isEncodeSuccess || !mIsSuccess){
					//Logger.log("voiceupload", "upload insert-1 "+isEncodeSuccess+":"+mIsSuccess);
					mIsSend = true;
					return;
				}
				long processTime = System.currentTimeMillis()-mStartTime;
				if(processTime>=MAX_RECORD_TIME){
					processTime = MAX_RECORD_TIME;
				}
				try {
					if(processTime > MIN_RECORD_TIME && isCanSend() && mIsSend){
						ChatMessageWarpper_Voice message = new ChatMessageWarpper_Voice();
						message.mComefrom  = ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT;
						message.mMessageContent = absFileName;
						message.mPlayTime = (int)((processTime)/1000);
						message.mLocalUserId = Long.valueOf(LoginManager.getInstance().getLoginInfo().mUserId);
						message.parseUserInfo(mChatActivity.getmToChatUser());
						message.mMessageContent = absFileName;
						//message.mIsGroupMessage = mChatActivity.mGroup.Value;
						//message.mGroupId = message.mToChatUserId;
						//message.mDomain = mChatActivity.mChatListAdapter.getDomain();
						ChatDataHelper.getInstance().insertToTheDatabase(message);
						ChatMessageSender.getInstance().uploadData(message, data);
					}
				} catch (Exception e) {
					if(Logger.mDebug){
						Logger.errord(e.getMessage());
						e.printStackTrace();
					}
					// TODO: handle exception
				//	Logger.log("voiceupload", "upload error "+e+":"+Logger.printStackElements(e.getStackTrace()));
				}
				
				mIsSend = true;
			}
			
			
			public void onRecording(final int vsize) {
				long time = System.currentTimeMillis()-mStartTime;
				if(time>SEND_STATE_MESSAGE_TIME && !mIsSendState ){
					StateMessageSender.getInstance().send(LoginManager.getInstance().getLoginInfo().mUserName, 
							Long.parseLong(LoginManager.getInstance().getLoginInfo().mUserId), 
							mChatActivity.getmToChatUser().getUId(),
							StateMessageModel.STATE_TYPE.SPEEKING);
					mIsSendState = true;
				}
				ThreadPool.obtain().executeMainThread(new Runnable() {
					@Override
					public void run() {
						mChatActivity.mRoot_VoiceView.setVolumn(vsize);
					}
				});
			}
			
			public void onRecordStart(String fileName) {
				mStartTime = System.currentTimeMillis();
				ThreadPool.obtain().executeMainThread(new Runnable() {
					@Override
					public void run() {
						mChatActivity.mRoot_VoiceView.popView();
					}
				});
				
			}
			public void onRecordEnd(String absFileName) {
				if(mIsSendState){
					StateMessageSender.getInstance().send(LoginManager.getInstance().getLoginInfo().mUserName, 
							Long.parseLong(LoginManager.getInstance().getLoginInfo().mUserId), 
							mChatActivity.getmToChatUser().getUId(), 
							StateMessageModel.STATE_TYPE.CANCELED);
					mIsSendState = false;
				}
			}
			public boolean isCanRecord() {
				return System.currentTimeMillis()-mStartTime<=MAX_RECORD_TIME;
			}
		};
		return listenner;
	}
	
	//OnRecorderSendListenner mCanSendListenner = null;
//	public void setOnCanSendListenner(OnRecorderSendListenner listenner){
//		mCanSendListenner = listenner;
//	}
	public boolean isCanSend(){
//		if(mCanSendListenner!=null){
//			return mCanSendListenner.isCanSend();
//		}
		return true;
	}
//	public static interface OnRecorderSendListenner{
//		public boolean isCanSend();
//	}
	public void onVoiceStart() {
		// TODO Auto-generated method stub
		this.onStartCommand();
		mStartTimeShowCancel = 2000;
	}
	public int onGetTime() {
		// TODO Auto-generated method stub
		return (int)((System.currentTimeMillis()-mStartTime)/1000);
	}
	public int onGetAngle() {
		// TODO Auto-generated method stub
		return (int)(STEP*((System.currentTimeMillis()-mStartTime)/1000.0));
	}
	public boolean onIsLessMinTime() {
		// TODO Auto-generated method stub
		return (System.currentTimeMillis()-mStartTime)<MIN_RECORD_TIME;
	}
	/**
	 * @param isSuccess is abondon
	 * */
	public void onVoiceEnd(boolean isSuccess) {
		mIsSuccess = isSuccess;
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

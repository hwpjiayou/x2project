package com.renren.mobile.x2.components.comment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.command.Command;
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
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.mas.UGC;
import com.renren.mobile.x2.network.mas.UGCManager;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.RecordThread.OnRecordListenner;
import com.renren.mobile.x2.utils.voice.VoiceManager;




/**
 * @author hwp
 * @说明 录音命令
 * */
public class Comment_Recorder extends Command implements MonitorListener{
	
	private static final long MIN_RECORD_TIME = 1500L;
	private static final long MAX_RECORD_TIME = 60000L;
	private static final long WARN_RECORD_TIME = 56000L;
	private static final long SEND_STATE_MESSAGE_TIME = 1000l;
	private static final float STEP = 360/60;
	CommentFragment mView = null;
	private CommentItem message;
	private boolean mIsStart = false;
	private boolean mIsSendState = false;
	public boolean mIsSend = true;
	long mPreClickStartTime = 0L;
	public Comment_Recorder(CommentFragment view) {
		mView = view;
	}
	@Override
	public void onStartCommand() {
		mIsStart = DeviceUtil.getInstance().isSDCardHasEnoughSpace();
		if(!mIsStart){
			DeviceUtil.getInstance().toastNotEnoughSpace();
			return;
		}
		if((System.currentTimeMillis()-mPreClickStartTime)<MIN_RECORD_TIME){
			CommonUtil.toast("休息一会儿");
			
			mIsStart = false;
			return;
		}
		VoiceDownloadThread.getInstance().stopToAutoPlay();//不让语音自动播放
		VoiceManager.getInstance().setRecordListener(createRecordListenner());
		VoiceManager.getInstance().record(ChatUtil
				.createRecordFileName(Long.parseLong(mView.mLoginInfo.mUserId)));  //创建音频名称
		VoiceManager.getInstance().stopAllPlay();
		mView.keepScreenOn();//不锁屏
	}

	
	@Override
	public void onEndCommand(Map<String, Object> returnData) {
		if(!mIsStart){
			return;
		}
		mPreClickStartTime = System.currentTimeMillis();
		VoiceManager.getInstance().stopRecord(true);
    	mView.stopKeepScreenOn();
	}
	private long mStartTime = 0L;
	private boolean mIsSuccess = true;
	public OnRecordListenner createRecordListenner(){
		OnRecordListenner listenner =  new OnRecordListenner() {
			
			public void onEncoderEnd(String absFileName, byte[] data,boolean isEncodeSuccess) {
				if(!isEncodeSuccess || !mIsSuccess){
					mIsSend = true;
					return;
				}
				long processTime = System.currentTimeMillis()-mStartTime;
				if(processTime>=MAX_RECORD_TIME){
					processTime = MAX_RECORD_TIME;
				}
				try {
					if(processTime > MIN_RECORD_TIME && isCanSend() && mIsSend){
						message=new CommentItem();
						message.setVoiceComefrom(CommentItem.MESSAGE_COMEFROM.LOCAL_TO_OUT);
						message.setVoiceContent(absFileName);
						message.setVoicePlayTime((int)processTime/1000);
						message.setUserId(mView.mLoginInfo.mUserId);
						message.setUserHeadUrl(mView.mLoginInfo.mMediumUrl);
						message.setUserName(mView.mLoginInfo.mUserName);
						message.setCreateTime(System.currentTimeMillis());
						/**
						 *  在view界面中展现
						 */
						Log.d("--hwp--","url "+message.getVoiceContent()+" time "+message.getVoicePlayTime());
						mView.addCommentIten(message);
						RenrenChatApplication.getUiHandler().post(new Runnable() {
							
							@Override
							public void run() {
								mView.mAdapter.notifyDataSetChanged();
							}
						});
						
					}
				} catch (Exception e) {
					if(Logger.mDebug){
						Logger.errord(e.getMessage());
						e.printStackTrace();
					}
				}

				/**
				 *  //封装的数据解析成jsonarray发送到服务上就行了，在下面编写。
				 */
				UGC feedVoice=makeFeedRequests(message);
				UGCManager.getInstance().sendUGC(feedVoice);
				
				mIsSend = true;
			}
			//得到服务器发送回来的信息。
			public UGC makeFeedRequests(CommentItem item){
				INetRequest voicRequest=getVoiceRequest(item);
				String schoolId=mView.mLoginInfo.mSchool_id;
				int feedId=(int) mView.mFeedDataModel.getFeedId();
				UGC feed = new UGC(schoolId ,UGC.UGC_TYPE_COMMENT, feedId ,voicRequest, null, null, response);
				return feed;
			}
			
			
			public INetRequest getVoiceRequest(CommentItem item){
				if(!(item.getVoiceContent()).equals("")&&item.getVoiceContent().length()>0){
					File name=new File(item.getVoiceContent());
					byte[] bytes=new byte[(int)name.length()];
					FileInputStream fis;
					try {
						fis=new FileInputStream(name);
						fis.read(bytes);
						fis.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			//		long toId= mView.mFeedDataModel.getFeedId();
					long toId =      3000010698L;
					Log.v("--hwp--","log "+toId+" "+item.getVoicePlayTime()+" "+ bytes);
					return UGCManager.getInstance().getVoiceUploadRequest(toId, "1", 1, "end", item.getVoicePlayTime(), bytes);
				}
				
				return null;
			}
			
			public void onRecording(final int vsize) {
				long time = System.currentTimeMillis()-mStartTime;
				if(time>SEND_STATE_MESSAGE_TIME && !mIsSendState ){
					StateMessageSender.getInstance().send(LoginManager.getInstance().getLoginInfo().mUserName, 
							Long.parseLong(LoginManager.getInstance().getLoginInfo().mUserId), 
							mView.mFeedDataModel.getFeedId(),//发送给feed发送者，这个过程中产品形态没定，需要修改， 修改成发送给所有人吧，即 id 为null；
							StateMessageModel.STATE_TYPE.SPEEKING);
					mIsSendState = true;
				}
				ThreadPool.obtain().executeMainThread(new Runnable() {
					@Override
					public void run() {
						mView.mRoot_VoiceView.setVolumn(vsize);
					}
				});
			}
			
			 public  INetResponse  response = new INetResponse() {
					
					@Override
					public void response(INetRequest req, JSONObject obj) {
						JSONObject data = (JSONObject)obj;
						if(Methods.checkNoError(req, data)){
							//toast("success:"+data.toString());
							CommonUtil.toast("success:"+data.toString());
							Log.v("--hwp--","data "+data.toString());
						}else{
							CommonUtil.toast("error:"+data.toString());

						}
					}
				};
			
			public void onRecordStart(String fileName) {
				mStartTime = System.currentTimeMillis();
				ThreadPool.obtain().executeMainThread(new Runnable() {
					@Override
					public void run() {
						mView.mRoot_VoiceView.popView();
					}
				});
				
			}
			public void onRecordEnd(String absFileName) {
				if(mIsSendState){
					StateMessageSender.getInstance().send(LoginManager.getInstance().getLoginInfo().mUserName, 
							Long.parseLong(LoginManager.getInstance().getLoginInfo().mUserId), 
							mView.mFeedDataModel.getFeedId(), 
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
	
	OnRecorderSendListenner mCanSendListenner = null;
	public void setOnCanSendListenner(OnRecorderSendListenner listenner){
		mCanSendListenner = listenner;
	}
	public boolean isCanSend(){
		if(mCanSendListenner!=null){
			return mCanSendListenner.isCanSend();
		}
		return true;
	}
	public static interface OnRecorderSendListenner{
		public boolean isCanSend();
	}
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

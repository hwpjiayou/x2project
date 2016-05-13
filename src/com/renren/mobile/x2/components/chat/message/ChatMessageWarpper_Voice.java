package com.renren.mobile.x2.components.chat.message;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.face.IUploadable_Voice;
import com.renren.mobile.x2.components.chat.net.ChatMessageSender;
import com.renren.mobile.x2.components.chat.util.ChatDataHelper;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.CurrentChatSetting;
import com.renren.mobile.x2.components.chat.util.ItemLongClickDialogProxy.LONGCLICK_COMMAND;
import com.renren.mobile.x2.components.chat.util.Playable;
import com.renren.mobile.x2.components.chat.util.Playable.PLAY_STATE;
import com.renren.mobile.x2.components.chat.util.SwitchSpeakerIDs;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread.DownloadReuqest;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread.OnDownloadListenner;
import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.db.table.ChatHistory_Column;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.FileUtil;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnPlayerListenner;
import com.renren.mobile.x2.utils.voice.RecordEncoderPool;
import com.renren.mobile.x2.utils.voice.VoiceManager;


public class ChatMessageWarpper_Voice extends ChatMessageWarpper  implements OnDownloadListenner, 
OnPlayerListenner, 
IUploadable_Voice{
	/**
	 * 音频数据
	 * */
	@ORM(mappingColumn = ChatHistory_Column.VOICE_URL)
	public String mVoiceUrl = "";
	@ORM(mappingColumn = ChatHistory_Column.VOICE_ID)
	public String mVid = "";
	@ORM(mappingColumn = ChatHistory_Column.PLAY_TIME)
	public int mPlayTime = 0;
	public boolean mIsPlaying = false;
	public boolean mIsAutoDownload = false;
	public boolean mIsAutoPlay = false;
	public boolean mIsDelayPlay = false;
	public long mStartPlayTime = 0;
	public int mIndex = SwitchSpeakerIDs.RIGHT_SOURCE_IDS.length-1;
	public int mPointNumber = 0;
	public static final int POINT_NUMBER = 3;
	/**语音播放状态1停止 0播放  */
	public int mPlayState = Playable.PLAY_STATE.STOP;
	public Map<String, Object> mResetData = null;
	public ChatMessageWarpper_Voice() {
		this.mMessageType = ChatBaseItem.MESSAGE_TYPE.VOICE;
		this.mMessageState = Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_OVER;
	}
	/**
	 * @author dingwei.chen
	 * @param isForcePlay 是否强制播放
	 * */
	@Override
	public void download(boolean isForcePlay) {
		if(this.mComefrom == ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT){
			return;
		}
		if(this.mSendTextState==SEND_TEXT_STATE.SEND_PREPARE){
			return;
		}
		if (!DeviceUtil.getInstance().isMountSDCard()) {
			DeviceUtil.getInstance().toastNotMountSDCard();
			return;
		}
		if (!DeviceUtil.getInstance().isSDCardHasEnoughSpace()) {
			DeviceUtil.getInstance().toastNotEnoughSpace();
			return;
		}
		if (this.mSendTextState!=SEND_TEXT_STATE.SEND_PREPARE) {
			DownloadReuqest request = new DownloadReuqest();
			request.mVoiceUrl = this.mVoiceUrl;
			request.mIsAutoToPlay = this.mIsAutoPlay || isForcePlay;
			request.mSaveFilePath = this.mMessageContent;
			request.mListenner = this;
			request.mToChatId = this.mToChatUserId;
			request.mIsDelayToPlay = this.mIsDelayPlay;
			request.mMessage = this;
			VoiceDownloadThread.getInstance().addDownloadRequest(request);
		}
	}
	@Override
	public void onAddToAdapter() {
		if (mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL) {
			File file = new File(mMessageContent);
			if (file.exists()) {
				return;
			} else {
				if(		
						VoiceDownloadThread.getInstance()
								.isContainRequest(this.mVoiceUrl)!=null
					  &&this.mSendTextState != SEND_TEXT_STATE.SEND_PREPARE)
					{
					this.download(false);//替换掉之前的请求但不播放
					return;
				}
				if(		mIsAutoDownload
					&&	this.mSendTextState != SEND_TEXT_STATE.SEND_PREPARE
				){
					this.download(true&&!mIsSyn);
				}
			}
		}
	}
	@Override
	public void resend() {
		if(this.mSendTextState==SEND_TEXT_STATE.SEND_PREPARE){
			return;
		}
		VoiceManager.getInstance().removePlayRequest(mMessageContent);
		if (this.mMessageState == COMMAND.COMMAND_UPLOAD_VOICE_ERROR) {
			try {
				this.updateChatMessageState(Subject.COMMAND.COMMAND_UPLOAD_VOICE_OVER);
				ChatMessageSender.getInstance().uploadData(this, FileUtil.getInstance().readBytes(new File(mMessageContent)));
			} catch (Exception e) {}
		} else {
			ChatMessageSender.getInstance().sendMessageToNet(this, false);
		}
	}
	@Override
	public void reflash() {
		if (mPlayState == PLAY_STATE.PLAYING) {
			if (mObserver != null) {
				if (mResetData == null) {
					mResetData = new HashMap<String, Object>(1);
				}
				if(mIndex<0){
					mIndex = SwitchSpeakerIDs.RIGHT_SOURCE_IDS.length-1;
				}
				if (mComefrom == ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT) {
					mResetData.put(Subject.DATA.COMMAND_UPDATE_VOICE_VIEW, SwitchSpeakerIDs.RIGHT_SOURCE_IDS[mIndex]);
				} else {
					mResetData.put(Subject.DATA.COMMAND_UPDATE_VOICE_VIEW, SwitchSpeakerIDs.LEFT_SOURCE_IDS[mIndex]);
				}
				mIndex--;
				mObserver.update(Subject.COMMAND.COMMAND_UPDATE_VOICE_VIEW, mResetData);
			}
		}
	}
	@Override
	public List<OnLongClickCommandMapping> getOnClickCommands() {
		List<OnLongClickCommandMapping> list = new LinkedList<ChatMessageWarpper.OnLongClickCommandMapping>();
		if (this.mMessageState == Subject.COMMAND.COMMAND_MESSAGE_ERROR || this.mMessageState == Subject.COMMAND.COMMAND_UPLOAD_VOICE_ERROR) {
			list.add(new OnLongClickCommandMapping(ChatUtil.getText(R.string.chat_resend),LONGCLICK_COMMAND.RESEND));	//ChatMessageWarpper_FlashEmotion_java_1=重新发送; 
		}
		if (this.mMessageState == Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_ERROR) {
			list.add(new OnLongClickCommandMapping(ChatUtil.getText(R.string.chat_redownload), LONGCLICK_COMMAND.REDOWNLOAD));		//ChatMessageWarpper_Image_java_2=重新下载; 
		}
		list.add(new OnLongClickCommandMapping(ChatUtil.getText(R.string.chat_del_voice_message), LONGCLICK_COMMAND.DELETE));		//ChatMessageWarpper_Voice_java_1=删除语音消息; 
		//list.add(new OnLongClickCommandMapping("转发",LONGCLICK_COMMAND.FORWARD));		//ChatMessageWarpper_FlashEmotion_java_3=转发; 
		list.add(new OnLongClickCommandMapping(ChatUtil.getText(R.string.cancel), LONGCLICK_COMMAND.CANCEL));		//ChatMessageWarpper_FlashEmotion_java_4=取消; 
		return list;
	}
	@Override
	public String getDescribe() {
		return "["+ChatUtil.getText(R.string.chat_voice)+"]";		//ChatMessageWarpper_Voice_java_2=[语音];
	}
	/*--------------------播放部分----------------------*/
	public void onPlayStart() {
		if(Logger.mDebug){
			Logger.logd("开始播放音频 from="+mComefrom+"#地址="+this.mMessageContent);
		}
		this.mPlayState = PLAY_STATE.PLAYING;
		if (mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL) {
			this.updateChatMessageState(Subject.COMMAND.COMMAND_PLAY_VOICE_OVER,false);
		}
		observerUpdate(Subject.COMMAND.COMMAND_PLAY_VOICE_OVER, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_VOICE_UNLISTEN,null);
	}
	public void onPlayPlaying() {}
	public void onPlayStop() {
		if(Logger.mDebug){
			Logger.logd("结束播放音频 from="+mComefrom+"#地址="+this.mMessageContent);
		}
		this.mPlayState = PLAY_STATE.STOP;
		observerUpdate(Subject.COMMAND.COMMAND_PLAY_VOICE_OVER, createResetData());
	}
	
	/**用于是否刷新当前界面*/
	public boolean isReflash() {
		return this.mPlayState == PLAY_STATE.PLAYING;
	}
	
	public void onDelete() {
		this.mIsAutoPlay = false;
		this.mIsDelayPlay = false;
		VoiceManager.getInstance().removePlayRequest(this.mMessageContent);
		if (this.mSendTextState== SEND_TEXT_STATE.SEND_PREPARE 
				&& this.mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL) {
			VoiceDownloadThread.getInstance().removeDownloadRequest(mVoiceUrl);
		}
		File file = new File(this.mMessageContent);
		if (file.exists()) {
			file.delete();
		}
	}
	/*----------------------下载部分--------------------------*/
	public void onDownloadPrepare() {
		this.updateChatMessageState(Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_OVER,false);
		this.showLoading(true);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_VOICE_UNLISTEN,null);
		this.observerUpdate(Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_OVER,null);
		this.mSendTextState = SEND_TEXT_STATE.SEND_PREPARE;
	}
	public void onDownloadStart() {
		this.mSendTextState = SEND_TEXT_STATE.SEND_PREPARE;			//ChatMessageWarpper_Voice_java_3=下载开始; 
	}
	public void onDownloadSuccess(DownloadReuqest request) {	//ChatMessageWarpper_Voice_java_4=下载成功; 
		this.mPlayState = PLAY_STATE.STOP;
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
		this.updateChatMessageState(Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_OVER,false);
		this.observerUpdate(Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_OVER, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_VOICE_UNLISTEN,null);
		this.showLoading(false);//UI变更
		/* 下载完成 */
		if (	CurrentChatSetting.isChatActivityTop()
				&& CurrentChatSetting.isScreenOn()
				&& !CurrentChatSetting.isCurrentAppRunBackground()
				&& (request.mToChatId == CurrentChatSetting.CHAT_ID)) {
			if (!RecordEncoderPool.getInstance().isEncoding()) {
				if(!this.mIsSyn){
					if (request.mIsAutoToPlay) {
						VoiceManager.getInstance().addToPlay(this.mMessageContent,this);
					} else if (request.mIsDelayToPlay) {
						this.mIsDelayPlay = false;
						this.mIsAutoPlay = true;
						VoiceManager.getInstance().addToPlay(this.mMessageContent,this);
					}
				}
			} else {
				if (request.mIsAutoToPlay) {
					this.mIsAutoPlay = false;
					this.mIsDelayPlay = true;
					VoiceManager.getInstance().addToDelayPlay(this.mMessageContent,this);
				} else {
					if (request.mIsDelayToPlay) {
						VoiceManager.getInstance().addToDelayPlay(this.mMessageContent,this);
					}
				}
			}
		}
	}
	public void onDownloadError() {
		this.mPlayState = PLAY_STATE.STOP;
		this.mIsAutoDownload = false;
		this.mIsAutoPlay = false;
		this.updateChatMessageState(Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_ERROR,false);
		this.observerUpdate(Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_ERROR, null);
		this.showLoading(false);
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
	}
	/*---------------------上传部分---------------------------*/
	public void onUploadStart() {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_VOICE,"开始上传语音");
		}
		this.updateChatMessageState(Subject.COMMAND.COMMAND_UPLOAD_VOICE_OVER);
		this.observerUpdate(Subject.COMMAND.COMMAND_VOICE_UPLOADING, createResetData());
		this.setSendState(SEND_TEXT_STATE.SEND_PREPARE);
	}
	private void onUploadSuccess() {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_VOICE,"成功上传语音");
		}
		this.mPlayState = PLAY_STATE.STOP;
		updateChatMessageState(Subject.COMMAND.COMMAND_UPLOAD_VOICE_OVER);
		this.observerUpdate(Subject.COMMAND.COMMAND_UPLOAD_VOICE_OVER, createResetData());
		ChatMessageSender.getInstance().sendMessageToNet(ChatMessageWarpper_Voice.this, false);
	}
	public void onUploadError() {
		if(Logger.mDebug){
			Logger.errord(Logger.SEND_VOICE,"上传语音失败");
		}
		this.mPlayState = PLAY_STATE.STOP;
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING, null);
		this.updateChatMessageState(Subject.COMMAND.COMMAND_UPLOAD_VOICE_ERROR);
		this.observerUpdate(Subject.COMMAND.COMMAND_UPLOAD_VOICE_ERROR, createResetData());
		this.setSendState(SEND_TEXT_STATE.SEND_OVER);
	}
	///////////////////////////语音文本的发送//////////////////////////////
	public void onSendTextPrepare(){
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_VOICE,"准备开始发送语音");
		}
		super.onSendTextSuccess();
	}
	public void onSendTextSuccess(){
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_VOICE,"语音发送成功");
		}
		super.onSendTextSuccess();
	}
	public void onSendTextError(){
		if(Logger.mDebug){
			Logger.errord(Logger.SEND_VOICE,"语音发送失败");
		}
		super.onSendTextError();
	}
	/* 其他 */
	public Map<String, Object> createResetData() {
		if (mResetData == null) {
			mResetData = new HashMap<String, Object>(2);
		}
		mResetData.put(Subject.DATA.COMMAND_UPDATE_VOICE_TIME, mPlayTime + "秒");		//RenRenChatActivity_java_4=秒; 
		if (mComefrom == ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT) {
			mResetData.put(Subject.DATA.COMMAND_UPDATE_VOICE_VIEW, SwitchSpeakerIDs.RIGHT_SOURCE_IDS[0]);
		} else {
			mResetData.put(Subject.DATA.COMMAND_UPDATE_VOICE_VIEW, SwitchSpeakerIDs.LEFT_SOURCE_IDS[0]);
		}
		return mResetData;
	}
	@Override
	public void onErrorCode() {
		this.addErrorCode(Subject.COMMAND.COMMAND_MESSAGE_ERROR)
			  .addErrorCode(Subject.COMMAND.COMMAND_UPLOAD_VOICE_ERROR)
		      .addErrorCode(Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_ERROR);
	}
	@Override
	public void swapDataFromXML(Message message) {
		this.mVoiceUrl = message.mBody.audio.mUrl;
		this.mPlayTime = message.mBody.audio.getPlayTime();
		this.mMessageContent = ChatUtil
						.getUserAudioFromOutToLocal(this.mToChatUserId)
						+FileUtil.getInstance().getFileNameFromURL(this.mVoiceUrl);
		boolean flag = CurrentChatSetting.isChatActivityTop()
				&& !CurrentChatSetting.isCurrentAppRunBackground()
				&& DeviceUtil.getInstance().isSDCardHasEnoughSpace(); 
		this.mIsAutoDownload = flag;
		this.mIsAutoPlay = flag&&!mIsSyn;
		if(!VoiceManager.getInstance().isRecording()){
			this.mIsAutoPlay = flag&&true;
			this.mIsDelayPlay = flag&&false;
		}else{
			this.mIsAutoPlay = flag&&false;
			this.mIsDelayPlay = flag&&true;
		}
	}
	public void onPreInsertDB() {
		this.download(false);
	}
	public long getToId() {
		return this.mToChatUserId;
	}
	public int getPlayTime() {
		return this.mPlayTime;
	}
	public void onUploadOver(String voiceId, String voiceUrl) {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_VOICE,"语音上传成功 voiceId="+voiceId+"#voiceUrl="+voiceUrl);
		}
		ContentValues contentValue = new ContentValues();
		this.mVoiceUrl =  voiceUrl;
		this.mVid =  voiceId;
		contentValue.put(ChatHistory_Column.VOICE_ID, this.mVid);
		contentValue.put(ChatHistory_Column.VOICE_URL, this.mVoiceUrl);
		contentValue.put(ChatHistory_Column.MESSAGE_STATE, COMMAND.COMMAND_UPLOAD_VOICE_OVER);
		ChatDataHelper.getInstance().update(this, contentValue);
		this.onUploadSuccess();
	}

}

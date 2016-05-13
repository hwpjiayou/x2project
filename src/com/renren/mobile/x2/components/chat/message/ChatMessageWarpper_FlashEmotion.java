package com.renren.mobile.x2.components.chat.message;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.ItemLongClickDialogProxy.LONGCLICK_COMMAND;
import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.emotion.Emotion;
import com.renren.mobile.x2.emotion.EmotionPool;
import com.renren.mobile.x2.utils.Methods;


public class ChatMessageWarpper_FlashEmotion extends ChatMessageWarpper {
	public ChatMessageWarpper_FlashEmotion(){
		this.mMessageType = ChatBaseItem.MESSAGE_TYPE.FLASH;
	}
	public int mIndex = 0;
	boolean mIsFlash = true;
	@Override
	public List<OnLongClickCommandMapping> getOnClickCommands() {
		List<OnLongClickCommandMapping> list = new LinkedList<ChatMessageWarpper.OnLongClickCommandMapping>();
		if(this.mMessageState == Subject.COMMAND.COMMAND_MESSAGE_ERROR ){
			list.add(new OnLongClickCommandMapping(RenrenChatApplication.getApplication().getText(R.string.chat_resend).toString(),LONGCLICK_COMMAND.RESEND));		//ChatMessageWarpper_FlashEmotion_java_1=重新发送; 
		}
		list.add(new OnLongClickCommandMapping(RenrenChatApplication.getApplication().getText(R.string.chat_del_cool).toString(),LONGCLICK_COMMAND.DELETE));		//ChatMessageWarpper_FlashEmotion_java_2=删除炫酷表情消息; 
		//list.add(new OnLongClickCommandMapping(RenrenChatApplication.getApplication().getText(R.string.ChatMessageWarpper_FlashEmotion_java_3),LONGCLICK_COMMAND.FORWARD));		//ChatMessageWarpper_FlashEmotion_java_3=转发; 
		list.add(new OnLongClickCommandMapping(RenrenChatApplication.getApplication().getText(R.string.cancel).toString(),LONGCLICK_COMMAND.CANCEL));		//ChatMessageWarpper_FlashEmotion_java_4=取消; 
		return list;
	}
	@Override
	public String getDescribe() {
		return "["+ChatUtil.getText(R.string.chat_flashimage)+"]";		//ChatMessageWarpper_FlashEmotion_java_5=[炫酷表情]; 
	}
	
	UpdateImageAction mUpdateAction = new UpdateImageAction();
	class UpdateImageAction implements Runnable{
		public Bitmap mBitmap = null;
		public void updateBitmap(Bitmap bitmap){
			this.mBitmap = bitmap;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mObserver!=null){
				Map<String,Object> data = obtainData(mBitmap);
				mObserver.update(
							Subject.COMMAND.COMMAND_UPDATE_FLASH_IMAGE,
							data);
			}
		}
		
	}
	
	
	Map<String,Object> mData = new HashMap<String, Object>();
	
	private Map<String,Object> obtainData(Bitmap bitmap){
		mData.put(Subject.DATA.COMMAND_UPDATE_IMAGE, bitmap);
		return mData;
	}

	
	
	@Override
	public void reflash() {
		Emotion emotion = EmotionPool.getInstance().getCoolEmotionbycode(mMessageContent);
		if(emotion == null || emotion.mFrameSize==0 ){
			return ;
		}
		Bitmap bitmap = emotion.get(mIndex);
		mIndex++;
		mIndex = mIndex%emotion.mFrameSize;
		this.mUpdateAction.updateBitmap(bitmap);
		this.post(this.mUpdateAction);
	}
	@Override
	public boolean isReflash() {
		return mIsFlash;
	}
	@Override
	public void download(boolean isForceDownload) {}
	@Override
	public void onErrorCode() {
		this.addErrorCode(Subject.COMMAND.COMMAND_MESSAGE_ERROR);
	}
	@Override
	public boolean isHideBackground() {
		return super.isHideBackground()||!hasNewsFeed();
	}
	@Override
	public void swapDataFromXML(Message message) {
		this.mMessageContent = Methods.htmlDecoder(message.mBody.text.mValue);
	}
}

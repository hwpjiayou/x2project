package com.renren.mobile.x2.components.chat.net;

import com.renren.mobile.x2.components.chat.face.IUploadable_Image;
import com.renren.mobile.x2.components.chat.face.IUploadable_Voice;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.util.ChatDataHelper;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.talk.MessageManager;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener;
import com.renren.mobile.x2.network.talk.NetRequestListener;
import com.renren.mobile.x2.network.talk.requests.Message_SendSynMessage;
import com.renren.mobile.x2.utils.log.Logger;

/**
 *负责消息的发送
 */
public final class ChatMessageSender {

	private static ChatMessageSender sSender = new ChatMessageSender();

	private ChatMessageSender(){}
	public static ChatMessageSender getInstance(){
		return sSender;
	}
	
	/**
	 * 将消息发送的服务器  （包括文本发送）
	 * @param message 要发送的消息
	 * @param isInsertToDatabase 是否小插入数据库
	 */
	public void sendMessageToNet(final ChatMessageWarpper message,boolean isInsertToDatabase){
		if(message!=null){
			message.onSendTextPrepare();
			if(isInsertToDatabase){
				ChatDataHelper.getInstance().insertToTheDatabase(message);
			}
			this.sendMessageToNet(message.mLocalUserId,message.mToChatUserId, message);
		}
	}
	
	public void sendRequestToNet(NetRequestListener request){
		MessageManager.sendMessage(request);
	}
	
//	public void sendMessageToNet(long fromId,long toId,OnSendTextListener listener){
//		sendMessageToNet(fromId, toId, null, listener);
//	} 
	
	public void sendMessageToNet(long fromId,long toId, OnSendTextListener listener){
		NetRequestListener request  = null;
		request = new Message_SendSynMessage(fromId, toId,listener);
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_TEXT,"加入队列request="+(request==null));
			Logger.traces(Logger.SEND_TEXT);
		}
		MessageManager.sendMessage(request);
	} 
	

	
	/**
	 * 上传语音文件
	 * @param message
	 * @param data
	 */
	public void uploadData(IUploadable_Voice message, byte[] data) {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_VOICE,"uploade voice data = "+ data.length);
			Logger.traces(Logger.SEND_VOICE);
		}
		message.onUploadStart();
		HttpMasService.getInstance().postVoice(message.getToId(), IUploadable_Voice.VID, IUploadable_Voice.SEQ_ID, IUploadable_Voice.END, message.getPlayTime(), data, new UploadVoice(message));
	}
	
	/**
	 * 上传图片文件
	 * @param message
	 * @param data
	 */
	public void uploadData(IUploadable_Image message, byte[] data) {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO,"uploade img data = "+ data.length);
			Logger.traces(Logger.SEND_PHOTO);
		}
		message.onUploadStart();
		HttpMasService.getInstance().sendPhoto(data, message.getToId(),  new UploadImage(message));
	}
	
	
	
}

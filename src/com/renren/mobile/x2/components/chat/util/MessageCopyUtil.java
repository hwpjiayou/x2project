package com.renren.mobile.x2.components.chat.util;

import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Voice;



/**
 * 模型复制工具
 * */
public class MessageCopyUtil {

	private static MessageCopyUtil sInstance = new MessageCopyUtil();
	private MessageCopyUtil(){}
	
	public static MessageCopyUtil getInstance(){
		return sInstance;
	}
	//创建克隆对象
	public ChatMessageWarpper createCloneMessage(ChatMessageWarpper message){
		switch(message.mMessageType){
		default:
			return null;
		}
	}
	//创建语音克隆对象
	public ChatMessageWarpper_Voice createCloneMessage(ChatMessageWarpper_Voice message){
		ChatMessageWarpper_Voice newMessage = new ChatMessageWarpper_Voice();
		message.copyToBaseField(newMessage);
		newMessage.mVoiceUrl = message.mVoiceUrl;
		return newMessage;
	}
	
	
}

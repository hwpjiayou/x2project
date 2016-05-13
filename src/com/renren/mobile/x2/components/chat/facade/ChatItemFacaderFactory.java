package com.renren.mobile.x2.components.chat.facade;

import com.renren.mobile.x2.components.chat.message.ChatBaseItem;



/**
 * @author dingwei.chen
 * @说明 装饰工厂
 * */
public class ChatItemFacaderFactory {

	private static ChatItemFacaderFactory sInstance = new ChatItemFacaderFactory();
	
	private ChatItemFacader mFacader_Text = new ChatItemFacader_Text();
	private ChatItemFacader mFacader_Image = new ChatItemFacader_Image();
	private ChatItemFacader mFacader_Voice = new ChatItemFacader_Voice();
	private ChatItemFacader mFacader_Flash = new ChatItemFacader_FlashEmotion();
	private ChatItemFacader mFacader_Notify = new ChatItemFacader_Notify();
	private ChatItemFacader mFacader_Null = new ChatItemFacader_Null();
	
	
	private  ChatItemFacaderFactory(){
		this.initFactory();
	}
	private void initFactory(){}
	
	public static ChatItemFacaderFactory getInstance (){
		return sInstance;
	}
	
	
	public ChatItemFacader createFacader(int messageType){
		switch(messageType){
			case ChatBaseItem.MESSAGE_TYPE.TEXT:
				return mFacader_Text;
			case ChatBaseItem.MESSAGE_TYPE.IMAGE:
				return mFacader_Image;
			case ChatBaseItem.MESSAGE_TYPE.VOICE:
				return mFacader_Voice;
			case ChatBaseItem.MESSAGE_TYPE.FLASH:
				return mFacader_Flash;
			case ChatBaseItem.MESSAGE_TYPE.SOFT_INFO:
				return mFacader_Notify;
			case ChatBaseItem.MESSAGE_TYPE.NULL:
				return mFacader_Null;
			default:
				return null;
		}
	}
	
}

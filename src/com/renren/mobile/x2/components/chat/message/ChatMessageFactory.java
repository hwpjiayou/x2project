package com.renren.mobile.x2.components.chat.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

import com.renren.mobile.x2.components.chat.message.ChatBaseItem.MESSAGE_ISGROUP;
import com.renren.mobile.x2.db.table.ChatHistory_Column;


/**
 * @author dingwei.chen
 * @说明消息创建工厂
 * */
public final class ChatMessageFactory {

	private static ChatMessageFactory sInstance = new ChatMessageFactory();
	
	private ChatMessageFactory(){}
	
	public static ChatMessageFactory getInstance(){
		return sInstance;
	}
	
	/**
	 * 申请一个消息模型
	 * */
	public ChatMessageWarpper obtainMessage(int messageType){
		ChatMessageWarpper  message = null;
		switch(messageType){
			case ChatBaseItem.MESSAGE_TYPE.TEXT:
				message = this.obtain(ChatMessageWarpper_Text.class);//文本消息类型
				break;
			case ChatBaseItem.MESSAGE_TYPE.VOICE:
				message = this.obtain(ChatMessageWarpper_Voice.class);//语音类型消息
				break;
			case ChatBaseItem.MESSAGE_TYPE.LBS:
				message = this.obtain(ChatMessageWarpper_LBS.class); //LBS类型消息
				break;
			case ChatBaseItem.MESSAGE_TYPE.IMAGE:
				message = this.obtain(ChatMessageWarpper_Image.class);//图片类型消息
				break;
			case ChatBaseItem.MESSAGE_TYPE.FLASH:
				message = this.obtain(ChatMessageWarpper_FlashEmotion.class);//图片类型消息
				break;
			case ChatBaseItem.MESSAGE_TYPE.SOFT_INFO:
				message = this.obtain(ChatMessageWarpper_SoftInfo.class);//图片类型消息
				break;
			default:
				message = new ChatMessageWarpper_Unknow();//未知数据类型
		}
//		message.mMessageType = messageType;
//		message.mIsGroupMessage  = MESSAGE_ISGROUP.IS_GROUP;
		return message;
	}
	
	/**
	 * 申请一个消息模型(由轮询消息调用)
	 * */
	public ChatMessageWarpper obtainMessage(String messageType){
		
		int type = 0;
		if(messageType.endsWith("text")){
			type=ChatBaseItem.MESSAGE_TYPE.TEXT;
		}else if(messageType.endsWith("voice")){
			type=ChatBaseItem.MESSAGE_TYPE.VOICE;
		}else if(messageType.endsWith("image")){
			type=ChatBaseItem.MESSAGE_TYPE.IMAGE;
		}else if(messageType.endsWith("lbs")){
			type=ChatBaseItem.MESSAGE_TYPE.LBS;
		}else if(messageType.endsWith("expression")){
			type=ChatBaseItem.MESSAGE_TYPE.FLASH;
		}else if(messageType.endsWith("info")){
			type=ChatBaseItem.MESSAGE_TYPE.SOFT_INFO;//弱提示
		}else{
			type=ChatBaseItem.MESSAGE_TYPE.UNKNOW;
		}
		return obtainMessage(type);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 通过数据库中的游标来批量创建消息
	 * */
	public ArrayList<ChatMessageWarpper> buildChatMessages(Cursor cursor){
		
		ArrayList<ChatMessageWarpper> messages = new ArrayList<ChatMessageWarpper>();
		while(cursor.moveToNext()){
			ChatMessageWarpper  message = null;
			int type = getMessageType(cursor);
			switch(type){
				case ChatBaseItem.MESSAGE_TYPE.TEXT:
					message = this.obtain(ChatMessageWarpper_Text.class);//文本消息类型
					break;
				case ChatBaseItem.MESSAGE_TYPE.VOICE:
					message = this.obtain(ChatMessageWarpper_Voice.class);//语音类型消息
					break;
				case ChatBaseItem.MESSAGE_TYPE.LBS:
					message = this.obtain(ChatMessageWarpper_LBS.class); //LBS类型消息
					break;
				case ChatBaseItem.MESSAGE_TYPE.IMAGE:
					message = this.obtain(ChatMessageWarpper_Image.class);//图片类型消息
					break;
				case ChatBaseItem.MESSAGE_TYPE.FLASH:
					message = this.obtain(ChatMessageWarpper_FlashEmotion.class);//图片类型消息
					break;
				case ChatBaseItem.MESSAGE_TYPE.SOFT_INFO:
					message = this.obtain(ChatMessageWarpper_SoftInfo.class);//图片类型消息
					break;
			}
		
			if(message!=null){
				message.parseMessageFromDatabase(cursor);
				message.mMessageType = type;//附上消息类型
				messages.add(0, message);
			}
		}
		return messages;
	}
	

	
	
	public ChatMessageWarpper createChatMessage(Cursor cursor){
		ChatMessageWarpper  message = null;
		int type = getMessageType(cursor);
		switch(type){
			case ChatBaseItem.MESSAGE_TYPE.TEXT:
				message = this.obtain(ChatMessageWarpper_Text.class);//文本消息类型
				break;
			case ChatBaseItem.MESSAGE_TYPE.VOICE:
				message = this.obtain(ChatMessageWarpper_Voice.class);//语音类型消息
				break;
			case ChatBaseItem.MESSAGE_TYPE.LBS:
				message = this.obtain(ChatMessageWarpper_LBS.class); //LBS类型消息
				break;
			case ChatBaseItem.MESSAGE_TYPE.IMAGE:
				message = this.obtain(ChatMessageWarpper_Image.class);//图片类型消息
				break;
			case ChatBaseItem.MESSAGE_TYPE.FLASH:
				message = this.obtain(ChatMessageWarpper_FlashEmotion.class);//图片类型消息
				break;
			case ChatBaseItem.MESSAGE_TYPE.SOFT_INFO:
				message = this.obtain(ChatMessageWarpper_SoftInfo.class);//图片类型消息
				break;
		}
		if(message!=null){
			message.mMessageType = type;//附上消息类型
		}
		return message;
	}
	
	
	
	//得到消息类型
	private int getMessageType(Cursor cursor){
		int index = cursor.getColumnIndex(ChatHistory_Column.MESSAGE_TYPE);
		int type = cursor.getInt(index);
		return type;
	}
	Map<Class,List<ChatMessageWarpper>> mRecycleBin = new HashMap<Class,List<ChatMessageWarpper>>(6);
	public void recycle(ChatMessageWarpper message){
		return;
//		synchronized (mRecycleBin) {
//			message.resetData();
//			Class clazz = message.getClass();
//			List<ChatMessageWarpper> list = mRecycleBin.get(clazz);
//			if(list!=null){
//				list.add(message);
//			}else{
//				list = new LinkedList<ChatMessageWarpper>();
//				list.add(message);
//				mRecycleBin.put(clazz, list);
//			}
//		}
	}
	public<T extends ChatMessageWarpper> T obtain(Class<T> clazz){
		try {
			return (T)clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
//		synchronized (mRecycleBin) {
//			List<ChatMessageWarpper> list = mRecycleBin.get(clazz);
//			if(list!=null){
//				if(list.size()>0){
//					return (T)list.remove(0);
//				}else{
//					try {
//						return clazz.newInstance();
//					} catch (Exception e) {}
//				}
//			}else{
//				list = new LinkedList<ChatMessageWarpper>();
//				mRecycleBin.put(clazz, list);
//				try {
//					return clazz.newInstance();
//				} catch (Exception e) {}
//			}
//			
//		}
//		return null;
	}
	
}

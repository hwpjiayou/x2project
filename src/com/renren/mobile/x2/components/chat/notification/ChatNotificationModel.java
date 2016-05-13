package com.renren.mobile.x2.components.chat.notification;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.util.ChatUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ChatNotificationModel {
	
	private ArrayList<ChatMessageWarpper> mUnReadMessageList =new ArrayList<ChatMessageWarpper>();
	private Set<Long> idSet = new HashSet<Long>();
	
	public ChatNotificationModel(){
		
	}
	
	public ChatNotificationModel(ArrayList<ChatMessageWarpper> unReadMessageList) {
		this.mUnReadMessageList = unReadMessageList;
	}
	
	public void setUnReadMessageList(ArrayList<ChatMessageWarpper> unReadMessageList){
		this.mUnReadMessageList = unReadMessageList;
	}	
	
	public ArrayList<ChatMessageWarpper> getUnReadMessageList(){
		return this.mUnReadMessageList;
	}
	
	public synchronized int getCount(){
		return this.mUnReadMessageList.size();
	}
	
	public boolean checkMessageIndex(int id){
		if(id>getCount()){
			return false;
		}else{
			return true;
		}
		
	}

	/***
	 * 获取通知的具体内容，最新的一条从id = 1开始
	 * **/
	public String getMessageUserName(int id){
		if(!checkMessageIndex(id)){
			return null; 
		}
		return this.mUnReadMessageList.get(getCount()-id).mUserName;
	}
	
	public String getMessageContent(int id){
		if(!checkMessageIndex(id)){
			return null; 
		}
		String content = this.mUnReadMessageList.get(getCount()-id).mMessageContent;
		int type = this.mUnReadMessageList.get(getCount()-id).mMessageType;
		if(type == ChatBaseItem.MESSAGE_TYPE.VOICE){
			content = "["+ChatUtil.getText(R.string.chat_voice)+"]";
		}else if(type == ChatBaseItem.MESSAGE_TYPE.IMAGE){
			content = "["+ChatUtil.getText(R.string.chat_image)+"]";
		}else if(type == ChatBaseItem.MESSAGE_TYPE.FLASH){
			content = "["+ChatUtil.getText(R.string.chat_emotion)+"]";
		}
		return content;
	}
	
	public long getMessageDate(int id){
		if(!checkMessageIndex(id)){
			return 0; 
		}
		return this.mUnReadMessageList.get(getCount()-id).mMessageReceiveTime;
	}
	
	public long getMessageUserId(int id){
		if(!checkMessageIndex(id)){
			return 0; 
		}
		return this.mUnReadMessageList.get(getCount()-id).mToChatUserId;
	}
	
//	public long getGroupId(int id){
//		if(!checkMessageIndex(id)){
//			return 0; 
//		}
//		return this.mUnReadMessageList.get(getCount()-id).mGroupId;
//	}
	
	public int getMessageType(int id){
		if(!checkMessageIndex(id)){
			return 0; 
		}
//		return this.mUnReadMessageList.get(getCount()-id).mMessageSendMethod;
		return 0;
	}
	
	public int getMessageSmsId(int id){
		if(!checkMessageIndex(id)){
			return 0; 
		}
		return 0;
//		return this.mUnReadMessageList.get(getCount()-id).mSmsId;
	}
	
	public long getMessageId(int id){
		if(!checkMessageIndex(id)){
			return 0; 
		}
		return this.mUnReadMessageList.get(getCount()-id).mMessageId;
	}
	
//	public int getIsGroupMessage(int id){
//		if(!checkMessageIndex(id)){
//			return 0; 
//		}
//		return this.mUnReadMessageList.get(getCount()-id).mIsGroupMessage;
//	}
	
	public long getSMSId(int id){
		if(!checkMessageIndex(id)){
			return 0; 
		}
		return 0;
//		return this.mUnReadMessageList.get(getCount()-id).mSmsId;
	}
	
	public String getHeadUrl(int id){
		if(!checkMessageIndex(id)){
			return null; 
		}
		return this.mUnReadMessageList.get(getCount()-id).mHeadUrl;
	}
	
	public String getLargeHeadUrl(int id){
		if(!checkMessageIndex(id)){
			return null; 
		}
		return this.mUnReadMessageList.get(getCount()-id).mLargeHeadUrl;
	}
	
	public String getUserPhoneNumber(int id){
		if(!checkMessageIndex(id)){
			return null; 
		}
		return null;
//		return this.mUnReadMessageList.get(getCount()-id).mPhoneNumber;
	}
	

	public synchronized void addNotificaiton(ChatMessageWarpper chatMessage){
		this.mUnReadMessageList.add(chatMessage);
	}
	
	public synchronized void addNotificaitonList(ArrayList<ChatMessageWarpper> chatMessageList){
		this.mUnReadMessageList.addAll(chatMessageList);		
	}
	
//	public synchronized void removeNotificationByUserId(long id){
//		int count = getCount();		
//		
//		for(int i=0;i<count;i++){
//			if(id == this.mUnReadMessageList.get(i).mToChatUserId){
//				this.mUnReadMessageList.remove(i);
//				idSet.remove(id);
//				i--;
//				count--;
//			}
//		}
//	}
	
	public synchronized void removeNotificationByGroupId(long id){
		int count = getCount();		
		
		for(int i=0;i<count;i++){
			if(id == this.mUnReadMessageList.get(i).mToChatUserId){
				this.mUnReadMessageList.remove(i);
				idSet.remove(id);
				i--;
				count--;
			}
		}
	}
	
	
	/**
	 * 通过消息的message id删除此消息
	 * */
	public synchronized void clearUnReadMessageList(){
		if(mUnReadMessageList != null && mUnReadMessageList.size()>0){
			mUnReadMessageList.clear();
		}
	}
	
	public synchronized void removeNotificationByMessageId(long id){
		int count = getCount();		
		
		for(int i=0;i<count;i++){
			if(id == this.mUnReadMessageList.get(i).mMessageId){
				this.mUnReadMessageList.remove(i);
				break;
			}
		}
	}
	
	public synchronized void removeAllNotification(){
		this.mUnReadMessageList.clear();
	}

//	public synchronized int getUnreadMessageCountById(long id){
//		int count = getCount();
//		int num = 0;
//		for(int i=0;i<count;i++){
//			if(this.mUnReadMessageList.get(i).mToChatUserId == id && this.mUnReadMessageList.get(i).mGroupId == -1){
//				
//				num++;
//			}
//			
//		}		
//		return num;
//	}
	
//	public synchronized int getUnreadMessageCountByGroupId(long id){
//		int count = getCount();
//		int num = 0;
//		for(int i=0;i<count;i++){
//			if(this.mUnReadMessageList.get(i).mGroupId == id){
//				num++;
//			}
//		}		
//		return num;
//	}
	
	public synchronized boolean isMutil(){
		int count = getCount();
		long id = 0;
		for(int i=0;i<count;i++){
			id = this.mUnReadMessageList.get(i).mToChatUserId;
			if(!idSet.contains(id)){
				idSet.add(id);
			}
		}
		return (idSet.size()>1)? true:false;
	}
	public synchronized int getUnreadMessageCountByGroupId(long id){
		int count = getCount();
		int num = 0;
		for(int i=0;i<count;i++){
			if(this.mUnReadMessageList.get(i).mToChatUserId == id){
				num++;
			}
		}		
		return num;
	}
}

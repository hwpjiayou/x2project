package com.renren.mobile.x2.components.home.chatlist;

import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;

/**
 * @author dingwei.chen
 * @说明 会话页面数据适配器(将聊天主界面的数据模型转换为会话页面的数据模型)
 * */
public class ChatSessionDataModelAdapter extends ChatListDataModel{
	
	public ChatSessionDataModelAdapter(ChatMessageWarpper message){
		this.mToId = message.mToChatUserId;
		this.mType = message.mMessageType;
		this.mHeadImg = message.mHeadUrl;
		this.mComeFrom = message.mComefrom;
		this.mUserName = message.mUserName;
		this.mChatContent = message.getDescribe();
		this.mLastTime = message.mMessageReceiveTime;
		this.mLocalId = message.mLocalUserId;
		this.mId = message.mMessageId;
		this.mMessageId = message.mMessageId;
		this.mSendState=message.mMessageState;
//		if(this.mComeFrom ==ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT){
////			this.mName
//			ContactBaseModel model = C_ContactsData.getContact(this.mToId, this.mDomain);
//			if(model!=null){
//				this.mUserName = model.mContactName;
//			}else{
//				this.mUserName = message.mUserName;
//			}
//		}
		
		
	}
	
}

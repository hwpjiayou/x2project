package com.renren.mobile.x2.components.home.chatlist;

import android.content.ContentValues;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.db.dao.ChatListDAO;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.table.ChatList_Column;


public class ChatListHelper {
	private static ChatListHelper sInstance = new ChatListHelper();
	ChatListDAO mSessionDAO = null;
	private ChatListHelper(){
		mSessionDAO= DAOFactoryImpl.getInstance().buildDAO(ChatListDAO.class);	
	}
	public static ChatListHelper getInstance(){
		return sInstance;
	}
	/*变更数据库*/
	public int updateMessage(ChatMessageWarpper messageWarpper,String messageContent){
		ContentValues value = new ContentValues(7);
		value.put(ChatList_Column.MESSAGE, messageContent);
		value.put(ChatList_Column.MESSAGE_ID, messageWarpper.mMessageId);
		value.put(ChatList_Column.MESSAGE_STATE, messageWarpper.mMessageState);
		value.put(ChatList_Column.CHAT_TIME, messageWarpper.mMessageReceiveTime);
		value.put(ChatList_Column.COME_FROM, messageWarpper.mComefrom);
		value.put(ChatList_Column.MESSAGE_TYPE, messageWarpper.mMessageType);
		value.put(ChatList_Column.TO_CHAT_NAME, messageWarpper.mUserName);
		int row = mSessionDAO.updateByGroupId(messageWarpper.mToChatUserId, value);
		return row;
	}
	/*插入一条数据*/
	public void insertToTheDatabase(ChatMessageWarpper message){
		mSessionDAO.insertChatSession(message);
	}
	/*更新消息发送状态*/
	public void updateMessageState(long messageId, ContentValues values){
		mSessionDAO.updateByMessageId(messageId, values);
	}
	/*删除一条数据*/
	public void deleteChatSessionByUserId(long groupId){
		mSessionDAO.deleteChatSessionByUserId(groupId);
	}
	
}

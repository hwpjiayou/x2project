package com.renren.mobile.x2.db.dao;

import java.util.List;

import android.content.ContentValues;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.util.ContactModel;
import com.renren.mobile.x2.components.home.chatlist.ChatListDataModel;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.DAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.db.sql.Query_ChatSession;
import com.renren.mobile.x2.db.table.ChatList_Column;

public class ChatListDAO extends BaseDAO{

	public ChatListDAO(BaseDBTable table) {
		super(table);
		// TODO Auto-generated constructor stub
	}
	/*查询会话列表*/
	Query mQuery_Session = new Query_ChatSession(this);
	/*查询单条聊天记录*/
//	Query mQuery_ChatMessage = new Query_ChatMessage(this);

	/**
	 * @说明 这个方法用于会话列表页面获得该使用者的所有会话列表
	 * @param uid表示的是当前登录用户的ID
	 * */
	public List<ChatListDataModel> query_ChatSessions(long uid) {
		/*
		 * 拼接以后结果: select * from chathistory_table where localid = uid group by
		 * to_chat_id order by chat_time desc;
		 */
		String where = ChatList_Column.LOCAL_USER_ID + "=" + uid ;
		@SuppressWarnings("unchecked")
		List<ChatListDataModel> messages = mQuery_Session.query(null, where, null,
					ChatList_Column.CHAT_TIME+DAO.ORDER.DESC,
					null, List.class);
		
		return messages;
	}
	
	/**
	 * 向数据库中插入一条记录
	 */
	public void insertChatSession(ChatMessageWarpper message){
		ContentValues values = new ContentValues();
		ORMUtil.getInstance().ormInsert(message.getClass(), message, values);
		values.put(ChatList_Column.MESSAGE_ID, message.mMessageId);
		mInsert.insert(values);
	}
	
	/**
	 * 数据库中更新一条数据
	 * @param groupId
	 * @param values
	 * @return
	 */
	public int updateByGroupId(long groupId, ContentValues values) {
		String whereStr =  ChatList_Column.LOCAL_USER_ID + " = " + LoginManager.getInstance().getLoginInfo().mUserId+" AND " + ChatList_Column.TO_CHAT_ID + " = " + groupId;
		int row = this.update2(whereStr, values);
		return row;
	}
	
	/**
	 * 数据库中更新一条数据
	 * @param messageId
	 * @param values
	 */
	public void updateByMessageId(long messageId, ContentValues values){
		String whereStr =  ChatList_Column.LOCAL_USER_ID + " = " + LoginManager.getInstance().getLoginInfo().mUserId + " AND " + ChatList_Column.MESSAGE_ID + " = " + messageId;
		this.update2(whereStr, values);
	}
	
	/**
	 * 从数据库中删除一条数据
	 * @param groupId
	 */
	public void deleteChatSessionByUserId(long userId){
		String whereString = ChatList_Column.TO_CHAT_ID + " = " + userId +" AND "+ChatList_Column.LOCAL_USER_ID +" = "+LoginManager.getInstance().getLoginInfo().mUserId;
		this.delete2(whereString);
	}
	
	/**
	 * 查询单人聊天的总数
	 * */
	public int queryCountSinglePerson() {
		String[] whereSqls = new String[]{
				ChatList_Column.LOCAL_USER_ID+"="+LoginManager.getInstance().getLoginInfo().mUserId
		};
		return this.getTableRecordCount1(whereSqls);
	}
	
	/**
	 * 删除所有人的聊天记录
	 * */
	public void deleteAll() {
		this.delete2(null);
	}

}

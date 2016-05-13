package com.renren.mobile.x2.components.chat.util;

import android.content.ContentValues;
import android.content.Context;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.notification.ChatNotificationManager;
import com.renren.mobile.x2.db.dao.ChatHistoryDAO;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.table.ChatHistory_Column;
import com.renren.mobile.x2.utils.SystemService;
import com.renren.mobile.x2.utils.log.Logger;

import java.util.List;



/**
 * @author dingwei.chen
 * @说明 数据存储器(提供数据库存储,文件存储和网络存储)
 * */
public class ChatDataHelper {

	private static ChatDataHelper sInstance = new ChatDataHelper();
	ChatHistoryDAO mChatDao = null;
	private ChatDataHelper(){
		mChatDao= DAOFactoryImpl.getInstance().buildDAO(ChatHistoryDAO.class);
		
	}
	public static ChatDataHelper getInstance(){
		return sInstance;
	}
	public static interface OnSupportFeedListener{
		public boolean onHasFeed();
	//	public NewsFeedWarpper onGetFeedMessage();
		public void onClearFeedMessage();
	}
//	private OnSupportFeedListener mFeedListener = null;
//	public void registorOnSupportFeedListener(OnSupportFeedListener listener){
//		mFeedListener = listener;
//	}
//	public void unreigstorOnSupportFeedListener(){
//		mFeedListener = null;
//	}
//	public void onFeed(ChatMessageWarpper message){
////		if(this.mFeedListener!=null){
////			if(this.mFeedListener.onHasFeed()){
////				NewsFeedWarpper newsFeedWarpper = this.mFeedListener.onGetFeedMessage();
////				if(newsFeedWarpper!=null){
////					message.setNewsFeedModel(newsFeedWarpper);
////				}
////				this.mFeedListener.onClearFeedMessage();
////			}
////		}
//	}
	/*所有发送的消息都以默认成功的方式存在*/
	public void insertToTheDatabase(ChatMessageWarpper message){
		//this.onFeed(message);
		mChatDao.insertChatMessage(message);
	}
	/*存储一列消息！优化空间(采用批处理接口)*/
	public void saveToTheDatabase(List<ChatMessageWarpper> messages){
		for(ChatMessageWarpper message :messages){
			insertToTheDatabase(message);
		}
	}
	/*从数据库中删除*/
	public void deleteToTheDatabase(ChatMessageWarpper message){
		mChatDao.deleteChatMessage(message);
	}
	
	/*从数据库中删除某个人的聊天记录*/
	public void deleteChatMessageByUserId(long userId){
		mChatDao.deleteChatMessageByUserId(userId);
	}
	
//	PUBLIC VOID DELETECHATMESSAGEBYGROUPID(LONG GROUPID){
//		MCHATDAO.DELETECHATMESSAGEBYGROUPID(GROUPID);
//	}
	
	/*从数据库中删除所有人的聊天记录*/
	public void deleteAll(){
		mChatDao.deleteAll();
	}
	/*变更数据库*/
	public void update(ChatMessageWarpper message,ContentValues value){
		mChatDao.update(message, value,true);
	}
	/*变更数据库*/
	public void updateMessageState(ChatMessageWarpper message,int state,boolean isNotifyUpdate){
		if(message.mMessageState!=state){
			ContentValues value = new ContentValues(1);
			value.put(ChatHistory_Column.MESSAGE_STATE, state);
			mChatDao.update(message, value,isNotifyUpdate);
		}
	}
	
	
	public void updateMessageByUserId(long id){
		mChatDao.readUserAllMessage(id);
		ChatNotificationManager.getInstance().clearChatNotification(RenrenChatApplication.getApplication(), id);
	}
	/*复制消息*/
	@SuppressWarnings("deprecation")
	public void copyTheMessage(Context context,String message){
		SystemService.sClipboardManager.setText(message); // 复制
	}

	public int querySumCount(){
	    return this.mChatDao.queryChatHistoryCount();
	}
	public int querySinglePersonHistory(long localId){
		return this.mChatDao.queryCountSinglePerson(localId);
	}
	
	
	public List<ChatMessageWarpper> queryChatHistory(long localId, long toChatId, int pageSize, int page, long time){

		if(Logger.mDebug){
			Logger.logd("localid="+localId+"#tochatid="+toChatId+"#pageSize="+pageSize+"#page="+page+"#time="+time);
		}
		
		List<ChatMessageWarpper> data= this.mChatDao.querySinglePersonChatHistory(
				localId,
				toChatId, 
				pageSize, 
					0, 
				time);
		
		return data;
	}
	
	public List<ChatMessageWarpper> queryChatHistory(long localId, long toChatId, int pageSize, int id){

		List<ChatMessageWarpper> data= this.mChatDao.querySinglePersonChatHistory(
				localId,
				toChatId, 
				pageSize, 
				id);
		
		return data;
	}
	public ChatMessageWarpper queryLastMessageByToChatId(long mToId) {
		// TODO Auto-generated method stub
		return mChatDao.queryLastChatHistoryMessageByToChatId(mToId);
	}
	
	
	
}

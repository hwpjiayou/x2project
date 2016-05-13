package com.renren.mobile.x2.components.chat;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.util.ThreadPool;
import com.renren.mobile.x2.db.dao.ChatHistoryDAO;
import com.renren.mobile.x2.db.dao.ChatHistoryDAOObserver;
import com.renren.mobile.x2.db.table.ChatHistory_Column;



/**
 * @author dingwei.chen
 * @说明 聊天主界面的适配器(将进行数据检验)
 * */
public class ChatAdapter extends BaseChatListAdapter implements ChatHistoryDAOObserver{
	
	ChatHistoryDAO mSubject = null;
	
	RenRenChatActivity mActivity;
	List<ChatMessageWarpper> mMessageCache = new ArrayList<ChatMessageWarpper>();
	InsertRunnable mInsertRunnable = new InsertRunnable();
	
	
	public ChatAdapter(RenRenChatActivity activity) {
		super(activity);
		this.mActivity = activity;
	}
	
	
	
	public void onInsert(final ChatMessageWarpper message) {
		ThreadPool.obtain().removeCallbacks(mInsertRunnable);
		synchronized (mMessageCache) {
			mMessageCache.add(message);
		}
		ThreadPool.obtain().executeMainThread(mInsertRunnable);
	}
	
	
	public class InsertRunnable implements Runnable{

		@Override
		public void run() {
			synchronized (mMessageCache) {
				for(ChatMessageWarpper message:mMessageCache){
					addChatMessage(message);
					notifyDataSetInvalidated();
					notifyCallback();
				}
				mMessageCache.clear();
			}
		}
	}
	
	
	
	
	public void onDelete(String columns,long _id) {
		
		if(columns.equals(ChatHistory_Column._ID)){
			boolean flag = false;
			for(ChatMessageWarpper message:mChatMessages){
				if(message.mMessageId==_id){
					this.mChatMessages.remove(message);
					flag = true;
					break;
				}
			}
			this.processTime();
			if(flag){
				notifyDataSetChanged();
			}
		}
		if(columns.equals(ChatHistory_Column.TO_CHAT_ID)){//删除全部的时候要设置加载云端按钮
			if(_id ==this.mToChatUserId){
				//NewsFeedWarpper m = this.mMessageNull.mNewsFeedMessage;
				this.resetData();
				//this.mMessageNull.setNewsFeedModel(m);
				if(this.mNotifyCallback!=null){
					this.mNotifyCallback.onAllDataDelete();
				}
			}
		}
	}

	
	public void attachToDAO(ChatHistoryDAO dao){
		this.mSubject = dao;
		this.mSubject.registorObserver(this);
	}
	
	public void distachToDAO(){
		if(this.mSubject!=null){
			this.mSubject.unregistorObserver(this);
		}
	}


//	public void onAddPlay(PlayRequest request) {
//		boolean flag = false;
//		for(ChatMessageWarpper m:this.mChatMessages){
//			if(request.mPlayListenner==m){
//				flag = true;
//				continue;
//			}
//			if(flag){
//				if(m.mSendTextState!=SEND_TEXT_STATE.SEND_PREPARE&&(m.mMessageState==Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_OVER) && (m instanceof ChatMessageWarpper_Voice)){
//					VoiceManager.getInstance().addToPlay(m.mMessageContent, (ChatMessageWarpper_Voice)m);
//				}
//			}
//		};
//	}
	public void onInsert(final List<ChatMessageWarpper> messages) {
		final List<ChatMessageWarpper> list = new ArrayList<ChatMessageWarpper>();
		for(ChatMessageWarpper m:messages){
			if(m.mToChatUserId==mToChatUserId){
				list.add(m);
			}
		}
		ThreadPool.obtain().executeMainThread(new Runnable() {
			public void run() {
					addChatMessage(list);
					notifyDataSetInvalidated();
					notifyCallback();
			}
		});
	}
	
//	public void setDomain(String domain){
//		this.mDomain = domain;
//	}
//	public String getDomain(){
//		return this.mDomain;
//	}
	
	@Override
	public void onUpdate(String columnName,final long _id,final ContentValues value) {}
}

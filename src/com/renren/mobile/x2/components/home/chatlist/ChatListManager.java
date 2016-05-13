package com.renren.mobile.x2.components.home.chatlist;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.notification.ChatNotificationManager;
import com.renren.mobile.x2.components.chat.util.ChatDataHelper;
import com.renren.mobile.x2.components.home.HomeFragment;
import com.renren.mobile.x2.components.home.HomeTab;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.db.dao.ChatListDAO;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
public class ChatListManager implements HomeTab {
	public static LinkedList<Object> mChatList = new LinkedList<Object>();
	public static HashSet<Long> mContactSet = new HashSet<Long>();
	private ChatListView mChatListView;
	private long mUserId;
	private LoginInfo mLoginInfo;

    public void getChatListFromLocal(){
    	loadSession();
    	updateMessageList();
    }

    /**
	 * 初始化数据
	 * */
	public void loadSession() {
		if(!TextUtils.isEmpty(LoginManager.getInstance().getLoginInfo().mUserId)){
			mUserId=Long.parseLong((LoginManager.getInstance().getLoginInfo().mUserId));
		}
		if(this.mChatList == null||this.mChatList.size() == 0){
			ChatListDAO chatListDao = DAOFactoryImpl.getInstance().buildDAO(ChatListDAO.class);
			List<ChatListDataModel> sessionList = chatListDao.query_ChatSessions(mUserId);
			if(sessionList != null){
				for(ChatListDataModel model : sessionList){
					ChatMessageWarpper  warpper = ChatDataHelper.getInstance().queryLastMessageByToChatId(model.mToId);
					if(warpper == null){
						model.mChatContent = "";
					}
				}
			int count = sessionList.size();
			for(int i=0;i<count;i++){
				mContactSet.add(sessionList.get(i).mToId);
			}
			LinkedList<Object> list = new LinkedList<Object>(sessionList);
			this.mChatList = list;
		}
		}
				
	}
	/**
	 * 更新会话列表的未读信息数目
	 * */
	public void updateMessageList() {
		int count = mChatList.size();
		long id = 0;
		for (int i = 0; i < count; i++) {
			ChatListDataModel messageItem = (ChatListDataModel) mChatList.get(i);
				id = messageItem.mToId;
				messageItem.unReadCount = 0;
				messageItem.unReadCount += ChatNotificationManager.getInstance().getMessageNotificationModel()
				.getUnreadMessageCountByGroupId(id);
		}
	}
	/**
	 * 增加一条新信息，并且按时间进行排序
	 * */
	public synchronized void addMessage(ChatListDataModel messageItem) {
		loadSession();
		int count = mChatList.size();
		ChatListDataModel oldSessionItem = null;
		if(mContactSet.contains(messageItem.mToId)){
			for (int i = 0; i < count; i++) {
				oldSessionItem = (ChatListDataModel) mChatList.get(i);
				if (messageItem.mToId == oldSessionItem.mToId) {
					mChatList.remove(oldSessionItem);
					break;
				}
			}
		}
		
		long newTime = messageItem.getmLastTime();
		int count1 = mChatList.size();
		if(count1==0){
			mChatList.add(messageItem);
		}else{
		for(int index = 0; index < count1; index++){
			ChatListDataModel chatSessionDataModel = (ChatListDataModel) mChatList.get(index);
			long oldTime = chatSessionDataModel.getmLastTime();
			if(newTime >= oldTime){
				mChatList.add(index, messageItem);
				break;
			}else if(newTime < oldTime){
				if(index == mChatList.size() - 1) {
					mChatList.add(messageItem);
					break;
					}
				}
			}
		}
			mContactSet.add(messageItem.mToId);
	}
	public synchronized void delMessage(ChatListDataModel messageItem, ChatListDataModel model){
		long newTime = messageItem.getmLastTime();
		int count1 = mChatList.size();
		for(int index = 0; index < count1; index++){
			ChatListDataModel chatSessionDataModel = (ChatListDataModel) mChatList.get(index);
			long oldTime = chatSessionDataModel.getmLastTime();
			if(newTime >= oldTime){
				mChatList.add(index, messageItem);
				break;
			}else if(newTime < oldTime){
				if(index == mChatList.size() - 1) {
					mChatList.add(messageItem);
					break;
				}
			}
		}
		refreshChatSessionList();
	}
	public void refreshChatSessionList(){
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				mChatList.clear();
				loadSession();
		    	updateMessageList();
			}
		});
	}
	/**
	 * 变更消息状态
	 * */
	public synchronized void updateMessageState(long id,int state) {
		loadSession();
		for(Object o:mChatList){
			ChatListDataModel model = (ChatListDataModel)o;
			if(model.mId == id){
				model.mSendState = state;
			}
		}
	}

    private void finishChatListData(){
		if(mChatListView!=null){
			mChatListView.setChatListAdapter();
		}
    }

    @Override
    public int getNameResourceId() {
        return R.string.home_tab_chat;
    }

    @Override
    public int getIconResourceId() {
        return R.drawable.v1_home_menu_chat_selector;
    }

    @Override
    public View getView() {
        return mChatListView.getView();
    }

    @Override
    public View onCreateView(Context context) {
        mChatListView = new ChatListView(context);
        return mChatListView.getView();
    }

    @Override
    public void onLoadData() {
    }

    @Override
    public void onFinishLoad() {
        finishChatListData();
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroyData() {
    	mChatList.clear();
    	mContactSet.clear();
    }

    @Override
    public HomeFragment.OnActivityResultListener onActivityResult() {
        return null;
    }
}


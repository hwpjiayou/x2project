package com.renren.mobile.x2.components.home.chatlist;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.db.dao.ChatHistoryDAO;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
/**
 * 会话列表
 * 
 * @author niulu
 * */
public class ChatListView {
	private ListView mChatListView;
	private Context mContext;
	private ChatListAdapter mAdapter;
	final public static int REFRESH_LIST = 0;
	private ChatListManager chatListManager = new ChatListManager();
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_LIST:
				RenrenChatApplication.getUiHandler().post(new Runnable() {
					
					@Override
					public void run() {
						mAdapter.removeAll();
						chatListManager.getChatListFromLocal();
						mAdapter.addLinkList(ChatListManager.mChatList);
						
					}
				});
				break;
			}
			
		};

	};
	public ChatListView(Context context) {
		this.mContext=context;
		initView();
	}
	private void initView() {
		mChatListView = new ListView(mContext);
//		mChatListView.setBackgroundColor(R.color.chatlist_background);
		mChatListView.setBackgroundResource(R.drawable.chatlist_background);
		mChatListView.setVerticalFadingEdgeEnabled(false);
		mChatListView.setDrawingCacheEnabled(false);
		mAdapter = new ChatListAdapter(mContext, null,handler);
		ChatHistoryDAO dao = DAOFactoryImpl.getInstance().buildDAO(ChatHistoryDAO.class);
		mAdapter.attachToDAO(dao);
	}
	public View getView() {
		return mChatListView;
	}
	public void setChatListAdapter() {
		handler.sendEmptyMessage(REFRESH_LIST);
		mChatListView.setAdapter(mAdapter);
	}

}

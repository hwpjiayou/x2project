package com.renren.mobile.x2.components.chat.chatmessages;


import android.view.View;
import android.view.View.OnClickListener;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.view.ITitleBar;

public class ChatMessagesActivity extends BaseFragmentActivity<ChatMessagesModel>{
	ITitleBar mRoot_Title;
	@Override
	protected BaseFragment onCreateContentFragment() {
		ChatMessagesView view = new ChatMessagesView();
		initView();
		return view;
	}

	private void initView() {
		mRoot_Title = getTitleBar();
		mRoot_Title.setTitle(this.getText(R.string.chat_info));// 聊天详情
		mRoot_Title.setLeftAction(R.drawable.title_back, new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onPreLoad() {
		
	}

	@Override
	protected void onFinishLoad(ChatMessagesModel data) {
		
	}

	@Override
	protected ChatMessagesModel onLoadInBackground() {
		return null;
	}

	@Override
	protected void onDestroyData() {
		
	}

}

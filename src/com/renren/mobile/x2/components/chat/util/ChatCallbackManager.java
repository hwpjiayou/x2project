package com.renren.mobile.x2.components.chat.util;

import com.renren.mobile.x2.components.chat.RenRenChatActivity;



/**
 * @author dingwei.chen
 * @说明 聊天回调管理器
 * 和{@link com.renren.mobile.chat.activity.RenRenChatActivity}有高度的耦合度
 * */
public final class ChatCallbackManager {

	private RenRenChatActivity mChatActivity = null;
	
	public ChatCallbackManager(RenRenChatActivity activity){
		mChatActivity = activity;
	}
	public void init(){
//		mChatActivity.mChatListAdapter.setOnItemLongClickCallback(this.mChatListItem_OnLongClick);
//		mChatActivity.mChatListAdapter.setOnErrorCallback(this.mChatMessage_SendError_Callback);
	}
	
//	
//	/*长按事件回调*/
//	public OnItemLongClickCallback mChatListItem_OnLongClick = new OnItemLongClickCallback() {
//		@Override
//		public void onItemLongClick(ChatMessageWarpper item) {
//			mChatActivity.mLongClickDialogProxy.updateModelSelect(item);
//			mChatActivity.mLongClickDialogProxy.show();
//		}
//	};
//	/*错误重发回调*/
//	public OnErrorCallback mChatMessage_SendError_Callback = new OnErrorCallback() {
//		@Override
//		public void onErrorCallback(ChatMessageWarpper item) {
//			mChatActivity.mResendDialog.update(item);
//		}
//	};
	
	/*按下条目事件回调*/
	
	
}

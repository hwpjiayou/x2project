package com.renren.mobile.x2.components.chat.util;

import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;

import android.view.View;
import android.widget.ProgressBar;

/**
 * @author dingwei.chen
 * @说明 聊天主界面绘制管理器
 * 和{@link com.renren.mobile.chat.activity.RenRenChatActivity}有高度的耦合度
 * */
public final class ChatPaintThread extends Thread {

	public static final int PAINT_SLEEP_TIME = 200;//绘制时间中断时间
	private RenRenChatActivity mActivity = null;
	private boolean mIsStop = false;
	public ChatPaintThread(RenRenChatActivity activity){
		mActivity = activity;
		View view = null;
//		AnimatedRotateDrawable drawable;
		ProgressBar bar;
	}
	
	public void stopPaint(){
		mIsStop = true;
	}
	public void run(){
		while(!mIsStop){
			if(mActivity.mRoot_ChatList!=null){
				int index = mActivity.mRoot_ChatList.getFirstVisiblePosition();
				if(index!=0){
					index--;
				}
				int count = mActivity.mRoot_ChatList.getListItemCount();
				for(int i = index;i<=index+count && i<mActivity.mChatListAdapter.getCount();i++){
					ChatMessageWarpper message = ((ChatMessageWarpper)mActivity.mChatListAdapter.getItem(i));
					if(message.isReflash()){
						mActivity.mHandler.removeCallbacks(mUpdateListViewMessage);
						mActivity.mHandler.post(mUpdateListViewMessage);
					}
				}
			}
			if(ScrollingLock.isScrolling){
				synchronized (ScrollingLock.lock) {
					try{
						ScrollingLock.lock.wait();
					}catch (InterruptedException e) {
						// TODO: handle exception
					}
				}
			}
			try {
				Thread.sleep(PAINT_SLEEP_TIME);
			} catch (InterruptedException e) {}
		}
	}
	
	public Runnable mUpdateListViewMessage = new Runnable() {
		public void run() {
			if(mActivity.mRoot_ChatList!=null){
				int index = mActivity.mRoot_ChatList.getFirstVisiblePosition();
				if(index!=0){
					index--;
				}
				int count = mActivity.mRoot_ChatList.getListItemCount();
				for(int i = index;i<=index+count && i<mActivity.mChatListAdapter.getCount();i++){
					
					ChatMessageWarpper message = ((ChatMessageWarpper)mActivity.mChatListAdapter.getItem(i));
					if(message.isReflash()){
						message.reflash();
					}
				}
			}
		}
	};
	
	
}

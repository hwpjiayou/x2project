package com.renren.mobile.x2.components.chat.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.renren.mobile.x2.core.xmpp.node.Message;


public class ChatMessageWarpper_Null extends ChatMessageWarpper {

	int mIndex = 0;
	
	public ChatMessageWarpper_Null(){
		this.mMessageType = ChatBaseItem.MESSAGE_TYPE.NULL;
		this.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.NULL;
	}
	
	@Override
	public void download(boolean isForceDownload) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<OnLongClickCommandMapping> getOnClickCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onErrorCode() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescribe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void swapDataFromXML(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReflash() {
		return this.hasNewsFeed();
	}
	
	Map<String,Object> mData = new HashMap<String,Object>(1);
	
	boolean mUp = true;
	boolean flag = true;
	@Override
	public void reflash() {
		if(!this.hasNewsFeed()){
			return;
		}
		this.observerUpdate(Subject.COMMAND.COMMAND_UPDATE_BACKGROUND, obtainData(mIndex));
		if(mUp){
			if((mIndex+=50)>=255){
				mIndex = 255;
				mUp = false;
			}
		}else{
			if((mIndex-=50)<=0){
				mIndex = 0;
				mUp = true;
			}
		};
		if(flag){
//			ThreadPool.obtain().removeCallbacks(mReflashRunnable);
//			ThreadPool.obtain().executeMainThread(mReflashRunnable, 100);
		}
		flag = false;
	}
	
	public Runnable mReflashRunnable  = new Runnable() {
		@Override
		public void run() {
			reflash();
			flag = true;
		}
	};
	
	
	private Map<String,Object> obtainData(int data){
		mData.put(Subject.DATA.COMMAND_UPDATE_BACKGROUND, data);
		return mData;
	}
	public void deleteFeed(){
//		this.setNewsFeedModel(null);
		if(mListener!=null){
			mListener.onDeleteFeed();
			mListener = null;
		}
	}
	public static interface OnFeedDeleteListener{
		public void onDeleteFeed();
	}
	OnFeedDeleteListener mListener = null;
	public void setOnFeedDeleteListener(OnFeedDeleteListener listener){
		mListener = listener;
	}
	
	
	
}

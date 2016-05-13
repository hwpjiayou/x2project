package com.renren.mobile.x2.network.talk.binder;

import com.renren.mobile.x2.network.talk.messagecenter.ConnectionManager;
import com.renren.mobile.x2.network.talk.messagecenter.base.IMessage;

/*发送器*/
final class Sender{
	private static Sender sInstance = new Sender();
	private Sender(){}
	public static Sender getInstance(){
		return sInstance;
	}
	
	public void send(long key, String message){
        ConnectionManager.getInstance().sendMessage(new SendMessageCallback(key, message));
	}
	static class SendMessageCallback extends IMessage{
		private long mKey = -1L;
		private String mContent =null;
		public SendMessageCallback(long key,String content){
			this.mKey = key;
			this.mContent = content;
		}
		
		@Override
		public String getContent() {
			return this.mContent;
		}
		@Override
		public long getKey() {
			return mKey;
		}
    }
	
	
}

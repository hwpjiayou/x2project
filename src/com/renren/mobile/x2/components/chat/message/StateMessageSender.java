package com.renren.mobile.x2.components.chat.message;

import com.renren.mobile.x2.components.chat.net.ChatMessageSender;
import com.renren.mobile.x2.components.chat.util.StateMessageModel;


public final class StateMessageSender {

	private static StateMessageSender sInstance = new StateMessageSender();
	private StateMessageSender(){}
	StateMessageModel mBackModel = new StateMessageModel();
	public static StateMessageSender getInstance(){
		return sInstance;
	}
	/**
	 * @param type {@link StateMessageModel.STATE_TYPE}
	 * */
	public void send(String fName,long fromId,long toId,String type) {
		StateMessageModel model = this.obtainModel(fName, fromId,toId, type);
		ChatMessageSender.getInstance().sendMessageToNet(fromId,toId,model);
	}
	public StateMessageModel obtainModel(String fName,long fromId,long toId,String type){
		mBackModel.update(fName, fromId, toId, type);
		return mBackModel;
	}
}

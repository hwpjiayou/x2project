package com.renren.mobile.x2.components.chat.net;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.actions.base.Action;
import com.renren.mobile.x2.actions.base.ActionNotMatchException;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageFactory;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.notification.ChatNotificationManager;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.utils.BackgroundUtils;
import com.renren.mobile.x2.utils.RRSharedPreferences;
import com.renren.mobile.x2.utils.log.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * 负责接收聊天消息
 * */
public class PushSingleChat extends Action<Message> {

	public PushSingleChat() {
		super(Message.class);
	}

	List<ChatMessageWarpper> messages = null;
	RRSharedPreferences rrsp = new RRSharedPreferences(RenrenChatApplication.getApplication());
	
	public void processAction(Message node) {
		if(Logger.mDebug){
			Logger.errord("recevice message value="+node.mValue);
		}
		beginAction();
		parserSingleMessage(node);
		commitAction();
	}
	
	private void parserSingleMessage(Message node){
		ChatMessageWarpper message = ChatMessageFactory.getInstance().obtainMessage(node.mBody.type);
		this.basicParser(message, node);
		message.swapDataFromXML(node);
		if(Logger.mDebug){
			Logger.logd("recevice message type="+message.getMessageType()+"#conment="+message.getMessageContent());
		}
		messages.add(message);
	}
	
	private void basicParser(ChatMessageWarpper message,Message m){
		message.mUserName = m.getFromName();
		message.mLocalUserId = m.getToId();
		message.mToChatUserId = m.getFromId();
		message.mComefrom= ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL;
		message.mMessageKey = m.getMsgKey();
//		message.mDomain = m.getFromDomain();
		if(m.isOffline()){
			message.mMessageReceiveTime = getTime(m.mTime);
			message.mIsOffline = true;
		}
		if(m.isSyn()){
			message.mMessageReceiveTime = getTime(m.mTime);
			message.mIsSyn = true;
		}
		if(message.mToChatUserId==Long.parseLong(LoginManager.getInstance().getLoginInfo().mUserId)){
			long id = message.mToChatUserId;
			message.mToChatUserId = message.mLocalUserId;
			message.mLocalUserId = id;
			message.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT;
		}
	
	}
	
	public long getTime(String time){
		try {
			return Long.parseLong(time);
		} catch (Exception e) {
			if(Logger.mDebug){
				
			}
			return -1L;
		}
	}
	
	@Override
	public boolean checkActionType(Message node) throws ActionNotMatchException{
		return node!=null &&
				"chat".equals(node.mType) && 
				!"action".equals(node.mBody.type);
	}
	
	boolean mIsRunForeground;
	
	public void beginAction() {
		messages = new ArrayList<ChatMessageWarpper>();
		mIsRunForeground = BackgroundUtils.getInstance().isAppOnForeground();
	};
	
	public void commitAction() {
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			public void run() {
				ChatNotificationManager.getInstance()
						.handleNewMessage(messages);
			}
		});
	}

	@Override
	public void batchProcessAction(List<Message> nodeList) {
		if(Logger.mDebug){
			Logger.errord("recevice message size="+nodeList.size());
		}
		//Logger.l(nodeList.toArray());
		beginAction();
		
		for (Message message : nodeList) {
			parserSingleMessage(message);
		}
		
		commitAction();
	}

}

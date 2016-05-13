package com.renren.mobile.x2.service;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.renren.mobile.x2.actions.ActionDispatcher;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.network.talk.NetRequestsPool;

public class PushMessagesReceiver extends BroadcastReceiver{

	
	public static final String ACTION_NAME =  ".push.PushMessages";
	public static final String DATA = "message_content";
	public static final String TYPE = "type";
	public static final int GET_MESSAGE = 1;
	public static final int MESSAGE_FAILED = 2;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		switch (intent.getIntExtra(TYPE, -1)) {
		case GET_MESSAGE:
			ArrayList<XMPPNode> list = (ArrayList<XMPPNode>)intent.getSerializableExtra(DATA);
			ActionDispatcher.batchDispatchAction(list);
			break;
		case MESSAGE_FAILED:
			ArrayList<Long> messageList = (ArrayList<Long>) intent.getSerializableExtra(DATA);
			for(long msgKey : messageList){
				NetRequestsPool.getInstance().onError(msgKey, "");
			}
		default:
			break;
		}
	}
}

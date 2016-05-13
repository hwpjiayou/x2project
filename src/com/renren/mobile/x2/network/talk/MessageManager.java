package com.renren.mobile.x2.network.talk;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.network.talk.binder.LocalBinder;
import com.renren.mobile.x2.network.talk.binder.MessageState;
import com.renren.mobile.x2.network.talk.binder.PollBinder;
import com.renren.mobile.x2.service.RemoteService;

/**
 * @author dingwei.chen
 * @description 消息管理
 * */
public class MessageManager{

	private static boolean sIsBinder = false;
	
	public static void sendMessage(NetRequestListener request){
			NetRequestsPool.getInstance().sendNetRequest(request);
	}
//
//	public static <T extends IReqeustConstructor> void sendSingleMessage(long fromId,long toId, String domain,OnSendTextListener listener,Class<T> clazz){
//		NetRequestListener request = RequestConstructorDicProxy.getInstance(clazz).sendSynMessage(fromId, toId, domain, listener);
//		sendMessage(request);
//	}
//
//	/**
//	 * @author dingwei.chen
//	 * @说明 新接口采用的是异步方式发送
//	 * */
//	public static <T extends IReqeustConstructor> void sendGroupMessage(String fromName,long fromId,long roomId,OnSendTextListener listener,Class<T> clazz){
//		NetRequestListener request = RequestConstructorDicProxy.getInstance(clazz).sendGroupMessage(fromName, fromId, roomId, listener);
//		sendMessage(request);
//	}
	
	/**
	 * @author dingwei.chen
	 * @说明 发送聊天报文监听器
	 * */
	public static interface OnSendTextListener {
		public void onSendTextPrepare();//发送文本准备(未进行网络请求)
		public void onSendTextSuccess();//发送文本成功
		public void onSendTextError();//发送文本失败
		public boolean hasNewsFeed();
		public List<XMPPNode>  getNetPackage();
		public static interface SEND_TEXT_STATE{
				static final int SEND_PREPARE = MessageState.SEND_STATE.SEND_ERROR;
				static final int SEND_OVER =  MessageState.SEND_STATE.SEND_OVER;
		}
	}

	public static void startService(){
		Context context = RenrenChatApplication.getApplication();
		Class<?> serviceClazz = RemoteService.class;
		if(sIsBinder && LocalBinder.getInstance().isContainBinder()){
			context.startService(new Intent(context, serviceClazz));
			return;
		}
		sIsBinder = true;
		Intent i = new Intent(context, serviceClazz);
		context.bindService(i, new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName arg0) {}
			@Override
			public void onServiceConnected(ComponentName arg0, IBinder binder) {
				LocalBinder.getInstance().push(PollBinder.Stub.asInterface(binder));
			}
		},  Context.BIND_AUTO_CREATE);
	}
	
}

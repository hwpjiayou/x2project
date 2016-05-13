package com.renren.mobile.x2.components.chat.util;

import java.util.List;

import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener;




/**
 * @author dingwei.chen
 * @说明 状态消息模型
 * {@code
<message fname='陈定伟 Zero' from='457901640@talk.m.renren.com' to='234744463@talk.m.renren.com' type='chat' msgkey='22493080833831546'>
<body action='canceled' type='action' />
</message>
 * }
 * */
public class StateMessageModel implements OnSendTextListener{
	public long mFromId;
	public long mToId;
	public String mStateType;
	public int ack;
	public String mFromName;
	
	public static interface STATE_TYPE{
		public String TYPING = "typing";//正在输入
		public String SPEEKING = "speeking";//正在录音
		public String CANCELED = "canceled";//取消
	}

	public StateMessageModel() {}
	/**
	 * type:标识聊天消息通知类型
	 * typing|speeking|canceled
	 * */
	public StateMessageModel(String fName,int fromId,int toId,String type) {
		this.mFromName = fName;
		this.mFromId = fromId;
		this.mToId = toId;
		mStateType = type;
	}
	
	public void update(String fName,long fromId,long toId,String type){
		this.mFromName = fName;
		this.mFromId = fromId;
		this.mToId = toId;
		mStateType = type;
	}
	
	
	@Override
	public void onSendTextPrepare() {}

	@Override
	public void onSendTextSuccess() {}

	@Override
	public void onSendTextError() {}

	@Override
	public List<XMPPNode> getNetPackage() {
//		List<XMPPNode> list = new LinkedList<XMPPNode>();
//		list.add(C_NetPackageBuilder.getInstance().build(this));
//		return list;
		return null;
	}
	@Override
	public boolean hasNewsFeed() {
		// TODO Auto-generated method stub
		return false;
	}
	public long getFromId() {
		// TODO Auto-generated method stub
		return this.mFromId;
	}
	public long getToId() {
		// TODO Auto-generated method stub
		return mToId;
	}
}

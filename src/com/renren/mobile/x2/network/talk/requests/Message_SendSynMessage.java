package com.renren.mobile.x2.network.talk.requests;

import android.text.TextUtils;

import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.action.ACTION_TYPE;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.core.xmpp.node.XMPPNodeFactory;
import com.renren.mobile.x2.network.talk.DomainUrl;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener;
import com.renren.mobile.x2.network.talk.NetRequestListener;

/**
 * @author dingwei.chen
 * @说明 单聊请求
 * */
public class Message_SendSynMessage extends NetRequestListener{
	
	private OnSendTextListener mMessage = null;
	private long mFromId = -1L;
	private long mToId = -1L;
	private String mToDomain = null;
	private String mFromDomain = null;
	
	public Message_SendSynMessage(long fromId,long toId, String domain, OnSendTextListener message){
		this(fromId,toId,domain, null, message);
	}
	
	public Message_SendSynMessage(long fromId,long toId, String domain, String fromDomain, OnSendTextListener message){
		this.mFromId = fromId;
		this.mToId = toId;
		mMessage = message;
		mToDomain = domain;
		mFromDomain = fromDomain;
	}
	
	public Message_SendSynMessage(long fromId,long toId,OnSendTextListener message){
		this(fromId,toId,null, message);
	}
	
	@Override
	public void onNetError(String errorMsg) {
		mMessage.onSendTextError();
	}

	@Override
	public void onNetSuccess() {
		mMessage.onSendTextSuccess();
	}

	@Override
	public String getSendNetMessage() {
		XMPPNode root = XMPPNodeFactory.obtainRootNode(ACTION_TYPE.MESSAGE.TypeName,
				this.mFromId+"@"+(TextUtils.isEmpty(mFromDomain) ? LoginManager.getInstance().getLoginInfo().mDomainName : mFromDomain), 
				this.mToId+"@"+(TextUtils.isEmpty(mToDomain) ? DomainUrl.SIXIN_DOMAIN : mToDomain),
				getId());
		root.addAttribute("type", "chat");
		if(mMessage.hasNewsFeed()){
			root.addAttribute("feed", "true");
		}
		if(mMessage.getNetPackage()!=null){
			for(XMPPNode node:mMessage.getNetPackage()){
				root.addChildNode(node);
			}
		}
		return root.toXMLString();
	}

	@Override
	public void onSuccessRecive(String data) {}

}

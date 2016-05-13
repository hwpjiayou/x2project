package com.renren.mobile.x2.network.talk.requests;

import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.action.ACTION_TYPE;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.core.xmpp.node.XMPPNodeFactory;
import com.renren.mobile.x2.network.talk.DomainUrl;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener;
import com.renren.mobile.x2.network.talk.NetRequestListener;
import com.renren.mobile.x2.utils.CommonUtil;

/**
 * @author dingwei.chen1988@gmail.com
 * */
public class Message_SendGroupChat extends NetRequestListener {

	private long mFromId = -1L;
	private long mRoomId = -1L;
	private String mFromName = null;
	private String mDomain = null;
	private OnSendTextListener mMessage = null;

	public Message_SendGroupChat(String fromName, long fromId, long roomId,
			OnSendTextListener listener) {
		this(fromName, fromId, roomId, null, listener);
	}

	public Message_SendGroupChat(String fromName, long fromId, long roomId,
			String domain, OnSendTextListener listener) {
		mFromName = fromName;
		this.mFromId = fromId;
		this.mRoomId = roomId;
		this.mMessage = listener;
		this.mDomain = domain;
	}

	@Override
	public String getSendNetMessage() {
		XMPPNode root = XMPPNodeFactory.obtainRootNode(
				ACTION_TYPE.MESSAGE.TypeName,
				this.mFromId
						+ "@"
						+ (mDomain == null ? LoginManager.getInstance()
								.getLoginInfo().mDomainName : mDomain),
				mRoomId + "@" + DomainUrl.MUC_URL, getId());
		root.addAttribute("type", "groupchat");
		root.addAttribute("fname", mFromName);
		if (mMessage.hasNewsFeed()) {
			root.addAttribute("feed", "true");
		}
		if (mMessage.getNetPackage() != null) {
			for (XMPPNode node : mMessage.getNetPackage()) {
				root.addChildNode(node);
			}
		}
		return root.toXMLString();
	}

	@Override
	public void onNetSuccess() {
		this.mMessage.onSendTextSuccess();
	}

	@Override
	public void onNetError(String errorMsg) {
		this.mMessage.onSendTextError();
		CommonUtil.toast(errorMsg);
	}

	@Override
	public void onSuccessRecive(String data) {	
	}

}

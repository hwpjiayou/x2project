package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Error extends XMPPNode{

	private static final long serialVersionUID = 7692752579183479872L;

	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="type")
	public String mType;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="code")
	public String mCode;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="msg")
	public String mMsg;
	
	@Override
	public String getNodeName() {
		return "error";
	}

}

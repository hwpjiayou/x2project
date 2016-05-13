package com.renren.mobile.x2.core.xmpp.node.base;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

public class Success extends XMPPNode{
	private static final long serialVersionUID = -1655369849946117395L;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE, Name="id")
	public String id;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE, Name="from")
	public String from;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE, Name="to")
	public String to;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE, Name="type")
	public String type;

	@Override
	public String getNodeName() {
		return "success";
	}

	@Override
	public String getId() {
		return id;
	}
	
	
}

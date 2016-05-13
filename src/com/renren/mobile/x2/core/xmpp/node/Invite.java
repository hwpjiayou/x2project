package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Invite extends XMPPNode{

	private static final long serialVersionUID = -7204735327245471448L;

	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="from")
	public String mFrom = null;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="totype")
	public String mToType = null;
	@Override
	public String getNodeName() {
		return "invite";
	}

}

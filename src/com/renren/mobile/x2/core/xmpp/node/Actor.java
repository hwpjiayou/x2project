package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Actor extends XMPPNode {

	private static final long serialVersionUID = -2726044905958027333L;
	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="nick")
	public String mNic = null;
	
	@Override
	public String getNodeName() {
		return "actor";
	}

}

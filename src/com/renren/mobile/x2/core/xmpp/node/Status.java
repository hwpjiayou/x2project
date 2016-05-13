package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Status extends XMPPNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4555739551706147189L;
	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="code")
	public String mCode = null;
	
	@Override
	public String getNodeName() {
		return "status";
	}

}

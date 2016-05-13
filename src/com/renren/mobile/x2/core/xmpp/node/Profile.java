package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Profile extends XMPPNode{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7911238277967924209L;
	@XMLMapping(Type= XMLType.ATTRIBUTE, Name="type")
	public String mType;
	
	@Override
	public String getNodeName() {
		return "profile";
	}

}

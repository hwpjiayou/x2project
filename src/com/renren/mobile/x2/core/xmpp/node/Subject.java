package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Subject extends XMPPNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = -398514426459073546L;
	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="version")
	public String mVersion = null;
	@Override
	public String getNodeName() {
		return "subject";
	}
	
	public int getVersion(){
		try {
			return Integer.parseInt(mVersion);
		} catch (Exception e) {
			return -1;
		}
	}

}

package com.renren.mobile.x2.core.xmpp.node;


import java.util.LinkedList;
import java.util.List;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Contact extends XMPPNode {

	private static final long serialVersionUID = -4883553475607571713L;

	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="prefix")
	public String mPrefix ;
	
	@XMLMapping(Type=XMLType.NODE,Name="item",isIterable=true)
	public List<Item> mItems = new LinkedList<Item>();
	
	
	@Override
	public String getNodeName() {
		return "contact";
	}

}

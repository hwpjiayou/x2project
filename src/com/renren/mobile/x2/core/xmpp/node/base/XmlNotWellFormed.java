package com.renren.mobile.x2.core.xmpp.node.base;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.core.xmpp.node.childs.Text;

public class XmlNotWellFormed extends XMPPNode{

	private static final long serialVersionUID = 7229231767897320765L;
	
	@XMLMapping(Type=XMLType.NODE, Name="text")
	public Text text;

	@Override
	public String getNodeName() {
		return "xml-not-well-formed";
	}

}

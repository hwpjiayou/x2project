package com.renren.mobile.x2.core.xmpp.node;


import java.util.LinkedList;
import java.util.List;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Query extends XMPPNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4799373464990219688L;

	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="xmlns")
	public String mXmlns = null;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="prefix")
	public String mPrefix = null;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="version")
	public String mVersion = null;
	
	@XMLMapping(Type=XMLType.NODE,Name="subject")
	public Subject mSubjectNode = null;
	
	@XMLMapping(Type=XMLType.NODE,Name="item",isIterable=true)
	public List<Item> mItems = new LinkedList<Item>();
	
	@XMLMapping(Type=XMLType.NODE,Name="contact")
	public Contact mContactNode ;
	
	@Override
	public String getNodeName() {
		return "query";
	}
	public int getVersion(){
		try {
			return Integer.parseInt(mVersion);
		} catch (Exception e) {
			return -1;
		}
		
	}
}

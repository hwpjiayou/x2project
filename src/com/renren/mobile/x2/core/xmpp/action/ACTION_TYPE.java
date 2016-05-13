package com.renren.mobile.x2.core.xmpp.action;

public enum ACTION_TYPE {

	MESSAGE("message"),
	PRESENCE("presence"),
	IQ("iq"),
	;
	public String TypeName = null;
	ACTION_TYPE(String typeName){
		this.TypeName = typeName;
	}
}

package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Check extends XMPPNode{

	private static final long serialVersionUID = 6054162917880376584L;

	public static interface IS_MEMEBER{
		int TRUE 	=	0;
		int FLASE 	=	1;
		int NOROOM 	=	2;
	}
	
	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="from")
	public String mFrom;
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="update")
	public String mUpdate;
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="isMember")
	public String mIsMember;
	
	@Override
	public String getNodeName() {
		return "check";
	}
	
	public boolean getUpdate(){
		if(mUpdate.equals("true")){
			return true;
		}
		return false;
	}
	
	public boolean getIsMember(){
		if(mIsMember.equals("true")){
			return true;
		}
		return false;
	}
	
//	public long getRoomId(){
//		return parseLong(mFrom);
//	}

}

package com.renren.mobile.x2.core.xmpp.node;


import java.util.LinkedList;
import java.util.List;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Item extends XMPPNode{
	
	private static final long serialVersionUID = -7675919817216511738L;

	public static enum AFFILIATION{
		member (0),
		owner(1),
		outcast(2),
		;public int Value = 0;
		
		AFFILIATION(int v){
			this.Value =v;
		}
	}
	
	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="affiliation")
	public String mAffiliation = null;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="jid")
	public String mJid = null;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="name")
	public String mName = null;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="headurl")
	public String mHeadUrl = null;
	
	/* 高清头像 */
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="url")
	public String mUrl = null;
	
//	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="isFriend")
//	public String mIsFriend = null;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="rflag")
	public int mRflag;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="nick")
	public String mNickid = null;
	
	@XMLMapping(Type=XMLType.NODE,Name="actor")
	public Actor mActor;
	
	@XMLMapping(Type=XMLType.NODE,Name="item",isIterable=true)
	public List<Item> mItems = new LinkedList<Item>();
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="roomId")
	public String mRoomId;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="roomname")
	public String mRoomName;
	
	
	@Override
	public String getNodeName() {
		return "item";
	}
	
	public int getAffiliaction(){
		if(mAffiliation.equals("owner")){
			return AFFILIATION.owner.Value;
		}else{
			return AFFILIATION.member.Value;
		}
	}
//
//	public long getJid() {
//		return parseLong(mJid);
//	}
//	
//	public long getNickId(){
//		return parseLong(mNickid);
//	}

	
}

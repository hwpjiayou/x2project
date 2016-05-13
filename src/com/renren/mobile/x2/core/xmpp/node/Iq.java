package com.renren.mobile.x2.core.xmpp.node;

import com.renren.mobile.x2.core.xmpp.Utils;
import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

/**
 * @author dingwei.chen1988@gmail.com
 * @说明 作为根节点
 * */
public class Iq extends XMPPNode {

	private static final long serialVersionUID = -1296144744151404746L;

	public enum TYPE{
		DELETE_MEMBER_SUCCESS,//删除用户成功
		DELETE_MEMBER_ERROR,//删除用户失败
		
		QUERY_ROOM_MEMBER_SUCCESS,//查看群消息
		QUERY_ROOM_MEMBER_ERROR,
		
		SAVE_ROOM_SUCCESS,
		SAVE_ROOM_ERROR,
		
		QUERY_ROOM_SUCCESS,
		QUERY_ROOM_ERROR,
	}
	
	
	@XMLMapping(Type= XMLType.NODE,Name="query")
	public Query mQueryNode = null;

    @XMLMapping(Type = XMLType.NODE, Name = "z")
	public Z mZNode;
	
	
	
	@Override
	public String getNodeName() {
		return "iq";
	}

	@Override
	public String toString() {
		return Utils.showAllFields(0, this);
	}
	

}

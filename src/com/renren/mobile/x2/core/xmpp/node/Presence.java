package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;

public class Presence extends XMPPNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7202468424796324019L;


	public enum TYPE{
		INVITE,//邀请进入房间
		ERROR,//失败
		
		CREATE_ROOM_MASTER,//群主
		CREATE_ROOM_MEMBER,//群成员
		CREATE_ROOM_ERROR,//创建房间错误
		CREATE_ROOM_SUCCESS,//创建房间成功
		
		DELETE_MEMBER,//删除用户
		NOT_BEDEL_MEMBER,//未被删除用户
		
		NOT_QUIT_MEMBER,//未退群的人
		QUIT_ERROR,//退出群失败
		
		
		DESTROY_ROOM_MEMBER,//被销毁房间的群成员
		DESTROY_ROOM_SUCCESS,
		DESTROY_ROOM_ERROR,
		
		UPLOAD_ROOM_TIMESTAMP_SUCCESS,
		UPLOAD_ROOM_TIMESTAMP_ERROR,
		
	}
	
	@XMLMapping(Type= XMLType.NODE,Name="x")
	public X mXNode = null;

    @XMLMapping(Type = XMLType.NODE, Name = "z")
	public Z mZNode;
	
	
	@Override
	public String getNodeName() {
		return "presence";
	}

}

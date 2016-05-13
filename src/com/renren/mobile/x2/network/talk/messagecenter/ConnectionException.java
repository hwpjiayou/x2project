package com.renren.mobile.x2.network.talk.messagecenter;

public class ConnectionException extends Exception{
	private static final long serialVersionUID = -3373091015193093620L;
	
	public static final int GET_ERROR_DATA 		= 1;		//获取到连接错误的节点
	public static final int GET_TERMINATE_NODE 	= 2;		//获取到terminate节点(HTTP) 或者 error节点(Socket)
	public static final int GET_EMPTY_DATA 		= 3;		//获取不到想要的数据
	
	public int ExceptionDetail;

	public ConnectionException() {
		super();
	}

	public ConnectionException(int exceptionDetail) {
		super();
		ExceptionDetail = exceptionDetail;
	}

}

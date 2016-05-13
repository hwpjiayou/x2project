package com.renren.mobile.x2.network.talk;

public enum MessageType {

	text		(MESSAGE_TYPE.TEXT),
	voice		(MESSAGE_TYPE.VOICE),
	action		(MESSAGE_TYPE.STATUS),
	image		(MESSAGE_TYPE.IMAGE),
	lbs			(MESSAGE_TYPE.LBS),
	expression	(MESSAGE_TYPE.FLASH),
	info		(MESSAGE_TYPE.SOFT_INFO),
	NULL		(MESSAGE_TYPE.NULL),
	unknow		(MESSAGE_TYPE.UNKNOW),
	;
	int TYPE;
	MessageType(int type){
		this.TYPE = type;
	}
	public static interface MESSAGE_TYPE{
		final int TEXT = 0;//文本消息
		final int VOICE = 1;//声音消息
		final int STATUS = 2;//状态消息
		final int IMAGE = 3;//图片消息
		final int LBS = 4;//POI消息
		final int FLASH=6;//闪图消息
		final int SOFT_INFO=7;//弱消息提醒
		final int NULL=10;//空消息
		final int UNKNOW = 11;//未知消息类型(取10是为了扩展)
		
	}
}

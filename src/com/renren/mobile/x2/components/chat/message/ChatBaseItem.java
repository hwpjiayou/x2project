package com.renren.mobile.x2.components.chat.message;

import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.db.table.ChatHistory_Column;

/**
 * 
 * @author dingwei.chen
 * @说明 聊天基础POJO数据模型,不含有任何侵入性类和功能方法
 * @说明 为了配合ORM映射,属性采用public标记 对数据库中的对应列进行注入标记
 * like:
 * 	@ORM(mappingColumn=ChatHistory_Column.LOCAL_USER_ID)
 * 	public long mLocalUserId = 0L;//本地用户ID
 * */
public abstract class ChatBaseItem {

	/**
	 * 消息的类型接口
	 */
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
	/**
	 * 消息的流向
	 * */
	public static interface MESSAGE_COMEFROM{
		final int OUT_TO_LOCAL= 0 ;//从服务器发送到本地的消息
		final int LOCAL_TO_OUT= 1 ;//从本地发送到服务器的消息
		final int NULL = -1;
		final int NOTIFY = 2;
	}
	
	public static interface MESSAGE_READ{
		int READ = 1;
		int UNREAD = 0;
	}
	public static interface MESSAGE_ISGROUP{
		int IS_GROUP = 0;//群消息
		int IS_SINGLE= 1;//单人聊天消息
	}
	
	/**
	 * 公共数据
	 * */
	@ORM(mappingColumn=ChatHistory_Column._ID,isInsert=false)
	public int mMessageId = 0;//消息的ID号
	
	@ORM(mappingColumn=ChatHistory_Column.MESSAGE_TYPE)
	public int mMessageType = 0;//在接口 <ChatBaseItem.MESSAGE_TYPE>中取值
	
	@ORM(mappingColumn=ChatHistory_Column.LOCAL_USER_ID)
	public long mLocalUserId = 0L;//本地用户ID
	
	@ORM(mappingColumn=ChatHistory_Column.MESSAGE)
	public String mMessageContent = null;//消息内容
	
	@ORM(mappingColumn=ChatHistory_Column.TO_CHAT_ID)
	public long mToChatUserId = 0L;
	
	/**
	 * @see com.renren.mobile.x2.components.home.chat.message.chat.base.inter.Subject.COMMAND
	 * */
	@ORM(mappingColumn=ChatHistory_Column.MESSAGE_STATE)
	public int mMessageState = Subject.COMMAND.COMMAND_MESSAGE_OVER;
	
	@ORM(mappingColumn=ChatHistory_Column.MESSAGE_KEY)
	public String mMessageKey = "";
	
	
	/**
	 * @see ChatBaseItem.MESSAGE_COMEFROM
	 * */
	@ORM(mappingColumn=ChatHistory_Column.COME_FROM)
	public int mComefrom = 0;//消息的流向从接口 <ChatBaseItem.MESSAGE_COMEFROM>中取值
	
	@ORM(mappingColumn=ChatHistory_Column.CHAT_TIME)
	public long mMessageReceiveTime = -1L;//接受到消息的时间
	
	/**
	 * @see ChatBaseItem.MESSAGE_READ
	 * */
	@ORM(mappingColumn=ChatHistory_Column.READ)
	public int mRead = MESSAGE_READ.READ;
	/**
	 * 联系人附加信息
	 * */
	@ORM(mappingColumn=ChatHistory_Column.TO_CHAT_NAME)
	public String mUserName = null;//用户名
	
	@ORM(mappingColumn=ChatHistory_Column.HEAD_URL)
	public String mHeadUrl = null;//消息发送者头像链接
	
	public String mLargeHeadUrl;
	
	public int mVersion = -1;
	
	
	
	
}



	
	


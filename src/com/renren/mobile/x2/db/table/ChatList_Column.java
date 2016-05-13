package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.Column;
import com.renren.mobile.x2.core.db.DatabaseColumn;
import com.renren.mobile.x2.core.db.DatabaseTypeConstant;



public interface ChatList_Column extends DatabaseColumn{
	@Column(defineType=DatabaseTypeConstant.INT+" "+DatabaseTypeConstant.PRIMARY)
	public static final String _ID = "_id";
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public static final String MESSAGE_ID = "message_id";
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String LOCAL_USER_ID="local_user_id";//本地登录ID
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String TO_CHAT_ID="to_chat_id";//聊天对方的ID
	
	@Column(defineType=DatabaseTypeConstant.LONG)
	public final String CHAT_TIME = "chat_time";//消息的产生时间
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String TO_CHAT_NAME = "to_chat_name";//消息对象的名字
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String MESSAGE="message_content";//消息内容,如果是语音存储的是消息的URL
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String HEAD_URL="head_url";//头像的URL   对于纯通讯录联系人，存储的为此人的本地头像uri
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String READ = "read";
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String MESSAGE_KEY = "message_key";//去重时候使用
	
	
	/**
	 * 级联到:
	 * @see com.renren.mobile.chat.database.NewsFeed_Table
	 * */
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String FEED_ID = "feed_id";//级联查询 feed_id
	
	/**
	 * 消息的状态
	 * @see com.renren.mobile.chat.base.inter.Subject.COMMAND
	 * */
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String MESSAGE_STATE = "message_state";//消息的状态
	
	/**
	 * 消息的来自的方向
	 * @see com.renren.mobile.chat.base.model.ChatBaseItem.MESSAGE_COMEFROM
	 * */
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String COME_FROM = "come_from";//来自的方向
	
	/**
	 * 消息的类型
	 * @see com.renren.mobile.chat.base.model.ChatBaseItem.MESSAGE_TYPE
	 * */
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String MESSAGE_TYPE = "message_type";
	
	
	/**
	 * 音频数据
	 * */
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String PLAY_TIME = "play_time";//播放时间
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String VOICE_URL = "voice_url";//语音的URL
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String VOICE_ID = "voice_id";//语音的VID(服务器要求)
	/**
	 * 图片数据
	 * */
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String TINY_URL = "tiny_url";//小图
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String MAIN_URL = "main_url";//中图
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String LARGE_URL ="large_url";//大图
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String IS_BRUSH ="is_brush";//大图
}

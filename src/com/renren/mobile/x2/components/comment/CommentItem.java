package com.renren.mobile.x2.components.comment;

import java.io.Serializable;

/**
 * 
 * @author hwp
 * 添加评论封装类。。。
 * 
 */
public  class CommentItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8258016873639198949L;
	/**
	 * 信息的流向
	 */
	public static interface  MESSAGE_COMEFROM{
		final int OUT_TO_LOCAL=0; //从服务器发送到本地的信息
		final int LOCAL_TO_OUT=1; //从本地发送到服务器的信息。
		final int NULL=-1;
		final int NOTIFY=2;
	}
	
	
	public String userName;
	public String userId;
	public String userHeadUrl;
	public String content;
	public String imageLocationUrl;
	public String imageServiceUrl;
	public String action;
	public String place;
	public String tag;
	public String voiceUrl;
	
	
	private int voiceComefrom;
	private String voiceContent;
	private int voicePlayTime;

	
	private String ugc_id;
	
	public long createTime;
	public int type;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserHeadUrl() {
		return userHeadUrl;
	}
	public void setUserHeadUrl(String userHeadUrl) {
		this.userHeadUrl = userHeadUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getVoiceUrl() {
		return voiceUrl;
	}
	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public int getVoiceComefrom() {
		return voiceComefrom;
	}
	public void setVoiceComefrom(int voiceComefrom) {
		this.voiceComefrom = voiceComefrom;
	}
	public String getVoiceContent() {
		return voiceContent;
	}
	public void setVoiceContent(String voiceContent) {
		this.voiceContent = voiceContent;
	}
	public int getVoicePlayTime() {
		return voicePlayTime;
	}
	public void setVoicePlayTime(int voicePlayTime) {
		this.voicePlayTime = voicePlayTime;
	}
	public String getImageLocationUrl() {
		return imageLocationUrl;
	}
	public void setImageLocationUrl(String imageLocationUrl) {
		this.imageLocationUrl = imageLocationUrl;
	}
	public String getImageServiceUrl() {
		return imageServiceUrl;
	}
	public void setImageServiceUrl(String imageServiceUrl) {
		this.imageServiceUrl = imageServiceUrl;
	}
	public String getUgc_id() {
		return ugc_id;
	}
	public void setUgc_id(String ugc_id) {
		this.ugc_id = ugc_id;
	}
	
	
}

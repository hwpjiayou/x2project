package com.renren.mobile.x2.components.home.chatlist;

import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.db.table.ChatList_Column;

public class ChatListDataModel {
	@ORM(mappingColumn=ChatList_Column._ID)
	public long mId;
	@ORM(mappingColumn=ChatList_Column.LOCAL_USER_ID)
	public long mLocalId;
	@ORM(mappingColumn=ChatList_Column.MESSAGE_ID)
	public int mMessageId;
	@ORM(mappingColumn=ChatList_Column.CHAT_TIME)
	public long mLastTime;
	@ORM(mappingColumn=ChatList_Column.MESSAGE)
	public String mChatContent;
	@ORM(mappingColumn=ChatList_Column.TO_CHAT_ID)
	public long mToId;
	@ORM(mappingColumn=ChatList_Column.TO_CHAT_NAME)
	public String mUserName;
	@ORM(mappingColumn=ChatList_Column.COME_FROM)
	public int mComeFrom;
	@ORM(mappingColumn=ChatList_Column.HEAD_URL)
	public String mHeadImg;
	@ORM(mappingColumn=ChatList_Column.MESSAGE_TYPE)
	public int mType;
	
	@ORM(mappingColumn=ChatList_Column.MESSAGE_STATE)
	public int mSendState;
	public int unReadCount;
	public Boolean  sendFail;
	public long getmId() {
		return mId;
	}
	public void setmId(long mId) {
		this.mId = mId;
	}
	public long getmLocalId() {
		return mLocalId;
	}
	public void setmLocalId(long mLocalId) {
		this.mLocalId = mLocalId;
	}
	public int getmMessageId() {
		return mMessageId;
	}
	public void setmMessageId(int mMessageId) {
		this.mMessageId = mMessageId;
	}
	public long getmLastTime() {
		return mLastTime;
	}
	public void setmLastTime(long mLastTime) {
		this.mLastTime = mLastTime;
	}
	public String getmChatContent() {
		return mChatContent;
	}
	public void setmChatContent(String mChatContent) {
		this.mChatContent = mChatContent;
	}
	public long getmToId() {
		return mToId;
	}
	public void setmToId(long mToId) {
		this.mToId = mToId;
	}
	public String getmUserName() {
		return mUserName;
	}
	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}
	public int getmComeFrom() {
		return mComeFrom;
	}
	public void setmComeFrom(int mComeFrom) {
		this.mComeFrom = mComeFrom;
	}
	public String getmHeadImg() {
		return mHeadImg;
	}
	public void setmHeadImg(String mHeadImg) {
		this.mHeadImg = mHeadImg;
	}
	public int getmType() {
		return mType;
	}
	public void setmType(int mType) {
		this.mType = mType;
	}
	public int getmSendState() {
		return mSendState;
	}
	public void setmSendState(int mSendState) {
		this.mSendState = mSendState;
	}
	public int getUnReadCount() {
		return unReadCount;
	}
	public void setUnReadCount(int unReadCount) {
		this.unReadCount = unReadCount;
	}
	public Boolean getSendFail() {
		return sendFail;
	}
	public void setSendFail(Boolean sendFail) {
		this.sendFail = sendFail;
	}
	
}

package com.renren.mobile.x2.components.comment;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * @author hwp
 * 评论内容的数据封装model
 */

public class CommentContent implements Serializable {

	private static final long serialVersionUID = 9122622233383630134L;

	private String mContent;

	private String mHeadUrl;

	private long mId;

	private String mUserName;

	private long mTime;

	private long mUserId;
	
	private String mContentImageUrl;
	
	private String mContentText;

	public CommentContent(JSONObject objs) throws JSONException {
		this.mContent = objs
				.getString(CommentModel.COMMENT_COLUMN_COMMENT_CONTENT);
		this.mHeadUrl = objs
				.getString(CommentModel.COMMENT_COLUMN_COMMENT_HEAD_URL);
		this.mId = objs.getLong(CommentModel.COMMENT_COLUMN_COMMENT_ID);
		this.mUserName = objs
				.getString(CommentModel.COMMENT_COLUMN_COMMENT_USER_NAME);
		this.mTime = objs.getLong(CommentModel.COMMENT_COLUMN_COMMET_TIME);
		this.mUserId = objs.getLong(CommentModel.COMMENT_COLUMN_COMMENT_USER_ID);
	    this.mContentImageUrl=objs.getString(CommentModel.COMMENT_COLUM_COMMENT_CONTENT_IMG_URL);
	    this.mContentText=objs.getString(CommentModel.COMMENT_COLUM_COMMENT_CONTENT_TEXT);
	
	}

	public String getmContent() {
		return mContent;
	}

	public void setmContent(String mContent) {
		this.mContent = mContent;
	}

	public String getmHeadUrl() {
		return mHeadUrl;
	}

	public void setmHeadUrl(String mHeadUrl) {
		this.mHeadUrl = mHeadUrl;
	}

	public long getmId() {
		return mId;
	}

	public void setmId(long mId) {
		this.mId = mId;
	}

	public String getmUserName() {
		return mUserName;
	}

	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public long getmTime() {
		return mTime;
	}

	public void setmTime(long mTime) {
		this.mTime = mTime;
	}

	public long getmUserId() {
		return mUserId;
	}

	public void setmUserId(long mUserId) {
		this.mUserId = mUserId;
	}

	public String getmContentImageUrl() {
		return mContentImageUrl;
	}

	public void setmContentImageUrl(String mContentImageUrl) {
		this.mContentImageUrl = mContentImageUrl;
	}

	public String getmContentText() {
		return mContentText;
	}

	public void setmContentText(String mContentText) {
		this.mContentText = mContentText;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    

	
}

package com.renren.mobile.x2.proxy;

import android.content.Intent;

/**
 * @author dingwei.chen
 * */
public class UserInfo {

	public String mHeadUrl = null;
	public String mUserName = null;
	public long mUserId = 0l;
	
	public UserInfo(){}
	
	public UserInfo(Intent intent){
		this.mHeadUrl = intent.getStringExtra(NEED_PARAMS.HEAD_URL);
		this.mUserName = intent.getStringExtra(NEED_PARAMS.USER_NAME);
		this.mUserId = intent.getLongExtra(NEED_PARAMS.USER_ID, -1l);
	}
	
	public UserInfo(
			String headUrl, 
			String username, 
			long userId) {
		this.mHeadUrl = headUrl;
		this.mUserName = username;
		this.mUserId = userId;
	}
	public static interface NEED_PARAMS{
		String HEAD_URL	="userinfo_head_url";
		String USER_NAME="userinfo_user_name";
		String USER_ID 	="userinfo_userid";
	}
	
}

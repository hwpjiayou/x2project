package com.renren.mobile.x2.network.mas;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 头像部分将会做调整，服务端将会做调整
 */
public class UGCUserModel extends UGCModel {

	public static final long serialVersionUID = 6039666707158990871L;
	
	public String mName;
	public String mId;
	public int mGender;
	/**
	 * 男性默认值：1
	 */
	public static final int GENDER_MALE = 1;
	/**
	 * 女性默认值：0
	 */
	public static final int GENDER_FAMALE = 0;
	/**
	 *  200 * 200
	 * */
	public String mSmalHeadlUrl;
	/**
	 *  400 * 400
	 * */
	public String mNormalHeadUrl;
	
	public UGCUserModel(String name,String id,int gender,String headUrl){
		this.mName=name;
		this.mId=id;
		this.mGender=gender;
		this.mSmalHeadlUrl=headUrl;
	}
	
	public UGCUserModel(JSONObject object) {
		super(object);
	}

	@Override
	public UGCUserModel parse(JSONObject object) {
		if (object == null) return this;
		mId = object.optString("id");
		mName = object.optString("name");
		mGender = object.optInt("gender");
		JSONObject headUrl = object.optJSONObject("head_url");
		if (headUrl != null) {
			mSmalHeadlUrl = headUrl.optString("square200");
			mNormalHeadUrl = headUrl.optString("square400");
		}
		return this;
	}

	@Override
	public JSONObject build() {
		JSONObject object = new JSONObject();
		try {		
			object.put("id", mId);
			object.put("name", mName);
			object.put("gender", mGender);
			JSONObject headUrl = new JSONObject();
			headUrl.put("square200", mSmalHeadlUrl);
			headUrl.put("square400", mNormalHeadUrl);
			object.put("header_url", headUrl);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}

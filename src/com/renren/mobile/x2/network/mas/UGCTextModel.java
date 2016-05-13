package com.renren.mobile.x2.network.mas;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * UGC文本Model类, 
 * 
 * @author xingchen.li
 * 
 * */
public class UGCTextModel extends UGCModel {
	
	private static final long serialVersionUID = 5717437094724679374L;
	/**
	 * 文本内容
	 * */
	public String mText;
	
	public UGCTextModel(JSONObject object) {
		super(object);
	}
	
	/**
	 * 传值构造器, 如果需要使用 Model的JSONObject
	 * */
	public UGCTextModel(String content) {
		this.mType = UGCModel.UGCType.TEXT;
		this.mText = content;
	}
	
	public UGCModel update(String text) {
		this.mText = text;
		return this;
	}
	
	@Override
	public UGCTextModel parse(JSONObject object) {
		if (object == null) return this;
		mType = object.optString("type");
		JSONObject content = object.optJSONObject("content");
		if (content != null) 
			mText = content.optString("text");
		return this;
	}

	@Override
	public JSONObject build() {		
		JSONObject object = new JSONObject();
		try {
			object.put("type", mType);
			JSONObject content = new JSONObject();
			content.put("text", mText);
			object.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}	

}

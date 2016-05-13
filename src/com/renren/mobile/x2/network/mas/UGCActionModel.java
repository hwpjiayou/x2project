package com.renren.mobile.x2.network.mas;

import org.json.JSONException;
import org.json.JSONObject;

public class UGCActionModel extends UGCModel {
	
	private static final long serialVersionUID = -6973594557465109316L;
	public String mId ;
	public String mName;
	
	public UGCActionModel(JSONObject object) {
		super(object);
	}
	
	public UGCActionModel(String id, String name) {
		this.mType = UGCModel.UGCType.ACTION;
		this.mId = id;
		this.mName = name;
	}

	@Override
	public UGCActionModel parse(JSONObject object) {
		if (object == null) return this;
		mType = object.optString("type");
		JSONObject content = object.optJSONObject("content");
		if (content != null) {
			mId = content.optString("id");
			mName = content.optString("name");
		}
		return this;
	}

	@Override
	public JSONObject build() {
		JSONObject object = new JSONObject();
		try {
			object.put("type", mType);
			JSONObject content = new JSONObject();
			content.put("id", mId);
			content.put("name", mName);
			object.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}

package com.renren.mobile.x2.network.mas;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * UGC图片Model类, 
 * 
 * @author xingchen.li
 * 
 * */
public class UGCImgModel extends UGCModel {

	private static final long serialVersionUID = -9178866808020311512L;
	
	public String mThumbnailUrl;
	public int mThumbnailWidth;
	public int mThumbnailHeight;
	
	public String mOriginalUrl;
	public int mOriginalWidth;
	public int mOriginalHeight;	
	
	public UGCImgModel(JSONObject object) {
		super(object);
	}
	
	/**
	 * 属性构造器
	 * 参数是对应的是三种URL的url,宽度,高度 
	 * */
	public UGCImgModel(String oUrl, int oW, int oH, String tUrl, int tW, int tH) {		
		this.mType = UGCModel.UGCType.IMG;	
		this.mOriginalUrl = oUrl;
		this.mOriginalWidth = oW;
		this.mOriginalHeight = oH;
		
		this.mThumbnailUrl = tUrl;
		this.mThumbnailWidth = tW;
		this.mThumbnailHeight = tH;

	}
	
	/**
	 * TODO just for test the old interface, to be deleted
	 * test can be everything
	 * */
	public UGCImgModel(JSONObject object, String test) {
		this.parseTest(object);
	}
	
	/**
	 * TODO just for test the old interface, to be deleted
	 * test can be everything
	 * */
	public UGCImgModel update(JSONObject object, String test) {
		return this.parseTest(object);
	}
	/**
	 * TODO just for test the old interface, to be deleted
	 * */
	public UGCImgModel parseTest(JSONObject object) {
		if (object != null) {
			mType = UGCModel.UGCType.IMG;

			JSONObject original = object.optJSONObject("original");
			if (original != null) {
				mOriginalUrl = original.optString("url");
				mOriginalWidth = original.optInt("width");
				mOriginalHeight = original.optInt("height");
			}
					
			JSONObject thumbnail = object.optJSONObject("thumbnail");
			if (thumbnail != null) {
				mThumbnailUrl = thumbnail.optString("url");
				mThumbnailWidth = thumbnail.optInt("width");
				mThumbnailHeight = thumbnail.optInt("height");
			}
		}
		return this;
	}
	
	@Override
	public UGCImgModel parse(JSONObject object) {
		if (object == null) return this;
		mType = object.optString("type");
		JSONObject content = object.optJSONObject("content");
		
		JSONObject original = content.optJSONObject("original");
		if (original != null) {
			mOriginalUrl = original.optString("url");
			mOriginalWidth = original.optInt("width");
			mOriginalHeight = original.optInt("height");
		}
		
		JSONObject thumbnail = content.optJSONObject("large");
		if (thumbnail != null) {
			mThumbnailUrl = thumbnail.optString("url");
			mThumbnailWidth = thumbnail.optInt("width");
			mThumbnailHeight = thumbnail.optInt("height");
		}	

		return this;
	}
	
	@Override
	public JSONObject build() {
		JSONObject object = new JSONObject();
		try {
			object.put("type", mType);
			JSONObject content = new JSONObject();
			
			JSONObject original = new JSONObject();
			original.put("url", mOriginalUrl);
			original.put("width", mOriginalWidth);
			original.put("height", mOriginalHeight);
			content.put("original", original);
			
			JSONObject thumbnail = new JSONObject();
			thumbnail.put("url", mThumbnailUrl);
			thumbnail.put("width", mThumbnailWidth);
			thumbnail.put("height", mThumbnailHeight);
			content.put("thumbnail", thumbnail);
			
			object.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public JSONObject buildTest() {
		JSONObject object = new JSONObject();
		try {
			object.put("type", mType);
			JSONObject content = new JSONObject();
			
			JSONObject original = new JSONObject();
			original.put("url", mOriginalUrl);
			original.put("width", mOriginalWidth);
			original.put("height", mOriginalHeight);
			content.put("original", original);
			
			JSONObject large = new JSONObject();
			large.put("url", "");
			large.put("width", "");
			large.put("height", "");
			content.put("large", large);
			
			JSONObject medium = new JSONObject();
			medium.put("url", "");
			medium.put("width", "");
			medium.put("height", "");
			content.put("medium", medium);
			
			object.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}

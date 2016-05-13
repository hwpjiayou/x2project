package com.renren.mobile.x2.network.mas;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.renren.mobile.x2.utils.voice.PlayerThread.OnPlayerListenner;

/**
 * 帖子语音Model类, 
 * 
 * @author xingchen.li
 * 
 * */
public class UGCVoiceModel extends UGCModel {
	
	private static final long serialVersionUID = 6574013113873889271L;
	public String mUrl;
	public int mLength;
	//是否已经下载！ 默认false
	public boolean isDownload = false;
	//是否正在播放
	public boolean isPlay = false;
	//是否停止
	public boolean isStop = false;
	//是否是默认状态
	public boolean isDefault = true;
	
	public OnPlayerListenner mPlayListener = null;
	/**
	 * 设置默认状态
	 * add by jia.xia
	 */
	public void setDefaultStatus(){
		this.isDefault = true;
		this.isPlay = false;
		this.isStop = false;
	}
	/**
	 * 设置播放状态
	 * add by jia.xia
	 */
	public void setPlayStatus(){
		this.isDefault = false;
		this.isPlay = true;
		this.isStop = false;
	}
	/**
	 * 设置暂停状态
	 * add by jia.xia
	 */
	public void setStopStatus(){
		this.isDefault = false;
		this.isPlay = false;
		this.isStop = true;
	}
	
	
	public UGCVoiceModel(JSONObject object) {
		super(object);
	}
	
	/**
	 * 传值构造器
	 * */
	public UGCVoiceModel(String url, int length) {
		this.mType = UGCModel.UGCType.VOICE;
		this.mUrl = url;
		this.mLength = length;
	}	
	
	/**
	 * TODO just for test the old interface, to be deleted
	 * test can be everything
	 * */
	public UGCVoiceModel(JSONObject object, String str) {
		this.parseTest(object);
	}
	
	/**
	 * TODO just for test the old interface, to be deleted
	 * test can be everything
	 * */
	public UGCVoiceModel update(JSONObject object, String test) {
		
		return this.parseTest(object);
	}
	/**
	 * TODO just for test the old interface, to be deleted
	 * test can be everything
	 * */
	private UGCVoiceModel parseTest(JSONObject object) {
		mType = UGCModel.UGCType.VOICE;
		mUrl = object.optString("file_url");
		if (object.has("length")) {
			try {
				mLength = object.getInt("length");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			mLength = 15;
		}
		
		return this;
	}

	@Override
	public UGCVoiceModel parse(JSONObject object) {
		if (object == null) return this;
		mType = object.optString("type");
		JSONObject content = object.optJSONObject("content");
		if (content != null) {
			//TODO 改回 url
			mUrl = content.optString("voice_url");
			mLength = content.optInt("duration");
		}
		return this;
	}

	@Override
	public JSONObject build() {
		JSONObject object = new JSONObject();
		try {		
			object.put("type", mType);
			JSONObject content = new JSONObject();
			content.put("voice_url", mUrl);
			content.put("duration", mLength);
			object.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}

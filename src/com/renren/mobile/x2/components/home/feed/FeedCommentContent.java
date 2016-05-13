package com.renren.mobile.x2.components.home.feed;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.network.mas.UGCActionModel;
import com.renren.mobile.x2.network.mas.UGCImgModel;
import com.renren.mobile.x2.network.mas.UGCModel;
import com.renren.mobile.x2.network.mas.UGCPlaceModel;
import com.renren.mobile.x2.network.mas.UGCTagModel;
import com.renren.mobile.x2.network.mas.UGCTextModel;
import com.renren.mobile.x2.network.mas.UGCUserModel;
import com.renren.mobile.x2.network.mas.UGCVoiceModel;
/**
 * @author jia.xia
 */

public class FeedCommentContent implements Serializable {

	private static final long serialVersionUID = -8030374518222938296L;

	public long mTime;

	/**
	 * 用户信息
	 */
	public UGCUserModel mUser;
	/**
	 * Content信息
	 */
	public Map<String,UGCModel>contentInfo = null;

	public FeedCommentContent(){
		
	}
	
	public FeedCommentContent(JSONObject objs) {
		if(objs == null)
			return;
			JSONObject userObject = objs.optJSONObject(FeedModel.FEED_COLUMN_USER);
			if(userObject!=null){
				mUser = new UGCUserModel(userObject);
			}
			JSONArray contentArray = objs.optJSONArray(FeedModel.FEED_COLUMN_COMMENT_CONTENT);
			if(contentArray!=null){
				if(this.contentInfo == null)
					this.contentInfo = new HashMap<String, UGCModel>();
				for(int i = 0;i<contentArray.length();i++){
					try {
						JSONObject object = (JSONObject)contentArray.get(i);
						String type = object.optString("type");

						if (type.equals(UGCModel.UGCType.TEXT)){
							UGCTextModel t = new UGCTextModel(object);
							contentInfo.put(UGCModel.UGCType.TEXT, t);
						}
						else if (type.equals(UGCModel.UGCType.IMG))
							contentInfo.put(UGCModel.UGCType.IMG, new UGCImgModel(
									object));

						else if (type.equals(UGCModel.UGCType.VOICE))
							contentInfo.put(UGCModel.UGCType.VOICE,
									new UGCVoiceModel(object));

						else if (type.equals(UGCModel.UGCType.TAG))
							contentInfo.put(UGCModel.UGCType.TAG, new UGCTagModel(
									object));

						else if (type.equals(UGCModel.UGCType.PLACE))
							contentInfo.put(UGCModel.UGCType.PLACE,
									new UGCPlaceModel(object));

						else if (type.equals(UGCModel.UGCType.ACTION))
							contentInfo.put(UGCModel.UGCType.ACTION,
									new UGCActionModel(object));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			this.mTime = objs.optLong(FeedModel.FEED_COLUMN_TIME);
	}


	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		this.mTime = time;
	}


	public UGCUserModel getmUser() {
		return mUser;
	}


	public void setmUser(UGCUserModel mUser) {
		this.mUser = mUser;
	}


	public Map<String, UGCModel> getContentInfo() {
		return contentInfo;
	}


	public void setContentInfo(Map<String, UGCModel> contentInfo) {
		this.contentInfo = contentInfo;
	}

	
}

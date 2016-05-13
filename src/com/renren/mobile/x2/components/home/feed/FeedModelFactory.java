package com.renren.mobile.x2.components.home.feed;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.utils.CommonUtil;

/**
 * 
 * @author jia.xia
 *
 */
public class FeedModelFactory {
/**
 * 生成一条Feed
 * @param object Json
 * @param isNew
 * @return
 */
	public static FeedModel createFeedModel(JSONObject objs,boolean isNew){
		FeedModel chatFeedModel = new FeedModel(objs);
		return chatFeedModel;
	}
	
}

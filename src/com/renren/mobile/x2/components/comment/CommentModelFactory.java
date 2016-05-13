package com.renren.mobile.x2.components.comment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.renren.mobile.x2.utils.CommonUtil;
/**
 * 
 * @author hwp
 *  说明：判断评论的类型。
 */

public class CommentModelFactory {

	public static CommentModel createCommentModel(JSONObject objs) throws JSONException {
		int type = (int) objs.getInt("type");
		Log.v("--hwp--", "type "+type);
		 CommentModel mCommentModel = null;	
		
		switch (type) {
		case CommentType.COMMENT_STATUS_UPDATE:
		//	mCommentModel = new FeedTextModel(objs);
			break;
		case CommentType.COMMENT_PHOTO_PUBLISH_ONE:
		case CommentType.COMMENT_PHOTO_PUBLISH_MORE:
	//		mCommentModel = new FeedPhotoModel(objs);
			break;
		case CommentType.COMMENT_PAGE_STATUS_UPDATE:
			// TODO 新的feed类型处理~

			break;
		default:	
			break;
		}
		return mCommentModel;

	}

}

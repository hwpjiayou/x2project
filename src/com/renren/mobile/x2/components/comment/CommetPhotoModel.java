package com.renren.mobile.x2.components.comment;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommetPhotoModel extends CommentModel implements Serializable {

	private static final long serialVersionUID = -4492428004125095166L;

	private int mPhotoNum;

	private ArrayList<Object> mCommentPhotoContent = new ArrayList<Object>();

	public CommetPhotoModel(JSONObject obj) throws JSONException {
		super(obj);
		JSONArray array = obj.getJSONArray("attachement_list");
		if (array != null) {
			mPhotoNum = array.length();
			JSONObject o = null;
			for (int i = 0 ;i<array.length();i++) {
				o = array.getJSONObject(i);
				//TODO  jia.xia
				/*FeedPhotoContent c = new FeedPhotoContent(o);
				if (c != null) {
					mCommentPhotoContent.add(c);
				}*/
			}
		}
	}

	public int getPhotoNum() {
		return mPhotoNum;
	}

/*	@Override
	public ArrayList<Object> getCommentAttachmentContent() {
		// TODO Auto-generated method stub
		return mCommentPhotoContent;
	}*/
	
	
}

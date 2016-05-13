package com.renren.mobile.x2.components.comment;

import org.json.JSONObject;

import android.util.Log;

import com.renren.mobile.x2.emotion.Emotion;
import com.renren.mobile.x2.emotion.EmotionString;
import com.renren.mobile.x2.network.mas.UGCContentModel;
import com.renren.mobile.x2.network.mas.UGCImgModel;
import com.renren.mobile.x2.network.mas.UGCTextModel;

public class JsonObjectCommentItem {

	private static JsonObjectCommentItem mInstance=null;
	
	private CommentItem mItem=null;
	private UGCContentModel  model=null;
	
	public JsonObjectCommentItem(CommentItem item){
		this.mItem=item;
	}
	
//	public static JsonObjectCommentItem getIstense(CommentItem item){
//		if(mInstance==null){
//			mInstance=new JsonObjectCommentItem(item);
//		}
//		return mInstance;
//	}
   //得到文本信息。
	public void getTextModel () {
		if(mItem.getContent()!=null){
			EmotionString string=new EmotionString(mItem.getContent());
			UGCTextModel text = new UGCTextModel(string.toString());
			text.build();
			model=new UGCContentModel();
			model.contentInfo.put("text",text);
		}
	
	}
	//得到图文信息。
	public void getImageModel(){
		if(mItem.getImageServiceUrl()!=null){
			UGCImgModel image=new UGCImgModel(mItem.getImageServiceUrl(), 200, 200,
					 null, 200, 200);
			model.contentInfo.put("image", image);
			}
	}
	//得到语音信息。
	public void getVoiceModel(){
		if(mItem.getVoiceUrl()!=null){
			
		}
	}
	//得到地点信息。
	public void getPlaceModel(){
		if(mItem.getPlace()!=null){
			
		}
	}
	//得到action信息
	public void getActionModel(){
		if(mItem.getAction()!=null){
			
		}
	}
	//得到tag信息
	public void getTagModel(){
		if(mItem.getTag()!=null){
			
		}
	}
	
	public JSONObject toJsonObject(){
		
		
			getTextModel();
			
			getImageModel();
			
			getVoiceModel();
			
			getPlaceModel();
			
			getActionModel();
			
			getTagModel();
	
		
		return model.getContentInfoJSONValue();
	}
}

package com.renren.mobile.x2.network.mas;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.renren.mobile.x2.R;

public class UGCTagModel extends UGCModel {

	private static final long serialVersionUID = -1000231762255814355L;
	public String mName;
	public int mId;
	public String mIcon;
	public String mDesc;
	public ImageView selectbg;

	private int iconResourceId;
	private int iconUnpressResourceId;
	private int iconUnPressResourceIdForFeed;
	static final private String[] names = { 
			"求组队", 	"求认识", 	"吐槽", 
			"拜考神", 	"当学霸",	"无聊ing", 
			"末日后", 	"我饿了", 	"求帮忙", 
			"二手" };
	static final private String[] descs = {
			" 找人一起组队玩吧～Dota，魔兽，三国杀，运动，聚会，什么都行～", 
			" 求闺蜜求兄弟？爆下你的资料咯",
			"你点开了这里，就请自由地@#*$%#@%$#&*$%#@%$#&*", 
			"考神：今天你拜我了么？", 
			"驰骋考场数年，未曾一挂",
			"无聊的时候，就聊点什么呗", 
			"告诉全世界，我活过了2012", 
			"人是铁，饭是钢，一顿不吃饿得慌~",
			"遇到什么困难了？快告诉周围的朋友们吧，或许那个ta可以帮到你喔！", 
			"想买还是想卖，吆喝一下吧！", };
	static final private int[] ids = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	static final private int[] iconResourceIds = {
			R.drawable.publisher_tag_selector1,
			R.drawable.publisher_tag_selector2,
			R.drawable.publisher_tag_selector3,
			R.drawable.publisher_tag_selector4,
			R.drawable.publisher_tag_selector5,
			R.drawable.publisher_tag_selector6,
			R.drawable.publisher_tag_selector7,
			R.drawable.publisher_tag_selector8,
			R.drawable.publisher_tag_selector9,
			R.drawable.publisher_tag_selector10 };
	static final private int[] iconUnpressResourceIds = {
			R.drawable.publisher_tag_unpressed1,
			R.drawable.publisher_tag_unpressed2,
			R.drawable.publisher_tag_unpressed3,
			R.drawable.publisher_tag_unpressed4,
			R.drawable.publisher_tag_unpressed5,
			R.drawable.publisher_tag_unpressed6,
			R.drawable.publisher_tag_unpressed7,
			R.drawable.publisher_tag_unpressed8,
			R.drawable.publisher_tag_unpressed9,
			R.drawable.publisher_tag_unpressed10 };
	static final private int[]iconUnpressTagResIds = {
			R.drawable.v1_tag_unpressed1,
			R.drawable.v1_tag_unpressed2,
			R.drawable.v1_tag_unpressed3,
			R.drawable.v1_tag_unpressed4,
			R.drawable.v1_tag_unpressed5,
			R.drawable.v1_tag_unpressed6,
			R.drawable.v1_tag_unpressed7,
			R.drawable.v1_tag_unpressed8,
			R.drawable.v1_tag_unpressed9,
			R.drawable.v1_tag_unpressed10
	};
	

	public UGCTagModel(JSONObject object) {
		super(object);
	}

	public UGCTagModel(int id) {
		mType = UGCModel.UGCType.TAG;
		mId = (id > 0 && id < 11) ? id : 6;
		mName = names[id - 1];
		mDesc = descs[id - 1];
		iconResourceId = iconResourceIds[id - 1];
		iconUnpressResourceId = iconUnpressResourceIds[id - 1];
	}
	
	@Override
	public UGCTagModel parse(JSONObject object) {
		if (object == null)
			return this;
		mType = object.optString("type");
		JSONObject content = object.optJSONObject("content");
		if (content != null) {
			mId = content.optInt("id");
			mName = content.optString("name");
			mIcon = content.optString("icon");
			mDesc = content.optString("desc");
		}
		if (mId > 0 && mId < 11 && TextUtils.isEmpty(mName)) {
			mName = names[mId - 1];
			mDesc = descs[mId - 1];
			iconResourceId = iconResourceIds[mId - 1];
			iconUnpressResourceId = iconUnpressResourceIds[mId - 1];
			iconUnPressResourceIdForFeed = iconUnpressTagResIds[mId - 1];
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
			content.put("icon", mIcon);
			content.put("desc", mDesc);
			object.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public static ArrayList<UGCTagModel> getTagList() {
		ArrayList<UGCTagModel> models = new ArrayList<UGCTagModel>();
		for (int i = 0 ; i < ids.length ; i ++) {
			UGCTagModel model = new UGCTagModel(ids[i]);
			models.add(model);
		}
		return models;
	}
	
	public int getIconResourceId() {
		return iconResourceId;
	}

	public int getIconUnpressResourceId() {
		return iconUnpressResourceId;
	}

	public int getIconUnPressResourceIdForFeed(){
		return iconUnPressResourceIdForFeed;
	}
	
	public void setImageView(ImageView v){
		this.selectbg = v;
	}
	public void setSelected(){
		if(selectbg!=null){
			selectbg.setVisibility(View.VISIBLE);
		}
	}
	public void setUnSelected(){
		if(selectbg!=null){
			selectbg.setVisibility(View.INVISIBLE);
		}
	}

}

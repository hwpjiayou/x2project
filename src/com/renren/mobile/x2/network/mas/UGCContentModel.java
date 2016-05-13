package com.renren.mobile.x2.network.mas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * UGCContent模型
 * **/
public class UGCContentModel implements Serializable {

	private static final long serialVersionUID = 3176922692484024401L;

	/**
	 * 帖子ID
	 **/
	public String id;
	/**
	 * 客户端时间
	 **/
	public long clientTime;
	/**
	 * 更新时间
	 **/
	public long updateTime;
	/**
	 * 创建时间
	 **/
	public long createTime;
	/**
	 * 用户信息
	 **/
	public UGCUserModel user;
	/**
	 * 帖子状态
	 **/
	public String state;
	/**
	 * 帖子内容列表
	 * */
	public Map<String, UGCModel> contentInfo;

	public UGCContentModel() {
		contentInfo = new HashMap<String, UGCModel>();
	}

	
	/**
	 * 构造函数,new 实例化之后,直接.获取属性值
	 * */
	public UGCContentModel(JSONObject object) {
		this.parse(object);
	}

	/**
	 * 解析 帖子内contentInfo
	 * */
	private void ParseContentInfo(JSONArray jsonArray) {
		try {
			if (jsonArray != null) {
				int length = jsonArray.length();
				if (contentInfo == null)
					contentInfo = new HashMap<String, UGCModel>();
				
				for (int i = 0; i < length; i++) {
					JSONObject object = (JSONObject) jsonArray.get(i);
					String type = object.optString("type");

					if (type.equals(UGCModel.UGCType.TEXT))
						contentInfo.put(UGCModel.UGCType.TEXT, new UGCTextModel(
								object));

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
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析帖子内容
	 * */
	private UGCContentModel parse(JSONObject object) {
		id = object.optString("ugc_id");
		clientTime = object.optLong("client_time");
		updateTime = object.optLong("updated_time");
		createTime = object.optLong("created_time");
		state = object.optString("state");

		JSONObject jUser = object.optJSONObject("user");
		user = new UGCUserModel(jUser);

		JSONArray jContentInfo = object.optJSONArray("content");
		this.ParseContentInfo(jContentInfo);
		return this;
	}

	/**
	 * 获取解析后的Posts列表
	 * */
	public Map<String, UGCModel> getParsedPosts() {
		return this.contentInfo;
	}

	/**
	 * 构造器,本地发送帖子传入发送Model列表
	 * */
	public UGCContentModel(Map<String, UGCModel> contents) {
		contentInfo = contents;
	}

	/**
	 * 更新语音内容
	 * */
	public UGCContentModel updateVoiceContent(JSONObject object) {
		if (contentInfo != null
				&& contentInfo.containsKey(UGCModel.UGCType.VOICE)) {
			contentInfo.get(UGCModel.UGCType.VOICE).update(object);
		} else {
			contentInfo.put(UGCModel.UGCType.VOICE,
					new UGCVoiceModel(object));
		}
		return this;
	}
	/**
	 * TODO just for test the old interface, to be deleted
	 * test can be everything
	 * */
	public UGCContentModel updateVoiceContentTest(JSONObject object) {
		if (contentInfo != null
				&& contentInfo.containsKey(UGCModel.UGCType.VOICE)) {
			((UGCVoiceModel)contentInfo.get(UGCModel.UGCType.VOICE)).update(object, null);
		} else {
			contentInfo.put(UGCModel.UGCType.VOICE,
					new UGCVoiceModel(object, null));
		}
		return this;
	}
	/**
	 * TODO just for test the old interface, to be deleted
	 * test can be everything
	 * */
	public UGCContentModel updateImageContentTest(JSONObject object) {
		if (contentInfo != null
				&& contentInfo.containsKey(UGCModel.UGCType.IMG)) {
			((UGCImgModel)contentInfo.get(UGCModel.UGCType.IMG)).update(object,null);
		} else {
			contentInfo.put(UGCModel.UGCType.IMG, new UGCImgModel(object, null));
		}
		return this;
	}

	/**
	 * 更新图片内容
	 * */
	public UGCContentModel updateImageContent(JSONObject object) {
		if (contentInfo != null
				&& contentInfo.containsKey(UGCModel.UGCType.IMG)) {
			contentInfo.get(UGCModel.UGCType.IMG).update(object);
		} else {
			contentInfo.put(UGCModel.UGCType.IMG, new UGCImgModel(object));
		}
		return this;
	}

	/**
	 * 更新文本内容
	 * */
	public UGCContentModel updateTextContent(JSONObject object) {
		if (contentInfo != null
				&& contentInfo.containsKey(UGCModel.UGCType.TEXT)) {
			contentInfo.get(UGCModel.UGCType.TEXT).update(object);
		} else {
			contentInfo.put(UGCModel.UGCType.TEXT, new UGCTextModel(object));
		}
		return this;
	}
	
	/**
	 * 更新文本内容
	 * */
	public UGCContentModel updateTextContent(String text) {
		if (contentInfo != null
				&& contentInfo.containsKey(UGCModel.UGCType.TEXT)) {
			((UGCTextModel)contentInfo.get(UGCModel.UGCType.TEXT)).update(text);
		} else {
			contentInfo.put(UGCModel.UGCType.TEXT, new UGCTextModel(text));
		}
		return this;
	}
	
	public void setTagModel(UGCTagModel tagModel) {
		if (contentInfo != null) {
			contentInfo.put(UGCModel.UGCType.TAG, tagModel);
		}
	}
	
	public void setPlaceModel(UGCPlaceModel placeModel) {
		if (contentInfo != null) {
			contentInfo.put(UGCModel.UGCType.PLACE, placeModel);
		}
	}

	/**
	 * 获取內容contentInfo,JSONArray
	 * **/
	public JSONObject getContentInfoJSONValue() {
		if (contentInfo != null) {
			JSONObject object = new JSONObject();
			JSONArray array = new JSONArray();
			for (UGCModel model : contentInfo.values()) {
				array.put(model.build());
			}
			try {
				object.put("content", array);
				return object;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}

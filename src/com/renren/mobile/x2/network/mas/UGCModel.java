package com.renren.mobile.x2.network.mas;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * 帖子抽象类, Model类
 * 
 * @author xingchen.li
 * 
 * */

public abstract class UGCModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7330451258849396984L;
	/**
	 * model类公有属性 类型
	 * */
	public String mType;

	public interface UGCType {
		String IMG = "image";
		String VOICE = "voice";
		String TEXT = "text";
		String PLACE = "place";
		String TAG = "tag";
		String ACTION = "action";
	}
	
	public UGCModel(){}
	
	public UGCModel(JSONObject object) {
		this.parse(object);
	}
	/**
	 * 解析JSONObject,
	 * 使用 {@link #UGCModel(JSONObject)} 后调用
	 * */
	public abstract UGCModel parse(JSONObject object);
	
	/**
	 * 构造 model 的  {@link #mJSONObject}
	 * 使用 传参构造器后, 调用
	 * */
	public abstract JSONObject build();
	
	/**
	 * 更新UGCModel, 更新后, model的相应字段也更新
	 * @param JSONObject object 
	 * */
	public UGCModel update(JSONObject object) {
		
		if (object != null) {
			this.parse(object);
		}
		return this;
	}
	
	/**
	 * 获取Model的JSON字符串
	 * */
	public String getStringValue() {
		return this.build().toString();
	}
	
	/**
	 * 获取Model类的类型
	 * */
	public String getType() {
		return this.mType;
	}
	
}

package com.renren.mobile.x2.emotion;

import android.graphics.Bitmap;

/**
 * 
 * @author jiaxia
 * 定义了表情选择和解析的接口
 */
public interface IEmotionManager {

	/**
	 * 中表情解析回调接口
	 * 成功,失败,Loading
	 * @author jiaxia
	 * 
	 */
	public static interface OnEmotionParserCallback {
		public void onEmotionSuccess(Emotion emotion);

		public void onEmotionError(Bitmap bitmap);

		public void onEmotionLoading(Bitmap bitmap);
	}
	
	public static interface OnCoolEmotionSelectCallback {
		public void onCoolEmotionSelect(String emotion);
	}
	/**
	 * 
	 * @author jiaxia
	 *小表情点击监听器
	 */
	public static interface OnEmotionSelectCallback{
		public void onEmotionSelect(String emotion);
		public void mDelBtnClick();
		public void onCoolEmotionSelect(String emotion);
	}
	/**
	 * 加载本地文件信息
	 */
	public void loadEmotionPackage();
	/**
	 * 中表情解析,需注册接口(OnEmotionParserCallback)
	 * @param id
	 * @param emotionName
	 */
	public void getCoolEmotion(String emotionName);
	/**
	 * 注册小表情选择监听器
	 * @param listener
	 */
	public void registerNormalEmotionSelectCallBack(OnEmotionSelectCallback listener);
	/**
	 * 注册中表情选择监听器
	 * @param listener
	 */
	public void registerCoolEmotionSelectCallBack(OnCoolEmotionSelectCallback listener);
	/**
	 * 注册中表情解析监听器
	 * @param listener
	 */
	public void registerCoolEmotionParserCallBack(OnEmotionParserCallback listener);
}

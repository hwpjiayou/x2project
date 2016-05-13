package com.renren.mobile.x2.network.mas;

import org.json.JSONObject;

public interface INetResponse {
	
	String IMG_DATA = "img";
	
	String HTML_DATA = "html";
	
	String VOICE_DATA = "voice";

	/**
	 * 响应.
	 * @param req 请求对象
	 * @param obj 返回数据的封装<br>
	 * 1.获取图片使用map.getBytes(IMG_DATA);得到byte[]格式的图片数据<br>
	 * 2.发生错误时使用map.getInt("error_code");获取错误码，使用map.getString("error_msg");获取错误信息<br>
	 * 3.接口调用返回的格式参考接口文档<br>
	 */
	void response(INetRequest req, JSONObject obj);	
	
}

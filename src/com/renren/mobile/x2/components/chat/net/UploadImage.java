package com.renren.mobile.x2.components.chat.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.components.chat.face.IUploadable_Image;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.log.Logger;

public class UploadImage implements INetResponse{
	
	private IUploadable_Image listenner;
	public UploadImage(IUploadable_Image listenner) {
		this.listenner = listenner;
	}

	@Override
	public void response(INetRequest req, JSONObject data) {
		if(listenner!=null){
			if(data!=null){
				if(Methods.checkNoError(req, data)){
					if (Logger.mDebug) {
						Logger.logd(Logger.SEND_PHOTO,"图片上传成功  data="+data.toString());
					}
					String tinyUrl="";
					String largeUrl="";
					String mainUrl="";
					try {
						tinyUrl = data.getString("medium_url");
						largeUrl = data.getString("original_url");
					     mainUrl = data.getString("large_url");
					} catch (JSONException e) {
						if(Logger.mDebug){
							e.printStackTrace();
						}
					}
					listenner.onUploadOver(tinyUrl, mainUrl, largeUrl);
					return ;
				}
			}
			if (Logger.mDebug) {
				Logger.errord(Logger.SEND_PHOTO,"图片上传失败  data="+data.toString());
			}
			listenner.onUploadError();
		}
		
	}

}

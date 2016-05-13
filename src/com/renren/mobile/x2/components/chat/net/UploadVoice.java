package com.renren.mobile.x2.components.chat.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.components.chat.face.IUploadable_Voice;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.log.Logger;
/**
 *语音上传后的回调
 */
public class UploadVoice implements  INetResponse{

	private IUploadable_Voice listenner;
	public UploadVoice(IUploadable_Voice listenner) {
		this.listenner = listenner;
	}
	
	@Override
	public void response(INetRequest req, JSONObject data) {
		if(listenner!=null){
			if(data!=null){
				if(Methods.checkNoError(req, data)){
					if (Logger.mDebug) {
						Logger.logd(Logger.SEND_VOICE,"语音上传成功  data="+data.toString());
					}
					String voiceUrl="";
					try {
						voiceUrl = data.getString("file_url");
					} catch (JSONException e) {
						if(Logger.mDebug){
							e.printStackTrace();
						}
					}
					String vid =  "0";
					listenner.onUploadOver(vid,voiceUrl);
					return ;
				}
			}
			if (Logger.mDebug) {
				Logger.errord(Logger.SEND_PHOTO,"语音上传失败  data="+data.toString());
			}
			listenner.onUploadError();
		}
	}

}

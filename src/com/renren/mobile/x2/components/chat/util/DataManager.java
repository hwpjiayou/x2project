package com.renren.mobile.x2.components.chat.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.log.Logger;



/**
 * @author dingwei.chen
 * @description 数据管理器 用来上传下载数据
 * */
public final class DataManager {
	private static DataManager sInstance = new DataManager();
	private DataManager(){}
	public static DataManager getInstance(){
		return sInstance;
	}
	
//	/*获得图片*/
//	public void getImage(String url,INetResponse response){
//		//McsServiceProvider.getProvider().getImage(url, response);
//		HttpMasService.getInstance().getImage(url, response);
//	}
//	/*获得图片*/
//	public void getImage(String url,ImageListener listener){
//		//McsServiceProvider.getProvider().getImage(url, new ImageResponse(listener));
//		HttpMasService.getInstance().getImage(url, new ImageResponse(listener);
//	}
	public static interface ImageListener extends DataListener{
		public void onGetBitmap(Bitmap bitmap);
	}
	public static interface DataListener{
		public void onGetData(byte[] data);
		public void onGetError();
	}
	
//	public static class ImageResponse extends INetReponseAdapter{
//		public ImageListener mListener =null;
//		public ImageResponse(ImageListener listener){
//			this.mListener = listener;
////			ImageLoader loader = ImageLoaderManager.get(ImageLoaderManager.TYPE_FEED, null);
////			loader.get(new ImageLoader.HttpImageRequest(null, true), new ImageLoader.UiResponse(view) {
////				
////				@Override
////				public void failed() {
////					// TODO Auto-generated method stub
////					
////				}
////				
////				@Override
////				public void uiSuccess(Bitmap bitmap) {
////					// TODO Auto-generated method stub
////					View.seti
////					
////				}
////			})
//		}
//		@Override
//		public void onSuccess(INetRequest req, JSONObject data) {
//			byte[] imgData = data.getBytes(IMG_DATA);
//			if(this.mListener!=null){
//				this.mListener.onGetData(imgData);
//			}
//			Bitmap bitmap = ImageUtil.createImageByBytes(imgData);
//			if(this.mListener!=null){
//				this.mListener.onGetBitmap(bitmap);
//			}
//		}
//		@Override
//		public void onError(INetRequest req, JSONObject data) {
//			if(this.mListener!=null){
//				this.mListener.onGetError();
//			}
//		}
//	}
	
	
	/*获得语音*/
	public void getVoice(String url,INetResponse response){
		//McsServiceProvider.getProvider().getVoice(url, response);
	}
	/*获得语音*/
	public void getVoice(String url,DataListener listener){
		//McsServiceProvider.getProvider().getVoice(url, new VoiceResponse(listener));
	}

}

package com.renren.mobile.x2.components.comment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.command.Command;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.imageviewer.ImageViewActivity;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.mas.UGC;
import com.renren.mobile.x2.network.mas.UGCContentModel;
import com.renren.mobile.x2.network.mas.UGCImgModel;
import com.renren.mobile.x2.network.mas.UGCManager;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.log.Logger;
/**
 * 
 * @author hwp
 * 拍照的控制器类。
 */
public class Comment_TakePhoto extends Command {

	public static interface NEED_RETURN_DATA{
		String RESULT_CODE_INT="RESULT_CODE_INT";//请求码
	}
	
	private CommentFragment mActivity=null;
	private String mTmpFile=null;
	private CommentItem item;
	private static UGCImgModel imageModel;
	
	public Comment_TakePhoto(CommentFragment activity){
		this.mActivity=activity;
	}
	
	@Override
	public void onStartCommand() {
		// TODO Auto-generated method stub
        if(Logger.mDebug){
        	Logger.traces();
        }
		if(!DeviceUtil.getInstance().isSDCardHasEnoughSpace()){
			DeviceUtil.getInstance().toastNotEnoughSpace();
			return;
		}
		Intent intent=new Intent(mActivity.getActivity(),ImageViewActivity.class);
		mTmpFile = "sdcard/x2/ImageCache/"+
				"renren_"+ String.valueOf(System.currentTimeMillis()); 
		intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_LOCAL_URI, mTmpFile);
		intent.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.TAKE_PHOTO);
		mActivity.startActivityForResult(intent, ImageViewActivity.TAKE_PHOTO);
	}

	@Override
	public void onEndCommand(Map<String, Object> returnData) {
		if(Logger.mDebug){
			Logger.traces();
		}
		int resultCode = (Integer)returnData.get(NEED_RETURN_DATA.RESULT_CODE_INT);
		switch(resultCode){
		case Activity.RESULT_OK:
			this.sendMessage(mTmpFile);
			break;
		case Activity.RESULT_CANCELED:
			break;
		}
	}
	
	 public void sendMessage(String path){
	    	if(Logger.mDebug){
	    		Logger.traces();
	    	}
	    	item=new CommentItem();
	    	if(mActivity.mLoginInfo!=null){
	    		item.setUserName(mActivity.mLoginInfo.mUserName);
	    		item.setUserHeadUrl(mActivity.mLoginInfo.mOriginal_Url);
	    		item.setUserId(mActivity.mLoginInfo.mUserId);
	    		item.setImageLocationUrl(path);
	    		mActivity.addCommentIten(item);//展示在界面中。
	    	}
	    	
	    	String imgPath = path;
			if(Logger.mDebug){
				Logger.logd(Logger.SEND_PHOTO,"imagePath="+imgPath);
			}
			File file =  new File(imgPath);
			byte[] bytes = new byte[(int)file.length()];	
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				fis.read(bytes);
				fis.close();
			} catch (Exception e) {
				if(Logger.mDebug){
					Logger.errord(e.toString());
					e.printStackTrace();
				}
			}
			//向服务其发送数据，需要得到返回的url后在执行去发送请求回来的操作。及得到 
			//发送到服务器端，服务器给个回复信息。
			sendImage(bytes);
//    		if(imageModel.mMediumUrl!=null){
//    			item.setImageServiceUrl(imageModel.mMediumUrl);
//    		}
//    		
//    		JSONObject content;
//    		content=JsonObjectCommentItem.getIstense(item).toJsonObject();
//	    	HttpMasService.getInstance().postComment("1002", feedId, content,response);
    		
//	    	
//	    	InputMethodManager imm = (InputMethodManager)mActivity.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
//            imm.hideSoftInputFromWindow(mActivity.getView().getWindowToken(),0);
	    }
	 
	 /*
	  * 发送图片。
	  */
	 public void sendImage(byte[] bytes){
//		Bitmap test = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.test_default);
//        byte[] bytes1 = Bitmap2Bytes(test);
//		HttpMasService.getInstance().uploadPhoto(response, bytes);
			UGC feed=makeFeedRequests(bytes);
			UGCManager.getInstance().sendUGC(feed);
	 
	 }
	 
	 private byte[] Bitmap2Bytes(Bitmap bitmap) {

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	        try{

	            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
	            byte[] bytes = outputStream.toByteArray();
	            outputStream.flush();
	            outputStream.close();
	            return bytes;
	        } catch (Exception e){
	            e.printStackTrace();
	        }
	        return null;
	    }
	 
	public UGC makeFeedRequests(byte[] bytes){
		int feedId=(int) mActivity.mFeedDataModel.getFeedId();
		INetRequest imageRequest=UGCManager.getInstance().getImageUploadRequest(bytes);
		UGC feed=new UGC("1002",UGC.UGC_TYPE_COMMENT, feedId, null, imageRequest, null, response);
		return feed;
	}
	 
	 
	 public static INetResponse  response = new INetResponse() {
			
			@Override
			public void response(INetRequest req, JSONObject obj) {
				JSONObject data = (JSONObject)obj;
				if(Methods.checkNoError(req, data)){
					//toast("success:"+data.toString());
					CommonUtil.toast("success:"+data.toString());
					Log.v("--hwp--","data "+data.toString());
				}else{
					CommonUtil.toast("error:"+data.toString());

				}
			}
		};
		
}

package com.renren.mobile.x2.components.comment;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.renren.mobile.x2.components.chat.command.Command;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.imageviewer.ImageViewActivity;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.mas.UGCImgModel;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.log.Logger;
/**
 * 
 * @author hwp
 * 取图片的控制器类。
 */
public class Comment_SelectPhoto extends Command{

	public static interface NEED_RETURN_DATA{
		String RESULT_CODE_INT="RESULT_CODE_INT";//结果码
	}
	
	private CommentFragment mActivity=null;
	private String mTmpfile=null;
	private CommentItem item;
	private static UGCImgModel imageModel;
	
	public Comment_SelectPhoto(CommentFragment context){
		this.mActivity=context;
	}
	
	@Override
	public void onStartCommand() {
		Intent intent=new Intent(mActivity.getActivity(),ImageViewActivity.class);
	    intent.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.SELECT_PHOTO);
	    mTmpfile="sdcard/x2/ImageCache/"
	    		+"renren_"+String.valueOf(java.lang.System.currentTimeMillis());
	    intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_LOCAL_URI, mTmpfile);
	    mActivity.startActivityForResult(intent, ImageViewActivity.SELECT_PHOTO);
	}

	
	@Override
	public void onEndCommand(Map<String, Object> returnData) {
		int resultCode=(Integer)returnData.get(NEED_RETURN_DATA.RESULT_CODE_INT);
	    switch(resultCode){
	    case Activity.RESULT_OK:
	    	this.sendMessage(mTmpfile);
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
    		item.setImageLocationUrl(mTmpfile);
    		mActivity.addCommentIten(item);
    		InputMethodManager imm = (InputMethodManager)mActivity.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
            imm.hideSoftInputFromWindow(mActivity.getView().getWindowToken(),0);
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
		Log.v("--hwp--","null "+bytes);
		sendImage(bytes);
		String feedId=mActivity.mFeedDataModel.getFeedId()+"";
//    		if(imageModel.mMediumUrl!=null){
//			item.setImageServiceUrl(imageModel.mMediumUrl);
//		}
//		
//		JSONObject content;
//		content=JsonObjectCommentItem.getIstense(item).toJsonObject();
//    	HttpMasService.getInstance().postComment("1002", feedId, content,response);
		
//    	
//    	InputMethodManager imm = (InputMethodManager)mActivity.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
//        imm.hideSoftInputFromWindow(mActivity.getView().getWindowToken(),0);
    }
    

	 /*
	  * 发送图片。
	  */
	 public void sendImage(byte[] bytes){
		HttpMasService.getInstance().uploadPhoto(response, bytes);
	 }

	 public static INetResponse  response = new INetResponse() {
			
			@Override
			public void response(INetRequest req, JSONObject obj) {
				// TODO Auto-generated method stub
				JSONObject data = (JSONObject)obj;
				if(Methods.checkNoError(req, data)){
					//toast("success:"+data.toString());
					CommonUtil.toast("success:"+data.toString());
					Log.v("--hwp--","data "+data.toString());
					imageModel=new UGCImgModel(data);
					Log.v("--hwp--","url "+imageModel.mOriginalUrl);
				}else{
					CommonUtil.toast("error:"+data.toString());

				}
			}
		};
}

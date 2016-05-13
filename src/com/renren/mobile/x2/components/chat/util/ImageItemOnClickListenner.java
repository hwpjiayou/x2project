package com.renren.mobile.x2.components.chat.util;
import java.io.File;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Image;
import com.renren.mobile.x2.components.imageviewer.ImageViewActivity;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener.SEND_TEXT_STATE;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.FileUtil;

public class ImageItemOnClickListenner implements OnClickListener{
	ChatMessageWarpper_Image mMessage = null;
	public ImageItemOnClickListenner(ChatMessageWarpper_Image imageMessage){
		mMessage = imageMessage;
	}
	@Override
	public void onClick(View v) {
//		Intent i = new Intent(GlobalValue.getCurrentActivity(),ImageViewActivity.class);
//		if(mMessage.mComefrom==ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL){
//			this.viewPhotoFromNet(i);
//		}else{
//			i.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.VIEW_LOCAL_TO_OUT_LARGE_IMAGE);
//			String imgPath = null;
//           // SystemUtil.log("ViewLargeHead", "image item click listener: " + mMessage.mMessageContent);
//			if(mMessage.mMessageContent.endsWith(".jpg")){
//				imgPath = mMessage.mMessageContent.substring(0, mMessage.mMessageContent.length()-4);
//			}else{
//				imgPath = mMessage.mMessageContent;
//			}
//			File file = new File(imgPath);
//			if(file.exists()){
//				i.putExtra(ImageViewActivity.NEED_PARAM.LARGE_LOCAL_URI, mMessage.mMessageContent);
//				GlobalValue.getCurrentActivity().startActivityForResult(i, ImageViewActivity.VIEW_LOCAL_TO_OUT_LARGE_IMAGE);
//			}else{
//				if(mMessage.mLargeUrl!=null){
//					this.viewPhotoFromNet(i);
//				}else{
//					CommonUtil.toast("图片文件丢失");		//ImageItemOnClickListenner_java_1=图片文件丢失; 
//				}
//			}
//		}
		
	}
	private void viewPhotoFromNet(Intent i){
//		i.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.VIEW_OUT_TO_LOCAL_LARGE_IMAGE);
//		if(mMessage.mSendTextState==SEND_TEXT_STATE.SEND_PREPARE){
//			return;
//		}
//        /* update by yuchao.zhang 以前下载用来显示的图片为200标准，现更新为原图
//         * 原图最大为720 */
//		i.putExtra(ImageViewActivity.NEED_PARAM.LARGE_PORTRAIT_URL, mMessage.mLargeUrl);
//		i.putExtra(ImageViewActivity.NEED_PARAM.SMALL_LOCAL_URI, mMessage.mMessageContent);
//		CommonUtil.log("ViewLargeHead", "mMessage.mLargeUrl = "+mMessage.mLargeUrl+" mMessage.mMainUrl = "+mMessage.mMainUrl);
//		String path =ChatUtil.getUserPhotos_Large(mMessage.mToChatUserId)
//			+FileUtil.getInstance().getFileNameFromURL(mMessage.mLargeUrl);
//		i.putExtra(ImageViewActivity.NEED_PARAM.LARGE_LOCAL_URI, path);
//		GlobalValue.getCurrentActivity().startActivityForResult(i, ImageViewActivity.VIEW_OUT_TO_LOCAL_LARGE_IMAGE);
	}
}

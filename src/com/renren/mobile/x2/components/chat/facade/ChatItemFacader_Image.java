package com.renren.mobile.x2.components.chat.facade;

import java.io.File;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.face.IMessageOnClickListener;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Image;
import com.renren.mobile.x2.components.chat.message.Subject;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.ImageItemOnClickListenner;
import com.renren.mobile.x2.components.chat.util.ImagePool;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener.SEND_TEXT_STATE;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.TagResponse;



/**
 * @author dingwei.chen
 * @说明 图片条目装饰器
 * */
public class ChatItemFacader_Image extends ChatItemFacader{
	
	private static ImageLoader mChatImageLoader;
	public ChatItemFacader_Image(){
		if(mChatImageLoader == null){
			mChatImageLoader = ImageLoaderManager.get(ImageLoaderManager.TYPE_FEED, null);
		}
	}
	
	@Override
	public void facade(ChatItemHolder holder, ChatMessageWarpper chatmessage,final  IMessageOnClickListener iClick) {
		this.process(holder, (ChatMessageWarpper_Image)chatmessage,iClick);
	}
	
	private void process(ChatItemHolder holder,final  ChatMessageWarpper_Image chatmessage, final  IMessageOnClickListener iClick){
		holder.mMessage_Image_ImageView.setVisibility(View.VISIBLE);
		holder.mMessage_Image_ImageView.setIsBrush(chatmessage.isBrush());
		switch(chatmessage.mComefrom){
			case ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT:
				chatmessage.processLocalToOut(holder);
				break;
			case ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL:
				chatmessage.processOutToLocal(holder);
				break;
		}
		holder.update(chatmessage.mMessageState, null);
		if(chatmessage.mSendTextState==SEND_TEXT_STATE.SEND_PREPARE){
			holder.mMessage_Image_ImageView.setLayerVisible(chatmessage.mComefrom==ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT);
		}else{
			holder.mMessage_Image_ImageView.setLayerVisible(false);
		}
		if(chatmessage.mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL){
			holder.mMessage_Error_ImageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					chatmessage.download(true);
				}
			});
		}
		holder.mMessage_Image_ImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				iClick.onClick(chatmessage);
			}
		});
		holder.mMessage_Image_ImageView.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				//callback.onItemLongClick(chatmessage);//
				iClick.onLongClick(chatmessage);
				return false;
			}
		});
	}
	
	
	
	
	
	
//	/*处理发送来的消息*/
//	private void processOutToLocal(ChatItemHolder holder, ChatMessageWarpper_Image chatmessage){
//		
//		if(!TextUtils.isEmpty(chatmessage.mTinyUrl)){
//			holder.mMessage_Image_ImageView.setImageBitmap(ChatUtil.getDefualtBitmap(true));
//			
//			if(chatmessage.mMessageState!=Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR){
//				chatmessage.download(false);
//			}else{
//				holder.mMessage_Error_ImageView.setVisibility(View.VISIBLE);
//				return;
//			}
//			holder.mMessage_Error_ImageView.setVisibility(View.GONE);
//		}else{
//			
//		}
//		
//		TagResponse<String>response = new TagResponse<String>(imgModel.mLargeUrl) {
//			
//			@Override
//			public void failed() {
//				
//			}
//			
//			@Override
//			protected void success(final Bitmap bitmap, final String tag) {
//				if(tag.equals(imgModel.mLargeUrl)){
//					RenrenChatApplication.getUiHandler().post(new Runnable() {
//						
//						@Override
//						public void run() {
////							AlphaAnimation aAnimation = new AlphaAnimation(0,1);
////							aAnimation.setDuration(1000);
////							holder.mSinglePhotoImage.startAnimation(aAnimation);
//							holder.mSinglePhotoImage.setTag(imgModel.mLargeUrl);
//							holder.mSinglePhotoImage.setImageBitmap(bitmap);
//						}
//					});
//				}
//			}
//		};
//		mChatImageLoader.get(new HttpImageRequest(imgModel.mLargeUrl, true), response);
//		
//		
//		
//		ImageLoader loader =ImageLoaderManager.get(ImageLoaderManager.TYPE_CHAT, null);
//		loader.get(request, response)
//		final String imgPath = chatmessage.mMessageContent+(chatmessage.isBrush()?"":"_small");
//		File file = new File(imgPath);
//		if(file.exists()){
//			Bitmap bitmap = ImagePool.getInstance()
//					.getBitmapFromLocal(file.getAbsolutePath());
//			if(bitmap!=null){
//				holder.mMessage_Image_ImageView.setImageBitmap(bitmap,file.getAbsolutePath());
//			}else{
//				holder.mMessage_Image_ImageView
//											.setImageBitmap(ChatUtil.getDefualtBitmap(true));
//			}
//		}else{/*文件不存在*/
//			holder.mMessage_Image_ImageView.setImageBitmap(ChatUtil.getDefualtBitmap(true));
//			if(chatmessage.mMessageState!=Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR){
//				chatmessage.download(false);
//			}else{
//				holder.mMessage_Error_ImageView.setVisibility(View.VISIBLE);
//				return;
//			}
//			holder.mMessage_Error_ImageView.setVisibility(View.GONE);
//		}
//	}
//	/*处理发送出去的消息*/
//	private void processLocalToOut(ChatItemHolder holder, ChatMessageWarpper_Image chatmessage){
//		final String imgPath = chatmessage.mMessageContent+(chatmessage.isBrush()?"":"_small");
//		if(new File(imgPath).exists()){
//			holder.mMessage_Image_ImageView.setImagePath(imgPath,false);
//		}else{
//			chatmessage.download(false);
//		}
//	}
	@Override
	public View getFacadeView(ChatItemHolder holder) {
		return holder.mMessage_ImageMessage_LinearLayout;
	}
	

}

package com.renren.mobile.x2.components.chat.message;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.view.View;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.face.IUploadable_Image;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.components.chat.net.ChatMessageSender;
import com.renren.mobile.x2.components.chat.util.ChatDataHelper;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.ImagePool;
import com.renren.mobile.x2.components.chat.util.ImagePool.DownloadImageRequest;
import com.renren.mobile.x2.components.chat.util.ImagePool.OnDownloadImageListenner;
import com.renren.mobile.x2.components.chat.util.ItemLongClickDialogProxy.LONGCLICK_COMMAND;
import com.renren.mobile.x2.components.chat.util.Playable.PLAY_STATE;
import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.db.table.ChatHistory_Column;
import com.renren.mobile.x2.utils.FileUtil;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.Request;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.TagResponse;
import com.renren.mobile.x2.utils.log.Logger;

public class ChatMessageWarpper_Image extends ChatMessageWarpper implements 
OnDownloadImageListenner,
IUploadable_Image
{
	
	private static ImageLoader mChatHttpImageLoader;
//	private static ImageLoader mChatFillImageLoader;
	
	/**
	 * 图片数据
	 * */
	public String mMineType;
	@ORM(mappingColumn=ChatHistory_Column.TINY_URL)
	public String mTinyUrl;
	@ORM(mappingColumn=ChatHistory_Column.MAIN_URL)
	public String mMainUrl;
	@ORM(mappingColumn=ChatHistory_Column.LARGE_URL)
	public String mLargeUrl;
	@ORM(mappingColumn=ChatHistory_Column.IS_BRUSH)
	public int mIsBrushPen = IS_BRUSH.NO;
	
	public ChatMessageWarpper_Image(){
		this.mMessageType =ChatBaseItem.MESSAGE_TYPE.IMAGE;
		if(mChatHttpImageLoader == null){
			mChatHttpImageLoader = ImageLoaderManager.get(ImageLoaderManager.TYPE_CHAT, null);
		}
	}
	
	public static interface IS_BRUSH{
		public int YES = 1;
		public int NO = 0;
	}
		
	public boolean mIsDownLoad = false;
	public int mPlayState = PLAY_STATE.STOP;
	public long mUploadDataNumber = 0l;
	public long mUploadSumDataLength = 0l;
	public long mUploadSumDataPercent = 0L;
	public long mDownloadDataNumber = 0l;
	public long mDownloadSumDataLength = 0l;
	public long mDownloadSumDataPercent = 0L;
	Map<String,Object> mUpload_Or_Download_Data = null;
	
	
	public void download(boolean isForceDownload){
		if((			this.mSendTextState!=SEND_TEXT_STATE.SEND_PREPARE
				&&  this.mMessageState!=Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR)
					||	isForceDownload
		){
			String url = this.isBrush()?this.mLargeUrl:this.mTinyUrl;
			//SystemUtil.log("onmea", "down load =========== "+isBrush()+","+url);
			if(Logger.mDebug){
				Logger.errord("开始下载图片 url="+url);
			}
			DownloadImageRequest request = new DownloadImageRequest(
												url,
												this, 
												isBrush()?this.mMessageContent:this.mMessageContent+"_small");
			request.mIsCompress = false;
			ImagePool.getInstance().downloadImage(request);
		}
	}
	public void resend() {
		if(this.mSendTextState == SEND_TEXT_STATE.SEND_PREPARE){
			return;
		}
		if(this.mMessageState==COMMAND.COMMAND_UPLOAD_IMAGE_ERROR){
			this.mMessageState=COMMAND.COMMAND_MESSAGE_OVER;
			try {
				ChatMessageSender.getInstance().uploadData(
						this, 
						FileUtil.getInstance().readBytes(new File(mMessageContent.split(".jpg")[0])));
			} catch (Exception e) {
				if(Logger.mDebug){
					e.printStackTrace();
				}
				//SystemUtil.toast(RenrenChatApplication.getAppContext().getResources().getString(R.string.ChatMessageWarpper_Image_java_1));		//ChatMessageWarpper_Image_java_1=文件未找到; 
			}
		}else{
			this.mMessageState=COMMAND.COMMAND_MESSAGE_OVER;
			ChatMessageSender.getInstance().sendMessageToNet(this, false);
		}
	}
	@Override
	public List<OnLongClickCommandMapping> getOnClickCommands() {
		List<OnLongClickCommandMapping> list = new LinkedList<ChatMessageWarpper.OnLongClickCommandMapping>();
		if(this.mMessageState == Subject.COMMAND.COMMAND_MESSAGE_ERROR || this.mMessageState == Subject.COMMAND.COMMAND_UPLOAD_IMAGE_ERROR ){
			list.add(new OnLongClickCommandMapping("重新发送",LONGCLICK_COMMAND.RESEND));		//ChatMessageWarpper_FlashEmotion_java_1=重新发送; 
		}
		if(this.mMessageState == Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR){
			list.add(new OnLongClickCommandMapping("重新下载",LONGCLICK_COMMAND.REDOWNLOAD));		//ChatMessageWarpper_Image_java_2=重新下载; 
		}
		list.add(new OnLongClickCommandMapping("删除图片消息",LONGCLICK_COMMAND.DELETE));		//ChatMessageWarpper_Image_java_3=删除图片消息; 
		//list.add(new OnLongClickCommandMapping("转发",LONGCLICK_COMMAND.FORWARD));		//ChatMessageWarpper_FlashEmotion_java_3=转发; 
		list.add(new OnLongClickCommandMapping("取消",LONGCLICK_COMMAND.CANCEL));		//ChatMessageWarpper_FlashEmotion_java_4=取消; 
		return list;
	}
	@Override
	public String getDescribe() {
		return "[图片]";		//ChatMessageWarpper_Image_java_4=[图片];
	}
	/*----------------------下载回调-------------------------*/
	public void onDownloadStart() {
		this.mSendTextState = SEND_TEXT_STATE.SEND_PREPARE;
		this.updateChatMessageState(Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_OVER,false);
		this.observerUpdate(Subject.COMMAND.COMMAND_IMAGE_DOWNLOADING, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_LOADING, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_IMAGE_LAYER, null);
	}
	public void onDownloadSuccess(String imageUrl,Bitmap bitmap) {
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
		this.updateChatMessageState(Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_OVER,false);
		Map<String,Object> data = new HashMap<String, Object>(1);
		data.put(Subject.DATA.COMMAND_IMAGE_DOWNLOAD_OVER,bitmap);
		this.observerUpdate(Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_OVER, data);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING, null);
	}
	public void onDownloadError() {
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
		this.updateChatMessageState(Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR,false);
		this.observerUpdate(Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING, null);
	}
	/*----------------------上传回调-------------------------*/
	private void onUploadSuccess() {
		this.observerUpdate(Subject.COMMAND.COMMAND_UPLOAD_IMAGE_OVER, null);
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO, "图片上穿成功,开始发送文本信息");
		}
		ChatMessageSender.getInstance()			
				.sendMessageToNet(ChatMessageWarpper_Image.this,false);
	}
	public void onUploadError() {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO, "图片上穿失败");
			//Logger.traces(Logger.SEND_PHOTO);
		}
		this.updateChatMessageState(Subject.COMMAND.COMMAND_UPLOAD_IMAGE_ERROR,false);
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
		this.observerUpdate(Subject.COMMAND.COMMAND_UPLOAD_IMAGE_ERROR, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING, null);
	}
	public void onUploadStart() {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO, "开始上传图片");
		}
		this.updateChatMessageState(Subject.COMMAND.COMMAND_UPLOAD_IMAGE_OVER,false);
		this.observerUpdate(Subject.COMMAND.COMMAND_IMAGE_UPLOADING, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_IMAGE_LAYER, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_LOADING, null);
		this.observerUpdate(Subject.COMMAND.COMMAND_UPLOAD_IMAGE_OVER, null);
		this.mSendTextState = SEND_TEXT_STATE.SEND_PREPARE;
	}
	public void onSendTextSuccess() {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO, "图片发送成功");
		}
		super.onSendTextSuccess();
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_IMAGE_LAYER, null);
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
	}
	@Override
	public void onSendTextError() {
		if(Logger.mDebug){
			Logger.errord(Logger.SEND_PHOTO, "图片发送失败");
		}
		super.onSendTextError();
		this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_IMAGE_LAYER, null);
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
	}
	@Override
	public void onErrorCode() {
		this.addErrorCode(Subject.COMMAND.COMMAND_MESSAGE_ERROR)
			  .addErrorCode(Subject.COMMAND.COMMAND_UPLOAD_IMAGE_ERROR)
		      .addErrorCode(Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR);
	}
	public void swapDataFromXML(Message message) {
		this.mMineType = message.mBody.image.mMineType;
		this.mTinyUrl = message.mBody.image.mTiny;
		this.mMainUrl = message.mBody.image.mMain;
		this.mLargeUrl = message.mBody.image.mLarge;
		this.mIsBrushPen = message.mBody.image.isBrushImage()?IS_BRUSH.YES:IS_BRUSH.NO;
		String dir = ChatUtil.getUserPhotos_Tiny(this.mToChatUserId);
		this.mMessageContent = dir+FileUtil.getInstance().getFileNameFromURL(this.mTinyUrl);
	}
	public long getToId() {
		return this.mToChatUserId;
	}
	public void onUploadOver(String tinyUrl, String mainUrl, String largeUrl) {
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO,"图片上传到服务器上 tinyUrl="+tinyUrl+"#mainUrl="+mainUrl+"#largeUrl="+largeUrl);
		}
		ContentValues values = new ContentValues(4);
		this.mTinyUrl = tinyUrl;
		this.mMainUrl = mainUrl;
		this.mLargeUrl =largeUrl;
		values.put(ChatHistory_Column.MAIN_URL, this.mMainUrl);
		values.put(ChatHistory_Column.TINY_URL, this.mTinyUrl);
		values.put(ChatHistory_Column.LARGE_URL,this.mLargeUrl);
		values.put(ChatHistory_Column.MESSAGE_STATE, Subject.COMMAND.COMMAND_UPLOAD_IMAGE_OVER);
		ChatDataHelper.getInstance().update(this, values);
		this.onUploadSuccess();
	}
	public boolean isBrush(){
		return this.mIsBrushPen==IS_BRUSH.YES;
	}
	@Override
	public boolean isShowCover() {
		// TODO Auto-generated method stub
		return true;
	}	
	
	/*处理发送来的消息*/
	public void processOutToLocal(final ChatItemHolder holder){
		
       TagResponse<String>response = new TagResponse<String>(this.mTinyUrl) {
			
			@Override
			public void failed() {
				if(Logger.mDebug){
					Logger.logd("加载图像失败 mTinyUrl="+mTinyUrl);
				}
				holder.mMessage_Image_ImageView.setImageBitmap(ChatUtil.getDefualtBitmap(true));
				if(mMessageState!=Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR){
					holder.mMessage_Error_ImageView.setVisibility(View.GONE);
				}else{
					holder.mMessage_Error_ImageView.setVisibility(View.VISIBLE);
				}
				onDownloadError();
			}
			
			@Override
			protected void success(final Bitmap bitmap, final String tag) {
				if(tag.equals(mTinyUrl)){
					RenrenChatApplication.getUiHandler().post(new Runnable() {
						@Override
						public void run() {
							if(bitmap!=null && !bitmap.isRecycled()){
								holder.mMessage_Image_ImageView.setImageBitmap(bitmap);
							}else{
								holder.mMessage_Image_ImageView
															.setImageBitmap(ChatUtil.getDefualtBitmap(true));
							}
							onDownloadSuccess(tag,bitmap);
						}
					});
				}
			}
			
		}; 
		onDownloadStart();
		mChatHttpImageLoader.get(new HttpImageRequest(mTinyUrl, true), response);
//		
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
	}
	/*处理发送出去的消息*/
	public  void processLocalToOut(final ChatItemHolder holder){
		final String imgPath = mMessageContent+(isBrush()?"":"_small");
//		if(new File(imgPath).exists()){
//			holder.mMessage_Image_ImageView.setImagePath(imgPath,false);
//		}else{
//			download(false);
//		}
		  TagResponse<String>response = new TagResponse<String>(imgPath) {				
				@Override
				public void failed() {
					if(Logger.mDebug){
						Logger.logd("加载图像失败 mTinyUrl="+imgPath);
					}
					holder.mMessage_Image_ImageView.setImageBitmap(ChatUtil.getDefualtBitmap(false));
					if(mMessageState!=Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR){
						holder.mMessage_Error_ImageView.setVisibility(View.GONE);
					}else{
						holder.mMessage_Error_ImageView.setVisibility(View.VISIBLE);
					}
					//onDownloadError();
				}				
				@Override
				protected void success(final Bitmap bitmap, final String tag) {
					if(tag.equals(imgPath)){
						RenrenChatApplication.getUiHandler().post(new Runnable() {
							@Override
							public void run() {
								holder.mMessage_Image_ImageView.setImageBitmap(bitmap);
								//onDownloadSuccess(tag,bitmap);
							}
						});
					}
				}
			}; 
			//onDownloadStart();
			mChatHttpImageLoader.get(new Request(){

				@Override
				public int type() {
					return Request.TYPE_FILE;
				}

				@Override
				public int resId() {
					return 0;
				}

				@Override
				public String path() {
					return imgPath;
				}

				@Override
				public boolean allowDownload() {
					return false;
				}
				
			}, response);
	}
}

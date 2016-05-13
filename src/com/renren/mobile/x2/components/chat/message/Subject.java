package com.renren.mobile.x2.components.chat.message;




/**
 * @author dingwei.chen
 * 被观察者接口
 * */
public interface Subject {

	public static interface COMMAND{
		int COMMAND_HEAD_IMAGE_DOWNLOAD_OVER = 0;//头像下载完成
		
		int COMMAND_DOWNLOAD_IMAGE_OVER = 1;//图片下载完成
		int COMMAND_UPLOAD_VOICE_ERROR = 2;//上传语音失败
		int COMMAND_DOWNLOAD_VOICE_ERROR = 3;//下载语音失败
		int COMMAND_UPLOAD_IMAGE_ERROR = 4;//上传图片失败
		int COMMAND_DOWNLOAD_IMAGE_ERROR = 5;//下载图片失败
		
		/**发送图片上传完成 */
		int COMMAND_UPLOAD_IMAGE_OVER = 6;
		
		/**消息发送失败 */
		int COMMAND_MESSAGE_ERROR = 7;
		/**消息发送成功 */
		int COMMAND_MESSAGE_OVER =8;
		
		int COMMAND_UPLOAD_VOICE_OVER = 9;//语音上传成功
		/** 语音下载成功 */
		int COMMAND_DOWNLOAD_VOICE_OVER = 10;
		/**语音听完 */
		int COMMAND_PLAY_VOICE_OVER = 11;
		
		int COMMAND_UPDATE_VOICE_TIME=12;//变更声音时间
		int COMMAND_UPDATE_VOICE_VIEW=14;//更新声音消息喇叭图片
		int COMMAND_UPDATE_SEND_METHOD=15;//更新发送方法
		int COMMAND_UPDATE_FLASH_IMAGE = 16;//变更图片
		
		int COMMAND_VOICE_UPLOADING = 17;//语音上传
		int COMMAND_IMAGE_UPLOADING = 18;//图片上传
		int COMMAND_IMAGE_UPLOAD_OVER = 19;//图片上传成功
		int COMMAND_IMAGE_DOWNLOADING = 20;//图片下载
		int COMMAND_VOICE_DOWNLOADING = 21;//语音下载
		
		int COMMAND_MESSAGE_SEND_SHOW_LOADING =22;//消息发送准备
		int COMMAND_MESSAGE_SEND_HIDE_LOADING =23;//消息发送准备
		int COMMAND_MESSAGE_SEND_SHOW_IMAGE_LAYER =24;//消息发送准备
		int COMMAND_MESSAGE_SEND_HIDE_IMAGE_LAYER =25;//消息发送准备
		int COMMAND_MESSAGE_SEND_SHOW_VOICE_UNLISTEN =26;//消息发送准备
		int COMMAND_MESSAGE_SEND_HIDE_VOICE_UNLISTEN =27;//消息发送准备
		
		int COMMAND_UPDATE_BACKGROUND = 28;
		
	}
	public static interface DATA{
		String COMMAND_HEAD_IMAGE_DOWNLOAD_OVER = "COMMAND_HEAD_IMAGE_DOWNLOAD_OVER";
		String COMMAND_IMAGE_DOWNLOAD_OVER = "COMMAND_IMAGE_DOWNLOAD_OVER";
		String COMMAND_UPDATE_VOICE_TIME="COMMAND_UPDATE_VOICE_TIME";
		String COMMAND_UPDATE_TEXT = "COMMAND_UPDATE_TEXT";//变更文本
		String COMMAND_UPDATE_VOICE_VIEW="COMMAND_UPDATE_VOICE_VIEW";//更新声音消息喇叭图片
		String COMMAND_UPDATE_SEND_METHOD = "COMMAND_UPDATE_SEND_METHOD";//变更发送模式
		String COMMAND_UPDATE_IMAGE = "COMMAND_UPDATE_IMAGE";//变更图片
		String COMMAND_IMAGE_UPLOADING = "COMMAND_IMAGE_UPLOADING";//图片上传进度条
		String COMMAND_IMAGE_DOWNLOADING = "COMMAND_IMAGE_DOWNLOADING";//图片上传进度条
		String COMMAND_UPDATE_BACKGROUND = "COMMAND_UPDATE_BACKGROUND";
	}
	
	
	
	public void registorObserver(Observer observer);
	public void unregistorObserver(Observer observer);
}

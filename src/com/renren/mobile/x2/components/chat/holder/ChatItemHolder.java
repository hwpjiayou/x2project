package com.renren.mobile.x2.components.chat.holder;

import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.Observer;
import com.renren.mobile.x2.components.chat.message.Subject;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.view.BaseImageView;
import com.renren.mobile.x2.components.chat.view.EmotionTextView;
import com.renren.mobile.x2.components.chat.view.ListViewContentLayout;
import com.renren.mobile.x2.components.chat.view.SwitchImageView;
import com.renren.mobile.x2.components.chat.view.VoiceLinearLayout;
import com.renren.mobile.x2.utils.SystemService;
import com.renren.mobile.x2.utils.log.Logger;


/**
 * 单条消息的布局
 */
public final class ChatItemHolder implements Observer {
	
	/**整个消息 */
	public View mRootView = null;
	
	
	/**消息的时间 */
	public TextView mMessage_Time_TextView = null;
	/** 弱消息提醒 */
    public TextView mMessageNotify_TextView = null;
	/** 空消息 */
	public View mMessageNull_View = null;
	
	
	/**OUT_TO_LOCAL的消息  接收的消息条目视图,视图中包含mMessageFrom_HeadImage_ImageView和mMessageFrom_Content_LinearLayout*/
	public ViewGroup mMessageFrom_LinearLayout = null;
	/** LOCAL_TO_OUT的消息 内容区域 （条目中包含有头像和消息内容） */
	public ViewGroup mMessageTo_LinearLayout = null;// 发送出去的消息内容区域
		
		
    /**发过过来的消息布局的holder */
	InitViewHolder_From mFromInit = null;
	/**发送出去的消息布局的holder */
	InitViewHolder_To mToInit = null;
	
	/**消息内容区域 */
	public ViewGroup mMessage_Content_LinearLayout = null;
	/**除附件外的消息的内容区域 */
	public ListViewContentLayout mMessage_Content_ViewGroup = null;
	
	
	/**发送或接受的文本消息的布局 */
	public LinearLayout mMessage_TextMessage_LinearLayout = null;
	/**发送或接受的文本消息 */
	public EmotionTextView mMessage_TextView = null;
	
	/**图片消息的布局 */
	public ViewGroup mMessage_ImageMessage_LinearLayout = null;
	/**图片消息 */
	public BaseImageView mMessage_Image_ImageView = null;
	
	/**声音消息的布局 */
	public VoiceLinearLayout mMessage_VoiceMessage_LinearLayout = null;
	/**声音消息中的小喇叭 */
	public SwitchImageView mMessage_Voice_ImageView = null;
	/** 语音消息的时间 */
	public TextView mMessage_Voice_Time_TextView = null;
	
	/**flash表情布局 */
	public LinearLayout mMessage_FlashMessage_LinearLayout = null;
	/** flash图片 */
	public ImageView mMessage_Flash_ImageView = null;//
	/**flash文本 */
	public TextView mMessage_Flash_TextView = null;//
	
	/**显示消息正在发送的loading按钮 */
	public View mMessage_Loading = null;//加载控件
	
//	/**头像布局 */
	public FrameLayout mMessage_HeadImage_LinearLayout = null;
	/**头像 */
	public ImageView mMessage_HeadImage_ImageView = null;// 头像
	
	
	//附加控件
	/**发送或接受失败的icon */
	public ImageView mMessage_Error_ImageView = null;
	/**未读声音 */
	public View mMessage_Attach = null;
	/**消息来的域 */
	public View mMessage_Domain = null;
	/**消息发送失败 */
	public ImageView mMessage_Voice_Unlisten_ImageView = null;
	
		
	/**删除feed*/
	public View mMessage_FeedDeleteButton = null;
	
	
	//public ChatFeedView mMessage_FeedView = null;//新鲜事显示
	
	
	protected Subject mSubject = null;	

	public ChatItemHolder(Context context) {
		LayoutInflater inflater = SystemService.sInflaterManager;
		this.mRootView = inflater.inflate(R.layout.chat_listview_item, null);
		
		mMessage_Time_TextView = (TextView) this.mRootView
				.findViewById(R.id.cdw_listview_item_time);
		
		mMessageFrom_LinearLayout = (ViewGroup) this.mRootView
				.findViewById(R.id.chat_voice_from_linearlayout);
		
		mMessageTo_LinearLayout = (ViewGroup) this.mRootView
				.findViewById(R.id.chat_voice_to_linearlayout);
		
		mMessageNull_View = (View)this.mRootView
				.findViewById(R.id.chat_item_null);
		
		mMessageNotify_TextView = (TextView)this.mRootView
				.findViewById(R.id.chat_item_notify);
		
		mFromInit = new InitViewHolder_From(mMessageFrom_LinearLayout);
		
		mToInit = new InitViewHolder_To(mMessageTo_LinearLayout);
		
		mFromInit.initView(this, ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL);
		
		mToInit.initView(this, ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT);
	}

	public void selectView(int comefrom, int messageType) {

		switch (comefrom) {
		case ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL:
			mMessageFrom_LinearLayout.setVisibility(View.VISIBLE);
			mMessageTo_LinearLayout.setVisibility(View.GONE);
			mMessageNull_View.setVisibility(View.GONE);
			mMessageNotify_TextView.setVisibility(View.GONE);
			mFromInit.initView(this, messageType);
			mMessage_Voice_Unlisten_ImageView.setVisibility(View.GONE);
			break;

		case ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT:
			mMessageFrom_LinearLayout.setVisibility(View.GONE);
			mMessageTo_LinearLayout.setVisibility(View.VISIBLE);
			mMessageNull_View.setVisibility(View.GONE);
			mToInit.initView(this, messageType);
			mMessageNotify_TextView.setVisibility(View.GONE);
			mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			break;
		case ChatBaseItem.MESSAGE_COMEFROM.NOTIFY:
			mMessageFrom_LinearLayout.setVisibility(View.GONE);
			mMessageTo_LinearLayout.setVisibility(View.GONE);
			mMessageNull_View.setVisibility(View.GONE);
			mMessageNotify_TextView.setVisibility(View.VISIBLE);
			return;
		case ChatBaseItem.MESSAGE_COMEFROM.NULL:
			mMessageFrom_LinearLayout.setVisibility(View.GONE);
			mMessageTo_LinearLayout.setVisibility(View.VISIBLE);
			mMessageNotify_TextView.setVisibility(View.GONE);
			mToInit.initView(this, messageType);
			mMessageNull_View.setVisibility(View.VISIBLE);
			return;
		}
		this.clearData();
	}

	public View getRootView(){
		return this.mRootView;
	}
	
	private void clearData() {
		this.mMessage_TextMessage_LinearLayout.setVisibility(View.GONE);
		this.mMessage_ImageMessage_LinearLayout.setVisibility(View.GONE);
		this.mMessage_VoiceMessage_LinearLayout.setVisibility(View.GONE);
		this.mMessage_FlashMessage_LinearLayout.setVisibility(View.GONE);
	}

	private void updateText(int command, Map<String, Object> data) {
		if(Logger.mDebug){
			Logger.logd("updateText command ="+command);
		}
		switch (command) {
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_LOADING: {
			mMessage_Loading.setVisibility(View.VISIBLE);
			break;
		}
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING: {
			mMessage_Loading.setVisibility(View.GONE);
			break;
		}
		case Subject.COMMAND.COMMAND_MESSAGE_ERROR: {
			mMessage_Error_ImageView.setVisibility(View.VISIBLE);
			break;
		}
		case Subject.COMMAND.COMMAND_MESSAGE_OVER: {
			mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			break;
		}
		}
	}

	private void updateImage(int command, Map<String, Object> data) {
		
		switch (command) {
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_LOADING: {
			mMessage_Loading.setVisibility(View.VISIBLE);
			break;
		}
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING: {
			mMessage_Loading.setVisibility(View.GONE);
			break;
		}
		/*------------------------图片部分-----------------------------------*/
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_IMAGE_LAYER: {
			this.mMessage_Image_ImageView.setLayerVisible(true);
			break;
		}
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_IMAGE_LAYER: {
			this.mMessage_Image_ImageView.setLayerVisible(false);
			break;
		}
		/* 图片正在上传 */
		case Subject.COMMAND.COMMAND_IMAGE_UPLOADING: {
			this.mMessage_Image_ImageView.setLayerVisible(true);
			mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			break;
		}

		/* 图片上传失败 */
		case Subject.COMMAND.COMMAND_UPLOAD_IMAGE_ERROR: {
			mMessage_Error_ImageView.setVisibility(View.VISIBLE);
			this.mMessage_Image_ImageView.setLayerVisible(false);
			break;
		}
		/* 图片上传成功 */
		case Subject.COMMAND.COMMAND_UPLOAD_IMAGE_OVER: {
			mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			this.mMessage_Image_ImageView.setLayerVisible(false);
			break;
		}
		
		
		
		
		
		/* 下载图片完成 */
		case Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_OVER: {
			this.mMessage_Image_ImageView.setVisibility(View.VISIBLE);
			if (data != null) {
				Bitmap bitmap = (Bitmap) data
						.get(Subject.DATA.COMMAND_IMAGE_DOWNLOAD_OVER);
				mMessage_Image_ImageView.setImageBitmap(bitmap);
			}
			this.mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			this.mMessage_Image_ImageView.setLayerVisible(false);
			break;
		}
		/* 图片正在下载 */
		case Subject.COMMAND.COMMAND_IMAGE_DOWNLOADING: {
			this.mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			this.mMessage_Image_ImageView.setImageBitmap(ChatUtil.getDefualtBitmap(true));
			this.mMessage_Image_ImageView.setLayerVisible(true);
			this.mMessage_Loading.setVisibility(View.VISIBLE);
			break;
		}
		/* 图片下载失败 */
		case Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR: {
			mMessage_Error_ImageView.setVisibility(View.VISIBLE);
			this.mMessage_Image_ImageView.setLayerVisible(false);
			mMessage_Image_ImageView.setImageBitmap(ChatUtil.getDefualtBitmap(true));
			break;
		}
		}
	}

	public void updateVoice(int command, Map<String, Object> data) {
		switch (command) {
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_LOADING: {
			mMessage_Loading.setVisibility(View.VISIBLE);
			break;
		}
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING: {
			mMessage_Loading.setVisibility(View.GONE);
			break;
		}
		/*------------------------语音部分-----------------------------------*/
	
		/* 上传语音失败 */
		case Subject.COMMAND.COMMAND_UPLOAD_VOICE_ERROR: {
			mMessage_Error_ImageView.setVisibility(View.VISIBLE);
			mMessage_Voice_Time_TextView.setVisibility(View.VISIBLE);
			if (data != null) {
				mMessage_Voice_Time_TextView.setText((String) data
						.get(Subject.DATA.COMMAND_UPDATE_VOICE_TIME));
				mMessage_Voice_ImageView.switchImage((Integer) data
						.get(Subject.DATA.COMMAND_UPDATE_VOICE_VIEW));
			}
			break;
		}
		case Subject.COMMAND.COMMAND_UPLOAD_VOICE_OVER: {
			mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			mMessage_Voice_Time_TextView.setVisibility(View.VISIBLE);
			if (data != null) {
				mMessage_Voice_Time_TextView.setText((String) data
						.get(Subject.DATA.COMMAND_UPDATE_VOICE_TIME));
				mMessage_Voice_ImageView.switchImage((Integer) data
						.get(Subject.DATA.COMMAND_UPDATE_VOICE_VIEW));
			} 
			break;
		}
		/* 语音开始上传 */
		case Subject.COMMAND.COMMAND_VOICE_UPLOADING: {
			mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			break;
		}
		
		/*===========================下载==========================*/
		/* 语音开始下载 */
		case Subject.COMMAND.COMMAND_VOICE_DOWNLOADING: {
			mMessage_Error_ImageView.setVisibility(View.INVISIBLE);//接受失败
			mMessage_Voice_Unlisten_ImageView.setVisibility(View.GONE);
			break;
		}
		/* 语音下载失败 */
		case Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_ERROR: {
			mMessage_Error_ImageView.setVisibility(View.VISIBLE);
			break;
		}
		/* 语音下载成功 */
		case Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_OVER: {
			mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
			break;
		}
		/* 语音播放结束 */
		case Subject.COMMAND.COMMAND_PLAY_VOICE_OVER: {
			if (data != null) {
				mMessage_Voice_Time_TextView.setText((String) data
						.get(Subject.DATA.COMMAND_UPDATE_VOICE_TIME));
				mMessage_Voice_ImageView.switchImage((Integer) data
						.get(Subject.DATA.COMMAND_UPDATE_VOICE_VIEW));
			}
			break;
		}
		/* 语音变更音量 */
		case Subject.COMMAND.COMMAND_UPDATE_VOICE_VIEW: {
			mMessage_Voice_ImageView.switchImage((Integer) data
					.get(Subject.DATA.COMMAND_UPDATE_VOICE_VIEW));
			break;
		}
		/* 修改语音时间动画 */
		case Subject.COMMAND.COMMAND_UPDATE_VOICE_TIME: {
			if (data != null) {
				this.mMessage_Voice_Time_TextView.setVisibility(View.VISIBLE);
				String text = (String) data
						.get(Subject.DATA.COMMAND_UPDATE_VOICE_TIME);
				this.mMessage_Voice_Time_TextView.setText(text);
			}
			break;
		}
		/* 修改语音未读已读 */
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_VOICE_UNLISTEN: {
			if (this.mMessage_Voice_Unlisten_ImageView != null) {
				this.mMessage_Voice_Unlisten_ImageView.setVisibility(View.VISIBLE);
			}
			break;
		}
		/* 修改语音时间动画 */
		case Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_VOICE_UNLISTEN: {
			if (this.mMessage_Voice_Unlisten_ImageView != null) {
				this.mMessage_Voice_Unlisten_ImageView.setVisibility(View.GONE);
			}
			break;
		}
		}
	}
	public void updateFlash(int command, Map<String, Object> data){
		switch (command) {

		/*------------------------更新flash消息---------------------------*/
		case Subject.COMMAND.COMMAND_UPDATE_FLASH_IMAGE: {
			if (data != null) {
				mMessage_Flash_ImageView.setImageBitmap((Bitmap) data
						.get(Subject.DATA.COMMAND_UPDATE_IMAGE));
			}
			break;
		}
		}
	}
	private void updateBack(int command,Map<String, Object> data){
		switch (command) {
			case Subject.COMMAND.COMMAND_UPDATE_BACKGROUND:
				int backId = (Integer)data.get(Subject.DATA.COMMAND_UPDATE_BACKGROUND);
				if(this.mMessage_Content_ViewGroup instanceof ListViewContentLayout){
					((ListViewContentLayout)this.mMessage_Content_ViewGroup).setForegroundAlpha(backId);
				}
				break;
	
			default:
				break;
		}
	}
	

	@Override
	public void update(int command, Map<String, Object> data) {
		this.updateText(command, data);
		this.updateFlash(command, data);
		this.updateImage(command, data);
		this.updateVoice(command, data);
		this.updateBack(command, data);
	}

	@Override
	public void unregistorSubject() {
		if (mSubject != null) {
			mSubject.unregistorObserver(this);
		}
		this.mSubject = null;
	}

	@Override
	public void registorSubject(Subject subject) {
		this.mSubject = subject;
	}
	
	
}

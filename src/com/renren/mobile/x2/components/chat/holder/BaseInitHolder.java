package com.renren.mobile.x2.components.chat.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.view.BaseImageView;
import com.renren.mobile.x2.components.chat.view.EmotionTextView;
import com.renren.mobile.x2.components.chat.view.ListViewContentLayout;
import com.renren.mobile.x2.components.chat.view.SwitchImageView;
import com.renren.mobile.x2.components.chat.view.VoiceLinearLayout;
import com.renren.mobile.x2.utils.ViewMapping;


public abstract class BaseInitHolder {
	
	@ViewMapping(ID=R.id.chat_voice_content_linearlayout)
	public ViewGroup mMessage_Content_LinearLayout=null;//发送出去的消息内容区域
	
	@ViewMapping(ID=R.id.chat_listview_item_content_viewgroup)	
	public ListViewContentLayout mMessage_Content_ViewGroup = null;//发送来的内容区域
	
	@ViewMapping(ID=R.id.chat_voice_textmessage_linearlayout)	
	public LinearLayout mMessage_TextMessage_LinearLayout = null;//文本消息
	
	@ViewMapping(ID=R.id.chat_voice_textmessage_textview)	
	public EmotionTextView mMessage_TextView = null;//文本消息文本控件
	
	@ViewMapping(ID=R.id.chat_voice_imagemessage_linearlayout)	
	public ViewGroup mMessage_ImageMessage_LinearLayout = null;//图片消息
	
	@ViewMapping(ID=R.id.chat_voice_imagemessage_imageview)	
	public BaseImageView mMessage_Image_ImageView = null;//图片消息图片
	
	@ViewMapping(ID=R.id.chat_voice_voicemessage_linearlayout)	
	public VoiceLinearLayout mMessage_VoiceMessage_LinearLayout = null;//声音消息
	@ViewMapping(ID=R.id.chat_voice_voicemessage_imageview)	
	public SwitchImageView mMessage_Voice_ImageView = null;//语音喇叭图片
	@ViewMapping(ID=R.id.chat_voice_voicemessage_textview)	
	public TextView mMessage_Voice_Time_TextView = null;//语音消息的时间
	
	@ViewMapping(ID=R.id.chat_voice_flashmessage_linearlayout)	
	public LinearLayout mMessage_FlashMessage_LinearLayout = null;//flash表情
	@ViewMapping(ID=R.id.chat_voice_flashmessage_imageview)	
	public ImageView mMessage_Flash_ImageView = null;//flash图片
	@ViewMapping(ID=R.id.chat_voice_flashmessage_textview)	
	public TextView mMessage_Flash_TextView = null;//flash文本
	
	@ViewMapping(ID=R.id.cdw_chat_listview_item_loading)	
	public View mMessage_Loading = null;//消息发送loading框
	
	@ViewMapping(ID=R.id.chat_voice_head_imageview)	
	public ImageView mMessage_HeadImage_ImageView = null;//头像
	@ViewMapping(ID=R.id.chat_voice_head_linearlayout)	
	public FrameLayout mMessage_HeadImage_LinearLayout = null;//头像
	
	@ViewMapping(ID=R.id.cdw_chat_listview_item_send_error_imageview)
	public ImageView mMessage_Error_ImageView =  null;//接收失败的icon
	
	@ViewMapping(ID=R.id.cdw_chat_listview_item_voice_attach)
	public View mMessage_Attach = null; //未读声音
	
	@ViewMapping(ID=R.id.feed_delete)
	public View mMessage_FeedDeleteButton = null; //删除feed
	
	@ViewMapping(ID=R.id.cdw_chat_listview_item_domain)
	public View mMessage_Domain = null;
	
//	@ViewMapping(ID=R.id.cdw_feed_view)
//	public ChatFeedView mMessage_FeedView = null;
	
	
	
	
	
	
	
	public void initView(ChatItemHolder holder, int messageType) {
		holder.mMessage_HeadImage_LinearLayout = this.mMessage_HeadImage_LinearLayout;
		holder.mMessage_TextMessage_LinearLayout = this.mMessage_TextMessage_LinearLayout;
		holder.mMessage_VoiceMessage_LinearLayout = this.mMessage_VoiceMessage_LinearLayout;
		holder.mMessage_ImageMessage_LinearLayout = this.mMessage_ImageMessage_LinearLayout;
		holder.mMessage_FlashMessage_LinearLayout = this.mMessage_FlashMessage_LinearLayout;
		holder.mMessage_TextView = mMessage_TextView;
		holder.mMessage_HeadImage_ImageView = mMessage_HeadImage_ImageView;//头像
		holder.mMessage_Image_ImageView = mMessage_Image_ImageView;//图片消息
		holder.mMessage_Voice_ImageView = mMessage_Voice_ImageView;
		holder.mMessage_Voice_Time_TextView = mMessage_Voice_Time_TextView;
		holder.mMessage_Flash_ImageView= mMessage_Flash_ImageView;//图片消息
		holder.mMessage_Flash_TextView=mMessage_Flash_TextView;
		holder.mMessage_Error_ImageView = mMessage_Error_ImageView;
		holder.mMessage_Loading = this.mMessage_Loading;
		//holder.mMessage_FeedView = this.mMessage_FeedView;
		holder.mMessage_Content_LinearLayout = this.mMessage_Content_LinearLayout;
		holder.mMessage_Content_ViewGroup = this.mMessage_Content_ViewGroup;
		//holder.mMessage_UserName = this.mMessage_UserName;
		holder.mMessage_Attach = this.mMessage_Attach;
		holder.mMessage_FeedDeleteButton = this.mMessage_FeedDeleteButton;
		holder.mMessage_Domain = this.mMessage_Domain;
		this.initViewBySubClass(holder, messageType);
	}
	public abstract void initViewBySubClass(ChatItemHolder holder, int messageType);
	
}

package com.renren.mobile.x2.components.chat.view;



import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.DipUtil;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;

public class ListItemLayout extends LinearLayout {
	
	
	@ViewMapping(ID=R.id.chat_voice_content_linearlayout)
	public ViewGroup mMessage_Content_LinearLayout=null;//发送出去的消息内容区域
	@ViewMapping(ID=R.id.chat_listview_item_content_viewgroup)	
	public LinearLayout mMessage_Content_ViewGroup = null;//发送来的内容区域
	
	@ViewMapping(ID=R.id.chat_voice_textmessage_linearlayout)	
	public LinearLayout mMessage_TextMessage_LinearLayout = null;//文本消息
	@ViewMapping(ID=R.id.chat_voice_imagemessage_linearlayout)	
	public LinearLayout mMessage_ImageMessage_LinearLayout = null;//图片消息
	@ViewMapping(ID=R.id.chat_voice_voicemessage_linearlayout)	
	public VoiceLinearLayout mMessage_VoiceMessage_LinearLayout = null;//声音消息
	@ViewMapping(ID=R.id.chat_voice_flashmessage_linearlayout)	
	public LinearLayout mMessage_FlashMessage_LinearLayout = null;//flash表情
	
	@ViewMapping(ID=R.id.chat_voice_textmessage_textview)	
	public EmotionTextView mMessage_TextView = null;//文本消息文本控件
	
	@ViewMapping(ID=R.id.chat_voice_imagemessage_imageview)	
	public BaseImageView mMessage_Image_ImageView = null;//图片消息图片
	@ViewMapping(ID=R.id.chat_voice_voicemessage_imageview)	
	public SwitchImageView mMessage_Voice_ImageView = null;//语音喇叭图片
	@ViewMapping(ID=R.id.chat_voice_flashmessage_imageview)	
	public ImageView mMessage_Flash_ImageView = null;//flash图片
	@ViewMapping(ID=R.id.chat_voice_flashmessage_textview)	
	public TextView mMessage_Flash_TextView = null;//flash文本
	@ViewMapping(ID=R.id.chat_voice_voicemessage_textview)	
	public TextView mMessage_Voice_Time_TextView = null;//语音消息的时间
	@ViewMapping(ID=R.id.cdw_chat_listview_item_loading)	
	public View mMessage_Loading = null;//消息发送loading框
//	@ViewMapping(ID=R.id.cdw_feed_view)
//	public ChatFeedView mMessage_FeedView = null;
	@ViewMapping(ID=R.id.cdw_chat_listview_item_send_error_imageview)
	public ImageView mMessage_Error_ImageView =  null;//接收失败的icon
	@ViewMapping(ID=R.id.cdw_chat_listview_item_voice_attach)
	public View mMessage_Attach = null;
//	@ViewMapping(ID=R.id.chat_voice_user_name)
//	public TextView mMessage_UserName = null;
	@ViewMapping(ID=R.id.feed_delete)
	public View mMessage_FeedDeleteButton = null;
	
	
	int mDeleteY = DipUtil.calcFromDip(6);
	int mDeleteX = DipUtil.calcFromDip(3);
	
	public ListItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	@Override
	protected void onFinishInflate() {
		ViewMapUtil.viewMapping(this, this);
	};
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(isRight()&&this.mMessage_FeedDeleteButton.getVisibility()==View.VISIBLE){
			this.setMeasuredDimension(getMeasuredWidth(), this.getMeasuredHeight()+mDeleteY);
		}
	};

	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//		mMessage_Attach.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		super.onLayout(changed, left, top, right, bottom);
		if(isRight()){
			layoutView(mMessage_Content_ViewGroup,
											right, 
											bottom);
			int r =mMessage_Content_ViewGroup.getLeft();
			int l = r-mMessage_Attach.getMeasuredWidth();
			mMessage_Attach.layout(l, bottom-mMessage_Content_ViewGroup.getMeasuredHeight(), r, bottom);
		}else{
			mMessage_Content_ViewGroup.layout(0, top, mMessage_Content_ViewGroup.getMeasuredWidth(),bottom);
			mMessage_Attach.layout(mMessage_Content_ViewGroup.getMeasuredWidth(), top, mMessage_Content_ViewGroup.getMeasuredWidth()+mMessage_Attach.getMeasuredWidth(), bottom);
		}
		
	};
	boolean isRight(){
		return this.mMessage_FeedDeleteButton!=null;
	}
	
	
	public void layoutView(View view,int right,int bottom){
		
			view.layout(right-this.getLeft()-view.getMeasuredWidth(), 
									getBottom()-view.getMeasuredHeight(), 
									right-this.getLeft(), 
									getBottom());
			if(this.mMessage_FeedDeleteButton!=null&&this.mMessage_FeedDeleteButton.getVisibility()==View.VISIBLE){
				mMessage_FeedDeleteButton.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				int width = this.mMessage_FeedDeleteButton.getMeasuredWidth();
				int l = view.getLeft()-mDeleteX;
				int t = view.getTop()-mDeleteY;
				this.mMessage_FeedDeleteButton.layout(
						l, 
						t, 
						l+width,  
						t+this.mMessage_FeedDeleteButton.getMeasuredHeight());
			}
	}
	
}

package com.renren.mobile.x2.components.chat.facade;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.face.IMessageOnClickListener;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem.MESSAGE_COMEFROM;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem.MESSAGE_TYPE;
import com.renren.mobile.x2.components.chat.view.ListViewContentLayout;
import com.renren.mobile.x2.network.talk.DomainUrl;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener.SEND_TEXT_STATE;
import com.renren.mobile.x2.utils.DipUtil;
import com.renren.mobile.x2.utils.log.Logger;




/**
 * @author dingwei.chen
 * @说明 聊天条目的装饰器(也被用来处理视图事件)
 * */
public abstract class ChatItemFacader {
	
	public static final int DIP_SIX = DipUtil.calcFromDip(6);
	public static final int DIP_FORTEEN = DipUtil.calcFromDip(14);
	public static final int DIP_SIXTEEN = DipUtil.calcFromDip(16);
	public static final int DIP_TEN = DipUtil.calcFromDip(10);
	public static final int BOTTOM = DipUtil.calcFromDip(6);
	
	public void facadeMessage(ChatItemHolder holder,ChatMessageWarpper chatmessage,final  IMessageOnClickListener iClick){
		holder.mMessage_Attach.setVisibility(View.VISIBLE);
		holder.mMessage_HeadImage_LinearLayout.setVisibility(View.VISIBLE);
		View view = getFacadeView(holder);
		
		if(view!=null){
			view.setVisibility(View.VISIBLE);
		}else{ //ChatItemFacader_Null 会返回空 <TODO cf> 查一下在什么情况下显示
			this.showCover(holder, chatmessage);
			this.showBackground(holder, chatmessage, iClick);//显示背景
			this.showFeed(holder, chatmessage, iClick);//显示新鲜事
			holder.mMessage_HeadImage_LinearLayout.setVisibility(View.VISIBLE);
			this.facade(holder, chatmessage,iClick);
			return;
		}
		holder.mRootView.setVisibility(View.VISIBLE);
		holder.mMessage_Content_ViewGroup.setOnClickListener(null);//清空事件响应
		this.showBackground(holder, chatmessage, iClick);//显示背景
		this.showFeed(holder, chatmessage, iClick);//显示新鲜事
		this.showLoading(holder, chatmessage, iClick);//显示loading框
		this.showError(holder, chatmessage, iClick);//显示错误
		this.showDomain(holder, chatmessage);
		//this.hideFeedDeleteButton(holder, chatmessage);
		this.facade(holder, chatmessage,iClick);
		this.showCover(holder, chatmessage);
	}
	
	// 这个地方是是新鲜事回复时候带的背景
	private void showCover(ChatItemHolder holder,ChatMessageWarpper chatmessage){
		if(chatmessage.isShowCover()){
			holder.mMessage_Content_ViewGroup.showCover(chatmessage.mComefrom==MESSAGE_COMEFROM.OUT_TO_LOCAL);
		}else{
			holder.mMessage_Content_ViewGroup.hideCover();
		}
	}
	
	
	
	private void showDomain(ChatItemHolder holder,ChatMessageWarpper chatmessage){
		if(holder.mMessage_Domain!=null){
			holder.mMessage_Domain.setVisibility(View.GONE);
		}
		if(chatmessage.mComefrom==ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL && holder.mMessage_Domain!=null){
//			if(DomainUrl.RENREN_SIXIN_DOMAIN.equals(chatmessage.mDomain)){
//				holder.mMessage_Domain.setVisibility(View.VISIBLE);
//			}
		}
	}
	
	
	
//	private void hideFeedDeleteButton(ChatItemHolder holder,ChatMessageWarpper chatmessage){
//		if(holder.mMessage_FeedDeleteButton!=null){
//			holder.mMessage_FeedDeleteButton.setVisibility(View.GONE);
//		}
//	}
	
	private void showBackground(ChatItemHolder holder,ChatMessageWarpper chatmessage,final  IMessageOnClickListener iClick){
		if(chatmessage.mComefrom==ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT){
			holder.mMessage_Content_ViewGroup.setBackgroundResource(R.drawable.chat_listview_item_right);
		}else if(chatmessage.mComefrom==ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL){
			holder.mMessage_Content_ViewGroup.setBackgroundResource(R.drawable.chat_listview_item_left);
		}
		
		if(holder.mMessage_Content_ViewGroup instanceof ListViewContentLayout){
			((ListViewContentLayout)holder.mMessage_Content_ViewGroup).setForegroundAlpha(0);
		}
//		if(chatmessage.mComefrom==ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT){
//			holder.mMessage_HeadImage_LinearLayout.setPadding( DipUtil.calcFromDip(6), 0, DipUtil.calcFromDip(5), 0);
//		}else{
//			holder.mMessage_HeadImage_LinearLayout.setPadding( DipUtil.calcFromDip(5), 0, DipUtil.calcFromDip(6), 0);
//		}
		
		if(chatmessage.isHideBackground()){
			Drawable drawable= holder.mMessage_Content_ViewGroup.getBackground();
			Rect r = new Rect();
			drawable.getPadding(r);
			holder.mMessage_Content_ViewGroup.setBackgroundColor(0x00000000);
			holder.mMessage_Content_ViewGroup.setPadding(r.left, 0, r.right, 0);
		}else{
			if (chatmessage.getMessageType() == MESSAGE_TYPE.IMAGE || chatmessage
					.getMessageType() == MESSAGE_TYPE.FLASH) {
				if(chatmessage.mComefrom==MESSAGE_COMEFROM.OUT_TO_LOCAL){
					holder.mMessage_Content_ViewGroup
							.setPadding(DIP_FORTEEN, DIP_SIX, DIP_SIX, DIP_SIX);
				}else{
					holder.mMessage_Content_ViewGroup
						.setPadding(DIP_SIX, DIP_SIX, DIP_FORTEEN,DIP_SIX);
				}
			} else{
				if (chatmessage.mComefrom == MESSAGE_COMEFROM.OUT_TO_LOCAL) {
					holder.mMessage_Content_ViewGroup.setPadding(DIP_SIXTEEN,
							DIP_TEN, DIP_TEN, DIP_TEN);
				} else {
					holder.mMessage_Content_ViewGroup.setPadding(DIP_TEN,
							DIP_TEN, DIP_SIXTEEN, DIP_TEN);
				}
			}
			
		}
	}
//	  nn  18:42:48
//	  上边距10dp
//	  左边边距12dp
//	    nn  18:43:51
//	  右边边距20dp
//	  下面边距14dp
	
	
	
	
	private void showFeed(ChatItemHolder holder,ChatMessageWarpper chatmessage,final  IMessageOnClickListener iClick){
		//if(!chatmessage.hasNewsFeed())
	//	{
			//holder.mMessage_FeedView.setVisibility(View.GONE);
			holder.mMessage_Content_ViewGroup.hideFeed();
		//}else{
//			if(chatmessage.mNewsFeedMessage!=null){
//				LayoutParams params = holder.mMessage_FeedView.getLayoutParams();
//				int screenWidth = GlobalValue.getInstance().getScreenWidth();
//				params.width = (int)(0.65*screenWidth);
//				holder.mMessage_FeedView.setLayoutParams(params);
//				holder.mMessage_FeedView.setVisibility(View.VISIBLE);
//				FEED_ADAPTER.updateModel(chatmessage.mNewsFeedMessage);
//				holder.mMessage_FeedView.setAdapter(FEED_ADAPTER);
//				holder.mMessage_FeedView.mFeedDivier2.setVisibility(View.GONE);
//				holder.mMessage_FeedView.resetVideoGroupParams();
//				holder.mMessage_Content_ViewGroup.showFeed();
//			}else
			//{
			//	holder.mMessage_Content_ViewGroup.hideFeed();
			//}
		//}
	}
	private void showLoading(ChatItemHolder holder,ChatMessageWarpper chatmessage,final  IMessageOnClickListener iClick){
		if(chatmessage.mSendTextState == SEND_TEXT_STATE.SEND_PREPARE){
			holder.mMessage_Loading.setVisibility(View.VISIBLE);
		}else{
			holder.mMessage_Loading.setVisibility(View.INVISIBLE);
		}
	}
	private void showError(ChatItemHolder holder,ChatMessageWarpper chatmessage,final  IMessageOnClickListener iClick){
		if(chatmessage.isError()){
			if(Logger.mDebug){
				Logger.errord("显示发送错误的图标");
			}
			holder.mMessage_Error_ImageView.setVisibility(View.VISIBLE);
		}else{
			holder.mMessage_Error_ImageView.setVisibility(View.INVISIBLE);
		}
	}
	
	public abstract void facade(ChatItemHolder holder,ChatMessageWarpper chatmessage,final  IMessageOnClickListener iClick);
	public abstract View getFacadeView(ChatItemHolder holder);
	
	
	public void setGone(View...views){
		for(View v:views){
			v.setVisibility(View.GONE);
		}
	}
	
	
	
}

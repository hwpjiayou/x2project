package com.renren.mobile.x2.components.chat.facade;

import android.view.View;

import com.renren.mobile.x2.components.chat.face.IMessageOnClickListener;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;


public class ChatItemFacader_Notify extends ChatItemFacader{

	@Override
	public void facade(ChatItemHolder holder, ChatMessageWarpper chatmessage,
			final  IMessageOnClickListener iClick) {
		holder.mMessageNotify_TextView.setText(chatmessage.mMessageContent);
		
	}

	@Override
	public View getFacadeView(ChatItemHolder holder) {
		// TODO Auto-generated method stub
		return holder.mMessageNotify_TextView;
	}

}

package com.renren.mobile.x2.components.chat.face;

import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;

public interface IMessageOnClickListener {
	public void onClick(ChatMessageWarpper message);
	public void onLongClick(ChatMessageWarpper message);
	public void onReSendClick(ChatMessageWarpper message);
}

package com.renren.mobile.x2.components.chat.facade;

import android.view.View;
import android.view.View.OnLongClickListener;

import com.renren.mobile.x2.components.chat.face.IMessageOnClickListener;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Text;
import com.renren.mobile.x2.emotion.EmotionString;


/**
 * @author dingwei.chen
 * @说明 文本条目装饰器
 * */
public class ChatItemFacader_Text extends ChatItemFacader{

	@Override
	public void facade(ChatItemHolder holder, ChatMessageWarpper chatmessage,final  IMessageOnClickListener iClick) {
		this.process(holder, (ChatMessageWarpper_Text)chatmessage,iClick);
	}
	
	public void process(final ChatItemHolder holder,final ChatMessageWarpper_Text chatmessage,final  IMessageOnClickListener iClick){
//		EmotionString emotionstring = EmotionStringRef.getInstacne().get(chatmessage.mMessageContent.toString());
//		if(emotionstring == null ){
//			emotionstring = new EmotionString(chatmessage.mMessageContent.toString());
//			EmotionStringRef.getInstacne().put(chatmessage.mMessageContent.toString(), emotionstring);
//		}
		holder.mMessage_TextView.setText( new EmotionString(chatmessage.mMessageContent));
		holder.update(chatmessage.mMessageState, null);
		holder.mMessage_TextView.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				iClick.onLongClick(chatmessage);
				return false;
			}
		});
	}

	@Override
	public View getFacadeView(ChatItemHolder holder) {
		return holder.mMessage_TextMessage_LinearLayout;
	}

}

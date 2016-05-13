package com.renren.mobile.x2.components.chat.facade;

import android.graphics.Bitmap;
import android.view.View;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.face.IMessageOnClickListener;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_FlashEmotion;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.emotion.Emotion;
import com.renren.mobile.x2.emotion.EmotionManager;
import com.renren.mobile.x2.emotion.IEmotionManager.OnEmotionParserCallback;




/**
 * @author dingwei.chen
 * @说明 文本条目装饰器
 * */
public class ChatItemFacader_FlashEmotion extends ChatItemFacader{

	@Override
	public void facade(ChatItemHolder holder, ChatMessageWarpper chatmessage,IMessageOnClickListener iClick) {
		this.process(holder, (ChatMessageWarpper_FlashEmotion)chatmessage);
	}
	
	public void process(final ChatItemHolder holder,final  ChatMessageWarpper_FlashEmotion chatmessage){
		OnEmotionParserCallback callback = new OnEmotionParserCallback() {

			@Override
			public void onEmotionSuccess(Emotion emotion) {
				holder.mMessage_Flash_TextView.setVisibility(View.GONE);
				holder.mMessage_Flash_ImageView.setVisibility(View.VISIBLE);
				Bitmap bitmap = emotion.next();
				holder.mMessage_Flash_ImageView.setImageBitmap(bitmap);
			}

			@Override
			public void onEmotionError(Bitmap bitmap) {
				holder.mMessage_Flash_TextView.setVisibility(View.VISIBLE);
				holder.mMessage_Flash_ImageView.setVisibility(View.GONE);
				holder.mMessage_Flash_TextView
						.setText(ChatUtil.getText(R.string.chat_emotion_cool)
								+chatmessage.mMessageContent);
			}

			@Override
			public void onEmotionLoading(Bitmap bitmap) {
				// TODO Auto-generated method stub
				holder.mMessage_Flash_TextView.setVisibility(View.GONE);
				holder.mMessage_Flash_ImageView.setVisibility(View.VISIBLE);
				holder.mMessage_Flash_ImageView.setImageBitmap(bitmap);
			}
			
			
		};
		//EmotionManager.getInstance().registerCoolEmotionParserCallBack(callback);
		//EmotionManager.getInstance().getCoolEmotion(chatmessage.mMessageContent);
	}

	@Override
	public View getFacadeView(ChatItemHolder holder) {
		// TODO Auto-generated method stub
		return holder.mMessage_FlashMessage_LinearLayout;
	}
	
	
	
}

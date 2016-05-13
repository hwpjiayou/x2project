package com.renren.mobile.x2.components.chat.facade;

import android.view.View;
import android.view.View.OnClickListener;

import com.renren.mobile.x2.components.chat.face.IMessageOnClickListener;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Voice;
import com.renren.mobile.x2.components.chat.message.Subject.COMMAND;
import com.renren.mobile.x2.components.chat.util.Playable.PLAY_STATE;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener.SEND_TEXT_STATE;




/**
 * @author dingwei.chen
 * @说明 文本条目装饰器
 * */
public class ChatItemFacader_Voice extends ChatItemFacader{

	@Override
	public void facade(ChatItemHolder holder, ChatMessageWarpper chatmessage2,final  IMessageOnClickListener iClick) {
		//this.process(holder, (ChatMessageWarpper_Voice)chatmessage);
		final ChatMessageWarpper_Voice chatmessage = (ChatMessageWarpper_Voice)chatmessage2;
		//VoiceOnClickListenner click =new VoiceOnClickListenner(chatmessage);
				holder.mMessage_Content_ViewGroup.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						iClick.onClick(chatmessage);
					}
				});
				holder.update(chatmessage.mMessageState, chatmessage.createResetData());
				if(chatmessage.mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL){
					holder.mMessage_Error_ImageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							iClick.onClick(chatmessage);
						}
					});
				}
				if(chatmessage.mPlayState == PLAY_STATE.STOP){//不为播放状态
					holder.updateVoice(chatmessage.mMessageState, chatmessage.createResetData());
				}else if(chatmessage.mPlayState == PLAY_STATE.PLAYING){//播放状态
					chatmessage.reflash();
				}
				if(chatmessage.mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL){
					if(chatmessage.mMessageState == COMMAND.COMMAND_DOWNLOAD_VOICE_OVER
					&& chatmessage.mSendTextState== SEND_TEXT_STATE.SEND_OVER){
						holder.mMessage_Voice_Unlisten_ImageView.setVisibility(View.VISIBLE);
					}else{
						holder.mMessage_Voice_Unlisten_ImageView.setVisibility(View.GONE);
					}
				}
				holder.mMessage_VoiceMessage_LinearLayout.setTime(holder,chatmessage.mPlayTime,!chatmessage.hasNewsFeed());//是否自适应
	}
	

	@Override
	public View getFacadeView(ChatItemHolder holder) {
		// TODO Auto-generated method stub
		return holder.mMessage_VoiceMessage_LinearLayout;
	}
}

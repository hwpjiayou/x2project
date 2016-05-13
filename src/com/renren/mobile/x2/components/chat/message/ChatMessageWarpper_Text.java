package com.renren.mobile.x2.components.chat.message;
import java.util.LinkedList;
import java.util.List;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.ItemLongClickDialogProxy.LONGCLICK_COMMAND;
import com.renren.mobile.x2.core.xmpp.node.Message;


public class ChatMessageWarpper_Text extends ChatMessageWarpper{
	public ChatMessageWarpper_Text(){
		this.mMessageType =ChatBaseItem.MESSAGE_TYPE.TEXT;
	}
	@Override
	public List<OnLongClickCommandMapping> getOnClickCommands() {
		List<OnLongClickCommandMapping> list = new LinkedList<ChatMessageWarpper.OnLongClickCommandMapping>();
		if(this.mMessageState == Subject.COMMAND.COMMAND_MESSAGE_ERROR ){
			list.add(new OnLongClickCommandMapping(ChatUtil.getText(R.string.chat_resend),LONGCLICK_COMMAND.RESEND));		//ChatMessageWarpper_FlashEmotion_java_1=重新发送; 
		}
		list.add(new OnLongClickCommandMapping(ChatUtil.getText(R.string.chat_copy_message),LONGCLICK_COMMAND.COPY));		//ChatMessageWarpper_Text_java_2=复制消息; 
		list.add(new OnLongClickCommandMapping(ChatUtil.getText(R.string.chat_del_message),LONGCLICK_COMMAND.DELETE));		//ChatMessageWarpper_Text_java_3=删除消息; 
		//list.add(new OnLongClickCommandMapping("转发",LONGCLICK_COMMAND.FORWARD));		//ChatMessageWarpper_FlashEmotion_java_3=转发; 
		list.add(new OnLongClickCommandMapping(ChatUtil.getText(R.string.cancel),LONGCLICK_COMMAND.CANCEL));		//ChatMessageWarpper_FlashEmotion_java_4=取消; 
		return list;
	}
	@Override
	public String getDescribe() {
		return this.mMessageContent;
	}
	@Override
	public void onAddToAdapter() {}
	@Override
	public void download(boolean isForceDownload) {}
	@Override
	public void onErrorCode() {
		this.addErrorCode(Subject.COMMAND.COMMAND_MESSAGE_ERROR);
	}
	@Override
	public void swapDataFromXML(Message message) {
		this.mMessageContent = message.mBody.text.mValue;
	}
}

package com.renren.mobile.x2.components.chat.message;
import java.util.List;

import com.renren.mobile.x2.core.xmpp.node.Message;

public class ChatMessageWarpper_LBS extends ChatMessageWarpper{
	public ChatMessageWarpper_LBS(){
		this.mMessageType =ChatBaseItem.MESSAGE_TYPE.IMAGE;
	}
	public static final long DEFAULT_LATLON = 255*1000000;
	public long mLat;
	public long mLon;
	public String mPoiName;
	public long mPoiLat;
	public long mPoiLon;
	@Override
	public List<OnLongClickCommandMapping> getOnClickCommands() {
//		List<OnLongClickCommandMapping> list = new LinkedList<ChatMessageWarpper.OnLongClickCommandMapping>();
//		if(this.mMessageState == Subject.COMMAND.COMMAND_MESSAGE_ERROR ){
//			list.add(new OnLongClickCommandMapping(RenrenChatApplication.getmContext().getResources().getString(R.string.ChatMessageWarpper_FlashEmotion_java_1),LONGCLICK_COMMAND.RESEND));		//ChatMessageWarpper_FlashEmotion_java_1=重新发送; 
//		}
//		list.add(new OnLongClickCommandMapping(RenrenChatApplication.getmContext().getResources().getString(R.string.ChatMessageWarpper_LBS_java_1),LONGCLICK_COMMAND.DELETE));		//ChatMessageWarpper_LBS_java_1=删除LBS消息; 
//		list.add(new OnLongClickCommandMapping(RenrenChatApplication.getmContext().getResources().getString(R.string.ChatMessageWarpper_FlashEmotion_java_3),LONGCLICK_COMMAND.FORWARD));		//ChatMessageWarpper_FlashEmotion_java_3=转发; 
//		list.add(new OnLongClickCommandMapping(RenrenChatApplication.getmContext().getResources().getString(R.string.ChatMessageWarpper_FlashEmotion_java_4),LONGCLICK_COMMAND.CANCEL));		//ChatMessageWarpper_FlashEmotion_java_4=取消; 
//		return list;
		return null;
	}
	@Override
	public String getDescribe() {
		//return RenrenChatApplication.getmContext().getResources().getString(R.string.ChatMessageWarpper_LBS_java_2);		//ChatMessageWarpper_LBS_java_2=[位置];
		return "";
	}
	@Override
	public void download(boolean isForceDownload) {
	}
	@Override
	public void onErrorCode() {
	}
	@Override
	public void swapDataFromXML(Message message) {
	}
}

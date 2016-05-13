package com.renren.mobile.x2.components.chat.message;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.core.xmpp.node.Message;
public class ChatMessageWarpper_SoftInfo extends ChatMessageWarpper{
	public ChatMessageWarpper_SoftInfo(){
//		this.mMessageType = ChatBaseItem.MESSAGE_TYPE.SOFT_INFO;
//		this.mMessageContent = RenrenChatApplication.getmContext().getResources().getString(R.string.ChatMessageWarpper_SoftInfo_java_1);		//ChatMessageWarpper_SoftInfo_java_1=苍井空,川滨奈美,堤莎也加,町田梨乃,二阶堂仁美,饭岛爱; 
//		this.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.NOTIFY;
	}
	@Override
	public void swapDataFromXML(Message message) {
	}
	@Override
	public String getDescribe() {
		return this.mMessageContent;
	}
}

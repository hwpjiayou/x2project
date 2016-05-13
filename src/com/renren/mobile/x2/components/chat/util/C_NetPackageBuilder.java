package com.renren.mobile.x2.components.chat.util;

import java.util.LinkedList;
import java.util.List;

import com.renren.mobile.x2.components.chat.face.IUploadable_Voice;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_FlashEmotion;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Image;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Text;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Voice;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.core.xmpp.node.base.Body;
import com.renren.mobile.x2.core.xmpp.node.childs.Audio;
import com.renren.mobile.x2.core.xmpp.node.childs.Image;
import com.renren.mobile.x2.core.xmpp.node.childs.Text;
import com.renren.mobile.x2.utils.Methods;


public class C_NetPackageBuilder {

	private static C_NetPackageBuilder sInstance = new C_NetPackageBuilder();
	private C_NetPackageBuilder(){}
	public static C_NetPackageBuilder getInstance(){
		return sInstance;
	}
	
	public List<XMPPNode> build(ChatMessageWarpper message){
		List<XMPPNode> list = new LinkedList<XMPPNode>();
		Body body = this.obtainBodyNode(message);
		switch (message.getMessageType()) {
		case ChatBaseItem.MESSAGE_TYPE.TEXT:
				build((ChatMessageWarpper_Text)message,body);break;
		case ChatBaseItem.MESSAGE_TYPE.VOICE:
				build((ChatMessageWarpper_Voice)message,body);break;
		case ChatBaseItem.MESSAGE_TYPE.FLASH:
				build((ChatMessageWarpper_FlashEmotion)message,body);break;
		case ChatBaseItem.MESSAGE_TYPE.IMAGE:
				build((ChatMessageWarpper_Image)message,body);break;
		default:
				build((ChatMessageWarpper_Text)message,body);break;
		}
		list.add(body);
//		if(message.hasNewsFeed()){
//			XMPPNode node = this.addFeedNode(message);
//			if(node!=null){
//				list.add(node);
//			}
//		}
		return list;
	}
	
	/*图片消息*/
	private void build(ChatMessageWarpper_Image message_image,Body body){
		Image image = new Image();
		body.addChildNode(image);
		image.addAttribute("mine_type", "image/jpg");
		image.addAttribute("tiny", message_image.mTinyUrl);
		image.addAttribute("main",message_image.mMainUrl);
		image.addAttribute("large", message_image.mLargeUrl);
		if(message_image.isBrush()){
			image.addAttribute("source", "brushpen");
		}
	}
	
	
	/*语音消息*/
	private void build(ChatMessageWarpper_Voice message_voice,Body body){
		Audio audio = new Audio();
		audio.addAttribute("url", message_voice.mVoiceUrl);
		audio.addAttribute("fullurl", message_voice.mVoiceUrl);
		audio.addAttribute("filename", message_voice.mUserName);
		audio.addAttribute("seqid", String.valueOf(IUploadable_Voice.SEQ_ID));
		audio.addAttribute("vid", message_voice.mVid);
		audio.addAttribute("mode", IUploadable_Voice.END);
		audio.addAttribute("playtime", String.valueOf(message_voice.mPlayTime));
		body.addChildNode(audio);
	}
	
	/*状态消息*/
	public Body build(StateMessageModel message_state){
		Body body = new Body("action");
		body.addAttribute("action", message_state.mStateType);
		return body;
	}
	/*文本消息*/
	private void build(ChatMessageWarpper_Text message_text,Body body){
		Text text = new Text();
		body.addChildNode(text);
		text.mValue = (Methods.htmlEncoder(message_text.mMessageContent));
	}
	
	/*炫酷表情消息*/
	private void build(ChatMessageWarpper_FlashEmotion message_text,Body body) {
		Text text = new Text();
		body.addChildNode(text);
		text.mValue = (Methods.htmlEncoder(message_text.mMessageContent));
	}
	
	
	
	
	private Body obtainBodyNode(ChatMessageWarpper message){
		Body body = new Body(getType(message.getMessageType()));
		return body;
	}
//	private XMPPNode addFeedNode(ChatMessageWarpper message){
//		if(message.hasNewsFeed() && message.mNewsFeedMessage!=null){
//			return message.mNewsFeedMessage.getFeedNode();
//		}
//		return null;
//	}
	
	private String getType(int type){
		switch (type) {
		case ChatBaseItem.MESSAGE_TYPE.TEXT:
				return "text";
		case ChatBaseItem.MESSAGE_TYPE.VOICE:
				return "voice";
		case ChatBaseItem.MESSAGE_TYPE.FLASH:
				return "expression";
		case ChatBaseItem.MESSAGE_TYPE.IMAGE:
				return "image";
		default:
				return "unknow";
		}
	}

	
	
}

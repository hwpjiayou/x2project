package com.renren.mobile.x2.components.message.object;

import com.renren.mobile.x2.core.xmpp.node.Message;


public class MessageModelFactory {
    
private static MessageModelFactory mInstance = new MessageModelFactory();
    
    private MessageModelFactory(){}
    
    public static MessageModelFactory getInstance(){
        return mInstance;
    }
    
    public BaseMessageModel getMessageModel(int type) {
        BaseMessageModel message =  null;
        
        switch (type) {
        case BaseMessageModel.MESSAGE_TYPE.COMMENT:
            message = new CommentMessageModel();
            break;
        case BaseMessageModel.MESSAGE_TYPE.LIKE:
            message = new LikeMessageModel();
            break;
        case BaseMessageModel.MESSAGE_TYPE.DELETE_POST:
            message = new DeletePostMessageModel();
            break;
        case BaseMessageModel.MESSAGE_TYPE.SYSTEM:
            message = new SystemMessageModel();
            break;

        default:
            break;
        }
        
        return message;
    }
    
    public BaseMessageModel getMessageModel(Message node) {
        int messageType = 0;
        
         //TODO 处理方式需改进
        if(node.mComment != null) {
            messageType = BaseMessageModel.MESSAGE_TYPE.COMMENT;
        }else if(node.mLike != null) {
            messageType = BaseMessageModel.MESSAGE_TYPE.LIKE;
        }else if(node.mDeletePost != null) {
            messageType = BaseMessageModel.MESSAGE_TYPE.DELETE_POST;
        }else if(node.mSystemMessaeg != null) {
            messageType = BaseMessageModel.MESSAGE_TYPE.SYSTEM;
        }
        
        BaseMessageModel message = getMessageModel(messageType);
        
        return message;
    }

}

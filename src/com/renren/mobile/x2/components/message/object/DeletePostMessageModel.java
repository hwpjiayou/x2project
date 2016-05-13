package com.renren.mobile.x2.components.message.object;

import com.renren.mobile.x2.core.xmpp.node.Message;

public class DeletePostMessageModel extends BaseMessageModel {

    @Override
    public void swapDataFromXML(Message message) {
        
        this.content = message.mDeletePost.mBody.text.mValue;
        this.time = getTime(message.mDeletePost.mTime);

    }

}

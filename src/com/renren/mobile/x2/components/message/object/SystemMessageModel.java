package com.renren.mobile.x2.components.message.object;

import com.renren.mobile.x2.core.xmpp.node.Message;

public class SystemMessageModel extends BaseMessageModel {

    @Override
    public void swapDataFromXML(Message message) {
        
        this.content = message.mSystemMessaeg.mText.mValue;
        this.time = getTime(message.mSystemMessaeg.mTime);

    }

}

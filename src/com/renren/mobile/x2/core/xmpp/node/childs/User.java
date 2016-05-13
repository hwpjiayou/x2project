package com.renren.mobile.x2.core.xmpp.node.childs;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

public class User extends XMPPNode{

    private static final long serialVersionUID = -2303632504915620612L;
    
    @XMLMapping(Type=XMLType.ATTRIBUTE, Name="profile_picture_url")
    public String mHeadUrl;

    @Override
    public String getNodeName() {
        return "user";
    }
    
    

}

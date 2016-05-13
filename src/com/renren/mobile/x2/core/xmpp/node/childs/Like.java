package com.renren.mobile.x2.core.xmpp.node.childs;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

public class Like extends XMPPNode{

    private static final long serialVersionUID = 4042366272686010570L;
    
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "create_timestamp")
    public String mTime;

    @XMLMapping(Type = XMLType.NODE, Name = "user")
    public User mUser;

    @XMLMapping(Type = XMLType.NODE, Name = "text")
    public Text mText;

    @XMLMapping(Type = XMLType.NODE, Name = "feed")
    public Feed mFeed;

    @Override
    public String getNodeName() {
        return "like";
    }
    

}

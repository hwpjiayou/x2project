package com.renren.mobile.x2.core.xmpp.node.childs;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.core.xmpp.node.base.Body;

public class DeletePost extends XMPPNode {

    private static final long serialVersionUID = 6306040839898702586L;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "create_timestamp")
    public String mTime;

    @XMLMapping(Type = XMLType.NODE, Name = "user")
    public User mUser;

    @XMLMapping(Type = XMLType.NODE, Name = "body")
    public Body mBody;

    @XMLMapping(Type = XMLType.NODE, Name = "feed")
    public Feed mFeed;
    
    @Override
    public String getNodeName() {
        return "delete_post";
    }
    
    


}

package com.renren.mobile.x2.core.xmpp.node.childs;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

public class SystemMessage extends XMPPNode {

    private static final long serialVersionUID = -1331001094994776354L;
    
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "create_timestamp")
    public String mTime;

    @XMLMapping(Type = XMLType.NODE, Name = "user")
    public User mUser;

    @XMLMapping(Type = XMLType.NODE, Name = "text")
    public Text mText;

    @Override
    public String getNodeName() {
        return "system";
    }
    
    

}

package com.renren.mobile.x2.core.xmpp.node.base;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

import java.util.LinkedList;

/**
 * @author xiaobo.yuan
 */
public class FakeData extends XMPPNode{
    private static final long serialVersionUID = 8231907162368487251L;

    @XMLMapping(Type = XMLMapping.XMLType.ATTRIBUTE, Name = "id")
    public String mId = null;

    @XMLMapping(Type= XMLMapping.XMLType.NODE, Name="message", isIterable=true)
    public LinkedList<Message> messages = new LinkedList<Message>();

    @Override
    public String getNodeName() {
        return "fake_data";
    }
}

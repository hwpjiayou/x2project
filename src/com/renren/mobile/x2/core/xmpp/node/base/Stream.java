package com.renren.mobile.x2.core.xmpp.node.base;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

import java.util.LinkedList;

public class Stream extends XMPPNode {

    private static final long serialVersionUID = -7349867467969338413L;

    @XMLMapping(Type = XMLType.NODE, Name = "auth")
    public Auth auth;

    @XMLMapping(Type = XMLType.NODE, Name = "success")
    public Success success;

    @XMLMapping(Type = XMLType.NODE, Name = "<xml-not-well-formed")
    public XmlNotWellFormed xmlNotWellFormed;

    @XMLMapping(Type = XMLType.NODE, Name = "message", isIterable = true)
    public LinkedList<Message> messages = new LinkedList<Message>();

    @XMLMapping(Type = XMLType.NODE, Name = "error", isIterable = true)
    public LinkedList<Error> errors = new LinkedList<Error>();

    @Override
    public String getNodeName() {
        return "stream";
    }

}

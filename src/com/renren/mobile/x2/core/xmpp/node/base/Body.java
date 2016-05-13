package com.renren.mobile.x2.core.xmpp.node.base;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.core.xmpp.node.childs.Audio;
import com.renren.mobile.x2.core.xmpp.node.childs.Image;
import com.renren.mobile.x2.core.xmpp.node.childs.Text;

import java.util.LinkedList;

public class Body extends XMPPNode {

    private static final long serialVersionUID = -5301743778189606861L;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "ack")
    public String ack;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "sid")
    public String sid;

    @XMLMapping(Type = XMLType.NODE, Name = "auth")
    public Auth auth;

    @XMLMapping(Type = XMLType.NODE, Name = "success")
    public Success success;

    @XMLMapping(Type = XMLType.NODE, Name = "failure")
    public Failure failure;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "type")
    public String type;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "version")
    public String version;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "action")
    public String action;

    @XMLMapping(Type = XMLType.NODE, Name = "text")
    public Text text;

    @XMLMapping(Type = XMLType.NODE, Name = "audio")
    public Audio audio;

    @XMLMapping(Type = XMLType.NODE, Name = "image")
    public Image image;

    @XMLMapping(Type = XMLType.NODE, Name = "wml")
    public Wml wml;

    @XMLMapping(Type = XMLType.NODE, Name = "html")
    public Html html;

    @XMLMapping(Type = XMLType.NODE, Name = "message", isIterable = true)
    public LinkedList<Message> messages = new LinkedList<Message>();

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "inactivity")
    public String inactivity;

    @Override
    public String getNodeName() {
        return "body";
    }

    //	public String getType(){
//	if(mType!=null){
//		return mType;
//	}
//	return "unknow";
//}
//
    public Body() {
        super();
    }

    public Body(String type) {
        this();
        this.addAttribute("type", type);
    }

//public int getVersion(){
//	try {
//		return Integer.parseInt(mVersion);
//	} catch (Exception e) {
//		return -1;
//	}
//}
//

}

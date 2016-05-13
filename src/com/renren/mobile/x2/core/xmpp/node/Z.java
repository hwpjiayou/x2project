package com.renren.mobile.x2.core.xmpp.node;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.base.Body;

/**
 * Created with IntelliJ IDEA.
 * User: xiaobo.yuan
 * Date: 12-8-23
 * Time: 上午11:30
 */
public class Z extends XMPPNode{

	private static final long serialVersionUID = 8092433055441151181L;

	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="xmlns")
    public String mXmlns;
    
    @XMLMapping(Type=XMLType.NODE, Name="person")
    public Person mPersion;
    
    @XMLMapping(Type=XMLType.NODE, Name="body")
    public Body mBody;
    
    @XMLMapping(Type=XMLType.NODE, Name="profile")
    public Profile mProfile;

    @Override
    public String getNodeName() {
        return "z";
    }
}
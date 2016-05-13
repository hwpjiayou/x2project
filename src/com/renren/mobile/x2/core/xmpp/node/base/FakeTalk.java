package com.renren.mobile.x2.core.xmpp.node.base;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

import java.util.LinkedList;

/**
 * @author xiaobo.yuan
 */
public class FakeTalk extends XMPPNode {
    private static final long serialVersionUID = 1180751045067477106L;

    @XMLMapping(Type = XMLMapping.XMLType.NODE, Name = "fake_data", isIterable = true)
    public LinkedList<FakeData> mFakeData = new LinkedList<FakeData>();

    @Override
    public String getNodeName() {
        return "fake_talk";
    }
}

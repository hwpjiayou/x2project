package com.renren.mobile.x2.core.xmpp.node;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.utils.Methods;
import org.xml.sax.Attributes;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author dingwei.chen1988@gmail.com
 * @说明 XMPP节点基类
 */
public class XMPPNode implements Serializable {
    private static final long serialVersionUID = -7116281130601441750L;
    public XMPPNode mParent = null;
    public String mValue = "";
    public LinkedList<XMPPNode> mChilds = new LinkedList<XMPPNode>();
    private LinkedList<String> mAttributes = new LinkedList<String>();
    private boolean mIsPrintValueOrTail = true;
    public String mTag = null;
    private transient HashMap<String, Field> mXMLFieldMap = new HashMap<String, Field>();

    public XMPPNode() {
        mTag = getNodeName();
        initFieldMap();
    }

    public XMPPNode(String tag) {
        this();
        mTag = tag;
    }

    public void initFieldMap() {
        Field[] fields = getClass().getFields();
        for (Field f : fields) {
            XMLMapping xmlMapping = f.getAnnotation(XMLMapping.class);
            if (xmlMapping == null)
                continue;
            mXMLFieldMap.put(xmlMapping.Name(), f);
        }
    }

    public String getNodeName() {
        return "";
    }
//	
//	public void setParent(XMPPNode xmppNode){
//		mParent = xmppNode;
//		mappingNode(mParent, this);
//	}

    public String getId() {
        return null;
    }

    public void addChildNode(XMPPNode node) {
//		node.setParent(this);
        node.mParent = this;
        mappingNode(node.mParent, node);
        mChilds.add(node);
    }

    public void addAttribute(String key, String value) {
        if (value == null) {
            return;
        }
        mAttributes.add(key + "=\"" + Methods.htmlEncoder(value) + "\"");
    }

    public String toXMLString() {
        mIsPrintValueOrTail = true;
        StringBuilder builder = new StringBuilder();
        builder.append(preXML());
        builder.append(printValue());
        builder.append(printChilds());
        builder.append(aftXML());
        return builder.toString();
    }

    private String printValue() {
        if (mValue != null) {
            return mValue;
        }
        return "";
    }

    private String preXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(this.mTag);
        if (!mAttributes.isEmpty()) {
            builder.append(" ");
        }
        for (String attr : mAttributes) {
            builder.append(attr).append(" ");
        }
        if (this.mValue == null && this.mChilds.size() == 0) {
            this.mIsPrintValueOrTail = false;
            builder.append("/>");
        } else {
            builder.append(">");
        }

        return builder.toString();
    }

    private String printChilds() {
        if (mChilds.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (XMPPNode node : mChilds) {
            builder.append(node.toXMLString());
        }
        return builder.toString();
    }

    private String aftXML() {
        if (mIsPrintValueOrTail) {
            StringBuilder builder = new StringBuilder();
            builder.append("</").append(this.mTag).append(">");
            return builder.toString();
        }
        return "";
    }

    @Override
    public String toString() {
        return toXMLString();
    }

    @SuppressWarnings("unchecked")
    public static void mappingNode(XMPPNode parent, XMPPNode node) {
        if (node == null) {
            return;
        }
        Field f = parent.mXMLFieldMap.get(node.getNodeName());
        if (f == null) {
            return;
        }
        XMLMapping xmlMapping = f.getAnnotation(XMLMapping.class);
        if (xmlMapping == null) {
            return;
        }
        try {
            if (!xmlMapping.isIterable()) {
                f.setAccessible(true);
                f.set(parent, node);
            } else {
                Collection<XMPPNode> collection = (Collection<XMPPNode>) f.get(parent);
                collection.add(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mappingAttribute(XMPPNode object, Attributes attributes) {
        if (attributes == null) {
            return;
        }
        int length = attributes.getLength();
        for (int i = 0; i < length; ++i) {
            object.addAttribute(attributes.getLocalName(i), attributes.getValue(i));
            Field f = object.mXMLFieldMap.get(attributes.getLocalName(i));
            if (f == null) {
                continue;
            }
            try {
                f.setAccessible(true);
                f.set(object, attributes.getValue(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clear() {
        mChilds.clear();
    }
}

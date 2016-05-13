package com.renren.mobile.x2.network.talk.messagecenter.base;

import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.core.xmpp.node.XMPPNodeFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class XMLParser<T extends XMPPNode> extends DefaultHandler {
    private final static SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    private final SAXParser sax;
    private T root;
    private Class<T> mNodeClass;
    private Stack<XMPPNode> parseStack = new Stack<XMPPNode>();
    private Stack<String> unknownNodes = new Stack<String>();
    private Stack<Boolean> nodesIsKnown = new Stack<Boolean>();
    private StringBuilder mStringBuffer = new StringBuilder();

    public XMLParser(Class<T> nodeClass) {
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (Exception ignored) {
        }
        sax = saxParser;
        this.mNodeClass = nodeClass;
    }

    public T getRoot() {
        return root;
    }

    public void parse(InputStream inputStream) throws IOException, SAXException {
        sax.parse(inputStream, this);
    }

    public void parse(String str) throws IOException, SAXException {
        sax.parse(new ByteArrayInputStream(str.getBytes()), this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        XMPPNode node = XMPPNodeFactory.createXMPPNode(localName);//"wml"
        boolean isNodeNull = node == null;
        nodesIsKnown.push(!isNodeNull);
        if (isNodeNull) {
            unknownNodes.push(localName);
        } else {
            XMPPNode.mappingAttribute(node, attributes);
            if (parseStack.isEmpty()) {
                if (mNodeClass.isInstance(node)) {
                    root = (T) node;
                } else {
                    root = null;
                    unknownNodes.push(localName);
                    nodesIsKnown.pop();
                    nodesIsKnown.push(false);
                }
            } else {
                parseStack.peek().addChildNode(node);
            }
            parseStack.push(node);
        }
        mStringBuffer.delete(0, mStringBuffer.length());
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
//		XMPPNode node = parseStack.pop();
//		if (!(qName.equals(node.mTag))) {
//			throw new SAXException("start != end");
//		}
//
//		if (element.text != null &&NO_CONTENT_REGEX.matcher(element.text).matches()) {
//			element.text = null;
//		}
        if (!parseStack.isEmpty() && mStringBuffer.length() > 0) {
            parseStack.peek().mValue = mStringBuffer.toString();
            mStringBuffer.delete(0, mStringBuffer.length());
        }
        if (nodesIsKnown.pop()) {
            parseStack.pop();
        } else {
            unknownNodes.pop();
        }
        if (unknownNodes.isEmpty()) {
            onStackSizeChanged(parseStack.size());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        mStringBuffer.append(ch, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        root = null;
        parseStack.clear();
        unknownNodes.clear();
        nodesIsKnown.clear();
    }

    protected void onStackSizeChanged(int size) {
    }

}

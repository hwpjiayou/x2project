package com.renren.mobile.x2.core.xmpp.parser;

import org.xml.sax.helpers.DefaultHandler;

/**
 * @version 1.0
 * @date 	2012-06-04
 * @author  dingwei.chen1988@gmail.com
 * @项目说明: 
 * 			1.实现XMPP的数据转义
 * 			2.由XML数据格式到JAVA对象的数据转换
 * 			3.离开移动终端调试代码
 * @XMPP:
 * 			XMPP每个节点有对应的类,继承于{@link com.data.xmpp.XMPPNode}
 * @XML2JAVA:
 * 			XOMapping 通过注解{@link com.renren.mobile.x2.core.xmpp.XMLMapping} 完成
 * @注:
 * 			SAX回调调用次序:
 * 			{@link #startDocument()} 开启文档
 * 			[	
	 * 			{@link #startElement(String, String, String, org.xml.sax.Attributes)} 解析一个节点
	 * 			{@link #characters(char[], int, int)} 解析节点中的值
	 * 			{@link #endElement(String, String, String)} 解析节点结束
 * 			]* 中间过程调用多次
 * 			{@link #endDocument()}  结束文档
 * @see .OnDataParserListener XMPP数据解析回调,主要提供给第三方
 * 			
 * */
public class SAXParserHandler extends DefaultHandler{
//
//	/*SAX方式解析XML文件*/
//	private SAXParser mParser = null;
//	/*SAX提供的解析回调*/
//	private static SAXParserHandler sHandler = new SAXParserHandler();
//	private XMPPNode mNodeRoot = null;
//	private XMPPNode mTag = null;
//	private String mPreNodeName = null;
//	private String mOldTmp = null;
//	
//	public static SAXParserHandler getInstance(){
//		return sHandler;
//	}
//	private SAXParserHandler(){
//		try {
//			mParser = SAXParserFactory.newInstance().newSAXParser();//只相当于一个代理实际解析通过tagparser解析
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static interface Transaction extends OnDataParserListener{
//		public void commit();
//		public void clear();
//		public List<XMPPNode> getNodes();
//	}
//	class TransactionImpl implements Transaction{
//
//		OnDataParserListener mTListener = null;
//		List<XMPPNode> mM = new ArrayList<XMPPNode>();
//		
//		public TransactionImpl(OnDataParserListener listener){
//			this.mTListener = listener;
//			if(this.mTListener==null){
//				throw new RuntimeException("OnDataParserListener can not be null");
//			}
//		}
//		@Override
//		public void commit() {
//			mTListener.onParserNode(mM);
//		}
//		
//		@Override
//		public void onParserError(String errorMessage) {}
//		@Override
//		public void clear() {
//			mM.clear();
//		}
//		@Override
//		public List<XMPPNode> getNodes() {
//			return this.mM;
//		}
//		@Override
//		public void onParserNode(List<XMPPNode> list) {
//			mM.addAll(list);
//		}
//		
//	}
//	Transaction mTransactionLock = null;
//	public Transaction beginTransaction(OnDataParserListener listener){
//		if(mTransactionLock==null){
//			mTransactionLock = new TransactionImpl(listener);
//		}
//		mTransactionLock.clear();
//		return mTransactionLock;
//	}
//	
//	Transaction mTransaction = null;
//	public synchronized void parse(String text,Transaction transaction){
//		if(text == null || text.trim().length() == 0){
//			return;
//		}
//		mTransaction = transaction;
//		text = text.replaceAll("stream:stream", "stream");
//		ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes());
//		parse(bais);//转义成为本进程数据
//		try {
//			bais.close();
//		} catch (Exception e) {}
//	}
//	
//	public synchronized void parse(String text){
//		this.parse(text, null);
//	}
//	
//	public void parse(InputStream inputStream){
//		try {
//			InputSource is = new InputSource(inputStream);
//			is.setEncoding("utf-8");
//			mParser.parse(is, this);
//			
//		} catch (Exception e) {
//			onParserError(e);
//		}
//	}
//	
//	
//	@Override
//	public void startDocument() throws SAXException {
//		mNodeRoot = null;
//		mTag = null;
//		mPreNodeName = null;
//		mOldTmp = null;
//		XMPPNodeFactory.getInstance().clear();
//	};
//	
//	@Override
//	public void endDocument() throws SAXException {
//		this.onParserNode(XMPPNodeFactory.getInstance().mMessages);
//	};
//	
//	
//	@Override
//	public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
//		mOldTmp = null;
//		if(mNodeRoot==null){
//			mNodeRoot = XMPPNodeFactory.createXMPPRootNode(qName);
//			mTag = mNodeRoot;
//			XOMUtil.mappingAttribute(mTag,attributes);
//		}else{
//			XMPPNode node = XMPPNodeFactory.createXMPPChildNode(qName);
//			if(node==null){
//				return;
//			}
//			if("z".equals(qName) && node instanceof Z){
//				Z z = (Z) node;
//				z.mXmlns = uri;
//			}
//			XOMUtil.mappingAttribute(node, attributes);
//			node.setParent(mTag);
//			mTag.addChildNode(node);
//			this.mTag = node;
//		}
//		mPreNodeName = qName;
//	};
//	
//	
//	@Override
//	public void endElement(String uri, String localName, String qName) throws SAXException {
//		mOldTmp = null;
//		if(mTag!=null&&mTag.getNodeName().equals(qName)){
//			mTag = mTag.mParent;
//		}
//		if(mNodeRoot!=null&&mNodeRoot.getNodeName().equals(qName)){
//			mNodeRoot = null;
//		}
//	};
//	
//	@Override
//	public void characters(char[] ch, int start, int length) throws SAXException {
//		String str = new String(ch,start,length);
//		if(mPreNodeName!=null&&mTag!=null && mPreNodeName.equals(mTag.getNodeName())){
//			if(mOldTmp!=null){
//				mOldTmp+=str;
//			}else{
//				mOldTmp= str;
//			}
//			if(mOldTmp!=null&&mOldTmp.length()>0){
//				mTag.mValue = mOldTmp;
//			}
//		}
//	};
//	
//	@Override
//	public void error(org.xml.sax.SAXParseException e) throws SAXException {
//	};
//	
//	public static interface OnDataParserListener{
//		public void onParserNode(List<XMPPNode> list);
//		public void onParserError(String errorMessage);
//	}
//	OnDataParserListener mListener = null;
//	public void setOnDataParserListener (OnDataParserListener listener){
//		mListener = listener;
//	}
//	public void onParserNode(List<XMPPNode> list){
//		if(this.mTransaction!=null){
//			this.mTransaction.onParserNode(list);
//			return;
//		}
//		if(mListener!=null&&list!=null&& list.size()>0){
//            for(Object obj:list){
//
//            }
//			mListener.onParserNode(list);
//		}
//	}
//
//	public void onParserError(Exception e){
//		if(mListener!=null){
//			mListener.onParserError(e+"");
//		}
//	}
}

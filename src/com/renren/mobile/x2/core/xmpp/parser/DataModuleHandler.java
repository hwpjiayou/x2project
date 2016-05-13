package com.renren.mobile.x2.core.xmpp.parser;

import android.text.TextUtils;
import android.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DataModuleHandler {
	public static DataModuleHandler instance = new DataModuleHandler();
	private DataModuleHandler(){}
	
	private String mFrom = "";
	private String mTo = "";
	
	public void setFrom(String from) {
		this.mFrom = from;
	}

	public void setTo(String to) {
		this.mTo = to;
	}

	ArrayList<Element> elements = new ArrayList<Element>();
	
	private IRecvRecommandMsg recvRecommandMsg = null;
	
	
	
	public void parse(String str) {
		String formAndTo = " ";
		if(!TextUtils.isEmpty(mFrom)){
			formAndTo += "from=\"" + mFrom + "\" ";
		}
		if(!TextUtils.isEmpty(mTo)){
			formAndTo += "to=\"" + mTo + "\" ";
		}
		str = "<root "+formAndTo+" >" + new String(Base64.decode(str.getBytes(), Base64.DEFAULT)) + "</root>";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(str.getBytes()));
			elements.add(document.getDocumentElement());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void notifyListener(){
		if(this.elements.size() > 0 && recvRecommandMsg != null){
			for(int i = 0; i < elements.size(); ++i){
				recvRecommandMsg.recvMessge(this.elements.get(i));
			}
		}
	}

	public void setRecvRecommandMsg(IRecvRecommandMsg recvRecommandMsg) {
		this.recvRecommandMsg = recvRecommandMsg;
	}
	
}

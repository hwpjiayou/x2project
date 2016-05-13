package com.renren.mobile.x2.network.mas;

import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.renren.mobile.x2.RenrenChatApplication;
import android.content.res.AssetManager;
import android.util.Xml;

/**
 * @author 宁长胜
 * 通过读取assets文件夹里面的mas_fake_data.xml文件
 * 中对应数据产生模拟mas接口返回
 *
 */
public class MasFakeDataManager {

	private final static String MasFakeDateXMLName = "mas_fake_data.xml"; //存放mas模拟数据的xml文件名
	
	/**
	 * 采用Pull解析xml取得相应数据
	 * @param method 调用的mas接口名
	 * @return 模拟的mas接口返回数据
	 */
	public static JSONObject getDatabyPull(String method) {
		AssetManager manager = RenrenChatApplication.getApplication().getAssets();
		try {
			InputStream is = manager.open(MasFakeDateXMLName);
			XmlPullParser parser=Xml.newPullParser();    
	        parser.setInput(is, "UTF-8");
	        int event=parser.getEventType();
	        while(event!= XmlPullParser.END_DOCUMENT){    
	            switch(event){    
	            	case XmlPullParser.START_DOCUMENT:
	            		break;
	            	case XmlPullParser.START_TAG:
	            		if ("method".equals(parser.getName())
	            				&& method.equals(parser.getAttributeValue(0))) {
//	            			Log.d("NCS", parser.getAttributeValue(0));
	            			while (!"json".equals(parser.getName())) {
	            				parser.next();
							}
							String responseString = parser.nextText();
							JSONObject object = new JSONObject(responseString);
//							Log.d("NCS", "getData().json:"+object.toString());
							return object;
	            		}
	            		break;
	            	case XmlPullParser.END_TAG:
	            		break;
	            		
	            }
	            event=parser.next();
	        }
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	
	
	public static JSONObject getDatabyDOM(String method) {
		AssetManager manager = RenrenChatApplication.getApplication().getAssets();
		try {
			InputStream is = manager.open(MasFakeDateXMLName);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder build = factory.newDocumentBuilder();
			Document document = build.parse(is);
			Element rootElement = document.getDocumentElement();
			NodeList nodeList = rootElement.getElementsByTagName("method");
			if (nodeList == null || nodeList.getLength() == 0) {
				return null;
			}
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				String id = element.getAttribute("id");
				if (id.equalsIgnoreCase(method)) {
					NodeList list = element.getElementsByTagName("json");
					Element element2 = (Element) list.item(0);
					Node node1 = element2.getFirstChild();
					String value = node1.getNodeValue();
					JSONObject object = new JSONObject(value);
//					Log.d("NCS", "getData().json:"+object.toString());
					return object;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 获取与currentRequest相对应的mas模拟数据
	 * @param currentRequest 调用的mas接口名
	 * @return 模拟的mas接口返回的JSON数据
	 */
	public static JSONObject getData(INetRequest currentRequest) {
//		Log.d("NCS", "request:"+currentRequest.toString());
		String method = currentRequest.getUrl();
//		Log.d("NCS", "method:"+method);
		JSONObject object = getDatabyPull(method);
		if (object == null) {
			try {
				object = new JSONObject();
				object.put("error_code", -1225);
				object.put("error_msg", "本地未找到对应的模拟返回数据");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return object;
	}
	
	public boolean setData(String method, String response) {
		
		return false;
		
	}
	
}

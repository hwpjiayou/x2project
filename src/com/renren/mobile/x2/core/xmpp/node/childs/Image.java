package com.renren.mobile.x2.core.xmpp.node.childs;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

public class Image extends XMPPNode {

//	 <image mine_type='image/jpg' 
//		 tiny='ttp://ip:port/xxx.jpg' 
//			 main='ttp://ip:port/xxx.jpg' 
//				 large='http://ip:port/xxx.jpg'
//					 filename='name1.jpg'/>
	
	private static final long serialVersionUID = 3385844866875440576L;

	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="mine_type")
	public String mMineType;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="main")
	public String mMain;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="large")
	public String mLarge;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="tiny")
	public String mTiny;
	
	@XMLMapping(Type=XMLType.ATTRIBUTE,Name="source")
	public String mSource;
	
	public boolean isBrushImage(){
		if(mSource==null){
			return false;
		}else{
			return mSource.toLowerCase().equals("brushpen");
		}
	}
	
	
	@Override
	public String getNodeName() {
		return "image";
	}

}

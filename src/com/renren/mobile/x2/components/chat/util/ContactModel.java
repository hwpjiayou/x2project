package com.renren.mobile.x2.components.chat.util;

import com.renren.mobile.x2.components.chat.face.ICanTalkable;


public class ContactModel implements ICanTalkable{

	private String mName = "";
	private long mUid = 0l;
	private String mUrl = "";
	public ContactModel(){
		
	}
	public ContactModel (String name,long uid,String url) {
		this.mName = name;
		this.mUid = uid;
		this.mUrl = url;
	}
	
	@Override
	public long getUId() {
		// TODO Auto-generated method stub
		return mUid;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return mName;
	}

	@Override
	public String getHeadUrl() {
		return mUrl;
		//return "http://hdn.xnimg.cn/photos/hdn121/20120921/1505/h_main_5Twe_43b60000473c1376.jpg";
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public long getUid() {
		return mUid;
	}

	public void setUid(long uid) {
		this.mUid = uid;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

}

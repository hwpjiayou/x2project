package com.renren.mobile.x2.network.talk;


/**
 * @author dingwei.chen1988@gmail.com
 * @说明 网络请求
 * */
public abstract class NetRequestListener {
	protected long mKey = -1L;
	
	public NetRequestListener(){
		setKey(System.currentTimeMillis());
	}
	
	public long getKey() {
		return mKey;
	}

	public void setKey(long key) {
		this.mKey = key;
	}
	
	protected String getId(){
		if(this.mKey != -1L){
			return this.mKey+"";
		}
		return null;
	}
	
	public long getNetTimeOutTime(){
		return 59 * 1000;
	}
	
	public abstract void onNetError(String errorMsg);					//网络请求失败
	public abstract void onNetSuccess();								//网络请求成功
	public abstract void onSuccessRecive(String data);
	public abstract String getSendNetMessage();							//得到发送报文
}

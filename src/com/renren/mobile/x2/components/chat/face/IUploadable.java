package com.renren.mobile.x2.components.chat.face;


/**
 * 
 *所有需要上传的数据都要实现这个接口
 */
public interface IUploadable {
	public long getToId();
	public void onUploadStart();
	public void onUploadError();
}

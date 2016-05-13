package com.renren.mobile.x2.components.chat.face;


public interface IUploadable_Voice extends IUploadable{
	public final String SEGMENT="segment" ;//表示非结尾片段
	public final String END="end";//表示结尾 片段
	public int SEQ_ID = 1;
	public String VID = "1";
	public int getPlayTime();
	public void onUploadOver(String voiceId,String voiceUrl);
}

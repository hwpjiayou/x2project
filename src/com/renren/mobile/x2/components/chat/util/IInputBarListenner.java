package com.renren.mobile.x2.components.chat.util;

import android.view.View;



/**
 * 
 * 输入条中的接口
 */
public interface IInputBarListenner {
	/** */
	public void onEmotionSelect(String emotion);
	public void onCoolEmotionSelect(String emotion);
	public void mDelBtnClick();
	
	public void onTyping();
	public void onTypingCancel();
	
	public void onSend(String message);
	
	public void onChangeLayout( int w,int h);
	public View getRootView();
	
	public void onKeyBoardShow();
	
	public void onViewShow();

}

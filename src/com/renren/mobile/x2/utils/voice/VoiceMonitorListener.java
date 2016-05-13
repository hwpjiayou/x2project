package com.renren.mobile.x2.utils.voice;

public interface VoiceMonitorListener {
	public void onVoiceStart();
	public int onGetTime();
	public int onGetAngle();
	public boolean onIsLessMinTime();
	public void onVoiceEnd(boolean isSuccess);
	public boolean onIsShowMoveToCancel();
}

package com.renren.mobile.x2.network.talk.binder;

interface PollBinder{
	void connect();
	void send(long key,String message);
	boolean isConnect();
	void disConnect();
	boolean isRecvOfflineMessage();
	void changeAppGround(boolean isForeGround);
	void onLogout();
	void notifyNetwork();
}
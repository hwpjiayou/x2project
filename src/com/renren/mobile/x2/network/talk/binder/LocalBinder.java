package com.renren.mobile.x2.network.talk.binder;


public class LocalBinder {

	private LocalBinder(){}
	PollBinder mBinder = null;
	private static final byte[] LOCK = new byte[0];
	private static LocalBinder sInstance = new LocalBinder();
	public static LocalBinder getInstance(){
		return sInstance;
	}
	public void push(PollBinder binder){
		synchronized (LOCK) {
			mBinder = binder;
			LOCK.notifyAll();
		}
	}
	
	public PollBinder obtainBinder(){
		synchronized (LOCK) {
			if(mBinder==null){
				try {
					LOCK.wait();
				} catch (InterruptedException e) {}
			}
			return mBinder;
		}
	}
	
	public boolean isContainBinder(){
		return mBinder!=null;
	}
}

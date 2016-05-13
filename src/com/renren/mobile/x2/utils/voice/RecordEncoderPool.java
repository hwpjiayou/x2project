package com.renren.mobile.x2.utils.voice;

import java.lang.Thread.State;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @author dingwei.chen
 * @说明 录音编码池
 * */
public class RecordEncoderPool implements Runnable{

	public static RecordEncoderPool sInstance = new RecordEncoderPool();
	public List<short[]> mList = new LinkedList<short[]>();
	public Thread mEncoderThread = new Thread(this);
	public AtomicBoolean mIsEncoding = new AtomicBoolean(false);
	
	public static RecordEncoderPool getInstance(){
		if(sInstance.mEncoderThread.getState()==State.NEW){
			sInstance.mEncoderThread.start();
		}
		return sInstance;
	}
	public void addToList(short[] s){
		synchronized (mList) {
			mList.add(s);
			mList.notify();
		}
	}
	Pcm2OggEncoder mEncoder = null;
	public void setEncoder(Pcm2OggEncoder encoder){
		mEncoder = encoder;
	}
	
	
	@Override
	public void run() {
		while(true){
			short[] s = null;
			synchronized (mList) {
				if(mList.size()==0){
					try {
						mList.wait();
						continue;
					} catch (InterruptedException e) {}
				}
				try {
					s = mList.remove(0);
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				}
				
			}
				if(s!=null){
					if(s.length!=0){
						mIsEncoding.set(true);
						mEncoder.enc(s, 0, s.length);
					}else{
						mEncoder.end();
						mIsEncoding.set(false);
					}
				}
			
		}
	}
	public void stop(boolean issave){
//		mList.add(new short[0]);
		if(issave){
			this.addToList(new short[0]);
		}else{
			mEncoder.setIsWriteToFile(false);
			mEncoder.end();
			synchronized (mList) {
				mList.clear();
			}
			this.mIsEncoding.set(false);
		}
	}
	public boolean isEncoding(){
		return mIsEncoding.get();
	}
	
}

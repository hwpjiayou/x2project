package com.renren.mobile.x2.utils.voice;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dingwei.chen
 * @说明 帧池
 * */
final class FramesPool {

	private List<PCMFrame> mFramePool = new LinkedList<PCMFrame>();
	private static FramesPool sInstance = new FramesPool();
	private byte[] mLock = new byte[0];
	private FramesPool(){}
	public static FramesPool getInstance(){
		return sInstance;
	}
	public int size(){
		return mFramePool.size();
	}
	public void addFrame2Pool(PCMFrame frame){
		synchronized (mFramePool) {
			mFramePool.add(frame);
		}
		synchronized (mLock) {
			mLock.notify();
		}
	}
	public void putFrame2Pool(PCMFrame frame){
		synchronized (mFramePool) {
			mFramePool.add(0,frame);
		}
		synchronized (mLock) {
			mLock.notify();
		}
	}
	public PCMFrame obtainFrame(){
		PCMFrame frame = null;
		try {
			synchronized (mLock) {
				while(true){
					if(mFramePool.size() == 0){
						mLock.wait();
					}else{
						synchronized (mFramePool) {
							frame = mFramePool.remove(0);
						}
						break;
					}
				}
			}
		} catch (Exception e) {}
		return frame;
	}
	
	public void clearCache(){
		synchronized (mFramePool) {
			mFramePool.clear();
		}
	}
}

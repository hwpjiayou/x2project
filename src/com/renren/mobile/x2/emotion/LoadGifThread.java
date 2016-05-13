package com.renren.mobile.x2.emotion;

import java.util.HashMap;

//import com.core.util.SystemUtil;


/****
 * 下载线程池
 * @author zxc
 *	一次只会有一个线程在run
 */
public class LoadGifThread {
	private static LoadGifThread mInstance;
	private Object mlock = new Object();
	private boolean isallowed = true;
//	private LinkedList<ThreadNode> list = new LinkedList<ThreadNode>();
	private HashMap<String,ThreadNode> map =new HashMap<String, LoadGifThread.ThreadNode>();
	public static LoadGifThread getInstance(){
		if(mInstance == null){
			mInstance = new LoadGifThread();
		}
		return mInstance; 
	}
	public void add(String path){
		if(path == null||path.equals("")){
			return;
		}
		if(map.containsKey(path)){
		}else{
			ThreadNode node = new ThreadNode( path.hashCode(),path);
			this.map.put(path, node);
		}
	}
	public void remove(String path){
		this.map.remove(path);
	}
	
	public class ThreadNode{
		public ThreadNode(int c,String path) {
			this.path = path;
			this.code = c;
			this.run();
		}
		public int code;
		public String path;
		private void run(){
			new Thread(){
				public void run() {

					if(!isallowed){
						synchronized (mlock) {
							try{
								mlock.wait();
							}catch(InterruptedException e){
								
							}
						}
					}
					if(isallowed){
						isallowed =false;
					}
					 EmotionPool.getInstance().reloadAlEmotionByFlash(path,EmotionConfig.COOL_PACKAGE_ID);
					remove(path);
					isallowed = true;
					synchronized (mlock) {
						mlock.notifyAll();// /唤醒所有线程
					}
				
				};
			}.start();
		}
	}
	
}

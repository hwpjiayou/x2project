package com.renren.mobile.x2.components.chat.util;


/***
 *  滑动时加锁，让刷新线程停止，获取GIF表情变为获取第一帧
 * @author	zxc
 *
 */
public class ScrollingLock {
	public static boolean isScrolling =false;
	public static int lastposition = 0;
	public static int firstposition= 0;
	public static Object lock = new Object();
	/***
	 * 加锁
	 */
	public static void lock(){
		isScrolling = true;
	}
	/***
	 * 解锁
	 */
	public static void unlock(){
	
		isScrolling = false;
		synchronized (lock) {
			lock.notifyAll();
		}
	}
}

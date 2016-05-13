package com.renren.mobile.x2.emotion;

/****
 * 如果是版本升级更新数据库的时候用户快速进入聊天页面，但是表情数据库没有加载完成则会使用此类
 * 
 * @author zxc
 *
 */
public class DataBaseLock {
public static Object dBlock = new Object();
public static boolean isWriteToDB = false;

	
	public static void lock(){
		isWriteToDB = true;
	}
	public static void unlock(){
		if(isWriteToDB){
			synchronized (dBlock) {
				dBlock.notifyAll();
				isWriteToDB = false;
			}
		}
	}
}

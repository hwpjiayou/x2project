package com.renren.mobile.x2.emotion;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/***
 * 缓存
 * @author zxc
 *	在插入key值时对对其进行修饰，增加计数器 ,将key值存储在一个linkedList中，
 *	这样就避免了将计数器存到Value中，删除最小值时比较之前还得进行get操作，节省了时间
 *	所有没有用到LinkedHashmap
 */
public class EmotionCache {
//	private float goal =0;
//	private float total = 0;
	private static final int DEFAULTSIZE = 20;
	private SoftRefHashMap<Integer> mEmotionTable ;
	private LinkedList<CacheNode> list = new LinkedList<CacheNode>();
	private int max =0;//计数器最大
	private int cache_size = DEFAULTSIZE;
	private boolean mIspng = true;
	private ReadWriteLock lock = new ReentrantReadWriteLock();  
	private Lock read = lock.readLock();  
    private Lock write = lock.writeLock();
	ReferenceQueue<Bitmap> queue=new ReferenceQueue<Bitmap>();

	public EmotionCache() {
		mEmotionTable = new SoftRefHashMap<Integer>(DEFAULTSIZE);
	}
	/***
	 * 
	 * @param size 初始化时size的大小
	 */
	public EmotionCache(int size){
		mEmotionTable = new SoftRefHashMap<Integer>(size);
		cache_size = size;
	}
	public EmotionCache(int size, boolean ispng){
		mEmotionTable = new SoftRefHashMap<Integer>(size);
		cache_size = size;
		this.mIspng = ispng;
	}
	/***
	 * 
	 * @param hashcode 存入的key值
	 * @param emotion  存入的表情
	 */
	public void put(int hashcode, Emotion emotion) {
		write.lock();
		try {
			max++;
			emotion.counter = max;
			if (mEmotionTable != null) {
				CacheNode node = new CacheNode(hashcode, emotion.counter);
				int ret = addToList(node);
				if (ret == -1) {// 不够资格进入缓存
				} else if (ret == 0) {// 可以进入缓存
					if (mEmotionTable.containsKey(hashcode)) {
						mEmotionTable.remove(hashcode);
					}
					mEmotionTable.put(hashcode, emotion);
				} else if (ret != 0 && ret != -1) {// ret此时是hashcode
					if (mEmotionTable.containsKey(ret)) {// /这一步可以删掉
						mEmotionTable.remove(ret);
						
						mEmotionTable.put(hashcode, emotion);
					} else {
					}
				}
			}
//			CommonUtil.log("size", "Hashmap size: " + mEmotionTable.size()
//					+ "  list size: " + list.size());
		} catch (Exception e) {

		} finally {
			write.unlock();
		}

	}	

	
	
	
	/***
	 *  -1 没有插入
	 *  0插入此值的时候没有插满
	 *  node.code 插入此值的时候已经插满
	 * @param node
	 * @return
	 */
	private int addToList(CacheNode node){///添加后进行排序
		if(list.size() < cache_size){ //没有存满
			for(int i = 0; i < list.size(); ++i){
				if(node.counter >list.get(i).counter ){
					int ret =0;
					if((ret=this.isContains(node.code))!=-1){//遇见重复数据
						list.remove(ret);
						list.add(node);
						return 0;
					}else{
						list.add(i,node);
						return 0;
					}
				}
			}
			list.add(node);

			return 0;
		}else{                              //已经满了
			for(int i = 0; i < list.size(); ++i){
				if(node.counter > list.get(i).counter){
					int ret = 0;
					if((ret=this.isContains(node.code))!=-1){
						list.remove(ret);
						list.add(i,node);
						return 0;
					}else{
						list.add(i,node);
						return list.removeLast().code;
					}
				}
			}
			int retval =list.removeLast().code;
			list.addLast(node);
			return retval;
		}
	}
	/***
	 * 判断是否存在重复数据
	 * 
	 * @param key
	 * @return 返回值为-1代表不存在重复值，其余返回值表示在list中的索引
	 */
	private int isContains(int key){
		int ret =0;
		for(Iterator<CacheNode> iter = list.iterator();iter.hasNext();){
			
			if(iter.next().code == key){
				return ret;
			}
			ret++;
		}
		return -1;
	}
	/***
	 * 返回大小 
	 * @return return -1 if null
	 */
	public int size(){
		return mEmotionTable!=null ? mEmotionTable.size() : -1;
	}
	/***
	 *  获取表情，如果没有的话 ，则从sdcard和数据库获取，如果还是没有则返回null
	 * @param key 键值
	 * @return {@link Emotion}
	 */
	public Emotion get( int key){
//		total += 1;
		Emotion emotion = mEmotionTable.get(key);
		if(emotion != null){//存在，则计数器进行加1
			 Emotion e = emotion;
					e.counter++;
					put(key, e);
		}
//		if(emotion == null&&mIspng){
//			emotion = this.getFromLocal(key);
//			if(emotion == null){
//				CommonUtil.log("xiaochao", "本地也不存在此表情");
//			}
//			return emotion;
//		}else{
//			return emotion;
//		}
		return emotion;
	}
//	private Emotion getFromLocal(int key){
//
//		final List<Integer> plist =EmotionNameList.getPackageList();
//		
//		int index = 0;
//		if(plist != null){
//			for(int i = 0; i < plist.size(); ++i){
//				Node nodeTmp = EmotionNameList.getEmotionNameList(plist.get(i));
//				if(nodeTmp== null){
//					return null;
//				}
//				index = nodeTmp.getIndex(key);
//				if(index != -1){
//					try {
//						try{
//
//							final Emotion emotion = new Emotion(nodeTmp.name[index]);
//							emotion.mFrameSize = 1;
//							
//							emotion.counter = nodeTmp.counter[index];
//							final String strpath = nodeTmp.path[index];
//
//							InputStream is;
//							is = AbstractRenrenApplication.getAppContext().getAssets().open(strpath+".png");
//							emotion.addBitmap(BitmapFactory.decodeStream(is));
//							is.close();
//							if(emotion != null){
////										EmotionNameList.updateCounter(plist.get(i), index, emotion.counter+=1);
//										put(strpath.hashCode(), emotion);
//							}
//							return emotion;
//						
//						}catch(OutOfMemoryError error){
//							CommonUtil.log("emotion"," 内存溢出 ");
//						}
//					} catch (IOException e) {
//						CommonUtil.log("xiaochao", "loading error ");
//						e.printStackTrace();
//						return null;
//					}
//				}
//			}
//		}
//		return null;
//	}
	/***
	 * 清空
	 */
	public void clear(){
		mEmotionTable.clear();
		list.clear();
	}
	private class CacheNode{
		public CacheNode(int code, int counter) {
			this.code = code;
			this.counter = counter;
		}
		public int code;
		public int counter;
	}
}

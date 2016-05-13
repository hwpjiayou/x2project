package com.renren.mobile.x2.emotion;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;


/****
 * 软引用HashMap
 * @author zxc
 *	
 * @param <K> Key值类型
 */
public class SoftRefHashMap<K> {
	private ReferenceQueue<Emotion> queue = new ReferenceQueue<Emotion>();
	private HashMap<K,_SoftReference> hashmap;
	public SoftRefHashMap() {
		this.hashmap = new HashMap<K, _SoftReference>() ;
	}
	public SoftRefHashMap(int size){
		this.hashmap = new HashMap<K, _SoftReference>(size) ;
	}
	/**
	 * 同hashmap的put方法
	 * @param key
	 * @param emotion
	 */
	public void put(K key, Emotion emotion){
//		CommonUtil.log("ref","put " + emotion.mEmotionName);
//		this.cleanCache();
		_SoftReference<K,Emotion> ref = new _SoftReference<K,Emotion>(key,emotion, queue);
		this.hashmap.put(key, ref);
	}
	/****
	 * 调用此方法很有可能返回一个NULL，即使SofthashMap有值，这取决于你当前内存的情况
	 * @param key
	 * @return
	 */
	public Emotion get(K key){
		if(!this.hashmap.containsKey(key)){
			return null;
		}
		_SoftReference<K,Emotion> ref = this.hashmap.get(key);
		if(null!=ref&&ref.get()!=null){
			Emotion emotion = ref.get();
			return emotion;
		}
		return null;
	}
	/***
	 * 同HasHmap的containsKey
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key){
		return this.hashmap.containsKey(key);
	}
	/***
	 * 同哈市map的remove
	 * @param key
	 * @return
	 */
	public Emotion remove(K key){
		Emotion e = (Emotion) this.hashmap.remove(key).get();
		
//		this.cleanCache();
		return e;
	}
	public void clear(){
		this.hashmap.clear();
	}
	/**
	 * 返回map的大小
	 * @return
	 */
	public int size(){
		return this.hashmap.size();
	}
	/***
	 * 清空已经失去软引用的
	 */
	private void cleanCache(){
		_SoftReference<K, Emotion> ref =  null;
		while((ref = (_SoftReference<K, Emotion>) queue.poll())!=null){
			this.hashmap.remove(ref.key);
		}
	}
	/***
	 * 
	 * @author 加入key值的SoftReference
	 *
	 * @param <K>
	 * @param <V>
	 */
	private class _SoftReference<K,V> extends SoftReference<V>{
		public _SoftReference(K k, V r, ReferenceQueue<? super V> q) {
			super(r, q);
			this.key = k;
		}
		public K key;
		
	}
}



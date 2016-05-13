package com.renren.mobile.x2.components.chat.util;


public interface IDataPool {

	
	
	public int getInt(String name,int defaultValue);
	public String getString(String name);
	public<T> T getObject(String name,Class<T> clazz);
	public long getLong(String name,long defaultValue);
	public void clear();
	public void put(String name,Object o);
	
	public int size();
	
	
}

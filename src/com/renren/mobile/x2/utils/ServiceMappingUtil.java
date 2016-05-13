package com.renren.mobile.x2.utils;

import java.lang.reflect.Field;

import android.content.Context;

/**
 * @author dingwei.chen
 * @说明 服务映射工具
 * */
public final class ServiceMappingUtil {
	private static ServiceMappingUtil sInstance = new ServiceMappingUtil();
	private ServiceMappingUtil(){} 
	public static ServiceMappingUtil getInstance(){
		return sInstance;
	}
	public void mappingService(Object object,Context context){
		if(context==null){
			throw new RuntimeException(" context is null");
		}
		Class clazz = object.getClass();
		Field[] fields = clazz.getFields();
		for(Field f:fields){
			ServiceMapping mapping = f.getAnnotation(ServiceMapping.class);
			System.out.println(f.getName()+":"+mapping.serviceName());
			if(mapping!=null){
				f.setAccessible(true);
				String serviceName = mapping.serviceName();
				Object o = context.getSystemService(serviceName);
				try {
					f.set(object, o);
				} catch (Exception e){}
			}
		}
	}
	public void mappingService(Class clazz,Context context){
		if(context==null){
			throw new RuntimeException(" context is null");
		}
		Field[] fields = clazz.getFields();
		for(Field f:fields){
			ServiceMapping mapping = f.getAnnotation(ServiceMapping.class);
			if(mapping!=null){
				f.setAccessible(true);
				String serviceName = mapping.serviceName();
				Object o = context.getSystemService(serviceName);
				try {
					f.set(clazz, o);
				} catch (Exception e){}
			}
		}
	}
	
}

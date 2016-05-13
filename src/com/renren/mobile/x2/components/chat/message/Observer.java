package com.renren.mobile.x2.components.chat.message;

import java.util.Map;



/**
 * @author dingwei.chen
 * @说明  观察者接口
 * */
public interface Observer {
	
	public void registorSubject(Subject subject);
	public void unregistorSubject();
	public void update(int command,Map<String,Object> data);
}

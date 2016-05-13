package com.renren.mobile.x2.core.xmpp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dingwei.chen
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface XMLMapping {
	XMLType Type();
	String Name() default "";
	boolean isIterable() default false;
	public enum XMLType {
		NODE,		//节点类型
		ATTRIBUTE	//属性类型
	}
	public enum XMLTurnType {
		INT,
		LONG,
		STRING,
		BOOL
	}
}

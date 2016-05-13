package com.renren.mobile.x2.network.talk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.renren.mobile.x2.core.xmpp.action.Actions;
/**
 * @author dingwei.chen
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface Request {
	Actions action();
	Class<?> request();
}

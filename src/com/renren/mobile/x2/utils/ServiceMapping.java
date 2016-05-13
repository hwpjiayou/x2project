package com.renren.mobile.x2.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
@Inherited
/**
 * @author dingwei.chen
 * @说明 服务映射注解
 * */
@interface ServiceMapping {
	String serviceName();
}

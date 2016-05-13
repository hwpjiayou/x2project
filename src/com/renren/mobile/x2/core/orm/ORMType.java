package com.renren.mobile.x2.core.orm;

/**
 * @author dingwei.chen
 * @说明 ORM类型
 * */
public interface ORMType {

	public final static int NORMAL_COLUMN = 0;// 基本类型
	public final static int ITERATOR = 1;// 迭代模式
	public final static int CLASS = 2;// 对象集合模式
}

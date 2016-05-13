package com.renren.mobile.x2.utils;

public abstract class AbstractCloneable<T> implements Cloneable {

	@Override
	public T clone() {
		try {
			return (T) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}

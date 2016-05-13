package com.renren.mobile.x2.components.chat.util;

import java.util.Observable;

public final  class ObservableImpl extends Observable {

	private static ObservableImpl sInstance = new ObservableImpl();
	private ObservableImpl(){}
	public static ObservableImpl getInstance (){
		return sInstance;
	}
	@Override
	public void notifyObservers(Object data) {
		if(data!=null){
			super.setChanged();
			super.notifyObservers(data);
		}
	}
	
	
}

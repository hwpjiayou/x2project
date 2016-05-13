package com.renren.mobile.x2.components.chat.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * @author dingwei.chen
 * @说明 界面元素持有类
 * */
public abstract class BaseHolder {

    public View mRoot = null;
	
	public BaseHolder(){}
	
	public BaseHolder(Context context,int layoutID){
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = inflater.inflate(layoutID, null);
	}
	public BaseHolder(Context context,int layoutID,ViewGroup root){
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = inflater.inflate(layoutID, root);
	}
	
	
	public View getRootView(){
		return mRoot;
	}
	
	public abstract void initViews();
	
}

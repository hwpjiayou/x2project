package com.renren.mobile.x2.emotion;

import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;
import com.renren.mobile.x2.emotion.IEmotionManager.OnEmotionSelectCallback;
import com.renren.mobile.x2.utils.CommonUtil;

import android.content.Context;
import android.view.View;



public class EmotionManager {
	private static Context mContext;
	public static EmotionManager mInstance ;
	public EmotionManager() {
		ininEmotionData();
		initEmotionNameList();
		initEmotionPool();
	}
	public static EmotionManager getInstance(){
		if(mInstance == null){
			long start = System.currentTimeMillis();
			mInstance = new EmotionManager();
			CommonUtil.log("time","cost"+ (System.currentTimeMillis()-start));
			
		}
		return mInstance;
	}
	public static void setContext(Context context){
		mContext = context;
	}
	/*
	 * 1初始化数据
	 */
	private void ininEmotionData(){
		EmotionData.getInstance();
	}
	
	/***
	 * 2初始化Namelist
	 */
	private void initEmotionNameList(){
		EmotionNameList.initEmotion_NameList();
	}
	
	/***
	 * 3加载表情资源
	 */
	private void initEmotionPool(){
		EmotionPool.setContext(mContext);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				EmotionPool.getInstance();
			}
		}).start();
	}
	
	/***
	 * 4初始化view
	 */
	private void initEmotionView(){
		
	}
	public View getView(Context context,boolean isneedgif,OnEmotionSelectCallback listener){
		long start = System.currentTimeMillis();
		ErrLog.ll("isneedGIf " + isneedgif);
		EmotionScreen retscreen = new EmotionScreen(context,isneedgif);
		retscreen.registerCallback(listener);
		ErrLog.ll("createview Tiem " + (System.currentTimeMillis()-start) );
		return retscreen.getview();
	}
	
	
	
}

package com.renren.mobile.x2.emotion;

import com.renren.mobile.x2.utils.SystemService;

import android.util.DisplayMetrics;

/**
 * @author dingwei.chen
 * */
public class DipUtil {
	
	public static int getScreenWidth(){
		return getDisplayMetrics().widthPixels;
	}
	public static int getScreentHeight(){
		return getDisplayMetrics().heightPixels;
	}
	
	public static int calcFromDip(int number){
		float f = getDisplayMetrics().density;
		return (int)(number*f);
	}
	static DisplayMetrics sDisplay = null;
	public static DisplayMetrics getDisplayMetrics(){
		if(sDisplay==null){
			sDisplay = new DisplayMetrics();
			SystemService.sWindowsManager.getDefaultDisplay().getMetrics(sDisplay);
		}
		return sDisplay;
	}
}

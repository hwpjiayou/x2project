package com.renren.mobile.x2.components.chat.util;

import java.util.List;

import com.renren.mobile.x2.utils.SystemService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.os.PowerManager;


/**
 * @author dingwei.chen
 * */
public class CurrentChatSetting {

	/**/
	public static boolean isScreenOn(){
		PowerManager powerManager = SystemService.sPowerManager;
		return powerManager.isScreenOn();
	} 
	
	public static boolean isCurrentAppRunBackground(){
		ActivityManager manager =SystemService.sActivityManager;
        List<RunningTaskInfo> list = manager.getRunningTasks(1);
        return !list.remove(0).baseActivity.getPackageName().equalsIgnoreCase("com.renren.mobile.x2");
	} 
	public static int STATE = 0;
	public static final int MASK_CHAT_ACTIVITY_RESUME = 0x1;
	public static final int MASK_GROUP = 0x1;
	public static long CHAT_ID =0;
	
	public static void onChatActivityResume(){
		STATE = 0;
		STATE |=MASK_CHAT_ACTIVITY_RESUME;
	}
	public static void onChatActivityStop(){
		STATE &=~MASK_CHAT_ACTIVITY_RESUME;
		CHAT_ID = -1L;
	}
	public static boolean isChatActivityTop(){
		return calcState(MASK_CHAT_ACTIVITY_RESUME);
	}
//	public static boolean isGroupValueEquals(int v){
//		if(calcState(MASK_GROUP)){
//			return v==GROUP.GROUP.Value;
//		}else{
//			return v==GROUP.CONTACT_MODEL.Value;
//		}
//	}
//	public static void setGroupValue(int v){
//		STATE&=~MASK_GROUP;
//		if(v == GROUP.GROUP.Value){
//			STATE|=MASK_GROUP;
//		}
//	}
	
	private static boolean calcState(int mask){
		int s = STATE&mask;
		return s!=0;
	}
	
	
	
	
}

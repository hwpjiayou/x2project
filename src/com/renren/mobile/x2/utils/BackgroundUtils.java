package com.renren.mobile.x2.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;

import com.renren.mobile.x2.RenrenChatApplication;

/**
 * 切换到后台的工具类
 * **/
public class BackgroundUtils {
	private static BackgroundUtils mBackgroundUtils = new BackgroundUtils();
	private boolean mCurrenState;
	private boolean mOldState = false;// false为后台，true为前台
	private String mPackageName = "";
	
	private BackgroundUtils() {
		mPackageName = RenrenChatApplication.getApplication().getPackageName();
	}

	public static BackgroundUtils getInstance() {
		return mBackgroundUtils;
	}
	
	public void dealAppRunState(){
			background();
	}
	
	/**
	 * @param awakeWay 唤醒方式（在isActive为false的情况下）
	 * @param isActive 是否是主动运行
	 */
	public void dealAppRunState(String awokeWay, boolean isActive){
		boolean show = isAppOnForeground();
		if(show){
			forceground(show, awokeWay, isActive);
		}else{
			background();
		}
	}

	private void forceground(boolean show,String awokeWay, boolean isActive) {
		mCurrenState = show;
		if (mCurrenState != mOldState){//切换到前台
			mOldState = mCurrenState;
			dealForceground(awokeWay, isActive);
		}
	}

	private void background() {
		mCurrenState = isAppOnForeground();
		if(mCurrenState == false&&mCurrenState!= mOldState){//切换到后台
			mOldState = false;
			dealBackground();
		}

	}
	
	//前台切换到后台
	private void dealBackground() {
		// TODO
//		AbstractRenrenApplication.IS_AUTO = IS_AUTO_VALUE.BACKGROUND;;
	}
	
	/**
	 * 后台切换到前台
	 * @param awokeWay 唤醒方式（在isActive为false的情况下）
	 * @param isActive 是否是主动运行
	 */
	private void dealForceground(String awokeWay, boolean isActive){
		
		// TODO
//		AbstractRenrenApplication.IS_AUTO = IS_AUTO_VALUE.FOREGROUND;
		
		//统计 TODO
//		LocalStatisticsManager.getInstance().computeStatistics(awokeWay, isActive);

	}


	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device
		ActivityManager activityManager = SystemService.sActivityManager;
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(mPackageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && tasksInfo.size() > 0 && mPackageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/** ***********************************************************
	 * 群聊统计方法
	 */
	/**
	 * 
	 * @param key 统计键值对key
	 */
	public void multiChatStatistics(String key){
//		LocalStatisticsManager.getInstance().computeMultiChatStatistics(key); TODO
	}
	/** ***********************************************************/
}

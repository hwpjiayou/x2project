package com.renren.mobile.x2.components.chat.util;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.utils.SystemService;
import com.renren.mobile.x2.utils.log.Logger;


public final class ChatUtil {
	
	//默认图片
		public static Bitmap sDefaultBitmap_left = null;
		public static Bitmap sDefaultBitmap_right = null;
		
	public static String getUserRootPath(){
		String userRootDir = 
				"/sdcard/x2/"+LoginManager.getInstance().getLoginInfo().mUserId+"/";
		File dir = new File(userRootDir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		createNomediaFile();
		return userRootDir;
	}
	
	public static String getUserAudioPath(long toChatId){
		String userRootDir = getUserRootPath();
		String audioPath = userRootDir+"Audio/"+toChatId+"/";
		File dir = new File(audioPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		return audioPath;
	}
	
	private static void createNomediaFile() {
		String nomediaPath = "/sdcard/x2/.nomedia";
		File nomedia = new File(nomediaPath);
		if (!nomedia.exists()) {
			try {
				nomedia.createNewFile();
			} catch (IOException e) {
				if(Logger.mDebug){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getUserPhotos(long toChatId){
		String userRootDir = getUserRootPath();
		String photoPath = userRootDir+"Photos/"+toChatId+"/";
		createDir(photoPath);
		return photoPath;
	}
	
	private static void createDir(String dir){
		File dirFile = new File(dir);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
	}
	
	public static Bitmap getDefualtBitmap(boolean isLeft){
		if(sDefaultBitmap_left==null){
			sDefaultBitmap_left = BitmapFactory.decodeResource(RenrenChatApplication.getApplication().getResources(), R.drawable.test_default);
		}
		if(sDefaultBitmap_right==null){
			sDefaultBitmap_right = BitmapFactory.decodeResource(RenrenChatApplication.getApplication().getResources(), R.drawable.test_default);
		}
		if(isLeft){
			return sDefaultBitmap_left;
		}else{
			return sDefaultBitmap_right;
		}
	}
	
	public static int calcFromDip(int number){
		float f = getDisplayMetrics().density;
		return (int)(number*f);
	}
	
	static DisplayMetrics mDisplay = null;
	public static DisplayMetrics getDisplayMetrics(){ //<TODO cf> 换成统一的
		if(mDisplay==null){
			mDisplay = new DisplayMetrics();
			WindowManager windowsManager = SystemService.sWindowsManager;
			windowsManager.getDefaultDisplay().getMetrics(mDisplay);
		}
		return mDisplay;
	}
	
	public static String getUserAudioFromOutToLocal(long toChatId){
		String dir = getUserAudioPath(toChatId);
		String newDir = dir+"out_to_local/";
		File dirFile = new File(newDir);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		return newDir;
	}
	public static String getUserAudioFromLocalToOut(long toChatId){
		String dir = getUserAudioPath(toChatId);
		String newDir = dir+"local_to_out/";
		File dirFile = new File(newDir);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		return newDir;
	}

	
	//小图路径
	public static String getUserPhotos_Tiny(long toChatId){
		String parentDir = getUserPhotos(toChatId);
		String dir = parentDir+"tiny/";
		createDir(dir);
		return parentDir;
	}
	
	//中图路径
	public static String getUserPhotos_Main(long toChatId){
		String parentDir = getUserPhotos(toChatId);
		String dir = parentDir+"main/";
		createDir(dir);
		return parentDir;
	}
	//大图路径
	public static String getUserPhotos_Large(long toChatId){
		String parentDir = getUserPhotos(toChatId);
		String dir = parentDir+"large/";
		createDir(dir);
		return parentDir;
	}
	public static String createRecordFileName(long userId){
		String str = getUserAudioFromLocalToOut(userId)+System.currentTimeMillis();
		return str;
	}
	
	public static String getText(int id){
		return RenrenChatApplication.getApplication().getText(id).toString();
	}
	public static String getText(int ...ids){
		StringBuilder sb = new StringBuilder();
		for (int id : ids) {
			sb.append(RenrenChatApplication.getApplication().getText(id));
		}
		return sb.toString();
	}
	
}

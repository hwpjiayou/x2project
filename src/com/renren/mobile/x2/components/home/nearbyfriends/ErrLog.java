package com.renren.mobile.x2.components.home.nearbyfriends;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import com.renren.mobile.x2.RenrenChatApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
/***
 * 一个我用来打log的玩意，最后得删掉
 * @author zxc
 *
 */
public class ErrLog {
	private static String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	private static File file;
	private final static String TAG ="errzxc";
	private static ArrayList<Object> errlist = new ArrayList<Object>();
	private static boolean isPrintLog = true;
	private static boolean isWriteToLocal = true;
	private static Context context = RenrenChatApplication.getApplication();
	
	private static SharedPreferences preferences = context.getSharedPreferences(TAG, 0);
	private static StackTraceElement stacks[] ;
	public static void Print(Object log){
		stacks = Thread.currentThread().getStackTrace();
		StringBuffer sb = new StringBuffer();
		for(int i = 3;  i < stacks.length;++i){
			if(!stacks[i].getClassName().contains("com.renren")){
				break;
			}
			sb.append(stacks[i]);
		}
		if(log==null){
			return;//防止空指针异常
		}
		if(log instanceof JSONObject){
			log = log.toString();
		}
		try{
		
			if(log.toString().length()> 100){
				isPrintLog = false;
			}else{
				isPrintLog = true;
			}
			if(isPrintLog){
				Log.d(TAG,""+sb.toString()+log.toString());
			}
		errlist.add(log);
		if(log.toString().length()> 100){
			isWriteToLocal = true;
		}else{
			isWriteToLocal = false;
		}
		
		if(isWriteToLocal){
			writeToFile(log+"  time  "+ longToString(System.currentTimeMillis()) +  '\n');
		}}
		catch(NullPointerException e){
			Throwable throwable= new Throwable();
			throwable.getStackTrace();
			Log.d(TAG,"NullPointerException " +e.getLocalizedMessage()  );
		}
	}
	public static void log(){
		stacks = Thread.currentThread().getStackTrace();
		StringBuffer sb = new StringBuffer();
		for(int i = 3;  i < stacks.length;++i){
			if(!stacks[i].getClassName().contains("com.renren")){
				break;
			}
			sb.append(stacks[i]);
		}
		Log.d("xiaochao","" + sb.toString());
	}
	private static  void writeToFile(String str){
		file = new File(mFilePath+"/"+getCurrentData(System.currentTimeMillis())+".txt");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
//				Log.d("zxc","1IO EXception " + e.getLocalizedMessage());
			}
		}
		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(str);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			Log.d("zxc","2IO EXception " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private  static String  longToString(Long time){
        Date date= new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        System.out.println("TIME:::"+dateString);
        return dateString;
    }
	private static String getCurrentData(Long time){
//		Log.d("zxc","log1"+longToString(time));
		return longToString(time).substring(0,10);
	}
	public static void ll(String log){
		Log.d("emotion",""+log);
	}
}

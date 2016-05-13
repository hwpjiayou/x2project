package com.renren.mobile.x2.proxy;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;

import com.renren.mobile.x2.components.login.LoginModel;


/**
 * @author dingwei.chen
 * @说明 人人客户端代理类
 * */
public final class RenrenClientProxy implements IRenrenClientProxy{

	private static RenrenClientProxy sProxy = new RenrenClientProxy();
	private static char key='c';
	private RenrenClientProxy(){}
	
	public static RenrenClientProxy getProxy(){
		return sProxy;
	}
	
	
	
	@Override
	public boolean isInstall(Context context) {
		PackageManager packManager = context.getPackageManager();
		List<PackageInfo> list = packManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		boolean flag = false;
		for(PackageInfo info:list){
			if(info.packageName.equals(IRenrenClientProxy.RENREN_CLIENT_APPLICATION_NAME)){
				flag = true;
				break;
			}
		}
		return flag;
	}

	@Override
	public LoginModel queryLoginInfo(Context context) {
		try {
			if(context == null){
				return null;
			}
			String whereSql = "isDefault=1";//当前登录账号用  isDefault=1 产品规定注销用户不自动登录
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(IRenrenClientProxy.ACCOUNT_URI, 
											null,//列名
											whereSql,//where
											null,
											null);
			try {
				if(cursor!=null){
					cursor.moveToFirst();
					LoginModel info = new LoginModel();
					info.mUserId = cursor.getString(cursor.getColumnIndex("uid"));
					info.mHeadUrl = cursor.getString(cursor.getColumnIndex("head_url"));
					info.mSessionKey = cursor.getString(cursor.getColumnIndex("sessionkey"));
					info.mSessionKey = decode(info.mSessionKey);
					info.mSecretKey = cursor.getString(cursor.getColumnIndex("srt_key"));
					info.mTicket = cursor.getString(cursor.getColumnIndex("ticket"));
					info.mPassword = cursor.getString(cursor.getColumnIndex("pwd"));
					info.mPassword = decode(info.mPassword);
					info.mAccount = cursor.getString(cursor.getColumnIndex("account"));
					info.mUserName = cursor.getString(cursor.getColumnIndex("name"));
					//info.createJsonValue();
					//SystemUtil.logykn("Password:" + info.mPassword);
					cursor.close();
					return info;
				}
			} catch (Exception e) {}
			
		} catch (Exception e) {}
	
		return null;
	}
	/**
	 * @author dingwei.chen
	 * @说明 客户端采用的不大可靠的取反   加密session
	 * */
	private  String decode(String toDecode) {
		if (toDecode == null) {
			return null;
		}
		char[] decode = toDecode.toCharArray();
		StringBuilder decoded = new StringBuilder();
		for (int i = 0; i < decode.length; i++) {
			decoded.append((char) (decode[i] ^ key));
		}
		return decoded.toString();
	}
	
}

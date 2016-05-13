package com.renren.mobile.x2.db.dao;

import java.util.ArrayList;

import org.json.JSONObject;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.components.login.LoginModel;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.db.sql.QueryAccount;
import com.renren.mobile.x2.db.table.AccountColumn;
import com.renren.mobile.x2.proxy.RenrenClientProxy;
import com.renren.mobile.x2.utils.RRSharedPreferences;


/**
 * @author dingwei.chen
 * @说明 账户DAO 
 * @说明 采用不同的数据存储方式(文件存储方法)
 * @说明 文件存储明文有高度的不可靠性因此要对数据文件进行加密处理	
 * @加密方式:
 * 		对存入文件进行取反操作
 * <b>数据模型</b>
 * @see com.renren.mobile.chat.LoginInfo
 * */
public class AccountDAO extends BaseDAO { 
	
    Query mQuery_Account = new QueryAccount(this);
	
	public AccountDAO(BaseDBTable table) {
		super(table);
	}


	public synchronized LoginModel loadPreUserData() {
		LoginModel info = null;
		info = loadAutoLoginUserData();
		RRSharedPreferences rrSharedPreferences = new RRSharedPreferences(RenrenChatApplication.getApplication(), "login_authorize");
		if(info == null) {
			// 本地的帐号中没有 auto_login == 1 的账户，并且检测到本地官方客户端有登陆账户
			// info = readLoginMessageFromRenren(RenrenChatApplication.getAppContext());
			
			LoginModel loginfo = readLoginMessageFromRenren(RenrenChatApplication.getApplication().getApplicationContext());
			
			if(loginfo != null){
				// 标志人人授权登陆 1 授权登陆页面
				LoginManager.getInstance().getLoginInfo().mAccount = loginfo.mAccount;
				LoginManager.getInstance().getLoginInfo().mPassword = loginfo.mPassword;
				LoginManager.getInstance().getLoginInfo().mHeadUrl = loginfo.mHeadUrl;
				LoginManager.getInstance().getLoginInfo().mUserName = loginfo.mUserName;
				LoginManager.getInstance().getLoginInfo().mUserId = loginfo.mUserId;
				rrSharedPreferences.putIntValue("renren_authorize", 1);
			} else {
				rrSharedPreferences.putIntValue("renren_authorize", 0);
			}
		} else {
			// 标志人人授权登陆 0 自动登陆
			rrSharedPreferences.putIntValue("renren_authorize", 0);
		}
		
		return info;
    }
	
	
	
	public synchronized LoginModel loadAutoLoginUserData() {
		ArrayList<LoginModel> infos = getAllAccountInfoDB();
		for(int i = 0; infos != null && i < infos.size(); i ++) {
//			Log.d("NCS", "infos.get(i).mAutoLogin--" + infos.get(i).mAutoLogin);
			if (infos.get(i).mAutoLogin == 1) {
				return infos.get(i);
			}
		}
		return null;
	}
	
	public synchronized LoginModel loadLastLoginUserData() {
		ArrayList<LoginModel> infos = getAllAccountInfoDB();
		for(int i = 0; infos != null && i < infos.size() ; i ++) {
//			Log.d("NCS", "infos.get(i).mLastLogin--" + infos.get(i).mLastLogin);
			if (infos.get(i).mLastLogin == 1) {
				return infos.get(i);
			}
		}
		return null;
	}
	
	
	// 保存新登陆的账户
	public synchronized void saveUserData(String account,String password_md5,JSONObject object){
		LoginModel info = LoginManager.getInstance().parseLoginInfo(object);
		info.mAccount = account;
		info.mPassword = password_md5;
		// 新登陆的账户为自动登陆和最后登陆帐号
		info.mAutoLogin = 1;
		info.mLastLogin = 0;
		this.saveUserData(info);
	}
	
	/**
	 * 将所有用户信息更新后保存到数据库中
	 * 新加本次登陆info，修改历史登陆账户的info
	 * 保存到数据库
	 * @param info
	 */
	public synchronized void saveUserData(LoginModel info){
		info.mLastLogin = 1;
		ArrayList<LoginModel> infos = getAllAccountInfoDB();
//		Log.d("NCS", "DAO:"+info.mAccount);
//		Log.d("NCS", "DAO:"+info.mUserName);
//		Log.d("NCS", "DAO:"+info.mPassword);
		info.mAutoLogin = 1;
		for (int i = 0; infos != null && i < infos.size(); i ++) {
			if (infos.get(i).mAccount != null && infos.get(i).mAccount.equals(info.mAccount)) {
				infos.remove(i);
				// test
				i --;
			} else {
				infos.get(i).mLastLogin = 0;
				infos.get(i).mAutoLogin = 0;
			}
		}
//		for (int i = 0; infos != null && i < infos.size(); i ++) {
//			infos.get(i).mAutoLogin = 0;
			// infos.get(i).mLastLogin = 0;
//			infos.get(i).mPassword = "";
//		}
		if (infos == null) {
			infos = new ArrayList<LoginModel>();
		}
//		Log.d("NCS", "DAO:"+info.mAccount);
		if (info != null && !TextUtils.isEmpty(info.mAccount)) {
			infos.add(info);
		}
		saveAccountInfoDB(infos); // 将用户信息存储到DB数据库中
		//this.saveLoginedAccount(info.mAccount); // 保存账户到文件
	}
	
	
	public synchronized void saveUserData(String json_String){
		try{
			//JSONObject object = (JSONObject)JsonParser.parse(json_String);
			JSONObject object = new JSONObject(json_String);
			LoginModel info = LoginManager.getInstance().parseLoginInfo(object);
			saveAccountInfoDB(info);
		}catch(Exception e){
			
		}
	}
	
	public synchronized void saveUserLogoutInfo(String account) {
		clearAccountInfoDB();//清空账号信息
	}
	
	// 注销时更新数据库账户
	public synchronized void updateUserInfoLogout(LoginInfo info) {
		ArrayList<LoginModel> infos = getAllAccountInfoDB();
		for (int i = 0; infos != null && i < infos.size(); i ++) {
			if (infos.get(i).mAccount.equals(info.mAccount)) {
				infos.remove(i);
				// test
				i --;
			}
		}
		for (int i = 0; infos != null && i < infos.size(); i ++) {
			infos.get(i).mPassword = "";
			infos.get(i).mAutoLogin = 0;
			infos.get(i).mLastLogin = 0;
		}
//		info.mPassword = "";
		info.mAutoLogin = 1;
		info.mLastLogin = 1;
		
		LoginModel loginfo = new LoginModel();
		loginfo.parse(info);
		if (infos == null) {
			infos = new ArrayList<LoginModel>();
		}
		if (loginfo != null && !TextUtils.isEmpty(loginfo.mAccount)) {
			infos.add(loginfo);
		}
		saveAccountInfoDB(infos);
	}
	
	/**
	 * @author kaining.yang
	 * @param info 
	 */
	public synchronized void saveAccountInfoDB(LoginModel info) {
		// 不删除历史登陆用户信息
		deleteAccount(null);
		
		if (info == null || TextUtils.isEmpty(info.mAccount)) {
			return;
		}
		
		ContentValues values = new ContentValues();
		values.put(AccountColumn.LOGIN_USERID, info.mUserId);
		values.put(AccountColumn.LOGIN_PASSWORD, info.mPassword);
		values.put(AccountColumn.LOGIN_SESSIONKEY, info.mSessionKey);
		values.put(AccountColumn.LOGIN_SECRETKEY, info.mSecretKey);
		values.put(AccountColumn.LOGIN_TICKET, info.mTicket);
		values.put(AccountColumn.LOGIN_HEADURL, info.mHeadUrl);
		values.put(AccountColumn.LOGIN_USERNAME, info.mUserName);
		values.put(AccountColumn.LOGIN_ACCOUNT, info.mAccount);
		values.put(AccountColumn.LOGIN_HEAD_LARGE, info.mHeadUrl);
		values.put(AccountColumn.LOGIN_HEAD_MEDIUM, info.mMediumUrl);
		values.put(AccountColumn.LOGIN_HEAD_ORIGINAL, info.mOriginal_Url);
		values.put(AccountColumn.BIND_INFO, info.mBindInfo);
		values.put(AccountColumn.IS_AUTO_LOGIN, info.mAutoLogin);
		values.put(AccountColumn.IS_LAST_LOGIN, info.mLastLogin);
		values.put(AccountColumn.PROFILE_GENDER, info.mGender);
		values.put(AccountColumn.PROFILE_BIRTHDAY, info.mBirthday);
		values.put(AccountColumn.PROFILE_SCHOOL, info.mSchool);
		values.put(AccountColumn.IS_FIRST_LOGIN, info.mIsFirstLogin);
		
		//values.put(AccountColumn.PROFILE_PRIVATE, info.mPrivate);
		ORMUtil.getInstance().ormInsert(LoginModel.class, info, values);
		mInsert.insert(values);
	}
	
	/**
	 * 将本次登陆账户和历史账户一并存入数据库
	 * @author kaining.yang
	 * @param infos
	 */
	public synchronized void saveAccountInfoDB(ArrayList<LoginModel> infos) {
		// 不删除历史登陆用户信息
		deleteAccount(null);
//		Log.d("NCS", "infos:"+infos.size());
		for (int i = 0; i < infos.size(); i ++) {
			
			if (infos.get(i) == null || TextUtils.isEmpty(infos.get(i).mAccount)) {
				continue;
			}
			ContentValues values = new ContentValues();
			values.put(AccountColumn.LOGIN_USERID, infos.get(i).mUserId);
			values.put(AccountColumn.LOGIN_PASSWORD, infos.get(i).mPassword);
			values.put(AccountColumn.LOGIN_SESSIONKEY, infos.get(i).mSessionKey);
			values.put(AccountColumn.LOGIN_SECRETKEY, infos.get(i).mSecretKey);
			values.put(AccountColumn.LOGIN_TICKET, infos.get(i).mTicket);
			values.put(AccountColumn.LOGIN_HEADURL, infos.get(i).mHeadUrl);
			values.put(AccountColumn.LOGIN_USERNAME, infos.get(i).mUserName);
			values.put(AccountColumn.LOGIN_ACCOUNT, infos.get(i).mAccount);
			values.put(AccountColumn.LOGIN_HEAD_LARGE, infos.get(i).mHeadUrl);
			values.put(AccountColumn.LOGIN_HEAD_MEDIUM, infos.get(i).mMediumUrl);
			values.put(AccountColumn.LOGIN_HEAD_ORIGINAL, infos.get(i).mOriginal_Url);
			values.put(AccountColumn.BIND_INFO, infos.get(i).mBindInfo);
			values.put(AccountColumn.IS_AUTO_LOGIN, infos.get(i).mAutoLogin);
			values.put(AccountColumn.IS_LAST_LOGIN, infos.get(i).mLastLogin);
			values.put(AccountColumn.PROFILE_GENDER, infos.get(i).mGender);
			values.put(AccountColumn.PROFILE_BIRTHDAY, infos.get(i).mBirthday);
			values.put(AccountColumn.PROFILE_SCHOOL, infos.get(i).mSchool);
			values.put(AccountColumn.IS_FIRST_LOGIN, infos.get(i).mIsFirstLogin);
			
			//values.put(AccountColumn.PROFILE_PRIVATE, infos.get(i).mPrivate);
//			Log.d("NCS", "value::"+values.toString());
			ORMUtil.getInstance().ormInsert(LoginModel.class, infos.get(i), values);
			mInsert.insert(values);
		}
	}
	
	/**
	 * 更新数据库当前帐号的信息
	 * @author kaining.yang
	 * @param infos
	 */
	public synchronized void updateAccountInfoDB(LoginModel info) {
		ContentValues values = new ContentValues();
		values.put(AccountColumn.LOGIN_USERID, info.mUserId);
		values.put(AccountColumn.LOGIN_PASSWORD, info.mPassword);
		values.put(AccountColumn.LOGIN_SESSIONKEY, info.mSessionKey);
		values.put(AccountColumn.LOGIN_SECRETKEY, info.mSecretKey);
		values.put(AccountColumn.LOGIN_TICKET, info.mTicket);
		values.put(AccountColumn.LOGIN_HEADURL, info.mHeadUrl);
		values.put(AccountColumn.LOGIN_USERNAME, info.mUserName);
		values.put(AccountColumn.LOGIN_ACCOUNT, info.mAccount);
		values.put(AccountColumn.LOGIN_HEAD_LARGE, info.mLargeUrl);
		values.put(AccountColumn.LOGIN_HEAD_MEDIUM, info.mMediumUrl);
		values.put(AccountColumn.LOGIN_HEAD_ORIGINAL, info.mOriginal_Url);
		values.put(AccountColumn.BIND_INFO, info.mBindInfo);
		values.put(AccountColumn.IS_AUTO_LOGIN, info.mAutoLogin);
		values.put(AccountColumn.IS_LAST_LOGIN, info.mLastLogin);
		values.put(AccountColumn.PROFILE_GENDER, info.mGender);
		values.put(AccountColumn.PROFILE_BIRTHDAY, info.mBirthday);
		values.put(AccountColumn.PROFILE_SCHOOL, info.mSchool);
		values.put(AccountColumn.PROFILE_PRIVATE, info.mPrivate);
		values.put(AccountColumn.IS_FIRST_LOGIN, info.mIsFirstLogin);
		
		ORMUtil.getInstance().ormUpdate(LoginModel.class, info, values);
		String whereString = AccountColumn.LOGIN_ACCOUNT + "=" + "'"+ info.mAccount + "'";
		mUpdate.update(values, whereString);
	}
	
	
	public synchronized void deleteAccount(String where){
		mDelete.delete(where);
	}
	
	/**
	 * 更新数据库中的用户信息
	 **/
	public synchronized void clearAccountInfoDB() {
        ContentValues values = new ContentValues();
        values.put(AccountColumn.LOGIN_USERID, "");
		values.put(AccountColumn.LOGIN_PASSWORD, "");
		values.put(AccountColumn.LOGIN_SESSIONKEY, "");
		values.put(AccountColumn.LOGIN_SECRETKEY, "");
		values.put(AccountColumn.LOGIN_TICKET, "");
		values.put(AccountColumn.LOGIN_HEADURL, "");
		values.put(AccountColumn.LOGIN_USERNAME, "");
		values.put(AccountColumn.LOGIN_HEAD_LARGE, "");
		values.put(AccountColumn.LOGIN_HEAD_MEDIUM, "");
		values.put(AccountColumn.LOGIN_HEAD_ORIGINAL, "");
		values.put(AccountColumn.BIND_INFO, "");
		values.put(AccountColumn.IS_AUTO_LOGIN, 0);
		values.put(AccountColumn.IS_LAST_LOGIN, 0);
		values.put(AccountColumn.PROFILE_GENDER, -1);
		values.put(AccountColumn.PROFILE_BIRTHDAY, "");
		values.put(AccountColumn.PROFILE_SCHOOL, "");
		values.put(AccountColumn.IS_FIRST_LOGIN, -1);
		//values.put(AccountColumn.PROFILE_PRIVATE, -1);
		mUpdate.update(values, null);
	}
	
	/**
	 * 从DB中获取用户信息
	 * */
	/*public LoginfoModel getAccountInfoDB(){
		this.beginTransaction();
		LoginfoModel abm =  mQuery_Account.query(null, null, null, null, null,  LoginfoModel.class);
		Log.v("abm","===================abm:"+abm);
		this.commit();
		return abm;
	}	
	*/
	
	/**
	 * @author kaining.yang
	 * 从DB中获取所有登陆过的用户信息
	 * */
	public synchronized ArrayList<LoginModel> getAllAccountInfoDB(){
		ArrayList<LoginModel> abm = mQuery_Account.query(null, null, null, null, null,  ArrayList.class);
		return abm;
	}
	
	/**
	 * 获取所有登录过的用户名
	 * @return
	 */
	/*public ArrayList<String> getAllAccounts(){
		try{
			ArrayList<String> list = new ArrayList<String>();
			
			HashMap<String,String> map = getMapAccounts();
			if(map!=null){
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
				    Map.Entry entry = (Map.Entry) iter.next();
				    list.add(entry.getKey().toString());
				} 
			}
			return list;
		}catch(Exception e){
			
		}
		return null;
	}*/
	
	public synchronized ArrayList<String>  getAllAccounts(){
		ArrayList<String> accountList = new ArrayList<String>();
		ArrayList<LoginModel> infos = getAllAccountInfoDB();
		if (infos != null) {
			for (LoginModel info : infos) {
				accountList.add(info.mAccount);
			}
		}
		return accountList;
	}
	 
	/**
	 * @author dingwei.chen
	 * 读取官方客户端的账户信息
	 * */
	public synchronized LoginModel readLoginMessageFromRenren(Context context){
		try {
			if(RenrenClientProxy.getProxy().isInstall(context)){
				LoginModel info = RenrenClientProxy.getProxy().queryLoginInfo(context);
				/*if(info != null){
					this.saveLoginedAccount(info.mAccount);
				}*/
				return  info;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	 

}

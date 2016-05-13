package com.renren.mobile.x2.components.login;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;

import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog; 
import com.renren.mobile.x2.db.dao.AccountDAO;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.talk.DomainUrl;
import com.renren.mobile.x2.network.talk.binder.LocalBinder;
import com.renren.mobile.x2.utils.AbstractCloneable;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;

/**
 * 单例实现，封装了登录和注销的操作
 */
public class LoginManager {
	public static final String LOGIN_TYPE = "login_type";
	public static final int LOGIN_SIXIN = 0;
	public static final int LOGIN_RENREN = 1;
	private LoginInfo mLoginInfo = new LoginInfo();

	private static LoginManager instance = new LoginManager();
	private List<LoginListener> mLoginListeners = new LinkedList<LoginListener>();
	private AccountDAO mAccountDao = null;
	private String mPassWord;

	protected void setLoginfo(LoginInfo loginfo) {
		this.mLoginInfo = loginfo;
	}

	protected LoginManager() {
		mAccountDao = DAOFactoryImpl.getInstance().buildDAO(AccountDAO.class);
		clean();
	}

	public static LoginManager getInstance() {
		return instance;
	}

	public static class LoginResponse implements INetResponse {
		LoginStatusListener mListener = null;
		String mAccount = null;
		String mPassword = null;

		public LoginResponse(LoginStatusListener listener) {
			mListener = listener;
		}

		public LoginResponse(String account, String password,
				LoginStatusListener listener) {
			mListener = listener;
			this.mAccount = account;
			this.mPassword = password;
		}

		@Override
		public void response(INetRequest req, JSONObject object) {
			mListener.onLoginResponse(object);
			CommonUtil.log("login", "login json:" + object.toString());
			ErrLog.Print("logresponse " + object.toString());
			if (Methods.checkNoError(req, object)) {
				LoginModel model = LoginManager.getInstance().parseLoginInfo(
						object);
				model.mPassword = mPassword;
				LoginManager.getInstance().mAccountDao.saveUserData(model);
				LoginManager.getInstance().initLoginInfo(model.getLoginfo());
				mListener.onLoginSuccess(object, req.getCurrentSession());
			} else {
				mListener.onLoginFailed(object, req.getCurrentSession());
			}
		}

	}

	// public void login(int accountType, String account, String pwd, String
	// captcha, long session, LoginStatusListener listener) {
	// HttpMasService.getInstance().login(
	// accountType, account, pwd,
	// session,LoginManager.getInstance().getLoginInfo().mCaptchaNeeded,
	// captcha,
	// new LoginResponse(account, pwd, listener));
	// mPassWord = pwd;
	// }

	public void loginX2(int accountType, String account, String pwd,
			String captcha, long session, LoginStatusListener listener) {
		HttpMasService.getInstance().loginX2(accountType, session, account,
				pwd, LoginManager.getInstance().getLoginInfo().mCaptchaNeeded,
				captcha, new LoginResponse(account, pwd, listener));
		mPassWord = pwd;
	}

	public boolean autoLogin(LoginStatusListener listener) {
		LoginModel model = this.loadAutoLoginUserData();

		if (model != null) {
			this.reset();
			listener.onLoginSuccess(null, -1);
			return true;
		} else {
		}
		return false;
	}

	/**
	 * 获取登录信息，用于票失效
	 * 
	 * @param account
	 * @param pwd
	 * @param listener
	 */
	public void getLoginInfo(String account, String pwd,
			LoginStatusListener listener) {
		HttpMasService.getInstance().getLoginInfo(
				new LoginResponse(account, pwd, listener));
	}

	/**
	 * 注销及跳转
	 */
	public void logout() {
		mLoginInfo.mUserId = null;
		mLoginInfo.mIsLogin = false;
		mLoginInfo.mAutoLogin = 0;
		mLoginInfo.mLastLogin = 1;
		this.updateAccountInfoDB(mLoginInfo);
		if (LocalBinder.getInstance().isContainBinder()) {
			try {
				LocalBinder.getInstance().obtainBinder().onLogout();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		clean();
		this.notifyLogout();

		// HttpMasService.getInstance().logout(null, mLoginInfo.mSessionKey);
	}

	/**
	 * 跳转到登录界面
	 * 
	 * @param context
	 */
	public void gotoLogin(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, WelcomeActivity.class);
		context.startActivity(intent);
	}

	public void clean() {
		mLoginInfo = new LoginInfo();
	}

	public void notifyLogout() {
		for (LoginListener listener : mLoginListeners) {
			listener.onLogout();
		}
	}

	public void notifyLogin() {
		if (mLoginInfo == null && mLoginInfo.mSecretKey != null) {
			return;
		}
		for (LoginListener listener : mLoginListeners) {
			listener.onLogin(mLoginInfo);
		}
	}

	public void registorLoginListener(LoginListener listener) {
		if (listener == null || mLoginListeners.contains(listener)) {
			return;
		} else {
			mLoginListeners.add(listener);
			if (mLoginInfo != null && mLoginInfo.mSecretKey != null) {
				listener.onLogin(mLoginInfo);
			}
		}
	}

	public static interface LoginListener {
		public void onLogin(LoginInfo info);

		public void onLogout();
	}

	/**
	 * 处理登陆结果的接口
	 * 
	 * @author he.cao
	 */
	public interface LoginStatusListener {
		public void onLoginResponse(JSONObject data); // 登陆请求返回

		public void onLoginSuccess(JSONObject data, long session); // 登陆成功

		public void onLoginFailed(JSONObject data, long session); // 登陆失败
	}

	// // 观察者
	// public interface BindInfoObserver {
	// public void update();
	// }

	public ArrayList<String> getAllAccountDao() {
		if (mAccountDao == null) {
			return null;
		}
		return mAccountDao.getAllAccounts();
	}

	public static class LoginInfo extends AbstractCloneable<LoginInfo> {
		public String mAccount = null;
		public String mPassword = null;
		public String mSessionKey = null;
		public String mSecretKey = null;//
		public String mHeadUrl = null;
		public String mUserId = null;
		public String mUserName = null;
		public String mTicket = null;
		public String mLargeUrl = null;
		public String mMediumUrl = null;
		public String mOriginal_Url = null;

		public int mAutoLogin = 1;
		public int mLastLogin = 0;

		public int mGender = -1;
		public String mBirthdayDisPlay = null;
		public String mBirthday = null;

		public String mSchool_id;
		public String mSchool = null;
		public String mDepartment = null;
		public int mDepartmentid = 0;
		public String mEnrollyear = null;
		public String mEmployer = null;
		public int mPrivate = -1;
		public String mDomainName = null;

		// 未存数据库
		public boolean mIsLogin = false;
		public String mPasswordToken = null;
		public String mLastAccount;

		public String mCoverUrl;

		// 登录验证码相关
		public int mCaptchaNeeded = 0; // 1：支持验证码
		public String mCaptchaUrl = "";

		/**
		 * 是否是第一次登陆 用户是否首次登录。server根据登录次数进行判断。0:首次登录。
		 */
		public int mIsFirstLogin = -1;
		/**
		 * 是否设置密码:
		 * <p>
		 * 1：无密码 0：有密码
		 */
		public int mFillStage = -1;

	}

	public LoginInfo getLoginInfo() {
		if (mLoginInfo == null) {
			this.reset();
		}
		return mLoginInfo;
	}
	
	public void getLoginInfo(LoginStatusListener listener) {
		HttpMasService.getInstance().getLoginInfo(new LoginResponse(listener));
	}

	public String getSessionKey() {
		if ((mLoginInfo == null || TextUtils.isEmpty(mLoginInfo.mSessionKey))) {
			this.reset();
		}
		if (mLoginInfo != null) {
			return mLoginInfo.mSessionKey;
		}
		return null;
	}

	public String getSecretKey() {
		if ((mLoginInfo == null || TextUtils.isEmpty(mLoginInfo.mSecretKey))) {
			this.reset();
		}
		if (mLoginInfo != null) {
			return mLoginInfo.mSecretKey;
		}
		return null;
	}

	public void reset() {
		LoginModel model = LoginManager.getInstance().mAccountDao
				.loadLastLoginUserData();
		if (model != null) {
			this.mLoginInfo = model.getLoginfo();
		}
	}

	public void updateAccountInfoDB(LoginInfo info) {
		LoginModel model = new LoginModel();
		model.parse(info);
		mAccountDao.updateAccountInfoDB(model);
	}

	/**
	 * @author dingwei.chen 说明 读取上一个登录用户信息
	 * */
	public LoginModel loadAutoLoginUserData() {
		if (LoginManager.getInstance().mAccountDao == null) {
			return null;
		}
		LoginModel tmp = LoginManager.getInstance().mAccountDao
				.loadAutoLoginUserData();
		if (tmp != null) {
			LoginManager.getInstance().initLoginInfo(tmp.getLoginfo());
		} else {
			CommonUtil.log("login",
					"loadAutoLoginUserData tem LoginfoModel is null");
		}
		return tmp;
	}

	/**
	 * @author dingwei.chen 说明 读取上一个登录用户信息
	 * */
	public LoginModel loadPreUserData() {
		if (mAccountDao == null) {
			return null;
		}
		LoginModel tmp = mAccountDao.loadPreUserData();
		if (tmp != null) {
			LoginManager.getInstance().initLoginInfo(tmp.getLoginfo());
		}
		return tmp;
	}

	public LoginModel loadLastLoginUserData() {
		if (mAccountDao == null) {
			return null;
		}
		LoginModel tmp = mAccountDao.loadLastLoginUserData();
		return tmp;
	}

	public boolean isLogout() {
		if ((mLoginInfo == null || TextUtils.isEmpty(mLoginInfo.mSessionKey))) {
			this.reset();
		}
		return mLoginInfo == null || TextUtils.isEmpty(mLoginInfo.mSessionKey)
				|| TextUtils.isEmpty(mLoginInfo.mSecretKey);
	}

	public void initLoginInfo(LoginInfo li) {
		mLoginInfo = li.clone();
		this.notifyLogin();
	}

	public LoginModel parseLoginInfo(JSONObject object) {
		CommonUtil.log("tag", object.toString());
		
		LoginModel tmpInfo = new LoginModel();

		tmpInfo.mIsFirstLogin = object.optInt("is_first_login");
		tmpInfo.mSessionKey = object.optString("session_key");
		tmpInfo.mSecretKey = object.optString("secret_key");

		JSONObject profileInfo = object.optJSONObject("profile_info");
		if (profileInfo != null) {
			 
			tmpInfo.mUserId = profileInfo.optString("user_id");
			tmpInfo.mUserName = profileInfo.optString("name");
			tmpInfo.mGender = profileInfo.optInt("gender");
			tmpInfo.mDomainName = profileInfo.optString("domain_name");
			tmpInfo.mCoverUrl = profileInfo.optString("cover_url");
			if (TextUtils.isEmpty(tmpInfo.mDomainName))
				tmpInfo.mDomainName = DomainUrl.SIXIN_DOMAIN;

			// 生日
			JSONObject birthDay = profileInfo.optJSONObject("birth_day");
			tmpInfo.mBirthday = profileInfo.optString("birth_display");
			if (TextUtils.isEmpty(tmpInfo.mBirthday) && birthDay != null) {
				int year = birthDay.optInt("year");
				int month = birthDay.optInt("month");
				int day = birthDay.optInt("day");
				tmpInfo.mBirthday = new StringBuilder().append(year)
						.append("-").append(month).append("-").append(day)
						.toString();
			}
			// 头像
			JSONObject profileImage = profileInfo
					.optJSONObject("profile_image");
			if (profileImage != null) {
				tmpInfo.mLargeUrl = profileImage.optString("large_url");
				tmpInfo.mMediumUrl = profileImage.optString("medium_url");
				tmpInfo.mOriginal_Url = profileImage.optString("original_url");
			}
			// 学校 因为服务端的数据出现过如下情况，所以多加判断
			//[{"name":"东北大学"},{"enroll_year":2008,"id":5002,"department_name":"软件学院","department_id":2557,"name":"东北大学"}]
			JSONArray schools = profileInfo.optJSONArray("school");
			if (schools != null && schools.length() > 0) {
				int length = schools.length();
				if(length == 1) {
					JSONObject school = schools.optJSONObject(0);
					tmpInfo.mSchool_id = school.optString("id");
//					if(TextUtils.isEmpty(tmpInfo.mSchool_id))
//						tmpInfo.mSchool_id = "5002";
					tmpInfo.mSchool = school.optString("name");
					tmpInfo.mDepartmentid = school.optInt("department_id");
					tmpInfo.mEnrollyear = school.optString("enroll_year");
					tmpInfo.mDepartment = school.optString("department_name");
				} else {
					for (int i = 0 ; i < length; i ++) {
						JSONObject school = schools.optJSONObject(i);
						if (school.has("id")){
							tmpInfo.mSchool_id = school.optString("id");
//							if(TextUtils.isEmpty(tmpInfo.mSchool_id))
//								tmpInfo.mSchool_id = "5002";
							tmpInfo.mSchool = school.optString("name");
							tmpInfo.mDepartmentid = school.optInt("department_id");
							tmpInfo.mEnrollyear = school.optString("enroll_year");
							tmpInfo.mDepartment = school.optString("department_name");
							break;
						}
					}
				}
			}
			// 工作
			JSONArray employers = profileInfo.optJSONArray("employer");
			if (employers != null && employers.length() > 0) {
				JSONObject employer = employers.optJSONObject(0);
				tmpInfo.mEmployer = employer.optString("name");
			}
		}

		if (object.has("bind_info")) {
			JSONArray bindInfo = object.optJSONArray("bind_info");
			for (int i = 0; i < bindInfo.length(); i++) {
				JSONObject object2 = bindInfo.optJSONObject(i);
				if (object2.optString("type").equals("email")) {
					tmpInfo.mAccount = object2.optString("bind_id");
					break;
				}
			}
			if (tmpInfo.mAccount == null) {
				for (int i = 0; i < bindInfo.length(); ++i) {
					JSONObject object2 = bindInfo.optJSONObject(i);
					if (object2.optString("type").equals("mobile")) {
						tmpInfo.mAccount = object2.optString("bind_id");
					}
				}
			}
		}
		return tmpInfo;
	}
}

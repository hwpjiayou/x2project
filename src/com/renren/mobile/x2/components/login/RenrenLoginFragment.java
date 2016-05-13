package com.renren.mobile.x2.components.login;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.home.HomeActivity;
import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.components.login.LoginManager.LoginStatusListener;
import com.renren.mobile.x2.network.talk.MessageManager;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.RRSharedPreferences;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;

public class RenrenLoginFragment extends BaseFragment implements
		LoginStatusListener {

	private View mView;
	private Activity mActivity;

	public LinearLayout mAuthorizeLayout;

	public Button mRenrenLoginButton;

	public Button jumpLoginButton;
	public TextView mRenrenUsername;

	private ImageLoader mImageLoader;
	private boolean isNeedChaptcha = false;

	private long mCurrentSession = System.currentTimeMillis();

	private ProgressDialog mDialog;

	private String mRenrenUserName; 

	private boolean isHaveRenren = false;
	
	private int LOGIN_RESULT = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mActivity = getActivity();
		mView = View.inflate(mActivity, R.layout.login_renren, null);

		return mView;
	}

	@Override
	protected void onPreLoad() {
		mRenrenLoginButton = (Button) mView.findViewById(R.id.login_authorize);
		jumpLoginButton = (Button) mView.findViewById(R.id.login_other);
		mRenrenUsername = (TextView) mView.findViewById(R.id.user_info);

		mImageLoader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD,
				mActivity);

		checkRenrenAccount();
		
		if (!TextUtils.isEmpty(mRenrenUserName)) {
			mRenrenLoginButton.setText(mActivity.getResources().getString(R.string.login_renren_notice_2) + mRenrenUserName);
			isHaveRenren = true;
		} else {
			mRenrenLoginButton.setText(mActivity.getResources().getString(R.string.login_renren_notice_1));
			isHaveRenren = false;
		}

		initViews();
		getTitleBar().hide();
		LoginManager.getInstance().autoLogin(this);

	}

	private void initViews() {
		mDialog = new ProgressDialog(mActivity);
		mDialog.setMessage(mActivity.getResources().getString(R.string.login_loading));
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				mCurrentSession = 0l;
			}
		});

		jumpLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, LoginActivity.class);
				startActivityForResult(intent, LOGIN_RESULT);
//				mActivity.finish();
			}
		});

		mRenrenLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isHaveRenren) {
					showDialog();

					LoginManager
							.getInstance()
							.loginX2(
									LoginManager.LOGIN_RENREN,
									LoginManager.getInstance().getLoginInfo().mAccount,
									LoginManager.getInstance().getLoginInfo().mPassword,
									"", mCurrentSession,
									RenrenLoginFragment.this);
				} else {
//					LoginActivity.show(mActivity);
					Intent intent = new Intent(mActivity, LoginActivity.class);
					startActivityForResult(intent,LOGIN_RESULT);
//					mActivity.finish();
				}

			}
		});
	}

	private boolean checkRenrenAccount() {
		LoginModel loginInfo = LoginManager.getInstance().loadPreUserData();

		if (loginInfo != null) {
			if (!LoginManager.getInstance().isLogout()) {
				return false;
			} else {
//				startActivity(mActivity, HomeActivity.class);
			}
		} else {
			RRSharedPreferences rrSharedPreferences = new RRSharedPreferences(
					mActivity, "login_authorize");
			// 两种登陆
			if (rrSharedPreferences.getIntValue("renren_authorize", 0) == 1) {
				// 检测到已登陆的官方客户端帐号，通过授权页面可直接登陆 == 1
				mRenrenUserName = LoginManager.getInstance().getLoginInfo().mUserName;

				if (!TextUtils.isEmpty(mRenrenUserName)) {
					mRenrenUsername.setText("欢迎你，" + mRenrenUserName);
					return true;
				}

			} else {
				return false;
			}
		}
		return false;

	}

	private void showDialog() {
		if (!mDialog.isShowing()) {
			mDialog.show();
		}
	}

	private void dismissDialog() {
		if (mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	public void onLoginResponse(JSONObject data) {

	} 
	
	LoginInfo loginInfo;
	@Override
	public void onLoginSuccess(JSONObject data, long session) {
		dismissDialog();
		loginInfo = LoginManager.getInstance().getLoginInfo();
		CommonUtil.log("tag", "isFirstLogin = " + loginInfo.mIsFirstLogin);
		
		// 这里应该改成0，测试时为1
		if (loginInfo.mIsFirstLogin == 0) {
			 new Thread(new Runnable() {
	                @Override
	                public void run() {
	                	loginInfo.mIsFirstLogin = 1;
	                	LoginManager.getInstance().updateAccountInfoDB(loginInfo);
	                }
	            }).start();
			
			startActivity(new Intent(mActivity, SynInfoActivity.class));
			
		} else {
			startActivity(new Intent(mActivity, HomeActivity.class));
//			startActivity(new Intent(mActivity, SynInfoActivity.class));
		}

		LoginManager.getInstance().getLoginInfo().mCaptchaNeeded = 0; // /不知道验证码策略是什么，以后得进行改进
		LoginManager.getInstance().getLoginInfo().mIsLogin = true;
		MessageManager.startService();
		isNeedChaptcha = false;

		mActivity.finish();
	}

	@Override
	public void onLoginFailed(JSONObject data, long session) {
		dismissDialog();

		try {
			if (data.getLong("error_code") == 10003) {// 需要验证码
				String captcha_url = data.getString("error_msg");
				LoginManager.getInstance().getLoginInfo().mCaptchaUrl = captcha_url;
				LoginManager.getInstance().getLoginInfo().mCaptchaNeeded = 1;
				isNeedChaptcha = true;// 需要验证码
				mImageLoader.get(new ImageLoader.HttpImageRequest(captcha_url,
						true), new ImageLoader.UiResponse() {

					@Override
					public void failed() {

					}

					@Override
					public void uiSuccess(Bitmap bitmap) {

					}
				});
			} else if (data.getLong("error_code") == 10001) {
				CommonUtil.toast(R.string.login_account_not_exist);
			} else if (data.getLong("error_code") == 10002) {
				CommonUtil.toast(R.string.login_password_error);
			} else if (data.getLong("error_code") == 10003) {
				CommonUtil.toast(R.string.login_account_useless);
			}

			CommonUtil.toast(R.string.login_failed);
		} catch (Exception e) {
			CommonUtil.toast(e.getLocalizedMessage());
			e.printStackTrace();
		}

		ErrLog.Print("LoginFailed " + data.toString());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == LOGIN_RESULT){
			mActivity.finish();
		}
	}

	@Override
	protected void onFinishLoad(Object data) {

	}

	@Override
	protected Object onLoadInBackground() {
		return null;
	}

	@Override
	protected void onDestroyData() {

	}
	

}

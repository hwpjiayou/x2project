package com.renren.mobile.x2.components.login;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.home.HomeActivity;
import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.components.login.LoginManager.LoginStatusListener;
import com.renren.mobile.x2.network.talk.MessageManager;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Md5;
import com.renren.mobile.x2.utils.RRSharedPreferences;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.log.Logger;

public class LoginFragment extends BaseFragment implements LoginStatusListener {

	private View mView;
	private Activity mActivity;

	public FrameLayout mLoginLayout;
	public ScrollView mScrollView;

	public AutoCompleteTextView mUserName;
	public ImageView mUserNameDeleIcon;
	public EditText mPassword;
	public ImageView mPasswordDeleIcon;

	public LinearLayout mCaptchaLayout;
	public EditText mCaptchaEditText;
	public ImageView mCaptchaImage;

	public Button loginButton; 

	public TextView mRenrenUsername;

	private ImageLoader mImageLoader;
	private boolean isNeedChaptcha = false;

	private long mCurrentSession = System.currentTimeMillis();

	private int CAPTCHA_NEEDED;

	private ProgressDialog mDialog; 

	private ArrayList<String> emailEndStr = new ArrayList<String>();
	private ArrayList<String> adapterStr = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mActivity = (LoginActivity) getActivity();
		mView = View.inflate(mActivity, R.layout.login, null);

		return mView;
	}

	@Override
	protected void onPreLoad() {
		mLoginLayout = (FrameLayout) mView
				.findViewById(R.id.normal_login_layout);
		mScrollView = (ScrollView) mView
				.findViewById(R.id.login_input_scrollview);

		mUserName = (AutoCompleteTextView) mView.findViewById(R.id.et_username);
		
		mUserNameDeleIcon = (ImageView) mView
				.findViewById(R.id.iv_username_del_icon);
		mPassword = (EditText) mView.findViewById(R.id.et_password);
		mPasswordDeleIcon = (ImageView) mView
				.findViewById(R.id.iv_password_del_icon);

		mCaptchaLayout = (LinearLayout) mView.findViewById(R.id.captcha);
		mCaptchaEditText = (EditText) mView.findViewById(R.id.et_captcha);
		mCaptchaImage = (ImageView) mView.findViewById(R.id.img_captcha);

		loginButton = (Button) mView.findViewById(R.id.btn_login);  

		mRenrenUsername = (TextView) mView.findViewById(R.id.user_info);

		mImageLoader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD,
				mActivity);

		loadLastLoginUser(); 

		initViews();
		getTitleBar().hide();
		
		LoginManager.getInstance().autoLogin(this);

	}

	/**
	 * 读取上次登录的用户，并设置到输入框中
	 */
	private void loadLastLoginUser() {
		LoginModel model = LoginManager.getInstance().loadLastLoginUserData();
		if (model != null && !TextUtils.isEmpty(model.mAccount)) {
			mUserName.setText(model.mAccount);
		}

	}

	private void refreshCaptcha() {
		if (LoginManager.getInstance().getLoginInfo().mCaptchaNeeded == CAPTCHA_NEEDED) {
			// mCaptchaLayout.setVisibility(View.VISIBLE);
			// setImageView(mCaptchaImage,
			// LoginManager.getInstance().getLoginInfo().mCaptchaUrl);
		}
	}

	private void initViews() {
		adapterStr = LoginManager.getInstance().getAllAccountDao();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
				R.layout.auto_complete_textview, adapterStr);
		mUserName.setAdapter(adapter);
		resetEmialEnd();

		refreshCaptcha();

		mDialog = new ProgressDialog(mActivity);
		mDialog.setMessage(mActivity.getResources().getString(R.string.login_loading));
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				mCurrentSession = 0l;
			}
		});

		// 设置验证码点击事件
		mCaptchaImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshCaptcha();
				mCaptchaImage.setVisibility(View.INVISIBLE);
			}
		});

		mUserName.addTextChangedListener(new AccountWatcher());
		mUserName.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mPassword.requestFocus();
			}
		});
		mPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(mPassword.getText().toString())) {
					mPasswordDeleIcon.setVisibility(View.GONE);
				} else {
					mPasswordDeleIcon.setVisibility(View.VISIBLE);
				}
			}
		});
		mUserNameDeleIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mUserName.setText("");
				mPassword.setText("");
			}
		});
		mPasswordDeleIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPassword.setText("");
			}
		});

		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (TextUtils.isEmpty(mUserName.getText().toString().trim())) {
//					CommonUtil.toast(R.string.login_account_null);
//				} else if (TextUtils.isEmpty(mPassword.getText().toString()
//						.trim())) {
//					CommonUtil.toast(R.string.login_password_null);
//				} else {
//					login();
//					hideKeyboard();
//				}

				startActivity(new Intent(mActivity, HomeActivity.class));
			}
		});
 
	}

	private void resetEmialEnd() {
		emailEndStr.clear();

		emailEndStr.add("qq.com");
		emailEndStr.add("163.com");
		emailEndStr.add("126.com");
		emailEndStr.add("sina.com");
		emailEndStr.add("hotmail.com");
		emailEndStr.add("yahoo.cn");
		emailEndStr.add("yahoo.com.cn");
		emailEndStr.add("sohu.com");
		emailEndStr.add("yeah.net");
		emailEndStr.add("tom.com");
		emailEndStr.add("gmail.com");
		emailEndStr.add("yahoo.com");
		emailEndStr.add("live.cn");
		emailEndStr.add("21cn.com");
		emailEndStr.add("xnmsn.com");
		emailEndStr.add("139.com");
		emailEndStr.add("vip.qq.com");
		emailEndStr.add("msn.com");
		emailEndStr.add("sina.com.cn");
		emailEndStr.add("foxmail.com");
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

	private void login() {

		if (isNeedChaptcha) {
			if (mCaptchaEditText.getText().toString().trim().length() == 0) {

			}
			showDialog();
			LoginManager.getInstance().loginX2(LoginManager.LOGIN_RENREN,
					mUserName.getText().toString(),
					Md5.toMD5(mPassword.getText().toString()),
					mCaptchaEditText.getText().toString().trim(),
					mCurrentSession, this);
		} else {
			showDialog();
			LoginManager.getInstance().loginX2(LoginManager.LOGIN_RENREN,
					mUserName.getText().toString(),
					Md5.toMD5(mPassword.getText().toString()), "",
					mCurrentSession, this);
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
		Logger.l();
		Log.d("zxc", "failed " + data.toString());
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
						mCaptchaImage.setVisibility(View.VISIBLE);
						mCaptchaEditText.setVisibility(View.VISIBLE);
						mCaptchaImage.setImageBitmap(bitmap);
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

	}

	private class _TextWatch implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				cleanPWD();
			}
		}

	}

	private void cleanPWD() {
		mPassword.setText("");
	}

	class AccountWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mCaptchaLayout.setVisibility(View.GONE);
			if (TextUtils.isEmpty(mUserName.getText().toString())) {
				mPassword.setText("");
				mUserNameDeleIcon.setVisibility(View.GONE);
			} else {
				mUserNameDeleIcon.setVisibility(View.VISIBLE);
			}

			ArrayList<String> list = LoginManager.getInstance()
					.getAllAccountDao();
			if (s.toString().trim().endsWith("@")) {
				resetEmialEnd();
				for (int i = 0; i < emailEndStr.size(); i++) {
					String str = s.toString().trim() + emailEndStr.get(i);
					CommonUtil.log("accountWatcher", "new str is " + str);
					if (!list.contains(str)) {
						list.add(str);
					}
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
						R.layout.auto_complete_textview, list);
				mUserName.setAdapter(adapter);

			}else if(!s.toString().trim().contains("@")){ 
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
						R.layout.auto_complete_textview, list);
				mUserName.setAdapter(adapter);
			}
			

		}
	}
	
	/**
	 * 隐藏输入法
	 */
	private void hideKeyboard() {
		((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(mActivity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

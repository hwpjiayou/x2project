package com.renren.mobile.x2.components.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;

public class LoginActivity extends BaseFragmentActivity<DATA>{
	
	
	@Override
	protected BaseFragment onCreateContentFragment() {
		LoginFragment loginFragment = new LoginFragment();
		return loginFragment;
	}

	@Override
	protected void onPreLoad() {
		
	}

	@Override
	protected void onFinishLoad(DATA data) {
		
	}

	@Override
	protected DATA onLoadInBackground() {
		return null;
	}

	@Override
	protected void onDestroyData() {
		
	}

}

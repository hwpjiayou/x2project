package com.renren.mobile.x2.components.login;

import android.view.WindowManager;

import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;

public class UserGuideActivity extends BaseFragmentActivity {

	@Override
	protected BaseFragment onCreateContentFragment() {
		UserGuideFragment userGuideFragment= new UserGuideFragment();
		this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return userGuideFragment;
	}

	@Override
	protected void onPreLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onFinishLoad(Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Object onLoadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onDestroyData() {
		// TODO Auto-generated method stub
		
	}

}

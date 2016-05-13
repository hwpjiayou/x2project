package com.renren.mobile.x2.components.login;

import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;

public class RenrenLoginActivity extends BaseFragmentActivity<DATA>{

	@Override
	protected BaseFragment onCreateContentFragment() {
		RenrenLoginFragment renrenLoginFragment = new RenrenLoginFragment();
		return renrenLoginFragment;
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

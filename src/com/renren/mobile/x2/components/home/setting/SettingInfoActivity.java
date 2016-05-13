package com.renren.mobile.x2.components.home.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;

public class SettingInfoActivity extends BaseFragmentActivity<Object> {
	
	public static final int NEWS=1;
	public static final int PRIVACY=2;
	public static final int ABOUT=3;
	public static final int BLACK=4;

	@Override
	protected BaseFragment onCreateContentFragment() {
		Bundle b = getIntent().getExtras();
		int flag = b.getInt("flag");
		if (flag == SettingInfoActivity.NEWS) {
			return new SettingNewsFragment();
		} else if (flag == SettingInfoActivity.ABOUT) {
			return new SettingAboutFragment();
		} else if (flag == SettingInfoActivity.PRIVACY) {
			return new SettingPrivacyFragment();
		}else if (flag == SettingInfoActivity.BLACK) {
			return new SettingBlackFragment();
		}
		return null;
	}

	@Override
	protected void onPreLoad() {

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

	public static void show(Context context, int flag) {
		Bundle b = new Bundle();
		b.putInt("flag", flag);
		Intent intent = new Intent(context, SettingInfoActivity.class);
		intent.putExtras(b);
		context.startActivity(intent);
	}

}

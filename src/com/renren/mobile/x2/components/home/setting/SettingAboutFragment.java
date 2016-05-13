package com.renren.mobile.x2.components.home.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;

public class SettingAboutFragment extends BaseFragment<Object>{
	
	private View mView;
	private Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mActivity=(SettingInfoActivity) getActivity();
		initView();
		getTitleBar().setTitle(mActivity.getText(R.string.setting_about));
		return mView;
	}

	private void initView() {
		mView = View.inflate(mActivity, R.layout.setting_about_main_layout, null);
		initEvent();
	}
	
	private void initEvent() {
		
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

}

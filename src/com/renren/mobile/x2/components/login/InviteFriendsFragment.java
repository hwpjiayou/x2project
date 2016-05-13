package com.renren.mobile.x2.components.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.view.ITitleBar;

public class InviteFriendsFragment extends BaseFragment {
 
	private Activity mActivity;
	private View mView;
	
	@Override
	protected ITitleBar getTitleBar() {
	     
		return super.getTitleBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = getActivity();
		mView = inflater.inflate(R.layout.login_invite_friends, null);
		
		return mView;
	}

	@Override
	protected void onPreLoad() {
		getTitleBar().setTitle(mActivity.getResources().getString(R.string.login_invite_friends_title));
		
		
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

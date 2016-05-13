package com.renren.mobile.x2.components.home.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;

public class ProfileActivity extends BaseFragmentActivity<ProfileDataModel>{
   private static String mUserId;
	@Override
	protected BaseFragment onCreateContentFragment() {
		ProfileFragment view = new ProfileFragment(mUserId);
		return view;
	}
	public static void show(Context context ,String userId){
		Intent intent = new Intent(context,ProfileActivity.class);
		mUserId = userId;
		intent.putExtra(ProfileView.USER_ID, userId );
		context.startActivity(intent);
	}

	@Override
	protected void onPreLoad() {
	}

	@Override
	protected void onDestroyData() {
		
	}

	@Override
	protected void onFinishLoad(ProfileDataModel data) {
		
	}

	@Override
	protected ProfileDataModel onLoadInBackground() {
		return null;
	}

}

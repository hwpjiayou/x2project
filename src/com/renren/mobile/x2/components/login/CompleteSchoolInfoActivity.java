package com.renren.mobile.x2.components.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;

import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;
import com.renren.mobile.x2.utils.CommonUtil;

/**
 * 完善学校院系信息和入学年份
 * @author shichao.song
 *
 */ 
public class CompleteSchoolInfoActivity extends BaseFragmentActivity<DATA> {
	
	public static void show(Context context, String schoolId, String schoolName){
		Intent intent = new Intent(context, CompleteSchoolInfoActivity.class);
		intent.putExtra("school_id", schoolId);
		intent.putExtra("school_name", schoolName);
		CommonUtil.log("tag", "CompleteSchoolInfoActivity " + schoolId + schoolName);
		context.startActivity(intent);
	}
  
	@Override
	protected BaseFragment onCreateContentFragment() { 
		Bundle mBundle = getIntent().getExtras();
		CompleteSchoolInfoFragment completeSchInfoFra = new CompleteSchoolInfoFragment();
		completeSchInfoFra.setArguments(mBundle);
		return completeSchInfoFra;
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

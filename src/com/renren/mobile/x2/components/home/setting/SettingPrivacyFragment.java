package com.renren.mobile.x2.components.home.setting;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.RRSharedPreferences;

public class SettingPrivacyFragment extends
		BaseFragment<Object> {
	public static final String ISLOCATION="isLocation";
	public static final String LOCATION="true";
	public static final String LOCATIOFF="false";
	private View mView;
	private Activity mActivity;
	private FrameLayout framePrivacy;
	private TextView txtPrivacy;
	private ImageView imgPrivacy;
	private RRSharedPreferences mRRs;
	private String getmRRs; 
	private boolean isNetWork;
	private boolean isOn=false;
	private String on;
	private String off;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mActivity = (SettingInfoActivity) getActivity();
		on=this.getString(R.string.setting_toggle_on);
		off=this.getString(R.string.setting_toggle_off);
		mRRs = new RRSharedPreferences(mActivity, "x2_setting");
		getmRRs = mRRs.getStringValue(SettingPrivacyFragment.ISLOCATION, LOCATIOFF);
		initView();
		return mView;
	}

	private void initView() {
		mView = View.inflate(mActivity, R.layout.setting_privacy_main_layout, null);
		framePrivacy = (FrameLayout)mView.findViewById(R.id.settingPrivacy_frame_privacy);
		txtPrivacy = (TextView)mView.findViewById(R.id.settingPrivacy_txt_privacy);
		imgPrivacy = (ImageView)mView.findViewById(R.id.settingPrivacy_img_privacy);
		if(getmRRs.equals(LOCATION)){
			 isOn = true;
			 setSingleView(txtPrivacy, imgPrivacy, isOn);
		}
		getTitleBar().setTitle(mActivity.getText(R.string.setting_privacy));
		initEvent();
	}
	
		private void setSingleView(final View v, final View g, final boolean flag){
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				
				@Override
				public void run() {
					if(flag){
						((TextView)v).setText(on);
					}else {
						((TextView)v).setText(off);
					}
					g.setSelected(flag);
					
				}
			});
			
		}

	private void initEvent() {
		framePrivacy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isOn=!isOn;
				setSingleView(txtPrivacy, imgPrivacy, isOn);
				if(isOn){
					mRRs.putStringValue(ISLOCATION, LOCATION);
				}else {
					mRRs.putStringValue(ISLOCATION, LOCATIOFF);
				}
				
			}
		});
	}
	
	private void putData(){
		
	}
	
	private void getData(){
		
	}

	@Override
	protected void onPreLoad() {
		if(TextUtils.isEmpty(getmRRs)){
			//loading,从网络获取数据
			
		}else {
			
		}
//		setView();
		
	}

	@Override
	protected void onFinishLoad(Object data) {
//		setVeiw();
	}

	@Override
	protected Object onLoadInBackground() {
		
		return null;
	}

	@Override
	protected void onDestroyData() {

	}


}

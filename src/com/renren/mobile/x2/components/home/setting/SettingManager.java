package com.renren.mobile.x2.components.home.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.util.ContactModel;
import com.renren.mobile.x2.components.home.HomeFragment.OnActivityResultListener;
import com.renren.mobile.x2.components.home.HomeTab;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.voice.VoiceTestActivity;

import java.util.List;

public class SettingManager implements HomeTab {
	
	private Activity mActivity;
	private View mView;
	private FrameLayout news;
	private FrameLayout privacy;
	private FrameLayout share;
	private FrameLayout blacklist;
	private FrameLayout features;
	private FrameLayout about;
	private FrameLayout feedback;
	private FrameLayout evaluate;
	private FrameLayout clear;
	private FrameLayout logout;
	private FrameLayout test;
	
	public static final String packageName ="com.renren.mobile.android";
	
	@Override
	public int getNameResourceId() {
		return R.string.home_tab_setting;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.v1_home_menu_settings_selector;
	}

	@Override
	public View getView() {
		return mView;
	}

	@Override
	public View onCreateView(Context context) {
		mActivity=(Activity)context;
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.setting_main_layout, null);
		initView();
		initEvent();
		return mView;
	}

	private void initView() {
		news=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_news);
		privacy=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_privacy);
		share=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_shareToRenRen);
		blacklist=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_blacklist);
		features=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_features);
		about=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_about);
		feedback=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_feedback);
		evaluate=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_evaluate);
		clear=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_clear);
		logout=(FrameLayout)mView.findViewById(R.id.setting_frameLayout_logout);
		test =(FrameLayout)mView.findViewById(R.id.test);
	}

	private void initEvent() {
		
		test.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, VoiceTestActivity.class);
				mActivity.startActivity(intent);
			}
		});
		news.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingInfoActivity.show(mActivity, SettingInfoActivity.NEWS);
			}
		});
		
		privacy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingInfoActivity.show(mActivity, SettingInfoActivity.PRIVACY);
			}
		});
		
		share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		blacklist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingInfoActivity.show(mActivity, SettingInfoActivity.BLACK);
			}
		});
		
		features.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent =new Intent(mActivity, WelcomeActivity.class);
//				mActivity.startActivity(intent);
			}
		});
		
		about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingInfoActivity.show(mActivity, SettingInfoActivity.ABOUT);
			}
		});
		
		feedback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 String mName = "x2";
				 long 	mUid = 111111L;
				 String mUrl = "http://hdn.xnimg.cn/photos/hdn121/20120921/1505/h_main_5Twe_43b60000473c1376.jpg";
				 ContactModel cc =new ContactModel(mName,mUid,mUrl);
				 RenRenChatActivity.show(mActivity, cc, true);
				
			}
		});
		
		
		evaluate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isMarketInstalled()){
					mActivity.startActivity(new Intent(Intent.ACTION_VIEW,
							Uri.parse("market://details?id="+packageName)));
				}else{
					CommonUtil.toast("！！！！！");
				}
			}
		});
		
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				  AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
	                builder.setTitle(mActivity.getText(R.string.setting_logout_title)); 
	                builder.setPositiveButton(mActivity.getText(R.string.ok),
	                        new DialogInterface.OnClickListener() {
	                            @Override
	                            public void onClick(DialogInterface dialog,int which) {
	                            	LoginManager.getInstance().logout();
	                            	LoginManager.getInstance().gotoLogin(mActivity);
	                            	mActivity.finish();
	                            	
	                            }
	                        });
	                builder.setNegativeButton(mActivity.getText(R.string.cancel),null); 
	                builder.create().show();
	            }
		});
		
	}

	@Override
	public void onLoadData() {

	}

	@Override
	public void onFinishLoad() {

	}

    @Override
    public void onResume() {
    }

    @Override
	public void onPause() {

	}

	@Override
	public void onDestroyData() {

	}
	
	private boolean isMarketInstalled() {
		Intent market = new Intent(Intent.ACTION_VIEW,Uri.parse("market://search?q=dummy"));
		PackageManager manager = mActivity.getPackageManager();
		List<ResolveInfo> list = manager.queryIntentActivities(market, 0);
		if (list.size() > 0)
			return true;
		else
			return false;
	}

	@Override
	public OnActivityResultListener onActivityResult() {
		// TODO Auto-generated method stub
		return null;
	}

}

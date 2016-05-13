package com.renren.mobile.x2.components.home.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.utils.RRSharedPreferences;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;

public class SettingNewsFragment extends BaseFragment<Object> {
	private View mView;
	private Activity mActivity;
	private RRSharedPreferences mRRS;
	private int flag;
	private NewsHolder mHolder;
	private boolean isSound = true;
	private boolean isVibrate = true;
	private boolean isBackground = true ;
	private String on;
	private String off;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mActivity = (SettingInfoActivity) getActivity();
		on=this.getString(R.string.setting_toggle_on);
		off=this.getString(R.string.setting_toggle_off);
		mHolder=new NewsHolder();
		mRRS = new RRSharedPreferences(mActivity);
		initView();
		return mView;
	}

	private void initView() {
		mView = View.inflate(mActivity, R.layout.setting_news_main_layout, null);
		ViewMapUtil.viewMapping(mHolder, mView);
		getTitleBar().setTitle(mActivity.getText(R.string.setting_news));
		initEvent();
	}

	private void initEvent() {
		mHolder.frameSound.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isSound=!isSound;
//				mHolder.txtSound.setText(String.valueOf(isSound));
				flag=isSound ? flag+1 : flag-1;
				setSingleView(mHolder.txtSound, mHolder.imgSound, isSound);
				mRRS.putIntValue(NewsInfo.SETTING_NEWS, flag);
			}
		});

		mHolder.frameVirate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isVibrate=!isVibrate;
//				mHolder.txtVibrate.setText(String.valueOf(isVibrate));
				flag=isVibrate ? flag+2 : flag-2;
				setSingleView(mHolder.txtVibrate, mHolder.imgVibrate, isVibrate);
				mRRS.putIntValue(NewsInfo.SETTING_NEWS, flag);
			}
		});
		
		mHolder.frameBackground.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isBackground=!isBackground;
				flag=isBackground ? flag+4 : flag-4;
				setSingleView(mHolder.txtBackground, mHolder.imgBackground, isBackground);
				mRRS.putIntValue(NewsInfo.SETTING_NEWS, flag);
			}
		});
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
	private void setView(int v) {
		if (v==NewsInfo.SOUND) {
			isBackground=false;
			isVibrate=false;
			mHolder.imgSound.setSelected(true);
			mHolder.txtVibrate.setText(off);
			mHolder.txtBackground.setText(off);
		} else if(v==NewsInfo.VIBRATE){
			isBackground=false;
			isSound=false;
			mHolder.imgVibrate.setSelected(true);
			mHolder.txtSound.setText(off);
			mHolder.txtBackground.setText(off);
		}else if(v==NewsInfo.SOUND_VIBRATE){
			mHolder.imgSound.setSelected(true);
			mHolder.imgVibrate.setSelected(true);
			isBackground=false;
			mHolder.txtBackground.setText(off);
		}else if(v==NewsInfo.NO_SOUND_VIBRATE){
			isSound=false;
			isVibrate=false;
			isBackground=false;
			mHolder.txtSound.setText(off);
			mHolder.txtVibrate.setText(off);
			mHolder.txtBackground.setText(off);
		}else if(v==NewsInfo.BACKGROUND){
			mHolder.imgBackground.setSelected(true);
			isSound=false;
			isVibrate=false;
			mHolder.txtSound.setText(off);
			mHolder.txtVibrate.setText(off);
		}else if(v==NewsInfo.SOUND_BACKGROUND){
			mHolder.imgSound.setSelected(true);
			mHolder.imgBackground.setSelected(true);
			isVibrate=false;
			mHolder.txtVibrate.setText(off);
		}else if(v==NewsInfo.VIBRATE_BACKGROUND){
			mHolder.imgVibrate.setSelected(true);
			mHolder.imgBackground.setSelected(true);
			isSound=false;
			mHolder.txtSound.setText(off);
		}else if(v==NewsInfo.SOUND_VIBRATE_BACKGROUND){
			mHolder.imgSound.setSelected(true);
			mHolder.imgVibrate.setSelected(true);
			mHolder.imgBackground.setSelected(true);
		}
	}

	@Override
	protected void onPreLoad() {

	}

	@Override
	protected void onFinishLoad(Object data) {
		setView(flag);
	}

	@Override
	protected Object onLoadInBackground() {
		flag = mRRS.getIntValue(NewsInfo.SETTING_NEWS, NewsInfo.SOUND_VIBRATE_BACKGROUND);
		return null;
	}

	@Override
	protected void onDestroyData() {

	}


	public interface NewsInfo {
		String SETTING_NEWS = "setting_news";
		int SOUND = 1;
		int VIBRATE = 2;
		int BACKGROUND = 4;
		int SOUND_VIBRATE = 3;
		int SOUND_BACKGROUND = 5;
		int VIBRATE_BACKGROUND = 6;
		int SOUND_VIBRATE_BACKGROUND = 7;
		int NO_SOUND_VIBRATE = 0;
		
	}

	public class NewsHolder {

		@ViewMapping(ID = R.id.settingNews_frame_sound)
		public FrameLayout frameSound;

		@ViewMapping(ID = R.id.settingNews_frame_vibrate)
		public FrameLayout frameVirate;
		
		@ViewMapping(ID = R.id.settingNews_frame_background)
		public FrameLayout frameBackground;

		@ViewMapping(ID = R.id.settingNews_txt_sound)
		public TextView txtSound;

		@ViewMapping(ID = R.id.settingNews_txt_vibrate)
		public TextView txtVibrate;
		
		@ViewMapping(ID = R.id.settingNews_txt_background)
		public TextView txtBackground;
		
		@ViewMapping(ID = R.id.settingNews_img_sound)
		public ImageView imgSound;

		@ViewMapping(ID = R.id.settingNews_img_vibrate)
		public ImageView imgVibrate;
		
		@ViewMapping(ID = R.id.settingNews_img_background)
		public ImageView imgBackground;
		
	}

}

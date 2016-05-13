package com.renren.mobile.x2.components.chat.util;

import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * @author dingwei.chen
 * @说明 弹出动画监听器
 * */
public class ToastAnimationListennerImpl implements AnimationListener{

	public View mView = null;
	public Animation mSwitchToastAnimation_Show = new AlphaAnimation(0.0f, 1.0f);
	public Animation mSwitchToastAnimation_Dismiss = new AlphaAnimation(1.0f, 0.0f);
	public static final long TOAST_EXIST_TIME = 1000L;
	
	public ToastAnimationListennerImpl(){
		mSwitchToastAnimation_Show.setDuration(TOAST_EXIST_TIME);
		mSwitchToastAnimation_Show.setFillAfter(true);
		mSwitchToastAnimation_Dismiss.setDuration(TOAST_EXIST_TIME);
		mSwitchToastAnimation_Show.setAnimationListener(this);
	}
	
	public void toast(View view){
		mView = view;
		mView.startAnimation(mSwitchToastAnimation_Show);
	}
	
	
	
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		new Handler().postDelayed(
				new Runnable() {
					
					@Override
					public void run() {
						if(mView!=null){
							mView.startAnimation(mSwitchToastAnimation_Dismiss);
						}
						mView = null;
					}
				}, 2000);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}

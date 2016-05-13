package com.renren.mobile.x2.components.chat.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;


/**
 * @author dingwei.chen
 * */
public class TranslateShowListenerImpl  implements AnimationListener {

	int mAnimation_Type = 0;
	public static final int SHOW_TYPE = 1;
	public static final int DISSMISS_TYPE = 2;
	View mView = null;
	public static final  TranslateAnimation DISSMISS = new TranslateAnimation(0,0,0,1000);
	public static final  TranslateAnimation SHOW = new TranslateAnimation(0,0,1000,0);
	public TranslateShowListenerImpl(){
		DISSMISS.setAnimationListener(this);
		DISSMISS.setDuration(300l);
		SHOW.setAnimationListener(this);
		SHOW.setDuration(300l);
	}
	
	
	public void show(View view){
		this.mAnimation_Type = SHOW_TYPE;
		mView = view;
		mView.setVisibility(View.VISIBLE);
		mView.startAnimation(SHOW);
	}
	
	public void dissmiss(View view){
		mAnimation_Type = DISSMISS_TYPE;
		mView = view;
		mView.startAnimation(DISSMISS);
	}
	
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(mView!=null){
			switch (mAnimation_Type) {
				case DISSMISS_TYPE:
					mView.setVisibility(View.GONE);
					break;
				case SHOW_TYPE:
					mView.setVisibility(View.VISIBLE);
					break;
			}
			mAnimation_Type = 0;
			mView = null;
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}

package com.renren.mobile.x2.components.comment;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class PathAnimation {

public static void startTranscationAnimationsClose(View view,int duration,float fromXValue,float toXValue,float fromYValue,float toYValue){
		
		TranslateAnimation animation;
		animation=new TranslateAnimation(Animation.ABSOLUTE, fromXValue, Animation.ABSOLUTE, toXValue, Animation.ABSOLUTE, fromYValue, Animation.ABSOLUTE, toYValue);
		animation.setFillAfter(true);
		animation.setDuration(duration);
		view.startAnimation(animation);
	}
	
	public static void startTranscationAnimationOpen(View view,int duration,float fromXValue,float toXValue,float fromYValue,float toYValue){
		TranslateAnimation animation;
		animation=new TranslateAnimation(Animation.ABSOLUTE, fromXValue, Animation.ABSOLUTE, toXValue, 
				                             Animation.ABSOLUTE, fromYValue, Animation.ABSOLUTE, toYValue);
	    animation.setFillAfter(true);
	    animation.setDuration(duration);
	    animation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
	    view.startAnimation(animation);
	}
}

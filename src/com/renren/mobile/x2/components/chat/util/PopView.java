package com.renren.mobile.x2.components.chat.util;

import com.renren.mobile.x2.R;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;


/**
 * @author dingwei.chen
 * @说明 弹出窗口
 * */
public class PopView extends PopupWindow implements AnimationListener{

	View mAnchor = null;
	View mRootView = null;
	Animation mScaleAnim = null;
	Animation mDismissAnim = null;
	WindowManager mWindowManager = null;
	TextView mMenu_ViewUserInfo = null;
//	TextView mMenu_SwitchMethod = null;
	public PopView(Context context,View anchor){
		super(context);
		mAnchor = anchor;
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		mRootView = inflater.inflate(R.layout.popview, null);
		this.setContentView(mRootView);
		this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mScaleAnim = new ScaleAnimation(
				0, 
				1.1f, 
				0, 
				1.1f,
				Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0f);
		mScaleAnim.setDuration(200);
		mDismissAnim = new ScaleAnimation(
				1, 
				0f, 
				1, 
				0f,
				Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0f);
		mDismissAnim.setDuration(150);
		mDismissAnim.setAnimationListener(this);
		
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));
		this.setOutsideTouchable(true);
		this.setFocusable(true);
		this.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					PopView.this.dismiss();
					return true;
				}
				return false;
			}
		});
		mMenu_ViewUserInfo = (TextView)mRootView.findViewById(R.id.popview_viewuserinfo);
//		mMenu_SwitchMethod= (TextView)mRootView.findViewById(R.id.popview_switchmethod);
//		initEvent();
	}
//	ItemSelectListenner mListenner = new ItemSelectListenner();
//	public void initEvent(){
//		mMenu_ViewUserInfo.setOnClickListener(mListenner);
////		mMenu_SwitchMethod.setOnClickListener(mListenner);
//	}
	
//	public class ItemSelectListenner implements OnClickListener{
//		public void onClick(View v) {
//			if(v==mMenu_ViewUserInfo){
//				if(mViewUserCallback!=null){
//					mViewUserCallback.onViewUserInfo();
//				}
//			}
////			}else if(v==mMenu_SwitchMethod){
////				if(mSwitchMethodCallback!=null){
////					mSwitchMethodCallback.onSwitchMethod();
////				}
////			}
//			PopView.this.dismiss();
//		}
//	}
//	OnViewUserInfoCallback mViewUserCallback = null;
//	public interface OnViewUserInfoCallback {
//		public void onViewUserInfo();
//	}
//	public void setViewUserCallback(OnViewUserInfoCallback callback){
//		mViewUserCallback = callback;
//	}
	
	
	/*弹出*/
	public void pop(){
		super.dismiss();
		int[] location 		= new int[2];
		
		mAnchor.getLocationOnScreen(location);

		Rect anchorRect 	= new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] 
		                	+ mAnchor.getHeight());
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int rootWidth 		= mRootView.getMeasuredWidth();
		int rootHeight 		= mRootView.getMeasuredHeight();

		int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight 	= mWindowManager.getDefaultDisplay().getHeight();
		int xPos 			= (location[0] - rootWidth)-anchorRect.width()*3/4;
		int yPos	 		= anchorRect.bottom;
		this.showAtLocation(this.mAnchor, Gravity.NO_GRAVITY, xPos, yPos);
		mRootView.startAnimation(mScaleAnim);
	}
	
	@Override
	public void dismiss() {
		this.mRootView.startAnimation(mDismissAnim);
//		super.dismiss();
	}


	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		super.dismiss();
	}


	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
}

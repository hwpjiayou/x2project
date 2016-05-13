package com.renren.mobile.x2.components.chat.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DipUtil;


/**
 * @author dingwei.chen
 * */
class InnerListView extends ListView {

	private float mStartY;
	private float mMotionY;
	private boolean mIsAddHeader = false;
	HeaderLinearLayout mHeader = null;
	public InnerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setCacheColorHint(0x00000000);
		mHeader = new HeaderLinearLayout(getContext());
		mHeader.setVisibility(View.GONE);
		this.addHeaderView(mHeader);
		setScrollingCacheEnabled(false);       
		setDrawingCacheEnabled(false);         
		setAlwaysDrawnWithCacheEnabled(false); 
		setWillNotCacheDrawing(true);    
	}

	private class HeaderLinearLayout extends LinearLayout{

		public HeaderLinearLayout(Context context) {
			super(context);
			this.setGravity(Gravity.CENTER);
		}
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			if(this.getVisibility()==View.GONE){
				this.setMeasuredDimension(0, 0);
			}
		}
	}
	
	public void addHeader(View view){
		if(!mIsAddHeader){
			mHeader.removeAllViews();
			mHeader.addView(view);
			mHeader.setVisibility(View.VISIBLE);
			view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mHeader.setPadding(DipUtil.calcFromDip(10), DipUtil.calcFromDip(12), DipUtil.calcFromDip(10),  DipUtil.calcFromDip(12));
			mIsAddHeader = true;
			this.layoutChildren();
		}
	}
	
	public void hideHeader(){
		mHeader.setVisibility(View.GONE);
		this.mIsAddHeader = false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		CommonUtil.log("listtouch", "listtouch"+ev.getAction());
		int action = ev.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
				mStartY = ev.getRawY();
				break;
		case MotionEvent.ACTION_MOVE:
			mMotionY = ev.getRawY();
			if(isDownDrag()){
				if(isTop()){
//					System.out.println("is top now");
					this.onTop();
				}
				mStartY = 0;
				mMotionY = 0;
			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	boolean isDownDrag(){
		return mStartY - mMotionY<0;
	}
	boolean isTop(){
		if(this.getChildCount()>0){
			View view = this.getChildAt(0);
			if(view!=null)
			return view.getTop()==this.getTop()&&this.getFirstVisiblePosition()==0;
		}
		return false;
	}
	boolean isBottom(){
		if(this.getChildCount()>0&&this.getLastVisiblePosition()==this.getCount()-1){
			View view = this.getChildAt(this.getChildCount()-1);
			if(view !=null ){
				return view.getBottom()==this.getBottom();
				
			}
		}
		CommonUtil.log("lock","ret again");
		return false;
	}
	OnPositionListener mListener = null;
	public void setOnPositionListener(OnPositionListener listener){
		mListener = listener;
	}
	public void onTop(){
		if(this.mListener!=null){
			this.mListener.onTop();
		}
	}
	public static interface OnPositionListener{
		public void onTop();
	}
}

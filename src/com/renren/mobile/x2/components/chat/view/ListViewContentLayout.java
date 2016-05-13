package com.renren.mobile.x2.components.chat.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DipUtil;

/**
 * @author dingwei.chen
 * */
public class ListViewContentLayout extends LinearLayout {
	
	int mAlpha = 0;
	public static Drawable FOREGROUND_DRAWABLE = null;//feed use
	public Drawable COVER = null;
	public Drawable COVER_RIGHT = null;
	public Drawable COVER_LEFT = null;
	public static final int SHOW_FEED_MASK = 0x04;
	public static final int SHOW_COVER_MASK = 0x01;
	public static final int SHOW_LEFT_COVER_MASK = 0x02;
	public int mState = 0x0;
	
	public void showFeed(){
		mState|=SHOW_FEED_MASK;
	}
	public void hideFeed(){
		mState&=~SHOW_FEED_MASK;
	}
	public boolean isShowFeed(){
		return (mState&SHOW_FEED_MASK)!=0;
	}
	
	public ListViewContentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		Drawable d=this.getBackground();
		
		if(FOREGROUND_DRAWABLE==null){
			this.setBackgroundResource(R.drawable.test_default);
			FOREGROUND_DRAWABLE = this.getBackground();
		}
		this.setBackgroundResource(R.drawable.test_default);
		COVER_RIGHT = this.getBackground();
		this.setBackgroundResource(R.drawable.test_default);
		COVER_LEFT = this.getBackground();
		this.setBackgroundDrawable(d);
		
	}
	OnTouchListener mListener = null;
	
	
	public void showCover(boolean isLeft){
		mState = mState|SHOW_COVER_MASK;
		if(isLeft){
			mState|=SHOW_LEFT_COVER_MASK;
		}else{
			mState &=~SHOW_LEFT_COVER_MASK;
		}
	}
	public void hideCover(){
		mState &=~	SHOW_COVER_MASK;
	}
	public boolean isLeft(){
		return (mState&SHOW_LEFT_COVER_MASK)>0;
	}
	
	public boolean isShowCover(){
		return (mState&SHOW_COVER_MASK)>0;
	}
	
	
	@Override
	public void setOnTouchListener(OnTouchListener l) {
		mListener = l;
	};
	float y=0;
	@Override
	public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
		if(ev.getAction()==MotionEvent.ACTION_DOWN){
			this.setPressed(true);
			y = ev.getY();
		}else if(ev.getAction()==MotionEvent.ACTION_UP){
			this.setPressed(false);
		}else if(ev.getAction()==MotionEvent.ACTION_MOVE){
			if((ev.getY()-y)<15){
				return true;
			}
		}
		boolean flag = super.dispatchTouchEvent(ev);
		return flag;
	};
	
	@Override
	public void setPressed(boolean pressed) {
		if(isShowCover()){
			COVER = isLeft()?COVER_LEFT:COVER_RIGHT;
		}
		super.setPressed(pressed);
	};
	
	@Override
	protected void dispatchSetPressed(boolean pressed) {
		if(isShowCover()){
			COVER.setState(this.getDrawableState());
		}
	};
	private static final int MIN_HEIGHT = DipUtil.calcFromDip(42);
	private static final int MIN_WIDTH = DipUtil.calcFromDip(50);
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
//		view.setTag(this.isShowFeed());
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if(MeasureSpec.getSize(heightMeasureSpec)<MIN_HEIGHT){
			height = MIN_HEIGHT;
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));
		}
		int width =  MeasureSpec.getSize(widthMeasureSpec);
		if(MeasureSpec.getSize(widthMeasureSpec)<MIN_WIDTH){
			width = MIN_WIDTH;
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec));
		}
		int maxWidth = DipUtil.getScreenWidth()-DipUtil.calcFromDip(130);
		if(MeasureSpec.getSize(widthMeasureSpec)>maxWidth){
			width = maxWidth;
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec));
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(this.getMeasuredWidth()<MIN_WIDTH){
			this.setMeasuredDimension(MIN_WIDTH, getMeasuredHeight());
		}
		if(this.getMeasuredHeight()<MIN_HEIGHT){
			this.setMeasuredDimension(getMeasuredWidth(), MIN_HEIGHT);
		}
		
		if(isShowFeed()&&hasOtherViews()){
			int minh = (MIN_HEIGHT)+this.getPaddingTop();
			
			if(this.getMeasuredHeight()<minh){
				this.setMeasuredDimension(getMeasuredWidth(), minh);
			}
		}
//		if(CommonUtil.mDebug){
//			CommonUtil.logd("view="+(view==null));
//			if(view!=null){
//				CommonUtil.logd("view vistviltty="+(view.getVisibility()==View.VISIBLE));
//			}
//		}
		View view = this.findViewById(R.id.chat_voice_voicemessage_linearlayout);
		if(view!=null && view.getVisibility()==View.VISIBLE){
			view.measure(MeasureSpec.AT_MOST|(width-this.getPaddingLeft()-this.getPaddingRight()), MIN_HEIGHT|MeasureSpec.AT_MOST);
			this.setMeasuredDimension(getMeasuredWidth(), MIN_HEIGHT);
		}
		View view2 = this.findViewById(R.id.chat_voice_textmessage_linearlayout);
		if(view2!=null&&view2.getVisibility()==View.VISIBLE){
			TextView text = (TextView)view2.findViewById(R.id.chat_voice_textmessage_textview);
			int count = text.getLineCount();
			if(count == 1){
				this.setMeasuredDimension(getMeasuredWidth(), MIN_HEIGHT);
				int h = (text.getMeasuredHeight()+this.getPaddingTop()+this.getPaddingBottom());
				if(h>MIN_HEIGHT){
					h = ((MIN_HEIGHT - text.getMeasuredHeight())>>1);
					this.setPadding(this.getPaddingLeft(), h, this.getPaddingRight(), h);
				}
			}
		}
		
	};
	private boolean hasOtherViews(){
		//View view =this.findViewById(R.id.cdw_feed_view);
		int index = 0;
		final int count = this.getChildCount();
		while(index<count){
			View v = this.getChildAt(index);
			if(v.getVisibility()==View.VISIBLE){
				return true;
			}
			index++;
		}
		return false;
	}
	
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
//		if(isShowFeed()){
//			View view = this.findViewById(R.id.cdw_feed_view);
//		//	SystemUtil.log("onmea", "view.height = "+this.getPaddingTop()+","+view.getMeasuredHeight()+","+this.getPaddingRight()+","+(getWidth()-this.getPaddingRight()));
//		
//			view.layout(
//					this.getPaddingLeft(), 
//					this.getPaddingTop(), 
//					this.getPaddingLeft()+view.getMeasuredWidth(), 
//					this.getPaddingTop()+view.getMeasuredHeight());
//			int k = 0;
//			final int count = this.getChildCount();
//			final int top = view.getBottom();
//			int tempTop = 0;
//			while(k<count){
//				View v = this.getChildAt(k);
//				if(v!=view && v.getVisibility()==View.VISIBLE){
//					tempTop = ((getHeight()-top-v.getMeasuredHeight())>>1)+top;
//					v.layout(this.getPaddingLeft(),  
//							tempTop, 
//							getWidth()-this.getPaddingRight(), 
//							tempTop+v.getMeasuredHeight());
//				}
//				k++;
//			}
//		}
	};
	
	
	@Override
	protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
		//if(!isShowFeed())
		{
			 final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			 int length = MeasureSpec.getSize(parentWidthMeasureSpec)-this.getPaddingLeft()-this.getPaddingRight();
			 if(lp.width>length){
				 lp.width=length;
			 }
			 super.measureChildWithMargins(child, MeasureSpec.EXACTLY|MeasureSpec.getSize(parentWidthMeasureSpec), widthUsed, parentHeightMeasureSpec, heightUsed);
		}
//		else{
//			layoutChildrenNoMagin(parentWidthMeasureSpec, parentHeightMeasureSpec);
//		} 
	};
	
	void layoutChildrenNoMagin(int parentWidthMeasureSpec, int parentHeightMeasureSpec){
		int c = this.getChildCount();
		 int index = 0;
		 while(index<c){
			 View v = this.getChildAt(index);
			 v.measure(MeasureSpec.EXACTLY|(MeasureSpec.getSize(parentWidthMeasureSpec)-this.getPaddingLeft()-this.getPaddingRight()), parentHeightMeasureSpec);
			 index++;
		 }
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		this.getBackground().setBounds(0, 0, getWidth(), getHeight());
		this.getBackground().draw(canvas);
		this.drawForeground(canvas);
		this.dispatchDraw(canvas);
		if(isShowCover()){
			this.drawCover(canvas);
		}
	}
	public void drawCover(Canvas canvas){
		COVER = isLeft()?COVER_LEFT:COVER_RIGHT;
		COVER.setBounds(0, 0, getWidth(), getHeight());
		COVER.draw(canvas);
	}
	
	
	public void drawForeground(Canvas canvas){
		FOREGROUND_DRAWABLE.setBounds(0, 0, getWidth(), getHeight());
		FOREGROUND_DRAWABLE.setAlpha(mAlpha);
		FOREGROUND_DRAWABLE.draw(canvas);
	}
	
	
	public void setForegroundAlpha(int alpha){
		mAlpha = alpha;
		this.invalidate();
	}
}

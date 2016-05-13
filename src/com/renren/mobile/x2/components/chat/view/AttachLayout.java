package com.renren.mobile.x2.components.chat.view;



import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.CommonUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * @author dingwei.chen
 * */
public class AttachLayout extends ViewGroup {

//	<attr format="integer" name="attach_loading"/>
//    <attr format="integer" name="attach_error" />
//    <attr format="integer" name="attach_unlisten"/>
//    <attr format="integer" name="attach_domain" />
//    <attr format="integer" name="loading_width" />
//    <attr format="integer" name="loading_height" />
//    <attr format="boolean" name="attach_right"/>
	
	int mLoadingId ;
	int mErrorId ;
	int mUnListenId;
	int mDomainId;
	Drawable mErrorSrc;
	Drawable mDomainSrc;
	Drawable UnListenSrc;
	boolean mIsRight = false;
	int mLoading_Width = 0;
	int mLoading_Height = 0;
	View mLoadingView ;
	ImageView mErrorView;
	ImageView mUnListenView;
	ImageView mDomainView;
	int mLeft_Padding = 0;
	
	
	public AttachLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AttachLayout);
		this.initArray(array);
		array.recycle();
		this.initViews();
		
	}
	void initViews(){
		mLoadingView = new ProgressBar(getContext());
		mLoadingView.setId(mLoadingId);
		mLoadingView.setVisibility(View.VISIBLE);
		AttachLayout.LayoutParams params = new AttachLayout.LayoutParams(mLoading_Width, mLoading_Height);
		mLoadingView.setLayoutParams(params);
		mErrorView = new ImageView(getContext());
		mErrorView.setId(mErrorId);
		mErrorView.setImageDrawable(mErrorSrc);
		mErrorView.setVisibility(View.VISIBLE);
		if(mDomainId!=0){
			mDomainView = new ImageView(getContext());
			mDomainView.setId(mDomainId);
			mDomainView.setImageDrawable(mDomainSrc);
		}
		
		if(mUnListenId!=0){
			mUnListenView = new ImageView(getContext());
			mUnListenView.setId(mUnListenId);
			mUnListenView.setImageDrawable(UnListenSrc);
		}
		this.addViews(mLoadingView,mErrorView,mDomainView,mUnListenView);
		
		
		
	}
	
	public void addViews(View...views){
		for(View v:views){
			if(v!=null){
				if(v.getLayoutParams()==null){
					CommonUtil.log("layout1", "add view1 ="+v.getClass().getSimpleName());
					this.addView(v,new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				}else{
					CommonUtil.log("layout1", "add view2 ="+v.getClass().getSimpleName());

					this.addView(v,v.getLayoutParams());
				}
			}
		}
	}
	public class LayoutParams extends MarginLayoutParams{

		public LayoutParams(int arg0, int arg1) {
			super(arg0, arg1);
		}
	}
	
	 
	
	void initArray(TypedArray array){
		mLoadingId = array.getResourceId(R.styleable.AttachLayout_attach_loading, 0);
		mErrorId = array.getResourceId(R.styleable.AttachLayout_attach_error, 0);
		mDomainId = array.getResourceId(R.styleable.AttachLayout_attach_domain, 0);
		mUnListenId = array.getResourceId(R.styleable.AttachLayout_attach_unlisten, 0);
		mIsRight = array.getBoolean(R.styleable.AttachLayout_attach_right, false);
		mLoading_Width =  (int)array.getDimension(R.styleable.AttachLayout_loading_height, 0f);
		mLoading_Height = (int)array.getDimension(R.styleable.AttachLayout_loading_width, 0f);
		mErrorSrc 	=array.getDrawable(R.styleable.AttachLayout_attach_error_src);
		mDomainSrc 	=array.getDrawable(R.styleable.AttachLayout_attach_domain_src);
		UnListenSrc =array.getDrawable(R.styleable.AttachLayout_attach_unlisten_src);
		this.mLeft_Padding = array.getDimensionPixelSize(R.styleable.AttachLayout_left_padding, 0);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int c = this.getChildCount();
		int index = 0;
		View child = null;
		while(index<c){
			child = this.getChildAt(index);
			if(child!=null){
				child.measure(widthMeasureSpec, heightMeasureSpec);
				if(child==mLoadingView){
					child.measure(MeasureSpec.EXACTLY|mLoading_Width,
					MeasureSpec.EXACTLY|mLoading_Height);
				}
			}
			index++;
		}
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int rw = mLoadingView.getMeasuredWidth()>mErrorView.getMeasuredWidth()
				?mLoadingView.getMeasuredWidth():mErrorView.getMeasuredWidth();
		int lw = 0;
		if(mDomainView!=null){
			
			lw = mDomainView.getMeasuredWidth()>mUnListenView.getMeasuredWidth()?
					mDomainView.getMeasuredWidth():mUnListenView.getMeasuredWidth();
			CommonUtil.log("domain", "domain is not null "+lw);
			lw+=mLeft_Padding;
		}
		this.setMeasuredDimension(rw+lw, height);
	};
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if(changed){
			this.layoutRight(left, top, right, bottom);
			if(mDomainView!=null){
				this.layoutLeft(left, top, right, bottom);
			}
		}
	}
	void layoutLeft(int left, int top, int right,int bottom){
		int width = mDomainView.getMeasuredWidth()>mUnListenView.getMeasuredWidth()?
				mDomainView.getMeasuredWidth():mUnListenView.getMeasuredWidth();
	    int height = this.getHeight();
	    int offset = ((width - mDomainView.getMeasuredWidth())>>1)+mLeft_Padding;
	   int t =( height -mDomainView.getMeasuredHeight());
	    mDomainView.layout(offset, t, offset+mDomainView.getMeasuredWidth(), t+mDomainView.getMeasuredHeight());
	    offset = ((width - mUnListenView.getMeasuredWidth())>>1)+mLeft_Padding;
	    t = (height-mUnListenView.getMeasuredHeight())>>1;
	    mUnListenView.layout(offset, t, offset+mUnListenView.getMeasuredWidth(), t+mUnListenView.getMeasuredHeight());
	}
	void layoutRight(int left, int top, int right,int bottom){
		
		int width = mLoadingView.getMeasuredWidth()>mErrorView.getMeasuredWidth()
				?mLoadingView.getMeasuredWidth():mErrorView.getMeasuredWidth();
		int offsetX = (this.getWidth()-width)>>1;
		CommonUtil.log("log", "layoutRight = "+(mErrorView.getVisibility()==View.VISIBLE));
		int height = this.getHeight();
		int t = (height-mLoadingView.getMeasuredHeight())>>1;
		int offset =( width-mLoadingView.getMeasuredWidth())>>1;
		mLoadingView.layout(offsetX+offset, t, offsetX+width-offset, t+mLoadingView.getMeasuredHeight());
		t = (height-mErrorView.getMeasuredHeight())>>1;
		offset =( width-mErrorView.getMeasuredWidth())>>1;
		mErrorView.layout(offsetX+offset, t, offsetX+width-offset, t+mErrorView.getMeasuredWidth());
	}
}

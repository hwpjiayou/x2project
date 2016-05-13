package com.renren.mobile.x2.components.comment;

import com.renren.mobile.x2.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

public class CommentEmptyViewPopwindow extends ViewGroup{

	private Context mContext;
	private View mPopView,mContentView;
	
	public CommentEmptyViewPopwindow(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CommentEmptyViewPopwindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext=context;
	}

	public CommentEmptyViewPopwindow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
//		mPopView=findViewById(R.id.comment_popview);
//		int specWidth=MeasureSpec.getSize(widthMeasureSpec);
//		int specHeight=MeasureSpec.getSize(heightMeasureSpec);
//		Log.v("--hwp--","height "+specHeight+" "+specWidth);
//		setMeasuredDimension(specWidth, specHeight);
//		mContentView=mPopView.findViewById(R.id.popwindow_Linear);
//	    measureChild(mContentView, MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY));   	
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int top=-(mPopView.getHeight());
		int left=0;
		
		mContentView.layout(left, top, mPopView.getMeasuredWidth(),mPopView.getMeasuredHeight());
	}

	
	public void doUpAnimation(){
		
	}
	
}

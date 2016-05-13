package com.renren.mobile.x2.emotion;

import java.util.ArrayList;

import com.renren.mobile.x2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class EmotionPointer {
	private ArrayList<ImageView> poinerList = new ArrayList<ImageView>(5);
	private Context mContext ;
	private LayoutInflater mInfalter;
	private Bitmap selectbitmap;
	private Bitmap unselectbitmap;
	private LinearLayout mRootView;
	public EmotionPointer(Context c, int pointSum,int defaultpos) {
		this.mContext = c;
		mInfalter=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = (LinearLayout) mInfalter.inflate(R.layout.emotion_point_layout, null);
		selectbitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_emotion_point_selected);
		unselectbitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_emotion_point_unselected);
		for(int i=0;i<pointSum;++i){
			ImageView img = new ImageView(mContext);
			poinerList.add(img);
			mRootView.addView(img);
		}
		poinerList.get(defaultpos).setImageBitmap(selectbitmap);
		this.setDefaultWithOut(defaultpos);
		
	}
	private void setDefaultWithOut(int index){
		for(int i = 0; i < poinerList.size();++i){
			if(index == i){
				continue;
			}
			poinerList.get(i).setImageBitmap(unselectbitmap);
		}
	}
	public void movetoIndex(int index){
		this.setDefaultWithOut(index);
		poinerList.get(index).setImageBitmap(selectbitmap);
	}
	
	public LinearLayout getPointView(){
		return mRootView;
	}
}

package com.renren.mobile.x2.emotion;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
/***
 *基本表情单元
 * @author 
 *
 */
public class Emotion {

	public Emotion(String emotionName){
		this.mEmotionName = emotionName;
	}
	/* 0无错误   1解析出现问题，需要重新加载，2或者null没有找到相应的阿狸	 */
	public int errorcode = 0;// 
	public int counter =0;
	public String mEmotionName;
	public List<Bitmap> mEmotionList = new ArrayList<Bitmap>();
	public int mFrameSize = 0;
	int mIndex = 0;
	public boolean mIsFlash = false;
	public Bitmap next(){
		if(mEmotionList.size()<=0){
			return null;
		}
		if(mIndex>=mEmotionList.size()){
			mIndex =0;
		}
		Bitmap bitmap = mEmotionList.get(mIndex);
		mIndex++;
		mIndex%=mFrameSize;
		return bitmap;
	}
	public void addBitmap(Bitmap bitmap){
		mEmotionList.add(bitmap);
		this.mFrameSize = mEmotionList.size();
	}
	public Bitmap get(int i){
		if(mEmotionList==null||mEmotionList.size()==0){
			return null;
		}
		if(i>mEmotionList.size()-1){
			i=0;
		}
		return mEmotionList.get(i);
	}
}

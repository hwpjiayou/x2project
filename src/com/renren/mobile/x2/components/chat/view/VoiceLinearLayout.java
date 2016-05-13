package com.renren.mobile.x2.components.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.utils.CommonUtil;

/**
 * @author dingwei.chen
 * @说明 可以通过传入的时间进行自适应长度的语音控件
 * 
 * */
public class VoiceLinearLayout extends LinearLayout{
	
	int mMaxLength = 0;
	int mTime = 0;
	int mMinLength = 0;
	View mTimeView = null;
	View mImage = null;
	public VoiceLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	protected void onFinishInflate() {
		mTimeView = this.findViewById(R.id.chat_voice_voicemessage_textview);
		mImage = this.findViewById(R.id.chat_voice_voicemessage_imageview);
		mMinLength = mTimeView.getMeasuredWidth()+mImage.getMeasuredWidth();
	};
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Boolean flag = (Boolean)this.getTag();
		if(flag==null){
			flag = false;
		}
		if(!flag){
			mMaxLength = MeasureSpec.getSize(widthMeasureSpec);
			int m = getLengthInMid(mTime, mMaxLength);
			int c = this.getChildCount();
			while(c-->0){
				View v = this.getChildAt(c);
				v.measure(MeasureSpec.AT_MOST|m, heightMeasureSpec);
			}
			this.setMeasuredDimension(m, getMeasuredHeight());
		}else{
			this.setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
		}
	};
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		CommonUtil.log("voice","voice onLayout height = "+this.getHeight());
	};
	/**
	 * @author dingwei.chen
	 * @说明 根据传入的时间进行适配控件长度
	 * */
	public void setTime(ChatItemHolder holder,int time,boolean isAutoMatch){
		mTime = time;
		holder.mMessage_Voice_Time_TextView.setText(time+"秒");
	}
	
	
	/**
	 * @author dingwei.chen
	 * 获得中屏下的气泡长度
	 * @param playTime 播放时间
	 * @param baseLength 基础长度 (采用屏幕宽度)
	 * */
	private final  int getLengthInMid(int playTime,int baseLength){
		int length = 0;
		if(playTime==1){
			length = (int)(0.3*baseLength);
		}else if(playTime<=10){
			length = (int)((3.5*playTime+30)/100*baseLength);
		}else if(playTime<60){
			length = (int)((0.7*playTime+65)/100*baseLength);
		}else{
			length = (int)(baseLength);
		}
		if(length<mMinLength){
			return mMinLength;
		}else if(length>baseLength){
			length = baseLength;
		}
		return length;
	}
	

}

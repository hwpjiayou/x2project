package com.renren.mobile.x2.components.chat.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.renren.mobile.x2.emotion.EmotionString;

import android.content.Context;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;


public class EmotionTextView extends TextView {

	public EmotionString mEmotionString = null;
	public boolean mIsRest = true;
	public final static Pattern SHORT_LINK_PATTERN =  Pattern.compile("http://rrurl.cn/[a-z0-9A-Z]{6}");//短连接匹配模式
	public final int LINK_COLOR = 0x7f0700af;
	public static final int MAX_WIDTH = 100;//<TODO cf>
	
	public EmotionTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * 设置消息
	 * */
	public void setMessage(String string){
//		if(!EmotionPool.getInstance().containTitle(string)){
//			mEmotionString = new EmotionString();
//				mEmotionString.updateText(string);
//				this.processUrl(mEmotionString);
//			EmotionPool.getInstance().putTitle(string, mEmotionString);
//		}else{
//			mEmotionString = EmotionPool.getInstance().getTitle(string);
//		}
		//add by jia.xia
	   // mEmotionString = EmotionStringRef.getInstacne().get(string);
		if(mEmotionString == null){
			mEmotionString = new EmotionString(string);
			//EmotionStringRef.getInstacne().put(string, mEmotionString);
		}
		
		this.setText(mEmotionString);
		ViewGroup.LayoutParams params = this.getLayoutParams();
		if(params!=null){
			this.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int width = this.getMeasuredWidth();
			if(width>=MAX_WIDTH){
				params.width = MAX_WIDTH;
			}else{
				params.width = width;
			}
			this.setLayoutParams(params);
		}
		this.setMovementMethod(LinkMovementMethod.getInstance());
	}
	private int getEndIndex(String sub){
		int length = sub.length();
		int index = "http://".length();
		while(index<length){
			if(isUrlChar(sub.charAt(index))){
				index++;
				continue;
			}else{
				break;
			}
		}
		return index;
	}
	
	private boolean isUrlChar(char c){
		int k = (int)c;
		return Character.isDigit(c)
				||(65<=k&&k<=90)
				||(97<=k&&k<=122)
				||c=='%'
				||(c=='/')
				||(c=='.');
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}

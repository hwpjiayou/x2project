package com.renren.mobile.x2.components.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author dingwei.chen
 * */
public class SwitchImageView extends ImageView{
	public SwitchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public void switchImage(int sourceId){
		this.setImageResource(sourceId);
	}
	
	
	
}

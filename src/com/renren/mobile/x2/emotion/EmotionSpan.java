package com.renren.mobile.x2.emotion;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;


public class EmotionSpan extends ImageSpan {

	public Bitmap mBitmap ;
	public Drawable mDrawable;
	public EmotionSpan(Bitmap b) {
		super(b, ImageSpan.ALIGN_BOTTOM);
		this.mBitmap = b;
	}
	public EmotionSpan(Drawable b) {
		super(b, ImageSpan.ALIGN_BOTTOM);
		this.mDrawable = b;
	}
	public EmotionSpan copy(){
		if(this.mBitmap!=null){
			return new EmotionSpan(mBitmap);
		}else if(this.mDrawable!=null){
			return new EmotionSpan(mDrawable);
		}
		return null;
		
	}

}

package com.renren.mobile.x2.components.chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.renren.mobile.x2.components.chat.face.IVoiceBg;
import com.renren.mobile.x2.utils.DipUtil;

public final class VoiceBgImageView extends ImageView{

	private final Paint PAINT = new Paint();
	private final float START_ANGLE = -90F;
	private final int left_top_padding = DipUtil.calcFromDip(11);// 6 8+3=11
	private final int right_bottom_padding = DipUtil.calcFromDip(11);// 4
	private final int lineW = DipUtil.calcFromDip(6);
	private  IVoiceBg voiceBg;
	
	public VoiceBgImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		PAINT.setAntiAlias(true);
	}

	public void setVoiceBg(IVoiceBg bg){
		voiceBg = bg;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		PAINT.setStyle(Style.STROKE);
		PAINT.setStrokeWidth(lineW);
		if(voiceBg!=null){
			PAINT.setColor(voiceBg.getColor());
		}
		Path path = new Path();
		Rect rect = new Rect();
		this.getHitRect(rect);
//		if(Logger.mDebug){
//			Logger.logd(Logger.RECORD,"rect 00000="+rect.toString()+"#dip="+lineW+"#l="+left_top_padding+"#b="+right_bottom_padding);
//			
//		}
		rect.left += left_top_padding;
		rect.top += left_top_padding;
		rect.right -= right_bottom_padding;
		rect.bottom -= right_bottom_padding;
		
//		if(Logger.mDebug){
//			Logger.logd(Logger.RECORD,"rect 00000="+rect.toString()+"#dip="+dip+"#l="+left_top_padding+"#b="+right_bottom_padding);
//			
//		}
		
		RectF rectf = new RectF(rect);
		if(voiceBg!=null){
			path.addArc(rectf, START_ANGLE, voiceBg.getDrawAngle());
		}
		canvas.drawPath(path, PAINT);
		path.close();
		PAINT.setStyle(Style.FILL);
			
	}
	
}
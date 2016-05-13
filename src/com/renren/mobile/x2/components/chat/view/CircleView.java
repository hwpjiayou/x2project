/**
 * 
 */
package com.renren.mobile.x2.components.chat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 圆形的图片的ImageView
 * author zxc
 */
public class CircleView extends ImageView{

	private final static int RADIUS = 200;
	
	private Bitmap mBitmap;
	private Paint mPaint;
	private Rect rect;
	private Path clip;
	private float height;
	private float width;
	private int radius = RADIUS;
	/**
	 * @param context
	 */
	public CircleView(Context context) {
		super(context);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xff008000);
		mPaint.setStyle(Style.STROKE);
		rect = new Rect(0, 0, RADIUS * 2, RADIUS * 2);
		clip = new Path();
		Log.d("zxc",2+RADIUS+" " + 2+RADIUS+"  "+  RADIUS+"   " +Direction.CW);
	}
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xff008000);
		mPaint.setStyle(Style.STROKE);
		rect = new Rect(0, 0, RADIUS * 2, RADIUS * 2);
		clip = new Path();
		Log.d("zxc",2+RADIUS+" " + 2+RADIUS+"  "+  RADIUS+"   " +Direction.CW);
	}
	/* (non-Javadoc)
	 * @see android.widget.ImageView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.height = this.measureHeigh(heightMeasureSpec);
		this.width  = this.measureWidth(widthMeasureSpec);
		Log.d("zxc","onMeasure " + height+" " + width);
	}
	
	
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if(specMode == MeasureSpec.AT_MOST){
			result = specSize;
		}else{
			result = specSize;
			
		}
		return result;
	}
	private int measureHeigh(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if(specMode == MeasureSpec.AT_MOST){
			result = specSize;
		}else{
			result = specSize;
			
		}
		return result;
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xff008000);
		mPaint.setStyle(Style.STROKE);
		rect = new Rect(0, 0, RADIUS * 2, RADIUS * 2);
		clip = new Path();
		Log.d("zxc",2+RADIUS+" " + 2+RADIUS+"  "+  RADIUS+"   " +Direction.CW);
	}
//	
	
	/* (non-Javadoc)
	 * @see android.view.View#setLayoutParams(android.view.ViewGroup.LayoutParams)
	 */
	@Override
	public void setLayoutParams(LayoutParams params) {
		super.setLayoutParams(params);
	}
	private float getWH(){
//		height = mBitmap.getHeight();
//		width = mBitmap.getWidth();
		Log.d("zxc","h " + height+ " W " + width);
		return height > width? width:height;
	}
	/* (non-Javadoc)
	 * @see android.widget.ImageView#setImageBitmap(android.graphics.Bitmap)
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		radius = (int)getWH()/2-5;
		Matrix matrix = new Matrix();
//		matrix.postScale(sx, sy)
		float h = bm.getHeight();
		float w = bm.getWidth();
		float hRate =height/h;
		float wRate = width/w;
		Log.d("zxc","hRate " + hRate + " wRate " + wRate + " bm.h " + bm.getHeight()+" bm.w " +  bm.getWidth());
		matrix.postScale(wRate, hRate);
		if(bm==null||h==0||w==0){
			return;
		}
//		mBitmap = Bitmap.createBitmap(bm, 0, 0, (int)w, (int)h, matrix, true);
		mBitmap = bm;
		Log.d("zxc","radius " + radius);
		clip.addCircle(2+radius, 2+radius, radius, Direction.CW);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("draw","ondraw ");
		mPaint.setAntiAlias(true); 
		Log.d("zxc","onDraw" + clip.toString());
		canvas.save();
		//canvas.clipPath(clip);
		// draw popup
		if(mBitmap!=null){
			canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		}
	}
	
}

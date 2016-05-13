package com.renren.mobile.x2.components.chat.view;

import com.renren.mobile.x2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author dingwei.chen
 * */
public class PluginPager extends ViewGroup{

	public static Bitmap sLineX = null;
	public static Bitmap sLineY = null;
	public static final int DEFAULT_PADDING = 10;
	public static final int DEFAULT_PADDING_TOP = 33;
	public static final Paint PAINT = new Paint();
	private int mPadding = DEFAULT_PADDING;
	private int mPadding_Top = DEFAULT_PADDING_TOP;
	private int mStepX = 0;
	private int mStepY = 0;
	private int mItemWidth = 0;
	private int mItemHeight = 0;
	Rect mRect = null;
	private static final int DEFUALT_BACK_COLOR = 0X00eaeaea;
	
	public PluginPager(Context context){
		super(context);
		this.init(context);
	}
	
	private void init(Context context){
		sLineX   = getBitmap(context,sLineX, R.drawable.test_plugin_linex);
		sLineY   = getBitmap(context,sLineY, R.drawable.test_plugin_liney);
		PAINT.setAntiAlias(true);
		PAINT.setColor(Color.RED);
		this.setBackgroundColor(DEFUALT_BACK_COLOR);
		this.setDrawingCacheEnabled(false);
	}
	public static Bitmap getBitmap(Context context,Bitmap bitmap,int id){
		if(bitmap==null||bitmap.isRecycled()){
			return BitmapFactory.decodeResource(context.getResources(), id);
		}
		return bitmap;
	}
	
	public PluginPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		this.drawLineX(canvas);
		this.drawLineY(canvas);
	};
	
	private void drawLineX(Canvas canvas){
		int height = sLineX.getHeight();
		int top = ((getHeight()-height)>>1);
		Rect rect = this.obtainRect(0, top, 0, top+height);
		rect.left = mPadding;
		rect.right = getWidth()-mPadding;
		canvas.drawBitmap(sLineX, null, rect,PAINT);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.measureChildren(widthMeasureSpec, heightMeasureSpec);
	};
	
	private void drawLineY(Canvas canvas){
		int width = sLineY.getWidth();
		int height = ((this.getHeight()-sLineX.getHeight())>>1);
		int width_step = this.getWidth()/3;
		int left = (width_step-(width>>1));
		int left2 = ((width_step<<1)-(width>>1));
		
		Rect rect = this.obtainRect(left, this.mPadding_Top, left+width, height-this.mPadding_Top);
		canvas.drawBitmap(sLineY, null, rect,PAINT);
		rect = this.obtainRect(left, mStepY+this.mPadding_Top, left+width, getHeight()-this.mPadding_Top);
		canvas.drawBitmap(sLineY, null, rect,PAINT);
		rect = this.obtainRect(left2, this.mPadding_Top, left2+width, height-this.mPadding_Top);
		canvas.drawBitmap(sLineY, null, rect,PAINT);
		rect = this.obtainRect(left2, mStepY+this.mPadding_Top, left2+width, getHeight()-this.mPadding_Top);
		canvas.drawBitmap(sLineY, null, rect,PAINT);
	}
	
	private Rect obtainRect(int left, int top, int right, int bottom){
		if(mRect==null){
			mRect = new Rect();
		}
		mRect.left = left;
		mRect.top = top;
		mRect.right = right;
		mRect.bottom = bottom;
		return mRect;
	}

	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = this.getChildCount();
		if(count>0){
			int index = 0;
			mStepX = this.getWidth()/3;
			mStepY = this.getHeight()>>1;
			mItemWidth = (mStepX-(mPadding<<1));//about 1/6 view's width
			mItemHeight = (mStepY-(mPadding<<1));
			int off_x = mPadding;
			int off_y = mPadding;
			while(index<count){
				View view = this.getChildAt(index);
				int offX = index%3;//0,1,2
				int offY = (index)/3;
				offX = (mStepX*offX+off_x);
				offY = (mStepY*offY+off_y);
				view.layout(offX, offY, offX+mItemWidth, offY+mItemHeight);
				index ++;
			}
		}
	}

}

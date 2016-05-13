package com.renren.mobile.x2.components.chat.view;

import java.io.File;

import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.ImagePool;
import com.renren.mobile.x2.utils.CommonUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**
 * @author dingwei.chen
 * @说明 自适应控件
 * */
public class BaseImageView extends ImageView {
	
	static final int DEFAULT_PARAMS_WIDTH_MID = 190;
	static final int DEFAULT_PARAMS_HEIGHT_MID = 190;
	static final int MIN_LENGTH_MID = 30;
	static final int WARN_LENGTH_MID = 75;
//	private String mPath = null;
	private int mState = 0;
	private static final int MASK_LAYER_SHOW = 0X1;
	private static final int MASK_BRUSH = 0X2;
	private static final int LAYER_COLOR = 0X77000000;
	
	public FrameLayout.LayoutParams mDefaultParams = 
			new FrameLayout.LayoutParams(
					100
					,100);
	
	Paint mPaint = new Paint();
	public BaseImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.WHITE);
	}
	@Override
	protected void onDraw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		Drawable d = this.getDrawable();
//		Bitmap b = null;
//		if(d!=null){
//			BitmapDrawable bd = (BitmapDrawable)d;
//			if(bd.getBitmap().isRecycled()){
//				if(mPath==null){
//					return;
//				}else{
//					b = ImagePool.getInstance().getBitmapFromLocalNotCompress(mPath);
//					if(b!=null&&!b.isRecycled()){
//						this.setImageBitmap(b,mPath);
//					}else{
//						return;
//					}
//				}
//			}
//		}
		super.onDraw(canvas);
		if(this.isShowLayer()){
			canvas.drawColor(LAYER_COLOR);
		}
	}
	
	
	Bitmap mBitmap = null;
	public void setImageBitmap(Bitmap bm) {
		mBitmap  = bm;
		super.setImageBitmap(bm);
		
		//SystemUtil.log("onmea", "setImageBitmap");
	}
//	public void setImageBitmap(Bitmap bm,String path) {
//	//	this.mPath = path;
//		mBitmap  = bm;
//		super.setImageBitmap(bm);
//		//SystemUtil.log("onmea", "setImageBitma "+path);
//	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//SystemUtil.log("onmea", CommonUtil.getMode(widthMeasureSpec)+","+CommonUtil.getSize(widthMeasureSpec));
		if(isBrush()){
			if(mBitmap!=null){
				//SystemUtil.log("onmea", "isBrush "+mBitmap.getWidth());
				Size size = this.compress(CommonUtil.getSize(widthMeasureSpec), mBitmap);
				this.setMeasuredDimension(size.width, size.height);
				//SystemUtil.log("onmea", "brush width = "+size.width+","+size.height+",");
			}
		}else{
			//SystemUtil.log("onmea", "notBrush");
			if(mBitmap!=null){
				Size size = this.calcParams(this.mBitmap);
					this.setMeasuredDimension(size.width, size.height);
					//SystemUtil.log("onmea", "width = "+size.width+","+size.height);
			}
		}
	};
	
	private Size compress(int baseLength,Bitmap bitamp){
		Size size = new Size(bitamp);
		if(size.width>baseLength){
			double alp = ((baseLength+0.0)/size.width);
			size.width = (int)(size.width*alp);
			size.height = (int)(size.height*alp);
			return size;
		}else{
			return size;
		}
		
	}
	
	
//	public void setImagePath(String path,boolean isDefault_Left){
//		File file = new File(path);
//		if(file.exists()){
//			Bitmap bitmap = ImagePool.getInstance()
//					.getBitmapFromLocal(path);
//			if(bitmap!=null){
//				this.setImageBitmap(bitmap,path);
//			}else{
//				/*图片丢失*/
//				this.setImageBitmap(ChatUtil.getDefualtBitmap(isDefault_Left));
//				return;
//			}
//		}else{
//			/*图片丢失*/
//			this.setImageBitmap(ChatUtil.getDefualtBitmap(isDefault_Left));
//			return;
//		}
//	}
	
	public static class Size{
		public int width ;
		public int height;
		public Size(){}
		public Size(Bitmap bitmap){
			this.width = bitmap.getWidth();
			this.height = bitmap.getHeight();
		}
	}
	
	public Size calcParams(Bitmap bitmap){
		if(bitmap==null){
			return null;
		}
		Size size = new Size();
		size.width = mDefaultParams.width;
		size.height = mDefaultParams.height;
		
		int width = ChatUtil.calcFromDip(bitmap.getWidth());
		int height = ChatUtil.calcFromDip(bitmap.getHeight());
		int viewWidth = size.width;
		int viewHeight = size.height;
		int warnNumber = ChatUtil.calcFromDip(WARN_LENGTH_MID);
		if(width< warnNumber && height<warnNumber){
			size.width = width;
			size.height = height;
			return size; 
		}
		int min_length =  ChatUtil.calcFromDip(MIN_LENGTH_MID);
		if(width>height){
			double alpha = ((double)width)/viewWidth;
			height = (int)(((double)height)/alpha);
			size.height = height;
			
			if(size.height<min_length){
				size.height = min_length;
			}
			return size;
		}
		if(width<=height){
			double alpha = ((double)height)/viewHeight;
			width = (int)(((double)width)/alpha);
			size.width = width;
			if(size.width<min_length){
				size.width = min_length;
			}
		}
		return size;
	}
	
	
	public void calcParams(LayoutParams params,Bitmap bitmap){
		params.width = mDefaultParams.width;
		params.height = mDefaultParams.height;
		if(bitmap==null){
			return;
		}
		int width = ChatUtil.calcFromDip(bitmap.getWidth());
		int height = ChatUtil.calcFromDip(bitmap.getHeight());
		int viewWidth = params.width;
		int viewHeight = params.height;
		int warnNumber = ChatUtil.calcFromDip(WARN_LENGTH_MID);
		if(width< warnNumber && height<warnNumber){
			params.width = width;
			params.height = height;
			return ; 
		}
		int min_length =  ChatUtil.calcFromDip(MIN_LENGTH_MID);
		if(width>height){
			double alpha = ((double)width)/viewWidth;
			height = (int)(((double)height)/alpha);
			params.height = height;
			
			if(params.height<min_length){
				params.height = min_length;
			}
			return;
		}
		if(width<=height){
			double alpha = ((double)height)/viewHeight;
			width = (int)(((double)width)/alpha);
			params.width = width;
			if(params.width<min_length){
				params.width = min_length;
			}
		}
	}
	public void setLayerVisible(boolean isShow){
		if(isShow){
			if(!isShowLayer()){
				mState|=MASK_LAYER_SHOW;
				this.invalidate();
			}
		}else{
			if(isShowLayer()){
				mState&=~MASK_LAYER_SHOW;
				this.invalidate();
			}
		}
		
	}

	public boolean isShowLayer(){
		return (mState&MASK_LAYER_SHOW)!=0;
	}
	public void setIsBrush(boolean isBrush){
		if(isBrush){
			mState|=MASK_BRUSH;
		}else{
			mState&=~MASK_BRUSH;
		}
	}
	public boolean isBrush(){
		return (mState&MASK_BRUSH)!=0;
	}
	
	
	
}

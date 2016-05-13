package com.renren.mobile.x2.components.home.feed;

import com.renren.mobile.x2.utils.log.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
/**
 * @author jia.xia
 * */
public class NewImageView  extends ImageView {
	
	private Bitmap bitmap;
	private Context mContext;
	public NewImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}
	
	public void setURL(final String url,final Bitmap defalutBitmap){
		if(url == null)
			return;
		HttpImageRequest request = new HttpImageRequest(url, !FeedAdapter.isScrolling);
		ImageLoader loader =  ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD, this.mContext);
		Bitmap temp = loader.getMemoryCache(request);
		if(temp != null){
			setImageBitmap(temp);
		}
		else{
			
		}
	}
	
	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	
	//@Override
	//protected void onDraw(android.graphics.Canvas canvas) {
	//		CommonUtil.log("xx", "onDraw");
	//		super.onDraw(canvas);
	//
	//};
	//
	//
	//@Override
	//public void draw(Canvas canvas) {
	//	CommonUtil.log("li", "draw:");
	//	super.draw(canvas);
	//	if(this.bitmap == null)
	//		return;
	//	int width = this.bitmap.getWidth();
	//	int height =this.bitmap.getHeight();
	//	//Rect src = new Rect(0, 0, width, height);
	//	RectF dest = new RectF(0,0,81,81);
	//	Paint paint = new Paint();
	//	paint.setColor(0x00000000);
	//	//canvas.drawBitmap(this.bitmap, src, dest, new Paint());
	//	canvas.drawOval(dest, paint);
	//	CommonUtil.log("li", "draw: end ");
	//}

	
	

	public void setSize(int number){
		//TODO
		number = 0;
		LayoutParams params = this.getLayoutParams();
		params.width = number;
		params.height = number ; 
		this.setLayoutParams(params);
	}
	
	
}

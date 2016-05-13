package com.renren.mobile.x2.components.publisher;

import Pinguo.Android.SDK.Image360JNI;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;

import com.renren.mobile.x2.R;

/**
 * @author ccl
 */
public class IProcessor extends View {

    private static final String TAG = "IProcessor";

    private  Image360JNI mImage360JNI;

    private int[] mBitmapData;
    private Bitmap mBitmap;
    private Rect mDst;

    public IProcessor(Context context) {
        this(context, null, 0);
    }

    public IProcessor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IProcessor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDrawingCacheEnabled(false);
        mDst = new Rect();
        
        
        mBitmap= ((BitmapDrawable) getResources().getDrawable(R.drawable.test_default)).getBitmap();
//        mBitmap = tmp.copy(Bitmap.Config.ARGB_8888, true);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] data = byteArrayOutputStream.toByteArray();
//        tmp.recycle();
//        boolean result;
//        mImage360JNI = new Image360JNI();
//        result = 100 == mImage360JNI.test(10);
//        if(result) {
//            Log.d(TAG, "@IProcessor load succeeded");
//        }
//        result = mImage360JNI.Verify("273719ECF170994E969C400D4FEC72B8");
//        if(result) {
//            Log.d(TAG, "@IProcessor verify succeeded");
//        }
//        result = mImage360JNI.SetImageFromJpegData(data, data.length, 50);
//        if(result) {
//            Log.d(TAG, "@IProcessor load image succeeded");
//        }
    }
    public static Bitmap scaleBitmapToFixMixPixel(Bitmap oriBitmap,int mixPixel){
    	int oriHeight=0;
    	int oriWidth = 0;
    	if(oriBitmap!=null&&mixPixel>0){
    		oriHeight = oriBitmap.getHeight();
    		oriWidth = oriBitmap.getWidth();
    		if(oriWidth*oriHeight<=mixPixel){
    			return oriBitmap;
    		}else{
    			double scaleRate= Math.sqrt((double)mixPixel/(oriWidth*oriHeight));
    			return Bitmap.createScaledBitmap(oriBitmap, (int)(oriWidth*scaleRate), (int)(oriHeight*scaleRate), false);
    		}
    	}
    	return null;
    }
    
    public void setResource(Bitmap bitmap){
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        setResource(data);
    }
    public void setResource(byte[] data){
         boolean result;
         mBitmap = scaleBitmapToFixMixPixel(PublisherUtils.getPicFromBytes(data, null),700000);
         data =PublisherUtils.getByteFromPic(mBitmap, 100);
         mImage360JNI = new Image360JNI();
         result = 100 == mImage360JNI.test(10);
         if(result) {
             Log.d(TAG, "@IProcessor load succeeded");
         }
         result = mImage360JNI.Verify("273719ECF170994E969C400D4FEC72B8");
         if(result) {
             Log.d(TAG, "@IProcessor verify succeeded");
         }
         result = mImage360JNI.SetImageFromJpegData(data, data.length, 80);
         if(result) {
             Log.d(TAG, "@IProcessor load image succeeded data.length:"+data.length);
         }
         Log.d(TAG, "w "+mImage360JNI.GetImageWidth()+"h"+mImage360JNI.GetImageHeight());
         invalidate();
    }
    public Bitmap getProcessedBitmap(){
    	return mBitmap;
    }
    
    public int[] getBitmapData() {
        return mBitmapData;
    }

    public void setBitmapData(int[] bitmapData) {
        this.mBitmapData = bitmapData;
    }

    public void enhance() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_enhance", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void nightEnhance() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_night_enhance", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void cleanSkin() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_cleanskin", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void lomoFengYe() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_lomo_fengye", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void lomoQianHuiYi() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_lomo_qianhuiyi", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void retroRecall() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_retro_recall", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void lightColorGrace() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_lightcolor_grace", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void lightColorSweet() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_lightcolor_sweet", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void hdrpro() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_hdrpro", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }


    public void sketch() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_sketch", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void bw() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mImage360JNI.ProcessEffectImage("effect=renren_bw", false),
                mImage360JNI.GetImageWidth(),
                mImage360JNI.GetImageHeight(),
                Bitmap.Config.ARGB_8888);
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        final int cw = canvas.getWidth();
        final int ch = canvas.getHeight();
        final int bw = mBitmap.getWidth();
        final int bh = mBitmap.getHeight();

        // how many percent we should scale.
        final float wp = cw / (float) bw;
        final float hp = ch / (float) bh;
        final float min = Math.min(wp, hp);

        final int dw = (int) (bw * min);
        final int dh = (int) (bh * min);

        mDst.left = (int) ((cw - dw) / 2f);
        mDst.right = mDst.left + dw;

        mDst.top = (int) ((ch - dh) / 2f);
        mDst.bottom = mDst.top + dh;

        canvas.drawBitmap(mBitmap, null, mDst, null);
    }
}

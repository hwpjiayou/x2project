package com.renren.mobile.x2.components.chat.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;
/***
 * 
 * @author zxc
 *
 */
public class ProgressImageButton extends ImageButton implements Runnable{
	private Handler handler = new Handler();
	private float x,y;
	private float degree=0.0f;
	private float increase =0;
	private boolean isRun =false;
	private boolean isReset = false;
	private float passdegree=0.0f;
	private Timer mTimer;
	private Bitmap runBm,normalBm;
	private int pressid,unpressid;
	private boolean isSetRuningRes = false;
	public ProgressImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ProgressImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ProgressImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {	
		Log.d("zxc","onDraw " + increase);
		x=this.getWidth()/2;
		y=this.getHeight()/2;
		increase+=degree;
		increase%=360;
		canvas.rotate(increase, x, y);
		super.onDraw(canvas);
	}
	public void start(float degrees){
		this.degree=degrees;
		if(isRun){
			return;
		}
		isRun = true;
		
		if(isSetRuningRes)
		{this.setImageBitmap(runBm);}
		new Thread(this).start();
	}
	public void stop(){
//		isRun = false;
//		isReset=true;
//		this.invalidate();
		this.stopdelay(0);
	
	}
	public void stopdelay(final long delay){
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Log.d("zxc","stop with dely  " + delay);
				isRun=false;
			}
		}, delay);
	}
	@Override
	public void run() {
		while(isRun){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					Log.d("zxc","run  ");
					ProgressImageButton.this.invalidate();
				}
			});
		}
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if(isSetRuningRes)
				{
					setImageBitmap(normalBm);
				}			
				}
		});
	}
	/****
	 * 
	 * @param pressRid 按下的drawable id 
	 * @param unPressRid 弹起的drawable id
	 */
	public void setPressSelector(int pressRid,int unPressRid){
		this.pressid= pressRid;
		this.unpressid = unPressRid;
		this.setBackgroundResource(unpressid);
		
	}
	
	/****
	 *  设置旋转和非旋转的src
	 * @param runRes
	 * @param normalRes
	 */
	public void setNormalAndRuningRes(int runRes,int normalRes){
		runBm= BitmapFactory.decodeResource(getResources(), runRes);
		normalBm = BitmapFactory.decodeResource(getResources(), normalRes);
		if(runBm !=null && normalBm != null){
			isSetRuningRes = true;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.setBackgroundResource(pressid);
			break;
		case MotionEvent.ACTION_UP:
			this.setBackgroundResource(unpressid);
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
}

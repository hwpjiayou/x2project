package com.renren.mobile.x2.components.chat.view;



import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DipUtil;
import com.renren.mobile.x2.utils.log.Logger;

/**
 * 语音控件
 * */
public final class VoiceView extends LinearLayout implements android.view.View.OnTouchListener{

	private static final Paint PAINT = new Paint();
	private float mSweepAngle = 360.0f;
	private static final float START_ANGLE = -90F;
	private  int LINE_COLOR = 0xff31b5f4;
	private  int ALERT_COLOR = 0xffef5c30;
	//private  int BACK_COLOR = 0xd84a5154;
	private int mLineWidth = 10;
	private int mColor = LINE_COLOR;
	TextView mTimeTextView = null;
	View mBackImage = null;
	View mRubbish = null;
	View mVoiceToShortView = null;
	View mVoiceSpeaker = null;
	View mVoiceRoot = null;
	LayoutInflater mInflater = null;
	HeaderLayout mHeaderLayout = null;
	private boolean mIsAbandon = false;
	private boolean mIsOver = false;
	private  String TEXT_CANCEL_SEND = ChatUtil.getText(R.string.chat_record_cancel);
	private  String TEXT_RECORD_LESS = ChatUtil.getText(R.string.chat_record_short);
	private  String TEXT_RECORD_OVER = ChatUtil.getText(R.string.chat_record_end);
	private  String TEXT_MOVE_TO_CANCEL = ChatUtil.getText(R.string.chat_record_scroll);
	public static final int MAX_TIME = 60;
	public static final int ALERT_TIME = 55;
	private boolean mIsAlert = false;
	MotionEvent mMotionUp = null;
	private boolean mIsShow = false;
	View[] mVoices = new View[4];
	private boolean mIsShowVolumns = true;
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			int time = getTime();
			int angle = getAngle();
			boolean isShowToCancel =  isToCancel();
			if(time>ALERT_TIME){
				mIsShowVolumns = false;
				mColor = ALERT_COLOR;
				//mAlertImage.setVisibility(View.VISIBLE);
				if(!mIsAbandon){
					mTimeTextView.setVisibility(View.VISIBLE);
				}
				mHeaderLayout.setVisibility(View.VISIBLE);
				if(!mIsAbandon){
					mHeaderLayout.mTextView.setText(TEXT_RECORD_OVER);
				}
				mVoiceSpeaker.setVisibility(View.GONE);
				mIsAlert = true;
				if(time>=MAX_TIME){
					time = MAX_TIME;
					angle = 360;
				}
			}else{
				mIsShowVolumns = true;
				mColor = LINE_COLOR;
				//mAlertImage.setVisibility(View.GONE);
				if(!mIsAbandon && mIsShow){
					mVoiceSpeaker.setVisibility(View.VISIBLE);
					if(isShowToCancel){
						removeCallbacks(mDissmissHeader);
						mHeaderLayout.mTextView.setText(TEXT_MOVE_TO_CANCEL);
						mIsAlert=true;
						mHeaderLayout.setVisibility(View.VISIBLE);
						postDelayed(mDissmissHeader, 2000);
					}
				}
				mTimeTextView.setVisibility(View.GONE);
			}
			setTime(time);
			setSweepAngle(angle);
			postInvalidate();
		};
	};
	
	Runnable mDissmissHeader =new Runnable() {
		public void run() {
			if(!mIsAbandon){
				mHeaderLayout.setVisibility(View.GONE);
				mIsAlert=false;
			}
		}
	};
	
	
	boolean isToCancel(){
		if(this.mListener!=null){
			return this.mListener.onIsShowMoveToCancel();
		}
		return false;
	}
	
	
	private static final int MESSAGE_DRAW = 0;
	public VoiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VoiceView);
		//BACK_COLOR = array.getColor(R.styleable.VoiceView_background_color, 0xd84a5154);
		LINE_COLOR = array.getColor(R.styleable.VoiceView_line_color, 0xff31b5f4);
		ALERT_COLOR = array.getColor(R.styleable.VoiceView_alert_line_color, 0xffef5c30);
		TEXT_CANCEL_SEND = array.getString(R.styleable.VoiceView_cancel_send);
		TEXT_RECORD_LESS = array.getString(R.styleable.VoiceView_record_less);
		TEXT_RECORD_OVER = array.getString(R.styleable.VoiceView_record_over);
		TEXT_MOVE_TO_CANCEL = array.getString(R.styleable.VoiceView_move_to_cancel);
		array.recycle();
		PAINT.setAntiAlias(true);
		this.setBackgroundColor(0x00000000);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.voice_view, this);
		mTimeTextView = (TextView)this.findViewById(R.id.voice_time);
		mBackImage = this.findViewById(R.id.back_image);
		mVoiceRoot = this.findViewById(R.id.voice_root);
		mRubbish = this.findViewById(R.id.bin);
		mVoiceSpeaker = this.findViewById(R.id.voice_speaker);
		mVoiceToShortView = this.findViewById(R.id.voice_to_short);
		this.setOnClickListener(null);
		mHeaderLayout = new HeaderLayout(context);
		this.addView(mHeaderLayout);
		mHeaderLayout.measure(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mHeaderLayout.setVisibility(View.GONE);
		mVoices[0] = this.findViewById(R.id.voice1);
		mVoices[1] = this.findViewById(R.id.voice2);
		mVoices[2] = this.findViewById(R.id.voice3);
		mVoices[3] = this.findViewById(R.id.voice4);
	}
	
	public void setVolumn(int volumn){
		for(View v:mVoices){
			v.setVisibility(View.GONE);
		}
		if(mIsShowVolumns&&!mIsAbandon){
			if(volumn<0){
				volumn = 0;
			}
			if(volumn>=4){
				volumn=3;
			}
			mVoices[volumn].setVisibility(View.VISIBLE);
		}
		
	}
		
	@Override
	public void draw(Canvas canvas) {
		//this.drawBackground(canvas);
		super.draw(canvas);
		this.drawCircle(canvas);
		if(this.getVisibility()==View.VISIBLE && !mIsOver){
			Message m = mHandler.obtainMessage(MESSAGE_DRAW);
			mHandler.sendMessageDelayed(m,30);
		}
	}
	public void pop(){
		this.reset();
		this.setVisibility(View.VISIBLE);
		ScaleAnimation mScaleAnim = new ScaleAnimation(
				0, 
				1.1f, 
				0, 
				1.1f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mScaleAnim.setDuration(200L);
		this.startAnimation(mScaleAnim);
	}
	public void dismiss(){
		this.setVisibility(View.INVISIBLE);
		mHandler.removeMessages(MESSAGE_DRAW);
		ScaleAnimation mScaleAnim = new ScaleAnimation(
				1, 
				0f, 
				1, 
				0f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mScaleAnim.setDuration(200L);
		this.startAnimation(mScaleAnim);
		this.removeCallbacks(mDissmissHeader);
		
	}
	
	
	
	Rect mRect = new Rect();
	public Rect obtainRect(){
		return mRect;
	}
	
    /**
     * 感应区域为与录音圆圈同心的正方形，边长为直径的1.5倍
     */
    boolean isContain(int x, int y) {
        mHeaderLayout.mTextView.setVisibility(View.VISIBLE);
        Rect hitRect = this.obtainRect();
        mVoiceRoot.getGlobalVisibleRect(hitRect);
        int ox = hitRect.centerX();
        int oy = hitRect.centerY();
        int r = (hitRect.width()+(hitRect.width() >> 1))>>1;
        hitRect.left = ox-r;
        hitRect.top = oy -r;
        hitRect.bottom = oy +r;
        hitRect.right = ox +r;
        return hitRect.contains(x, y);
        
    }
	
	public void setTime(int time){
		mTimeTextView.setText(time+"s");
		mTimeTextView.requestLayout();
	}
	
//	private void drawBackground(Canvas canvas){
//		
//		Rect r = new Rect();
//		mVoiceRoot.getHitRect(r);
//		r.top = r.top+mLineWidth;
//		r.left = r.left+mLineWidth;
//		r.bottom = r.bottom-mLineWidth;
//		r.right = r.right-mLineWidth;
//		PAINT.setColor(BACK_COLOR);
//		PAINT.setAlpha(150);
//		canvas.drawCircle(r.centerX(), r.centerY(),(r.width()/2), PAINT);
//		PAINT.setAlpha(255);
//	}
	
	private void drawCircle(Canvas canvas){
		PAINT.setStyle(Style.STROKE);
		int dip=DipUtil.calcFromDip(3);;
		PAINT.setStrokeWidth(dip);
		PAINT.setColor(mColor);
		Path path = new Path();
		Rect rect = new Rect();
		this.mVoiceRoot.getHitRect(rect);
		//canvas.drawRect(rect, PAINT);
		
//		Rect rect222 = new Rect();
//		mBackImage.getHitRect(rect222);
//		canvas.drawRect(rect222, PAINT);
//		
//		int[] location222 = new  int[2] ;
//		rect222 = new Rect();
//		mBackImage.getLocationInWindow(location222);
//		rect222.left=location222[0];
//		rect222.top=location222[1];
//		rect222.right = rect222.left+mBackImage.getWidth();
//		rect222.bottom = rect222.top+mBackImage.getHeight();
//		canvas.drawRect(rect222, PAINT);
//		
//		location222 = new  int[2] ;
//		rect222 = new Rect();
//		mBackImage.getLocationOnScreen(location222);
//		rect222.left=location222[0];
//		rect222.top=location222[1];
//		rect222.right = rect222.left+mBackImage.getWidth();
//		rect222.bottom = rect222.top+mBackImage.getHeight();
//		canvas.drawRect(rect222, PAINT);
//		
//		rect222 = new Rect();
//		mBackImage.getDrawingRect(rect222);
//		canvas.drawRect(rect222, PAINT);
//		
//		rect222 = new Rect();
//		mBackImage.getFocusedRect(rect222);
//		canvas.drawRect(rect222, PAINT);
//		
//		rect222 = new Rect();
//		mBackImage.getGlobalVisibleRect(rect222);
//		canvas.drawRect(rect222, PAINT);
//		
//		rect222 = new Rect();
//		mBackImage.getHitRect(rect222);
//		canvas.drawRect(rect222, PAINT);
//		
//		rect222 = new Rect();
//		mBackImage.getLocalVisibleRect(rect222);
//		canvas.drawRect(rect222, PAINT);
//		
//		if(Logger.mDebug){
//			Logger.logd(Logger.PLAY_VOICE,"asfasdfa ="+mVoiceRoot.getWidth()+"#"+mVoiceRoot.getHeight());
//		}
//		
//		if(Logger.mDebug){
//			Logger.logd(Logger.PLAY_VOICE,"root view ="+rect.toString());
//		}
//		int[] location2 = new  int[2] ;
//		this.mVoiceRoot.getLocationInWindow(location2);
//		if(Logger.mDebug){
//			Logger.logd(Logger.PLAY_VOICE,"root view111 x="+location2[0]+"#"+location2[1]);
//		}
//		Rect rect2 = new Rect();
//		this.mBackImage.getHitRect(rect2);
//		if(Logger.mDebug){
//			Logger.logd(Logger.PLAY_VOICE,"mBackImage ="+rect2.toString());
//		}
//		int[] location = new  int[2] ;
//		this.mBackImage.getLocationInWindow(location);
//		if(Logger.mDebug){
//			Logger.logd(Logger.PLAY_VOICE,"mBackImage view111 x="+location[0]+"#"+location[1]+"#top="+mBackImage.getTop());
//		} 
//		
		RectF rectf = new RectF(rect);
		
		path.addArc(rectf, START_ANGLE, mSweepAngle);
		canvas.drawPath(path, PAINT);
		path.close();
		PAINT.setStyle(Style.FILL);
	}
	public void setSweepAngle(float angle){
		this.mSweepAngle = angle;
	}
	int mH;
	int mW;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mH = 0;
		int count = this.getChildCount();
		if(count>0){
			int k = 0;
			while(k<count){
				View view = this.getChildAt(k);
				view.measure(MeasureSpec.AT_MOST|getWidth(),MeasureSpec.AT_MOST|getHeight());
				if(view!=mHeaderLayout){
					if(mH<view.getMeasuredHeight()){
						mH = view.getMeasuredHeight();
					}
				}
				k++;
			}
			
		}
		int h = mHeaderLayout.getMeasuredHeight();
		if((h+mH)<MeasureSpec.getSize(heightMeasureSpec)){
			 setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
		                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
		}else{
			 setMeasuredDimension(h+mH,h+mH);
		}
	};
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = mVoiceRoot.getMeasuredWidth();
		int height = mVoiceRoot.getMeasuredHeight();
		l = (getWidth()-width)>>1;
		t = ((getHeight()-height)>>1);
		mVoiceRoot.layout(l, t, l+width, t+height);
		width = mHeaderLayout.getMeasuredWidth();
		height = mHeaderLayout.getMeasuredHeight();
		l = (getWidth()-width)>>1;
		this.mHeaderLayout.layout(l, t-height-mLineWidth, l+width, t-mLineWidth);
	}
	
	int getCircleWidth(){
		return DipUtil.calcFromDip(200);
	}
	public void popView(){
		this.pop();
		this.onStart();
	}
	OnTouchListener mTouchListener;
	public void setOnPreTouchListener(OnTouchListener listener){
		mTouchListener = listener;
	}
	
	
	public void hide(){
		CommonUtil.log("hide", "hide1");
		if(mIsShow){
			CommonUtil.log("hide", "hide2");
			this.onTouch(null,obtainEvent());
		}
	}
	
	
	
	private void cloneEvent(MotionEvent event){
		if(this.mMotionUp ==null){
			mMotionUp = MotionEvent.obtain(event);
			mMotionUp.setAction(MotionEvent.ACTION_UP);
		}
	}
	private MotionEvent obtainEvent(){
		return mMotionUp;
	}
	long mPreClickTime = 0L;
	private static final long MIN_OFFSET_TIME = 1500L;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		CommonUtil.log("touch", "ontouch");
		this.cloneEvent(event);
		if(mTouchListener!=null && event.getAction() == MotionEvent.ACTION_DOWN){
			if((System.currentTimeMillis()-mPreClickTime)<MIN_OFFSET_TIME){
				CommonUtil.toast("请休息一会儿");
				this.mIsShow = false;
				return false;
			}
			if(mTouchListener.onTouch(v, event)){
				this.mIsShow = true;
				return false;
			}
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			CommonUtil.log("recordb", "action_down");
			if((System.currentTimeMillis()-mPreClickTime)<MIN_OFFSET_TIME){
				CommonUtil.toast("请休息一会儿");
				this.mIsShow = false;
				return false;
			}
			popView();
			this.mIsShow = true;
			break;
		case MotionEvent.ACTION_MOVE:
			
			int x = (int)event.getRawX();
			int y = (int)event.getRawY();
			Rect r = new Rect();
			mVoiceRoot.getGlobalVisibleRect(r);
			CommonUtil.log("recordb", "action_move "+x+","+y+","+r+"\r\n"+mVoiceRoot.getScrollX()+","+mVoiceRoot.getScrollY());
			if(isContain(x, y)){
				this.mTimeTextView.setVisibility(View.GONE);
				this.mVoiceSpeaker.setVisibility(View.GONE);
				this.mRubbish.setVisibility(View.VISIBLE);
				mHeaderLayout.setVisibility(View.VISIBLE);
				mHeaderLayout.mTextView.setText(TEXT_CANCEL_SEND);
				if(mIsAlert==true) mIsAlert=false;
				this.mIsAbandon = true;
			}else{
				this.mRubbish.setVisibility(View.GONE);
				if(!mIsAlert){
					mHeaderLayout.setVisibility(View.GONE);
				}
				this.mIsAbandon = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			CommonUtil.log("recordb", "action_up");
			if(!this.mIsShow){
				return false;
			}
			mPreClickTime = System.currentTimeMillis();
			this.mIsOver = true;
			this.mIsShow = false;
			CommonUtil.log("hide", "up");
			if(!isLessTime() || mIsAbandon){
				this.mVoiceToShortView.setVisibility(View.GONE);
				this.dismiss();
				onVoiceEnd(!mIsAbandon);
			}else{
				this.mTimeTextView.setVisibility(View.GONE);
				this.mVoiceToShortView.setVisibility(View.VISIBLE);
				this.mHeaderLayout.mTextView.setText(TEXT_RECORD_LESS);
				this.mHeaderLayout.setVisibility(View.VISIBLE);
				this.mIsShowVolumns = false;
				this.setVolumn(0);
				mVoiceSpeaker.setVisibility(View.GONE);
				mHandler.removeMessages(MESSAGE_DRAW);
				this.postDelayed(new Runnable() {
					@Override
					public void run() {
						dismiss();
						onVoiceEnd(false);
					}
				}, 500);
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	public void monitorView(View view){
		view.setOnTouchListener(this);
	}
	public static interface MonitorListener{
		public void onVoiceStart();
		public int onGetTime();
		public int onGetAngle();
		public boolean onIsLessMinTime();
		public void onVoiceEnd(boolean isSuccess);
		public boolean onIsShowMoveToCancel();
	}
	public void onVoiceEnd(boolean isSuccess){
		if(mListener!=null){
			mListener.onVoiceEnd(isSuccess);
		}
	}
	
	public boolean isLessTime(){
		if(mListener!=null){
			return mListener.onIsLessMinTime();
		}
		return false;
	}
	
	public void onStart(){
		if(mListener!=null){
			mListener.onVoiceStart();
		}
	}
	public int getTime(){
		if(mListener!=null){
			return mListener.onGetTime();
		}
		return 0;
	}
	public int getAngle(){
		if(mListener!=null){
			return mListener.onGetAngle();
		}
		return 0;
	}
	
	
	MonitorListener mListener = null;
	public void setMonitorListener(MonitorListener listener){
		mListener = listener;
	}
	public void reset(){
		mColor = LINE_COLOR;
		this.setTime(0);
		mTimeTextView.setVisibility(View.GONE);
		mRubbish.setVisibility(View.GONE);
		//mAlertImage.setVisibility(View.GONE);
		mHeaderLayout.mTextView.setText(TEXT_CANCEL_SEND);
		mHeaderLayout.setVisibility(View.GONE);
		mVoiceSpeaker.setVisibility(View.VISIBLE);
		this.mVoiceToShortView.setVisibility(View.GONE);
		mIsAbandon = false;
		mIsOver = false;
		mIsAlert = false;
		this.setSweepAngle(0);
		this.mIsShowVolumns = true;
	}
	
	
	class HeaderLayout extends LinearLayout{

		public TextView mTextView = null;
		public HeaderLayout(Context context) {
			super(context);
			mInflater.inflate(R.layout.voice_pop_message, this);
			mTextView = (TextView)this.findViewById(R.id.pop_message);
		}
		
	}
	
}

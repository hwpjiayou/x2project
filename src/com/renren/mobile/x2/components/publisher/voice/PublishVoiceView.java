package com.renren.mobile.x2.components.publisher.voice;

import java.io.File;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.face.IVoiceBg;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread;
import com.renren.mobile.x2.components.chat.view.VoiceBgImageView;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PlayerThread;
import com.renren.mobile.x2.utils.voice.VoiceManager;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnPlayerListenner;
import com.renren.mobile.x2.utils.voice.PlayerThread.PlayRequest;

public final class PublishVoiceView extends LinearLayout implements android.view.View.OnTouchListener,OnPlayerListenner,IVoiceBg{

	
	private  float mSweepAngle = 360.0f;
	private  int LINE_COLOR = 0xff31b5f4;
	private  int ALERT_COLOR = 0xffef5c30;
	private int disenableColor = 0xffd4d3d3;
	private  int mColor = LINE_COLOR;
	
	/**弹出的消息提示 */
	public TextView mAlertTextView = null;
	/**弹出的消息提示 */
	public TextView mLeftTimeTextView = null;
	/**语音主体控件的父控件 */
	View mVoiceRoot = null;
	/**语音背景托盘图片 */
	VoiceBgImageView mBackImage = null;
	/**4个音量图片 */
	View[] mVoices = new View[4];
	/**语音垃圾桶 */
	View mRubbish = null;
	/**录音太短时候 中间的带叹号的图片 */
	View mVoiceToShortView = null;
	View mVoiceStop;
	View mVoicePlay;
	View mVoiceDefault;
	View mVoiceSpeaker = null;
	Button mVoiceBottomBt;
	
	TextView mTimeTextView = null;
	
	LayoutInflater mInflater = null;
	
	/**是否滑动到了录音上面 */
	private boolean mIsAbandon = false;
	/**录音是否结束 */
	private boolean mIsOver = true;
	private  String TEXT_CANCEL_SEND = ChatUtil.getText(R.string.chat_record_cancel);
	private  String TEXT_RECORD_LESS = ChatUtil.getText(R.string.chat_record_short);
	private  String TEXT_RECORD_OVER = ChatUtil.getText(R.string.chat_record_end);
	private  String TEXT_MOVE_TO_CANCEL = ChatUtil.getText(R.string.chat_record_scroll);
	public static final int MAX_TIME = 60;
	public static final int ALERT_TIME = 55;
	private boolean mIsAlert = false;
	
	/**录音大于55秒后不在现实音量 */
	private boolean mIsShowVolumns = true;
	private boolean init = false;
	private boolean mIsToShort = false;
	private boolean mIsPlaying = false;
	
	private byte mVolumn = 0;
	
	private String mRecordFile;
	private int mLastTime;
	private long mLastShowAngleTime;
	private float mLastAngle;
	private long playStartTime;
	
	
	IVoiceChanged voiceChangedListenter;
	
	private static final int MESSAGE_DRAW = 0;
	private static final int MESSAGE_PLAY = 1;
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(Logger.mDebug){
				Logger.logd(Logger.PLAY_VOICE, "handleMessage what = "+msg.what);
			}
			switch (msg.what) {
			case MESSAGE_DRAW:
				handlerRecord();
				break;
			case MESSAGE_PLAY:
				handlerPlay();
				break;
			}
		}
		
		private void handlerPlay(){
			int time = mLastTime - (int)((System.currentTimeMillis()-playStartTime)/1000);
			if(time<0){
				time = 0;
			}
			int angle =(int)(mLastAngle - (6*((System.currentTimeMillis()-playStartTime)/1000.0)));
			if(angle<0){
				angle = 0;
			}
			
			if(time>ALERT_TIME){
				mColor = ALERT_COLOR;
			}else{
				mColor = LINE_COLOR;
			}
			setVolumn();
			setLeftTime(time);
			setSweepAngle(angle);
			postInvalidate();
			mBackImage.postInvalidate();
		}

		private void handlerRecord() {
			int time = getTime();
			int angle = getAngle();
			if(Logger.mDebug){
				Logger.logd(Logger.PLAY_VOICE, "time="+time+"#angle="+angle);
			}
			boolean isShowToCancel =  isToCancel();
			if(time>ALERT_TIME){ //时间大于55秒
				mIsShowVolumns = false;
				mColor = ALERT_COLOR;
				//mAlertImage.setVisibility(View.VISIBLE);
				if(!mIsAbandon){
					mTimeTextView.setVisibility(View.VISIBLE);
				}
				mAlertTextView.setVisibility(View.VISIBLE);
				if(!mIsAbandon){
					mAlertTextView.setText(TEXT_RECORD_OVER);
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
				if(!mIsAbandon){
					mVoiceSpeaker.setVisibility(View.VISIBLE);
					if(isShowToCancel){
						removeCallbacks(mDissmissHeader);
						mAlertTextView.setText(TEXT_MOVE_TO_CANCEL);
						mIsAlert=true;
						mAlertTextView.setVisibility(View.VISIBLE);
						postDelayed(mDissmissHeader, 2000);
					}
				}
				mTimeTextView.setVisibility(View.GONE);
			}
			setTime(time);
			setSweepAngle(angle);
			postInvalidate();
			mBackImage.postInvalidate();
			
		};
	};
	
	Runnable mDissmissHeader =new Runnable() {
		public void run() {
			if(!mIsAbandon){
				mAlertTextView.setVisibility(View.INVISIBLE);
				mIsAlert=false;
			}
		}
	};
	
	public PublishVoiceView(Context context, AttributeSet attrs) {
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
		this.setBackgroundColor(0x00000000);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.pubshish_voice_view, this);
		mTimeTextView = (TextView)this.findViewById(R.id.voice_time);
		mBackImage = (VoiceBgImageView)this.findViewById(R.id.back_image);
		mBackImage.setVoiceBg(this);
		mVoiceRoot = this.findViewById(R.id.voice_root);
		mRubbish = this.findViewById(R.id.bin);
		mVoiceSpeaker = this.findViewById(R.id.voice_speaker);
		mVoiceToShortView = this.findViewById(R.id.voice_to_short);
		mVoiceStop = this.findViewById(R.id.voice_stop);
		mVoiceStop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Logger.mDebug) {
					Logger.logd(Logger.PLAY_VOICE, "点击结束播放 mRecordFile="
							+ mRecordFile);
				}
				PlayerThread.getInstance().stopAllPlay();
				VoiceDownloadThread.getInstance().stopToAutoPlay();
				VoiceManager.getInstance().stopAllPlay();
				VoiceManager.getInstance().stopRecord(false);
			}
		});
		mVoicePlay = this.findViewById(R.id.voice_play);
		mVoicePlay.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Logger.mDebug) {
					Logger.logd(Logger.PLAY_VOICE, "开始播放语音 mRecordFile="
							+ mRecordFile);
				}
				if (!TextUtils.isEmpty(mRecordFile)) {
					File file = new File(mRecordFile);
					if (file.exists() && file.length() > 0) {
						final PlayRequest request = new PlayRequest();
						request.mAbsVoiceFileName = mRecordFile;
						request.mPlayListenner = PublishVoiceView.this;
						VoiceDownloadThread.getInstance().stopToAutoPlay();
						if (PlayerThread.getInstance().forceToPlay(request)) {
							PlayerThread.getInstance().onAddPlay(request);
						}
					}
				}
				
			}
		});
		mVoiceDefault = this.findViewById(R.id.publisher_voice);
		mVoiceBottomBt = (Button)this.findViewById(R.id.publish_voice_bt);
		mVoiceBottomBt.setOnTouchListener(this);
		mVoiceBottomBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Logger.mDebug){
					Logger.logd("录音按钮点击 mRecordFile="+mRecordFile);
				}
				if(!TextUtils.isEmpty(mRecordFile)){
					try {
						File file = new File(mRecordFile);
						if(file.exists()){
							file.delete();
						}
					} catch (Exception e) {
					}finally{
						mRecordFile = null;
						delRecordFile();
						mVoiceBottomBt.setText(R.string.chat_voice_talk);
						initBeforeRecordState();
					}
				}
			}
		});
		
		mLeftTimeTextView = (TextView)this.findViewById(R.id.publish_voice_time);
		this.setOnClickListener(null);
		mAlertTextView = (TextView)this.findViewById(R.id.pop_message);
		mAlertTextView.setVisibility(View.INVISIBLE);
		mVoices[0] = this.findViewById(R.id.voice1);
		mVoices[1] = this.findViewById(R.id.voice2);
		mVoices[2] = this.findViewById(R.id.voice3);
		mVoices[3] = this.findViewById(R.id.voice4);
		init=true;
//		if(!TextUtils.isEmpty(mRecordFile)){
//			endRecordState();
//		}else{
//			initBeforeRecordState();
//		}
		mRecordFile = null;
		initBeforeRecordState();
	}
	
	boolean isToCancel(){
		if(this.mListener!=null){
			return this.mListener.onIsShowMoveToCancel();
		}
		return false;
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
	
	private void setVolumn(){
		if(System.currentTimeMillis() - mLastShowAngleTime > 200){
			mLastShowAngleTime = System.currentTimeMillis();
			for(View v:mVoices){
				v.setVisibility(View.GONE);
			}
			if(mVolumn>=4){
				mVolumn=0;
			}
			mVoices[mVolumn].setVisibility(View.VISIBLE);
			mVolumn++;
		}
	}
		
	@Override
	public void draw(Canvas canvas) {
		//this.drawBackground(canvas);
		super.draw(canvas);
		//this.drawCircle(canvas);
		if(this.getVisibility()==View.VISIBLE && !mIsOver){
			Message m = mHandler.obtainMessage(MESSAGE_DRAW);
			mHandler.sendMessageDelayed(m,30);
		}
		if(mIsPlaying){
			Message m = mHandler.obtainMessage(MESSAGE_PLAY);
			mHandler.sendMessageDelayed(m,30);
		}
	}
	
	public void dismiss(){
		mHandler.removeMessages(MESSAGE_DRAW);
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
        mAlertTextView.setVisibility(View.VISIBLE);
        Rect hitRect = this.obtainRect();
        mBackImage.getGlobalVisibleRect(hitRect);
        return hitRect.contains(x, y);
    }
	
	public void setTime(int time){
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE,"settime="+time);
		}
		mTimeTextView.setText(time+"s");
		mTimeTextView.requestLayout();
	}
	
	private void setLeftTime(int time){
		mLeftTimeTextView.setText(time+"s");
	}
	
	public void setSweepAngle(float angle){
		mSweepAngle = angle;
	}

	OnTouchListener mTouchListener;
	public void setOnPreTouchListener(OnTouchListener listener){
		mTouchListener = listener;
	}
	
	
	long mPreClickTime = 0L;
	private static final long MIN_OFFSET_TIME = 1500L;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(!TextUtils.isEmpty(mRecordFile)){
			if(Logger.mDebug){
				Logger.logd(Logger.RECORD,"录音文件还存在 必须先删除能录制");
			}
			return false;
		}
		
		if(Logger.mDebug){
			Logger.logd(Logger.RECORD,"onTouch switch="+event.getAction());
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(Logger.mDebug){
				Logger.logd(Logger.RECORD,"action_down");
			}
			if((System.currentTimeMillis()-mPreClickTime)<MIN_OFFSET_TIME){
				CommonUtil.toast("请休息一会儿");
				return false;
			}
			mIsToShort = false;
			this.onStart();
			///this.mIsShow = true;
			break;
		case MotionEvent.ACTION_MOVE:
			int x = (int)event.getRawX();
			int y = (int)event.getRawY();
			Rect r = new Rect();
			mVoiceRoot.getGlobalVisibleRect(r);
			if(Logger.mDebug){
				Logger.logd(Logger.RECORD,"action_move "+x+","+y+","+r+"\r\n"+mVoiceRoot.getScrollX()+","+mVoiceRoot.getScrollY());
			}
			if(isContain(x, y)){
				this.mTimeTextView.setVisibility(View.GONE);
				this.mVoiceSpeaker.setVisibility(View.GONE);
				this.mRubbish.setVisibility(View.VISIBLE);
				mAlertTextView.setVisibility(View.VISIBLE);
				mAlertTextView.setText(TEXT_CANCEL_SEND);
				if(mIsAlert==true) mIsAlert=false;
				this.mIsAbandon = true;
			}else{
				this.mRubbish.setVisibility(View.GONE);
				if(!mIsAlert){
					mAlertTextView.setVisibility(View.INVISIBLE);
				}
				this.mIsAbandon = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(Logger.mDebug){
				Logger.logd(Logger.RECORD,"action_up");
			}
			mPreClickTime = System.currentTimeMillis();
			this.mIsOver = true;
			if(mIsAbandon){ //录音取消
				initBeforeRecordState();
			}else if(isLessTime()){//录音时间太短
				mIsToShort =  true;
			}
			dismiss();
			onVoiceEnd();
			break;
		}
		return false;
	}
	


	public static interface MonitorListener{
		public void onVoiceStart();
		public int onGetTime();
		public int onGetAngle();
		public boolean onIsLessMinTime();
		public void onVoiceEnd();
		public boolean onIsShowMoveToCancel();
	}
	
	public void onVoiceEnd(){
		if(mListener!=null){
			mListener.onVoiceEnd();
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
	
	
	

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE,"view ="+changedView.toString()+"#visibility="+visibility);
		}
		if(changedView == this){
			if(visibility == View.GONE){
				mIsOver = true;
				mIsPlaying = false;
			}
			if(TextUtils.isEmpty(mRecordFile)){
				initBeforeRecordState();
			}else{
				endRecordState();
			}
		}
	}
	
	
		
	
	/******************各种状态*************************/
	
	private void initBeforeRecordState(){
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE,"initBeforeRecordState init="+init);
		}
		
		mIsOver = true;
		mIsPlaying = false;
		
		if(!init||mAlertTextView==null){
			return ;
		}
		mAlertTextView.setVisibility(View.INVISIBLE);
		mBackImage.setVisibility(View.INVISIBLE);
		mVoices[0].setVisibility(View.GONE);
		mVoices[1].setVisibility(View.GONE);
		mVoices[2].setVisibility(View.GONE);
		mVoices[3].setVisibility(View.GONE);
		mRubbish.setVisibility(View.GONE);
		mVoiceToShortView.setVisibility(View.GONE);
		mVoiceStop.setVisibility(View.GONE);
		mVoicePlay.setVisibility(View.GONE);
		mVoiceDefault.setVisibility(View.VISIBLE);
		mVoiceSpeaker.setVisibility(View.GONE);
		mTimeTextView.setVisibility(View.GONE);
		mLeftTimeTextView.setVisibility(View.INVISIBLE);
		
		this.setTime(0);
		this.setLeftTime(0);
		this.setSweepAngle(0);
		
	}
	
	public void  initStartRecordState(){
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE,"initStartRecordState init="+init);
		}
		if(!init||mAlertTextView==null){
			return ;
		}
		mAlertTextView.setVisibility(View.INVISIBLE);
		mBackImage.setVisibility(View.VISIBLE);
		mVoices[0].setVisibility(View.VISIBLE);
		mVoices[1].setVisibility(View.GONE);
		mVoices[2].setVisibility(View.GONE);
		mVoices[3].setVisibility(View.GONE);
		mRubbish.setVisibility(View.GONE);
		mVoiceToShortView.setVisibility(View.GONE);
		mVoiceStop.setVisibility(View.GONE);
		mVoicePlay.setVisibility(View.GONE);
		mVoiceDefault.setVisibility(View.GONE);
		mVoiceSpeaker.setVisibility(View.VISIBLE);
		mTimeTextView.setVisibility(View.GONE);
		mLeftTimeTextView.setVisibility(View.INVISIBLE);
		
		mColor = LINE_COLOR;
		mIsAbandon = false;
		mIsOver = false;
		mIsAlert = false;
		this.setSweepAngle(0);
		this.mIsShowVolumns = true;
		this.setTime(0);
	}
	
	 private void playRecordState(){
		   if(Logger.mDebug){
				Logger.logd(Logger.PLAY_VOICE,"playrecordState");
			}
			if(!init||mAlertTextView==null){
				return ;
			}
			mAlertTextView.setVisibility(View.INVISIBLE);
			mBackImage.setVisibility(View.VISIBLE);
			mVoices[0].setVisibility(View.GONE);
			mVoices[1].setVisibility(View.GONE);
			mVoices[2].setVisibility(View.GONE);
			mVoices[3].setVisibility(View.VISIBLE);
			mRubbish.setVisibility(View.GONE);
			mVoiceToShortView.setVisibility(View.GONE);
			mVoiceStop.setVisibility(View.VISIBLE);
			mVoicePlay.setVisibility(View.GONE);
			mVoiceDefault.setVisibility(View.GONE);
			mVoiceSpeaker.setVisibility(View.GONE);
			mTimeTextView.setVisibility(View.GONE);
			mLeftTimeTextView.setVisibility(View.VISIBLE);
			mVoiceBottomBt.setTextColor(disenableColor);
			//Color.LTGRAY
			mVoiceBottomBt.setEnabled(false);
			//mVoiceBottomBt.setTextColor(Color.GRAY);
	}

	
	private void endRecordState() {
		if (Logger.mDebug) {
			Logger.logd(Logger.PLAY_VOICE, "endRecordState");
		}
		
		mIsOver = true;
		mIsPlaying = false;
		
		if (!init || mAlertTextView == null) {
			return;
		}
		mAlertTextView.setVisibility(View.INVISIBLE);
		mBackImage.setVisibility(View.VISIBLE);
		mVoices[0].setVisibility(View.GONE);
		mVoices[1].setVisibility(View.GONE);
		mVoices[2].setVisibility(View.GONE);
		mVoices[3].setVisibility(View.VISIBLE);
		mRubbish.setVisibility(View.GONE);
		mVoiceToShortView.setVisibility(View.GONE);
		mVoiceStop.setVisibility(View.GONE);
		mVoicePlay.setVisibility(View.VISIBLE);
		mVoiceDefault.setVisibility(View.GONE);
		mVoiceSpeaker.setVisibility(View.GONE);
		mTimeTextView.setVisibility(View.GONE);
		mLeftTimeTextView.setVisibility(View.VISIBLE);
		mVoiceBottomBt.setEnabled(true);
		mVoiceBottomBt.setTextColor(R.drawable.inputbar_record_botton_text_color);
		if (!TextUtils.isEmpty(mRecordFile)) {
			mVoiceBottomBt.setText(R.string.chat_del);
		}
		setLeftTime(mLastTime);

	}
	
	private void delRecordFile(){
		 mRecordFile =null;
		if(voiceChangedListenter!=null){
			voiceChangedListenter.voiceDel();
		}
	}
	
	private void recordToShortState() {
		   if(Logger.mDebug){
				Logger.logd(Logger.PLAY_VOICE,"recordToShortState");
			}
		    delRecordFile();
			if(!init||mAlertTextView==null){
				return ;
			}
			mAlertTextView.setVisibility(View.VISIBLE);
			mAlertTextView.setText(TEXT_RECORD_LESS);
			
			mBackImage.setVisibility(View.VISIBLE);
			mVoices[0].setVisibility(View.GONE);
			mVoices[1].setVisibility(View.GONE);
			mVoices[2].setVisibility(View.GONE);
			mVoices[3].setVisibility(View.GONE);
			mRubbish.setVisibility(View.GONE);
			mVoiceToShortView.setVisibility(View.VISIBLE);
			mVoiceStop.setVisibility(View.GONE);
			mVoicePlay.setVisibility(View.GONE);
			mVoiceDefault.setVisibility(View.GONE);
			mVoiceSpeaker.setVisibility(View.GONE);
			mTimeTextView.setVisibility(View.GONE);
			mLeftTimeTextView.setVisibility(View.INVISIBLE);
			
			this.setTime(0);
			this.setLeftTime(0);
			this.setSweepAngle(0);
   }
	
	private void endToLongState() {
		if (Logger.mDebug) {
			Logger.logd(Logger.PLAY_VOICE, "endToLongState");
		}
		
		mIsOver = true;
		mIsPlaying = false;
		delRecordFile();
		if (!init || mAlertTextView == null) {
			return;
		}
		mAlertTextView.setVisibility(View.VISIBLE);
		mAlertTextView.setText(R.string.chat_voice_long);
		
		mBackImage.setVisibility(View.VISIBLE);
		mVoices[0].setVisibility(View.GONE);
		mVoices[1].setVisibility(View.GONE);
		mVoices[2].setVisibility(View.GONE);
		mVoices[3].setVisibility(View.GONE);
		mRubbish.setVisibility(View.GONE);
		mVoiceToShortView.setVisibility(View.VISIBLE);
		mVoiceStop.setVisibility(View.GONE);
		mVoicePlay.setVisibility(View.GONE);
		mVoiceDefault.setVisibility(View.GONE);
		mVoiceSpeaker.setVisibility(View.GONE);
		mTimeTextView.setVisibility(View.GONE);
		mLeftTimeTextView.setVisibility(View.INVISIBLE);
		
		this.setTime(0);
		this.setLeftTime(0);
		this.setSweepAngle(0);
	}

	/********************语音播放******************* */
	@Override
	public void onPlayStart() {
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE,"开始播放语音");
		}
		playStartTime = System.currentTimeMillis();
		mIsPlaying =  true;
		mSweepAngle = mLastAngle;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				playRecordState();
			}
		});
		
	}

	@Override
	public void onPlayPlaying() {
		
	}

	@Override
	public void onPlayStop() {
		if(Logger.mDebug){
			Logger.logd(Logger.PLAY_VOICE,"播放语音结束");
		}
		mIsPlaying =  false;
		mHandler.removeMessages(MESSAGE_PLAY);
		mSweepAngle = mLastAngle;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				endRecordState();
			}
		});
	}
	
	
	/**
	 * 录音停止后的回调
	 * @param isEncodeSuccess 录音是否成功
	 * @param mIsSuccess  是否滑动到了取消区域
	 * @param absFileName 
	 */
	public void endEncorderEnd(boolean isEncodeSuccess,
			String absFileName , int time) {
		
		mIsOver = true;
		mIsPlaying = false;
		
		if(!isEncodeSuccess){//
			CommonUtil.toast(R.string.chat_voice_error);
			initBeforeRecordState();
			delRecordFile();
			mIsToShort = false;
			return ;
		}
		
		if(mIsAbandon){ //录音取消
			initBeforeRecordState();
		}else if(mIsToShort){//录音时间太短
			recordToShortState();
		}else {
			if(time>=60){
				time = 60;
			}
//			if(time>=60){
//				endToLongState();
//			}else
			{
				mRecordFile=absFileName;
				if(voiceChangedListenter!=null){
					voiceChangedListenter.voiceAdded();
				}
				mLastAngle = mSweepAngle;
				mLastTime = time;
				endRecordState();
			}
		}
		mIsToShort = false;
	}
	
	public  void setVoiceChangedListenter(IVoiceChanged changeListenter){
		this.voiceChangedListenter = changeListenter;
	}

	public String getRecordFile(){
		return mRecordFile;
	}



	@Override
	public int getColor() {
		return mColor;
	}


	@Override
	public float getDrawAngle() {
		return mSweepAngle;
	}
}
	
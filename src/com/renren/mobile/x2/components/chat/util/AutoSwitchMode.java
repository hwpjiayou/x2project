package com.renren.mobile.x2.components.chat.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;

import com.renren.mobile.x2.utils.RRSharedPreferences;
import com.renren.mobile.x2.utils.SystemService;
import com.renren.mobile.x2.utils.voice.VoiceManager;


public class AutoSwitchMode {
	
	/*
	 * 标志是否为Google标准的常规机型：mValueMax == MaximumRange
	 * Login检测：此类机型直接用MaximumRange判断，其它机型牺牲第一次传感器切换为代价自学习传感器参数
	 * true：标准机型  false：自学习机型
	 */
	public static final String PROXIMITY_SENSOR_FLAG_NORMAL = "proximity_sensor_flag_normal"; 
	
	// 默认值为-1 无自动切换功能
	// public static final String PROXIMITY_SENSOR_MAXIMUMRANGE = "proximity_sensor_maximumRange";
	// public static final float PROXIMITY_SENSOR_MAXIMUMRANGE_DEFAULT = -1;
	
	// public static final String PROXIMITY_SENSOR_FLAG = "proximity_sensor_flag";
	// public static final boolean PROXIMITY_SENSOR_FLAG_AVAILABLE = true;
	// public static final boolean PROXIMITY_SENSOR_FLAG_UNAVAILABLE = false;
	
	// public static final String PROXIMITY_SENSOR_MIN_FLAG = "proximity_sensor_min_flag";
	
	// public static final String PROXIMITY_SENSOR_MINIMUMRANGE = "proximity_sensor_minimumRange";

	private SensorEventListener sensorListener;

	private Context context;
	private boolean mIsCloseEar = false;
	private boolean mIsRegistor = false;
	// private float oldValue = -999f; 
	private float mValue = -1f;
	private float mValueTemp = -1f;
	private boolean mHeadsetMode;
	private RRSharedPreferences rrSharedPreferences;
	
	private BroadcastReceiver headSetReceiver = new BroadcastReceiver() {  
	      
		@Override
		public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();  
              
              
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
            	//headphone plugged  
                if(intent.getIntExtra("state", 0) == 1){  
					mHeadsetMode = true;
                  
                //headphone unplugged  
                }else{  
                	mHeadsetMode = false;
                }
            }
		}
	};
	
	public AutoSwitchMode(Context context) {
		this.context = context;
		rrSharedPreferences = new RRSharedPreferences(context);
	}

	public void registorSensor() {
		this.context.registerReceiver(headSetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		if(mIsRegistor){
			return;
		}
		SensorManager sm = SystemService.sSensorManager;
		final Sensor proximitySensor = sm
				.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		if (proximitySensor != null) {
			if (sensorListener == null) {
				// 如果没有插入耳机，进入页面时设置为外放；然后根据传感器来设置是否外放
				sensorListener = new SensorEventListener() {
					@Override
					public void onSensorChanged(SensorEvent event) {
						// Android sixin 2.0 策略 根据传感器变化来判断靠近远离，缺点将导致第一次手一直遮住时将不会切换
						if (Sensor.TYPE_PROXIMITY == event.sensor.getType()) {
							mValue = event.values[SensorManager.DATA_X];
							
							if (rrSharedPreferences.getBooleanValue(PROXIMITY_SENSOR_FLAG_NORMAL, false)) {
								// 标准机型
								float maximumRange = event.sensor.getMaximumRange();
								if (mValue < maximumRange && !mIsCloseEar && !mHeadsetMode) {
									// 听筒模式。
									setEarphoneStatus(true);
									VoiceManager.getInstance().replay();
									onCloseEar();
								} else if(mValue >= maximumRange && mIsCloseEar){
									setEarphoneStatus(false);
									VoiceManager.getInstance().replay();
									onOverEar();
								}
							} else {
								// 自学习机型
								if (mValue == event.sensor.getMaximumRange()) {
									rrSharedPreferences.putBooleanValue(PROXIMITY_SENSOR_FLAG_NORMAL, true);
								}
								if (mValue > mValueTemp && mIsCloseEar) {
									// 切换到扬声器模式
									setEarphoneStatus(false);
									VoiceManager.getInstance().replay();
									onOverEar();
								} else if ( mValue < mValueTemp  && !mIsCloseEar && !mHeadsetMode) {
									// 切换到听筒模式
									setEarphoneStatus(true);
									VoiceManager.getInstance().replay();
									onCloseEar();
								}
								mValueTemp = mValue;
							}
						}
					}
					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {

					}
				};
				sm.registerListener(sensorListener, proximitySensor,
						SensorManager.SENSOR_DELAY_UI);
			}
		}
		mIsRegistor = true;
	}

	public void unregisterSensor() {
		this.context.unregisterReceiver(headSetReceiver);
		if(!mIsRegistor){
			return;
		}
		SensorManager sm = SystemService.sSensorManager;
		if (sensorListener != null) {
			sm.unregisterListener(sensorListener);
			sensorListener = null;
		}
		mIsCloseEar = false;
		mIsRegistor = false;
		mValue = -1f;
		// oldValue = -999f;
	}

	/**
	 * @param isCall :当为true时表示开启听筒否则为扬声器
	 * @return 听筒切换
	 * */
	public void setEarphoneStatus(boolean isCall) {
		this.mIsCloseEar = isCall;
		if (isCall) {
			VoiceManager.getInstance().switchPlayMethod(AudioManager.STREAM_VOICE_CALL);
		} else {
			VoiceManager.getInstance().switchPlayMethod(AudioManager.STREAM_MUSIC);
		}
	}
	public static interface OnProximityChangeListenner{
		public void onCloseEar();
		public void onOverEar();
	}
	private  OnProximityChangeListenner mListenner = null;
	public void setOnProximityChangeListenner(OnProximityChangeListenner listenner){
		mListenner = listenner;
	}
	public void onCloseEar(){
		if(mListenner!=null){
			mListenner.onCloseEar();
		}
	}
	public void onOverEar(){
		if(mListenner!=null){
			mListenner.onOverEar();
		}
	}
	
}
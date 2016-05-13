package com.renren.mobile.x2.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.DropBoxManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * @author dingwei.chen
 * @说明 系统服务 
 * */
public final class SystemService {

	/***
	 * @see #WINDOW_SERVICE
	     * @see #KEYGUARD_SERVICE
	     * @see android.app.KeyguardManager
	     * @see #LOCATION_SERVICE
	     * @see android.location.LocationManager
	     * @see #SEARCH_SERVICE
	     * @see android.app.SearchManager
	     * @see #SENSOR_SERVICE
	     * @see android.hardware.SensorManager
	     * @see #STORAGE_SERVICE
	     * @see android.os.storage.StorageManager
	     * @see #VIBRATOR_SERVICE
	     * @see android.os.Vibrator
	     * @see #CONNECTIVITY_SERVICE
	     * @see android.net.ConnectivityManager
	     * @see #WIFI_SERVICE
	     * @see android.net.wifi.WifiManager
	     * @see #AUDIO_SERVICE
	     * @see android.media.AudioManager
	     * @see #TELEPHONY_SERVICE
	     * @see android.telephony.TelephonyManager
	     * @see #INPUT_METHOD_SERVICE
	     * @see android.view.inputmethod.InputMethodManager
	     * @see #UI_MODE_SERVICE
	     * @see android.app.UiModeManager
	 */
	
	@ServiceMapping(serviceName=Context.LAYOUT_INFLATER_SERVICE)
	public static LayoutInflater sInflaterManager = null;//布局管理器
	
	@ServiceMapping(serviceName=Context.NOTIFICATION_SERVICE)
	public static NotificationManager sNotificationManager = null;//notify管理
	
	@ServiceMapping(serviceName=Context.INPUT_METHOD_SERVICE)
	public static InputMethodManager sInputMethodManager = null;//输入法管理
	
	@ServiceMapping(serviceName=Context.POWER_SERVICE)
	public static PowerManager sPowerManager = null;//电源管理
	
	@ServiceMapping(serviceName=Context.TELEPHONY_SERVICE)
	public static TelephonyManager sTelephonyManager = null;//电话管理
	
	@ServiceMapping(serviceName=Context.ACTIVITY_SERVICE)
	public static ActivityManager sActivityManager = null;//Activity管理器
	
	@ServiceMapping(serviceName=Context.ALARM_SERVICE)
	public static AlarmManager sAlarmManager = null;//定时提醒管理器
	
	@ServiceMapping(serviceName=Context.ALARM_SERVICE)
	public static LocationManager sLocationManager = null;//定位管理器
	
	@ServiceMapping(serviceName=Context.SENSOR_SERVICE)
	public static SensorManager sSensorManager = null;//传感器管理器
	
	@ServiceMapping(serviceName=Context.WINDOW_SERVICE)
	public static WindowManager sWindowsManager = null;//Window管理器
	
	@ServiceMapping(serviceName=Context.WIFI_SERVICE)
	public static WifiManager sWifiManager = null;//wifi管理器
	
	@ServiceMapping(serviceName=Context.AUDIO_SERVICE)
	public static AudioManager sAudioManager = null;//Audio管理器
	
	@ServiceMapping(serviceName=Context.CONNECTIVITY_SERVICE)
	public static ConnectivityManager sConnectivityManager = null;//网络管理器
	
	@ServiceMapping(serviceName=Context.DROPBOX_SERVICE)
	public static DropBoxManager sDropBoxManager = null;//网络管理器
	
	@ServiceMapping(serviceName=Context.CLIPBOARD_SERVICE)
	public static ClipboardManager sClipboardManager = null; //剪切板
	
	public static PackageManager sPackageManager = null;//包管理器
}

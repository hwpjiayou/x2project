/**
 * 
 */
package com.renren.mobile.x2.utils.location;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

/**
 * @author xiaochao.zheng
 *	一个精简版的定位模块,逻辑是先进行gps定位，再进行network和wifi以及附近的wifi定位
 */
public class NewLocationManager {
	private static final long MINTIME = 1000*5*60;//5分钟内只能定位一次
	private static final String tag = "location";
	private TelephonyManager mTelephonyManager;
	private LocationManager  mLocationManager;
	private Context mContext;
	private CellLocation mCellLocation;
	private static NewLocationManager mInstance ;
	private WifiManager mWifiManager;
	private Location location;
	private List<ScanResult> wifiList ;
	private boolean isLocationSuccess;
	private Timer mTimer;
	private long lastLocationTime;
	private long delaytime = 10000;
	private NewLocationListener mListener;
	private StringBuffer JsonBuffer;
	public LocationState mLocationstate;
	/**
	 * 
	 */
	private NewLocationManager(Context c) {
		this.mContext = c;
		mTimer = new Timer();
		this.initData();
	}
	public static NewLocationManager getInstance(Context conetxt){
		if(mInstance==null){
			mInstance = new NewLocationManager(conetxt);
		}
		return mInstance;
	}
	/****
	 * 开始定位
	 */
	public boolean startLocation(NewLocationListener listener){
		Log.d(tag,"startLocation .. ");
		mListener = listener;
//		if(lastLocationTime == 0){
//			lastLocationTime = System.currentTimeMillis();
//		}else{
//			if((System.currentTimeMillis()-lastLocationTime) > MINTIME){
//				lastLocationTime = System.currentTimeMillis();
//			}else{
//				return false;
//			}
//		}
		if(!isLocationEnable()){
			return false;
		}else if(listener == null){
			return true;
		}
		
		startGPS();
		startBaseStation();
		getWifiInfo();
		getWifiNeibor();
		return true;
		
	}
	
	
	private boolean isLocationEnable(){
		if(mLocationManager == null){
			return false;
		}
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	private void initData(){
		mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}
	private void startGPS(){
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			Toast.makeText(mContext, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
			Log.d(tag,""+"please open GPS");
//			return;
		}
		location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
//				Log.d(tag,"before requsetLoactionUpdates");
//				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mGPSListener);
//				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mNetWorkListener);				
//				Log.d(tag,"requestLocationUpdates ");
//				mTimer.schedule(new TimerTask() {
//					
//					@Override
//					public void run() {
//						removeLocationListener();//如果到10秒钟消息还不回来就取消gps定位
////						if(!LocationState.getResult()){
////							mListener.onLocationFailed(0);
////						}
//						if(LocationState.isGpsendabled&&(!LocationState.isBaseStationenabled&&!LocationState.isNetWorkenabled&&!LocationState.isWifienabled)){
//							mListener.onLocationFailed(0);//当仅仅只有gps可用，并且gps也定位失败
//						}
//					}
//				}, delaytime);
			}
		});
	}
	private void startBaseStation(){
		mCellLocation = mTelephonyManager.getCellLocation();
		if(mCellLocation== null){
			return ;
			
		}
		if(mCellLocation instanceof GsmCellLocation){
			GsmCellLocation gsmCellLocation  = (GsmCellLocation) mCellLocation;
			Log.d(tag,"" + gsmCellLocation.getCid()+ " " + gsmCellLocation.getLac()+" "+gsmCellLocation.getPsc());
		}else if( mCellLocation instanceof CdmaCellLocation){
			CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) mCellLocation;
			Log.d(tag,"" + cdmaCellLocation.getBaseStationId()+" " + cdmaCellLocation.getNetworkId());
		}else{
			
		}
	}
	private void getWifiInfo(){
		WifiInfo info = mWifiManager.getConnectionInfo();
		if(info.getBSSID()==null){
			LocationState.isWifienabled=false;
			return ;
		}
		Log.d(tag,"getwifiInfo " + " " + info.getBSSID()+ " " + info.getIpAddress() +" " + info.getLinkSpeed() + " "+
		info.getMacAddress()+ " " + info.getRssi());
	}
	private void getWifiNeibor(){
		if(!LocationState.isWifienabled){
			mListener.onLocationFailed(1);
			return;
		}
		wifiList = mWifiManager.getScanResults();
		for(int i = 0 ; wifiList!=null  && i < wifiList.size();++i)
		{
			Log.d(tag,wifiList.get(i).BSSID+" " + wifiList.get(i).capabilities+ " " + wifiList.get(i).SSID);
		}
	}
	
	
	private LocationListener mGPSListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			isLocationSuccess = true;//表示一次定位Success			
			Log.d(tag,"gps onStatusChanged " +status); 
			ErrLog.Print("onStatusChanged   ");

		}
		
		@Override
		public void onProviderEnabled(String provider) {
			LocationState.isGpsendabled = false;
			ErrLog.Print("onProviderEnabled   ");
			Log.d(tag,"gps onProviderEnabled ");
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			LocationState.isGpsendabled = false;
			ErrLog.Print("onProviderDisabled   ");
			Log.d(tag,"gps onProviderDisabled ");			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Log.d(tag,"gps location " + location.getLatitude()+" " + location.getLatitude());
			ErrLog.Print("onLocationChanged   "+location.getLatitude()+" " + location.getLatitude());
			LocationSuccess(location, 0);
			removeLocationListener();
		}
	};
	private LocationListener mNetWorkListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			Log.d(tag,"net work onStatusChanged " +status); 
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			LocationState.isNetWorkenabled = true;
			Log.d(tag,"network onProviderEnabled ");
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			LocationState.isNetWorkenabled = false;
			Log.d(tag,"network onProviderDisabled ");			
		}
		
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.d(tag,"network location " + location.getLatitude()+" " + location.getLongitude());
			removeLocationListener();
			LocationSuccess(location, 1);
			
		}
	};
	private void removeLocationListener(){
		if(mGPSListener != null){
			mLocationManager.removeUpdates(mGPSListener);
		}
		if(mNetWorkListener != null){
			mLocationManager.removeUpdates(mNetWorkListener);
		}
	}
	private void LocationSuccess(Location location, int state){
		mListener.onLocationSuccess(location);
	}
	
	private JSONObject toJson(){
		return null;
	}
	
	protected static class LocationState{
		public static boolean isGpsendabled = true;
		public static boolean isNetWorkenabled = true;
		public static boolean isBaseStationenabled = true;
		public static boolean isWifienabled = true;
		public static boolean getResult(){
			return isGpsendabled||isNetWorkenabled||isBaseStationenabled||isWifienabled;
		}
	}
	
}

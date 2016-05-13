package com.renren.mobile.x2.utils.location;

import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.Methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * RenrenLocationManager
 * @author xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:06
 * 人人定位的全部逻辑都在这个式实现类里面实现，只需要掉这个类的方法就好，因为这是一个Manager功能的类所以用单例方
 */
public class RenrenLocationManager {

    /**
     * 拼接latlon json串手机系统信息的key
     */
    private static String OS_TYPE = "os_type";

    /**
     * 系统返回的location超时时间 用来判断getLastKnowLocation是否可用的
     */
    private static long SYSTEM_LOCATE_TIMEOUT = 3 * 60 * 1000;

    /**
     * 定位失败错误码，表示网络连接错误
     */
    public static int NO_NETWORK_CODE = 1;

    /**
     * 定位失败错误码，表示需要的定位依据不足，或者定位依据不可用
     */
    public static int NO_LOCATION_CONDITION = 2;

    /**
     * 定位失败错误码，服务器返回通知定位失败，错误码为20401
     */
    public static int LOCATION_FAIL = 3;

    /**
     * 定位失败错误码，其他原因造成的定位失败，基本是服务器下发除20401以外的错误码
     */
    public static int OTHER_FAIL = 4;

    /**
     * 默认请求预定位接口时每一页显示的数量
     */
    private static int DEFAULT_PAGE_SIZE = 20;

    /**
     * 默认请求预定位接口时返回的页码数
     */
    private static int DEFAULT_PAGE = 1;

    /**
     * 默认请求预定位接口时需要偏移
     */
    private static int DEFAULT_NEED2DEFLECT_CODE = 0;

    /**
     * 默认请求照片定位时需要偏移
     */
    private static int DEFAULT_EXIF_NEED2DEFLECT_CODE = 1;

    /**
     * 默认请求预定位接口时周边距离
     */
    private static int DEFAULT_RADIUS = 1000;

    /**
     * 单次定位GPS延迟控制的定时器
     */
    private Timer mGPSDelayTimer;

    /**
     * 单次定位开启GPS延续时间，单位秒
     */
    private int mGPSDelayTime = 3;

    /**
     * 连续定位timer
     */
    private Timer continuesTimer;

    /**
     * 系统定位监听器最小间隔时间，单位秒
     */
    private static final int LOCATE_UPDATE_MIN_TIME = 100;

    /**
     * 系统定位监听器最小距离间隔，单位米
     */
    private static final int LOCATE_UPDATE_MIN_DISTANCE = 100;

    /**
     * 连续定位的间隔时间
     */
    private static final int CONTINUE_LOCATE_INTERVAL = 5 * 60;

    /**
     * 得到一个RenrenLocationManager的实例，因为这里是单例
     */
    private static RenrenLocationManager instance;

    /**
     * 标志是否来至刷泡页
     */
    private boolean isFromShuaPao;

    /**
     * 标志是否来至poiList页
     */
    private boolean isFromPoiList;

    /**
     * 用来得到wifi信息以及周边wifi信息的manager
     */
    private WifiManager mWifiManager;

    /**
     * 用来得到系统定位信息（包括GPS和network）的manager
     */
    private LocationManager mLocationManager;

    /**
     * 用来得到基站以及周边基站信息的manager
     */
    private TelephonyManager mTelephonyManager;

    /**
     * 用来保存GPS定位信息的数据封装对象
     */
    private RenrenGPSData mGPSInfo;

    /**
     * 用来保存wifi信息的数据封装对象
     */
    private RenrenWifiData mWifiInfo;

    /**
     * 用来保存network定位信息的数据封装对象
     */
    private RenrenNetworkData mNetworkInfo;

    /**
     * 用来保存基站信息的数据封装对象
     */
    private RenrenStationData mStationInfo;

    /**
     * 用来保存周边基站信息的数据封装对象
     */
    private RenrenNeighborStationData mNeighborStationInfo;

    /**
     * 用来保存周边wifi信息的数据封装对象
     */
    private RenrenNeighborWifiData mNeighborWifiInfo;

    /**
     * 监听人人定位模块是否定位成功的listenr
     */
    private RenrenLocationListener mRenrenLocationListener;

    /**
     * 通过listener得到的系统GPS的location
     */
    private Location gpsListenerLocation;

    /**
     * 通过listener得到的系统network的location
     */
    private Location networkListenerLocation;

    /**
     * 通过getLastKnowLocation方法得到的GPS的location
     */
    private Location gpsLastKnowLocation;

    /**
     * 通过getLastKnowLocation方法得到的network的location
     */
    private Location networkLastKnowLocation;

    /**
     * 保存最终要的GPS的location
     */
    private Location gpsLocation;

    /**
     * 保存最终要的network的location
     */
    private Location networkLocation;

    /**
     * 调用系统方法等需要使用
     */
    private Context mContext;

    /**
     * 标志是否有wifi信息
     */
    private boolean isWificon;

    /**
     * 标志是否有基站信息
     */
    private boolean isStationCon;

    /**
     * 标志是否有可用GPS得到location
     */
    private boolean isHaveGPSinfo;

    /**
     * 标志是否有可用network得到location
     */
    private boolean isHaveNetworkInfo;

    /**
     * 标志是否有周边wifi信息
     */
    private boolean isWifiNeighbor;

    /**
     * 标志是否有周边基站信息
     */
    private boolean isStationNeighbor;

    /**
     * 标志是否已经取消定位
     */
    private boolean isLocationCancel;

    /**
     * 构造函数
     * @param context
     */
    private RenrenLocationManager(Context context){

        mContext = context;
        init(context);
    }

    /**
     * 这个用来开启系统定位模块的监听，包括GPS定位和network定位
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATE_UPDATE_MIN_TIME *1000, LOCATE_UPDATE_MIN_DISTANCE, gpsLocationListener);
                    break;
                case 1:
                    try{
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATE_UPDATE_MIN_TIME * 1000, LOCATE_UPDATE_MIN_DISTANCE, networkLocationListener);
                    } catch (Exception e){//部分手机或PAD缺少NetworkLocation.apk系统包，导致此处会抛出异常
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化各个属性
     * @param context 初始化属性时需要调用系统一些东西，需要这个参数
     */
    private void init(Context context) {

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.d("zxc","mWifiManager "+ this.mWifiManager.toString());
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.d("zxc","mLocationManger " + mLocationManager.toString());
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("zxc","mTelephonyManager "+mTelephonyManager.toString());
        mGPSInfo = new RenrenGPSData();
        Log.d("zxc","mGPSInfo " +mGPSInfo.getGpsLat() + " " + mGPSInfo.getGpsLon());//现在返回的是
        mWifiInfo = new RenrenWifiData();
        Log.d("zxc","mWifiInfo "+ mWifiInfo.toFormatString());
        mStationInfo = new RenrenStationData();
        
        mNetworkInfo = new RenrenNetworkData();
        mNeighborStationInfo = new RenrenNeighborStationData();
        mNeighborWifiInfo = new RenrenNeighborWifiData();
    }

    /**
     * 得到一个RenrenLocationManager的单例
     * @return 一个RenrenLocationManager的单例
     */
    public static RenrenLocationManager getRenrenLocationManager(Context context) {

        if(instance == null) {
            instance = new RenrenLocationManager(context);
        }
        return instance;
    }

    /**
     * 监听系统GPS返回的值
     */
    private LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gpsListenerLocation = location;
            Log.d("zxc","location data :  " + location.getLatitude()*1E6 + "  " + location.getLongitude()*1E6);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        	Log.d("zxc","onStatusChanged ");
        }

        @Override
        public void onProviderEnabled(String provider) {
        	Log.d("zxc","onProviderEnabled ");
        }

        @Override
        public void onProviderDisabled(String provider) {
        	Log.d("zxc","onProviderDisabled ");
        }
    };

    /**
     * 监听系统network返回的值
     */
    private LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            networkListenerLocation = location;
            Log.d("zxc","onLocationChanged " + location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("zxc","onStatusChanged ");

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("zxc","onProviderEnabled ");

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("zxc","onProviderDisabled ");
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.setClassName(“com.android.phone”, “com.android.phone.NetworkSetting”);
//            intent.
//            startActivity(intent);
        }
    };

    /**
     * RenrenLocationListener
     * @author xiaoguang.zhang
     * 其他地方可以通过实现这个接口来接收人人提供的定位成功，失败以及取消等消息
     */
    public interface RenrenLocationListener {

        /**
         * 成功定位
         * @param location 返回的定位信息
         */
        public void onLocationSucceeded(final RenrenLocationData location);

        /**
         * 定位失败
         * @param errorMessage 错误信息
         * @param errorCode 错误码，请查看RenrenLocationManager中定义的错误码的意义
         */
        public void onLocatedFailed(final String errorMessage, final int errorCode);

        /**
         * 取消定位
         */
        public void onLocatedCancel();
    }

    public interface GetLatLonListener {
        public void getLatLon(JSONObject LatLon);
    }

    /**
     * 启动单次定位，通过listener异步返回
     * @param listener 定位监听器，成功或失败都返回
     * @param fromShuaPao 是否来至刷泡页面，这个参数之后需要跟产品核对是否还需要
     * @param fromPoiList 是否来至poiList页面，如果是定位成功需要开启系统后台定位，不是则不用
     */
    public void startLocateSingle(RenrenLocationListener listener, boolean fromShuaPao, boolean fromPoiList) {

        this.mRenrenLocationListener = listener;
        isFromShuaPao = fromShuaPao;
        isFromPoiList = fromPoiList;

        if(!checkNet(mContext, false)) {
            mRenrenLocationListener.onLocatedFailed("请检查您的网络，无网络无法定位！", NO_NETWORK_CODE);
            return;
        }

        isLocationCancel = false;//每次定位前需要将这个值指定为false，否则影响定位结果

        if(isGPSEnable() || isNetworkProviderEnable()) {
            stopGPS();
            startGPS();
            startGPSDelayTimer();
        } else {
            startLocatinWithoutGPSAndNetwork();
        }
    }

    /**
     * 启动单次定位，通过listener异步返回
     * @param listener 定位监听器，成功或失败都返回
     */
    public void getLatLon(GetLatLonListener listener) {

        isLocationCancel = false;//每次定位前需要将这个值指定为false，否则影响定位结果

        if(isGPSEnable() || isNetworkProviderEnable()) {
            stopGPS();
            startGPS();
            getLatLonGPSDelayTimer(listener);
        } else {
            listener.getLatLon(getLatLonWithoutGPSAndNetwork());
        }
    }

    /**
     * 开始用除GPS和network以外的其他信息定位，有时候手机可能没有打开GPS模块或者其他原因，致使GPS和network不可用
     * 就没必要再等待他们的返回值直接用其他信息调用接口开始定位
     */
    private void startLocatinWithoutGPSAndNetwork() {

        isHaveGPSinfo = false; //因为这里没有用到GPS，所以务必赋值为false
        isHaveNetworkInfo = false; //因为这里没有用到network，所以务必赋值为false
        getStationInfo();
        getNeighborStationInfo();
        getWifiInfo();
        getNeighborWifiInfo();
        getLocationData();
    }

    /**
     * 开始得到所有能得到的数据进行定位，这个更准确，但前提是GPS和network至少有一个可以使用
     */
    private void startLoctionWithAll() {

        getStationInfo();
        getNeighborStationInfo();
        getWifiInfo();
        getNeighborWifiInfo();
        gpsLastKnowLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //得到最近一次通过GPS的到的location
//        Log.d("zxc","gpsLastKnowLocation " + gpsLastKnowLocation.getLongitude() + " " + gpsLastKnowLocation.getLatitude() );
        networkLastKnowLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //得到最近一次通过network的到的location
//        Log.d("zxc","networkLast knowLocation " + networkLastKnowLocation.getLatitude()+ "  " + networkLastKnowLocation.getLatitude());
        setLocation();
        getLocationInfo();
        getLocationData();
    }

    private JSONObject getLatLonWithAll() {

        getStationInfo();
        getNeighborStationInfo();
        getWifiInfo();
        getNeighborWifiInfo();
        gpsLastKnowLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //得到最近一次通过GPS的到的location
//        Log.d("zxc","gpsLastKnowLocation " + gpsLastKnowLocation.getLongitude() + " " + gpsLastKnowLocation.getLatitude() );
        networkLastKnowLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //得到最近一次通过network的到的location
//        Log.d("zxc","networkLast knowLocation " + networkLastKnowLocation.getLatitude()+ "  " + networkLastKnowLocation.getLatitude());
        setLocation();
        getLocationInfo();
        return getLatlonJson();
    }

    private JSONObject getLatLonWithoutGPSAndNetwork() {

        isHaveGPSinfo = false; //因为这里没有用到GPS，所以务必赋值为false
        isHaveNetworkInfo = false; //因为这里没有用到network，所以务必赋值为false
        getStationInfo();
        getNeighborStationInfo();
        getWifiInfo();
        getNeighborWifiInfo();
        return getLatlonJson();
    }

    /**
     * 所有定位依据都得到后通过这个方法调用预定位接口来获得定位的详细信息
     */
    private void getLocationData() {

        if(!isLocateCanStart()) {
            mRenrenLocationListener.onLocatedFailed("没有可以提供定位说需要的数据！！", NO_LOCATION_CONDITION);
            return;
        }
        JSONObject latlonJson = getLatlonJson();
        if(latlonJson != null) {
            INetResponse response = new INetResponse() {


				@Override
				public void response(INetRequest req, JSONObject obj) {

//					Log.d("zxc"," ret Json "+ obj.toString());
                    if(isLocationCancel) {
                        mRenrenLocationListener.onLocatedCancel();
                        return;
                    }

                    if (obj != null && obj instanceof JSONObject) {

                        final JSONObject root =  obj;

                            Log.v("@@@", "预定位返回:" + root.toString());
                            RenrenLocationData locationData = RenrenLocationData.parseLocationData(root);
                            RenrenLocationCache.saveRenrenLocationCache(locationData, mContext, isFromShuaPao);
                            Log.v("@@@", "need2deflect is:" + locationData.getNeed2deflect());
                            Log.v("@@@", "gpsLat is:" + locationData.getGpsLat() + " and gpsLon is:" + locationData.getGpsLon());
                            mRenrenLocationListener.onLocationSucceeded(locationData);
                            if(isFromPoiList) {
                                startBackstageLocateContinues();
                            }

                    }
                
				}
            };
            String latlonString = latlonJson.toString();
            JSONObject latlon = null;
			try {
				latlon = new JSONObject(latlonString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Log.d("zxc","latlon data  "+ latlon.toString());
            HttpMasService.getInstance().getLocationInfo(DEFAULT_PAGE, RenrenLocationConfig.LATLONDEFAULT, RenrenLocationConfig.LATLONDEFAULT, DEFAULT_NEED2DEFLECT_CODE, latlon, response, DEFAULT_RADIUS, DEFAULT_PAGE_SIZE, false);
        } else {
            mRenrenLocationListener.onLocatedFailed("没有可以提供定位说需要的数据！！", NO_LOCATION_CONDITION);
        }

    }

    /**
     * 根据照片的经纬度获得照片拍照地点的附近位置信息
     */

    /**
     * 拼接定位依据latlon json串的方法
     * @return 拼接好后的latlon json串
     */
    private JSONObject getLatlonJson() {

        JSONObject latlon = new JSONObject();

        try {
            latlon.put(OS_TYPE, Build.MODEL + ";" + Build.VERSION.SDK + ";" + Build.VERSION.RELEASE);
            if(isStationCon) {
                int mobileCode = mStationInfo.getMobileCode();
                latlon.put(RenrenStationData.MOBILE_CODE_JSON_PARAM, mobileCode);
                switch (mobileCode) {
                    case RenrenStationData.GSM_MOBILE_CODE:
                        latlon.put(RenrenStationData.GSM_JSON_PARAM, mStationInfo.toGsmJsonObject());
                        break;
                    case RenrenStationData.CDMA_MOBILE_CODE:
                        latlon.put(RenrenStationData.CDMA_JSON_PARAM, mStationInfo.toCdmaJsonObject());
                        break;
                }
                mStationInfo.clear();//拼接结束后必须将定位依据中的值初始化，否侧会影响到下一次的定位
            }
            if(isHaveGPSinfo) {
                latlon.put(RenrenGPSData.GPS_JSON_PARAM, mGPSInfo.toGPSJsonArray());
                mGPSInfo.clear();//拼接结束后必须将定位依据中的值初始化，否侧会影响到下一次的定位
            }
            if(isHaveNetworkInfo) {
                latlon.put(RenrenNetworkData.NETWORK_JSON_PARAM, mNetworkInfo.toNetworkJsonArray());
                mNetworkInfo.clear();//拼接结束后必须将定位依据中的值初始化，否侧会影响到下一次的定位
            }
            if(isWificon) {
                latlon.put(RenrenWifiData.WIFI_JSON_PARAM, mWifiInfo.toWifiJsonObject());
                mWifiInfo.clear();//拼接结束后必须将定位依据中的值初始化，否侧会影响到下一次的定位
            }
            if(isStationNeighbor) {
                latlon.put(RenrenNeighborStationData.NEIGHBOR_JSON_PARAM, mNeighborStationInfo.toNeighborStationJsonObject());
                mNeighborStationInfo.clear();//拼接结束后必须将定位依据中的值初始化，否侧会影响到下一次的定位
            }
            if(isWifiNeighbor) {
                latlon.put(RenrenNeighborWifiData.NEIGHBOR_WIFI_PARAM, mNeighborWifiInfo.toNeighborWifiJsonObject());
                mNeighborWifiInfo.clear();//拼接结束后必须将定位依据中的值初始化，否侧会影响到下一次的定位
            }
            Log.v("@@@", "latlon is:" + latlon.toString());
            return latlon;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 判断是否有定位依据可用
     * @return 是否有定位依据
     */
    private boolean isLocateCanStart() {

        return isHaveGPSinfo || isHaveNetworkInfo || isWificon || isStationCon || isWifiNeighbor || isStationNeighbor;
    }

    /**
     * 开启GPS或者network监听的方法，让系统GPS或者network开始工作
     */
    private void startGPS(LocationListener listener) {
    	Log.d("zxc","listner " + listener);
        if(mLocationManager == null) {
            listener.onLocationChanged(null);
            return;
        }
        if(listener == gpsLocationListener) {
        	Log.d("zxc","gps listener ");
            handler.sendEmptyMessage(0);
        } else if(listener == networkLocationListener) {
        	Log.d("zxc","network listener ");
            handler.sendEmptyMessage(1);
        }
    }

    /**
     * 开启GPS和network监听的方法
     */
    private void startGPS() {

        if(isGPSEnable()) {
            startGPS(gpsLocationListener);
        }
        if(checkNet(mContext, false) && isNetworkProviderEnable()) {
            startGPS(networkLocationListener);
        }
    }

    /**
     * 单次定位，GPS启动延迟定时器开启
     */
    private void startGPSDelayTimer() {

        stopGPSDelayTimer();

        mGPSDelayTimer = new Timer();
        mGPSDelayTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                startLoctionWithAll();
                stopGPS(); //因为是单次定位，定位结束后要关闭定位监听
            }
        }, mGPSDelayTime * 1000);//四秒造成的后果是GPS大部分情况下不可用，不过让用户久等体验更差以后看需求的取舍吧
    }

    private void getLatLonGPSDelayTimer(final GetLatLonListener listener) {

        stopGPSDelayTimer();

        mGPSDelayTimer = new Timer();
        mGPSDelayTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                listener.getLatLon(getLatLonWithAll());
                stopGPS(); //因为是单次定位，定位结束后要关闭定位监听
            }
        }, mGPSDelayTime * 1000);//四秒造成的后果是GPS大部分情况下不可用，不过让用户久等体验更差以后看需求的取舍吧
    }

    /**
     * 启动多次定位（间隔为五分钟的连续定位）
     */
    public void startLocateContinues(RenrenLocationListener listener, boolean fromShuaPao) {

        this.mRenrenLocationListener = listener;
        isFromShuaPao = fromShuaPao;
        isFromPoiList = false;//因为这里已经是一直开启后台的GPS和network监听了，所以这里不用这个标志位了，直接为false

        if(!checkNet(mContext, false)) {
            mRenrenLocationListener.onLocatedFailed("请检查您的网络，无网络无法定位！", NO_NETWORK_CODE);
            return;
        }

        stopLocateContinues();//为了防止已经开启多次定位，所以再次开启前先关闭一次

        isLocationCancel = false; //每次定位前一定将这个置为false

        if(isGPSEnable() || isNetworkProviderEnable()) {

            startGPS();

            startContinuesLocationTimer();
        } else {
            continuesTimer = new Timer();
            continuesTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startLocatinWithoutGPSAndNetwork();
                }
            }, mGPSDelayTime * 1000, CONTINUE_LOCATE_INTERVAL * 1000);
        }
    }

    
 
    
    /**
     * 启动多次定位,只获得系统的定位数据，不去掉定位接口，这个是后台跑数据，为了下一次能更准确更快地获得GPS和network信息
     */
    protected void startBackstageLocateContinues() {

        if(isGPSEnable() || isNetworkProviderEnable()) {
            stopGPS();

            startGPS();
        }
    }

    /**
     * 开启连续定位的定时器
     */
    private void startContinuesLocationTimer() {

        continuesTimer = new Timer();
        continuesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startLoctionWithAll();
            }
        }, mGPSDelayTime * 1000, CONTINUE_LOCATE_INTERVAL * 1000);
    }

    /**
     * 停止多次定位
     */
    public void stopLocateContinues() {

        stopContinuesLocationTimer();
        stopGPS();
        isLocationCancel = true;
        if(mRenrenLocationListener != null) {
            mRenrenLocationListener.onLocatedCancel();
        }
    }

    /**
     * 停止单次定位
     */
    public void stopLocateSingle() {

        stopGPSDelayTimer();
        stopGPS();
        isLocationCancel = true;
        if(mRenrenLocationListener != null) {
            mRenrenLocationListener.onLocatedCancel();
        }
    }

    /**
     * 停止单次定位计时器
     */
    private void stopGPSDelayTimer() {

        if (mGPSDelayTimer != null) {
            try {
                mGPSDelayTimer.cancel();
                mGPSDelayTimer = null;
            } catch (Exception e) {
            }
        }
    }

    /**
     * 停止连续定位计时器
     */
    private void stopContinuesLocationTimer() {

        if (continuesTimer != null) {
            try {
                continuesTimer.cancel();
                continuesTimer = null;
            } catch (Exception e) {
            }
        }
    }

    /**
     * 确定是否有可用的GPS location和network location，如果有赋值给对应的属性
     */
    private void setLocation() {

        long nowTime = System.currentTimeMillis();

        if(isGPSListenerLocationEffective(nowTime) && isGPSLastKnowLocationEffective(nowTime)) {
            Log.v("@@@", "listenerLocation's time is:" + gpsListenerLocation.getTime() +" and lasrKnowLocation's time is:" + gpsLastKnowLocation.getTime());
            if(gpsListenerLocation.getTime() >= gpsLastKnowLocation.getTime()) {
                gpsLocation = gpsListenerLocation;
            } else {
                gpsLocation = gpsLastKnowLocation;
            }
        } else if(isGPSListenerLocationEffective(nowTime)) {
            Log.v("@@@", "listenerLocation's time is:" + gpsListenerLocation.getTime());
            gpsLocation = gpsListenerLocation;
        } else if(isGPSLastKnowLocationEffective(nowTime)) {
            Log.v("@@@", "lasrKnowLocation's time is:" + gpsLastKnowLocation.getTime());
            gpsLocation = gpsLastKnowLocation;
        }
        gpsListenerLocation = null; //赋值结束后必须给这连个值清空，要不会影响到下一次的定位
        gpsLastKnowLocation = null;
        if(isNetworkListenerLocationEffective(nowTime) && isNetworkLastKnowLocationEffective(nowTime)) {
            Log.v("@@@", "network listenerLocation's time is:" + networkListenerLocation.getTime() +" and lasrKnowLocation's time is:" + networkLastKnowLocation.getTime());
            if(networkListenerLocation.getTime() >= networkLastKnowLocation.getTime()) {
                networkLocation = networkListenerLocation;
            } else {
                networkLocation = networkLastKnowLocation;
            }
        } else if(isNetworkListenerLocationEffective(nowTime)) {
            Log.v("@@@", "network listenerLocation's time is:" + networkListenerLocation.getTime());
            networkLocation = networkListenerLocation;
        } else if(isNetworkLastKnowLocationEffective(nowTime)) {
            Log.v("@@@", "network lasrKnowLocation's time is:" + networkLastKnowLocation.getTime());
            networkLocation = networkLastKnowLocation;
        }
        networkListenerLocation = null; //赋值结束后必须给这连个值清空，要不会影响到下一次的定位
        networkLastKnowLocation = null;
    }

    /**
     * 判断gpsListenerLocation是否可用
     * @return gpsListenerLocation是否可用
     */
    private boolean isGPSListenerLocationEffective(long nowTime) {

        return gpsListenerLocation != null && (nowTime - gpsListenerLocation.getTime() <= SYSTEM_LOCATE_TIMEOUT);
    }

    /**
     * 判断gpsLastKnowLocation是否可用
     * @return gpsLastKnowLocation是否可用
     */
    private boolean isGPSLastKnowLocationEffective(long nowTime) {

        return gpsLastKnowLocation != null && (nowTime - gpsLastKnowLocation.getTime() <= SYSTEM_LOCATE_TIMEOUT);
    }

    /**
     * 判断networkListenerLocation是否可用
     * @return networkListenerLocation是否可用
     */
    private boolean isNetworkListenerLocationEffective(long nowTime) {

        return networkListenerLocation != null && (nowTime - networkListenerLocation.getTime() <= SYSTEM_LOCATE_TIMEOUT);
    }

    /**
     * 判断networkLastKnowLocation是否可用
     * @return networkLastKnowLocation是否可用
     */
    private boolean isNetworkLastKnowLocationEffective(long nowTime) {

        return networkLastKnowLocation != null && (nowTime - networkLastKnowLocation.getTime() <= SYSTEM_LOCATE_TIMEOUT);
    }

    /**
     * 给mGPSInfo和mNetworkInfo赋值，这两个是定位依据，并且根据是否有可付的值给isHaveGPSinfo和isHaveNetworkInfo两个标志位赋值
     */
    private void getLocationInfo() {

        if(gpsLocation != null) {
            mGPSInfo.setGpsLat(gpsLocation.getLatitude());
            mGPSInfo.setGpsLon(gpsLocation.getLongitude());
            if (gpsLocation.hasAccuracy()) {
                mGPSInfo.setGpsAccuracy(gpsLocation.getAccuracy());
            } else {
                mGPSInfo.setGpsAccuracy(-1);
            }
            mGPSInfo.setTime(System.currentTimeMillis());
            gpsLocation = null; //结束后必须赋值为空，要不会影响到下一次定位
            isHaveGPSinfo = true;
        } else {
            isHaveGPSinfo = false;
        }
        if(networkLocation != null) {
            mNetworkInfo.setNetworkLat(networkLocation.getLatitude());
            mNetworkInfo.setNetworkLon(networkLocation.getLongitude());
            if (networkLocation.hasAccuracy()) {
                mNetworkInfo.setNetworkAccuracy(networkLocation.getAccuracy());
            } else {
                mNetworkInfo.setNetworkAccuracy(-1);
            }
            mNetworkInfo.setTime(System.currentTimeMillis());
            networkLocation = null; //结束后必须赋值为空，要不会影响到下一次定位
            isHaveNetworkInfo = true;
        } else {
            isHaveNetworkInfo = false;
        }
    }

    /**
     * 给mWifiInfo赋值，这个是定位依据，并且根据是否有可付的值给isWificon标志位赋值
     */
    private void getWifiInfo() {

        if (mWifiManager == null) {
            isWificon = false;
            return;
        }
        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info != null) {
            mWifiInfo.setMacAddress(info.getMacAddress());
            mWifiInfo.setWifiSsid(info.getSSID());
            mWifiInfo.setWifiBssid(info.getBSSID());
            mWifiInfo.setWifiIpAddress(info.getIpAddress());
            mWifiInfo.setTime(System.currentTimeMillis());
            isWificon = true;
        } else {
            isWificon = false;
        }
    }

    /**
     * 给mStationInfo赋值，这个是定位依据，并且根据是否有可付的值给isStationCon标志位赋值
     */
    private void getStationInfo() {

        if (mTelephonyManager == null) {
            isStationCon = false;
            return;
        }

        CellLocation sm = mTelephonyManager.getCellLocation();
        // 连接上的cell id信息
        if(sm!=null){
            String imei = mTelephonyManager.getDeviceId();
            String imsi = mTelephonyManager.getSubscriberId();
            String carrier = mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY ? mTelephonyManager.getSimOperator() : "";
            mStationInfo.setImei(imei);
            mStationInfo.setImsi(imsi);
            mStationInfo.setCarrier(carrier);
            if (sm instanceof GsmCellLocation) {//gsm和cdma用于定位的基站信息有些相同有些不同，所以要分开处理
                GsmCellLocation gsmCellInfo = (GsmCellLocation) sm;

                mStationInfo.setRadioType(RenrenStationData.GSM_RADIO_TYPE);
                mStationInfo.setCellId(gsmCellInfo.getCid());
                if (Methods.fitApiLevel(5)) {// getLac 在api level 5以上才能使用getLac
                    mStationInfo.setLocationAreaCode(gsmCellInfo.getLac());
                }
                mStationInfo.setMobileCode(RenrenStationData.GSM_MOBILE_CODE);
            } else if (sm instanceof CdmaCellLocation) {
                if(Integer.parseInt(Build.VERSION.SDK) >= 5){// 部分方法必须在api level 5以上才能使用
                    CdmaCellLocation cdmaCellInfo = (CdmaCellLocation) sm;
                    mStationInfo.setRadioType(RenrenStationData.CDMA_RADIO_TYPE);
                    mStationInfo.setBid(cdmaCellInfo.getBaseStationId());
                    mStationInfo.setSid(cdmaCellInfo.getSystemId());
                    mStationInfo.setNid(cdmaCellInfo.getNetworkId());
                    mStationInfo.setBaseStationLatitude(cdmaCellInfo.getBaseStationLatitude());
                    mStationInfo.setBaseStationLongitude(cdmaCellInfo.getBaseStationLongitude());
                }
                mStationInfo.setMobileCode(RenrenStationData.CDMA_MOBILE_CODE);
            }
            mStationInfo.setTime(System.currentTimeMillis());
            Configuration con = mContext.getResources().getConfiguration();
            if (con != null) {
                mStationInfo.setHomeMobileCountryCode(String.valueOf(con.mcc));
                mStationInfo.setHomeMobileNetworkCode(String.valueOf(con.mnc));
            }
            isStationCon = true;
        } else {
            isStationCon = false;
        }
    }

    /**
     * 给mNeighborWifiInfo赋值，这个是定位依据，并且根据是否有可付的值给isWifiNeighbor标志位赋值
     */
    private void getNeighborWifiInfo() {

        if (mWifiManager == null) {
            isWifiNeighbor = false;
            return;
        }
        List<ScanResult> mWifiList = mWifiManager.getScanResults();
        if (mWifiList != null) {
            int size = mWifiList.size();
            if (size > 10) {
                size = 10;
            }
            ArrayList<RenrenNeighborWifiData.NeiborWifi> neibors = new ArrayList<RenrenNeighborWifiData.NeiborWifi>();
            if (size > 0) {

                for (int i = 0; i < size; i++) {
                    ScanResult sr = (mWifiList.get(i));
                    RenrenNeighborWifiData.NeiborWifi neiborWifiInfo = new RenrenNeighborWifiData.NeiborWifi();
                    neiborWifiInfo.wifiSSID = sr.SSID;
                    neiborWifiInfo.wifiBSSID = sr.BSSID;
                    neiborWifiInfo.signalStrength = sr.level;
                    neibors.add(neiborWifiInfo);
                }
                mNeighborWifiInfo.setNeibors(neibors);
                mNeighborWifiInfo.setTime(System.currentTimeMillis());
            }
            isWifiNeighbor = true;
        } else {
            isWifiNeighbor = false;
        }
    }

    /**
     * 给mNeighborStationInfo赋值，这个是定位依据，并且根据是否有可付的值给isStationNeighbor标志位赋值
     */
    private void getNeighborStationInfo() {

        if (mTelephonyManager == null) {
            isStationNeighbor = false;
            return;
        }

        List<NeighboringCellInfo> list = mTelephonyManager.getNeighboringCellInfo();
        if (list != null) {
            int count = list.size();
            if (count > 10) {
                count = 10;
            }
            if (count > 0) {
                ArrayList<RenrenNeighborStationData.NeighborStation> neibors = new ArrayList<RenrenNeighborStationData.NeighborStation>();

                NeighboringCellInfo info;
                Configuration con = mContext.getResources().getConfiguration();

                for (int i = 0; i < count; i++) {
                    info = list.get(i);
                    if (info != null) {
                        RenrenNeighborStationData.NeighborStation neighborStationInfo = new RenrenNeighborStationData.NeighborStation();
                        neighborStationInfo.cellId = info.getCid();
                        if (Methods.fitApiLevel(5)) {
                            // getLac 在api level 5以上才能使用getLac
                            neighborStationInfo.locationAreaCode = getLac(info);
                        }
                        neighborStationInfo.signalStrength = info.getRssi();
                        if (con != null) {
                            neighborStationInfo.homeMobileCountryCode = String.valueOf(con.mcc);
                            neighborStationInfo.homeMobileNetworkCode = String.valueOf(con.mnc);
                        }
                        neibors.add(neighborStationInfo);
                    }
                }
                mNeighborStationInfo.setNeiborList(neibors);
                mNeighborStationInfo.setTime(System.currentTimeMillis());
            }
            isStationNeighbor = true;
        } else {
            isStationNeighbor = false;
        }
    }


    /**
     * 停止系统对GPS或者network的监听，也就是停止系统的定位服务
     */
    private void stopGPS(LocationListener listener){

        try {
            if(mLocationManager != null){
                mLocationManager.removeUpdates(listener);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 停止系统对GPS和network的监听
     */
    public void stopGPS() {

        stopGPS(gpsLocationListener);
        stopGPS(networkLocationListener);
    }

    /**
     * 判断GPS是否可用
     * @return
     */
    private boolean isGPSEnable(){

        if(mLocationManager == null) {
            return false;
        }
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ;
    }

    /**
     * 判断network是否可用
     * @return
     */
    private boolean isNetworkProviderEnable(){

        if(mLocationManager == null) {
            return false;
        }
        return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * 判断网络是否可用
     * @param context
     * @return
     */
    private boolean checkNet(Context context) {
        return checkNet(context, false);
    }

    /**
     * 判断网络是否可用
     * @param context
     * @param isShowMess 是否要提示网络不可用的toast
     * @return
     */
    private boolean checkNet(Context context, boolean isShowMess) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
        if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
            if (isShowMess) {
                Toast.makeText(context, "网络不可用",
                        Toast.LENGTH_LONG)
                .show();
            }
            return false;

        } else {
            return true;
        }
    }
    public static final int getLac(NeighboringCellInfo info) {
		return info.getLac();
	}
}

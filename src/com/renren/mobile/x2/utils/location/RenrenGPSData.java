package com.renren.mobile.x2.utils.location;

import android.location.Location;
import android.location.LocationManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;

/**
 * RenrenGPSData
 * @author : xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:39
 * 这个类是一个通过GPS返回的数据的封装类，和系统的Location类中封装的数据差不多，不过这里的时间用的是系统最新时间，另外这里提供了一些我们会用到的基本方法，
 * 比如拼到请求接口的latlon中的json串转换等
 */
public class RenrenGPSData {

    /**
     * 拼接latlon json串gps信息的key
     */
    public final static String GPS_JSON_PARAM = "gps_location";

    /**
     * 转换为formatString时的头
     */
    public final static String GPS_STRING_PARAM = "&gps=";

    /**
     * 拼接latlon json串gps纬度的key
     */
    public final static String GPS_LAT_PARAM = "gps_lat";

    /**
     * 拼接latlon json串gps经度的key
     */
    public final static String GPS_LON_PARAM = "gps_lon";

    /**
     * 拼接latlon json串gps时间的key
     */
    public final static String GPS_TIME_PARAM = "gps_time";

    /**
     * 拼接latlon json串gps精确度的key
     */
    public final static String GPS_ACCURACY_PARAM = "gps_accuracy";

    /**
     * 判断两点位置是否发生变化最小误差,如果小于等于0，认为每次都已经发生变化
     */
    private int mMaxTolerance = 50;

    /**
     * 用来保存两点之间距离的变量
     */
    private float mDistance = 0.0f;

    /**
     * 通过gps获得的纬度，这个和通过服务端返回的纬度不同，该值*1000000与服务端返回的纬度就一致了
     */
    private double gpsLat =RenrenLocationConfig.LATLONDEFAULT / 100000.00d;

    /**
     * 通过gps获得的经度，这个和通过服务端返回的经度不同，该值*1000000与服务端返回的经度就一致了
     */
    private double gpsLon = RenrenLocationConfig.LATLONDEFAULT / 100000.00d;

    /**
     * 通过gps获得的定位精确度
     */
    private float gpsAccuracy;

    /**
     * 获得gps后的系统时间
     */
    private long time;

    /**
     * gpsLat的set方法，给gpsLat赋值
     * @param gpsLat 要给gpsLat赋的值
     */
    public void setGpsLat(double gpsLat) {
        this.gpsLat = gpsLat;
    }

    /**
     * gpsLat的get方法，得到当前的gpsLat
     * @return 当前的gpsLat
     */
    public double getGpsLat() {
    	ErrLog.Print("gpsLat " + gpsLat);
        return this.gpsLat;
    }

    /**
     * gpsLon的set方法，给gpsLon赋值
     * @param gpsLon 要给gpsLon赋的值
     */
    public void setGpsLon(double gpsLon) {
        this.gpsLon = gpsLon;
    }

    /**
     * gpsLon的get方法，得到当前的gpsLon
     * @return 当前的gpsLon
     */
    public double getGpsLon() {
    	ErrLog.Print("gpsLon " + gpsLon);
    	return this.gpsLon;
    }

    /**
     * gpsAccuracy的set方法，给gpsAccuracy赋值
     * @param gpsAccuracy 要给gpsAccuracy赋的值
     */
    public void setGpsAccuracy(float gpsAccuracy) {
        this.gpsAccuracy = gpsAccuracy;
    }

    /**
     * gpsAccuracy的get方法，得到当前的gpsAccuracy
     * @return 当前的gpsAccuracy
     */
    public float getGpsAccuracy() {
        return this.gpsAccuracy;
    }

    /**
     * time的get方法，得到当前的time
     * @return 当前的time
     */
    public long getTime() {
        return time;
    }

    /**
     * time的set方法，给time赋值
     * @param time 要给time赋的值
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * 将这个数据封装转换成拼接latlon json串的相应json格式，如下面[中的内容]
     * "gps_location":[{
     "gps_lat":"39959301",
     "gps_lon":"116434177",
     "gps_time":"1339386441129",
     "gps_accuracy":"46.0",
     }],
     * @return 转换后的Json串
     */
    public JSONArray toGPSJsonArray() {

        JSONObject gpsJSONObj = new JSONObject();
        JSONArray gpsJSONArray = new JSONArray();
        try {
            gpsJSONObj.put(GPS_LAT_PARAM, String.valueOf((long) (1000000 * this.gpsLat)));
            gpsJSONObj.put(GPS_LON_PARAM, String.valueOf((long) (1000000 * this.gpsLon)));
            gpsJSONObj.put(GPS_TIME_PARAM, String.valueOf(this.time));
            gpsJSONObj.put(GPS_ACCURACY_PARAM, String.valueOf(this.gpsAccuracy));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        gpsJSONArray.put(gpsJSONObj);
        return gpsJSONArray;
    }

    /**
     * 将所有数据恢复到初始化
     */
    public void clear() {

        gpsLat = RenrenLocationConfig.LATLONDEFAULT;
        gpsLon = RenrenLocationConfig.LATLONDEFAULT;
        time = 0;
        gpsAccuracy = 0;
    }

    /**
     * 跟上一次坐标比较，如果大于50米，则认为位置发生改变
     *
     * @param loc 要用来比较的Location
     * @return
     */
    public boolean locationChanged(Location loc) {

        if (this.gpsLat ==RenrenLocationConfig.LATLONDEFAULT
                || this.gpsLon ==RenrenLocationConfig.LATLONDEFAULT) {
            return true;
        }
        if (this.mMaxTolerance <= 0) {
            return true;
        }
        if(loc != null){
            Location oldLoc = new Location(LocationManager.GPS_PROVIDER);
            oldLoc.setLatitude(this.gpsLat);
            oldLoc.setLongitude(this.gpsLon);
            mDistance = oldLoc.distanceTo(loc);
            return mDistance > mMaxTolerance;
        } else {
            return true;
        }
    }

    /**
     * 将这个数据封装类转化为formatString格式的String串，格式如下
     * &gps=39.959301|116.434177|46.0|
     */
    public String toFormatString(){
        StringBuilder bf = new StringBuilder(GPS_STRING_PARAM);
        bf.append(this.gpsLat).append(RenrenLocationUtil.SEPERATOR)
                .append(this.gpsLon).append(RenrenLocationUtil.SEPERATOR)
                .append(this.gpsAccuracy).append(RenrenLocationUtil.SEPERATOR);
        return bf.toString();
    }

}

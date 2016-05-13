package com.renren.mobile.x2.utils.location;

import android.location.Location;
import android.location.LocationManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * RenrenNetworkData
 * @author xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:39
 * 这个类是一个通过network返回的数据的封装类，和系统的Location类中封装的数据差不多，不过这里的时间用的是系统最新时间，另外这里提供了一些我们会用到的基本方法，
 * 比如拼到请求接口的latlon中的json串转换等
 */
public class RenrenNetworkData {

    /**
     * 拼接latlon json串network信息的key
     */
    public final static String NETWORK_JSON_PARAM = "network_location";

    /**
     * 转换为formatString的头
     */
    public final static String NETWORK_STRING_PARAM = "&network=";

    /**
     * 拼接latlon json串network信息中纬度的key
     */
    private final static String NETWORK_LAT_PARAM = "network_lat";

    /**
     * 拼接latlon json串network信息中经度的key
     */
    private final static String NETWORK_LON_PARAM = "network_lon";

    /**
     * 拼接latlon json串network信息中时间的key
     */
    private final static String NETWORK_TIME_PARAM = "network_time";

    /**
     * 拼接latlon json串network信息中精确度的key
     */
    private final static String NETWORK_ACCURACY = "network_accuracy";

    /**
     * 判断两点位置是否发生变化最小误差,如果小于等于0，认为每次都已经发生变化
     */
    private int mMaxTolerance = 50;

    /**
     * 用来保存两点之间距离的变量
     */
    private float mDistance = 0.0f;

    /**
     * 通过network获得的纬度，这个和通过服务端返回的纬度不同，该值*1000000与服务端返回的纬度就一致了
     */
    private double networkLat = RenrenLocationConfig.LATLONDEFAULT / 100000.00d;;

    /**
     * 通过network获得的纬度，这个和通过服务端返回的纬度不同，该值*1000000与服务端返回的纬度就一致了
     */
    private double networkLon = RenrenLocationConfig.LATLONDEFAULT / 100000.00d;;

    /**
     * 通过network获得的定位精确度
     */
    private float networkAccuracy;

    /**
     * 获得network后的系统时间
     */
    private long time;

    /**
     * networkLat的set方法，给networkLat赋值
     * @param networkLat 要给networkLat赋的值
     */
    public void setNetworkLat(double networkLat) {
        this.networkLat = networkLat;
    }

    /**
     * networkLat的get方法，得到当前的networkLat
     * @return 当前的networkLat
     */
    public double getNetworkLat() {
        return this.networkLat;
    }

    /**
     * networkLon的set方法，给networkLon赋值
     * @param networkLon 要给networkLon赋的值
     */
    public void setNetworkLon(double networkLon) {
        this.networkLon = networkLon;
    }

    /**
     * networkLon的get方法，得到当前的networkLon
     * @return 当前的networkLon
     */
    public double getNetworkLon() {
        return this.networkLon;
    }

    /**
     * networkAccuracy的set方法，给networkAccuracy赋值
     * @param networkAccuracy 要给networkAccuracy赋的值
     */
    public void setNetworkAccuracy(float networkAccuracy) {
        this.networkAccuracy = networkAccuracy;
    }

    /**
     * networkAccuracy的get方法，得到当前的networkAccuracy
     * @return 当前的networkAccuracy
     */
    public float getNetworkAccuracy() {
        return this.networkAccuracy;
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
     * "network_location":[{
     "network_lat":"39959301",
     "network_lon":"116434177",
     "network_time":"1339386441129",
     "network_accuracy":"46.0",
     }],
     * @return 转换后的Json串
     */
    public JSONArray toNetworkJsonArray() {

        JSONObject gpsJSONObj = new JSONObject();
        JSONArray gpsJSONArray = new JSONArray();
        try {
            gpsJSONObj.put(NETWORK_LAT_PARAM, String.valueOf((long) (1000000 * this.networkLat)));
            gpsJSONObj.put(NETWORK_LON_PARAM, String.valueOf((long) (1000000 * this.networkLon)));
            gpsJSONObj.put(NETWORK_TIME_PARAM, String.valueOf(this.time));
            gpsJSONObj.put(NETWORK_ACCURACY, String.valueOf(this.networkAccuracy));
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

        networkLat = RenrenLocationConfig.LATLONDEFAULT;
        networkLon = RenrenLocationConfig.LATLONDEFAULT;
        time = 0;
        networkAccuracy = 0;
    }

    /**
     * 跟上一次坐标比较，如果大于50米，则认为位置发生改变
     *
     * @param loc
     * @return
     */
    public boolean locationChanged(Location loc) {

        if (this.networkLat == RenrenLocationConfig.LATLONDEFAULT
                || this.networkLon == RenrenLocationConfig.LATLONDEFAULT) {
            return true;
        }
        if (this.mMaxTolerance <= 0) {
            return true;
        }
        if(loc != null){
            Location oldLoc = new Location(LocationManager.GPS_PROVIDER);
            oldLoc.setLatitude(this.networkLat);
            oldLoc.setLongitude(this.networkLon);
            mDistance = oldLoc.distanceTo(loc);
            return mDistance > mMaxTolerance;
        } else {
            return true;
        }
    }

    /**
     * 将这个数据封装类转化为formatString格式的String串，格式如下
     * &network=39.959301|116.434177|46.0|
     * @return formatStirng格式
     */
    public String toFormatString(){
        StringBuilder bf = new StringBuilder(NETWORK_STRING_PARAM);
        bf.append(this.networkLat).append(RenrenLocationUtil.SEPERATOR)
                .append(this.networkLon).append(RenrenLocationUtil.SEPERATOR)
                .append(this.networkAccuracy).append(RenrenLocationUtil.SEPERATOR);
        return bf.toString();
    }
}

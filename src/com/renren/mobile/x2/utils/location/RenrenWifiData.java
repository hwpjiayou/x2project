package com.renren.mobile.x2.utils.location;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * RenrenWifiData
 * @author xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:38
 * 这个类是一个通过WifiManager的getConnectionInfo方法得到的数据的封装，也就是取本机所连接wifi信息的数据的封装，取得数据也只有那些
 * 我们定位接口需要的数据，不是全部
 */
public class RenrenWifiData {

    /**
     * 拼接latlon json串wifi信息的key
     */
    public final static String WIFI_JSON_PARAM = "wifi_tower_connected_info";

    /**
     * 转换为formatString时的头
     */
    public final static String WIFI_STRING_PARAM = "&wf=";

    /**
     * 拼接latlon json串wifi信息中mac地址的key
     */
    private final static String WIFI_MAC_ADDRESS_PARAM = "mac_address";

    /**
     * 拼接latlon json串wifi信息中ssid的key
     */
    private final static String WIFI_SSID_PARAM = "wifi_ssid";

    /**
     * 拼接latlon json串wifi信息中bssid的key
     */
    private final static String WIFI_BSSID_PARAM = "wifi_bssid";

    /**
     * 拼接latlon json串wifi信息中ip地址的key
     */
    private final static String WIFI_IP_ADDRESS_PARAM = "wifi_ip_address";

    /**
     * 拼接latlon json串wifi信息时间的key
     */
    private final static String WIFI_TIME_PARAM = "time";

    /**
     * 所连接的wifi的mac地址
     */
    private String macAddress;

    /**
     * 所连接的wifi的ssid
     */
    private String wifiSsid;

    /**
     * 所连接的wifi的bssid
     */
    private String wifiBssid;

    /**
     * 所连接的wifi的ip地址
     */
    private int wifiIpAddress;

    /**
     * 获得所连接的wifi的信息时的系统时间
     */
    private long time;

    /**
     * macAddress的set方法，给macAddress赋值
     * @param macAddress 要给time赋的值
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * macAddress的get方法，得到当前的macAddress
     * @return 当前的macAddress
     */
    public String getMacAddress() {
        return this.macAddress;
    }

    /**
     * wifiSsid的set方法，给wifiSsid赋值
     * @param wifiSsid 要给wifiSsid赋的值
     */
    public void setWifiSsid(String wifiSsid) {
        this.wifiSsid = wifiSsid;
    }

    /**
     * wifiSsid的get方法，得到当前的wifiSsid
     * @return 当前的wifiSsid
     */
    public String getWifiSsid() {
        return this.wifiSsid;
    }

    /**
     * wifiBssid的set方法，给wifiBssid赋值
     * @param wifiBssid 要给wifiBssid赋的值
     */
    public void setWifiBssid(String wifiBssid) {
        this.wifiBssid = wifiBssid;
    }

    /**
     * wifiBssid的get方法，得到当前的wifiBssid
     * @return 当前的wifiBssid
     */
    public String getWifiBssid() {
        return this.wifiBssid;
    }

    /**
     * wifiIpAddress的get方法，得到当前的wifiIpAddress
     * @return 当前的wifiIpAddress
     */
    public int getWifiIpAddress() {
        return wifiIpAddress;
    }

    /**
     * wifiIpAddress的set方法，给wifiIpAddress赋值
     * @param wifiIpAddress 要给wifiIpAddress赋的值
     */
    public void setWifiIpAddress(int wifiIpAddress) {
        this.wifiIpAddress = wifiIpAddress;
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
     * 将这个数据封装转换成拼接latlon json串的相应json格式，如下面wifi信息后{}内全部内容
     "wifi_tower_connected_info"://wifi信息
     {"mac_address":"00:23:76:08:21:e7",//本机mac地址
     "wifi_ssid":"xiaonei-3G",
     "wifi_bssid":"00:1f:a3:65:7f:00",//wifi mac地址
     "wifi_ip_address":"192.168.1.1",
     "time":"51595352",//获取wifi信息时间
     }
     * @return 转换后的Json串
     */
    public JSONObject toWifiJsonObject() {

        JSONObject connectedWifiInfo = new JSONObject();
        try {
            connectedWifiInfo.put(WIFI_MAC_ADDRESS_PARAM, this.macAddress);
            connectedWifiInfo.put(WIFI_SSID_PARAM, this.wifiSsid);
            connectedWifiInfo.put(WIFI_BSSID_PARAM, this.wifiBssid);
            connectedWifiInfo.put(WIFI_IP_ADDRESS_PARAM, String.valueOf(this.wifiIpAddress));
            connectedWifiInfo.put(WIFI_TIME_PARAM, String.valueOf(this.time));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connectedWifiInfo;
    }

    /**
     * 将所有数据恢复到初始化
     */
    public void clear() {

        time = 0;
        wifiIpAddress = 0;
        wifiBssid = null;
        wifiSsid = null;
        macAddress = null;
    }

    /**
     * 判断最新连接到的wifi和记录下的连接过的wifi是不是同一个
     * @param winfo 最新连接的wifi的信息
     * @return 是或者不是
     */
    public boolean equals(RenrenWifiData winfo) {

        if(winfo != null){
            return wifiBssid.equals(winfo.getWifiBssid());
        } else {
            return false;
        }
    }

    /**
     * 将这个数据封装类转化为formatString格式的String串，格式如下
     * &wfs=002568b060f1|-88|
     */
    public String toFormatString(){
        StringBuilder bf = new StringBuilder(WIFI_STRING_PARAM);
        bf.append(TextUtils.isEmpty(this.wifiBssid) ? "" : this.wifiBssid.replaceAll(":", "")).append(RenrenLocationUtil.SEPERATOR)
                .append(this.wifiSsid).append(RenrenLocationUtil.SEPERATOR);
        return bf.toString();
    }
}

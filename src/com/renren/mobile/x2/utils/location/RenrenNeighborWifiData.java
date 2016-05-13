package com.renren.mobile.x2.utils.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * RenrenNeighborWifiData
 * @author xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:54
 * 这个类是一个通过WifiManager的getScanResults方法得到的数据的封装，也就是取附近wifi信息的数据的封装，取得数据也只有那些
 * 我们定位接口需要的数据，不是全部
 */
public class RenrenNeighborWifiData {

    /**
     * 拼接latlon json串周边wifi信息的key
     */
    public final static String NEIGHBOR_WIFI_PARAM = "wifi_tower_neighbors_info";

    /**
     * 转换为formatString时的头
     */
    public final static String NEIGHBOR_WIFI_STRING_PARAM = "&wfs=";

    /**
     * 转换为formatString时的每一个单独的wifi信息的头
     */
    private final static String TYPE = "w";

    /**
     * 拼接latlon json串周边wifi信息时间的key
     */
    private final static String NEIGHBOR_WIFI_TIME_PARAM = "time";

    /**
     * 拼接latlon json串周边wifi信息ssid的key
     */
    private final static String NEIGHBOR_WIFI_SSID_PARAM = "wifi_ssid";

    /**
     * 拼接latlon json串周边wifi信息bssid的key
     */
    private final static String NEIGHBOR_WIFI_BSSID_PARAM = "wifi_bssid";

    /**
     * 拼接latlon json串周边wifi信息信号强度的key
     */
    private final static String NEIGHBOR_WIFI_SIGNAL_STRENGTH_PARAM = "signal_strength";

    /**
     * 拼接latlon json串周边wifi信息周边wifi列表的key
     */
    private final static String NEIGHBOR_WIFI_LIST_PARAM = "wifi_tower_list";

    /**
     * 获得周边wifi信息时的系统时间
     */
    private long time;

    /**
     * 获得周边wifi信息的周边信息列表
     */
    private ArrayList<NeiborWifi> neibors;

    /**
     * 一个内部类里面封装着周边wifi的wifi的各个信息属性
     */
    public static class NeiborWifi
    {
        /**
         * 周边wifi信息中的bssid
         */
        public  String wifiBSSID;

        /**
         * 周边wifi信息中的ssid
         */
        public String wifiSSID;

        /**
         * 周边wifi信息中的信号强度
         */
        public int signalStrength;

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
     * neibors的get方法，得到当前的neibors
     * @return 当前的neibors
     */
    public ArrayList<NeiborWifi> getNeibors() {
        return neibors;
    }

    /**
     * neibors的set方法，给neibors赋值
     * @param neibors 要给neibors赋的值
     */
    public void setNeibors(ArrayList<NeiborWifi> neibors) {
        this.neibors = neibors;
    }

    /**
     * 将这个数据封装转换成拼接latlon json串的相应json格式，如下面周围wifi信息后{}内全部内容
     "wifi_tower_neighbors_info"://周围wifi信息
     {"time":"51595352",
     "wifi_tower_list":
     [{"wifi_ssid":"xiaonei-3G",
     "wifi_bssid":"00:1f:a3:65:7f:00",
     "signal_strength":"4"},
     {},{},{},......................]
     }
     }
     * @return 转换后的Json串
     */
    public JSONObject toNeighborWifiJsonObject() {

        if(neibors == null || neibors.size() == 0)
        {
            return null;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put(NEIGHBOR_WIFI_TIME_PARAM, String.valueOf(this.time));

            JSONArray arr = new JSONArray();
            for (NeiborWifi nor : neibors)
            {
                JSONObject subObj = new JSONObject();
                subObj.put(NEIGHBOR_WIFI_SSID_PARAM, nor.wifiSSID);
                subObj.put(NEIGHBOR_WIFI_BSSID_PARAM , nor.wifiBSSID);
                subObj.put(NEIGHBOR_WIFI_SIGNAL_STRENGTH_PARAM, String.valueOf(nor.signalStrength));
                arr.put(subObj);
            }
            obj.put(NEIGHBOR_WIFI_LIST_PARAM, arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * 将所有数据恢复到初始化
     */
    public void clear() {

        time = 0;
        if(neibors != null) {
            neibors.clear();
            neibors = null;
        }
    }

    /**
     * 将这个数据封装类转化为formatString格式的String串，格式如下
     * &wfs=w|00 1f a3 65 7f 00|xiaonei-3G|4|+w|00 1f a3 65 7f 00|xiaonei-3G|4|
     */
    public String toFormatString(){
        StringBuilder bf = new StringBuilder(NEIGHBOR_WIFI_STRING_PARAM);
        for (NeiborWifi nor : neibors){
            bf.append(TYPE).append(RenrenLocationUtil.SEPERATOR)
               .append(nor.wifiBSSID.replaceAll(":", "")).append(RenrenLocationUtil.SEPERATOR)
               .append(nor.wifiSSID).append(RenrenLocationUtil.SEPERATOR)
               .append(nor.signalStrength).append(RenrenLocationUtil.SEPERATOR).append(RenrenLocationUtil.MULTI_SEPERATOR);
        }
        String result = bf.toString();
        if(result.endsWith(RenrenLocationUtil.MULTI_SEPERATOR))
            result = result.substring(0, result.length() - 1);
        return result;
    }
}

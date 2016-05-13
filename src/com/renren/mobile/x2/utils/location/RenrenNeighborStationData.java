package com.renren.mobile.x2.utils.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * RenrenNeighborStationData
 * @author xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:53
 * 这个类是一个通过TelephonyManager的getNeighboringCellInfo方法得到的数据的封装，也就是取附近基站信息的数据的封装，取得数据也只有那些
 * 我们定位接口需要的数据，不是全部，另外经过测试只有移动可以取到这些数据，联通和电信都获取不了
 */
public class RenrenNeighborStationData {

    /**
     * 拼接latlon json串周边基站信息的key
     */
    public final static String NEIGHBOR_JSON_PARAM = "cell_tower_neighbors_info";

    /**
     * 转换为formatString时的头
     */
    public final static String NEIGHBOR_STRING_PARAM = "&cls=";

    /**
     * 转换为formatString时的每一个单独的基站信息的头
     */
    private final static String TYPE = "g";

    /**
     * 拼接latlon json串周边基站信息时间的key
     */
    private final static String NEIGHBOR_TIME_PARAM = "time";

    /**
     * 拼接latlon json串周边基站信息cellid的key（这个似乎只针对gsm，不知道以后会不会修改）
     */
    private final static String NEIGHBOR_CELL_ID_PARAM = "cell_id";

    /**
     * 拼接latlon json串周边基站信息的定位信息的key（这个似乎只针对gsm，不知道以后会不会修改）
     */
    private final static String NEIGHBOR_LOCATION_AREA_CODE_PARAM = "location_area_code";

    /**
     * 拼接latlon json串周边基站信息国家码的key
     */
    private final static String NEIGHBOR_HOME_MOBILE_COUNTRY_CODE_PARAM = "home_mobile_conutry_code";

    /**
     * 拼接latlon json串周边基站信息网络码的key
     */
    private final static String NEIGHBOR_HOME_MOBILE_NETWORK_CODE_PARAM = "home_mobile_network_code";

    /**
     * 拼接latlon json串周边基站信息信号强度的key
     */
    private final static String NEIGHBOR_SINAL_STRENGTH_PARAM = "signal_strength";

    /**
     * 拼接latlon json串周边基站信息全部列表的key
     */
    private final static String NEIGHBOR_CELL_LIST_PARAM = "cell_tower_list";

    /**
     * 获得周边基站信息时的系统时间
     */
    private long time;

    /**
     * 获得周边基站信息的周边信息列表
     */
    private ArrayList<NeighborStation> neiborList;

    /**
     * 一个内部类里面封装着周边基站的基站的各个信息属性
     */
    public static class NeighborStation{

        /**
         * 周边基站信息中的定位信息（这个似乎只针对gsm，不知道以后会不会修改）
         */
        public int locationAreaCode;

        /**
         * 周边基站信息中的网络码
         */
        public String homeMobileNetworkCode;

        /**
         * 周边基站信息中的国家码
         */
        public String homeMobileCountryCode;

        /**
         * 周边基站信息中的信号强度
         */
        public int signalStrength;

        /**
         * 周边基站信息中的cellid（这个似乎只针对gsm，不知道以后会不会修改）
         */
        public int cellId;
    }

    /**
     * time的set方法，给time赋值
     * @param time 要给time赋的值
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * time的get方法，得到当前的time
     * @return 当前的time
     */
    public long getTime() {
        return this.time;
    }

    /**
     * neiborList的set方法，给neiborList赋值
     * @param neiborList 要给neiborList赋的值
     */
    public void setNeiborList(ArrayList<NeighborStation> neiborList) {
        this.neiborList = neiborList;
    }

    /**
     * neiborList的get方法，得到当前的neiborList
     * @return 当前的neiborList
     */
    public ArrayList<NeighborStation> getNeiborList() {
        return this.neiborList;
    }

    /**
     * 将这个数据封装转换成拼接latlon json串的相应json格式，如下面周围基站信息后{}内全部内容
     "cell_tower_neighbors_info"://周围基站信息
     {"time":"51595352",
     "cell_tower_list":
     [{"cell_id":"2513",
     "location_area_code":"415",
     "home_mobile_conutry_code":"310",
     "home_mobile_network_code":"410",
     "signal_strength":"-70"},
     {},{},{},{},{},..................]

     }
     * @return 转换后的Json串
     */
    public JSONObject toNeighborStationJsonObject(){
        if(neiborList == null || neiborList.size() == 0)
        {
            return null;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put(NEIGHBOR_TIME_PARAM, String.valueOf(time));

            JSONArray arr = new JSONArray();
            for (NeighborStation nor : neiborList)
            {
                JSONObject subObj = new JSONObject();
                subObj.put(NEIGHBOR_CELL_ID_PARAM, String.valueOf(nor.cellId));
//                if (Methods.fitApiLevel(5)) {
                    subObj.put(NEIGHBOR_LOCATION_AREA_CODE_PARAM , String.valueOf(nor.locationAreaCode));
//                }
                if(nor.homeMobileCountryCode != null && !"".equals(nor.homeMobileCountryCode)) {
                    subObj.put(NEIGHBOR_HOME_MOBILE_COUNTRY_CODE_PARAM, nor.homeMobileCountryCode);
                }
                if(nor.homeMobileNetworkCode != null && !"".equals(nor.homeMobileNetworkCode)) {
                    subObj.put(NEIGHBOR_HOME_MOBILE_NETWORK_CODE_PARAM, nor.homeMobileNetworkCode);
                }
                subObj.put(NEIGHBOR_SINAL_STRENGTH_PARAM, String.valueOf(nor.signalStrength));
                arr.put(subObj);
            }
            obj.put(NEIGHBOR_CELL_LIST_PARAM, arr);
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
        if(neiborList != null) {
            neiborList.clear();
            neiborList = null;
        }
    }

    /**
     * 将这个数据封装类转化为formatString格式的String串，格式如下
     * &cls=g|61258|46001|40961|1|460|1311150638997|+g|61258|46001|40961|1|460|1311150638997|+g|61258|46001|40961|1|460|1311150638997|
     */
    public String toFormatString(){
        if(neiborList != null && neiborList.size() > 0){
            StringBuilder bf = new StringBuilder(NEIGHBOR_STRING_PARAM);
            for (NeighborStation nor : neiborList)
            {
                bf.append(TYPE).append(RenrenLocationUtil.SEPERATOR)
                    .append(nor.cellId).append(RenrenLocationUtil.SEPERATOR)
                    .append(nor.homeMobileCountryCode).append(nor.homeMobileNetworkCode).append(RenrenLocationUtil.SEPERATOR)
                    .append(nor.locationAreaCode).append(RenrenLocationUtil.SEPERATOR)
                    .append(nor.homeMobileNetworkCode).append(RenrenLocationUtil.SEPERATOR)
                    .append(nor.homeMobileCountryCode).append(RenrenLocationUtil.SEPERATOR)
                    .append(time).append(RenrenLocationUtil.SEPERATOR)
                    .append(RenrenLocationUtil.MULTI_SEPERATOR);
            }
            String result = bf.toString();
            if(result.endsWith(RenrenLocationUtil.MULTI_SEPERATOR))
                result = result.substring(0, result.length() - 1);
            return result;
        } else
            return null;
    }
}

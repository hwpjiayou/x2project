package com.renren.mobile.x2.utils.location;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;


/**
 * RenrenLocationData
 * @author : xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:12
 * 用来保存预定位返回的数据，以及保存到定位缓存的数据
 */
public class RenrenLocationData {

    /**
     * location cache(定位缓存) 超时时间 5分钟
     */
    private static final long LOCATE_CACHE_TIMEOUT = 5 * 60 * 1000;

    /**
     * location cache(定位缓存) 超时时间 1小时，这个是指长时间没有用到和定位相关的页面，需要一个小时候修改一次定位缓存，这样可以保证下一次
     * 使用定位时能减少等待时间，也能确保侧边栏里位置泡上面数字的准确性和及时性
     */
    private static final long LOCATE_CACHE_LONG_TIMEOUT = 60 * 60 * 1000;

    /**
     * 转换为formatString时的头
     */
    private final static String LOCATION_STRING_PARAM = "&location=";

    /**
     * 保存到定位缓存以及预定位返回的json串的poilistCount的key
     */
    private final static String POI_LIST_COUNT_PARAM = "count";

    /**
     * 保存到定位缓存以及预定位返回的json串的纬度的key
     */
    private final static String GPS_LAT_COUNT_PARAM = "lat_gps";

    /**
     * 保存到定位缓存以及预定位返回的json串的经度的key
     */
    private final static String GPS_LON_COUNT_PARAM = "lon_gps";

    /**
     * 保存到定位缓存以及预定位返回的json串的poilist的key
     */
    private final static String POI_LIST_PARAM = "poi_list";

    /**
     * 保存到定位缓存以及预定位返回的json串的是否需要偏移的key
     */
    private final static String NEED2DEFLECT_PARAM = "need2deflect";

    /**
     * 保存到定位缓存以及预定位返回的json串的附近活动数的key
     */
    private final static String ACTIVITY_COUNT_PARAM = "nearby_activity_count";

    /**
     * 保存到定位缓存以及预定位返回的json串的门址全部信息的key
     */
    private final static String ADDRESS_INFO_PARAM = "info";

    /**
     * 保存到定位缓存的json串的门址名的key
     */
    private final static String ADDRESS_NAME_PARAM = "address_name";

    /**
     * 保存到定位缓存以及预定位返回的json串的info内的门址名的key
     */
    private final static String ADDRESS_PARAM = "address";

    /**
     * 保存到定位缓存的json串的定位时间的key
     */
    private final static String LAST_LOCATION_TIME_PARAM = "last_location_time";

    /**
     * 要保存到定位缓存的门址名
     */
    private String addressName;

    /**
     * 要保存到定位缓存的纬度
     */
    private long gpsLat = RenrenLocationConfig.LATLONDEFAULT;

    /**
     * 要保存到定位缓存的经度
     */
    private long gpsLon = RenrenLocationConfig.LATLONDEFAULT;

    /**
     * 要保存到定位缓存的是否显示快速报道的标志位，新需求暂时去掉，不知道以后是否还要用暂时先注释掉
     */
//    private boolean isShowRapideCheckinTag = false;

    /**
     * 要保存到定位缓存的附近活动数
     */
    private int activityCount;

    /**
     * 要保存到定位缓存的定位时间
     */
    private long lastLocationTime;

    /**
     * 要保存到定位缓存的poiList的首页详细信息
     */
    private String poiListData;

    /**
     * 要保存到定位缓存的是否需要便宜的标志，0：不偏移；1：偏移
     */
    private int need2deflect = 1;

    /**
     * 要保存到定位缓存的poilist的数量
     */
    private int poiListCount;

    /**
     * 要保存到定位缓存的门址详细信息
     */
    private String addressInfo;

    /**
     * gpsLat的get方法，得到当前的gpsLat
     * @return 当前的gpsLat
     */
    public long getGpsLat() {
        return gpsLat;
    }

    /**
     * gpsLat的set方法，给gpsLat赋值
     * @param gpsLat 要给gpsLat赋的值
     */
    public void setGpsLat(long gpsLat) {
        this.gpsLat = gpsLat;
    }

    /**
     * addressName的get方法，得到当前的addressName
     * @return 当前的addressName
     */
    public String getAddressName() {
        return addressName;
    }

    /**
     * addressName的set方法，给addressName赋值
     * @param addressName 要给addressName赋的值
     */
    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    /**
     * gpsLon的get方法，得到当前的gpsLon
     * @return 当前的gpsLon
     */
    public long getGpsLon() {
        return gpsLon;
    }

    /**
     * gpsLon的set方法，给gpsLon赋值
     * @param gpsLon 要给gpsLon赋的值
     */
    public void setGpsLon(long gpsLon) {
        this.gpsLon = gpsLon;
    }

    /**
     * 这两个方法暂时先注释掉，如果以后确认不用刻意删除
     */
//    public boolean isShowRapideCheckinTag() {
//        return isShowRapideCheckinTag;
//    }
//
//    public void setShowRapideCheckinTag(boolean showRapideCheckinTag) {
//        isShowRapideCheckinTag = showRapideCheckinTag;
//    }

    /**
     * activityCount的get方法，得到当前的activityCount
     * @return 当前的activityCount
     */
    public int getActivityCount() {
        return activityCount;
    }

    /**
     * activityCount的set方法，给activityCount赋值
     * @param activityCount 要给activityCount赋的值
     */
    public void setActivityCount(int activityCount) {
        this.activityCount = activityCount;
    }

    /**
     * activityCount的get方法，得到当前的activityCount
     * @return 当前的activityCount
     */
    public long getLastLocationTime() {
        return lastLocationTime;
    }

    /**
     * lastLocationTime的set方法，给lastLocationTime赋值
     * @param lastLocationTime 要给lastLocationTime赋的值
     */
    public void setLastLocationTime(long lastLocationTime) {
        this.lastLocationTime = lastLocationTime;
    }

    /**
     * poiListData的get方法，得到当前的poiListData
     * @return 当前的poiListData
     */
    public String getPoiListData() {
        return poiListData;
    }

    /**
     * poiListData的set方法，给poiListData赋值
     * @param poiListData 要给poiListData赋的值
     */
    public void setPoiListData(String poiListData) {
        this.poiListData = poiListData;
    }

    /**
     * need2deflect的get方法，得到当前的need2deflect
     * @return 当前的need2deflect
     */
    public int getNeed2deflect() {
        return need2deflect;
    }

    /**
     * need2deflect的set方法，给need2deflect赋值
     * @param need2deflect 要给need2deflect赋的值
     */
    public void setNeed2deflect(int need2deflect) {
        this.need2deflect = need2deflect;
    }

    /**
     * poiListCount的get方法，得到当前的poiListCount
     * @return 当前的poiListCount
     */
    public int getPoiListCount() {
        return poiListCount;
    }

    /**
     * poiListCount的set方法，给poiListCount赋值
     * @param poiListCount 要给poiListCount赋的值
     */
    public void setPoiListCount(int poiListCount) {
        this.poiListCount = poiListCount;
    }

    /**
     * addressInfo的get方法，得到当前的addressInfo
     * @return 当前的addressInfo
     */
    public String getAddressInfo() {
        return addressInfo;
    }

    /**
     * addressInfo的set方法，给addressInfo赋值
     * @param addressInfo 要给addressInfo赋的值
     */
    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

    /**
     * 将数据转换成要保存到缓存中的标准Json格式,格式如下：
     * {"count":199,
     "lat_gps":39961520,
     "lon_gps":116438710,
     "poi_list":[
     {"phone":"","lon":116440410,"distance":221,"self_checkin":5,"address":"北三环东路8号","total_vistited":13764,"activity_count":0,"poi_name":"静安中心","activity_caption":"","pid":"B000A30D3A","lat":39960020},
     ...],
     "need2deflect":0,
     "info":{"lon":116438710,"county":"朝阳区","address":"香河园","poi_name":"香河园","province":"北京市","pid":"","nation":"中国","lat":39961520,"city":"北京市"},
     "last_location_time":1339386441137,
     "address_name":"香河园"}
     * @return 转换后的Json串
     */
    public JSONObject toLocationJsonObject() {

    	JSONObject locationDataJson = new JSONObject();

        try {
			locationDataJson.put(POI_LIST_COUNT_PARAM, getPoiListCount());
			locationDataJson.put(GPS_LAT_COUNT_PARAM, getGpsLat());
			locationDataJson.put(GPS_LON_COUNT_PARAM, getGpsLon());
			locationDataJson.put(POI_LIST_PARAM, new JSONObject(getPoiListData()));
			locationDataJson.put(NEED2DEFLECT_PARAM, getNeed2deflect());
			locationDataJson.put(ACTIVITY_COUNT_PARAM, getActivityCount());
			locationDataJson.put(ADDRESS_INFO_PARAM,  new JSONObject(getAddressInfo()));
			locationDataJson.put(ADDRESS_NAME_PARAM, getAddressName());
//        locationDataJson.put("", isShowRapideCheckinTag()); //暂时先注释掉，以后可能会删除，根据产品需求做最后决定
			locationDataJson.put(LAST_LOCATION_TIME_PARAM, getLastLocationTime());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return locationDataJson;
    }

    /**
     * 将预定位返回的数据的json串或者定位缓存中的数据的json串转换成LocationData的格式
     * @param obj 要转换的json数据
     * @return 转换后的LocationData
     */
    public static RenrenLocationData parseLocationData(JSONObject obj) {

//        Log.v("@@@", obj.toJsonString());
        RenrenLocationData locationData = new RenrenLocationData();
        try {
			locationData.setPoiListCount((int)obj.getLong(POI_LIST_COUNT_PARAM));
			locationData.setGpsLat(obj.getLong(GPS_LAT_COUNT_PARAM));
			locationData.setGpsLon(obj.getLong(GPS_LON_COUNT_PARAM));
			locationData.setPoiListData(obj.getJSONArray(POI_LIST_PARAM).toString());
			locationData.setNeed2deflect((int)obj.getLong(NEED2DEFLECT_PARAM));
			//to do 下一句会抛出异常
			locationData.setActivityCount((int)obj.getLong(ACTIVITY_COUNT_PARAM));//测试使用，记得最后去掉254
			JSONObject addressData = obj.getJSONObject(ADDRESS_INFO_PARAM);
			locationData.setAddressInfo(addressData.toString());
			String address = obj.getString(ADDRESS_NAME_PARAM);
			if(address != null && !"".equals(address)) {
				locationData.setAddressName(address);
			} else {
				locationData.setAddressName(addressData.getString(ADDRESS_PARAM));
			}
//        locationData.setShowRapideCheckinTag(obj.getBool(""));
			long time = obj.getLong(LAST_LOCATION_TIME_PARAM);
			if(0 == time) {
				locationData.setLastLocationTime(System.currentTimeMillis());
			} else {
				locationData.setLastLocationTime(time);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return locationData;
    }

    /**
     * 将预定位返回的数据的json串或者定位缓存中的数据的json字符串转换成LocationData的格式
     * @param jsonString 要转换的json字符串
     * @return 转换后的LocationData
     */
    public static RenrenLocationData parseLocationData(String jsonString) {

        JSONObject obj;
		try {
			obj = new JSONObject(jsonString);
			return RenrenLocationData.parseLocationData(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    /**
     * 定位缓存中的数据拼成format字符串显示
     * &location=poilistCount|activityCount|门址|经度|纬度|是否需要偏移|上次定位的时间|门址的具体信息|附近poi列表信息|
     * @return formatString
     */
    public String toFormatString(){

        StringBuilder bf = new StringBuilder(LOCATION_STRING_PARAM);
        bf.append(this.poiListCount).append(RenrenLocationUtil.SEPERATOR)
                .append(this.activityCount).append(RenrenLocationUtil.SEPERATOR)
                .append(this.addressName).append(RenrenLocationUtil.SEPERATOR)
                .append(this.gpsLat).append(RenrenLocationUtil.SEPERATOR)
                .append(this.gpsLon).append(RenrenLocationUtil.SEPERATOR)
                .append(this.need2deflect).append(RenrenLocationUtil.SEPERATOR)
                .append(this.lastLocationTime).append(RenrenLocationUtil.SEPERATOR)
//                .append(this.isShowRapideCheckinTag).append(RenrenLocationUtil.SEPERATOR)
                .append(this.addressInfo).append(RenrenLocationUtil.SEPERATOR)
                .append(this.poiListData).append(RenrenLocationUtil.SEPERATOR);
        return bf.toString();
    }


    /**
     * 判断缓存中的数据是否有效，方法用当前时间和缓存中的时间做减法运算看看是否已经超过五分钟，超过的话缓存失效
     * @param nowTime 当前时间
     * @return 当前缓存是否有效，true：有效；false：无效
     */
    public boolean isLocationCacheEffective(long nowTime) {

        return nowTime - getLastLocationTime() <= LOCATE_CACHE_TIMEOUT;
    }

    /**
     * 判断缓存中的数据是否需要更新，方法用当前时间和缓存中的时间做减法运算看看是否已经超过1小时，超过的话缓存失效需要重新定位
     * @param nowTime 当前时间
     * @return 当前缓存是否需要更新，true：需要；false：不需要
     */
    public boolean isNeedRefreshLocationCache(long nowTime) {

        return nowTime - getLastLocationTime() > LOCATE_CACHE_LONG_TIMEOUT;
    }
}

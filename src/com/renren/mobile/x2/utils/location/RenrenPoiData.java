package com.renren.mobile.x2.utils.location;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * RenrenPoiData
 * @author xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:13
 * 用来保存Poi缓存以及Exif poi的数据，以及保存到对应缓存的数据
 */
public class RenrenPoiData {

    /**
     * 转换为formatString时的头
     */
    private final static String POI_STRING_PARAM = "&poi=";

    /**
     * poi的经纬度与定位缓存经纬度的最大有效距离，如果poi缓存中的poi与定位缓存距离大于这个距离poi缓存不可用
     */
    private final static long MAX_TO_LERANCE = 20;

    /**
     * 新exif的经纬度与exif缓存经纬度的最大有效距离，如果新exif只想的地点与exif缓存中制定的地点距离大于这个距离exif缓存不可用
     */
    private final static long EXIF_MAX_TO_LERANCE = 500;

    /**
     * 保存到Poi缓存以及Exif poi缓存的json串的pid的key
     */
    private final static String PID_PARAM = "place_id";

    /**
     * 保存到Poi缓存以及Exif poi缓存的json串的poiName的key
     */
    private final static String POI_NMAE_PARAM = "place_name";

    /**
     * 保存到Poi缓存以及Exif poi缓存的json串的need2deflect的key
     */
    private final static String NEED2DEFLECT_PARAM = "d";

    /**
     * 保存到Poi缓存以及Exif poi缓存的json串的纬度的key
     */
    private final static String POI_LAT_PARAM = "gps_latitude";

    /**
     * 保存到Poi缓存以及Exif poi缓存的json串的经度的key
     */
    private final static String POI_LON_PARAM = "gps_longitude";

    /**
     * 保存到Poi缓存以及Exif poi缓存的json串的地址名称的key
     */
    private final static String ADDRESS_PARAM = "place_location";

    /**
     * 发布UGC时默认的定位类型，由于现在服务器下发下来的不是很可靠，所以这里一切发默认值，0代表手动输入
     */
    public final static int DEFAULT_LOCATE_TYPE = 0;

    /**
     * 发布UGC时默认的当前位置的可见范围，这里一切发默认值，2代表所有人可见
     */
    public final static int DEFAULT_PRICACY = 2;

    /**
     * 发布UGC时默认的来源，这里一切发默认值，5代表android客户端
     */
    public final static int DEFAULT_SOURCE_TYPE = 5;

    /**
     * Poi的pid
     */
    private String pid;

    /**
     * Poi的名称
     */
    private String poiName;

    /**
     * Poi所在的地址名
     */
    private String poiAddress;

    /**
     * Poi的纬度，这里如果是poi缓存用的是定位缓存中的纬度，如果是exif poi的缓存用的是exif偏移钱的纬度
     */
    private long gpsLat = RenrenLocationConfig.LATLONDEFAULT;

    /**
     * Poi的经度，这里如果是poi缓存用的是定位缓存中的经度，如果是exif poi的缓存用的是exif偏移钱的经度
     */
    private long gpsLon = RenrenLocationConfig.LATLONDEFAULT;

    /**
     * 当前位置是否需要再次偏移，1代表需要，0代表不需要，默认为1，之后用系统返回的值
     */
    private int need2deflect = 1;

    /**
     * pid的get方法，得到当前的pid
     * @return 当前的pid
     */
    public String getPid() {
        return pid;
    }

    /**
     * pid的set方法，给pid赋值
     * @param pid 要给pid赋的值
     */
    public void setPid(String pid) {
        this.pid = pid;
    }

    /**
     * poiName的get方法，得到当前的poiName
     * @return 当前的poiName
     */
    public String getPoiName() {
        return poiName;
    }

    /**
     * poiName的set方法，给poiName赋值
     * @param poiName 要给poiName赋的值
     */
    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    /**
     * poiAddress的get方法，得到当前的poiAddress
     * @return 当前的poiAddress
     */
    public String getPoiAddress() {
        return poiAddress;
    }

    /**
     * poiAddress的set方法，给poiAddress赋值
     * @param poiAddress 要给poiAddress赋的值
     */
    public void setPoiAddress(String poiAddress) {
        this.poiAddress = poiAddress;
    }

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
     * 将数据转换成要保存到缓存中的标准Json格式,格式如下：
     * {"place_id":"B000A30D3A",
     "place_latitude":39961520,
     "place_longitude":116438710,
     "place_name":"静安中心"
     "place_location":"北三环东路8号"
     "d":0}
     * @return 转换后的Json串
     */
    public JSONObject toPoiJsonObject() {

    	JSONObject poiDataJson = new JSONObject();
        try {
			poiDataJson.put(PID_PARAM, getPid());
			poiDataJson.put(POI_NMAE_PARAM, getPoiName());
			poiDataJson.put(ADDRESS_PARAM, getPoiAddress());
			poiDataJson.put(NEED2DEFLECT_PARAM, getNeed2deflect());
			poiDataJson.put(POI_LAT_PARAM, getGpsLat());
			poiDataJson.put(POI_LON_PARAM, getGpsLon());
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return poiDataJson;
    }

    /**
     * 将Poi缓存中的数据的json串或者Exif Poi缓存中的数据的json串转换成PoiData的格式
     * @param obj 要转换的json数据
     * @return 转换后的PoiData
     */
    public static RenrenPoiData parsePoiData(JSONObject obj) {

        try {
        	RenrenPoiData poiData = new RenrenPoiData();
        	poiData.setGpsLat(obj.getLong(POI_LAT_PARAM));
        	poiData.setGpsLon(obj.getLong(POI_LON_PARAM));
        	poiData.setNeed2deflect((int)obj.getLong(NEED2DEFLECT_PARAM));
        	poiData.setPid(obj.getString(PID_PARAM));
        	poiData.setPoiName(obj.getString(POI_NMAE_PARAM));
			poiData.setPoiAddress(obj.getString(ADDRESS_PARAM));
			return poiData;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    /**
     * 将Poi缓存中的数据的json字符串或者Exif Poi缓存中的数据的json字符串转换成LocationData的格式
     * @param jsonString 要转换的json字符串
     * @return 转换后的PoiData
     */
    public static RenrenPoiData parsePoiData(String jsonString) {

//        JSONObject obj = (JSONObject)JsonParser.parse(jsonString);
    	JSONObject obj;
		try {
			obj = new JSONObject(jsonString);
			return RenrenPoiData.parsePoiData(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
    }

    /**
     * poi缓存中的数据拼成formatString显示
     * &poi=B000A30D3A|静安中心|北三环东路8号|39961520|116438710|0|
     * &poi=pid|poiName|门址|纬度|经度|是否需要偏移|
     */
    public String toFormatString(){

        StringBuilder bf = new StringBuilder(POI_STRING_PARAM);
        bf.append(this.pid).append(RenrenLocationUtil.SEPERATOR)
                .append(this.poiName).append(RenrenLocationUtil.SEPERATOR)
                .append(this.poiAddress).append(RenrenLocationUtil.SEPERATOR)
                .append(this.gpsLat).append(RenrenLocationUtil.SEPERATOR)
                .append(this.gpsLon).append(RenrenLocationUtil.SEPERATOR)
                .append(this.need2deflect).append(RenrenLocationUtil.SEPERATOR);
        return bf.toString();
    }

    /**
     * 判断当前poiData是否有效，用传入的定位缓存中的经纬度与当前poi的经纬度算出距离看是否大于20，大于20无效，否则有效
     * @param locationData 要比较的定位信息
     * @return 是否有效，true，有效，false无效
     */
//    public boolean isPoiEffective(RenrenLocationData locationData) {
//
//        if(locationData == null) {
//            return false;
//        }
//        long distance = PlacesUtils.getMeterDistance(locationData.getGpsLon(), locationData.getGpsLat(), this.gpsLon, this.gpsLat);
//        return distance <= MAX_TO_LERANCE;
//    }

    /**
     * 判断当前exifPoiData是否有效，用传入的最新的照片的经纬度与当前exifPoiData的经纬度算出距离看是否大于500，大于500无效，否则有效
     * @param photoLon 照片的经度
     * @param photoLat 照片的纬度
     * @return 是否有效，true，有效，false无效
     */
//    public boolean isExifPoiEffective(long photoLon, long photoLat) {
//
//        long distance = PlacesUtils.getMeterDistance(photoLon, photoLat, this.gpsLon, this.gpsLat);
//        return distance <= EXIF_MAX_TO_LERANCE;
//    }
}

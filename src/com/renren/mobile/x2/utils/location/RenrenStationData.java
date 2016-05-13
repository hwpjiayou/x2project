package com.renren.mobile.x2.utils.location;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.utils.Methods;

/**
 * RenrenStationData
 * @author xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:38
 * 这个类是一个通过TelephonyManager的getCellLocation方法得到的数据的封装，也就是取当前所属基站信息的数据的封装，取得数据也只有那些
 * 我们定位接口需要的数据，不是全部，另外由于存在gsm和cdma不同的格式，所以数据取得不完全相同，并且转json串时用的也不是相同的方法
 */
public class RenrenStationData {

    /**
     * 拼接latlon json串gsm信息的key
     */
    public final static String GSM_JSON_PARAM = "cell_tower_connected_info";

    /**
     * 拼接latlon json串cdma信息的key
     */
    public final static String CDMA_JSON_PARAM = "cdma_cell_tower_connected_info";

    /**
     * 转换为formatString的头
     */
    public final static String STATION_STRING_PARAM = "&cl=";

    /**
     * 拼接latlon json串移动网络制式的key
     */
    public final static String MOBILE_CODE_JSON_PARAM = "mobile_code";

    /**
     * 拼接latlon json串cellid的key,这个只针对gsm用户
     */
    private final static String CELL_ID_PARAM = "cell_id";

    /**
     * 拼接latlon json串位置信息的key,这个只针对gsm用户
     */
    private final static String LOCATION_AREA_CODE_PARAM = "location_area_code";

    /**
     * 拼接latlon json串BaseStationId的key,这个只针对cdma用户
     */
    private final static String BID_PARAM = "bid";

    /**
     * 拼接latlon json串SystemId（系统识别码）的key,这个只针对cdma用户
     */
    private final static String SID_PARAM = "sid";

    /**
     * 拼接latlon json串NetworkId（网络标识码）的key,这个只针对cdma用户
     */
    private final static String NID_PARAM = "nid";

    /**
     * 拼接latlon json串基站信息中纬度的key,这个只针对cdma用户
     */
    private final static String BASE_STATION_LAT_PARAM = "base_station_latitude";

    /**
     * 拼接latlon json串基站信息中经度的key,这个只针对cdma用户
     */
    private final static String BASE_STATION_LON_PARAM = "base_station_longitude";

    /**
     * 拼接latlon json串DeviceId的key
     */
    private final static String IMEI_PARAM = "imei";

    /**
     * 拼接latlon json串SubscriberId的key
     */
    private final static String IMSI_PARAM = "imsi";

    /**
     * 拼接latlon json串网络制式的key
     */
    private final static String RADIO_TYPE_PARAM = "radio_type";

    /**
     * 拼接latlon json串运营商信息的key
     */
    private final static String CARRIER_PARAM = "carrier";

    /**
     * 拼接latlon json串基站信息时间的key
     */
    private final static String TIME_PARAM = "time";

    /**
     * 拼接latlon json串国家码的key
     */
    private final static String HOME_MOBILE_COUNTRY_CODE_PARAM = "home_mobile_conutry_code";

    /**
     * 拼接latlon json串网络码的key
     */
    private final static String HOME_MOBILE_NETWORK_CODE_PARAM = "home_mobile_network_code";

    /**
     * cdma制式的常量
     */
    public final static String CDMA_RADIO_TYPE = "cdma";

    /**
     * gsm制式的常量
     */
    public final static String GSM_RADIO_TYPE = "gsm";

    /**
     * 表示是gsm制式的mobileCode常量
     */
    public final static int GSM_MOBILE_CODE = 1;

    /**
     * 表示是cdma制式的mobileCode常量
     */
    public final static int CDMA_MOBILE_CODE = 2;

    /**
     * 基站信息中的DeviceId
     */
    private String imei;

    /**
     * 基站信息中的SubscriberId
     */
    private String imsi;

    /**
     * 基站信息中的运营商
     */
    private String carrier;

    /**
     * 基站信息中的网络制式，这里只区分gsm和cdma两种
     */
    private String radioType;

    /**
     * 基站信息中的cellId，这个只针对gsm用户
     */
    private int cellId;

    /**
     * 获得基站信息的系统时间
     */
    private long time;

    /**
     * 国家码
     */
    private String homeMobileCountryCode;

    /**
     * 网络码
     */
    private String homeMobileNetworkCode;

    /**
     * 基站信息中的定位信息，这个只针对gsm用户
     */
    private int locationAreaCode;

    /**
     * 基站信息中的BaseStationId，这个只针对cdma用户
     */
    private int bid;

    /**
     * 基站信息中的SystemId（系统识别码），这个只针对cdma用户
     */
    private int sid;

    /**
     * 基站信息中的NetworkId（网络标识码），这个只针对cdma用户
     */
    private int nid;

    /**
     * 基站信息中的纬度，这个只针对cdma用户
     */
    private int baseStationLatitude;

    /**
     * 基站信息中的经度，这个只针对cdma用户
     */
    private int baseStationLongitude;

    /**
     * 表示是gsm还是cdma，为1是gsm，为2是cdma
     */
    private int mobileCode;

    /**
     * imei的set方法，给imei赋值
     * @param imei 要给imei赋的值
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * imei的get方法，得到当前的imei
     * @return 当前的imei
     */
    public String getImei() {
        return this.imei;
    }

    /**
     * imsi的set方法，给imsi赋值
     * @param imsi 要给imsi赋的值
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * imsi的get方法，得到当前的imsi
     * @return 当前的imsi
     */
    public String getImsi() {
        return this.imsi;
    }

    /**
     * carrier的set方法，给carrier赋值
     * @param carrier 要给carrier赋的值
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    /**
     * carrier的get方法，得到当前的carrier
     * @return 当前的carrier
     */
    public String getCarrier() {
        return this.carrier;
    }

    /**
     * radioType的set方法，给radioType赋值
     * @param radioType 要给radioType赋的值
     */
    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }

    /**
     * radioType的get方法，得到当前的radioType
     * @return 当前的radioType
     */
    public String getRadioType() {
        return this.radioType;
    }

    /**
     * cellId的set方法，给cellId赋值
     * @param cellId 要给cellId赋的值
     */
    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    /**
     * cellId的get方法，得到当前的cellId
     * @return 当前的cellId
     */
    public int getCellId() {
        return this.cellId;
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
     * homeMobileCountryCode的set方法，给homeMobileCountryCode赋值
     * @param homeMobileCountryCode 要给homeMobileCountryCode赋的值
     */
    public void setHomeMobileCountryCode(String homeMobileCountryCode) {
        this.homeMobileCountryCode = homeMobileCountryCode;
    }

    /**
     * homeMobileCountryCode的get方法，得到当前的homeMobileCountryCode
     * @return 当前的homeMobileCountryCode
     */
    public String getHomeMobileCountryCode() {
        return this.homeMobileCountryCode;
    }

    /**
     * homeMobileNetworkCode的set方法，给homeMobileNetworkCode赋值
     * @param homeMobileNetworkCode 要给homeMobileNetworkCode赋的值
     */
    public void setHomeMobileNetworkCode(String homeMobileNetworkCode) {
        this.homeMobileNetworkCode = homeMobileNetworkCode;
    }

    /**
     * homeMobileNetworkCode的get方法，得到当前的homeMobileNetworkCode
     * @return 当前的homeMobileNetworkCode
     */
    public String getHomeMobileNetworkCode() {
        return this.homeMobileNetworkCode;
    }

    /**
     * locationAreaCode的set方法，给locationAreaCode赋值
     * @param locationAreaCode 要给locationAreaCode赋的值
     */
    public void setLocationAreaCode(int locationAreaCode) {
        this.locationAreaCode = locationAreaCode;
    }

    /**
     * locationAreaCode的get方法，得到当前的locationAreaCode
     * @return 当前的locationAreaCode
     */
    public int getLocationAreaCode() {
        return this.locationAreaCode;
    }

    /**
     * bid的set方法，给bid赋值
     * @param bid 要给bid赋的值
     */
    public void setBid(int bid) {
        this.bid = bid;
    }

    /**
     * bid的get方法，得到当前的bid
     * @return 当前的bid
     */
    public int getBid() {
        return this.bid;
    }

    /**
     * sid的set方法，给sid赋值
     * @param sid 要给sid赋的值
     */
    public void setSid(int sid) {
        this.sid = sid;
    }

    /**
     * sid的get方法，得到当前的sid
     * @return 当前的sid
     */
    public int getSid() {
        return this.sid;
    }

    /**
     * nid的set方法，给nid赋值
     * @param nid 要给nid赋的值
     */
    public void setNid(int nid) {
        this.nid = nid;
    }

    /**
     * nid的get方法，得到当前的nid
     * @return 当前的nid
     */
    public int getNid() {
        return this.nid;
    }

    /**
     * baseStationLatitude的set方法，给baseStationLatitude赋值
     * @param baseStationLatitude 要给baseStationLatitude赋的值
     */
    public void setBaseStationLatitude(int baseStationLatitude) {
        this.baseStationLatitude = baseStationLatitude;
    }

    /**
     * baseStationLatitude的get方法，得到当前的baseStationLatitude
     * @return 当前的baseStationLatitude
     */
    public int getBaseStationLatitude() {
        return this.baseStationLatitude;
    }

    /**
     * baseStationLongitude的set方法，给baseStationLongitude赋值
     * @param baseStationLongitude 要给baseStationLongitude赋的值
     */
    public void setBaseStationLongitude(int baseStationLongitude) {
        this.baseStationLongitude = baseStationLongitude;
    }

    /**
     * baseStationLongitude的get方法，得到当前的baseStationLongitude
     * @return 当前的baseStationLongitude
     */
    public int getBaseStationLongitude() {
        return this.baseStationLongitude;
    }

    /**
     * mobileCode的set方法，给mobileCode赋值
     * @param mobileCode 要给mobileCode赋的值
     */
    public void setMobileCode(int mobileCode) {
        this.mobileCode = mobileCode;
    }

    /**
     * mobileCode的get方法，得到当前的mobileCode
     * @return 当前的mobileCode
     */
    public int getMobileCode() {
        return this.mobileCode;
    }

    /**
     * 将这个数据封装转换成拼接latlon json串的相应gsm json格式，如下面gsm信息后面{}中的内容
     "cell_tower_connected_info":{//gsm信息
     "imei":"354059021137664",
     "imsi":"460022105507870",
     "radio_type":"gsm",//网络类型
     "carrier":"中国移动",//运营商信息
     "cell_id":"2512",
     "location_area_code":"415",
     "home_mobile_conutry_code":"310",
     "home_mobile_network_code":"410",
     "time":"51595352"//获取基站信息的时间点
     }
     * @return 转换后的Json串
     */
    public JSONObject toGsmJsonObject() {

        JSONObject connectedCellInfo = getBaseStationJson();
        try {
            connectedCellInfo.put(CELL_ID_PARAM, String.valueOf(cellId));
            if (Methods.fitApiLevel(5)) {// 只有在api level 5以上才能有locationAreaCode
                connectedCellInfo.put(LOCATION_AREA_CODE_PARAM, String.valueOf(locationAreaCode));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return connectedCellInfo;
    }

    /**
     *  将这个数据封装转换成拼接latlon json串的相应cdma json格式，如下面cdma信息后面{}中的内容
     "cdma_cell_tower_connected_info": {
     "sid": "14206",//系统识别码
     "time": "1314190572260",//获取基站信息的时间点
     "radio_type": "cdma",
     "base_station_longitude": "149130808819",
     "nid": "18",//网络标识码
     "imei": "A00000228854CD",
     "carrier": "46003",
     "imsi": "460036640093553",
     "home_mobile_network_code": "3",
     "base_station_latitude": "149130808819",
     "bid": "4406",//cell id
     "home_mobile_conutry_code": "460"
     }
     * @return 转换后的Json串
     */
    public JSONObject toCdmaJsonObject() {

        JSONObject connectedCdmaCellInfo = getBaseStationJson();
        try {
            if(Methods.fitApiLevel(5)) {// 只有在api level 5以上才能有一下这些信息
                connectedCdmaCellInfo.put(BID_PARAM, String.valueOf(bid));
                connectedCdmaCellInfo.put(SID_PARAM, String.valueOf(sid));
                connectedCdmaCellInfo.put(NID_PARAM, String.valueOf(nid));
                double latDouble = (double)baseStationLatitude /14400;
                double lonDouble = (double)baseStationLongitude /14400;
                long lat=(long) (latDouble * 1000000);
                long lon=(long) (lonDouble * 1000000);
                connectedCdmaCellInfo.put(BASE_STATION_LAT_PARAM, String.valueOf(lat));
                connectedCdmaCellInfo.put(BASE_STATION_LON_PARAM, String.valueOf(lon));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return connectedCdmaCellInfo;
    }

    /**
     *  将这个数据封装转换成拼接latlon json串的相应json格式（这里面只把gsm和cdma共有的部分拼接起来），通过以下两个方法的注释可以看到最终
     *  转化完的样式
     *  @see #toCdmaJsonObject()
     *  @see #toGsmJsonObject()
     *  @return 转换后的Json串
     */
    private JSONObject getBaseStationJson() {
        JSONObject connectedStationInfo = new JSONObject();
        try {
            connectedStationInfo.put(IMEI_PARAM, imei == null ? "" : imei);
            connectedStationInfo.put(IMSI_PARAM, imsi == null ? "" : imsi);
            connectedStationInfo.put(RADIO_TYPE_PARAM, radioType);
            connectedStationInfo.put(CARRIER_PARAM, carrier == null ? "" : carrier);
            connectedStationInfo.put(TIME_PARAM, String.valueOf(time));
            if(homeMobileCountryCode != null && !"".equals(homeMobileCountryCode)) {
                connectedStationInfo.put(HOME_MOBILE_COUNTRY_CODE_PARAM, homeMobileCountryCode);
            }
            if(homeMobileNetworkCode != null && !"".equals(homeMobileNetworkCode)) {
                connectedStationInfo.put(HOME_MOBILE_NETWORK_CODE_PARAM, homeMobileNetworkCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return connectedStationInfo;
    }

    /**
     * 将所有数据恢复到初始化
     */
    public void clear() {

        imei = null;
        imsi = null;
        radioType = null;
        carrier = null;
        time = 0;
        homeMobileCountryCode = null;
        homeMobileNetworkCode = null;
        cellId = 0;
        locationAreaCode = 0;
        bid = 0;
        sid = 0;
        nid = 0;
        baseStationLatitude = 0;
        baseStationLongitude = 0;
        mobileCode = 0;
    }

    /**
     * 判断最新使用的基站和记录下的基站是不是同一个
     * @param rinData 最新使用的基站信息
     * @return 是或者不是
     */
    public boolean equals(RenrenStationData rinData) {

        if(rinData == null) {
            return false;
        }
        String tempRadioType = rinData.getRadioType();
        if(GSM_RADIO_TYPE.equals(tempRadioType)){//要判断是gsm制式还是cdma制式，因为不同的制式判断的条件不同
            return homeMobileCountryCode.equals(rinData.getHomeMobileCountryCode()) &&
                    homeMobileNetworkCode.equals(rinData.getHomeMobileNetworkCode()) &&
                    (locationAreaCode == rinData.getLocationAreaCode()) && (cellId == rinData.getCellId());
        } else if(CDMA_RADIO_TYPE.equals(tempRadioType)){
            return homeMobileCountryCode.equals(rinData.getHomeMobileCountryCode()) &&
                    homeMobileNetworkCode.equals(rinData.getHomeMobileNetworkCode()) &&
                    (sid == rinData.getSid()) && (bid == rinData.getBid()) && (nid == rinData.getNid());
        } else {
            return false;
        }
    }

    /**
     * cdma和gsm得到的信息不一样所以FormatString也不同，前面一组是cdma的样式，后面一组是gsm的样式
     * &cl=cdma|1258|460|61|1|460|1311150533123|
     * &cl=GSM/CDMA标识|BID（基站ID)|NID（网络识别码)|SID（系统识别码)|MNC|MCC|连接时间
     * &cl=gms|61258|46001|40961|1|460|1311150638997|
     * &cl=GSM/CDMA标识|cellid|运营商ID|LAC|MNC|MCC|连接时间
     * @return formatStirng格式
     */
    public String toFormatString(){

        StringBuilder bf = new StringBuilder(STATION_STRING_PARAM);
        String tempRadioType = getRadioType();
        bf.append(tempRadioType).append(RenrenLocationUtil.SEPERATOR);
        if(tempRadioType != null && GSM_RADIO_TYPE.equals(tempRadioType)){
            bf.append(this.cellId).append(RenrenLocationUtil.SEPERATOR)
                .append(this.homeMobileCountryCode).append(this.homeMobileNetworkCode).append(RenrenLocationUtil.SEPERATOR)
                .append(this.locationAreaCode).append(RenrenLocationUtil.SEPERATOR)
                .append(this.homeMobileNetworkCode).append(RenrenLocationUtil.SEPERATOR)
                .append(this.homeMobileCountryCode).append(RenrenLocationUtil.SEPERATOR)
                .append(this.time).append(RenrenLocationUtil.SEPERATOR);
        } else if(tempRadioType != null && CDMA_RADIO_TYPE.equals(tempRadioType)) {
            bf.append(this.bid).append(RenrenLocationUtil.SEPERATOR)
                .append(this.nid).append(RenrenLocationUtil.SEPERATOR)
                .append(this.sid).append(RenrenLocationUtil.SEPERATOR)
                .append(this.homeMobileNetworkCode).append(RenrenLocationUtil.SEPERATOR)
                .append(this.homeMobileCountryCode).append(RenrenLocationUtil.SEPERATOR)
                .append(this.time).append(RenrenLocationUtil.SEPERATOR);
        }
        return bf.toString();
    }

}

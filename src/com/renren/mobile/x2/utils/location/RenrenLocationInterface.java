package com.renren.mobile.x2.utils.location;

/**
 * RenrenLocationInterface
 * @author  xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:40
 * 用于提供给所有界面调用LBS底层数据的方法的接口，这里可以根据不同的需求给予不同的实现来完成自己想要做的事
 */
public interface RenrenLocationInterface {

    /**
     * 表示来至PoiList页面的首次获得定位信息的需求，这样就按照正常逻辑取缓存判断是否可用，
     * 不过定位数据获得后要在后台开启系统定位服务
     */
    public final static int POILIST_PAGE_FIRST_LOCATE = 0x00;

    /**
     * 表示来至PoiList页面的刷新时获得定位信息的需求，直接重新定位获得数据不适用缓存，
     * 同样定位数据获得后要在后台开启系统定位服务
     */
    public final static int POILIST_PAGE_REFRESH_LOCATE = 0x01;

    /**
     * 表示来至位置页面的首次获得定位信息的需求，这样就按照正常逻辑取缓存判断是否可用，
     * 不过存缓存时不发broadcast通知系统侧栏的活动数气泡，这个参数以后可能会去掉，
     * 暂且保留
     */
    public final static int PLACE_PAGE_FIRST_LOCATE = 0x02;

    /**
     * 表示来至位置页面的刷新时获得定位信息的需求，直接重新定位获得数据不适用缓存，
     * 不过存缓存时不发broadcast通知系统侧栏的活动数气泡，这个参数以后可能会去掉，
     * 暂且保留
     */
    public final static int PLACE_PAGE_REFRESH_LOCATE = 0x03;

    /**
     * 表示来至其他页面的首次获得定位信息的需求，这样就按照正常逻辑取缓存判断是否可用
     */
    public final static int OTHER_PAGE_FIRST_LOCATE = 0x04;

    /**
     * 表示来至其他页面的刷新时获得定位信息的需求，直接重新定位获得数据不适用缓存
     */
    public final static int OTHER_PAGE_REFRESH_LOCATE = 0x05;

    /**
     * 表示poiList页面暂时不在前台要停止后台服务的定位服务
     */
    public final static int POILIST_PAGE_STOP_BACKSTAGE_LOCATE = 0x06;

    /**
     * 表示其他需要定位的页面还没有定位成功时离开这个页面，要通知定位模块停止定位
     */
    public final static int OTHER_PAGE_STOP_LOCATE = 0x07;

    /**
     * 这里主要是用来定位得到相应的RenrenLocationData
     * @see RenrenLocationImpl#checkLocation(RenrenLocationManager.RenrenLocationListener listener, int locationType) 这个实现里面有具体说明
     * @param listener
     * @param locationType
     */
    public abstract void checkLocation(RenrenLocationManager.RenrenLocationListener listener, int locationType);


    /**
     * 这里主要是得到缓存中保存的poi信息
     * @see RenrenLocationImpl#getPoiDataCache() 这个实现里有具体说明
     * @return
     */
    public abstract RenrenPoiData getPoiDataCache();

    /**
     * 这里主要是得到缓存中保存的exif poi信息
     * @see RenrenLocationImpl#getExifPoiDataCache() 这个实现里有具体说明
     * @return
     */
    public abstract RenrenPoiData getExifPoiDataCache();

    /**
     * 这里主要是得到缓存中保存的定位信息
     * @see RenrenLocationImpl#getLocationCache() 这个实现里有具体说明
     * @return
     */
    public abstract RenrenLocationData getLocationCache();

    /**
     * 这里主要是将poi信息保存到缓冲中去
     * @see RenrenLocationImpl#savePoiDataCache(RenrenPoiData poiData) 这个实现里有具体说明
     * @param poiData
     */
    public abstract void savePoiDataCache(RenrenPoiData poiData);

    /**
     * 这里主要是将exif poi信息保存到缓冲中去
     * @see RenrenLocationImpl#saveExifPoiDataCache(RenrenPoiData exifPoiData) 这个实现里有具体说明
     * @param exifPoiData
     */
    public abstract void saveExifPoiDataCache(RenrenPoiData exifPoiData);

    /**
     * 这里主要是将位置信息保存到缓冲中去
     * @see RenrenLocationImpl#saveLocationDataCache(RenrenLocationData locationData, boolean fromShuaPao) 这个实现里有具体说明
     * @param locationData
     * @param fromShuaPao
     */
    public abstract void saveLocationDataCache(RenrenLocationData locationData, boolean fromShuaPao);

    /**
     * 这里主要是停止位置定位服务
     * @see RenrenLocationImpl#stopLocation(int locationType) 这个实现里有具体说明
     * @param locationType
     */
    public abstract void stopLocation(int locationType);

    /**
     * 这里主要是自动触发定位的方法
     * @see RenrenLocationImpl#checkLocation() 这个实现里有具体说明
     */
    public abstract void checkLocation();

    /**
     * 这里主要是系统后台的定位服务
     * @see RenrenLocationImpl#startBackstageLocateContinues() 这个实现里有具体说明
     */
    public abstract void startBackstageLocateContinues();

    /**
     * 这里主要是一启动客户端需要调用的方法，主动刷新位置气泡
     * @see RenrenLocationImpl#getNearbyActivityCount(boolean isFromStartApplication) 这个实现里有具体说明
     * @param isFromStartApplication 是否是重新打开人人应用
     */
    public abstract void getNearbyActivityCount(boolean isFromStartApplication);
}

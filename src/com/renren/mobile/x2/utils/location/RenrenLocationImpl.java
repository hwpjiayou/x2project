package com.renren.mobile.x2.utils.location;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * RenrenLocationImpl
 * @author xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:41
 * RenrenLocationInterface的实现，里面主要提供其他页面要调用定位时的各种方法，其他页面调用时
 * 只需要知道这个类中的方法就可以了，当然以后也可以根据不同的用处对RenrenLocationInterface
 * 进行不同的实现
 */
public class RenrenLocationImpl implements RenrenLocationInterface {

    /**
     * 用于标志现在是否已经有自动触发的定位存在
     */
    private static boolean isLocationRequesting;

    /**
     * 用于调用一些系统方法或者给RenrenLocationManager传递用
     */
    private Context mContext;

    /**
     * 人人定位模块的申明
     */
    private RenrenLocationManager mLocationManager;

    /**
     * 构造函数，这里初始化话两个属性的值
     * @param context 用来给mContext赋值
     */
    public RenrenLocationImpl(Context context) {

        mContext = context;
        mLocationManager = RenrenLocationManager.getRenrenLocationManager(mContext);
    }

    /**
     * RenrenLocationInterface中方法的实现，这里主要是用来定位得到相应的RenrenLocationData
     * @see RenrenLocationInterface 中参数的详细介绍
     * @param listener 定位成功后会调用listener的onLocationSucceeded方法，需要注意的是listener中
     *                 要实现的三个方法中尽量写在runOnUiThread中
     * @param locationType 这个表示定位的方式：POILIST_PAGE_FIRST_LOCATE，PLACE_PAGE_FIRST_LOCATE，OTHER_PAGE_FIRST_LOCATE
     *                     这三个都是表示首次请求定位，会根据定位缓存时间来确定是否使用缓存；
     *                     POILIST_PAGE_REFRESH_LOCATE，PLACE_PAGE_REFRESH_LOCATE，OTHER_PAGE_REFRESH_LOCATE
     *                     这三个表示刷新，无论定位缓存中是否有数据都需要重新定位
     *                     POILIST_PAGE_FIRST_LOCATE，POILIST_PAGE_REFRESH_LOCATE
     *                     这两个表示来至poiList页面的请求，这时会在定位结束后打开系统的定位模块，在后台取值
     *                     PLACE_PAGE_FIRST_LOCATE，PLACE_PAGE_REFRESH_LOCATE
     *                     这个是来至位置页这样就不会发送活动数的broadcast（现在需求有变这两组已经没用，不过暂且保留）
     *                     OTHER_PAGE_FIRST_LOCATE，OTHER_PAGE_REFRESH_LOCATE
     *                     大部分页面定位时都传这两组，这时正常定位逻辑，HTML5部分都用这两组即可
     */
    @Override
    public void checkLocation(RenrenLocationManager.RenrenLocationListener listener, int locationType) {

        switch (locationType) {
        case POILIST_PAGE_FIRST_LOCATE:
            checkLocation(listener, false, false, true);
            break;
        case POILIST_PAGE_REFRESH_LOCATE:
            checkLocation(listener, false, true, true);
            break;
        case PLACE_PAGE_FIRST_LOCATE:
            checkLocation(listener, true, false, false);
            break;
        case PLACE_PAGE_REFRESH_LOCATE:
            checkLocation(listener, true, true, true);
            break;
        case OTHER_PAGE_FIRST_LOCATE:
            checkLocation(listener, false, false, false);
            break;
        case OTHER_PAGE_REFRESH_LOCATE:
            checkLocation(listener, false, true, false);
            break;
        default:
            checkLocation(listener, false, false, false);
            break;
        }
    }


    /**
     * RenrenLocationInterface中方法的实现，这里主要是得到缓存中保存的poi信息
     * @return 缓存中的poi信息
     */
    @Override
    public RenrenPoiData getPoiDataCache() {

        return RenrenLocationCache.getRenrenPoiCache(mContext);
    }

    /**
     * RenrenLocationInterface中方法的实现，这里主要是得到缓存中保存的exif poi信息
     * @return 缓存中的exif poi信息
     */
    @Override
    public RenrenPoiData getExifPoiDataCache() {

        return RenrenLocationCache.getRenrenExifPoiCache(mContext);
    }

    /**
     * RenrenLocationInterface中方法的实现，这里主要是得到缓存中保存的定位信息
     * @return 缓存中的定位信息
     */
    @Override
    public RenrenLocationData getLocationCache() {

        return RenrenLocationCache.getRenrenLocationCache(mContext);
    }

    /**
     * RenrenLocationInterface中方法的实现，这里主要是保存poi信息到缓存中
     * @param poiData 要保存到缓存的poi信息
     */
    @Override
    public void savePoiDataCache(RenrenPoiData poiData) {

       RenrenLocationCache.saveRenrenPoiCache(poiData, mContext);
    }

    /**
     * RenrenLocationInterface中方法的实现，这里主要是保存exif poi信息到缓存中
     * @param exifPoiData 要保存到缓存的exif poi信息
     */
    @Override
    public void saveExifPoiDataCache(RenrenPoiData exifPoiData) {

        RenrenLocationCache.saveRenrenExifPoiCache(exifPoiData, mContext);
    }

    /**
     * RenrenLocationInterface中方法的实现，这里主要是保存定位信息到缓存中
     * @param locationData 要保存到缓存的location信息
     * @param fromShuaPao 是不是来至刷泡的页面，这里传false即可，等需求确认不需要这个参数时候会去掉
     */
    @Override
    public void saveLocationDataCache(RenrenLocationData locationData, boolean fromShuaPao) {

        RenrenLocationCache.saveRenrenLocationCache(locationData, mContext, fromShuaPao);
    }

    /**
     * RenrenLocationInterface中方法的实现，这里主要是停止定位，有两种情况poiList页面因为要后台开启系统的定位，所以他要停止系统定位
     * 其他的用于定位数据还没有返回时候离开这个界面，这个逻辑需要各个页面自己来做，这个同样用于poilist页正在定位的时候
     * @see RenrenLocationInterface 参数详解
     * @param locationType 这个表示要停止什么样的定位，POILIST_PAGE_STOP_BACKSTAGE_LOCATE表示停止后台定位服务
     *                     OTHER_PAGE_STOP_LOCATE表示还没有返回定位结果的时候离开这个需要定位的界面
     */
    @Override
    public void stopLocation(int locationType) {

        switch (locationType) {
            case POILIST_PAGE_STOP_BACKSTAGE_LOCATE:
                stopLocation(true);
                break;
            case OTHER_PAGE_STOP_LOCATE:
                stopLocation(false);
                break;
            default:
                stopLocation(false);
                break;
        }
    }

    /**
     * 用于停止定位
     * @param fromPoiList 这个表示要停止什么样的定位，true：停止后台服务；false：停止当前的定位行为
     */
    private void stopLocation(boolean fromPoiList) {

        if(fromPoiList) {
            mLocationManager.stopGPS();
        } else {
            mLocationManager.stopLocateSingle();
        }
    }

    /**
     * 用于定位或者读缓存得到相应的RenrenLocationData
     * @param listener 定位成功后会调用listener的onLocationSucceeded方法，需要注意的是listener中
     *                 要实现的三个方法中尽量写在runOnUiThread中
     * @param fromShuaPao 是否来至刷泡页，主要控制保存缓存时是否想系统侧栏发送活动数的broadcast
     * @param isRefresh 是否是刷新，如果是刷新就不读缓存，直接重新定位
     * @param fromPoiList 是否来至PoiList页面，如果是在获得定位数据后要开启后台的系统定位模块
     */
    private void checkLocation(RenrenLocationManager.RenrenLocationListener listener, boolean fromShuaPao, boolean isRefresh,
                               boolean fromPoiList) {

        if(!isRefresh) {

            RenrenLocationData locationData = getLocationCache();
            Log.v("@@@", "locationData is:" + locationData);
            if(locationData != null) {
                Log.v("@@@", "locationData's time is:" + locationData.getLastLocationTime() + " and now time is:" + System.currentTimeMillis());
            }
            if(locationData != null && locationData.isLocationCacheEffective(System.currentTimeMillis())) {
                listener.onLocationSucceeded(locationData);
                if(fromPoiList) {
                    mLocationManager.startBackstageLocateContinues();
                }
                return;
            }
        }

        mLocationManager.startLocateSingle(listener, fromShuaPao, fromPoiList);
    }

    /**
     * 这里主要是自动触发定位的方法，就是在出发了三种需要自动定位的条件时需要调这个方法，三种条件分别是：
     * 一、新鲜事刷新时
     * 二、又后台重新切换回客户端时
     * 三、客户端在前台锁屏后又重新解锁时
     * 当然触发了这个方法，这个方法自身也会判断，如果现在有缓存并且缓存不需要更新时才会重新定位
     */
    @Override
    public void checkLocation() {

        RenrenLocationManager.RenrenLocationListener listener = new RenrenLocationManager.RenrenLocationListener() {
            @Override
            public void onLocationSucceeded(RenrenLocationData location) {

                isLocationRequesting = false;//定位结果出来后，无论成功与否都需要通知将正在后台定位的标志置为false，下面两个同这个意义
            }

            @Override
            public void onLocatedFailed(String errorMessage, int errorCode) {

                isLocationRequesting = false;
            }

            @Override
            public void onLocatedCancel() {

                isLocationRequesting = false;
            }
        };//这个listener里面什么都不用做只是用来调用系统定位的一个必要参数，以后可能根据需求会做一些处理

        RenrenLocationData locationData = getLocationCache();

        if(locationData != null && !locationData.isNeedRefreshLocationCache(System.currentTimeMillis())) {//如果缓存中有定位数据并且不需要更新
                                                                                                          //就不用去重新定位
            return;
        }

        if(isLocationRequesting) {//如果已经有后台自动定位执行，这里不做任何处理

            return;
        }

        isLocationRequesting = true;//开始后台触发的自动定位的话需要通知其他的触发条件现在不能再定位
        mLocationManager.startLocateSingle(listener, false, false);
    }

    /**
     * 这里就是单纯地将系统的定位服务再后台打开
     */
    @Override
    public void startBackstageLocateContinues() {

        mLocationManager.startBackstageLocateContinues();
    }

    /**
     * 这里主要是一启动客户端需要调用的方法，主动刷新位置气泡,触发条件必须是刚刚启动客户端，或者重新返回Desktop，如果是
     * 刚刚启动客户端的话参数为true，这里实现方法是查看定位缓存是否过期没有过期就将里面的activity气泡数发给侧栏，如果已
     * 经失效重新定位一次并刷新气泡；如果重新返回参数为false，这里只用缓存刷新气泡书
     * @param isFromStartApplication 是否是重新打开人人应用
     */
    @Override
    public void getNearbyActivityCount(boolean isFromStartApplication) {

//        RenrenLocationData locationData = getLocationCache();
//        if(!isFromStartApplication || (locationData != null && locationData.isLocationCacheEffective(System.currentTimeMillis()))) {
//            Intent intent = new Intent(DesktopActivity.POI_ACTIVITY_COUNT_RECEIVER);
//
//            intent.putExtra("count", locationData.getActivityCount());
//            mContext.sendBroadcast(intent);
//            return;
//        }
//
//        RenrenLocationManager.RenrenLocationListener listener = new RenrenLocationManager.RenrenLocationListener() {
//            @Override
//            public void onLocationSucceeded(RenrenLocationData location) {
//
//                isLocationRequesting = false;//定位结果出来后，无论成功与否都需要通知将正在后台定位的标志置为false，下面两个同这个意义
//            }
//
//            @Override
//            public void onLocatedFailed(String errorMessage, int errorCode) {
//
//                isLocationRequesting = false;
//            }
//
//            @Override
//            public void onLocatedCancel() {
//
//                isLocationRequesting = false;
//            }
//        };//这个listener里面什么都不用做只是用来调用系统定位的一个必要参数，以后可能根据需求会做一些处理
//
//        if(isLocationRequesting) {//如果已经有后台自动定位执行，这里不做任何处理
//
//            return;
//        }
//
//        isLocationRequesting = true;//开始后台触发的自动定位的话需要通知其他的触发条件现在不能再定位
//        mLocationManager.startLocateSingle(listener, false, false);
    }
}

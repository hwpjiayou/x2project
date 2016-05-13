package com.renren.mobile.x2.utils.location;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * RenrenLocationCache
 * @author : xiaoguang.zhang
 * Date: 12-6-1
 * Time: 下午5:41
 * 用来保存定位缓存，poi缓存，照片exif poi缓存，以及读取缓存的类，这里面的方法与属性全部为静态的
 */
public class RenrenLocationCache {

    /**
     * 用来保存定位缓存的key
     */
    private static final String PREF_KEY = "LoctionCache";

    /**
     * 用来保存Poi缓存的key
     */
    private static final String POI_KEY = "PoiCache";

    /**
     * 用来保存exif poi缓存的key
     */
    private static final String EXIF_POI_KEY = "ExifPoiCache";

    /**
     * 得到定位缓存的方法
     * @param context
     * @return 缓存中的LocationData，里面包含与定位接口返回的数据以及定位缓存的时间
     */
    protected static RenrenLocationData getRenrenLocationCache(Context context) {

        SharedPreferences sharedPre = context.getSharedPreferences(RenrenLocationConfig.PREF, Context.MODE_PRIVATE);
        String locationDataString = sharedPre.getString(PREF_KEY, null);
        if(locationDataString != null) {
            return RenrenLocationData.parseLocationData(locationDataString);
        }
        return null;
    }

    /**
     * 得到Poi缓存的方法
     * @param context
     * @return 缓存中的poiData，里面包含与poi相关的信息比如pid等
     */
    protected static RenrenPoiData getRenrenPoiCache(Context context) {

        SharedPreferences sharedPre = context.getSharedPreferences(RenrenLocationConfig.PREF, Context.MODE_PRIVATE);
        String poiDataString = sharedPre.getString(POI_KEY, null);
        if(poiDataString != null) {
            return RenrenPoiData.parsePoiData(poiDataString);
        }
        return null;
    }

    /**
     * 得到exif Poi缓存的方法
     * @param context
     * @return 缓存中的exifPoiData，里面包含与exif Poi相关的信息比如pid等
     */
    protected static RenrenPoiData getRenrenExifPoiCache(Context context) {

        SharedPreferences sharedPre = context.getSharedPreferences(RenrenLocationConfig.PREF, Context.MODE_PRIVATE);
        String exifPoiDataString = sharedPre.getString(EXIF_POI_KEY, null);
        if(exifPoiDataString != null) {
            return RenrenPoiData.parsePoiData(exifPoiDataString);
        }
        return null;
    }

    /**
     *
     * @return
     */
    protected synchronized static String getCaller() {

        StringBuffer sb = new StringBuffer();
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement ste = stack[i];
            sb.append(ste.getClassName()).append(".").append(ste.getMethodName()).append("\n");
            sb.append("   ").append(i).append("--").append(ste.getMethodName());
            sb.append("--").append(ste.getFileName());
            sb.append("--").append(ste.getLineNumber()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 保存locationData到缓存的方法
     * @param cache 要保存到缓存的locationData的数据
     * @param context
     * @param fromShuaPao 是否是有关刷泡页过来的数据，是就不发送刷泡的broadcast，不是就需要发，以后这个参数可能需要去掉
     */
    protected synchronized static void saveRenrenLocationCache(RenrenLocationData cache, Context context, boolean fromShuaPao) {

//        if(cache != null && cache.getGpsLat() != RenrenLocationConfig.LATLONDEFAULT && cache.getGpsLon() != RenrenLocationConfig.LATLONDEFAULT) {
//            SharedPreferences sharedPre = context.getSharedPreferences(RenrenLocationConfig.PREF, Context.MODE_PRIVATE);
//            SharedPreferences.Editor sEditor = sharedPre.edit();
//
//            sEditor.putString(PREF_KEY, cache.toLocationJsonObject().toJsonString());
//            sEditor.commit();
//
//            if(!fromShuaPao){
//                Intent intent = new Intent(DesktopActivity.POI_ACTIVITY_COUNT_RECEIVER);
//
//                intent.putExtra("count", cache.getActivityCount());
//                context.sendBroadcast(intent);
//            }
//        }
    }

    /**
     * 保存poiData到缓存的方法
     * @param cache 要保存到缓存的poiData的数据
     * @param context
     */
    protected synchronized static void saveRenrenPoiCache(RenrenPoiData cache, Context context) {

        if(cache != null && cache.getGpsLat() != RenrenLocationConfig.LATLONDEFAULT && cache.getGpsLon() != RenrenLocationConfig.LATLONDEFAULT) {
            SharedPreferences sharedPre = context.getSharedPreferences(RenrenLocationConfig.PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor sEditor = sharedPre.edit();

            sEditor.putString(POI_KEY, cache.toPoiJsonObject().toString());
            sEditor.commit();

        }
    }

    /**
     * 保存exifPoiData到缓存的方法
     * @param cache 要保存到缓存的exifPoiData的数据
     * @param context
     */
    protected synchronized static void saveRenrenExifPoiCache(RenrenPoiData cache, Context context) {

        if(cache != null && cache.getGpsLat() != RenrenLocationConfig.LATLONDEFAULT && cache.getGpsLon() != RenrenLocationConfig.LATLONDEFAULT) {
            SharedPreferences sharedPre = context.getSharedPreferences(RenrenLocationConfig.PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor sEditor = sharedPre.edit();

            sEditor.putString(EXIF_POI_KEY, cache.toPoiJsonObject().toString());
            sEditor.commit();

        }
    }
}

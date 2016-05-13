package com.renren.mobile.x2;

import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;
import com.renren.mobile.x2.db.X2DB;
import com.renren.mobile.x2.utils.ServiceMappingUtil;
import com.renren.mobile.x2.utils.SystemService;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

/**
 * 全局信息。配置在此，在manifest。xml中同样添加了此类的实现。
 * @author Think
 *
 */
public class RenrenChatApplication extends Application {
    public final static String PACKAGE_NAME = "com.renren.mobile.x2";

    /**
     * 用户信息配置(ClientInfo)
     */
    private static String from;
    private static String pubDate;
    private static String softID;
    private static String appID;
    private static String versionName;
    private static String subProperty;
    private static String apiKey;
    private static String secretKey;

    /**
     * 其他全局量
     */
    private static RenrenChatApplication application;
    private static Handler uiHandler = new Handler();
    private static int logPriority = Log.VERBOSE;
    private static String SCREEN = null;
    private static float screendensity;
    private static int screenHeight;
    private static int screenWidth;
    public static boolean sInChatList;
    /**
     * 是否当前在消息列表界面
     */
    public static boolean sInMessageList;
    
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        new X2DB(this);
        init();
//        LoginManager.getInstance().loadAutoLoginUserData();
    }


    public static void init() {
        String packageName = application.getPackageName();
        PackageManager pm = application.getPackageManager();
        ServiceMappingUtil.getInstance().mappingService(SystemService.class, application);
        try {
            final Resources res = application.getResources();
            from = res.getString(R.string.from);
            pubDate = res.getString(R.string.pubdate);
            softID = res.getString(R.string.softid);
            appID = res.getString(R.string.appid);
            subProperty = res.getString(R.string.subproperty);
            apiKey = res.getString(R.string.apikey);
            secretKey = res.getString(R.string.secretkey);
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
            screendensity = application.getResources().getDisplayMetrics().density;
        } catch (NameNotFoundException ignored) {
        }
    }

    public static String getFrom() {
        return from;
    }

    public static String getPubDate() {
        return pubDate;
    }

    public static String getSoftID() {
        return softID;
    }

    public static String getAppID() {
        return appID;
    }

    public static String getVersionName() {
        return versionName;
    }

    public static String getSubProperty() {
        return subProperty;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getScreen() {
        return SCREEN;
    }

    public static RenrenChatApplication getApplication() {
        return application;
    }

    public static Handler getUiHandler() {
        return uiHandler;
    }

    public static int getLogPriority() {
        return logPriority;
    }
    
    public static String getSecretKey() {
        return secretKey;
    }
    
    public static float getdensity(){
    	return screendensity;
    }
    public static void initScreenInfomation(int height, int width){
    	screenHeight = height;
    	screenWidth = width;
    	ErrLog.ll("screenHeight " + screenHeight +"screenWidth " + screenWidth);
    }
    public static int getScreenHeight(){
    	return screenHeight;
    }
    public static int getScreenWidth(){
    	return screenWidth;
    }
}

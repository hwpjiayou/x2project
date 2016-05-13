package com.renren.mobile.x2.utils;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.utils.log.Logger;

import java.util.Locale;

/**
 * author yuchao.zhang
 * description 定义MCS、MAS的服务器地址 定义国内、国际版本类型
 */
public class Config {

    public final static boolean DEVELOPMENT = true;                //TODO 发版之后改成false，用来去掉StrictMode


    public final static int IMG_THREAD_NUM = 3;                 //图片请求最大线程数
    public final static int TEXT_THREAD_NUM = 3;                //文本请求最大线程数
    public final static int HTTP_POST_IMAGE_TIMEOUT = 45000;    //上传图片超时时间，毫秒
    public final static int HTTP_POST_BIN_TIMEOUT = 45000;        //上传文件超时时间，毫秒
    public final static int HTTP_POST_TEXT_TIMEOUT = 45000;        //文本、图片下载请求超时时间，毫秒
    public final static int OFFLINE_MESSAGE_TIMEOUT = 2000;        //离线消息接收超时时间，毫秒
    public final static int V_TYPE = 15;                        //V类型： 用于确定所支持的推送消息类型
    public final static int FAKE_MAS_DELY = 1;                    //本地假数据伪装mas服务器返回数据延时（单位：秒）
    public final static String PREF = "MY_PREF";                //Preference 的名字
    public final static Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    public static String HTTP_SEND_URL;
    public static String HTTP_TALK_URL;
    public final static String SOCKET_URL;
    public final static String HOST_NAME;                //talk服务器地址
    public static String CURRENT_SERVER_URI;        //mas服务器地址
    //public static String MAS_SERVER_IP_URI;         //mas服务器ip地址，未关联域名
    public final static int SOCKET_DEFAULT_PORT;
    public final static int HTTP_DEFAULT_PORT;
    public static boolean IS_ADD_XONLINEHOST;
    public final static String REAL_TALK_URL;
    public final static String REAL_SEND_URL;

    public final static int CMWAP = 1;
    public final static int UNIWAP = 2;
    public static int sNetType;


    private static final int switchTalkServer = 3;

    static {
        CURRENT_SERVER_URI = "http://123.125.42.190:8080/api/sixin/3.0"; //MAS服务器ip地址，未关联域名
        //CURRENT_SERVER_URI = "http://mas.m.renren.com/api/sixin/3.0"; // MAS国内版本的测试服务器
        //MAS_SERVER_IP_URI = "http://123.125.42.190:8080/api/sixin/3.0"; //MAS服务器ip地址，未关联域名
        switch (switchTalkServer) {
            case 1:
                HOST_NAME = "talk.m.renren.com";
                HTTP_DEFAULT_PORT = 80;
                SOCKET_DEFAULT_PORT = 25553;
                break;
            case 3:
                HOST_NAME = "111.13.4.147";
                HTTP_DEFAULT_PORT = 80;
                SOCKET_DEFAULT_PORT = 25553;
                break;
            case 2:
            default:
                HOST_NAME = "10.9.17.147";
                HTTP_DEFAULT_PORT = 12345;
                SOCKET_DEFAULT_PORT = 25554;
                break;
        }

        REAL_TALK_URL = "http://" + HOST_NAME + ":" + HTTP_DEFAULT_PORT
                + "/talk";
        REAL_SEND_URL = "http://" + HOST_NAME + ":" + HTTP_DEFAULT_PORT
                + "/send";
        changeHttpURL();
        SOCKET_URL = HOST_NAME;
    }

    public static void changeHttpURL() {
        Logger.l();
        String proxyHostname = "", proxyPort = "";
        Cursor cursor = null;
        try {
            final Uri apnUri = Uri.parse("content://telephony/carriers/preferapn");
            final String[] apnInfo = {"proxy", "port"};
            cursor = RenrenChatApplication.getApplication()
                    .getContentResolver()
                    .query(apnUri, apnInfo, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                proxyHostname = cursor
                        .getString(cursor.getColumnIndex("proxy"));
                proxyPort = cursor.getString(cursor.getColumnIndex("port"));
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        String url = "http://"
                + (TextUtils.isEmpty(proxyHostname) ? android.net.Proxy.getDefaultHost() : proxyHostname)
                + ":"
                + (TextUtils.isEmpty(proxyPort) ? android.net.Proxy.getDefaultPort() : proxyPort);
        String sendWapUrl = url + "/send";
        String talkWapUrl = url + "/talk";

        ConnectivityManager cm = (ConnectivityManager) RenrenChatApplication
                .getApplication().getSystemService(
                        android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null //TODO Test
                && ("cmwap".equalsIgnoreCase(mobileInfo.getExtraInfo()) ||
                "3gwap".equalsIgnoreCase(mobileInfo.getExtraInfo()) ||
                "uniwap".equalsIgnoreCase(mobileInfo.getExtraInfo()))) {
            HTTP_TALK_URL = talkWapUrl;
            HTTP_SEND_URL = sendWapUrl;
            IS_ADD_XONLINEHOST = true;
            sNetType = "cmwap".equalsIgnoreCase(mobileInfo.getExtraInfo()) ? CMWAP : UNIWAP;
        } else {
            HTTP_TALK_URL = REAL_TALK_URL;
            HTTP_SEND_URL = REAL_SEND_URL;
            IS_ADD_XONLINEHOST = false;
        }
    }
}

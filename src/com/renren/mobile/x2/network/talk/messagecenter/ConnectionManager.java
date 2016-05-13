package com.renren.mobile.x2.network.talk.messagecenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.network.talk.messagecenter.base.*;
import com.renren.mobile.x2.utils.Config;
import com.renren.mobile.x2.utils.SystemService;

import static com.renren.mobile.x2.utils.log.Logger.l;

public final class ConnectionManager extends GetConnectionState {
    private static final ConnectionManager instance = new ConnectionManager();
    private static Connection sConnection = null;
    public static int sNetworkInfo;

    public static ConnectionManager getInstance() {
        return instance;
    }

    private ConnectionManager() {
        Connection.setReconnectStrategy(ReconnectStrategyImpl.getInstance());
    }

    public void start() {
        start(RenrenChatApplication.getApplication(), new ConnectionArgs());
    }

    public void start(ConnectionArgs args) {
        start(RenrenChatApplication.getApplication(), args);
    }

    public void start(Context context) {
        start(context, new ConnectionArgs());
    }

    //TODO test
    public static long time1;

    /**
     * 根据网络情况发起连接
     */
    public void start(Context context, ConnectionArgs args) {
        l("Connection Start");
        time1 = System.currentTimeMillis();
        if (LoginManager.getInstance().isLogout()) {
            return;
        }
        if (args == null) {
            args = new ConnectionArgs();
        }
        NetworkInfo mobileInfo = SystemService.sConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = SystemService.sConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        /** 需要建立的网络连接类型(socket/http) **/
        final int connType = getNetworkType(mobileInfo, wifiInfo, context);
        /** 当前网络连接情况 **/
        final int connInfo = getNetworkInfo(mobileInfo, wifiInfo);
        // 当前网络全部断掉时或者收到单点登录提醒的时候，关闭连接.
        if (connType == Connection.NONETWORK || Connection.sCannotReconnect) {
            if (sConnection != null) {
                l("===============ConnectionManager start disconnect=======================");
                sConnection.disconnect(false);
            }
            sConnection = null;
            l(1, Connection.sCannotReconnect, connType);
            return;
        } else if (sConnection != null) {
            switch (sConnection.mStatus) {
                case Connection.CONNECTED:
                case Connection.CONNECTING:
                    if (connType == sConnection.mType && connInfo == sNetworkInfo) {
                        l("do nothing");
                        return;
                    }
                    break;
                default:
                case Connection.DISCONNECTED:
                    sConnection.disconnect(true);
            }
        }
        sNetworkInfo = connInfo;
        sConnection = null;
        l("New A Connection Object...");
        if (connType == Connection.HTTP) {
            //TODO 貌似联通的超时时间更长些，这里可以适当的延长联通的轮询等待时间，需要确认。。。
            args.mHttpArgs.setValue(ConnectionArgs.HttpArgs.WAITTIME, Config.sNetType == Config.CMWAP ? 20 : 40);
            sConnection = new HttpConnection(this, args);
        } else {
            sConnection = new SocketConnection(this, args);
        }
    }

    /**
     * 发送消息
     */
    public synchronized void sendMessage(IMessage msg) {
        if (sConnection == null) {
            l("===============connectionManager sendMessage connection is null");
            start();
        }
        Connection.sendMessage(msg);
    }

    public synchronized void disConnect(boolean isLogout) {
        if (sConnection != null) {
            sConnection.disconnect(isLogout);
            if (isLogout) {
                sConnection = null;
            }
        }
    }

    /**
     * 判断是否连接建立成功
     *
     * @return true:建立成功 false:建立失败
     */
    public synchronized boolean isConnected() {
        return sConnection != null && Connection.sIsReady.get() && sConnection.mStatus == Connection.CONNECTED;
    }

    public synchronized void onLogout() {
        Connection.cleanMessageQueue();

    }

    /**
     * 根据网络情况判断需要建立的连接
     *
     * @return Connection.NONETWORK:当前无可用网络 Connection.HTTP:建立HTTP连接
     *         Connection.SOCKET:建立Socket连接
     */
    public static int getNetworkType(NetworkInfo mobile, NetworkInfo wifi,
                                     Context context) {
        if (isNetworkNullOrDisconnected(mobile) && isNetworkNullOrDisconnected(wifi)) {
            return Connection.NONETWORK;
        }

        if (!wifi.isConnected() && (!isNetworkNullOrDisconnected(mobile))
                && HttpProxy.getProxyNetProxy(context) != null) {
            return Connection.HTTP;
        }
        return Connection.SOCKET;
    }

    private static boolean isNetworkNullOrDisconnected(NetworkInfo info) {
        return info == null || !info.isConnected();
    }

    public static int getNetworkInfo(NetworkInfo mobile, NetworkInfo wifi) {
        int mobileType = 0;
        int wifiType = 0;
        if (mobile != null && mobile.isConnected()) {
            mobileType = 1;
        }
        if (wifi != null && wifi.isConnected()) {
            wifiType = 2;
        }
        return wifiType | mobileType;
    }

    public static void notifyAppBackGround(boolean isAppForeGround) {
        if (isAppForeGround) {
            instance.start();
        }
        if (sConnection != null) {
            sConnection.onChangeAppGround(isAppForeGround);
        }
    }

}

package com.renren.mobile.x2.network.talk.messagecenter;

import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.network.talk.binder.RemoteServiceBinder;
import com.renren.mobile.x2.network.talk.messagecenter.base.Connection;
import com.renren.mobile.x2.network.talk.messagecenter.base.IReconnectStrategy;
import com.renren.mobile.x2.utils.BackgroundUtils;
import com.renren.mobile.x2.utils.log.Logger;

import java.util.ArrayList;

/**
 * @author yang.chen
 */
public class ReconnectStrategyImpl implements IReconnectStrategy {
    private static final ReconnectStrategyImpl sInstance = new ReconnectStrategyImpl();

    private static final ArrayList<Integer> sReconnectTime = new ArrayList<Integer>();
    private static final int sFinalReconnectTime = 60;

    public static final int SOCKET_FOREGROUND = Connection.SOCKET + (0x1 << 2);
    public static final int SOCKET_BACKGROUND = Connection.SOCKET + (0x0 << 2);
    public static final int HTTP_FOREGROUND = Connection.HTTP + (0x1 << 2);
    public static final int HTTP_BACKGROUND = Connection.HTTP + (0x0 << 2);

    private static void addReconnectTime(final int time, final int count) {
        for (int i = 0; i < count; i++) {
            sReconnectTime.add(time);
        }
    }

    private static int getStatusMask(boolean isForeGround, int type) {
        return type + ((isForeGround ? 1 : 0) << 2);
    }

    static {
        addReconnectTime(5, 3);
        addReconnectTime(20, 3);
        addReconnectTime(-1, 1);//-1标识策略的结束
    }

    private static int getReconnectTime(int count) {
        int time = sReconnectTime.get(Math.min(sReconnectTime.size() - 1, count));
        Logger.l("recount time %d", time);
        return (time == -1 ? sFinalReconnectTime : time) * 1000;
    }

    private ReconnectStrategyImpl() {
    }

    public static ReconnectStrategyImpl getInstance() {
        return sInstance;
    }

    /**
     * 开始重连
     *
     * @param type 网络类型
     */
    @Override
    public void beginReconnect(int type) {
        if (LoginManager.getInstance().isLogout())
            return;
        switch (getStatusMask(RemoteServiceBinder.sIsForeGround, type)) {
            case SOCKET_BACKGROUND:
            case SOCKET_FOREGROUND:
            case HTTP_FOREGROUND:
            case HTTP_BACKGROUND:
                Logger.l("status %s", Integer.toBinaryString(getStatusMask(BackgroundUtils.getInstance().isAppOnForeground(), type)));
                ReConnectUtils.endReconnect();
                ReConnectUtils.beginReconnect(getReconnectTime(Connection.sReconnectCount));
                ++Connection.sReconnectCount;
                break;
//            case HTTP_BACKGROUND: TODO 等待服务器的支持
//                Change to direct polling
//                break;
            default:
                break;
        }
    }
}

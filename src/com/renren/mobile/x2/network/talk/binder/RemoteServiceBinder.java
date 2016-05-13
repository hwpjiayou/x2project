package com.renren.mobile.x2.network.talk.binder;

import android.os.RemoteException;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.network.talk.messagecenter.ConnectionManager;
import com.renren.mobile.x2.network.talk.messagecenter.base.Connection;
import com.renren.mobile.x2.utils.log.Logger;

/**
 * @author dingwei.chen
 */
public class RemoteServiceBinder extends PollBinder.Stub {

    private int mConnectNumber = 0;
    public static boolean sIsForeGround = true;

    @Override
    public void connect() throws RemoteException {
        this.addConnect(1);
    }

    public void addConnect(int offset) {
        this.mConnectNumber += offset;
    }

    @Override
    public void send(long key, String message) throws RemoteException {
        Sender.getInstance().send(key, message);
    }

    @Override
    public boolean isConnect() throws RemoteException {
        return ConnectionManager.getInstance().isConnected();
    }

    @Override
    public void disConnect() throws RemoteException {
        Connection.sCannotReconnect = false;
        ConnectionManager.getInstance().disConnect(true);
        LoginManager.getInstance().clean();
    }

    @Override
    public boolean isRecvOfflineMessage() throws RemoteException {
        return Connection.sIsRecvOfflineMsg;
    }

    @Override
    public void changeAppGround(boolean isForeGround) throws RemoteException {
        Logger.l("app ground:" + isForeGround);
        sIsForeGround = isForeGround;
        ConnectionManager.notifyAppBackGround(isForeGround);
    }

    @Override
    public void onLogout() throws RemoteException {
        ConnectionManager.getInstance().onLogout();
    }

    @Override
    public void notifyNetwork() throws RemoteException {
        ConnectionManager.getInstance().start();
        Connection.sReconnectCount = 0;
    }

}

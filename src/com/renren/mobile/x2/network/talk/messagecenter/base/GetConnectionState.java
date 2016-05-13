package com.renren.mobile.x2.network.talk.messagecenter.base;

import android.content.Intent;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;

public class GetConnectionState {
    public final static int SUCCESS = 1;
    public final static int FAILED = 2;
    public final static int BEGIN_RECONNECT = 3;
    public final static int END_RECONNECT = 4;
    public final static int RECV_OFFLINE_MSG = 5;

    public static void sendBroadcast(int status) {
        if (LoginManager.getInstance().isLogout()) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(RenrenChatApplication.getApplication().getPackageName() + ".SingleLogin");
        intent.putExtra("status", status);
        RenrenChatApplication.getApplication().sendBroadcast(intent);
    }

    public void onConnectSuccess() {
        sendBroadcast(SUCCESS);
    }

    public void onConnectFailed() {
        sendBroadcast(FAILED);
    }

    public void onBeginReonnect() {
        sendBroadcast(BEGIN_RECONNECT);
    }

    public void onEndReonnect() {
        sendBroadcast(END_RECONNECT);
    }

    public void onRecvOffLineMessage() {
        sendBroadcast(RECV_OFFLINE_MSG);
    }

}

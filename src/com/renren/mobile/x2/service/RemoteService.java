package com.renren.mobile.x2.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.network.talk.binder.RemoteServiceBinder;
import com.renren.mobile.x2.network.talk.messagecenter.ConnectionManager;
import com.renren.mobile.x2.network.talk.messagecenter.ReConnectUtils;
import com.renren.mobile.x2.network.talk.messagecenter.base.Connection;
import com.renren.mobile.x2.utils.Config;
import com.renren.mobile.x2.utils.log.Logger;

import java.util.ArrayList;
import java.util.LinkedList;

public class RemoteService extends Service {
    public static final RemoteServiceBinder BINDER = new RemoteServiceBinder();

    public static void sendReceivedMessage(LinkedList<? extends XMPPNode> nodes) {
        Intent i = new Intent(RenrenChatApplication.getApplication().getPackageName() + PushMessagesReceiver.ACTION_NAME);
        i.putExtra(PushMessagesReceiver.TYPE, PushMessagesReceiver.GET_MESSAGE);
        i.putExtra(PushMessagesReceiver.DATA, nodes);
        RenrenChatApplication.getApplication().sendBroadcast(i);
    }

    public static void sendFailedMessage(ArrayList<Long> data) {
        if (data == null || data.isEmpty())
            return;
        Intent i = new Intent(RenrenChatApplication.getApplication().getPackageName() + PushMessagesReceiver.ACTION_NAME);
        i.putExtra(PushMessagesReceiver.TYPE, PushMessagesReceiver.MESSAGE_FAILED);
        i.putExtra(PushMessagesReceiver.DATA, data);
        RenrenChatApplication.getApplication().sendBroadcast(i);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.l();
        if (!LoginManager.getInstance().isLogout()) {
            this.startPollThread();// 开启轮询
        }
        if (Config.DEVELOPMENT && Build.VERSION.SDK_INT >= 10) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build());
        }
        Config.changeHttpURL();
        ReConnectUtils.registerReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.l();
        this.startPollThread();// 开启轮询
        return super.onStartCommand(intent, flags, startId);
    }

    private void startPollThread() {
        Connection.sCannotReconnect = false;
        ConnectionManager.getInstance().start(
                RenrenChatApplication.getApplication());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ReConnectUtils.unregisterReceiver();
        System.exit(0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return BINDER.asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean flag = super.onUnbind(intent);
        BINDER.addConnect(-1);// 减少一个连接数
        return flag;
    }

    public static interface ConnHandler {
        /**
         * 接受一个服务器消息
         *
         * @param nodes 服务器返回的消息
         * @return 返回false表示允许其他ChatHandler接受本消息，返回true表示要霸占这个消息，消息不向下传递
         */
        boolean receive(LinkedList<? extends XMPPNode> nodes);
    }

}

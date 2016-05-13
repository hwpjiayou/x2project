package com.renren.mobile.x2.network.talk.messagecenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.network.talk.messagecenter.base.Connection;
import com.renren.mobile.x2.network.talk.messagecenter.base.ConnectionArgs;
import com.renren.mobile.x2.utils.SystemService;
import com.renren.mobile.x2.utils.log.Logger;

/**
 * User: kent
 * Time:  11/6/12 11:55 AM
 */
public final class ReConnectUtils extends BroadcastReceiver {
    private static final ReConnectUtils instance = new ReConnectUtils();
    private static PendingIntent mPendingIntent = null;
    private static ConnectionArgs connectionArgs = null;

    public static final void beginReconnect(long startTime) {
        Logger.l("startTime = %d", startTime);
        Context context = RenrenChatApplication.getApplication();
        Intent intent = new Intent(context.getPackageName()
                + ".reconnection");
        mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        SystemService.sAlarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + startTime,
                mPendingIntent);
    }

    public static final void beginReconnect(long startTime, ConnectionArgs args) {
        connectionArgs = args;
        beginReconnect(startTime);
    }

    public static final void endReconnect() {
        try {
            if (mPendingIntent != null) {
                SystemService.sAlarmManager.cancel(mPendingIntent);
            }
            mPendingIntent = null;
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.l("ReConnectionReciver(isLogout:%s)", LoginManager.getInstance().isLogout());
        if (!LoginManager.getInstance().isLogout() && !Connection.sCannotReconnect)
            ConnectionManager.getInstance().start(RenrenChatApplication.getApplication(), connectionArgs);
        else
            endReconnect();
    }

    public static void registerReceiver() {
        IntentFilter filter = new IntentFilter(
                RenrenChatApplication.getApplication().getPackageName() + ".reconnection");
        RenrenChatApplication.getApplication().registerReceiver(
                instance, filter);
    }

    public static void unregisterReceiver() {
        try {
            RenrenChatApplication.getApplication().unregisterReceiver(instance);
        } catch (Exception ignored) {
        }
    }
}

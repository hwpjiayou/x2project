package com.renren.mobile.x2.network.talk.messagecenter.base;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.network.talk.messagecenter.ConnectionManager;
import com.renren.mobile.x2.network.talk.messagecenter.LoginErrorException;
import com.renren.mobile.x2.service.RemoteService;
import com.renren.mobile.x2.utils.Config;
import com.renren.mobile.x2.utils.log.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.renren.mobile.x2.network.talk.messagecenter.base.Utils.addItemWithNotify;
import static com.renren.mobile.x2.utils.log.Logger.l;

/**
 * @author yang-chen
 */
public abstract class Connection extends Thread {
    public static String sCommonBuildString = null;
    public static final int DISCONNECTED = 0x00;
    public static final int CONNECTING = 0x01;
    public static final int CONNECTED = 0x10;
    public static final int NONETWORK = 0x00;
    public static final int SOCKET = 0x01;
    public static final int HTTP = 0x10;
    public static final int MAX_SEND_TIMES = 3;        //最大重发次数
    public static final long timeout = 59000;        //超时时间
    public static final AtomicBoolean sIsReady = new AtomicBoolean(false);
    public static boolean sIsRecvOfflineMsg = false;
    public static volatile boolean sCannotReconnect = false;
    public static int sReconnectCount = 0;
    public static PollMessageTaskThread sMessageTaskThread = null;
    protected static final MessageQueue sMessageTasks = new MessageQueue();
    protected static final MessageQueue sMessageTimeoutCheckQueue = new MessageQueue();
    protected static WatchTimeOutThread sCheckTimeOutThread = null;
    private static IReconnectStrategy sReconnectStrategy = null;
    protected static Context sContext = RenrenChatApplication.getApplication();

    public ConnectionManager mConnectionManager = null;
    public volatile int mStatus = DISCONNECTED;
    public volatile int mType = NONETWORK;

    private static class PollMessageTaskThread extends Thread {
        public Connection mConn = null;

        @Override
        public void run() {
            assert (mConn != null);
            l("PollMessageTaskThread start");
            while (true) {
                synchronized (sMessageTasks) {
                    try {
                        sMessageTasks.wait();
                    } catch (InterruptedException ignored) {
                        l("on Interrupt");
                    }
                }
                ArrayList<Long> failedList = new ArrayList<Long>();
                Iterator<IMessage> iter = sMessageTasks.iterator();
                while (iter.hasNext()) {
                    IMessage msg = iter.next();
                    if (msg.getFailureTimes() > MAX_SEND_TIMES) {
                        failedList.add(iter.next().getKey());
                        iter.remove();
                    }
                }
                RemoteService.sendFailedMessage(failedList);
                if (sIsReady.get() && mConn != null && !sMessageTasks.isEmpty())
                    mConn.onSendMessageToNet();
            }
        }
    }

    /**
     * 超时检测线程
     */
    private static class WatchTimeOutThread extends Thread {
        private final byte[] LOCK = new byte[0];
        private IMessage currentMsg = null;
        private long waitStartTime = -1;

        public void removeKey(long key) {
            if (currentMsg != null && currentMsg.getKey() == key) {
                synchronized (LOCK) {
                    LOCK.notify();
                }
                return;
            }
            sMessageTimeoutCheckQueue.remove(key);
        }

        @Override
        public void run() {
            while (true) {
                IMessage msg = null;
                synchronized (sMessageTimeoutCheckQueue) {
                    if (!sMessageTimeoutCheckQueue.isEmpty()) {
                        msg = sMessageTimeoutCheckQueue.remove(0);
                    } else {
                        try {
                            sMessageTimeoutCheckQueue.wait();
                        } catch (InterruptedException ignored) {
                        }
                        continue;
                    }
                }
                if (msg == null) {
                    continue;
                }
                currentMsg = msg;
                long waitTime = timeout - (waitStartTime == -1 ? 0 : Math.abs(System.currentTimeMillis() - msg.getAddTime()));
                waitStartTime = System.currentTimeMillis();
                if (waitTime > 0) {
                    synchronized (LOCK) {
                        try {
                            l("wait time:%d", waitTime);
                            LOCK.wait(waitTime);
                        } catch (InterruptedException ignored) {
                            Logger.l("on Interrupt");
                        }
                    }
                    l("finish wait");
                }
                IMessage message = sMessageTasks.getMessageWithKey(msg.getKey());
                if (message != null) {
                    message.addFailureTimes();
                    message.setSendingStatus(false);
                }
            }
        }
    }

    /**
     * 假如当前网络状况不对，会直接return 开始连接会在线程池中执行
     */
    public Connection(ConnectionManager connectionManage) {
        if (TextUtils.isEmpty(sCommonBuildString))
            sCommonBuildString = String
                    .format(" to='talk.sixin.com' v='%d' c_appid='%s' c_fromid='%s' c_version='%s' xml:lang='zh_CN' ",
                            Config.V_TYPE, RenrenChatApplication.getAppID(),
                            RenrenChatApplication.getFrom(),
                            "2.2.0 Beta");
        sIsRecvOfflineMsg = false;
        if (sMessageTaskThread == null) {
            sMessageTaskThread = new PollMessageTaskThread();
            sMessageTaskThread.mConn = this;
            sMessageTaskThread.start();
        }
        sMessageTaskThread.mConn = this;

        if (sCheckTimeOutThread == null) {
            sCheckTimeOutThread = new WatchTimeOutThread();
            sCheckTimeOutThread.start();
        }
        mConnectionManager = connectionManage;
        mStatus = CONNECTING;
        start();
    }

    @Override
    public void run() {
        mConnectionManager.onBeginReonnect();
        l("on begin connect(%d)", System.currentTimeMillis() - ConnectionManager.time1);
        onBeginConnection();
    }

    protected abstract void onBeginConnection();

    protected abstract void onSendMessageToNet();

    protected abstract void onDisconnect(boolean isLogout);

    protected abstract String getNodeStr(int nodeType, Object... args);

    public void onChangeAppGround(boolean isAppForegound) {
    }

    /**
     * 断开连接
     *
     * @param isLogout 用来表示是否会发送注销请求。 true:会发送注销请求
     */
    public synchronized void disconnect(boolean isLogout) {
        sCannotReconnect = isLogout;
        this.onDisconnect(isLogout);
    }


    /**
     * 发送消息，唤醒进程池中的发送消息的线程，由池中的线程发送队列中的所有消息
     *
     * @param msg 要发送的消息
     */
    public static void sendMessage(IMessage msg) {
        addItemWithNotify(sMessageTasks, msg);
    }

    /**
     * 在重新发送前清空消息队列和进程池
     */
    public static void cleanMessageQueue() {
        ArrayList<Long> data = new ArrayList<Long>();
        synchronized (sMessageTasks) {
            while (!sMessageTasks.isEmpty()) {
                data.add(sMessageTasks.poll().getKey());
            }
            sMessageTasks.notify();
        }
        RemoteService.sendFailedMessage(data);
    }

    protected final synchronized void onConnectionLost(Exception e) {
        e.printStackTrace();
        l("onConnectionLost:%s isLogout(%s)", e.toString(), String.valueOf(LoginManager.getInstance().isLogout()));
        if (LoginManager.getInstance().isLogout()) {
            return;
        }
        mConnectionManager.onConnectFailed();
        mStatus = DISCONNECTED;
        onDisconnect(false);
        if (!sCannotReconnect) {
            sReconnectStrategy.beginReconnect(mType);
        }
    }

    protected final void onRecvMessage(LinkedList<? extends XMPPNode> e) {
        if (e == null || e.isEmpty()) {
            return;
        }
//		if ("message".equals(e.tag) TODO 单点登录还有离线消息没处理。。。
//				&& "alert".equals(e.getAttr("type"))
//				&& e.getFirstChild("action") != null
//				&& "terminate"
//						.equals(e.getFirstChild("action").getAttr("type"))) {
//			try {
//				String alert = e.getFirstChild("body").getFirstChild("text").text;
//				loginErrorNotification(alert);
//			} catch (Exception exception) {
//				exception.printStackTrace();
//			}
//		} else if ("message".equals(e.tag) && "EOOM".equals(e.getAttr("type"))) {
//			sIsRecvOfflineMsg = true;
//			ConnectionManager.sGetConnectionState
//					.iter(ConnectionManager.sOnRecvOfflineIter);
//		}
        for (XMPPNode node : e) {
            String id = node.getId();
            if (!TextUtils.isEmpty(id)) {
                Long key = Long.parseLong(id);
                sMessageTasks.remove(key);
                sCheckTimeOutThread.removeKey(key);
            }
        }
        RemoteService.sendReceivedMessage(e);
        e.clear();
    }

    protected final void loginErrorNotification(LoginErrorException e) {
        e.printStackTrace();
        if (sCannotReconnect)
            return;
        sCannotReconnect = true;
        LoginManager.getInstance().logout();
        disconnect(true);
        Intent intent = new Intent();
        intent.setAction(sContext.getPackageName() + ".SingleLogin");
        sContext.sendBroadcast(intent);
    }

    /**
     * 用于当前线程阻塞，等待更新完成。 假如已经连接上，会直接返回。
     */
    protected static void waitForReady() {
        while (!sIsReady.get()) {
            synchronized (sIsReady) {
                try {
                    sIsReady.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * 唤醒由于没有连接上而等待的线程
     */
    protected final void onConnectReady() {
        l("======onConnectReady(%d)", (System.currentTimeMillis() - ConnectionManager.time1));
        mStatus = CONNECTED;
        sIsReady.set(true);
        sIsRecvOfflineMsg = true;
        sCannotReconnect = false;
        mConnectionManager.onConnectSuccess();
        sMessageTaskThread.mConn = this;
        sReconnectCount = 0;
        synchronized (sIsReady) {
            sIsReady.notifyAll();
        }
        synchronized (sMessageTasks) {
            sMessageTasks.notify();
        }
    }

    public static void setReconnectStrategy(IReconnectStrategy reconnectStrategy) {
        sReconnectStrategy = reconnectStrategy;
    }

}

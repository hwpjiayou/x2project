package com.renren.mobile.x2.components.message.notification;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.message.object.BaseMessageModel;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.dao.MessageDAO;
import com.renren.mobile.x2.emotion.EmotionString;
import com.renren.mobile.x2.utils.BackgroundUtils;
import com.renren.mobile.x2.utils.log.Logger;

/**
 * author yuchao.zhang
 * <p/>
 * 消息Push的Notification管理类
 */
public class MessageNotificationManager {

    private static MessageNotificationManager mMessageNotificationManager = new MessageNotificationManager();

    public static MessageNotificationManager getInstance() {
        return mMessageNotificationManager;
    }

    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private HandlerThread mMessageHandlerThread;
    private MessageHandler mMessageHandler;
    private MessageNotificationModel mNotificationModel;
    public static final int MESSAGE_NOTIFICATION_ID = 6362024;
    public static final int THE_LATEST_MESSAGE = 1;

    private Context mContext = RenrenChatApplication.getApplication();

    MessageDAO mMessageDao = DAOFactoryImpl.getInstance().buildDAO(
            MessageDAO.class);

    private MessageNotificationManager() {

        initMessageHandlerThread();
        initMessageNotificationModel();
    }

    /**
     * 初始化 消息Push的线程、Handler
     */
    private void initMessageHandlerThread() {
        mMessageHandlerThread = new HandlerThread("handler message thread",
                Process.THREAD_PRIORITY_BACKGROUND);
        mMessageHandlerThread.start();
        mMessageHandler = new MessageHandler(mMessageHandlerThread.getLooper());
    }

    /**
     * 初始化 消息Push的NotificationModel
     */
    public void initMessageNotificationModel() {
        MessageDAO mMessageDao = DAOFactoryImpl.getInstance().buildDAO(
                MessageDAO.class);
        //TODO 暂时传的uid为0，需要修改
        List<BaseMessageModel> messageList = mMessageDao
                .queryAll(0L);
        mNotificationModel = new MessageNotificationModel();
        //TODO 暂时注释掉了
//        mNotificationModel.setUnReadMessageList(messageList);
    }

    /**
     * 获取mNotificationModel
     */
    public synchronized MessageNotificationModel getMessageNotificationModel() {
        if (mNotificationModel == null) {
            initMessageNotificationModel();
        }
        return mNotificationModel;
    }

    /**
     * 处理Push到的新消息
     */
    public void handleNewMessage(List<BaseMessageModel> messages) {
        List<BaseMessageModel> messageItems = new ArrayList<BaseMessageModel>();
        if (mMessageHandlerThread == null || !mMessageHandlerThread.isAlive()) {
            initMessageHandlerThread();
        }
        BaseMessageModel m = null;
        try {
            for (int k = 0; k < messages.size(); k++) {
                m = messages.get(k);
                messageItems.add(m);
            }
        } catch (Exception e) {
        }

        Message message = mMessageHandler.obtainMessage();
        message.obj = messageItems;
        message.sendToTarget();
    }

    /**
     * 增加一个最新的通知 消息Push
     */
    public void addNotification(BaseMessageModel mMessage) {
        if (mNotificationModel == null) {
            initMessageNotificationModel();
        }
        //TODO 暂时注释掉了
//        mNotificationModel.addNotificaiton(mMessage);
    }

    public void sendNotification(boolean needRoll) {

        //如果应用程序在后台而且消息通知关闭，不发送notification
        if (!BackgroundUtils.getInstance().isAppOnForeground()) {
            if (Logger.mDebug) {
                Logger.logd(Logger.RECEVICE_MESSAGE, "程序已经退出 不发送通知");
            }
            return;
        } else {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotification = new Notification();

            /* 有数据 */
            if (mNotificationModel != null) {

                /* 消息数大于0 */
                if (mNotificationModel.getCount() > 0) {
                    if (needRoll) {
                        int color = mContext.getResources().getColor(R.color.message_notification);
                        int interval = 200;
                        /* 设置LED灯 */
                        mNotification.ledARGB = color;
                        mNotification.ledOnMS = interval;
                        mNotification.ledOffMS = interval;
                        mNotificationManager.cancel(MESSAGE_NOTIFICATION_ID);
                        mNotification.defaults = Notification.DEFAULT_VIBRATE;
                        mNotification.tickerText = mNotificationModel.getMessageUserName(THE_LATEST_MESSAGE)+ ":"
                                + new EmotionString(mNotificationModel.getMessageContent(THE_LATEST_MESSAGE)).getStringWithOutSpecialString("(表情)");
                    }
                    mNotification.icon = R.drawable.test_default;
                    mNotification.when = mNotificationModel.getMessageDate(1);
                    mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                    mNotification.number = mNotificationModel.getCount();

                    Intent cancel = new Intent(mContext, MessageNotificationCancel.class);
                    cancel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    cancel.setAction("" + System.currentTimeMillis());

                    String messageName = mNotificationModel
                            .getMessageUserName(THE_LATEST_MESSAGE) != null ? mNotificationModel
                            .getMessageUserName(THE_LATEST_MESSAGE) : "";

                    String messageContent = mNotificationModel
                            .getMessageContent(THE_LATEST_MESSAGE) != null ? mNotificationModel
                            .getMessageContent(THE_LATEST_MESSAGE) : "";

                    mNotification.setLatestEventInfo(mContext,
                            mNotificationModel.getCount() + "条未读的消息Push", messageName + ":"        //MessageNotificationManager_java_1=条未读消息;
                            + new EmotionString(messageContent).getStringWithOutSpecialString("表情"),
                            PendingIntent.getActivity(mContext, 0, cancel, 0));

                    mNotificationManager.notify(MESSAGE_NOTIFICATION_ID,
                            mNotification);
                } else {/* 没有消息 */

                    mNotificationManager.cancel(MESSAGE_NOTIFICATION_ID);
                }
            }
        }
    }

    /**
     * 通知的栈
     */
    private final class MessageHandler extends Handler {

        public MessageHandler(Looper looper) {
            super(looper);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            final ArrayList<BaseMessageModel> messageList = (ArrayList<BaseMessageModel>) msg.obj;
            final int count = messageList.size();

            int outToLocalMessageCount = 0;

            //TODO 删除操作 监听者
//            chatHistoryDao.cancelRepeatMessageList(messageList);
            for (BaseMessageModel message : messageList) {
                //TODO 暂时注释掉了
//                if (message.mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL
//                        && message.mIsSuccessInsert
//                        && message.mRead == ChatBaseItem.MESSAGE_READ.UNREAD
//                        && message.mMessageType != ChatBaseItem.MESSAGE_TYPE.SOFT_INFO) {
//                    outToLocalMessageCount++;
//                    addNotification(message);
//                }
            }

            //TODO 插入操作 监听者
            for(int i = 0; i < messageList.size(); i++) {
                mMessageDao.insertMessage(messageList.get(i));
            }
           


            if (Logger.mDebug) {
                Logger.logd(Logger.RECEVICE_MESSAGE, "count=" + count + "#outToLocalMessageCount=" + outToLocalMessageCount);
            }

            /* 消息的列表中有数据 */
            if (count > 0) {
                if (RenrenChatApplication.sInChatList) {/* 在消息主界面 */
                    if (Logger.mDebug) {
                        Logger.logd(Logger.RECEVICE_MESSAGE, "在消息主界面不用发送 消息Push的通知");
                    }
                } else {/* 不在消息主界面 */
                    if (Logger.mDebug) {
                        Logger.logd(Logger.RECEVICE_MESSAGE, "不在消息主界面发送通知 outToLocalMessageCount=" + outToLocalMessageCount);
                    }
                    if (outToLocalMessageCount != 0) {
                        sendNotification(true);
                    }
                }
            }
        }
    }
}

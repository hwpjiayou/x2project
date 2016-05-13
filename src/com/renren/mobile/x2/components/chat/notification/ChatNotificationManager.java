package com.renren.mobile.x2.components.chat.notification;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.db.dao.ChatHistoryDAO;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.emotion.EmotionString;
import com.renren.mobile.x2.utils.BackgroundUtils;
import com.renren.mobile.x2.utils.RRSharedPreferences;
import com.renren.mobile.x2.utils.log.Logger;

import java.util.ArrayList;
import java.util.List;


public class ChatNotificationManager
		 {
	private static ChatNotificationManager mMessageNoiticationManager = new ChatNotificationManager();
	private List<ChatMessageWarpper> mGroupMessageListCache = new ArrayList<ChatMessageWarpper>();
	private Notification mNoitification;
	private NotificationManager mNoitificationManager;
	private HandlerThread mMessageHandlerThread;
	private MessageHandler mMessageHandler;
	private ActivityManager mActivityManager;
	private ChatNotificationModel mNoitificationModel;
	private RRSharedPreferences rRSharedPreferences;
	public static final int MESSAGE_NOTIFICATION_ID = 6362023;
	public static final int SINGLE_LOGIN_NOTIFICATION_ID = 6362027;
	public static final int THE_LASTEST_MESSAGE = 1;
	ChatHistoryDAO chatHistoryDao = DAOFactoryImpl.getInstance().buildDAO(
			ChatHistoryDAO.class);
	private ChatNotificationManager() {
		mActivityManager = (ActivityManager) RenrenChatApplication
				.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
		initMessageHanlderThread();
		initMessageNotificationModel();
	}
	private void initMessageHanlderThread() {
		mMessageHandlerThread = new HandlerThread("handler message thread",
				Process.THREAD_PRIORITY_BACKGROUND);
		mMessageHandlerThread.start();
		mMessageHandler = new MessageHandler(mMessageHandlerThread.getLooper(),
				RenrenChatApplication.getUiHandler());
	}
	/**
	 * 初始化mNoitificationModel
	 * */
	public void initMessageNotificationModel() {
		ChatHistoryDAO chatHistoryDao = DAOFactoryImpl.getInstance().buildDAO(
				ChatHistoryDAO.class);
		ArrayList<ChatMessageWarpper> unReadMessageList = chatHistoryDao
				.queryUnreadMessageList();
		mNoitificationModel = new ChatNotificationModel();
		mNoitificationModel.setUnReadMessageList(unReadMessageList);
	}
	/**
	 * 获取mNoitificationModel
	 * */
	public synchronized ChatNotificationModel getMessageNotificationModel() {
		if (mNoitificationModel == null) {
			initMessageNotificationModel();
		}
		return mNoitificationModel;
	}
	public static int i;
	/***
	 * 处理消息轮询到的新信息或者新的SMS信息
	 * */
	public void handleNewMessage(List<ChatMessageWarpper> messages) {
		List<ChatMessageWarpper> singleChatMessageList = new ArrayList<ChatMessageWarpper>();
		if (mMessageHandlerThread == null || !mMessageHandlerThread.isAlive()) {
			initMessageHanlderThread();
		}
		ChatMessageWarpper m = null;
		try {
			for ( int k = 0;k<messages.size(); k++) {
				m = messages.get(k);
				singleChatMessageList.add(m);
			}
		} catch (Exception e) {}
		
		Message message = mMessageHandler.obtainMessage();
		message.obj = singleChatMessageList;
		message.sendToTarget();
	}
	
	public void clearCache(){
		mGroupMessageListCache.clear();
	}
	/**
	 * 增加一个最新的通知 chat
	 * */
	public void addNotification(ChatMessageWarpper chatMessage) {
		if (mNoitificationModel == null) {
			initMessageNotificationModel();
		}
		mNoitificationModel.addNotificaiton(chatMessage);
	}
	/**
	 * 通过一个人的id来清空相关通知
	 * */
	public void removeNotificationByGroupId(long id) {
		if (mNoitificationModel != null) {
			mNoitificationModel.removeNotificationByGroupId(id);
		}
	}
	public void removeNotificationByMessageId(long id) {
		if (mNoitificationModel != null) {
			mNoitificationModel.removeNotificationByMessageId(id);
		}
	}
	/**
	 * 清空mNoitificationModel chat
	 * */
	public void clearMessageNotificationModel() {
		mNoitificationModel = null;
	}
	public static ChatNotificationManager getInstance() {
		return mMessageNoiticationManager;
	}
	
	/**单点登录通知 */
	public void sendSingleLoginNotification(Context context, String content){
		mNoitificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		rRSharedPreferences = new RRSharedPreferences(context);
//		RemoteViews contentView = new RemoteViews(context.getPackageName(),R.layout.single_login_notification);  
//		contentView.setImageViewResource(R.id.icon, R.drawable.icon);
//		contentView.setTextViewText(R.id.title, content);
		mNoitification = new Notification(R.drawable.test_default,content,System.currentTimeMillis());
		mNoitification.ledARGB = 0xFF0000ff;
		mNoitification.ledOnMS = 200;
		mNoitification.ledOffMS = 200;
		mNoitificationManager.cancel(MESSAGE_NOTIFICATION_ID);
		
		//SettingDataManager.getInstance().obtainSwitchState();
//		int flag = SettingDataManager.getInstance().mRemindState;
		/*
		 * 设置声音 震动,详见SettingScreen
		 * @see com.renren.mobile.chat.ui.setting.SettingScreen
		 */
//		mNoitification.defaults = flag;
		mNoitification.tickerText = content;
//		mNoitification.contentView = contentView;
			
		mNoitification.icon = R.drawable.test_default;
		mNoitification.when = System.currentTimeMillis();
		mNoitification.flags |= Notification.FLAG_AUTO_CANCEL;
		
//		Intent cancelIntent1 = new Intent(context,SingleLoginNotificationCancel.class);
//		cancelIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		cancelIntent1.setAction("" + System.currentTimeMillis());
//		
//		mNoitification.contentIntent = PendingIntent.getActivity(context, 0, cancelIntent1, 0);
//		mNoitification.setLatestEventInfo(context,RenrenChatApplication.getApplication().getText(R.string.chat_notify_name),content,PendingIntent.getActivity(context, 0, cancelIntent1, 0));
//		mNoitificationManager.notify(SINGLE_LOGIN_NOTIFICATION_ID,
//					mNoitification);
	}
	
	public void sendNotification(Context context, boolean needRoll) {
		//SettingDataManager.getInstance().obtainSwitchState();
		//如果应用程序在后台而且消息通知关闭，不在发送notification
		if(!BackgroundUtils.getInstance().isAppOnForeground()){
			if(Logger.mDebug){
				Logger.logd(Logger.RECEVICE_MESSAGE,"程序已经退出 不发送通知");
			}
			return;
		}else{
			mNoitificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			rRSharedPreferences = new RRSharedPreferences(context);
			mNoitification = new Notification();
			if(mNoitificationModel != null){
				if (mNoitificationModel.getCount() > 0) {
					if (needRoll) {
						mNoitification.ledARGB = 0xFF0000ff;
						mNoitification.ledOnMS = 200;
						mNoitification.ledOffMS = 200;
						mNoitificationManager.cancel(MESSAGE_NOTIFICATION_ID);
						//int flag = SettingDataManager.getInstance().mRemindState;
						/*
						 * 设置声音 震动,详见SettingScreen
						 * @see com.renren.mobile.chat.ui.setting.SettingScreen
						 */
						
						//如果设置中开启震动 或者 声音加震动  都为Notification属性设置成震动 
						//if(flag == 2 || flag == 3)
						{
							mNoitification.defaults = 2;
						}
						mNoitification.tickerText = mNoitificationModel
								.getMessageUserName(THE_LASTEST_MESSAGE)
								+ ":"
								+ new EmotionString(mNoitificationModel.getMessageContent(THE_LASTEST_MESSAGE)).getStringWithOutSpecialString("(表情)");
						
					}
					mNoitification.icon = R.drawable.v1_logo;
					mNoitification.when = mNoitificationModel.getMessageDate(1);
					mNoitification.flags = Notification.FLAG_AUTO_CANCEL;
					mNoitification.number = mNoitificationModel.getCount();
					Intent cancelIntent1 = new Intent(context,
							ChatNotificationCancel.class);
					cancelIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					cancelIntent1.setAction("" + System.currentTimeMillis());
					String messageName = mNoitificationModel
							.getMessageUserName(THE_LASTEST_MESSAGE) != null ? mNoitificationModel
							.getMessageUserName(THE_LASTEST_MESSAGE) : "";
					String messageContent = mNoitificationModel
							.getMessageContent(THE_LASTEST_MESSAGE) != null ? mNoitificationModel
							.getMessageContent(THE_LASTEST_MESSAGE) : "";
					mNoitification.setLatestEventInfo(context,
							mNoitificationModel.getCount() + ChatUtil.getText(R.string.chat_unread_num), messageName + ":"		//MessageNotificationManager_java_1=条未读消息; 
									+ new EmotionString(messageContent).getStringWithOutSpecialString(ChatUtil.getText(R.string.chat_emotion)),
							PendingIntent.getActivity(context, 0, cancelIntent1, 0));
					mNoitificationManager.notify(MESSAGE_NOTIFICATION_ID,
							mNoitification);
				} else {
					mNoitificationManager.cancel(MESSAGE_NOTIFICATION_ID);
				}
			}
		}
	}
	
	public synchronized void clearAllNotification(Context context) {
		mNoitificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNoitificationManager.cancel(MESSAGE_NOTIFICATION_ID);
	}
	
	public synchronized void clearSingleLoginNotification(Context context) {
		mNoitificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNoitificationManager.cancel(SINGLE_LOGIN_NOTIFICATION_ID);
	}
	/**
	 * 清空通知栏的所有关于id为uid的用户聊天的notification 在聊天界面调用
	 * **/
	public void clearChatNotification(final Context context, long groupId) {
		if (mNoitificationModel != null) {
			mNoitificationModel.removeNotificationByGroupId(groupId);
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					sendNotification(context, false);
				}
			});
		}
	}
	public void clearGroupNotification(final Context context, long groupId) {
		if (mNoitificationModel != null) {
			mNoitificationModel.removeNotificationByGroupId(groupId);
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					sendNotification(context, false);
				}
			});
		}
	}
	public void sendNotification(final Context context) {
		if (mNoitificationModel != null) {
			if (mNoitificationModel.getCount() > 0) {
				RenrenChatApplication.getUiHandler().post(new Runnable() {
					@Override
					public void run() {
						sendNotification(context, false);
					}
				});
			}
		}
	}
	public void clearUnReadMessageList() {
		if (mNoitificationModel != null) {
			mNoitificationModel.clearUnReadMessageList();
		}
	}
	
	private final class MessageHandler extends Handler {
		
		public MessageHandler(Looper looper, Handler handler) {
			super(looper);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			final ArrayList<ChatMessageWarpper> messageList = (ArrayList<ChatMessageWarpper>) msg.obj;
			final int count = messageList.size();
			
			int outToLocalMessageCount = 0;
			chatHistoryDao.cancelRepeatMessageList(messageList);
			//SystemUtil.log("handle", "3:"+count);
			for (ChatMessageWarpper message : messageList) {
				if (message.mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL
						&& message.mIsSuccessInsert
						&& message.mRead == ChatBaseItem.MESSAGE_READ.UNREAD
						&& message.mMessageType != ChatBaseItem.MESSAGE_TYPE.SOFT_INFO) {
					outToLocalMessageCount++;
					// message.mRead = ChatBaseItem.MESSAGE_READ.UNREAD;
					addNotification(message);
				}
			}
			//SystemUtil.log("handle", "4:"+count);
			/* 在数据库插入的时候做未读标志判断和去重判断 */
			
			chatHistoryDao.insertChatMessageList(messageList);
			//SystemUtil.log("handle", "5:"+count);
			if(Logger.mDebug){
				Logger.logd(Logger.RECEVICE_MESSAGE,"count="+count+"#outToLocalMessageCount="+outToLocalMessageCount);
			}
			if (count > 0) {
				if(RenrenChatApplication.sInChatList){
					if(Logger.mDebug){
						Logger.logd(Logger.RECEVICE_MESSAGE,"在聊天主界面不用发送通知");
					}
				}else{
					if(Logger.mDebug){
						Logger.logd(Logger.RECEVICE_MESSAGE,"不在聊天主界面发送通知 outToLocalMessageCount="+outToLocalMessageCount);
					}
					if (outToLocalMessageCount != 0) {
						sendNotification(RenrenChatApplication.getApplication(), true);
					}
				}
			}
		}
	}
	

	
}

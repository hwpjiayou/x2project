package com.renren.mobile.x2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.network.talk.MessageManager;
import com.renren.mobile.x2.utils.Config;
/**
 * 监听一下broadcast的receiver
 * 手机启动broadcast
 * 网络连接状态broadcast
 * 收发sms broadcast
 * */


public class RenRenChatReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(TextUtils.isEmpty(RenrenChatApplication.getFrom())){
			RenrenChatApplication.init();
		}
		Config.changeHttpURL();
		MessageManager.startService();
	}
}

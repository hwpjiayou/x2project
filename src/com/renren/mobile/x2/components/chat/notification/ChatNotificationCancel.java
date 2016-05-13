package com.renren.mobile.x2.components.chat.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.util.ContactModel;
import com.renren.mobile.x2.components.login.LoginActivity;
import com.renren.mobile.x2.utils.log.Logger;




public class ChatNotificationCancel extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Logger.mDebug){
			Logger.logd(Logger.RECEVICE_MESSAGE,"点击通知  NotificationModelsize = "+ ChatNotificationManager.getInstance().getMessageNotificationModel().getCount());
		}
		if (ChatNotificationManager.getInstance().getMessageNotificationModel().getCount()>0) {
			ChatNotificationModel messageNotificationModel = ChatNotificationManager.getInstance().getMessageNotificationModel();
			ContactModel model = new ContactModel();
			model.setmName(messageNotificationModel.getMessageUserName(1));
			model.setUid(messageNotificationModel.getMessageUserId(1));
			model.setUrl(messageNotificationModel.getHeadUrl(1));
			RenRenChatActivity.show(this, model);
			finish();
		} else {
			finish();
			Intent intent = new Intent(this,LoginActivity.class);
			this.startActivity(intent);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

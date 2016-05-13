package com.renren.mobile.x2.components.chat.util;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
public class ResendDialog {
	AlertDialog mDialog = null;
	ChatMessageWarpper mMessage = null;
	public ResendDialog(Context context) {
		Builder builder =new  AlertDialog.Builder(context);
		builder.setTitle(ChatUtil.getText(R.string.chat_resend_message));		//ResendDialog_java_1=重新发送消息; 
		builder.setPositiveButton(ChatUtil.getText(R.string.ok), new OnClickListener() {		//VoiceOnClickListenner_java_3=确定; 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mMessage!=null){
					mMessage.resend();
				}
				mDialog.dismiss();
			}
		});
		builder.setNegativeButton(ChatUtil.getText(R.string.cancel), new OnClickListener() {		//ChatMessageWarpper_FlashEmotion_java_4=取消; 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDialog.dismiss();
			}
		});
		mDialog = builder.create();
	}
	public void update(ChatMessageWarpper message){
		this.mMessage = message;
		mDialog.show();
	}
}

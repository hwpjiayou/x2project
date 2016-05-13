package com.renren.mobile.x2.components.chat.util;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper.OnLongClickCommandMapping;
import com.renren.mobile.x2.utils.log.Logger;


public class ItemLongClickDialogProxy {

	RenRenChatActivity mActivity;
	ChatMessageWarpper mMessage;
	AlertDialog.Builder mDialogBuilder;
	AlertDialog mDialog = null;

	public static enum LONGCLICK_COMMAND {
		DELETE,      // 删除
		CANCEL,      // 取消
		COPY,        // 拷贝
		RESEND,      // 重新发送
		REDOWNLOAD,  // 重新下载
		FORWARD      // 转发
	}

	public ItemLongClickDialogProxy(RenRenChatActivity activity) {
		mActivity = activity;
		mDialogBuilder = new AlertDialog.Builder(activity);
	}

	public void updateModelSelect(ChatMessageWarpper message) {
		this.mMessage = message;
	}

	public void show() {
		final List<OnLongClickCommandMapping> list = mMessage.getOnClickCommands();
		if(list==null){
			return;
		}
		final String[] texts = new String[list.size()];
		int i = 0;
		for (OnLongClickCommandMapping m : list) {
			texts[i++] = m.mText;
		}
		mDialog = mDialogBuilder.setItems(texts, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				OnLongClickCommandMapping m = list.get(which);
				processCommand(m.mCommand);
			}
		}).create();
		mDialog.show();
	}

	public void dismiss() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	private void processCommand(LONGCLICK_COMMAND command) {
		switch (command) {
		case DELETE:
			deleteMessage(mMessage);
			break;
		case COPY:
			copyMessage(mMessage);
			break;
		case RESEND:
			resendMessage(mMessage);
			break;
		case REDOWNLOAD:
			redownloadMessage(mMessage);
			break;
		case FORWARD:
//			//转发 TODO
//			RenrenChatApplication.sForwardMessage = mMessage;
//			/**
//			 * 原来的跳转
//			 * MultiChatForwardActivity.show(mActivity,null);
//			 * */
//			Intent i = new Intent(mActivity, D_SelectGroupChatContactActivity.class);
//			i.putExtra(PARAMS.COMEFROM, COMEFROM.CHAT_FEED_REBOLG);
//			i.putExtra(RenRenChatActivity.PARAM_NEED.CONTEXT_FROM, mActivity.getClass().getSimpleName());
//			i.putExtra(RenRenChatActivity.PARAM_NEED.IS_GET_MESSAGE, true);
//			mActivity.startActivity(i);
			
			break;
		default:

			break;
		}
	}

	/* 删除消息 */
	public void deleteMessage(ChatMessageWarpper message) {
		ChatDataHelper.getInstance().deleteToTheDatabase(mMessage);
		mMessage.onDelete();
	}

	/* 复制 */
	public void copyMessage(ChatMessageWarpper message) {
		ChatDataHelper.getInstance().copyTheMessage(mActivity, message.getMessageContent());

	}

	/* 重新发送 */
	public void resendMessage(ChatMessageWarpper message) {
		mActivity.mResendDialog.update(mMessage);

	}

	/* 重新下载 */
	public void redownloadMessage(final ChatMessageWarpper message) {
		if(Logger.mDebug){
			Logger.errord("去掉注视");
		}
//		AlertDialog.Builder builder = new AlertDialog.Builder(GlobalValue.getInstance().getCurrentActivity());
//		AlertDialog dialog = builder.setMessage("是否重新下载").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				message.download(true);
//			}
//		}).setNegativeButton("取消", null).create();
//		dialog.show();

	}

}

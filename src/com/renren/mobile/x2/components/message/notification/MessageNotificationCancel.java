package com.renren.mobile.x2.components.message.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.renren.mobile.x2.components.comment.CommentActivity;
import com.renren.mobile.x2.components.home.profile.ProfileActivity;
import com.renren.mobile.x2.utils.log.Logger;

/**
 * author yuchao.zhang
 *
 * 取消消息Push显示的工具页面
 *
 */
public class MessageNotificationCancel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int count = MessageNotificationManager.getInstance().getMessageNotificationModel().getCount();

        if(Logger.mDebug){
            Logger.logd(Logger.RECEVICE_MESSAGE,"点击通知  NotificationModelsize = "+ count);
        }

        if (count > 0) {
            MessageNotificationModel messageNotificationModel = MessageNotificationManager.getInstance().getMessageNotificationModel();
            //TODO 以前是跳转聊天Push：聊天页面的代码，现应更改为跳评论列表页面、跳个人主页、跳消息列表的代码
//            ContactModel model = new ContactModel();
//            model.setmName(messageNotificationModel.getMessageUserName(1));
//            model.setUid(messageNotificationModel.getMessageUserId(1));
//            model.setUrl(messageNotificationModel.getHeadUrl(1));
//            RenRenChatActivity.show(this, model);
            finish();
        } else {
            finish();
            //TODO 不知道为什么跳登录页面，产品的需求目前不明
//            Intent intent = new Intent(this,RealLoginActivity.class);
//            this.startActivity(intent);
        }
    }

    /**
     * 跳转到个人主页
     * @param uid
     */
    private void jumpToProfile(String uid){

        ProfileActivity.show(MessageNotificationCancel.this, uid);
    }

    /**
     * 跳转到评论列表页面
     */
    private void jumpToComment(){

        Intent intent = new Intent();
        intent.setClass(MessageNotificationCancel.this,CommentActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("feedmodel",null);
        bundle.putInt("feed_layout_id", 0);
        intent.putExtras(bundle);

        MessageNotificationCancel.this.startActivity(intent);

    }
}

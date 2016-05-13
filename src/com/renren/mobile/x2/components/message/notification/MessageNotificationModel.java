package com.renren.mobile.x2.components.message.notification;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.message.object.BaseMessageModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * author yuchao.zhang
 *
 * 消息Push的模型
 */
public class MessageNotificationModel {

    /**
     * 未读消息的列表
     */
    private ArrayList<BaseMessageModel> mMessageList =new ArrayList<BaseMessageModel>();
    /**
     * 用来去重的列表
     */
    private Set<Long> idSet = new HashSet<Long>();

    public MessageNotificationModel(){

    }

    public MessageNotificationModel(ArrayList<BaseMessageModel> unReadMessageList) {
        this.mMessageList = unReadMessageList;
    }

    public void setUnReadMessageList(ArrayList<BaseMessageModel> unReadMessageList){
        this.mMessageList = unReadMessageList;
    }

    public ArrayList<BaseMessageModel> getUnReadMessageList(){
        return this.mMessageList;
    }

    public synchronized int getCount(){
        return this.mMessageList.size();
    }

    public boolean checkMessageIndex(int id){
        if(id>getCount()){
            return false;
        }else{
            return true;
        }

    }

    /***
     * 获取通知的具体内容，最新的一条从id = 1开始
     * **/
    public String getMessageUserName(int id){
        if(!checkMessageIndex(id)){
            return null;
        }
        return this.mMessageList.get(getCount()-id).getName();
    }

    public String getMessageContent(int id){
        if(!checkMessageIndex(id)){
            return null;
        }
        String content = this.mMessageList.get(getCount()-id).getContent();
        int type = 0;
        //TODO 需要在MessageItem中添加Type
//        int type = this.mMessageList.get(getCount()-id).getType();
        if(type == ChatBaseItem.MESSAGE_TYPE.VOICE){
            content = "["+ ChatUtil.getText(R.string.chat_voice)+"]";
        }else if(type == ChatBaseItem.MESSAGE_TYPE.IMAGE){
            content = "["+ChatUtil.getText(R.string.chat_image)+"]";
        }else if(type == ChatBaseItem.MESSAGE_TYPE.FLASH){
            content = "["+ChatUtil.getText(R.string.chat_emotion)+"]";
        }
        return content;
    }

    public long getMessageDate(int id){
        if(!checkMessageIndex(id)){
            return 0;
        }
        return this.mMessageList.get(getCount()-id).getTime();
    }

    public String getHeadUrl(int id){
        if(!checkMessageIndex(id)){
            return null;
        }
        return this.mMessageList.get(getCount()-id).getHeadUrl();
    }
}

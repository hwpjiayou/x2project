package com.renren.mobile.x2.components.message.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.actions.base.Action;
import com.renren.mobile.x2.components.message.notification.MessageNotificationManager;
import com.renren.mobile.x2.components.message.object.BaseMessageModel;
import com.renren.mobile.x2.components.message.object.MessageModelFactory;
import com.renren.mobile.x2.core.xmpp.node.Message;

/**
 * author yuchao.zhang
 *
 * 消息推送 接收器
 */
public class PushMessage extends Action<Message> {

    public PushMessage() {
        super(Message.class);
    }

    /**
     * 消息的数据队列
     */
    private List<BaseMessageModel> mMessages;

    /**
     * 解析的全过程（老方法，已经不再使用）
     * @param node
     */
    @Override
    public void processAction(Message node) {

        beginAction();
        parserSingleMessage(node);
        commitAction();
    }

    /**
     * 解析的全过程（新方法）
     * @param nodeList 要解析的数据
     */
    @Override
    public void batchProcessAction(List<Message> nodeList) {

        beginAction();

        for (Message message : nodeList) {
            parserSingleMessage(message);
        }

        commitAction();
    }

    /**
     * 检查是否为消息推送的类型
     * @param node
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkActionType(Message node) throws Exception {
        
        return node!=null &&
        "notification".equals(node.mType);
    }

    /**
     * 初始化数据
     */
    private void beginAction(){

        mMessages = new ArrayList<BaseMessageModel>();
    }

    /**
     * 解析单条消息
     * @param node
     */
    private void parserSingleMessage(Message node){
        //TODO 这里需要封装数据
//        ChatMessageWarpper message = ChatMessageFactory.getInstance().obtainMessage(node.mBody.type);
        BaseMessageModel message = MessageModelFactory.getInstance().getMessageModel(node);
        if(message != null) {
            this.basicParser(message, node);
            message.swapDataFromXML(node);
            mMessages.add(message);
        }
       
    }

    /**
     * 数据封装
     * @param message
     * @param m
     */
    private void basicParser(BaseMessageModel message,Message m){
        //TODO 需要转化数据
        message.setName( m.getFromName() );
    }

    /**
     * 时间的类型转换
     * @param time
     * @return
     */
    public long getTime(String time){
        try {
            return Long.parseLong(time);
        } catch (Exception e) {
            return -1L;
        }
    }

    /**
     * 通知Notification
     */
    public void commitAction() {
        RenrenChatApplication.getUiHandler().post(new Runnable() {
            public void run() {
                MessageNotificationManager.getInstance()
                        .handleNewMessage(mMessages);
            }
        });
    }
}

package com.renren.mobile.x2.db.dao;

import com.renren.mobile.x2.components.message.object.BaseMessageModel;
import com.renren.mobile.x2.components.message.object.BaseMessageModel.MESSAGE_STATE;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.DAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.db.sql.QueryMessageList;
import com.renren.mobile.x2.db.table.MessageColumn;

import java.util.LinkedList;
import java.util.List;

public class MessageDAO extends BaseDAO{
    
    private List<MessageObserver> mObservers = new LinkedList<MessageObserver>();

    public MessageDAO(BaseDBTable table) {
        super(table);
    }
    
    Query mQueryMessageList = new QueryMessageList(this);
    
    /**
     * @说明 查询所有的消息
     * 
     * @param uid 表示的是当前登录用户的ID
     */
    public List<BaseMessageModel> queryAll(long uid) {
        String where = MessageColumn.LOCAL_USER_ID + "=" + uid ;
        @SuppressWarnings("unchecked")
        List<BaseMessageModel> messages = mQueryMessageList.query(null, where, null,
                    MessageColumn.IS_READ + ", " + MessageColumn.TIME + DAO.ORDER.DESC,
                    null, List.class);
        
        return messages;
    }
    
    /**
     * 插入一条消息信息  说明 让模型自己告诉数据库自己要插入那些数据
     * */

    public void insertMessage(BaseMessageModel message) {
        if(message==null){
            return;
        }
        if(message.time<=0L){
            message.time = System.currentTimeMillis();
        }
        message.id = (int)this.insert(message.getValueToInsert());
        
        for(int i = 0; i < mObservers.size(); i++) {
            mObservers.get(i).onInsert(message);
        }
    }
    
    /**
     * 删除某个人的聊天记录
     * */
    public void deleteMessageByUserId(long uid) {
        String where = MessageColumn.LOCAL_USER_ID + "=" + uid ;
        this.delete2(where);
//        this.onDelete(ChatHistory_Column.TO_CHAT_ID, id);
    }
    
    /**
     * 将消息从未读变为已读, 此处逻辑为： 对于新鲜事id相同的信息，统一状态置为已读，若没有新鲜事则把单条置为已读
     * @param uid
     * @param message
     */
    public void updateMessageState(long uid, BaseMessageModel message) {
        
        String queryWhere = MessageColumn.LOCAL_USER_ID
                + "="
                + uid
                + " and "
                + (message.feedId == 0 ? MessageColumn._ID + "=" + message.id
                        : MessageColumn.FEED_ID + "=" + message.feedId) + " and "
                + MessageColumn.IS_READ + " =" + MESSAGE_STATE.UNREAD;
        
//        List<MessageItem> messages = (List<MessageItem>) this.query1(mQueryMessageList, null, where, null, null, null);
        @SuppressWarnings("unchecked")
        List<BaseMessageModel> messages = mQueryMessageList.query(null, queryWhere, null, null, null, List.class);
        
        for(BaseMessageModel m: messages) {
            m.setIsRead(MESSAGE_STATE.READ);
            
            String[] where = {MessageColumn.LOCAL_USER_ID + "=" + uid,
                    MessageColumn._ID + "=" + m.id };
             
            this.update1(where, m.getValueToInsert());
        }
        
    }
    
    
    public void registorObserver(MessageObserver observer){
        if(!this.mObservers.contains(observer)){
            this.mObservers.add(observer);
        }
    }
    
    public void unregistorObserver(MessageObserver observer){
        this.mObservers.remove(observer);
    }

}

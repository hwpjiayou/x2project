package com.renren.mobile.x2.components.message.object;

import android.content.ContentValues;
import android.database.Cursor;

import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.db.table.ChatList_Column;
import com.renren.mobile.x2.db.table.MessageColumn;

/**
 * author yuchao.zhang
 *
 * 消息 数据结构
 */
public abstract class BaseMessageModel {
  
    public static final String MESSAGE_HEAD_URL = "head_url";
    public static final String MESSAGE_USER_NAME = "user_name";
    public static final String MESSAGE_CONTENT = "title";
    public static final String MESSAGE_TIME = "time";
    
    public static interface MESSAGE_STATE{
        int READ = 0;//已读消息
        int UNREAD= 1;//未读消息
    }
    
    public static interface MESSAGE_TYPE {
        int COMMENT = 0; //评论消息
        int LIKE = 1; //顶消息
        int DELETE_POST = 2; //删帖消息
        int SYSTEM = 3;//系统消息
    }
    
    /**
     * 消息ID
     */
    @ORM(mappingColumn=ChatList_Column._ID)
    public long id;

    /**
     * 头像地址
     */
    @ORM(mappingColumn=MessageColumn.HEAD_URL)
    public String headUrl;

    /**
     * 名字
     */
    @ORM(mappingColumn=MessageColumn.USER_NAME)
    public String name;

    /**
     * 内容
     */
    @ORM(mappingColumn=MessageColumn.TEXT_CONTENT)
    public String content;

    /**
     * 时间
     */
    @ORM(mappingColumn=MessageColumn.TIME)
    public long time;
    
    @ORM(mappingColumn=MessageColumn.IS_READ)
    public int isRead = MESSAGE_STATE.UNREAD;
    
    @ORM(mappingColumn=MessageColumn.MESSAGE_TYPE)
    public int messageType;
    
    /**
     * 新鲜事ID
     */
    @ORM(mappingColumn=MessageColumn.FEED_ID)
    public long feedId;
    
    /**
     * @return the feedId
     */
    public long getFeedId() {
        return feedId;
    }

    /**
     * @param feedId the feedId to set
     */
    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    /**
     * 新鲜事图片地址
     */
    public String imageUrl;

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void node(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    
    
    /**
     * @return the mMessageId
     */
    public long getId() {
        return id;
    }

    /**
     * @param mMessageId the mMessageId to set
     */
    public void setId(long id) {
        this.id = id;
    }
    
//    /**
//     * @return the feedId
//     */
//    public long getFeedId() {
//        return feedId;
//    }
//
//    /**
//     * @param feedId the feedId to set
//     */
//    public void setFeedId(long feedId) {
//        this.feedId = feedId;
//    }

    /**
     * @return the isRead
     */
    public int getIsRead() {
        return isRead;
    }

    /**
     * @param isRead the isRead to set
     */
    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    /**
     * 数据库相关
     * */
    //数据库解析(公共解析)
    public void parseMessageFromDatabase(Cursor cursor){
        ORMUtil.getInstance().ormQuery(getClass(), this, cursor);
    }
    
    //数据库插入数据对(公共数据)
    public ContentValues getValueToInsert(){
        ContentValues values = new ContentValues();
        ORMUtil.getInstance().ormInsert(getClass(), this, values);
        return values;
    }

    public abstract void swapDataFromXML(Message message);
    
    public long getTime(String time) {
        try {
            return Long.parseLong(time);
        } catch (Exception e) {
            return 0;
        }

    }
    
}

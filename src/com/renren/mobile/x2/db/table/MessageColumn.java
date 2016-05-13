package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.Column;
import com.renren.mobile.x2.core.db.DatabaseColumn;
import com.renren.mobile.x2.core.db.DatabaseTypeConstant;

/**
 * 
 * 消息表的各字段定义
 * @author Liu Xu
 * @version 1.0.0
 */
public interface MessageColumn  extends DatabaseColumn{

    @Column(defineType=DatabaseTypeConstant.INT+" "+DatabaseTypeConstant.PRIMARY)
    public static final String _ID = "_id"; 
    
    @Column(defineType=DatabaseTypeConstant.LONG)
    public static final String LOCAL_USER_ID = "local_user_id";
    
    @Column(defineType=DatabaseTypeConstant.LONG)
    public static final String USER_ID  = "user_id";
    
    @Column(defineType=DatabaseTypeConstant.TEXT)
    public static final String USER_NAME = "user_name";
    
    @Column(defineType=DatabaseTypeConstant.TEXT)
    public static final String HEAD_URL = "head_url";
    
    @Column(defineType=DatabaseTypeConstant.LONG)
    public static final String TIME = "time";
    
    @Column(defineType=DatabaseTypeConstant.TEXT)
    public static final String TEXT_CONTENT = "text_content";
    
    @Column(defineType=DatabaseTypeConstant.TEXT)
    public static final String AUDIO_CONTENT_URL = "audio_content_url";
    
    @Column(defineType=DatabaseTypeConstant.INT)
    public static final String IS_READ = "is_read";
    
    @Column(defineType=DatabaseTypeConstant.INT)
    public static final String MESSAGE_TYPE = "message_type";
    
    @Column(defineType=DatabaseTypeConstant.LONG)
    public static final String FEED_ID = "feed_id";
    
    
}

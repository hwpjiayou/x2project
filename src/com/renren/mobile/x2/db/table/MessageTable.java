package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.BaseDB;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.Table;
import com.renren.mobile.x2.db.dao.MessageDAO;

/**
 * 
 * 消息记录表
 * @author Liu Xu
 * @version 1.0.0
 */

@Table(
        TableName=MessageTable.TABLE_NAME, //表名
        TableColumns = MessageColumn.class,//表对应的列
        DAO = MessageDAO.class
    )

public class MessageTable  extends BaseDBTable{
    
    public final static String TABLE_NAME="message";//表名
    
    public MessageTable(BaseDB database) {
        super(database);
    }

  
}


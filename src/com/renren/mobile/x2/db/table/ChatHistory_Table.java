package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.BaseDB;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.Table;
import com.renren.mobile.x2.db.dao.ChatHistoryDAO;




/**
 * @author dingwei.chen
 * @说明 聊天记录表
 * */
@Table(
	TableName=ChatHistory_Table.TABLE_NAME, //表名
	TableColumns = ChatHistory_Column.class,//表对应的列
	DAO = ChatHistoryDAO.class
)
public class ChatHistory_Table extends BaseDBTable{
	
	public final static String TABLE_NAME="chat";//表名

	public ChatHistory_Table(BaseDB database) {
		super(database);
	}


	
}

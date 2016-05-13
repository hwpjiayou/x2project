package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.BaseDB;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.Table;
import com.renren.mobile.x2.db.dao.ChatListDAO;
/**
 * @author niulu
 * @说明 对话表
 * */
@Table(
		TableName=ChatListTable.TABLE_NAME, //表名
		TableColumns = ChatList_Column.class,//表对应的列
		DAO = ChatListDAO.class
	)

public class ChatListTable extends BaseDBTable{
	public final static String TABLE_NAME="renren_chatlist_table";//表名

	public ChatListTable(BaseDB database) {
		super(database);
		// TODO Auto-generated constructor stub
	}

}

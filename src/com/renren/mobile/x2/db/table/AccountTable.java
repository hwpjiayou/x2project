package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.BaseDB;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.Table;
import com.renren.mobile.x2.db.dao.AccountDAO;

@Table(
		TableName=AccountTable.TABLE_NAME, //表名
		TableColumns = AccountColumn.class,//表对应的列
		DAO = AccountDAO.class
)
public class AccountTable extends BaseDBTable {
	
	public final static String TABLE_NAME = "renren_account_table";

	public AccountTable(BaseDB database) {
		super(database);
	}
	
}

package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.BaseDB;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.Table;
import com.renren.mobile.x2.db.dao.NearByFriendDAO;

@Table(
		TableName = NearByfTable.TABLE_NAME, 
		TableColumns = NearByfColumn.class,// 表对应的列
		DAO = NearByFriendDAO.class
		)
public class NearByfTable extends BaseDBTable {
	public final static String TABLE_NAME = "renren_nearbyfriends_table";

	public NearByfTable(BaseDB database) {
		super(database);
	}

}

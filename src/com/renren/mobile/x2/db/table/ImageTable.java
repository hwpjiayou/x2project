package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.BaseDB;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.Table;
import com.renren.mobile.x2.db.dao.ImageDAO;

@Table(
		TableName=ImageTable.TABLE_NAME, //表名
		TableColumns = ImageColumn.class,//表对应的列
		DAO = ImageDAO.class
)
public class ImageTable extends BaseDBTable {
	
	public final static String TABLE_NAME = "renren_image_table";

	public ImageTable(BaseDB database) {
		super(database);
	}
	
}

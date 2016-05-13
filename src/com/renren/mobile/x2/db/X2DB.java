package com.renren.mobile.x2.db;

import android.content.Context;
import com.renren.mobile.x2.core.db.BaseDB;
import com.renren.mobile.x2.core.db.Database;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.table.*;


@Database(
		tables = {
			AccountTable.class,
			ImageTable.class,
			ChatListTable.class,
			NearByfTable.class,
			ChatHistory_Table.class,
			MessageTable.class,
            LocationNameTable.class
		}
)
public class X2DB extends BaseDB{
	public static final String DATABASE_NAME="x2.db";

	public static final int DATABASE_VERSION = 20;

	public X2DB(Context context) {
		super(context,
			  DATABASE_NAME,
			  DATABASE_VERSION,
			  DAOFactoryImpl.getInstance());
	}
}

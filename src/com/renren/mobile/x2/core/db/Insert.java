package com.renren.mobile.x2.core.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Insert extends Sql {

	public Insert(BaseDAO dao) {
		super(dao);
	}

	public long insert(ContentValues values) {
		SQLiteDatabase database = mDao.getDatabase();
		//long number = database.insertOrThrow(mDao.getTableName(), null, values);
		long number = database.replaceOrThrow(mDao.getTableName(), null, values);
		return number;
	}

}

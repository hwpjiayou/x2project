package com.renren.mobile.x2.core.db;

import android.database.sqlite.SQLiteDatabase;

public class Delete extends Sql {

	public Delete(BaseDAO dao) {
		super(dao);
	}

	public long delete(String whereString) {
		SQLiteDatabase database = mDao.getDatabase();
		long number = database.delete(mDao.getTableName(), whereString, null);
		return number;
	}

}

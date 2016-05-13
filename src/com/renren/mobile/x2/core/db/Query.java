package com.renren.mobile.x2.core.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author dingwei.chen
 * @说明 这个类的主要目的是为了转换数据
 * */
public abstract class Query extends Sql {

	public Query(BaseDAO dao) {
		super(dao);
	}

	public Object query(String[] columns, String whereString, String groupBy,
			String orderBy, String limit) {
		/* 测试代码开始 */
		// {
		// String sql = SQLiteQueryBuilder.buildQueryString(
		// false, mDao.getTableName(), columns, whereString, groupBy, null,
		// orderBy, limit);
		// }
		/* 测试代码结束 */
		SQLiteDatabase database = mDao.getDatabase(false);
		Object returnObject = null;
		Cursor c = null;
		try {
			c = database.query(mDao.getTableName(), columns, whereString, null,
					groupBy, null, orderBy, limit);
			if (c != null) {
				returnObject = wrapData(c);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return returnObject;
	}

	@SuppressWarnings("unchecked")
	public <T> T query(String[] columns, String whereString, String groupBy,
			String orderBy, String limit, Class<T> type) {
		/* 测试代码开始 */
		// {
		// String sql = SQLiteQueryBuilder.buildQueryString(
		// false, mDao.getTableName(), columns, whereString, groupBy, null,
		// orderBy, limit);
		// }
		/* 测试代码结束 */
		SQLiteDatabase database = mDao.getDatabase(false);
		Object returnObject = null;
		Cursor c = null;
		try {
			c = database.query(mDao.getTableName(), columns, whereString, null,
					groupBy, null, orderBy, limit);
			if (c != null) {
				returnObject = wrapData(c);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return (T) returnObject;
	}

	/* 承载数据模型转换的重要方法 */
	public abstract Object wrapData(Cursor c);

}

package com.renren.mobile.x2.core.db;

/**
 * @author dingwei.chen
 * */
public interface DAO {

	public interface REST_COMMAND {
		int ADD = 0;
		int DELETE = 1;
		int UPDATE = 2;
		int QUERY = 3;
		int BATCH_ADD = 4;
	}

	public static interface ORDER {
		public String DESC = " desc";// 降序
		public String ASC = " ";// 升序
	}
}

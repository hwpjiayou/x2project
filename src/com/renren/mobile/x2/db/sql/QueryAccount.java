package com.renren.mobile.x2.db.sql;

import java.util.ArrayList;

import com.renren.mobile.x2.components.login.LoginModel;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;

import android.database.Cursor;


public class QueryAccount extends Query{

	public QueryAccount(BaseDAO dao) {
		super(dao);
	}

	/*@Override
	public Object warpData(Cursor c) {
		LoginfoModel model = null;
		if(c.getCount()>0){
			model = new LoginfoModel();
			c.moveToFirst();
			ORMUtil.getInstance().ormQuery(LoginfoModel.class, model, c);
		}
		return model;
	}*/
	
	/**
	 * @author kaining.yang
	 * 表中存储所有登陆过的账户信息
	 */
	@Override
	public Object wrapData(Cursor c) {
		ArrayList<LoginModel> mModleList = new ArrayList<LoginModel>();
		if (c.getCount() > 0) {
			c.moveToFirst();
		}
		while (!c.isAfterLast()) {
			LoginModel model = new LoginModel();
			ORMUtil.getInstance().ormQuery(LoginModel.class, model, c);
			mModleList.add(model);
			c.moveToNext();
		}
		return mModleList;
	}

}
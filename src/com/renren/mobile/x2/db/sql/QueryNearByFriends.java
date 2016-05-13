package com.renren.mobile.x2.db.sql;

import android.database.Cursor;
import android.util.Log;

import com.renren.mobile.x2.components.home.nearbyfriends.NearByFModel;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;

public class QueryNearByFriends extends Query{

	public QueryNearByFriends(BaseDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object wrapData(Cursor c) {
		if (c.getCount() > 0) {
			c.moveToFirst();
			NearByFModel model = new NearByFModel();
			ORMUtil.getInstance().ormQuery(NearByFModel.class, model, c);
			Log.d("jason","query getCount");
			return model;
			
		}		
		return null;
	}

}

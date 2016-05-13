package com.renren.mobile.x2.db.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.renren.mobile.x2.components.home.nearbyfriends.NearByFModel;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;

public class QueryNearByFriendsList extends Query{

	public QueryNearByFriendsList(BaseDAO dao) {
		super(dao);
	}

	@Override
	public Object wrapData(Cursor c) {

		if(c!=null&&c.getCount()>0){
			List<NearByFModel> list = new ArrayList<NearByFModel>();
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				NearByFModel model = new NearByFModel();
				ORMUtil.getInstance().ormQuery(NearByFModel.class, model, c);
				list.add(model);
			}
			Log.d("zxc"," in database "+ list.size());
			return list;
		}
		return null;
	
	}

}

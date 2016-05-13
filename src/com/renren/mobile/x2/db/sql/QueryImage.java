package com.renren.mobile.x2.db.sql;

import android.database.Cursor;
import android.util.Log;

import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.utils.img.ImageModel;


public class QueryImage extends Query{

	public QueryImage(BaseDAO dao) {
		super(dao);
	}
	
	@Override
	public Object wrapData(Cursor cursor) {
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			ImageModel model = new ImageModel();
			ORMUtil.getInstance().ormQuery(ImageModel.class, model, cursor);
			Log.d("jason","query getCount");
			return model;
			
		} 
		return null;
	}

}
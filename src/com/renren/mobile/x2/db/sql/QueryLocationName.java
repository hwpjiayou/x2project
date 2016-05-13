package com.renren.mobile.x2.db.sql;

import android.database.Cursor;
import android.util.Log;
import com.renren.mobile.x2.components.publisher.LocationNameItem;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;

/**
 * QueryLocationName
 * @author xiaoguang.zhang
 * Date: 12-11-13
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
public class QueryLocationName extends Query {

    public QueryLocationName(BaseDAO dao) {
        super(dao);
    }

    @Override
    public Object wrapData(Cursor c) {
        if (c.getCount() > 0) {
            c.moveToFirst();
            LocationNameItem model = new LocationNameItem();
            ORMUtil.getInstance().ormQuery(LocationNameItem.class, model, c);
            Log.d("jason", "query getCount");
            return model;

        }
        return null;
    }
}

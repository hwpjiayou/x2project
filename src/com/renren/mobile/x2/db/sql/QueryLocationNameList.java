package com.renren.mobile.x2.db.sql;

import android.database.Cursor;
import android.util.Log;
import com.renren.mobile.x2.components.publisher.LocationNameItem;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaoguang
 * Date: 12-11-13
 * Time: 上午11:48
 * To change this template use File | Settings | File Templates.
 */
public class QueryLocationNameList extends Query {

    public QueryLocationNameList(BaseDAO dao) {
        super(dao);
    }

    @Override
    public Object wrapData(Cursor c) {
        if(c!=null&&c.getCount()>0){
            List<LocationNameItem> list = new ArrayList<LocationNameItem>();
            for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                LocationNameItem model = new LocationNameItem();
                ORMUtil.getInstance().ormQuery(LocationNameItem.class, model, c);
                list.add(model);
            }
            Log.d("zxc", " in database " + list.size());
            return list;
        }
        return null;
    }
}

package com.renren.mobile.x2.db.dao;

import android.content.ContentValues;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.publisher.LocationNameItem;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.db.sql.QueryLocationName;
import com.renren.mobile.x2.db.sql.QueryLocationNameList;
import com.renren.mobile.x2.db.table.ChatList_Column;
import com.renren.mobile.x2.db.table.LocationNameColumn;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: xiaoguang
 * Date: 12-11-13
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public class LocationNameDao  extends BaseDAO {

    public QueryLocationName mQuery =new QueryLocationName(this);

    private QueryLocationNameList mQueryAll = new QueryLocationNameList(this);

    public LocationNameDao(BaseDBTable table) {
        super(table);
    }

    public synchronized void saveLocationData(LocationNameItem model) {
        ContentValues values = new ContentValues();
        ORMUtil.getInstance().ormInsert(LocationNameItem.class, model, values);
        mInsert.insert(values);
    }

    public LocationNameItem queryById(int id){
        String whereStr = LocationNameColumn._ID + " = '" + id+"'";
        LocationNameItem locationNameModel = mQuery.query(null, whereStr, null, null, null, LocationNameItem.class);
        return locationNameModel ;
    }

    public LocationNameItem queryByName(String name){
        String whereStr = LocationNameColumn.CUSTOM_NAME + " = '" + name+"'";
        LocationNameItem locationNameModel = mQuery.query(null, whereStr, null, null, null, LocationNameItem.class);
        return locationNameModel ;
    }

    public ArrayList<LocationNameItem> queryAllByType(String locationTypename){
        String whereStr = LocationNameColumn.LOCATION_TYPE_NAME + " = '" + locationTypename+"'";
        ArrayList<LocationNameItem> list = (ArrayList<LocationNameItem>) mQueryAll.query(null, whereStr, null, null, null);
        return list;
    }

    /**
     * 数据库中更新一条数据
     * @param id
     * @param values
     * @return
     */
    public int updateById(int id, ContentValues values) {
        String whereStr =  LocationNameColumn._ID + " = " + id;
        int row = this.update2(whereStr, values);
        return row;
    }

    /**
     * 从数据库中删除一条数据
     * @param id
     */
    public void deleteById(int id){
        String whereString = LocationNameColumn._ID + " = " + id;
        this.delete2(whereString);
    }

    /**
     * 查询单人聊天的总数
     * */
    public int queryCountSinglePerson() {
        String[] whereSqls = new String[]{
                ChatList_Column.LOCAL_USER_ID+"="+LoginManager.getInstance().getLoginInfo().mUserId
        };
        return this.getTableRecordCount1(whereSqls);
    }

    /**
     * 删除所有人的聊天记录
     * */
    public void deleteAll() {
        this.delete2(null);
    }
}

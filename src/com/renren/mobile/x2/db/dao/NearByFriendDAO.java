package com.renren.mobile.x2.db.dao;

import android.content.ContentValues;
import com.renren.mobile.x2.components.home.nearbyfriends.NearByFModel;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.db.sql.QueryNearByFriends;
import com.renren.mobile.x2.db.sql.QueryNearByFriendsList;
import com.renren.mobile.x2.db.table.NearByfColumn;

import java.util.ArrayList;

public class NearByFriendDAO extends BaseDAO{
	public QueryNearByFriends mQuery =new QueryNearByFriends(this);
	private QueryNearByFriendsList mQueryAll = new QueryNearByFriendsList(this);
	public NearByFriendDAO(BaseDBTable table) {
		super(table);
	}

	public synchronized void saveNearByfData(NearByFModel model) {
        ContentValues values = new ContentValues();
        ORMUtil.getInstance().ormInsert(NearByFModel.class, model, values);
        mInsert.insert(values);
    }
	public NearByFModel queryByUid(long uid){
		String whereStr = NearByfColumn.USER_ID + " = '" + uid+"'";
		NearByFModel nearByFModel = mQuery.query(null, whereStr, null, null, null, NearByFModel.class);
		return nearByFModel ;
	}
	public ArrayList<NearByFModel> queryAll(){
		ArrayList<NearByFModel> list = (ArrayList<NearByFModel>) mQueryAll.query(null, null, null, null, null);
		return list;
	}
	/***
	 * 清空所有的数据
	 */
	public void clearData(){
		this.delete2(null);
	}
}
/*

		String[] whereSqls = new String[]{
				ChatHistory_Column.LOCAL_USER_ID+"="+localId,
				ChatHistory_Column.GROUP_ID + "=" + groupId
		};
		return this.getTableRecordCount1(whereSqls);
	

*/
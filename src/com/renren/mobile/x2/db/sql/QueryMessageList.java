package com.renren.mobile.x2.db.sql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.renren.mobile.x2.components.home.chatlist.ChatListDataModel;
import com.renren.mobile.x2.components.home.nearbyfriends.NearByFModel;
import com.renren.mobile.x2.components.message.object.BaseMessageModel;
import com.renren.mobile.x2.components.message.object.MessageModelFactory;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.db.table.ChatHistory_Column;
import com.renren.mobile.x2.db.table.MessageColumn;

public class QueryMessageList extends Query{

    public QueryMessageList(BaseDAO dao) {
        super(dao);
    }

    @Override
    public Object wrapData(Cursor c) {
        
        if(c!=null&&c.getCount()>0){
            List<BaseMessageModel> list = new ArrayList<BaseMessageModel>();
            for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                
                int index = c.getColumnIndex(MessageColumn.MESSAGE_TYPE);
                int type = c.getInt(index);
                
                BaseMessageModel model = MessageModelFactory.getInstance().getMessageModel(type);
                ORMUtil.getInstance().ormQuery(BaseMessageModel.class, model, c);
                list.add(model);
            }
            return list;
        }
        return null;
    }

}

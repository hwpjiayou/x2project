package com.renren.mobile.x2.db.sql;

import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import com.renren.mobile.x2.components.home.chatlist.ChatListDataModel;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;

public class Query_ChatSession extends Query{

	public Query_ChatSession(BaseDAO dao) {
		super(dao);
	}

	@Override
	public Object wrapData(Cursor c) {
		// TODO Auto-generated method stub
		List<ChatListDataModel> messages = new LinkedList<ChatListDataModel>();
		while(c.moveToNext()){
			ChatListDataModel model = new ChatListDataModel();
			com.renren.mobile.x2.core.orm.ORMUtil.getInstance().ormQuery(ChatListDataModel.class, model, c);
			messages.add(model);
		}
		return messages;
	}



}

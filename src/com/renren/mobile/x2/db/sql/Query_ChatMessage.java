package com.renren.mobile.x2.db.sql;

import android.database.Cursor;

import com.renren.mobile.x2.components.chat.message.ChatMessageFactory;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;
import com.renren.mobile.x2.core.orm.ORMUtil;



public class Query_ChatMessage extends Query{

	public Query_ChatMessage(BaseDAO dao) {
		super(dao);
	}

	@Override
	public Object wrapData(Cursor c) {
		if(c.getCount()>0){
			c.moveToFirst();
			ChatMessageWarpper message = ChatMessageFactory.getInstance().createChatMessage(c);
			ORMUtil.getInstance().ormQuery(message.getClass(), message, c);
			return message;
		}
		return null;
	}

}

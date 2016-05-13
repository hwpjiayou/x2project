package com.renren.mobile.x2.db.sql;

import java.util.List;

import android.database.Cursor;

import com.renren.mobile.x2.components.chat.message.ChatMessageFactory;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.Query;



public class Query_ChatHistory extends Query{

	public Query_ChatHistory(BaseDAO dao) {
		super(dao);
	}

	@Override
	public Object wrapData(Cursor c) {
		List<ChatMessageWarpper> list 
			= ChatMessageFactory.getInstance().buildChatMessages(c);
		return list;
	}

}

package com.renren.mobile.x2.db.dao;

import java.util.List;

import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;

import android.content.ContentValues;


/**
 * @author dingwei.chen
 * */
public interface ChatHistoryDAOObserver{
	public void onInsert(ChatMessageWarpper message);
	public void onInsert(List<ChatMessageWarpper> message);
	public void onDelete(String columnName,long _id);
	public void onUpdate(String column,long _id,ContentValues values);
}

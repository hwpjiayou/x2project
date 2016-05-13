package com.renren.mobile.x2.db.dao;

import com.renren.mobile.x2.components.message.object.BaseMessageModel;

/**
 * author yuchao.zhang
 *
 * 消息推送的观察者
 */

public interface MessageObserver{

        public void onInsert(BaseMessageModel message);
        public void onDeleteAll();
//        public void onUpdate();
    }

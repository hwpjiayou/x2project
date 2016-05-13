package com.renren.mobile.x2.components.chat.holder;

import com.renren.mobile.x2.utils.ViewMapUtil;

import android.view.View;


/**
 * @author dingwei.chen
 * 发送消息控件初始化
 * */
public class InitViewHolder_To extends BaseInitHolder {
	public InitViewHolder_To(View view) {
		ViewMapUtil.viewMapping(this, view);
	}

	@Override
	public void initViewBySubClass(ChatItemHolder holder, int messageType) {}
}

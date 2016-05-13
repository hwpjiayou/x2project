package com.renren.mobile.x2.actions.base;


import com.renren.mobile.x2.core.xmpp.node.Message;

import java.util.List;

/**
 * @author dingwei.chen1988@gmail.com
 * @说明 错误回调业务
 * */
public class ActionError extends Action<Message>{

	public ActionError() {
		super(Message.class);
	}

	@Override
	public void processAction(Message node) {
		int code =  0;
		try {
			code = Integer.parseInt(node.mErrorNode.mCode);
		} catch (Exception e) {}
		this.onErrorCallback(Integer.parseInt(node.mId),code,node.mErrorNode.mMsg);
	}

    @Override
    public void batchProcessAction(List<Message> nodeList) {
    }

    @Override
	public boolean checkActionType(Message node) throws Exception {
		return node.mType!=null && (node.mType.equals("error")||node.mErrorNode!=null);
	}

}

package com.renren.mobile.x2.actions.message;

import java.util.List;

import com.renren.mobile.x2.actions.base.Action;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.utils.CommonUtil;

/**
 * @author xiaobo.yuan
 */
public class MessageDemo extends Action<Message>{
	
    public MessageDemo() {
		super(Message.class);
	}

	@Override
    public void processAction(Message node) {
    }

    @Override
    public void batchProcessAction(List<Message> nodeList) {

    }

    @Override
	public boolean checkActionType(Message node) throws Exception {
		return node.mBody != null;
	}

}

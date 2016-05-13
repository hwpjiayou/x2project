package com.renren.mobile.x2.actions.base;

import java.util.List;

import android.text.TextUtils;

import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

/**
 * @author dingwei.chen1988@gmail.com
 * @说明 成功回调:不在意回执
 * */
public class ActionSendSuccess extends Action<XMPPNode>{

	public ActionSendSuccess() {
		super(XMPPNode.class);
	}

	@Override
	public void processAction(XMPPNode node) {
		this.onSuccessCallback(Long.parseLong(node.getId()));
	}

    @Override
    public void batchProcessAction(List<XMPPNode> nodeList) {
    	for(XMPPNode node : nodeList){
    		processAction(node);
    	}
    }

    @Override
	public boolean checkActionType(XMPPNode node) throws Exception {
    	return !TextUtils.isEmpty(node.getId());
	}

}

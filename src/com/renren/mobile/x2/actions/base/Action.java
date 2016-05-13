package com.renren.mobile.x2.actions.base;


import com.renren.mobile.x2.core.xmpp.action.Actions;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.network.talk.NetRequestListener;
import com.renren.mobile.x2.network.talk.NetRequestsPool;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dingwei.chen1988@gmail.com
 * @说明 业务处理模型
 * */
public abstract class Action<T extends XMPPNode> {

	protected boolean mIsSelfAction = true;
	public Actions mAction = null;
	protected NetRequestListener mCallbackRequest = null;
	protected Class<T> mClazz;
	public Action(Class<T> clazz){
		mClazz = clazz;
	}

	public final Class<T> getNodeClass(){
		return mClazz;
	}
	
    public List<T> nodeList = new LinkedList<T>();
	
    
    @SuppressWarnings("unchecked")
	public final void addToList(XMPPNode node){
    	nodeList.add((T) node);
    }
    
    public synchronized void batchRun(){
    	batchProcessAction(nodeList);
    	nodeList.clear();
    }
    
	public abstract void processAction(T node);

    public abstract void batchProcessAction(List<T> nodeList);
	
	public abstract boolean checkActionType(T node)  throws Exception;
	
	public void setActions(Actions action){
		this.mAction = action;
	}
	public void setCallback(NetRequestListener request){
		this.mCallbackRequest = request;
	}
	public final void onSuccessCallback(long key,Object object){
//		NetRequestsPool.getInstance().callDataCallbackNotSyn(key, object);
	}
	public final void onErrorCallback(long id,int errorCode,String errorMsg){
		NetRequestsPool.getInstance().onError(id, errorMsg);
	}
	public final void onSuccessCallback(long id){
		NetRequestsPool.getInstance().onSuccess(id);
	}
}

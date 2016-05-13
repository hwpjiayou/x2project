package com.renren.mobile.x2.network.mas;

import org.json.JSONObject;

import com.renren.mobile.x2.network.mas.UGCManager;
import com.renren.mobile.x2.utils.log.Logger;

/**
 * @author 宁长胜
 * FeedManager对应的的Response
 */
public class UGCResponse implements INetResponse {
	
	private INetResponse m_response;
	
	/**
	 * @param response
	 */
	public UGCResponse(INetResponse response) {
		this.m_response = response;
	}
	
	public INetResponse getInternalResponse() {
		return m_response;
	}
	
	@Override
	public void response(INetRequest req, JSONObject obj) {
		UGCManager.getInstance().onResponse(req, obj);
		Logger logger = new Logger("NCS");
		logger.d("FeedResponse:" + req.getTypeInQueue()+"/obj:"+obj.toString());
		if (m_response!=null && req.getTypeInQueue() == INetRequest.TYPE_UGC_POST_UGC) { //仅第一次发送返回response，之后再不返回response
			m_response.response(req, obj);
			m_response = null;
		}
		
	}

}

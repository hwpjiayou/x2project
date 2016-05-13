package com.renren.mobile.x2.network.mas;

import org.json.JSONObject;

import com.renren.mobile.x2.utils.log.Logger;

/**
 * @author 宁长胜
 * RequestQueueManager对应的Response
 */
public class QueueResponse implements INetResponse {
	
	@Override
	public void response(INetRequest req, JSONObject obj) {
		RequestQueueManager.getInstance().onResponse(req, obj);
		Logger logger = new Logger("NCS");
		logger.d("QueueResponse:" + req.getTypeInQueue()+"/obj:"+obj.toString());
		
	}

}

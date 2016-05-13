package com.renren.mobile.x2.network.mas;

public interface INetProvider {

	/**
	 * 添加一个请求.
	 * @param req
	 */
	void addRequest(INetRequest req);

	/**
	 * 增加一个请求，priority分高低两种，为INetRequest.PRIORITY_HIGH_PRIORITY: INetRequest.PRIORITY_LOW_PRIORITY:
	 */
	void addRequest(INetRequest req, int priority);
	
	/**
	 * 取消队列中的请求.
	 */
	void cancel();
	
	/**
	 * 停止网络线程.
	 */
	void stop();
	
}

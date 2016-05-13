package com.renren.mobile.x2.network.mas;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.utils.FileUtil;
import com.renren.mobile.x2.utils.log.Logger;

import android.os.Environment;

/**
 * @author 宁长胜 离线消息发送管理类
 */
public class RequestQueueManager {

	private static RequestQueueManager instance = new RequestQueueManager();

	private RequestQueueManager() {

	}

	public static RequestQueueManager getInstance() {
		return instance;
	}
	
	private static final int QUEUE_TYPE_REQUEST = 1;
	private static final int QUEUE_TYPE_SENCOND = 2;
	private static final String SDCARD_FILE_PATH = Environment.getExternalStorageDirectory() 
			+ File.separator + RenrenChatApplication.getApplication().getPackageName()
			+ File.separator + "outlinecache" + File.separator;
	private static final String OUTLINE_FILE_NAME = "outlineQueue";

	/**
	 * @author 宁长胜 内部类，存放离线Request及其发送状态
	 */
	private class RequestQueueItem {

		public static final int STATUS_NOT_READY = -1;
		public static final int STATUS_REAY = 1;
		public static final int STATUS_SENDING = 2;

		public INetRequest request;
		public int status;

		public RequestQueueItem(INetRequest request, int status) {
			this.request = request;
			this.status = status;
		}

	}

	/**
	 * 一级发送队列，存放不依赖其他Request的Request集合
	 */
	private Queue<RequestQueueItem> mRequestQueue = new ConcurrentLinkedQueue<RequestQueueItem>();
	/**
	 * 二级发送队列，存放被阻塞的Request
	 */
	private Queue<RequestQueueItem> mSecondQueue = new ConcurrentLinkedQueue<RequestQueueItem>();

	private Logger logger = new Logger("NCS");
	
	private outlineQueueHandleThread mHandleThread = new outlineQueueHandleThread();
	
	private static final int ACTION_ADD = 1;
	private static final int ACTION_QUERY = 2;
	private static final int ACTION_SEND = 3;
	private static final int ACTION_RESPONSE = 4;
	private static final int ACTION_RECOVER = 0;
	
	private class outlineQueueHandleThread extends Thread {
		private int action = -1;
		private INetRequest request;
		private boolean runable = false;
		private List<Long> idList;
		private JSONObject object;
		
		public outlineQueueHandleThread() {
		}

		public void setProperties(int action, INetRequest request,
				JSONObject object, List<Long> idList, boolean runable) {
			this.action = action;
			this.request = request;
			this.object = object;
			this.idList = idList;
			this.runable = runable;
		}
		
		public void run() {
			if (runable) {
				this.runable = false;
				switch (this.action) {
					case ACTION_ADD:
						addOneOfflineRequest(this.request);
						saveQueuesToFile();
						break;
					case ACTION_QUERY:
						getUGCIds(idList);
						break;
					case ACTION_SEND:
						sendAllReadyOfflineRequests();
						break;
					case ACTION_RESPONSE:
						response(request, object);
						saveQueuesToFile();
						break;
					case ACTION_RECOVER:
						readQueueFromFile();
						break;
					default:
						break;
				}
			}
		}
		
	}
	
	private synchronized void addThreadTask(int action, INetRequest request,JSONObject object, List<Long> idList) {
		while (mHandleThread.isAlive()) {
			try {
				this.wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (!mHandleThread.isAlive()) {
			mHandleThread.setProperties(action, request, object, idList, true);
			mHandleThread.run();
		}
	}
	
	public synchronized void addOneRequest(INetRequest request) {

		this.addThreadTask(ACTION_ADD, request, null, null);
		
	}
	
	/**
	 * 添加离线Request并根据依赖关系将其添加到一级或二级队列，同时置换Response为QueueResponse 1：不依赖其他Request
	 * 1.1：不依赖其他Request且依赖它的Request已全部进入二级队列或没有依赖它的Request，则进入一级队列，
	 * 且状态置为STATUS_REAY，准备发送
	 * 1.2：不依赖其他Request但依赖它的Request并没有全部进入二级队列，则进入一级队列，且状态置为STATUS_NOT_READY
	 * 2：依赖其他Request，则进入二级队列并将一级队列中等待其进入二级队列的Request状态置为STATUS_REAY，准备发送
	 * 
	 * @param request
	 */
	private synchronized void addOneOfflineRequest(INetRequest request) {
		
		logger.d("添加了一条离线Request:" + request.getTypeInQueue());

		// 判断依赖我的Feed是否都已进入阻塞队列，根据结果设置Feed_Status
		List<Long> dependMeINetRequests = request.getDependMeRequestsIdList();
		int waitDependMeRequestCount = dependMeINetRequests.size();
		if (dependMeINetRequests.size() > 0) {
			int req_status = RequestQueueItem.STATUS_NOT_READY;
			for (RequestQueueItem blockedINetRequestItem : mSecondQueue) {
				for (long dependMeId : request.getDependMeRequestsIdList()) {
					if (dependMeId == blockedINetRequestItem.request.getCreatTime()) {
						waitDependMeRequestCount--;
					}
				}

				if (waitDependMeRequestCount <= 0) {
					req_status = RequestQueueItem.STATUS_REAY;
					break;
				}
			}
			this.resetResponse(request);
			mRequestQueue.offer(new RequestQueueItem(request, req_status)); // 仅考虑单向依赖，未考虑既依赖其他Feed又被其他Feed依赖情况
			return;
		}

		// 判断我依赖的是否因为等我进入阻塞队列而状态为STATUS_WAIT_DEPAND_ME，根据结果判断是否更改状态
		List<Long> dependOtherINetRequestIds = request.getDependOtherRequestsIdList();
		if (dependOtherINetRequestIds.size() > 0) {
			for (Long dependOtherId : dependOtherINetRequestIds) {
				for (RequestQueueItem requestItem : mRequestQueue) {
					if (requestItem.status == RequestQueueItem.STATUS_NOT_READY && dependOtherId == requestItem.request.getCreatTime()) {
						int dependMeRequestCount = requestItem.request.getDependMeRequestsIdList().size();
						dependMeRequestCount--;
						for (Long dependMeRequestId : requestItem.request.getDependMeRequestsIdList()) {
							for (RequestQueueItem blockedINetRequest : mSecondQueue) {
								if (dependMeRequestId == blockedINetRequest.request.getCreatTime()) {
									dependMeRequestCount--;
								}
							}
						}
						if (dependMeRequestCount <= 0) {
							requestItem.status = RequestQueueItem.STATUS_REAY;
							break;
						}
					}
				}
			}
			this.resetResponse(request);
			mSecondQueue.offer(new RequestQueueItem(request, RequestQueueItem.STATUS_NOT_READY));
		} else {
			this.resetResponse(request);
			mRequestQueue.offer(new RequestQueueItem(request, RequestQueueItem.STATUS_REAY));
		}

	}

	/**
	 * 置换Request的Response为QueueResponse
	 * 
	 * @param request 要置换的Request
	 */
	private void resetResponse(INetRequest request) {
		request.setResponse(new QueueResponse());
	}
	
	/**
	 * 从一级队列中移除指定的Request
	 * 
	 * @param req
	 */
	private void removeOneOfflinRequest(INetRequest req) {
		for (RequestQueueItem requestItem : mRequestQueue) {
			if (requestItem.request.getCreatTime() == req.getCreatTime()) {
				mRequestQueue.remove(requestItem);
			}
		}
	}
	
	/**
	 * 从二级队列中移除依赖req的Request，以及该Request的其他依赖对象（移除除req外的整个UGC包含的请求）
	 * 
	 * @param req
	 */
	private void removeDependMeRequests(INetRequest req) {
		for (Long dependMeId : req.getDependMeRequestsIdList()) {
			for (RequestQueueItem requestItem : mSecondQueue) {
				if (requestItem.request.getCreatTime() == dependMeId) {
					this.removeDependOtherRequests(requestItem.request);
					mSecondQueue.remove(requestItem);
				}
			}
		}
	}
	
	/**
	 * 从一级队列中移除req所依赖的Requests
	 * 
	 * @param req
	 */
	private void removeDependOtherRequests(INetRequest req) {
		for (long dependOtherId : req.getDependOtherRequestsIdList()) {
			for (RequestQueueItem requestItem : mRequestQueue) {
				if (requestItem.request.getCreatTime() == dependOtherId) {
					mRequestQueue.remove(requestItem);
				}
			}
		}
	}
	
	/**
	 * 移除req以及所有与req有关系的Request（移除整个UGC中的Request）
	 * 
	 * @param req
	 */
	private void removeOneOfflineRequestAndItsDepends(INetRequest req) {
		this.removeDependMeRequests(req);
		this.removeOneOfflinRequest(req);
	}

//	/**
//	 * 添加依赖关系
//	 * 
//	 * @param dependOtherRequest 依赖dependMeRequest的Request
//	 * @param dependMeRequest 被dependOtherRequest所依赖的Request
//	 */
//	private void setDepend(INetRequest dependOtherRequest, INetRequest dependMeRequest) {
//		dependOtherRequest.getDependOtherRequestsIdList().add(dependMeRequest.getCreatTime());
//		dependMeRequest.getDependMeRequestsIdList().add(dependOtherRequest.getCreatTime());
//	}

	/**
	 * 移除依赖关系，更新Request内容并根据情况将被阻塞的Request移入一级队列准备发送
	 * 1：对应req的被阻塞Request有其他依赖Request，根据obj更新Request内容并从dependOtherList中移除req的Id后继续阻塞
	 * 2：对应req的被阻塞Request没有其他依赖Request，根据obj更新Request内容并移入一级队列，状态置为STATUS_REAY，
	 * 调用发送离线消息方法发送一级队列中状态为STATUS_REAY的消息
	 * 
	 * @param req 发送成功的Request
	 * @param obj req对应返回的JSONObject
	 */
	private void removeDepend(INetRequest req, JSONObject obj) {
		if (!req.getDependMeRequestsIdList().isEmpty()) {
			for (Long denpendMeId : req.getDependMeRequestsIdList()) {
				for (RequestQueueItem blockINetRequestItem : mSecondQueue) {
					if (denpendMeId == blockINetRequestItem.request.getCreatTime()) {
						blockINetRequestItem.request.getDependOtherRequestsIdList().remove(req.getCreatTime());
						req.getDependMeRequestsIdList().remove(blockINetRequestItem.request.getCreatTime());
						this.updateUgcRequest(req, obj, blockINetRequestItem);
						if (blockINetRequestItem.request.getDependOtherRequestsIdList().isEmpty()) {
							mSecondQueue.remove(blockINetRequestItem);
							mRequestQueue.offer(blockINetRequestItem);
							blockINetRequestItem.status = RequestQueueItem.STATUS_REAY;
							logger.d(blockINetRequestItem.request.getUgcModel().getContentInfoJSONValue().toString());
							sendAllReadyOfflineRequests();
						}
					}
				}
			}
		}

	}
	
	public synchronized void getOutLineUGCIds(List<Long> idList) {
		
		this.addThreadTask(ACTION_QUERY, null, null, idList);
		
	}
	
	/**
	 * 获取所有离线队列中的UGC的Id（包含发送队列以及二级队列）
	 * 
	 * @return 离线队列中所有UGC的Id的List
	 */
	private void getUGCIds(List<Long> idList) {
		for (RequestQueueItem requestQueueItem : mRequestQueue) {
			if (requestQueueItem.request.getTypeInQueue() == INetRequest.TYPE_UGC_POST_UGC) {
				idList.add(requestQueueItem.request.getCreatTime());
			}
		}
		for (RequestQueueItem blockedQueueItem : mSecondQueue) {
			if (blockedQueueItem.request.getTypeInQueue() == INetRequest.TYPE_UGC_POST_UGC) {
				idList.add(blockedQueueItem.request.getCreatTime());
			}
		}
		
	}

	// 此处根据网络发送成功之后的返回结果在依赖它的Request中添加所需内容，此处与feed耦合较大，如需改动需添加结构（依赖与被依赖类型，需添加的字段名称）
	/**
	 * 根据网络发送成功后返回的JSONObject更新对应被阻塞的Request内容
	 * 
	 * @param successRequest 成功完成网络调用的Request
	 * @param successJsonObject 成功完成网络调用的Request对应的返回JSONObject
	 * @param blockedItem 与successRequest对应的被阻塞RequestQueueItem
	 */
	private void updateUgcRequest(INetRequest successRequest, JSONObject successJsonObject, RequestQueueItem blockedItem) { // 根据发送成功的Response内容完善依赖其的Request请求的相关内容
		switch (successRequest.getTypeInQueue()) {
		case INetRequest.TYPE_UGC_POST_IMG:
			blockedItem.request.getUgcModel().updateImageContentTest(successJsonObject);
			try {
				JSONObject bundle = blockedItem.request.getData();
				bundle.put("content", blockedItem.request.getUgcModel().getContentInfoJSONValue());
				bundle.put("client_time", System.currentTimeMillis());
				if (bundle.has("sig")) {
					bundle.remove("sig");
				}
				bundle.put("sig", AbstractHttpMcsService.getSigByJSON(bundle, false));
				blockedItem.request.setData(bundle);
				
			} catch (JSONException e) {
				logger.e(e.getMessage());
				e.printStackTrace();
			}
			break;
		case INetRequest.TYPE_UGC_POST_VOICE:
			try {
				if (successRequest.getData().has("length")) {
					successJsonObject.put("length", successRequest.getData().get("length"));
				}
				blockedItem.request.getUgcModel().updateVoiceContentTest(successJsonObject);
				JSONObject bundle = blockedItem.request.getData();
				bundle.put("content", blockedItem.request.getUgcModel().getContentInfoJSONValue());
				bundle.put("client_time", System.currentTimeMillis());
				if (bundle.has("sig")) {
					bundle.remove("sig");
				}
				bundle.put("sig", AbstractHttpMcsService.getSigByJSON(bundle, false));
				blockedItem.request.setData(bundle);
				
			} catch (JSONException e) {
				logger.e(e.getMessage());
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	public synchronized void sendOutlineUGCs() {
		
		this.addThreadTask(ACTION_SEND, null, null, null);
		
	}
	
	/**
	 * 发送一级队列中所有状态为STATUS_REAY的Request
	 * 1，Request创建超过24小时仍未发送成功：删除该Request以及其他与之相关的Request
	 * 2，发送间隔不足30分钟，等待
	 * 3，发送间隔超过30分钟，不足24小时
	 * 3.1，状态为Ready：发送
	 * 3.2，状态不为Ready，等待
	 */
	//TODO 是不是应该同步一下？
	private void sendAllReadyOfflineRequests() {
		for (RequestQueueItem requestItem : mRequestQueue) {
			long periodSend = System.currentTimeMillis() - requestItem.request.getLastFailTime();
			long priodGiveUp = System.currentTimeMillis() - requestItem.request.getCreatTime();
			logger.d("OffLine Send Period:"+periodSend+"/"+INetRequest.PERIOD_OFFLINE_REQUEST_SEND);
			logger.d("OffLine GiveUp Period:"+priodGiveUp+"/"+INetRequest.PERIOD_OFFLINE_REQUEST_GIVEUP);
			if (priodGiveUp >= INetRequest.PERIOD_OFFLINE_REQUEST_GIVEUP) { //删除超过24小时的离线Request以及与其相关的Request
				//mark：此处添加超过丢弃时间删除UGC时的回调操作
				this.removeOneOfflineRequestAndItsDepends(requestItem.request);
			} else if (periodSend <= INetRequest.PERIOD_OFFLINE_REQUEST_SEND) { //发送间隔为30分钟
				continue;
			} else {
				if (requestItem.status == RequestQueueItem.STATUS_REAY) {
					requestItem.status = RequestQueueItem.STATUS_SENDING;
					logger.d("发送了一条离线消息:" + requestItem.request.getTypeInQueue());
					HttpProviderWrapper.getInstance().addRequest(requestItem.request);
				} else {
					continue;
				}
			}
			
		}
	}

	/**
	 * 获取二级队列中被阻塞Request的数目（仅测试逻辑时使用）
	 * 
	 * @return 二级队列中被阻塞Request的数目
	 */
	public int getSecondQueueSize() {
		if (mSecondQueue != null) {
			return mSecondQueue.size();
		}
		return -1;
	}

	/**
	 * 获取一级队列中被阻塞Request的数目（仅测试逻辑时使用）
	 * 
	 * @return 一级队列中被阻塞Request的数目
	 */
	public int getRequestQueueSize() {
		if (mRequestQueue != null) {
			return mRequestQueue.size();
		}
		return -1;
	}

	private void response(INetRequest req, JSONObject obj) {

		if (obj.has("error_code")) {
			req.setFailCount(req.getFailCount()+1);
			req.setLastFailTime(System.currentTimeMillis());
			try {
				int errorCode = obj.getInt("error_code");
				if (errorCode == -97 || errorCode == -99) { // 离线队列发送因为无网络连接而失败
					for (RequestQueueItem requestItem : mRequestQueue) {
						if (requestItem.request.getCreatTime() == req.getCreatTime()) {
							requestItem.status = RequestQueueItem.STATUS_REAY;
						}
					}
				} else { // 离线队列发送因为其他原因而失败
					this.removeOneOfflineRequestAndItsDepends(req);
					logger.d("离线队列发送因其他原因失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
//			if (req.getTypeInQueue() == INetRequest.TYPE_UGC_POST_UGC) {
//				//mark：此处添加UGC发送成功时的回调
//			}
			this.removeOneOfflinRequest(req);
			this.removeDepend(req, obj);
		}
	}
	
	/**
	 * 网络调用返回时调用的方法
	 * 
	 * @param req 完成调用的Request
	 * @param obj req对应的JSONOb返回
	 */
	//TODO  是不是应该同步一下？
	public synchronized void onResponse(INetRequest req, JSONObject obj) {
		
		this.addThreadTask(ACTION_RESPONSE, req, obj, null);
		
	}

    private void putCommonData(INetRequest request, JSONObject data) throws JSONException, UnsupportedEncodingException {
		data.put("url", request.getUrl());
		data.put("data", request.getData());
		data.put("creat_time", request.getCreatTime());
		data.put("current_session", request.getCurrentSession());
		data.put("first_send_status", request.getFirstSendStatus());
		data.put("priority", request.getPriority());
		JSONObject modeljson = request.getUgcModel().getContentInfoJSONValue();
		data.put("ugc_content_model", modeljson);
		data.put("fail_count", request.getFailCount());
		data.put("last_fail_time", request.getLastFailTime());
		JSONArray dependMeArray = new JSONArray(request.getDependMeRequestsIdList());
		data.put("depend_me_list", dependMeArray);
		JSONArray dependOtherArray = new JSONArray(request.getDependOtherRequestsIdList());
		data.put("depend_other_list", dependOtherArray);
		data.put("type", request.getType());
		data.put("type_in_queue", request.getTypeInQueue());
		if (request.getData().has("data")) {
			byte[] bytes = (byte[]) request.getData().get("data");
			String bytes_name = request.getData().get("data").toString();
			logger.d("bytes name:"+bytes_name);
			if (!FileUtil.getInstance().isExistFile(SDCARD_FILE_PATH + bytes_name)) {
				FileUtil.getInstance().saveFile(bytes, SDCARD_FILE_PATH + bytes_name);
			}
			logger.d("bytes length in:"+bytes.length);
		}
    }
    
    private String getQueueString() {
		JSONArray array = new JSONArray();
		
		for (RequestQueueItem requestQueueItem : mRequestQueue) {
			JSONObject data = new JSONObject();
			try {
				data.put("queue_type", QUEUE_TYPE_REQUEST);
				data.put("status", requestQueueItem.status);
				INetRequest request = requestQueueItem.request;
				this.putCommonData(request, data);
			} catch (Exception e) {
				logger.d("异常："+e.getMessage());
				e.printStackTrace();
			}
			array.put(data);
		}
		
		for (RequestQueueItem requestQueueItem : mSecondQueue) {
			JSONObject data = new JSONObject();
			try {
				data.put("queue_type", QUEUE_TYPE_SENCOND);
				data.put("status", requestQueueItem.status);
				INetRequest request = requestQueueItem.request;
				this.putCommonData(request, data);
			} catch (Exception e) {
				logger.d("异常："+e.getMessage());
				e.printStackTrace();
			}
			array.put(data);
		}
		return array.toString();
	}
    
	private void saveQueuesToFile() {
		if (!FileUtil.getInstance().isExistFile(SDCARD_FILE_PATH)) {
			FileUtil.getInstance().createFile(SDCARD_FILE_PATH);
		}
		File file = new File(SDCARD_FILE_PATH);
		if (FileUtil.getInstance().getFIleSize(file) > 2000000) {
			FileUtil.getInstance().delAllFile(SDCARD_FILE_PATH);
		}
		String content = this.getQueueString();
		FileUtil.getInstance().writeStringtoSD(content, SDCARD_FILE_PATH+OUTLINE_FILE_NAME);
		logger.d("saveQueuesToFile:"+content);
	}
	
	private void resetRequestsIntoQueue(JSONArray array) throws Exception {
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = (JSONObject) array.get(i);
			logger.d(i+"---"+object.toString());
			int queue_type = object.getInt("queue_type");
			String url = object.getString("url");
			logger.d("url:"+url);
			int status = object.getInt("status");
			logger.d("status:"+status);
			long creat_time = object.getLong("creat_time");
			long current_session = object.getLong("current_session");
			int first_send_status = object.getInt("first_send_status");
			int priority = object.getInt("priority");
			UGCContentModel ugc_content_model = null;
			if (object.has("ugc_content_model")) {
				JSONObject modeljson = object.getJSONObject("ugc_content_model");
				ugc_content_model = new UGCContentModel(modeljson);
			}
			int fail_count = object.getInt("fail_count");
			long last_fail_time = object.getLong("last_fail_time");
			JSONArray depend_me_list = object.getJSONArray("depend_me_list");
			JSONArray depend_other_list = object.getJSONArray("depend_other_list");
			int type = object.getInt("type");
			logger.d("type:"+type);
			int type_in_queue = object.getInt("type_in_queue");
			logger.d("type_in_queue:"+type_in_queue);
			JSONObject data = new JSONObject(object.getString("data"));
			if (data.has("data")) {
				String bytes_name = data.getString("data");
				File file = new File(SDCARD_FILE_PATH+bytes_name);
				byte[] bytes = FileUtil.getInstance().readBytes(file);
//				FileUtil.getInstance().deleteFile(SDCARD_FILE_PATH+bytes_name);
				logger.d("bytes length out:"+bytes.length);
				data.put("data", bytes);
			}
			logger.d("data:"+data.toString());
			INetRequest request = new HttpRequestWrapper();
			request.setCreatTime(creat_time);
			request.setCurrentSession(current_session);
			request.setFirstSendStatus(first_send_status);
			request.setPriority(priority);
			request.setUgcModel(ugc_content_model);
			request.setFailCount(fail_count);
			request.setLastFailTime(last_fail_time);
			for (int j = 0; j < depend_me_list.length(); j++) {
				request.getDependMeRequestsIdList().add(depend_me_list.getLong(j));
			}
			for (int j = 0; j < depend_other_list.length(); j++) {
				request.getDependOtherRequestsIdList().add(depend_other_list.getLong(j));
			}
			request.setUrl(url);
			request.setData(data);
			request.setType(type);
			request.setTypeInQueue(type_in_queue);
			request.setSecretKey(LoginManager.getInstance().getSecretKey());
			this.resetResponse(request);
			RequestQueueItem requestQueueItem = new RequestQueueItem(request, status);
			if (queue_type == QUEUE_TYPE_REQUEST) {
				if (!this.isQueueContainsItem(mRequestQueue, requestQueueItem)) {
					mRequestQueue.add(requestQueueItem);
				}
			} else if (queue_type == QUEUE_TYPE_SENCOND) {
				if (!this.isQueueContainsItem(mSecondQueue, requestQueueItem)) {
					mSecondQueue.add(requestQueueItem);
				}
			}
		}
	}
	
	private boolean isQueueContainsItem(Queue<RequestQueueItem> queue, RequestQueueItem item) {
		for (RequestQueueItem requestQueueItem : queue) {
			if (requestQueueItem.request.getCreatTime() == item.request.getCreatTime()) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized void recoverQueue() {
		
		this.addThreadTask(ACTION_RECOVER, null, null, null);
		
	}
	
	private void readQueueFromFile() {
		try {
			if (FileUtil.getInstance().isExistFile(SDCARD_FILE_PATH+OUTLINE_FILE_NAME)) {
				File file = new File(SDCARD_FILE_PATH+OUTLINE_FILE_NAME);
				String data = new String(FileUtil.getInstance().readBytes(file));
				JSONArray array = new JSONArray(data);
				logger.d("从文件中读取"+array.toString());
				this.resetRequestsIntoQueue(array);
				this.sendAllReadyOfflineRequests();
			} else {
				return;
			}
		} catch (Exception e) {
			logger.e("异常："+e.getMessage());
			e.printStackTrace();
		}
	}
}

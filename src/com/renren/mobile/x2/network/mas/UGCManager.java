package com.renren.mobile.x2.network.mas;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.utils.log.Logger;

/**
 * @author 宁长胜
 * ugc的网络发送管理类，用于管理发送ugc的相关逻辑
 *
 */
public class UGCManager {
	
	private static UGCManager instance = new UGCManager();
	private List<RequestListItem> mBlockedINetRequestList = new ArrayList<RequestListItem>();
	private Logger logger = new Logger("NCS");
	private UGCManager() {
		
	}
	
	public static UGCManager getInstance() {
		return instance;
	}
	
	/**
	 * @author 宁长胜
	 *	ugcManager的内部类，用于记录Request及其对应的发送状态
	 */
	private class RequestListItem {
		
		public static final int STATUS_NOT_READY = -1;
		public static final int STATUS_REAY = 1;
		public static final int STATUS_SENDING = 2;
		
		public INetRequest request;
		public int status;
		
		public RequestListItem(INetRequest req, int state) {
			this.request = req;
			this.status = state;
		}
		
	}
	
	/**
	 * 用于获取发送文本类型、TAG类型以及Place类型的混合Request，因他们均和UGC一同发送，故而实际返回的是发送UGC的请求
	 * @param text 要发送的文本，如果没有要发送的文本可传null
	 * @param tagModel 要发送的TAG数据结构，通过UGCTagModel(String name, String id, String icon, String desc)构建
	 * @param placeModel 要发送的Place数据结构，通过UGCPlaceModel(String name, String latitude, String longitude)构建
	 * @return 发送UGC的Request
	 */
	public INetRequest getUGCUploadRequest(String text, UGCTagModel tagModel, UGCPlaceModel placeModel) {
		INetRequest ugcRequest = new HttpRequestWrapper();
		ugcRequest.setUgcModel(new UGCContentModel());
		if (text != null) {
			ugcRequest.getUgcModel().updateTextContent(text);
		}
		if (tagModel != null) {
			ugcRequest.getUgcModel().setTagModel(tagModel);
		}
		if (placeModel != null) {
			ugcRequest.getUgcModel().setPlaceModel(placeModel);
		}
		ugcRequest.setTypeInQueue(INetRequest.TYPE_UGC_POST_UGC);
		return ugcRequest;
	}
	

	/**
	 * 用于获取发送文本类型的Request，因为文本同ugc一同发送，故而实际返回的是发送UGC的请求
	 * @param textContent 要发送的文本，如果没有要发送的文本可传null
	 * @return 发送Text所需的Request
	 */
	public INetRequest getTextUploadRequest(String textContent) {
		return this.getUGCUploadRequest(textContent, null, null);
	}
	
	/**
	 * 获取上传图片的请求Request
	 * @param data 要上传的图片数据
	 * @return 上传图片的Request
	 */
	public INetRequest getImageUploadRequest(byte[] data) {
		INetRequest imageRequest = new HttpRequestWrapper();
		imageRequest = HttpMasService.getInstance().getImageUploadRequest(imageRequest, data);
		imageRequest.setTypeInQueue(INetRequest.TYPE_UGC_POST_IMG);
		return imageRequest;
	}
	
	/**
	 * 获取上传语音的请求Request
	 * @param toId 
	 * @param vid
	 * @param seqid
	 * @param mode
	 * @param playTime
	 * @param voiceData
	 * @return 发送语音的Request
	 */
	public INetRequest getVoiceUploadRequest(long toId, String vid, int seqid, String mode, int playTime, byte[] voiceData) {
		INetRequest voiceRequest = new HttpRequestWrapper();
		logger.d("voice length:"+playTime);
		voiceRequest = HttpMasService.getInstance().getVoiceUploadRequest(voiceRequest, toId, vid, seqid, mode, playTime, voiceData);
		voiceRequest.setTypeInQueue(INetRequest.TYPE_UGC_POST_VOICE);
		return voiceRequest;
	}
	
	/**
	 * 发送ugc的方法，根据ugc的组成方式添加合适的依赖关系
	 * @param ugc 要发送的ugc
	 */
	public void sendUGC(UGC ugc) {
		if (ugc.getUgcRequest() != null) { //此处的文本请求应转化为UGC请求
			
			if (ugc.getVoiceRequest() != null) {
				ugc.getVoiceRequest().setResponse(new UGCResponse(null));					//正式用
//				ugc.getVoiceRequest().setResponse(new UGCResponse(ugc.getResponse()));	//测试用
				this.setDepend(ugc.getUgcRequest(), ugc.getVoiceRequest());
			}
			if (ugc.getImageRequest() != null) {
				ugc.getImageRequest().setResponse(new UGCResponse(null));					//正式用
//				ugc.getImageRequest().setResponse(new UGCResponse(ugc.getResponse()));	//测试用
				this.setDepend(ugc.getUgcRequest(), ugc.getImageRequest());
			}
			ugc.getUgcRequest().setResponse(new UGCResponse(ugc.getResponse()));
			if (!ugc.getUgcRequest().getDependOtherRequestsIdList().isEmpty()) {
				mBlockedINetRequestList.add(new RequestListItem(ugc.getUgcRequest(),RequestListItem.STATUS_NOT_READY));
			} else {
				try {
					JSONObject bundle = ugc.getUgcRequest().getData();
					bundle.put("content", ugc.getUgcRequest().getUgcModel().getContentInfoJSONValue());
					bundle.put("client_time", System.currentTimeMillis());
					bundle.put("sig", AbstractHttpMcsService.getSigByJSON(bundle, false));
					logger.d("测试JSONObject的toString方法："+bundle.toString());
					ugc.getUgcRequest().setData(bundle);
					
				} catch (JSONException e) {
					logger.e(e.getMessage());
					e.printStackTrace();
				}
				HttpProviderWrapper.getInstance().addRequest(ugc.getUgcRequest());
			}
			
			if (ugc.getVoiceRequest() != null) {
				HttpProviderWrapper.getInstance().addRequest(ugc.getVoiceRequest());
			}
			if (ugc.getImageRequest() != null) {
				HttpProviderWrapper.getInstance().addRequest(ugc.getImageRequest());
			}
			
		} else { //由于在ugc中对不包含文本Request类型也初始化了ugcRequest，故而此处属于不允许存在情况
			return;
//			INetRequest ugcRequest = this.getUgctUploadRequest(null);
//			ugcRequest.setResponse(new ugcResponse(ugc.getResponse()));
//			if (ugc.getVoiceRequest() != null) {
//				this.setDepend(ugcRequest, ugc.getVoiceRequest());
//				HttpProviderWrapper.getInstance().addRequest(ugc.getVoiceRequest());
//			}
//			if (ugc.getImageRequest() != null) {
//				this.setDepend(ugcRequest, ugc.getImageRequest());
//				HttpProviderWrapper.getInstance().addRequest(ugc.getImageRequest());
//			}
		}
		
	}
	
	/**
	 * 获取ugcManager中阻塞的Request数量（仅用于测试逻辑）
	 * @return ugcManager中阻塞的Request数量
	 */
	public int getBlockedUGCNum() {
		return mBlockedINetRequestList.size();
	}
	
	/**
	 * ugcResponse的回调函数，用于处理发送ugc中包含的子Request返回时执行的逻辑
	 * 1：发送失败
	 * 发送失败次数+1，最后发送失败时间更新为当前时间
	 * 1.1：发送失败次数<3，重新发送
	 * 1.2：发送失败次数>=3
	 * 1.2.1：网络连接失败（errorCode == -99 || errorCode == -97）此时将失败的Request以及依赖其结果的Request一同交给RequestQueueManager处理，等待重新发送
	 * 1.2.2：其他类型失败。此时将失败的Request丢弃，不做处理，并将依赖其返回结果的Request从阻塞队列中移除
	 * 2：发送成功
	 * 2.1：发送成功的Request没有被依赖，则不做任何处理
	 * 2.2：发送成功的Request有被依赖的Request，则移除依赖关系
	 * @param req 
	 * @param obj
	 */
	public synchronized void onResponse(INetRequest req, JSONObject obj) {
		RequestListItem dependMeRequestItem = getDependMeResRequestItem(req.getDependMeRequestsIdList());
		if (obj.has("error_code")) {
			req.setFailCount(req.getFailCount()+1);
			req.setLastFailTime(System.currentTimeMillis());
			if(req.getFailCount() < 3) { //发送失败次数小于3次则重新发送，否则进入离线队列
				HttpProviderWrapper.getInstance().addRequest(req);
			} else {
				try {
					int errorCode = obj.getInt("error_code");
					if (errorCode == -97 || errorCode == -99) { //第一次请求返回网络错误
						if (req.getFirstSendStatus() == INetRequest.FIRST_SEND_STATUS_INITIAL) {
							req.setFirstSendStatus(INetRequest.FIRST_SEND_STATUS_RETURN_FAIL_NETERROR);
						}
						
						RequestQueueManager.getInstance().addOneRequest((INetRequest)req);
						
						if (dependMeRequestItem != null) {
							logger.d("dependMeRequest id:" + dependMeRequestItem.request.getCreatTime());
							mBlockedINetRequestList.remove(dependMeRequestItem);
							UGCResponse response = (UGCResponse) dependMeRequestItem.request.getResponse();
							if (response.getInternalResponse() != null) {
								response.getInternalResponse().response(req, obj);
							}
							RequestQueueManager.getInstance().addOneRequest(dependMeRequestItem.request);
						}
						
					} else { //第一次请求返回其他错误
						if (req.getFirstSendStatus() == INetRequest.FIRST_SEND_STATUS_INITIAL) {
							req.setFirstSendStatus(INetRequest.FIRST_SEND_STATUS_RETURN_FAIL_OTHERERROR);
							if (dependMeRequestItem != null) {
								mBlockedINetRequestList.remove(dependMeRequestItem);
								UGCResponse response = (UGCResponse) dependMeRequestItem.request.getResponse();
								if (response.getInternalResponse() != null) {
									response.getInternalResponse().response(req, obj);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else { //返回发送成功，此处应更新UGC请求内容，再根据依赖关系进行处理
			if (req.getFirstSendStatus() == INetRequest.FIRST_SEND_STATUS_INITIAL) {
				req.setFirstSendStatus(INetRequest.FIRST_SEND_STATUS_RETURN_SUCCESS);
			}
			if (dependMeRequestItem != null) {
				this.removeDepend(req, obj);
			} else {
				//发送成功，且在本类没有依赖关系（first send）
				return;
			}
		}
	}
	
	/**
	 * 添加依赖关系
	 * @param dependRequest 依赖其他Request的Request对象
	 * @param dependMeINetRequest 被依赖的Request对象
	 */
	public void setDepend(INetRequest dependRequest, INetRequest dependMeINetRequest) {
		dependRequest.getDependOtherRequestsIdList().add(dependMeINetRequest.getCreatTime());
		dependMeINetRequest.getDependMeRequestsIdList().add(dependRequest.getCreatTime());
	}
	
	/**
	 * 移除依赖关系的同时更新对应的Request内容
	 * 1：依赖其的Request仅依赖该成功返回的Request。此时更新并发送该阻塞的Request
	 * 2：依赖其的Request还有其他依赖的Request。此时从该阻塞的Request中去除该成功返回的Request的Id，更新内容，等待其他依赖Request的返回结果
	 * @param req
	 * @param obj
	 */
	public void removeDepend(INetRequest req, JSONObject obj) {
		if (req.getTypeInQueue()!=INetRequest.TYPE_UGC_POST_TEXT && !req.getDependMeRequestsIdList().isEmpty()) {
			for (Long denpendMeId : req.getDependMeRequestsIdList()) {
				for (RequestListItem blockINetRequestItem : mBlockedINetRequestList) {
					if (denpendMeId == blockINetRequestItem.request.getCreatTime()) {
						blockINetRequestItem.request.getDependOtherRequestsIdList().remove(req.getCreatTime());
						req.getDependMeRequestsIdList().remove(blockINetRequestItem.request.getCreatTime());
						this.updateUgcRequest(req, obj, blockINetRequestItem);
						if (blockINetRequestItem.request.getDependOtherRequestsIdList().isEmpty()) {
							blockINetRequestItem.status = RequestListItem.STATUS_SENDING;
							mBlockedINetRequestList.remove(blockINetRequestItem);
							logger.d(blockINetRequestItem.request.getUgcModel().getContentInfoJSONValue().toString());
							HttpProviderWrapper.getInstance().addRequest(blockINetRequestItem.request);
						}
					}
				}
			}
		}
		
	}
	
	//此处根据网络发送成功之后的返回结果在依赖它的Request中添加所需内容，此处与ugc耦合较大，如需改动需添加结构（依赖与被依赖类型，需添加的字段名称）
	/**
	 * 更新被阻塞Request的内容
	 * @param successRequest 返回成功发送的Request
	 * @param successJsonObject 返回成功发送的JSONObject
	 * @param blockedItem 被阻塞的Request对应的Item
	 */
	public void updateUgcRequest(INetRequest successRequest, JSONObject successJsonObject, RequestListItem blockedItem) { //根据发送成功的Response内容完善依赖其的Request请求的相关内容
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
				logger.d("IMG success："+bundle.toString());
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
				logger.d("VOICE success："+bundle.toString());
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
	
	/**
	 * 根据依赖我的Id集合获取其对应的被阻塞Request对应的Item，此处仅考虑对应被阻塞的Request仅有一个
	 * @param dependMeIdList 对应的被阻塞Request的Id集合
	 * @return
	 */
	private synchronized RequestListItem getDependMeResRequestItem(List<Long> dependMeIdList) {
		for (RequestListItem blockedRequestItem : mBlockedINetRequestList) {
			if (dependMeIdList.isEmpty()) {
				return null;
			}else {
				for (Long dependMeId : dependMeIdList) {
					if (dependMeId == blockedRequestItem.request.getCreatTime()) {
						return blockedRequestItem;
					}
				}
			}
		}
		return null;
	}

}

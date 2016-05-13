package com.renren.mobile.x2.network.mas;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.utils.Config;

/**
 * @author 宁长胜
 * Feed发送时对应的网络数据结构，暂时仅考虑由语音、图片和文字组成
 */
public class UGC {
	
	private INetRequest voiceRequest = null;
	private INetRequest imageRequest = null;
	private INetRequest ugcRequest = null;
	private INetResponse response = null;
	private String school_id = null;
	private int ugc_type = 0;
	
	public static final int UGC_TYPE_FEED = 1;
	public static final int UGC_TYPE_COMMENT = 2;
	
	/**
	 * Feed发送时的数据结构
	 * @param school_id UGC对应的学校id
	 * @param UgcType 发送的UGC类型：UGC_TYPE_FEED，发布新鲜事；UGC_TYPE_COMMENT发表评论
	 * @param FeedId 发布评论时使用，非评论传null
	 * @param voiceRequest 语音Request，通过调用FeedManager.getInstance().getVoiceUploadRequest(long toId, String vid, int seqid, String mode,
							int playTime, byte[] voiceData)方法获得
	 * @param imageRequest 图片Request，通过调用FeedManager.getInstance().getImageUploadRequest(byte[] data)获得
	 * @param ugcRequest 文字Request，通过调用FeedManager.getInstance().getUgctUploadRequest(String text)获得
	 * @param response 发送feed对应的Response
	 */
	public UGC(String school_id,
			int UgcType,
			Integer FeedId,
			INetRequest voiceRequest,
			INetRequest imageRequest,
			INetRequest ugcRequest,
			INetResponse response) {
		this.school_id = school_id;
		this.ugc_type = UgcType;
		this.voiceRequest = voiceRequest;
		this.imageRequest = imageRequest;
		this.ugcRequest = ugcRequest;
		this.response = response;
		
		if (this.ugcRequest == null) {
			this.ugcRequest = UGCManager.getInstance().getUGCUploadRequest(null, null, null);
		}
		
		JSONObject bundle;
		if (this.ugcRequest.getData() != null) {
			bundle = this.ugcRequest.getData();
		} else {
			bundle = AbstractHttpMcsService.obtainBaseRequestBundle(false);
		}
		
		switch (this.ugc_type) {
		case UGC_TYPE_FEED:
			this.ugcRequest.setUrl(Config.CURRENT_SERVER_URI+"/ugc/posts");
			break;
		case UGC_TYPE_COMMENT:
			try {
				assert(FeedId != null);
				bundle.put("feed_id", FeedId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			this.ugcRequest.setUrl(Config.CURRENT_SERVER_URI+"/ugc/comments");
			break;
		default:
			break;
		}
		
		try {
			bundle.put("school_id", this.school_id);
			this.ugcRequest.setData(bundle);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public INetRequest getVoiceRequest() {
//		voiceRequest.setTypeInQueue(INetRequest.TYPE_FEED_POST_VOICE);
		return voiceRequest;
	}

	public INetRequest getImageRequest() {
//		imageRequest.setTypeInQueue(INetRequest.TYPE_FEED_POST_IMG);
		return imageRequest;
	}

	public INetRequest getUgcRequest() {
//		ugcRequest.setTypeInQueue(INetRequest.TYPE_FEED_POST_UGC);
		return ugcRequest;
	}
	
	public INetResponse getResponse() {
		return response;
	}
	
}

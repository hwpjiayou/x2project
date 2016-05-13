package com.renren.mobile.x2.network.mas;

import java.util.List;
import org.json.JSONObject;

public interface INetRequest {
	
	public static final int TYPE_HTTP_REST = 0;
	public static final int TYPE_HTTP_GET_IMG = 1;
	public static final int TYPE_HTTP_POST_IMG = 2;
	public static final int TYPE_HTTP_GET_EMONTICONS = 3;
	public static final int TYPE_HTTP_GET_HTML = 4;
	public static final int TYPE_HTTP_SYNC_CONTACT = 5;
	public static final int TYPE_HTTP_POST_BIN_File = 8;
	public static final int TYPE_HTTP_GET_VOICE = 9;
	
	public static final int PRIORITY_LOW_PRIORITY = 0;
	public static final int PRIORITY_HIGH_PRIORITY = 1;
	
	public static final String gzip_key = "compress";
	public static final String gzip_value = "gz";
	
	public static final int TYPE_UGC_POST_VOICE = 10;
	public static final int TYPE_UGC_POST_IMG = 11;
	public static final int TYPE_UGC_POST_TEXT = 12;
	public static final int TYPE_UGC_POST_UGC = 13;
	
	public static final long PERIOD_OFFLINE_REQUEST_GIVEUP = 24*60*60*1000; //离线Request放弃时间间隔
	public static final long PERIOD_OFFLINE_REQUEST_SEND = 30*60*1000;		//离线Request发送时间间隔
	
	public static Integer FIRST_SEND_STATUS_INITIAL = 0;
	public static Integer FIRST_SEND_STATUS_RETURN_FAIL_OTHERERROR = 4;	//第一次请求返回非网络错误(100)
	public static Integer FIRST_SEND_STATUS_RETURN_FAIL_NETERROR = 5;	//第一次请求返回网络错误(101)
	public static Integer FIRST_SEND_STATUS_RETURN_SUCCESS = 6;			//第一次请求返回发送成功(11X)
	
	public static Integer FLAG_MASK_RETURN = 4;							//第一次请求是否返回掩码(100)
	public static Integer FLAG_MASK_SUCCESS = 2;						//返回是否成功掩码(010)
	public static Integer FLAG_MASK_ERROR = 1;							//返回的失败类型是否为未联网(001)
	
	
	boolean useGzip();
	
	/**
	 * 获取图片请求的优先级
	 * @return
	 */
	int getPriority();
	
	/**
	 * 设置图片请求的优先级
	 * @return
	 */
	void setPriority(int priority);
	
	/**
	 * 获取请求类型
	 * @return
	 */
	int getType();
	
	/**
	 * 设置请求类型
	 * @param type
	 */
	void setType(int type);
	
	/**
	 * 设置响应回调.
	 * @param resp
	 */
	void setResponse(INetResponse resp);
	
	/**
	 * 获取响应回调.
	 * @return
	 */
	INetResponse getResponse();

	/**
	 * 设置url.
	 * @param url
	 */
	void setUrl(String url);
	
	/**
	 * 获取url.
	 * @return
	 */
	String getUrl();
	
	Long getId();
	
	long getCurrentSession();
	
	void setCurrentSession(long currentSession);
	
	/**
	 * 设置请求参数，获取照片时不需要设置.
	 * @param data
	 */
	void setData(JSONObject data);
	
	/**
	 * 获取请求参数.
	 * @return
	 */
	JSONObject getData();
	
	void setSecretKey(String key);
	
	String getSecretKey();

	String getParamsString();

	String getMethod();
	
//	void setFileName(String fileName);
	
	byte[] serialize();
	
	public int getTypeInQueue();
	
	public void setTypeInQueue(int ftype);
	
	public void setFirstSendStatus(Integer firstSendStatus);
	
	public Integer getFirstSendStatus();
	
	public List<Long> getDependOtherRequestsIdList();
	
	public List<Long> getDependMeRequestsIdList();
	
	public void setUgcModel(UGCContentModel ugcModel);
	
	public UGCContentModel getUgcModel();

	public void setFailCount(int failCount);

	public int getFailCount();

	public void setLastFailTime(long lastFailTime);

	public long getLastFailTime();

	public long getCreatTime();
	
	public void setCreatTime(long creatTime);
	
	public void setDependOtherRequestsIdList(List<Long> dependOtherRequestsIdList);
	
	public void setDependMeRequestsIdList(List<Long> dependMeRequestsIdList);
}

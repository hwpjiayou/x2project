package com.renren.mobile.x2.network.mas;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.utils.CommonUtil;

/**
 * 网络请求的Http封装.
 * 
 * @author wenhua.li@opi-corp.com 2009-7-16下午04:30:23
 */
public class HttpRequestWrapper implements INetRequest {

	private JSONObject data = null;

	private String url = null;

	private INetResponse response = null;

	private Long id;// 用来标识请求唯一性

	private int type = TYPE_HTTP_REST;
	
	private Integer firstSendStatus = FIRST_SEND_STATUS_INITIAL;
	private Integer typeInQueue = -1;
	private List<Long> dependOtherRequestsIdList = new ArrayList<Long>();
	private List<Long> dependMeRequestsIdList = new ArrayList<Long>();
	private UGCContentModel ugcModel = null;
	private long creatTime = 0;
	private int failCount = 0;
	private long lastFailTime = 0;

	private int mpriority = PRIORITY_HIGH_PRIORITY;

	private long currentSession;
	
	HttpRequestWrapper(){
		this.id = System.currentTimeMillis();
		this.creatTime = this.id;
	}
	public Long getId() {
		return id;
	}
	
	public long getCurrentSession() {
		return currentSession;
	}

	public void setCurrentSession(long currentSession) {
		this.currentSession = currentSession;
	}

	public int getPriority() {
		return mpriority;
	}

	public void setPriority(int priority) {
		mpriority = priority;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public INetResponse getResponse() {
		return response;
	}

	public void setResponse(INetResponse resp) {
		this.response = resp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String getMethod() {
		try {
			return data.getString("method");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private final static int COMPUTE_SIGANATURE_CHAR_COUNT = 50;

	@Override
	public String getParamsString() {
		if (data == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
			Iterator<?> keys = data.keys();
			if (keys == null || keys.hasNext() == false) {
				return "";
			}
			Vector<String> vecSig = new Vector<String>();
			try {
				while (keys.hasNext()) {
					String key = keys.next().toString();
					String val = data.getString(key);
					sb.append(key).append('=').append(URLEncoder.encode(val)).append('&');

					// Modified by lin.zhu@opi-corp.com
					// 3G Server端计算Sig参数, 对value截长前50个字符.
					// Caution! 计算sig参数的入口(调用getSig方法)不止这一个,
					// 确保每个计算sig的入口均截长.
					if (val.length() > COMPUTE_SIGANATURE_CHAR_COUNT) {
						val = val.substring(0, COMPUTE_SIGANATURE_CHAR_COUNT);
					}
					// End modify.

					vecSig.add(key + "=" + val);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String[] ss = new String[vecSig.size()];
			vecSig.copyInto(ss);
			secretKey = (LoginManager.getInstance().getSecretKey() == null ? AbstractHttpMcsService.LOCAL_SECRET_KEY : LoginManager.getInstance().getSecretKey());
			CommonUtil.log("cdw", "secretkey = "+secretKey);
			if (secretKey.length() != 0) {
				sb.append("sig=").append(AbstractHttpMcsService.getSigByStrings(ss, secretKey));
			}

//		}
		String s = sb.toString();

		return s;
	}

	/**
	 * 将请求序列化操作
	 * 
	 * @param request
	 *            请求的对象
	 * @return 序列化数据
	 */
	public byte[] serialize() {
		if (data == null) {
			return null;
		}
		if (INetRequest.TYPE_HTTP_POST_IMG == getType() || INetRequest.TYPE_HTTP_POST_BIN_File == getType()) {
			try {
				return (byte[]) data.get("data");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		byte[] ret = null;
		try {
			ret = getParamsString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public boolean useGzip() {
		try {
			if (data != null && gzip_value.equals(data.getString(gzip_key))) 
				return true;
		} catch (JSONException e) {}
		return false;
	}

	private String secretKey = (LoginManager.getInstance().getSecretKey() == null ? "" : AbstractHttpMcsService.LOCAL_SECRET_KEY);

	@Override
	public void setSecretKey(String key) {
		this.secretKey = key;
	}

	@Override
	public String getSecretKey() {
		return secretKey;
	}
	@Override
	public int getTypeInQueue() {
		return typeInQueue;
	}
	@Override
	public void setTypeInQueue(int ftype) {
		typeInQueue = ftype;
	}
	@Override
	public void setFirstSendStatus(Integer firstSendStatus) {
		this.firstSendStatus = firstSendStatus;
	}
	@Override
	public Integer getFirstSendStatus() {
		return firstSendStatus;
	}
	@Override
	public List<Long> getDependOtherRequestsIdList() {
		return dependOtherRequestsIdList;
	}
	@Override
	public List<Long> getDependMeRequestsIdList() {
		return dependMeRequestsIdList;
	}
	@Override
	public void setUgcModel(UGCContentModel ugcModel) {
		this.ugcModel = ugcModel;
	}
	@Override
	public UGCContentModel getUgcModel() {
		if (ugcModel == null) {
			ugcModel = new UGCContentModel();
		}
		return ugcModel;
	}
	@Override
	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	@Override
	public int getFailCount() {
		return failCount;
	}
	@Override
	public void setLastFailTime(long lastFailTime) {
		this.lastFailTime = lastFailTime;
	}
	@Override
	public long getLastFailTime() {
		return lastFailTime;
	}
	@Override
	public long getCreatTime() {
		return creatTime;
	}
	@Override
	public void setCreatTime(long creatTime) {
		this.creatTime = creatTime;
	}
	@Override
	public void setDependOtherRequestsIdList(List<Long> dependOtherRequestsIdList) {
		this.dependOtherRequestsIdList = dependOtherRequestsIdList;
	}
	@Override
	public void setDependMeRequestsIdList(List<Long> dependMeRequestsIdList) {
		this.dependMeRequestsIdList = dependMeRequestsIdList;
	}
}

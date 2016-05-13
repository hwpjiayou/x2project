package com.renren.mobile.x2.network.mas;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Config;
import com.renren.mobile.x2.utils.DateFormat;
import com.renren.mobile.x2.utils.LanguageSettingUtil;
import com.renren.mobile.x2.utils.Md5;
import com.renren.mobile.x2.utils.SystemService;

abstract class AbstractHttpMcsService {

	/*3G 服务器的API KEY*/
	protected final static String MCS_API_KEY = RenrenChatApplication.getApiKey();
	/*本地Secret key*/
	// login返回成功后被付值为"运行時secret key".
	protected final static String LOCAL_SECRET_KEY =  RenrenChatApplication.getSecretKey();
	
//	public static String sRuntimeSecretKey = null;
//	public static String sRuntimeSessionKey = null;
	
	/**
	 * 每个接口都必须要携带的统计信息(client_info)
	 * 
	 * @return client_info
	 */
	public final static  String getClientInfo() {
		String imei = "";
		String mac = "";
		int mcc = -1; 
		int mnc = -1;
		if (RenrenChatApplication.getApplication() != null) {
			TelephonyManager tm = SystemService.sTelephonyManager;
			
			mcc = RenrenChatApplication.getApplication().getResources().getConfiguration().mcc;
			mnc = RenrenChatApplication.getApplication().getResources().getConfiguration().mnc; 
			if (tm != null && !TextUtils.isEmpty(imei)) {

			} else {
				WifiManager wifi = SystemService.sWifiManager;
				if (wifi != null) {
					WifiInfo info = wifi.getConnectionInfo();
					if (info != null) {
						mac = info.getMacAddress();
						imei = mac;
					}
				}
			}
		}

		JSONObject clientInfo = new JSONObject();
		try {
			clientInfo.put("screen", RenrenChatApplication.getScreen());
			clientInfo.put("os", Build.VERSION.SDK + "_" + Build.VERSION.RELEASE);
			clientInfo.put("model", Build.MODEL);
			clientInfo.put("from", RenrenChatApplication.getFrom());
			String[] langStrs = LanguageSettingUtil.getInstance().getLanguage().split("[_]");
			clientInfo.put("language", String.format("%s_%s", langStrs[0].toLowerCase(), langStrs[1].toUpperCase()));
			clientInfo.put("uniqid", imei);
			clientInfo.put("mac", mac);
			clientInfo.put("version", RenrenChatApplication.getVersionName());
			clientInfo.put("language", "zh_CN");
			if(mcc != 0){
				clientInfo.put("other", String.format("%03d", mcc) + String .format("%02d", mnc) + ",");
			}
			return clientInfo.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 通过JSON数据来计算sig
	 * @param data 获取sig的JSON数据
	 * @param isForLogin 是否获取登录所需sig
	 * @return sig
	 */
	protected static String getSigByJSON(JSONObject data, boolean isForLogin) {
		Iterator<?> keys = data.keys();
		StringBuilder sb = new StringBuilder();
		Vector<String> vecSig = new Vector<String>();
		try {
			while (keys.hasNext()) {
				String key = keys.next().toString();
				String val = data.getString(key);
				sb.append(key).append('=').append(URLEncoder.encode(val)).append('&');
				if (val.length() > 50) {
					val = val.substring(0, 50);
				}
				vecSig.add(key + "=" + val);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String[] strings = new String[vecSig.size()];
		vecSig.copyInto(strings);
		String secret_key = isForLogin ? LOCAL_SECRET_KEY : LoginManager.getInstance().getSecretKey();
		return getSigByStrings(strings, secret_key);
	}
	
	/**
	 * 通过String数组计算sig
	 * @param strings 计算sig的String数组
	 * @param secretKey 
	 * @return sig
	 */
	protected static String getSigByStrings(String[] strings, String secretKey) {
		for (int i = 0; i < strings.length; i++) {
			for (int j = strings.length - 1; j > i; j--) {
				if (strings[j].compareTo(strings[j - 1]) < 0) {
					String temp = strings[j];
					strings[j] = strings[j - 1];
					strings[j - 1] = temp;
				}
			}
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
		}
		sb.append(secretKey);
		return Md5.toMD5(sb.toString());
	}
	
	/**
	 * 获取本地服务器接口调用Request
	 * @param interfaceNameRestPath 本地接口短地址（不包含Config.CURRENT_SERVER_URI部分）
	 * @return 本地服务器接口调用Request
	 */
	protected static final INetRequest obtainINetRequest(String interfaceNameRestPath){
		INetRequest request = new HttpRequestWrapper();
		request.setUrl(Config.CURRENT_SERVER_URI+interfaceNameRestPath);
		return request;
	}
	
	/**
	 * 获取调用网路资源（不限于本地服务器）调用Request
	 * @param url 网路资源的URL
	 * @param type Request的Type
	 * @param response 调用后回调的Response
	 * @return 网路资源调用的Request
	 */
	protected static final INetRequest obtainINetRequest(String url,int type,INetResponse response){
		INetRequest request = new HttpRequestWrapper();
		request.setUrl(url);
		request.setType(type);
		request.setResponse(response);
		return request;
	}
	
	/**
	 * 获取UGC接口调用的Request
	 * @param request UGC接口相关Request
	 * @param interfaceNameRestPath 本地接口短地址（不包含Config.CURRENT_SERVER_URI部分）
	 * @return UGC接口相关Request
	 */
	protected static final INetRequest obtainINetRequest(INetRequest request, String interfaceNameRestPath){
		if (request == null) {
			request = new HttpRequestWrapper();
		}
		request.setUrl(Config.CURRENT_SERVER_URI+interfaceNameRestPath);
		return request;
	}
	

	/**
	 * 获取携带了基本信息的Bundle（JSONObject类型）
	 * @param batchRun 是否延迟发送，一般采用非延迟发送，传false
	 * @return 携带了基本统计信息的Bundle
	 */
	protected static JSONObject obtainBaseRequestBundle(boolean batchRun){
		JSONObject bundle = new JSONObject();
		
		try {
			bundle.put("api_key", MCS_API_KEY);
			bundle.put("call_id", System.currentTimeMillis());
			bundle.put("format", "JSON");
			//bundle.put("compress", "gz");
			// 使用批调用的请求不包含session_key, session_key统一由batch.run方法提供.
			if (!batchRun) {
				if (!TextUtils.isEmpty(LoginManager.getInstance().getSecretKey())) {
					bundle.put("session_key", LoginManager.getInstance().getSessionKey());
				}
			}
			bundle.put("client_info", getClientInfo());
			if(!"".equals(getMISCInfo())){
				bundle.put("misc", getMISCInfo());
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return bundle;
	}
	
	/**
	 * 获取每个接口都必须要携带的统计信息中的misc
	 * @description 
	 * 		misc字段定义
	 *         HTF：标志来源页面（即从哪个页面进入当前页面的）
	 *         离线or在线：0为在线，1为离线
	 *         联网状态：1对应wifi；2对应非wifi
	 * 		格式要求：最后一个字段后不加逗号。
	 * @return misc字符串
	 */
	protected static String getMISCInfo(){
		StringBuilder misc = new StringBuilder(",,");
		WifiManager wifi = SystemService.sWifiManager;
		if (wifi != null) {
			WifiInfo info = wifi.getConnectionInfo();
			if (info != null) {
				misc.append("1");
			}else{
				misc.append("2");
			}
		}
		else{
			misc.append("2");
		}
		return misc.toString();
	}
	
	/**
	 * 向本地服务器发送网络请求
	 * @param interfaceNameRest 本地接口短地址（不包含Config.CURRENT_SERVER_URI部分）
	 * @param bundle Request所携带的JSONObject
	 * @param response 调用返回的回调Response
	 */
	protected void sendRequest(String interfaceNameRest,JSONObject bundle,INetResponse response){
		INetRequest request = obtainINetRequest(interfaceNameRest);
		request.setData(bundle);
		request.setResponse(response);
		HttpProviderWrapper.getInstance().addRequest(request);
	}
	/**
	 * 构建照片上传的Multipart数据
	 */
	protected byte[] buildUploadPhotoData(long toId, byte[] imgData) {

		String[] props = { "api_key", "call_id", "client_info", "format", "session_key", "toid", "v", "sig" };
		String[] values = { MCS_API_KEY, String.valueOf(System.currentTimeMillis()), getClientInfo(), "json", LoginManager.getInstance().getSessionKey(), String.valueOf(toId), "1.0", "" };

		return this.buildImgDatas(imgData, props, values);
	}
	
	/**
	 * 构建照片上传的Multipart数据
	 */
	protected byte[] buildUploadPNGPhotoData(long toId, byte[] imgData) {

		byte[] ret = null;
		try {
			String[] props = { "api_key", "call_id", "client_info", "format", "session_key", "toid", "v", "sig" };
			String[] values = { MCS_API_KEY, String.valueOf(System.currentTimeMillis()), getClientInfo(), "json", LoginManager.getInstance().getSessionKey(), String.valueOf(toId), "1.0", "" };
			String[] params = new String[props.length - 1];
			for (int i = 0; i < params.length; i++) {
				if (values[i] != null && values[i].length() > 50) {
					params[i] = props[i] + "=" + values[i].substring(0, 50);
				} else {
					params[i] = props[i] + "=" + values[i];
				}

			}
			values[values.length - 1] = getSigByStrings(params, LoginManager.getInstance().getSecretKey());
			String BOUNDARY = "FlPm4LpSXsE"; // separate line
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < props.length; i++) { // send each property
				sb = sb.append("--");
				sb = sb.append(BOUNDARY);
				sb = sb.append("\r\n");
				sb = sb.append("Content-Disposition: form-data; name=\"" + props[i] + "\"\r\n\r\n");
				CommonUtil.log("lee","Disposition: " + props[i]+" = " + values[i]);
				sb = sb.append(values[i]);
				sb = sb.append("\r\n");
			}
			sb = sb.append("--");
			sb = sb.append(BOUNDARY);
			sb = sb.append("\r\n");
			sb = sb.append("Content-Disposition: form-data;name=\"data\";filename=\"" + DateFormat.now2() + ".png\"\r\n");
			sb = sb.append("Content-Type: image/png\r\n\r\n");
			CommonUtil.log("lee","Disposition: " + sb.toString());
			byte[] begin_data = sb.toString().getBytes("UTF-8");
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(begin_data);
			baos.write(imgData);
			baos.write(end_data);
			ret = baos.toByteArray();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	 /**
     * 构建Cover上传的Multipart数据
     */
    protected byte[] buildUploadCoverData(byte[] imgData, double yScale, String special) {
    	
    	String[] props = { "api_key", "call_id", "client_info", "format", "session_key",  "v",
                "action","y_scale","special", "name", "sig" };
        String[] values = { MCS_API_KEY, String.valueOf(System.currentTimeMillis()), getClientInfo(), "json",LoginManager.getInstance().getSessionKey(),  "1.0",
                "POST",String.valueOf(yScale),"special", "tall", "" };
        return buildImgDatas(imgData, props, values);
    }
	
    /**
     * 构建头像上传的Multipart数据
     */
    protected byte[] buildUploadHeadData(byte[] imgData, int x, int y , int width, int height) {
    	
    	String[] props = { "api_key", "call_id", "client_info", "format", "session_key",  "v",
                "action","x","y","width","height","name","sig" };
        String[] values = { MCS_API_KEY, String.valueOf(System.currentTimeMillis()), getClientInfo(), "json",LoginManager.getInstance().getSessionKey(),  "1.0",
                "POST",String.valueOf(x),String.valueOf(y), String.valueOf(width),String.valueOf(height),"tall", "" };
        return buildImgDatas(imgData, props, values);
    }

    /**
     * 构建图片上传的Multipart数据
     */
    protected byte[] buildUploadPhotoData(byte[] imgData) {
    	
    	 String[] props = { "api_key", "call_id", "client_info", "format", "session_key",  "v","action","name","sig" };
         String[] values = { MCS_API_KEY, String.valueOf(System.currentTimeMillis()), getClientInfo(), "json",LoginManager.getInstance().getSessionKey(),  "1.0","POST","tall", "" };

         return buildImgDatas(imgData, props, values);
    }
    
    private byte[] buildImgDatas(byte[] data, String[] props, String[] values) {
		byte[] ret = null;
		try {
			String[] params = new String[props.length - 1];
	        for (int i = 0; i < params.length; i++) {
	            if (values[i] != null && values[i].length() > 50) {
	                params[i] = props[i] + "=" + values[i].substring(0, 50);
	            } else {
	                params[i] = props[i] + "=" + values[i];
	            }
	
	        }
	        values[values.length - 1] = getSigByStrings(params, LoginManager.getInstance().getSecretKey());
	        String BOUNDARY = "FlPm4LpSXsE"; // separate line
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < props.length; i++) { // send each property
	            sb = sb.append("--");
	            sb = sb.append(BOUNDARY);
	            sb = sb.append("\r\n");
	            sb = sb.append("Content-Disposition: form-data; name=\"" + props[i] + "\"\r\n\r\n");
	            sb = sb.append(values[i]);
	            sb = sb.append("\r\n");
	        }
	        sb = sb.append("--");
	        sb = sb.append(BOUNDARY);
	        sb = sb.append("\r\n");
	        sb = sb.append("Content-Disposition: form-data;name=\"data\";filename=\"" + DateFormat.now2() + ".jpg\"\r\n");
	        sb = sb.append("Content-Type: image/jpg\r\n\r\n");
	        byte[] begin_data = sb.toString().getBytes("UTF-8");
	        byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        baos.write(begin_data);
	        baos.write(data);
	        baos.write(end_data);
	        ret = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * @author dingwei.chen
	 * 
	 * @说明 构建语音上传的Multipart数据
	 * 
	 * @param vid
	 *            string 语音组id
	 * @param seqid
	 *            int 切割语音的序列号
	 * @param mode
	 *            * string 传输模式segment|end，segment为分割语音模式，end为结束标识
	 * */
    
    
	protected static byte[] buildUploadAudioData(long toId, String vid, int seqid, String mode, int playTime, byte[] voiceData) {

		byte[] ret = null;
		try {
			String[] props = { "api_key", "call_id", "client_info","format",  "mode", "playtime", "seqid",
                    "session_key", "toid", "v", "vid", "action", "sig" };
			String[] values = { MCS_API_KEY, String.valueOf(System.currentTimeMillis()),getClientInfo(), "json", mode, String.valueOf(playTime), String.valueOf(seqid),
					LoginManager.getInstance().getSessionKey(),String.valueOf(toId), "1.0", String.valueOf(vid), "POST", "" };
			String[] params = new String[props.length - 1];
			for (int i = 0; i < params.length; i++) {
				if (values[i] != null && values[i].length() > 50) {
					params[i] = props[i] + "=" + values[i].substring(0, 50);
				} else {
					params[i] = props[i] + "=" + values[i];
				}

			}
			values[values.length - 1] = getSigByStrings(params, LoginManager.getInstance().getSecretKey());
			String BOUNDARY = "FlPm4LpSXsE"; // separate line
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < props.length; i++) { // send each property
				sb = sb.append("--");
				sb = sb.append(BOUNDARY);
				sb = sb.append("\r\n");
				sb = sb.append("Content-Disposition: form-data; name=\"" + props[i] + "\"\r\n\r\n");
				sb = sb.append(values[i]);
				sb = sb.append("\r\n");
			}
			sb = sb.append("--");
			sb = sb.append(BOUNDARY);
			sb = sb.append("\r\n");
			sb = sb.append("Content-Disposition: form-data;name=\"data\";filename=\"test.spx" + "\"\r\n");
			sb = sb.append("Content-Type: application/octet-stream\r\n\r\n");
			byte[] begin_data = sb.toString().getBytes("UTF-8");
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(begin_data);
			baos.write(voiceData);
			baos.write(end_data);
			ret = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
}

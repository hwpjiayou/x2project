package com.renren.mobile.x2.network.mas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.renren.mobile.x2.components.login.LoginManager;

/**
 * @MCS 网络业务
 */
public final class HttpMasService extends AbstractHttpMcsService {

	private static HttpMasService sInstance = new HttpMasService();

	private HttpMasService() {
	}

	public static HttpMasService getInstance() {
		return sInstance;
	}
	
	/**
	 * 登陆接口
	 * 
	 * @param accountType		是否用人人账户登陆 			required
	 * @param user				账户名			 			required
	 * @param password			登陆密码,经过MD5加密的		required
	 * @param captcha_needed	是否支持验证码,1：支持验证码  	not required
	 * @param captcha			用户输入的验证码   			not required
	 * @param response			登陆回调 						required
	 * 
	 * @author xingchen.li
	 */
	public void loginX2(int accountType, long session, String user, String password, int captcha_needed, String captcha,
			INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
//			bundle.put("is_fake", true);
			bundle.put("user", user);
			bundle.put("password", password);
			bundle.put("captcha_needed", captcha_needed);
			bundle.put("captcha", captcha);
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			bundle.put("sig", getSigByJSON(bundle, true));
			INetRequest request = 
				obtainINetRequest(accountType == LoginManager.LOGIN_RENREN ? "/user/login/renren":"/user/login/renren");			
			request.setData(bundle);
			request.setResponse(response);
			request.setSecretKey(AbstractHttpMcsService.LOCAL_SECRET_KEY);
			request.setCurrentSession(session);
			HttpProviderWrapper.getInstance().addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取用户资料
	 * 
	 */
	public void getProfile(String userId, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("user_id", userId);
			//bundle.put("profile_type", profileType);
			bundle.put("action", "GET");
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/user/profile/" + userId);
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
	
	/***
	 * 完善用户资料,只需要将需要更新的数据写入到Map中,完善资料目前,<b>必须上传学校相关信息，学校ID，院系ID和入学时间</b>,<b>注意其中的KEY值. 请根据WIKI</b>
	 * 
	 * <p>注意,参数如果有二级属性，需要<b>用"_"下划线与一级属性拼接在一起</b>。比如<b>学校</b> , <b>生日</b>
	 * <br>学校：school,二级属性为id, department_id,拼接则为 school_id,school_department_id
	 * <br>生日："birth_day":{"year":1979,"month":8,"day":6} <b>例外:</b> birth_year, birth_month, birth_day .etc<p>
	 * 
	 * @param profiles 为<b> HashMap<String, String></b>的实例,可更新内容包括：<b>用户资料</b>和<b>学校信息</b>
	 * @param response 回调
	 * 
	 * @author xingchen.li
	 */
	public void completeUserInfo (HashMap<String, String> profiles, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);		
		try {
			bundle.put("action", "PUT");
			Iterator<?> iterator = profiles.entrySet().iterator();
			while(iterator.hasNext()) {
				@SuppressWarnings("unchecked")
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
				String key = entry.getKey();				
				String value = entry.getValue();
				bundle.put(key, value);
			}
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/user/profile");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改用户资料
	 * TODO
	 */
	
	/***
	 * 搜索学校信息,根据关键字搜索学校信息<br>
	 * <b>一些固定的必要参数,接口内已添加</b>
	 * 
	 * @param keys 		学校关键字 		required
	 * @param response 	回调     			required
	 * @param batchRun  是否是批量请求 	required
	 * 
	 * @author xingchen.li
	 */
	
	public INetRequest searchSchool(String keys, INetResponse response , boolean batchRun) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("action", "GET");	//required 
			bundle.put("type", "school");	//required 	
			bundle.put("q", keys);
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/search");
			request.setData(bundle);
			request.setResponse(response);
			if (batchRun) {
				return request;
			} else {
				HttpProviderWrapper.getInstance().addRequest(request);
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * 搜索院系信息,根据学校ID 获取院系信息
	 * 
	 * @param schoolId 	学校ID 			required
	 * @param response 	回调     			required
	 * @param batchRun  是否是批量请求 	required
	 * 
	 * @author xingchen.li
	 */
	public INetRequest searchDepartment(String schoolId, INetResponse response , boolean batchRun) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("schoolId", schoolId);
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/school/" +schoolId + "/department");
			request.setData(bundle);
			request.setResponse(response);
			if (batchRun) {
				return request;
			} else {
				HttpProviderWrapper.getInstance().addRequest(request);
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * 发布帖子
	 * 
	 * @param schoolId 	学校ID 						required
	 * @param content 	帖子内容,ugc content			required
	 * @param response 	回调     						required
	 * @param batchRun  是否是批量请求 				required
	 * @author xingchen.li
	 */
	public INetRequest sendPost(JSONObject content , String id, INetResponse response, boolean batchRun) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", id);
			bundle.put("content", content);
			bundle.put("client_time", System.currentTimeMillis());// required
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/ugc/posts");
			request.setData(bundle);
			request.setResponse(response);
			if (batchRun) {
				return request;
			} else {
				HttpProviderWrapper.getInstance().addRequest(request);
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 删除一个自己发布的帖子
	 * 
	 * @param schoolId  学校Id
	 * @param feedId    查询的新鲜事Id
	 * @param postId	删除帖子的Id
	 * 
	 * @author xingchen.li 
	 **/
	public void deletePost(String schoolId, String feedId, String postId, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", schoolId);
			bundle.put("feed_id", feedId);
			bundle.put("post_id", postId);
			
			bundle.put("action", "DELETE"); 						// required
			bundle.put("client_time", System.currentTimeMillis());	// required
			
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/ugc/posts");
			
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 对一个帖子发表评论
	 * 
	 * @param schoolId 学校Id				required
	 * @param feedId   新鲜事Id				required
	 * @param content  帖子内容，由UGC拼成	required
	 * 
	 * @author xingchen.li 
	 */
	public void postComment(String schoolId, String feedId, JSONObject content, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", schoolId);
			bundle.put("feed_id", feedId);
			bundle.put("content", content);	
			bundle.put("client_time", System.currentTimeMillis());
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/ugc/comments");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除评论
	 * 
	 * @param schoolId 	学校Id				required
	 * @param feedId   	新鲜事Id				required
	 * @param commentId 评论Id				required
	 * 
	 * @author xingchen.li 
	 */
	public void deleteComment(String schoolId, String feedId, String commentId, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", schoolId);
			bundle.put("feed_id", feedId);
			bundle.put("comment_id", commentId);
			bundle.put("action", "DELETE"); 						// required
			bundle.put("client_time", System.currentTimeMillis());	// required
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/ugc/comments");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取一个帖子下的评论
	 * <p>limits传 10 条即可,默认为10条,如果需要其他则多传,field字段建议传空串,如果需要评论传回较少的参数,请自行拼接</p>
	 * 
	 * @param schoolId  学校Id													required
	 * @param feedId	新鲜事Id													required
	 * @param limits    一页的数量，默认10条										not required
	 * @param afterId	返回比此ID小, 即时间早于此ID的评论，默认返回最新    			not required
	 * @param field		默认包含用户,评论和客户端信息,建议传空串					not required
	 * @author xingchen.li 
	 **/
	public void getPostComments(String schoolId, String feedId, int limits, String afterId, String field, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			
			bundle.put("school_id", schoolId);
			bundle.put("feed_id", feedId);
			bundle.put("limit", limits);
			bundle.put("after_id", afterId);
			if(TextUtils.isEmpty(field))
				//调用接口者给空串的话,接口拼好评论所需参数串,
				bundle.put("field", "content,user,updated_time,created_time,state,ugc_id");
			else
				bundle.put("field", field);
			
			bundle.put("action", "GET");  //required
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/feeds/comments/list");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 对一个帖子发布赞评论
	 * 
	 * @param schoolId 学校Id			required
	 * @param feedId   新鲜事Id			required
	 * 
	 * @author xingchen.li 
	 */
	public void postLike(String schoolId, String feedId, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", schoolId);
			bundle.put("feed_id", feedId);
			bundle.put("client_time", System.currentTimeMillis());//客户端请求时间  required
			
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/ugc/likes");
			
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除赞
	 * 
	 * @param schoolId 学校Id			required
	 * @param feedId   新鲜事Id			required
	 * @param likeId   赞Id				required
	 * 
	 * @author xingchen.li 
	 */
	public void deleteLike(String schoolId, String feedId, String likeId, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", schoolId);
			bundle.put("feed_id", feedId);
			bundle.put("like_id", likeId);	
			
			bundle.put("action", "DELETE"); 						// required
			bundle.put("client_time", System.currentTimeMillis());	// required
			
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/ugc/likes");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据FeedId获取单条feed
	 * 
	 * @param schoolId 学校Id
	 * @param feedId   查询的新鲜事Id
	 * @param field    客户端条件，以半角逗号分隔，例如是否包括评论详情 filed = comment,默认返回所有
	 * 
	 * @author xingchen.li 
	 **/
	public void getFeedByID(String schoolId, String feedId, String field, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", schoolId);
			bundle.put("feed_id", feedId);
			bundle.put("field", field);
			bundle.put("action", "GET");  //required
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/feeds/school");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取校园十大新鲜事
	 * 
	 * @param schoolId 学校Id
	 */
	public void getTopTen(String schoolId, String field, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", schoolId);
			bundle.put("field", field);
			bundle.put("action", "GET");  //required
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/feeds/school/topten");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询一个学校的所有新鲜事
	 * 
	 * @param schoolId 	学校Id												required
	 * @param limits   	一页的数量，默认10条									not required
	 * @param field    	客户端条件, 以逗号分隔.默认返回全部					not required
	 * @param afterId	返回比此ID小, 即时间早于此ID的新鲜事,不传取最新新鲜事  	not required
	 * 
	 * @author xingchen.li 
	 **/
	public void getSchoolFeeds(String schoolId, int limits, String afterId, String field, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("school_id", schoolId);
			bundle.put("limit", limits);
			bundle.put("after_id", afterId);
			bundle.put("field", field);
			bundle.put("action", "GET");  //required
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/feeds/school/list");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取我发过的新鲜事
	 * 
	 * @param limits   	一页的数量，默认10条									not required
	 * @param field    	客户端条件, 以逗号分隔.默认返回全部					not required
	 * @param afterId	返回比此ID小, 即时间早于此ID的新鲜事,不传取最新新鲜事  	not required
	 * 
	 * @author xingchen.li 
	 **/
	public void getMyFeeds(int limits, String afterId, String field, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("limit", limits);
			bundle.put("after_id", afterId);
			bundle.put("field", field);
			bundle.put("action", "GET");  //required
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/feeds/me");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取其他人的新鲜事
	 * @param userId	用户的userId											require
	 * @param limits   	一页的数量，默认10条									not required
	 * @param field    	客户端条件, 以逗号分隔.默认返回全部					not required
	 * @param afterId	返回比此ID小, 即时间早于此ID的新鲜事,不传取最新新鲜事  	not required
	 * 
	 * @author xingchen.li 
	 **/
	public void getSomeOnesFeed(String userId, String field, int limits, String afterId, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("user_id", userId);
			bundle.put("limit", limits);
			bundle.put("after_id", afterId);
			bundle.put("field", field);
			bundle.put("action", "GET");  //required
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/feeds/user");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 定位
	 * TODO 暂不能使用
	 * 
	 * <P><b>gpsLatitude、gpsLongitude 或 latlon必须存在一个，不能都不存在</b></P>
	 * 
	 * @param gpsLatitude  	gps的纬度
	 * @param gpsLongitude 	gps的经度
	 * @param latlon 		json化的经纬度，若有此值gpsLatitude，gpsLongitude无效
	 * @param offset   		偏移量，默认值0
	 * @param limits   		一页的数量，默认10条
	 * @param p_zip 		如果传输的latlon参数为gzip压缩后再经过Base64编码的数据，那么值为1，如果不是则为0。缺省值为0
	 * @param cl			如果传输的latlon参数为gzip压缩后再经过AES加密，最后Base64编码的数据，那么值为1。如果不是则为0。缺省值为0
	 *
	 */
	public void getMyLocation(double gpsLatitude, double gpsLongitude, String latlon ,int p_zip, int cl, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("gpsLatitude", gpsLatitude);
			bundle.put("gpsLongitude", gpsLongitude);
			bundle.put("latlon", latlon);
			bundle.put("p_zip", p_zip);
			bundle.put("cl", cl);
			
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/place/locate");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 定位
	 * TODO 暂不能使用
	 * 
	 * <P><b>gpsLatitude、gpsLongitude或latlon必须存在一个，不能都不存在</b></P>
	 * 
	 * @param gpsLatitude  	gps的纬度
	 * @param gpsLongitude 	gps的经度
	 * @param latlon 		json化的经纬度，若有此值gpsLatitude，gpsLongitude无效
	 * @param p_zip 		如果传输的latlon参数为gzip压缩后再经过Base64编码的数据，那么值为1，如果不是则为0。缺省值为0
	 * @param cl			如果传输的latlon参数为gzip压缩后再经过AES加密，最后Base64编码的数据，那么值为1。如果不是则为0。缺省值为0
	 *
	 */
	public void getNeighbors(long gpsLatitude, long gpsLongitude, String latlon ,int offset, int limit, int p_zip, int cl, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("gpsLatitude", gpsLatitude);
			bundle.put("gpsLongitude", gpsLongitude);
			bundle.put("latlon", latlon);
			bundle.put("offset", offset);
			bundle.put("limit", limit);
			bundle.put("p_zip", p_zip);
			bundle.put("cl", cl);
			
			bundle.put("sig", getSigByJSON(bundle, false));
			INetRequest request = obtainINetRequest("/place/neighbors");
			request.setData(bundle);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传Cover
	 * TODO
	 * */
	public void uploadCover(byte[] data , double yScale, String special, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		INetRequest request = obtainINetRequest("/file/cover");
		try {
			bundle.put("data", buildUploadCoverData(data, yScale, special));
			request.setData(bundle);
			request.setSecretKey(LoginManager.getInstance().getSecretKey());
			request.setType(INetRequest.TYPE_HTTP_POST_IMG);
			request.setResponse(response);
			HttpProviderWrapper.getInstance().addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 登录接口，暂时使用私信国内版中人人官方登录接口
	 * 
	 * @author 宁长胜
	 * @param accountType
	 * @param account
	 * @param passwordMd5
	 * @param session
	 * @param captcha_needed
	 * @param captcha
	 * @param response
	 */
	public void login(int accountType, String account, String passwordMd5,
			long session, int captcha_needed, String captcha,
			INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		// 使用 3G 服务器的 api_key
		try {
			// bundle.put("is_fake", false);// 定义是否采用本地模拟返回的假数据

			bundle.put("api_key", AbstractHttpMcsService.MCS_API_KEY);
			//bundle.put("call_id", System.currentTimeMillis());
			bundle.put("client_info", getClientInfo());
			bundle.put("user", account);
			bundle.put("password", passwordMd5);
			bundle.put("captcha_needed", captcha_needed);
			bundle.put("captcha", captcha);
			//bundle.put("uniq_id", tm.getDeviceId());
			bundle.remove("session_key");
			bundle.put("sig", getSigByJSON(bundle, true));
			INetRequest request = obtainINetRequest(accountType == LoginManager.LOGIN_RENREN ? "/user/login/renren"
					: "/user/login");
			request.setData(bundle);
			request.setResponse(response);
			request.setSecretKey(AbstractHttpMcsService.LOCAL_SECRET_KEY);
			request.setCurrentSession(session);
			HttpProviderWrapper.getInstance().addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取网络图片
	 * 
	 * @param url 图片url
	 * @param response
	 */
	public void getImage(String url, INetResponse response) {
		INetRequest request = obtainINetRequest(url,
				INetRequest.TYPE_HTTP_GET_IMG, response);
		request.setPriority(INetRequest.PRIORITY_LOW_PRIORITY);
		request.setSecretKey(LoginManager.getInstance().getSecretKey());
		HttpProviderWrapper.getInstance().addRequest(request);
	}

	/**
	 * 用来定位的接口以后都会先用从系统得到的数据调用这个接口定位，其他位置接口都不再使用latlon，直接用这个接口返回的数据就好了
	 * 
	 * @param page          申请poilist第几页
	 * @param latitude      纬度，这里一般会传无用纬度
	 * @param longitude     经度，这里一般会传无用经度
	 * @param need2deflect  0不需要偏移，1需要偏移
	 * @param latlon     	定位用到的一个json数据串，这里面包括gps的经纬度，network的经纬度个一组，以及基站信息周边基站信息，wifi信息
	 						，以及周边wifi信息
	 * @param response		回调类
	 * @param radius		判断附近的半径
	 * @param pageSize		每一页的显示条数
	 * @param batchRun		是否是批处理
	 * @return 				如果是批处理返回这个请求，如果不是批处理返回null直接将这个请求发出
	 * 
	 */
	public INetRequest getLocationInfo(long page, long latitude,
			long longitude, int need2deflect, JSONObject latlon,
			INetResponse response, int radius, int pageSize, boolean batchRun) {

		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("v", "1.0");
			bundle.put("format", "json");
			bundle.put("page", page);
			bundle.put("page_size", pageSize);
			bundle.put("radius", radius);
			if (!batchRun) {
				bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			} else {
				bundle.put("method", "place.preLocate");
			}
			bundle.put("lat_gps", latitude);
			bundle.put("lon_gps", longitude);
			bundle.put("d", need2deflect);
			if (latlon != null && latlon.length() > 0) {
				bundle.put("latlon", latlon.toString());
				bundle.put("request_time", System.currentTimeMillis());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		INetRequest request = obtainINetRequest("/place/preLocate");
		request.setData(bundle);
		request.setResponse(response);
		if (batchRun) {
			return request;
		} else {
			HttpProviderWrapper.getInstance().addRequest(request);
			return null;
		}
	}

	/**
	 * 获取附近的好友列表
	 * **/
	public INetRequest getNearbyFriendsList(JSONObject latlon, long lonGps,
			long latGps, int need2deflect, int page, int pageSize,
			int excludeList, String orderType, String dataType,
			INetResponse response, boolean batchRun) {
		JSONObject bundle = obtainBaseRequestBundle(false);

		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("method", "place.friendList");
			bundle.put("v", "1.1");
			bundle.put("format", "json");
			bundle.put("order_type", orderType);
			bundle.put("data_type", dataType);
			bundle.put("page", page);
			bundle.put("page_size", pageSize);
			bundle.put("exclude_list", excludeList);
			if (!batchRun) {
				bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);
			}
			if (dataType.equals("near")) {
				bundle.put("lat_gps", latGps);
				bundle.put("lon_gps", lonGps);
				bundle.put("d", need2deflect);
				if (latlon != null && latlon.length() > 0) {
					bundle.put("latlon", latlon.toString());
					bundle.put("request_time", System.currentTimeMillis());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		INetRequest request = obtainINetRequest("/place/friendList");
		request.setData(bundle);
		request.setResponse(response);
		if (batchRun) {
			return request;
		} else {
			HttpProviderWrapper.getInstance().addRequest(request);
			return null;
		}
	}

	public void getLoginInfo(INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("v", "1.0");
			bundle.put("format", "JSON");
			bundle.put(INetRequest.gzip_key, INetRequest.gzip_value);

			sendRequest("/user/login", bundle, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取联系人列表详细信息
	 */
	public INetRequest getThirdFriendsInfo(INetResponse response,
			String siXinid, String externalAccountType, String thirdId,
			int limit, int offset) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("v", "1.0");
			bundle.put("profile_type", 1023);
			bundle.put("action", "GET");
			if (TextUtils.isEmpty(siXinid)) { // 用第三方ID
				sendRequest("/friends/" + externalAccountType + "/" + thirdId,
						bundle, response);
			} else {// 用私信id
				bundle.put("id_type", 1);
				sendRequest("/friends/" + externalAccountType + "/" + siXinid,
						bundle, response);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * 获取个人信息列表接口
	 */
	public void getUserInfoById(INetResponse response, String user_id) {
		JSONObject bundle = obtainBaseRequestBundle(false);

		try {
			bundle.put("is_fake", true);
			bundle.put("v", "1.0");
			bundle.put("action", "GET");// 值设置为GET
			sendRequest("/contact/getUserInfoById", bundle, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取个人feed信息列表接口
	 */
	public void getProfileFeedById(INetResponse response, String mUserId) {
		JSONObject bundle = obtainBaseRequestBundle(false);

		try {
			bundle.put("is_fake", true);
			bundle.put("v", "1.0");
			bundle.put("action", "GET");// 值设置为GET
			sendRequest("/feed/feedsById", bundle, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取联系人列表接口
	 */
	public INetRequest getContactList(INetResponse response, int page,
			int page_size, boolean batchRun) {
		JSONObject bundle = obtainBaseRequestBundle(false);

		try {
			bundle.put("v", "1.0");
			bundle.put("limit", page_size);
			bundle.put("offset", 0);// 偏移量
			bundle.put("profile_type", "1023");// 想要得到的个人资料标识
			bundle.put("action", "GET");// 值设置为GET
			sendRequest("/contact/contactlist/contacts_talkgroups", bundle,
					response);
		} catch (JSONException e) {
			e.printStackTrace();
		} // 每页最多纪录数

		return null;
	}

	/**
	 * 获取全部联系人（包含群组）列表接口
	 */
	public INetRequest getContactListIncludeGroup(INetResponse response,
			int page, int page_size, boolean batchRun) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("v", "1.0");
			bundle.put("limit", page_size); // 每页最多纪录数
			bundle.put("offset", 0);// 偏移量
			bundle.put("profile_type", "1023");// 想要得到的个人资料标识
			bundle.put("action", "GET");// 值设置为GET
			sendRequest("/contact/contactlist/contacts_talkgroups", bundle,
					response);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public INetRequest logout(INetResponse response, String sessionId) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("session_id", sessionId);
			sendRequest("/user/logout", bundle, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 上传图片
	 * */
	public void uploadPhoto(INetResponse response, byte[] data) {
		INetRequest request = getImageUploadRequest(null, data);
		request.setResponse(response);
		HttpProviderWrapper.getInstance().addRequest(request);
	}

	public INetRequest getImageUploadRequest(INetRequest request, byte[] data) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		request = obtainINetRequest("/file/photo");
		try {
			bundle.put("data", buildUploadPhotoData(data));
			request.setData(bundle);
			request.setSecretKey(LoginManager.getInstance().getSecretKey());
			request.setType(INetRequest.TYPE_HTTP_POST_IMG);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * 下载语音
	 */
	public void getVoice(String url, INetResponse response) {
		INetRequest request = obtainINetRequest(url,
				INetRequest.TYPE_HTTP_GET_VOICE, response);
		request.setSecretKey(LoginManager.getInstance().getSecretKey());
		HttpProviderWrapper.getInstance().addRequest(request);
	}
	
	/**
	 * 上传头像
	 * */
	public void uploadHeadPhoto(INetResponse response, int x, int y, int width,
			int height, byte[] data) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		INetRequest request = obtainINetRequest("/file/profile_picture");
		try {
			bundle.put("data", buildUploadHeadData(data, x, y, width, height));
			bundle.put("is_fake", false);
			request.setData(bundle);
			request.setResponse(response);
			request.setSecretKey(LoginManager.getInstance().getSecretKey());
			request.setType(INetRequest.TYPE_HTTP_POST_IMG);
			HttpProviderWrapper.getInstance().addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送语音
	 */
	public void postVoice(long toId, String vid, int seqid, String mode,
			int playTime, byte[] voiceData, INetResponse response) {
		INetRequest request = getVoiceUploadRequest(null, toId, vid, seqid,
				mode, playTime, voiceData);
		request.setResponse(response);
		HttpProviderWrapper.getInstance().addRequest(request);

	}

	public INetRequest getVoiceUploadRequest(INetRequest request, long toId,
			String vid, int seqid, String mode, int playTime, byte[] voiceData) {
		JSONObject bundle = AbstractHttpMcsService
				.obtainBaseRequestBundle(false);
		request = obtainINetRequest("/file/common_file");
		try {
			bundle.put("is_fake", false);
			bundle.put("length", playTime);
			bundle.put("data", buildUploadAudioData(toId, vid, seqid, mode, playTime, voiceData));
			request.setData(bundle);

			request.setSecretKey(LoginManager.getInstance().getSecretKey());
			request.setType(INetRequest.TYPE_HTTP_POST_BIN_File);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * 发送图片
	 * */
	public void sendPhoto(byte[] imgData, long toId, INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		INetRequest request = obtainINetRequest("/file/photo");
		try {
			bundle.put("data", buildUploadPhotoData(toId, imgData));
			request.setData(bundle);
			bundle.put("is_fake", false);
			request.setResponse(response);
			request.setSecretKey(LoginManager.getInstance().getSecretKey());
			request.setType(INetRequest.TYPE_HTTP_POST_IMG);
			HttpProviderWrapper.getInstance().addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 发布帖子的接口
	 * 
	 * @param resource
	 *            资源
	 * @param content_type
	 *            帖子类型
	 * @param content
	 *            帖子内容
	 * @param client_time
	 *            产生操作的当前时间
	 * 
	 * @author tian.wang
	 * **/
	public void publishPosts(INetResponse response, String resource,
			int content_type, String content, long client_time) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("content_type", content_type).put("content", content)
					.put("client_time", client_time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.sendRequest("/1.0/" + resource + "/posts", bundle, response);
	}

	/**
	 * 删除帖子的接口
	 * 
	 * @param resource 		资源
	 * @param feedId   		帖子所依附的feedid
	 * @param client_time	产生操作的当前时间
	 * @author tian.wang
	 */
	public void deletePosts(INetResponse response, String resource,
			String feedId, long client_time) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("action", "DELETE").put("client_time", client_time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.sendRequest("/1.0/" + resource + "/feeds/" + feedId + "/post",
				bundle, response);
	}


	/**
	 * 对一个帖子增加评论的接口
	 * 
	 * @param resource
	 *            资源
	 * @param feedId
	 *            帖子所依附的feedid
	 * @param content_type
	 *            评论类型
	 * @param content
	 *            评论内容
	 * @param client_time
	 *            产生操作的当前时间
	 * @author tian.wang
	 * **/
	public void AddPostsComment(INetResponse response, String resource,
			String feedId, int content_type, String content, long client_time) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("content_type", content_type).put("content", content)
					.put("client_time", client_time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.sendRequest("/1.0/" + resource + "/feeds/" + feedId
				+ "/post/comments", bundle, response);
	}

	/**
	 * 对一个帖子赞的接口
	 * 
	 * @param resource
	 *            资源
	 * @param feedId
	 *            帖子所依附的feedid
	 * @param content_type
	 *            评论类型
	 * @param content
	 *            评论内容
	 * @param client_time
	 *            产生操作的当前时间
	 * @author tian.wang
	 * **/
	public void AddPostsLike(INetResponse response, String resource,
			String feedId, int content_type, String content, long client_time) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("content_type", content_type).put("content", content)
					.put("client_time", client_time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.sendRequest("/1.0/" + resource + "/feeds/" + feedId
				+ "/post/likes", bundle, response);
	}

	/**
	 * 获取某个帖子下的评论列表的接口
	 * 
	 * @param resource
	 *            资源
	 * @param feedId
	 *            帖子所依附的feedid
	 * @param field
	 *            (非必须)
	 * @param offset
	 *            (非必须)
	 * @param limits
	 *            (非必须)
	 * @param after_id
	 *            (非必须)
	 * @param before_id
	 *            (非必须)
	 * @param client_time
	 *            产生操作的当前时间
	 * @author tian.wang
	 * **/
	public void getPostsCommentsList(INetResponse response, String resource,
			String feedId, String field, int offset, int limits,
			String after_id, String before_id, long client_time) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("action", "GET").put("offset", 0).put("limits", 10)
					.put("client_time", client_time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.sendRequest("/feed/Comments", bundle, response);
		// this.sendRequest("/1.0/"+resource+"/feeds/"+feedId +"/post/comments",
		// bundle, response);
	}

	/**
	 * 获取单条feed的接口
	 * 
	 * @param resource
	 *            资源
	 * @param feedId
	 *            帖子所依附的feedid
	 * @param client_time
	 *            产生操作的当前时间
	 * @author tian.wang
	 * **/
	public void getFeed(INetResponse response, String resource, String feedId,
			long client_time) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("action", "GET").put("client_time", client_time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.sendRequest("/1.0/" + resource + "/feeds/" + feedId, bundle,
				response);
	}

	/**
	 * 获取feed列表的接口
	 * 
	 * @param resource
	 *            资源
	 * @param feedId
	 *            帖子所依附的feedid
	 * @param field
	 *            (非必须)
	 * @param offset
	 *            (非必须)
	 * @param limits
	 *            (非必须)
	 * @param after_id
	 *            (非必须)
	 * @param before_id
	 *            (非必须)
	 * @param client_time
	 *            产生操作的当前时间
	 * @author tian.wang
	 * **/
	public void getFeedsList(INetResponse response, String resource,
			String feedId, String field, int offset, int limits,
			String after_id, String before_id, long client_time) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true); // 定义是否采用本地模拟返回的假数据
			bundle.put("action", "GET").put("offset", 0).put("limits", 10)
					.put("client_time", client_time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.sendRequest("/1.0/" + resource + "/feeds/", bundle, response);
	}

	/**
	 * 获取feed列表
	 * @param page      第几页
	 * @param page_size 页数量
	 * @param types     类型
	 * @param response
	 * @author jia.xia
	 */
	public void getFeedList(int page, int page_size, String types,
			INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true);
			bundle.put("v", "1.0");
			bundle.put("call_id", System.currentTimeMillis());
			bundle.put("action", "GET").put("page", page);
			bundle.put("page_size", page_size).put("focus", 1);
			bundle.put("type", "502,701,709");
			sendRequest("/feed/feeds", bundle, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	/**获取校园10大Feed
	 * @author jia.xia
	 * @param response
	 * 测试
	 */
	public void getTop10FeedsList(INetResponse response){
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true);
			bundle.put("v", "1.0");
			bundle.put("call_id", System.currentTimeMillis());
			sendRequest("/feed/feedsrefresh", bundle, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取消息列表 
	 * 
	 * 本地测试用
	 */
	public void getNewsList(INetResponse response) {
		JSONObject bundle = obtainBaseRequestBundle(false);
		try {
			bundle.put("is_fake", true);
			sendRequest("/feed/getNewsList", bundle, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
}

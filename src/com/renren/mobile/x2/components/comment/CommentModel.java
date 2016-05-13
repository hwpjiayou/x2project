package com.renren.mobile.x2.components.comment;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
/**
 * 
 * @author hwp
 *  
 */
public abstract class CommentModel implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 541038143540542553L;

	//回复信息。	

	public static final String COMMENT_COLUMN_COMMENT = "comment_list";

	public static final String COMMENT_COLUMN_COMMENT_CONTENT = "content";

	public static final String COMMENT_COLUMN_COMMENT_ID = "id";

	public static final String COMMENT_COLUMN_COMMENT_HEAD_URL = "head_url";

	public static final String COMMENT_COLUMN_COMMENT_USER_NAME = "user_name";


	public static final String COMMENT_COLUMN_COMMET_TIME = "time";


	public static final String COMMENT_COLUMN_COMMENT_USER_ID = "user_id";
	
	//附件的信息，如图片，语音，表情等
	public static final String COMMENT_COLUM_COMMENT_CONTENT_IMG_URL="content_img_url";
	
	public static final String COMMENT_COLUM_COMMENT_CONTENT_TEXT="content_text";
	
	public static final String COMMENT_COLUM_COMMENT_CONTENT_VOICE="voice";
	
	
	public static final String COMMENT_COLUMN_MEDIA_ID = "media_id";

	public static final String COMMENT_COLUMN_PHOTO_URL = "url";

	public static final String COMMENT_COLUMN_PHOTO_MAIN_URL = "main_url";

	public static final String COMMENT_COLUMN_PHOTO_LARGE_URL = "large_url";

	public static final String COMMENT_COLUMN_PHOTO_DIGEST = "digest";

	public static final String COMMENT_COLUMN_STATUS_FORWARD = "status_forward";
	
	
	
//	public static final int COMMENT_COLUMN_COMMENT_TYPE=1;
    //个人回复

	
	public static final String COMMENT_COLUMN_AUTHOR_ID="author";
	
	public static final String COMMENT_COLUMN_OWNER_NAME = "owner_name";

	public static final String COMMENT_COLUMN_OWNER_ID = "owner_id";
	
	public static final String COMMENT_COLUMN_ID="id";

	public static final String COMMENT_COLUMN_PLACE = "place";

	public static final String COMMENT_COLUMN_PLACE_NAME = "pname";

	public static final String COMMENT_COLUMN_PLACE_ID = "id";

	public static final String COMMENT_COLUMN_PLACE_PID = "pid";

	public static final String COMMENT_COLUMN_PLACE_LONGITUDE = "longitude";

	public static final String COMMENT_COLUMN_PLACE_LATITUDE = "latitude";


	public static final String COMMENT_COLUMN_OWNER_HEADURL = "head_url";



	public static final String COMMENT_COLUMN_OWNER_USERNAME = "user_name";

	public static final String COMMENT_COLUMN_OWNER_TIME = "time";

	public static final String COMMENT_COLUMN_OWNER_USERID = "user_id";


	protected boolean mIsReblog = false;

	protected boolean mHasLocation = false;

	private long mId; // 评论id

	private String mHeadUrl; // 头像url

	private String mUserName; // 用户姓名

	private String mContent; // 内容

	private long mTime; // 时间

	private String mUserId; // 评论用户的id

	private String mContentImageUrl;  //评论中带有图片
	
	private String mContentText;  //评论的文本信息。
	
	private String mContentVoice;  //评论中带有声音。
//	private int mType; // 评论类型

	//回复信息中用到的属性
	private long authorId; // 源状态拥有者id

	private long mediaId = 0; // 媒体内容的id

	private String type; // 类型

	private String url; // 缩略图url

	private String mainUrl; // 原图url

	private String largeUrl; //大图url

	private String digest; // 描述
	
	private String stauts_forward; //转发

	protected CommetLocationContent mCommentLocationContent;

	protected ArrayList<CommentContent> mCommentContent;

	public CommentModel(JSONObject objs) {
		
		try {
			this.mId = objs.getLong(COMMENT_COLUMN_COMMENT_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.mHeadUrl = objs.getString(COMMENT_COLUMN_COMMENT_HEAD_URL);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.mUserName = objs.getString(COMMENT_COLUMN_COMMENT_USER_NAME);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.mContent = objs.getString(COMMENT_COLUMN_COMMENT_CONTENT);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			this.mTime = objs.getLong(COMMENT_COLUMN_COMMET_TIME);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.mUserId = objs.getString(COMMENT_COLUMN_COMMENT_USER_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.mContentImageUrl = objs.getString(COMMENT_COLUM_COMMENT_CONTENT_IMG_URL);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.mContentText = objs.getString(COMMENT_COLUM_COMMENT_CONTENT_TEXT);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.mContentVoice = objs.getString(COMMENT_COLUM_COMMENT_CONTENT_VOICE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.authorId = objs.getLong(COMMENT_COLUMN_AUTHOR_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.mediaId = objs.getLong(COMMENT_COLUMN_MEDIA_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.url = objs.getString(COMMENT_COLUMN_PHOTO_URL);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.mainUrl = objs.getString(COMMENT_COLUMN_PHOTO_MAIN_URL);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.largeUrl = objs.getString(COMMENT_COLUMN_PHOTO_LARGE_URL);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.digest = objs.getString(COMMENT_COLUMN_PHOTO_DIGEST);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.stauts_forward = objs.getString(COMMENT_COLUMN_STATUS_FORWARD);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initLocationAndComment(objs);
	}

	public void initLocationAndComment(JSONObject objs)  {
		JSONObject objs_location = null;
		try {
			objs_location = objs.getJSONObject(COMMENT_COLUMN_PLACE);
			if (objs_location != null) {
				mHasLocation = true;
				mCommentLocationContent = new CommetLocationContent(
						objs_location);
			}
			JSONArray array_comment = objs.getJSONArray(COMMENT_COLUMN_COMMENT);
			Log.v("--hwp--", "json "+array_comment.length());
			if (array_comment != null) {
				mCommentContent = new ArrayList<CommentContent>();
				JSONObject o = null;
				for (int i = 0;i<array_comment.length();i++) {
					o = array_comment.getJSONObject(i);
					CommentContent c = new CommentContent(o);
					if (c != null) {
						mCommentContent.add(c);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public String toString() {
		return "id =" + mId + ";" + "head_url =" + mHeadUrl + ";" + "user_name ="
				+ mUserName + ";" + "content =" + mContent + ";" + "time =" + mTime
				+ ";"+ "user_id =" + mUserId + ";"+"Content_image_url ="+mContentImageUrl+";"
				+"content_text ="+mContentText+";"+"voice ="+mContentVoice+";";
	}

	
//	public abstract ArrayList<Object> getCommentAttachmentContent();

	public CommetLocationContent getCommentLocation() {
		return mCommentLocationContent;
	}

	public ArrayList<CommentContent> getCommentContent() {
		return mCommentContent;
	}

	public boolean ismIsReblog() {
		return mIsReblog;
	}

	public void setmIsReblog(boolean mIsReblog) {
		this.mIsReblog = mIsReblog;
	}

	public boolean ismHasLocation() {
		return mHasLocation;
	}

	public void setmHasLocation(boolean mHasLocation) {
		this.mHasLocation = mHasLocation;
	}

	public long getmId() {
		return mId;
	}

	public void setmId(long mId) {
		this.mId = mId;
	}

	public String getmHeadUrl() {
		return mHeadUrl;
	}

	public void setmHeadUrl(String mHeadUrl) {
		this.mHeadUrl = mHeadUrl;
	}

	public String getmUserName() {
		return mUserName;
	}

	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getmContent() {
		return mContent;
	}

	public void setmContent(String mContent) {
		this.mContent = mContent;
	}

	public long getmTime() {
		return mTime;
	}

	public void setmTime(long mTime) {
		this.mTime = mTime;
	}

	public String getmUserId() {
		return mUserId;
	}

	public void setmUserId(String mUserId) {
		this.mUserId = mUserId;
	}

	public String getmContentImageUrl() {
		return mContentImageUrl;
	}

	public void setmContentImageUrl(String mContentImageUrl) {
		this.mContentImageUrl = mContentImageUrl;
	}

	public String getmContentText() {
		return mContentText;
	}

	public void setmContentText(String mContentText) {
		this.mContentText = mContentText;
	}

	public long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}

	public long getMediaId() {
		return mediaId;
	}

	public void setMediaId(long mediaId) {
		this.mediaId = mediaId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMainUrl() {
		return mainUrl;
	}

	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}

	public String getLargeUrl() {
		return largeUrl;
	}

	public void setLargeUrl(String largeUrl) {
		this.largeUrl = largeUrl;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getStauts_forward() {
		return stauts_forward;
	}

	public void setStauts_forward(String stauts_forward) {
		this.stauts_forward = stauts_forward;
	}

	
	
	public String getmContentVoice() {
		return mContentVoice;
	}

	public void setmContentVoice(String mContentVoice) {
		this.mContentVoice = mContentVoice;
	}

	public CommetLocationContent getmCommentLocationContent() {
		return mCommentLocationContent;
	}

	public void setmCommentLocationContent(
			CommetLocationContent mCommentLocationContent) {
		this.mCommentLocationContent = mCommentLocationContent;
	}

	public ArrayList<CommentContent> getmCommentContent() {
		return mCommentContent;
	}

	public void setmCommentContent(ArrayList<CommentContent> mCommentContent) {
		this.mCommentContent = mCommentContent;
	}

	

}

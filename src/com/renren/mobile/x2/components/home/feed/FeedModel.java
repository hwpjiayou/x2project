package com.renren.mobile.x2.components.home.feed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.renren.mobile.x2.network.mas.UGCContentModel;

public class FeedModel implements Serializable,Cloneable{

	private static final long serialVersionUID = -8817804798472844549L;

	public static final String FEED_COLUMN_ID = "feed_id";

	public static final String FEED_COLUMN_HEAD_URL = "header_url";
	
	public static final String FEED_COLUMN_SMALL_HEADER = "small_header";

	public static final String FEED_COLUMN_NORMAL_HEADER = "normal_header";
	
	public static final String FEED_COLUMN_USER = "user";
	
	public static final String FEED_COLUMN_USER_NAME = "user_name";

	public static final String FEED_COLUMN_TITLE = "title";

	public static final String FEED_COLUMN_TIME = "created_time";

	public static final String FEED_COLUMN_SOURCE_ID = "source_id";

	public static final String FEED_COLUMN_PREFIX = "prefix";

	public static final String FEED_COLUMN_USER_ID = "user_id";

	public static final String FEED_COLUMN_TYPE = "type";

	public static final String FEED_COLUMN_OWNER_ID = "owner_id";

	public static final String FEED_COLUMN_MEDIA_ID = "media_id";

	public static final String FEED_COLUMN_PHOTO_URL = "url";

	public static final String FEED_COLUMN_PHOTO_MAIN_URL = "main_url";

	public static final String FEED_COLUMN_PHOTO_LARGE_URL = "large_url";

	public static final String FEED_COLUMN_PHOTO_DIGEST = "digest";

	public static final String FEED_COLUMN_STATUS_FORWARD = "status_forward";

	public static final String FEED_COLUMN_OWNER_NAME = "owner_name";

	public static final String FEED_COLUMN_REBLOG_OWNER_ID = "owner_id";

	public static final String FEED_COLUMN_REBLOGID = "id";

	public static final String FEED_COLUMN_STATUS = "status";

	public static final String FEED_COLUMN_PLACE = "place";

	public static final String FEED_COLUMN_PLACE_NAME = "pname";

	public static final String FEED_COLUMN_PLACE_ID = "id";

	public static final String FEED_COLUMN_PLACE_PID = "pid";

	public static final String FEED_COLUMN_PLACE_LONGITUDE = "longitude";

	public static final String FEED_COLUMN_PLACE_LATITUDE = "latitude";

	public static final String FEED_COLUMN_COMMENT = "comments";

	public static final String FEED_COLUMN_COMMENT_CONTENT = "content";

	public static final String FEED_COLUMN_COMMENT_HEADURL = "head_url";

	public static final String FEED_COLUMN_COMMENT_ID = "id";

	public static final String FEED_COLUMN_COMMENT_USERNAME = "user_name";

	public static final String FEED_COLUMN_COMMENT_TIME = "time";

	public static final String FEED_COLUMN_COMMENT_USERID = "user_id";

	public static final String FEED_COLUMN_COMMNET_COUNT = "total_count";
	
	public static final String FEED_COLUMN_LIKE_COUNT = "like_count";
	
	public static final String FEED_COLUMN_POST = "post";
	
	protected boolean mIsReblog = false;

	protected boolean mHasLocation = false;

	private long mFeedId; // 新鲜事id
	
	private int mCommentCount;//评论数量

	private int mLikeCount; // 赞数量
	
	private boolean mIsLike = false; // 喜欢的状态  ：true  :喜欢                      false: 不喜欢
	
	private int mPosition ;       //记录此model所在的position。

	protected FeedLocationContent mChatFeedLocationContent;
	
	protected UGCContentModel mPostContentModel ;    //post列表（因为可能是混合类型）

	protected List<FeedCommentContent> mChatFeedCommentContent;

	protected Long mCreateTime;
	
	
	@Override
	protected Object clone() {
		// TODO Auto-generated method stub
		Object o = null;
		try {
			o =  super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	
	public FeedModel(JSONObject objs)  {
		if(objs == null)
			return ;
		this.mFeedId = objs.optLong(FEED_COLUMN_ID);
		this.mCreateTime = objs.optLong(FEED_COLUMN_TIME);
		this.mCommentCount = (int)objs.optInt(FEED_COLUMN_COMMNET_COUNT);
		this.mLikeCount = objs.optInt(FEED_COLUMN_LIKE_COUNT);
		initPost(objs);
		initComment(objs);
	}
/**
 * 初始化Posts
 * @param objs
 */
	public void initPost(JSONObject objs) {
		JSONObject postsObject = null;
		postsObject = objs.optJSONObject(FEED_COLUMN_POST);
		
		if(postsObject != null){
			//TODO
			this.mPostContentModel =new UGCContentModel(postsObject);
		}
	}
	/**
	 * 初始化评论列表
	 * @param objs
	 */
	public void initComment(JSONObject objs){
		JSONArray array_comment = null;
		array_comment = objs.optJSONArray(FEED_COLUMN_COMMENT);
		if (array_comment != null) {
			mChatFeedCommentContent = new ArrayList<FeedCommentContent>();
			JSONObject o = null;
			for (int i = 0;i<array_comment.length();i++) {
				o = array_comment.optJSONObject(i);
				FeedCommentContent c = new FeedCommentContent(o);
				if (c != null) {
					mChatFeedCommentContent.add(c);
				}
			}
		}
	}
/**
 * 初始化位置和Comment
 * @param objs
 */
	public void initLocationAndComment(JSONObject objs)  {
		JSONObject objs_location = null;
			objs_location = objs.optJSONObject(FEED_COLUMN_PLACE);
			if (objs_location != null) {
				mHasLocation = true;
				mChatFeedLocationContent = new FeedLocationContent(
						objs_location);
			}
			JSONArray array_comment = null;
			array_comment = objs.optJSONArray(FEED_COLUMN_COMMENT);
			if (array_comment != null) {
				mChatFeedCommentContent = new ArrayList<FeedCommentContent>();
				JSONObject o = null;
				for (int i = 0;i<array_comment.length();i++) {
					o = array_comment.optJSONObject(i);
					FeedCommentContent c = new FeedCommentContent(o);
					if (c != null) {
						mChatFeedCommentContent.add(c);
					}
				}
			}
		
		
	}

	public void addCommentCount(int num){
		this.mCommentCount +=num;
	}
	
	
	public int getmPosition() {
	return mPosition;
}

public void setmPosition(int mPosition) {
	this.mPosition = mPosition;
}

	public boolean ismIsLike() {
		return mIsLike;
	}

	public void setmIsLike(boolean mIsLike) {
		this.mIsLike = mIsLike;
}

	public long getFeedId(){
		return this.mFeedId;
	}

	public void setFeedId(long id) {
		this.mFeedId = id;
	}

	public void setmIsReblog(boolean mIsReblog) {
		this.mIsReblog = mIsReblog;
	}

	public boolean isHasLocation() {
		return mHasLocation;
	}
	
	public int getmCommentCount() {
		return mCommentCount;
	}

	public void setmCommentCount(int mCommentCount) {
		this.mCommentCount = mCommentCount;
	}

	public FeedLocationContent getChatFeedLocation() {
		return mChatFeedLocationContent;
	}

	public List<FeedCommentContent> getmChatFeedCommentContent() {
		return mChatFeedCommentContent;
	}

	public void setmChatFeedCommentContent(
			List<FeedCommentContent> mChatFeedCommentContent) {
		this.mChatFeedCommentContent = mChatFeedCommentContent;
	}

	public int getmLikeCount() {
		return mLikeCount;
	}

	public void setmLikeCount(int mLikeCount) {
		this.mLikeCount = mLikeCount;
	}
	public UGCContentModel getmPostContentModel() {
		return mPostContentModel;
	}
	public void setmPostContentModel(UGCContentModel mPostContentModel) {
		this.mPostContentModel = mPostContentModel;
	}
}

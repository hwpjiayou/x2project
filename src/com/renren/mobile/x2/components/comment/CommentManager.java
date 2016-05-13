package com.renren.mobile.x2.components.comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.mas.UGCContentModel;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author hwp 评论控制器类，只要发送，请求数据。
 */
public class CommentManager {

	public static List<UGCContentModel> mComList = new ArrayList<UGCContentModel>();
	public CommentFragment mCommentView;
	private static CommentAdapter mAdapter;

	public static int COMMENT_CURRENT_PAGE = 1;
	public static final int COMMENt_DEFAULT_PAGE = 1;
	public static final int COMMENT_MAX_PAGE_SIZE = 10;

	private boolean flag = false;

	public CommentManager(CommentFragment mCommentView) {
		this.mCommentView = mCommentView;

	}

	public void getCommentListFromNet(boolean isRefresh,int page){
		//判断当前网络状况是否良好。
		
		LoadComment(isRefresh, 0);
	}


	public View getView() {
		mCommentView = new CommentFragment();
		return mCommentView.getRootView();
	}

	public void finishCommentsData() {
		if (getView() != null) {
			// mCommentView.setCommentAdapter(mComList);
		}
	}

	public void LoadComment(final boolean isRefresh, final int pageNum) {
		INetResponse response = new INetResponse() {
			@Override
			public void response(INetRequest req, JSONObject obj) {
				if (null != obj) {
					if (Methods.checkNoError(req, obj)) {
						try {
							int count=obj.getInt("count");
							int total_count=obj.getInt("total_count");
							if (count== 0) {
								mCommentView.setEmptyView();
							} else {
								Log.v("--hwp--","json count "+count);
								Log.v("--hwp--","json total_count"+obj.getInt("total_count"));
								JSONArray array = obj.getJSONArray("comments");
								Log.v("--hwp--","json "+array);
								if (array != null) {
									mCommentView.reset(array, isRefresh,count,total_count);
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		if (isRefresh) {
			COMMENT_CURRENT_PAGE = COMMENt_DEFAULT_PAGE;
		} else {
			COMMENT_CURRENT_PAGE++;
		}

		if (pageNum != 0) {

//			 HttpMasService.getInstance().getPostsCommentsList(response,
//			 null,null, null, pageNum, COMMENT_MAX_PAGE_SIZE, null, null, 1L);
			
			String feedId = mCommentView.mFeedDataModel.getFeedId() + "";
			String ugcId=mCommentView.model.id;
			String schoolId=mCommentView.mLoginInfo.mSchool_id;
			HttpMasService.getInstance().getPostComments(schoolId, feedId, 10,
					ugcId, null, response);
			
		} else {
			String feedId = mCommentView.mFeedDataModel.getFeedId() + "";
			String schoolId=mCommentView.mLoginInfo.mSchool_id;
			HttpMasService.getInstance().getPostComments(schoolId, feedId, 10,
					null, null, response);
//			 HttpMasService.getInstance().getPostsCommentsList(response,
//			 null,null, null, COMMENT_CURRENT_PAGE, COMMENT_MAX_PAGE_SIZE,
//			 null, null, 1L);
		}
	}

}

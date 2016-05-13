package com.renren.mobile.x2.components.comment;

import java.util.List;

import org.json.JSONException;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.components.home.feed.FeedAdapter;
import com.renren.mobile.x2.components.home.feed.FeedModel;
import com.renren.mobile.x2.network.mas.UGCContentModel;
import com.renren.mobile.x2.utils.log.Logger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
/**
 * 评论的activity
 * @author hwp
 * @time 2012.10.12
 *  */

public class CommentActivity extends BaseFragmentActivity<List<UGCContentModel>>{

	private View mRootView;
	private CommentFragment mCommentView;
	private FeedModel feedDataModel;
    private int layouPassFromFeed;
    private Bundle mFeedBundle;
	@Override
	protected BaseFragment<List<UGCContentModel>> onCreateContentFragment() {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		mFeedBundle=intent.getExtras();
		//得到feed传递过来的数据，为以后需要用到做备份。
		feedDataModel=(FeedModel) mFeedBundle.getSerializable("feedmodel");
		layouPassFromFeed=mFeedBundle.getInt("feed_layout_id");

		mCommentView= new CommentFragment();
		mCommentView.setArguments(mFeedBundle);
		
		return mCommentView;
	}

    
	//加载数据之前的回调,可做预处理,位于UI线程之中.
	@Override
	protected void onPreLoad() {
		// TODO Auto-generated method stub
	//	Log.v("--hwp--", "activity onPreLoad");
	}


	@Override
	protected void onFinishLoad(List<UGCContentModel> data) {
		// TODO Auto-generated method stub

	}


	@Override
	protected List<UGCContentModel> onLoadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void onDestroyData() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 switch (keyCode) {
         case KeyEvent.KEYCODE_BACK:
        	
             if (mCommentView.mInputBar.onBack()) {
            	 Log.v("--hwp--","KEYCODE_BACK");
                 return true;
             } 
             else {
            	mCommentView.rebackFeedinfo();
             	Logger.logd(" no  onback");
                 return super.onKeyDown(keyCode, event);
             }
         case KeyEvent.KEYCODE_MENU:
             break;
         case KeyEvent.KEYCODE_HOME:
        	 Log.v("--hwp--","hidesoft");
             mCommentView.hideKeyBoard();
        	 break;
     }
     return false;
	}
	
}

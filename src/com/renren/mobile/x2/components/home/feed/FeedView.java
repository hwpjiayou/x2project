package com.renren.mobile.x2.components.home.feed;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.R.color;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.network.mas.UGCUserModel;
import com.renren.mobile.x2.utils.DipUtil;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.Response;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.view.CoverWrappedListView;
import com.renren.mobile.x2.view.LoadMoreViewItem;
import com.renren.mobile.x2.view.LoadMoreViewItem.onLoadListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Feed UI
 * @author jia.xia
 *
 */
public class FeedView {

	private Context mContext ;
	private CoverWrappedListView mRootView;
	public ListView mFeedListView;

	public FeedAdapter mFeedAdapter;

	private int mListViewFirstIndex;
	private int mListViewLastIndex;

	private OnScrollListener mOnScrollListener = null;
	private OnTouchListener mOnTouchListener = null;
	private int mScrillState ;
	private ProgressBar mProgressBar = null;


	public FeedManager mFeedManager ;

	public LoadMoreViewItem mAddItem ;
	
	public OnClickListener mClickListener;
	
	public TextView mFeedButton;
	public TextView mTop10Button;
	public ProgressBar mRefreshProgress;
	/**
	 * 刷新按钮
	 */
	public ImageButton mRefreshButton;
	
	public Animation mRefreshAnimation;
	
	public View loadingView = null;
	
	/**
	 * 占位的空白VIew
	 */
	public View mBlackView = null;
	
	public FeedView(Context context,FeedManager manager){
		this.mContext = context;
		this.mFeedManager = manager;
		initView();
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mRootView = new CoverWrappedListView(mContext);
		mRootView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
		mFeedListView = mRootView.getListView();
		mFeedListView.setSelector(R.color.blue);
		mFeedListView.setDivider(null);
		loadingView = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.feed_progressbar, null);
		
		mFeedListView.addFooterView(loadingView);
		initTopView(mRootView);
		initTabView(mRootView);
		//初始化Adapter
		mFeedAdapter = new FeedAdapter(mContext,this);
	}
	
	private void initTabView(final CoverWrappedListView rootView){
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cover = (View)inflater.inflate(R.layout.feed_tab_layout, null);
		rootView.setTabHostView(cover);
//		mRefreshAnimation = AnimationUtils.loadAnimation(mContext, R.anim.refresh_roter_clockwise);
//		mRefreshAnimation.setFillAfter(true);
		mClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.feed_tab_refresh_button:
//					mRefreshButton.setImageResource(R.drawable.feed_tab_refresh_press);
//					mRefreshButton.start(40.0f);
					//mRefreshProgress.setVisibility(View.VISIBLE);
					mFeedListView.setSelection(0);
					if(mFeedAdapter.ismFeedStatus()){
						mFeedManager.getFeedListFromNet(true);
						Logger.logd("onclick","getfeedlist ");
					}
					else{
						mFeedManager.getTop10FeedFromNet();
					}
					break;
				case R.id.feed_tab_top10_button:  //点击十大的按钮，暂未使用
					Logger.logd("cc", "feed_tab_feed_button click");
					
					break;
				case R.id.feed_tab_feed_button:  //点击feed的按钮，暂未使用
					Logger.logd("cc", "feed_tab_top10_button click");
					
					break;
				case R.id.feed_tab_layout:  //点击整个布局
					boolean status = mFeedAdapter.ismFeedStatus();
					mFeedListView.setSelection(0);
					if(status == true){
						changeToTop10();
					}
					else{
						changeToFeed();
					}
					break;
					
				default:
					break;
				}
			}
		};
		
		RelativeLayout tabFeed = (RelativeLayout)cover.findViewById(R.id.feed_tab_layout);
		tabFeed.setOnClickListener(mClickListener);
		mFeedButton = (TextView)cover.findViewById(R.id.feed_tab_feed_button);
		//mFeedButton.setOnClickListener(mClickListener);
		mTop10Button  = (TextView)cover.findViewById(R.id.feed_tab_top10_button);
		//mTop10Button.setOnClickListener(mClickListener);
//		mRefreshProgress = (ProgressBar)cover.findViewById(R.id.feed_tab_refresh_progressbar);
		
		mTop10Button.setBackgroundDrawable(null);
		mRefreshButton  =(ImageButton)cover.findViewById(R.id.feed_tab_refresh_button);
//		mRefreshButton.setPressSelector(R.drawable.v1_common_refresh_bg_press, R.drawable.v1_common_refresh_bg_unpress);
		//mRefreshButton.setImageResource(R.drawable.feed_tab_refresh_bg);
		mRefreshButton.setOnClickListener(mClickListener);
		mRefreshButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mRefreshButton.setImageResource(R.drawable.v1_common_refresh_press);
					mRefreshButton.setBackgroundResource(R.drawable.v1_common_refresh_bg_press);
					break;
				case MotionEvent.ACTION_UP:
					mRefreshButton.setImageResource(R.drawable.v1_common_refrash_unpress);
					mRefreshButton.setBackgroundResource(R.drawable.v1_common_refresh_bg_unpress);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}
	/**
	 * 当状态转为Top10界面时，调用
	 */
	public void changeToTop10(){
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				//mProgressBar.setVisibility(View.VISIBLE);
				mTop10Button.setBackgroundResource(R.drawable.feed_tab_feed_press_bg);
				mTop10Button.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t4));
				mFeedButton.setBackgroundDrawable(null);
				mFeedButton.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t5));
				mFeedAdapter.setmFeedStatus(false);
				removeLoadMoreItem();
				//List中无数据，则自动加载一次
				if(mFeedAdapter.mTop10List.size() == 0){
					mFeedManager.getTop10FeedFromNet();
				}else{  //有数据则通知刷新
					RenrenChatApplication.getUiHandler().post(new Runnable() {
						
						@Override
						public void run() {
//							if(mProgressBar.isShown())
//								mProgressBar.setVisibility(View.GONE);
							mFeedAdapter.notifyDataSetChanged();
						}
					});
				}
			}
		});
	}
	/**
	 * 当状态转为Feed界面时，调用
	 */
	public void changeToFeed(){
		mFeedAdapter.setmFeedStatus(true);
		
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				setLoadMoreItem();
				mFeedAdapter.notifyDataSetChanged();
				mFeedButton.setBackgroundResource(R.drawable.feed_tab_feed_press_bg);
				mFeedButton.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t4));
				
				mTop10Button.setBackgroundDrawable(null);
				mTop10Button.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t5));
			}
		});
	}
	
	/**
	 *  初始化Cover的布局
	 * @param mRootView2:CoverWrappedListView
	 */
	private void initTopView(final CoverWrappedListView rootView) {
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cover = (View)inflater.inflate(R.layout.feed_top_layout, null);
		rootView.setTopView(cover);
		LoginInfo loginInfo = LoginManager.getInstance().getLoginInfo();
		//设置名字和学校
		TextView cover_name =  (TextView)cover.findViewById(R.id.cover_head_username);
		cover_name.setText(loginInfo.mUserName+"@"+loginInfo.mSchool);
		//设置头像
		final ImageView cover_headimg = (ImageView)cover.findViewById(R.id.cover_head_img);
		final String headUrl  = loginInfo.mMediumUrl;
		
		if(headUrl == null) return;
		
		Logger.logd("log","headUrl:"+headUrl);
		cover_headimg.setTag(headUrl);
		HttpImageRequest request = new HttpImageRequest(headUrl, true);
		ImageLoader  loader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD, mContext);
		
		Bitmap bitmap = loader.getMemoryCache(request);
		if(bitmap != null){
			//Bitmap ovalBit = ImageUtil.CreateOvalBitmap(bitmap);
			cover_headimg.setImageBitmap(bitmap);
		}
		else{
			Response response = new Response() {
				
				@Override
				public void success(final Bitmap bitmap) {
					RenrenChatApplication.getUiHandler().post(new Runnable() {
						
						@Override
						public void run() {
							ImageView  view = (ImageView)rootView.getListView().findViewWithTag(headUrl);
							if(view != null){
								//Bitmap ovalBit = ImageUtil.CreateOvalBitmap(bitmap);
								view.setImageBitmap(bitmap);
							}
						}
					});
				}
				
				@Override
				public void failed() {
					
				}
			};
			if(loginInfo.mGender == UGCUserModel.GENDER_FAMALE)
				cover_headimg.setImageResource(R.drawable.v1_default_famale);
			else{
				cover_headimg.setImageResource(R.drawable.v1_default_male);
			}
			loader.get(request, response);
		}
	}

	public void removeFooterView(){
		mFeedListView.removeFooterView(loadingView);
		mFeedListView.removeFooterView(mBlackView);
	}
	
	/**
	 * 加入自动加载控件
	 */
	public void setLoadMoreItem(){
		removeFooterView();
		if(mAddItem == null&&mFeedAdapter.ismFeedStatus()){
			Logger.logd("lij", "setLoadMoreItem");
			mAddItem = LoadMoreViewItem.getLoadMoreViewItem(mContext);
//			LayoutParams layout = new LayoutParams(LayoutParams.FILL_PARENT, DipUtil.calcFromDip(50));
//			mAddItem.setLayoutParams(layout);
			mAddItem.setSize(100);
			mAddItem.setOnLoadListener(new onLoadListener() {

				@Override
				public void onLoad() {
					mFeedManager.getFeedListFromNet(false);
				}
			});
			mFeedListView.addFooterView(mAddItem);
			//mAddItem.setProgressVisible(false);
		}
	}
	/**
	 * 进入TOP10时去掉自动加载控件
	 */
	public void removeLoadMoreItem(){
		if(mAddItem!=null&&!mFeedAdapter.ismFeedStatus()){
			Logger.logd("feed", "removeLoadMoreItem");
			mFeedListView.removeFooterView(mAddItem);
			mAddItem = null;
			if(mBlackView ==null){
				mBlackView = new View(mContext);
				android.widget.AbsListView.LayoutParams layout = new android.widget.AbsListView.LayoutParams(-1, DipUtil.calcFromDip(100));
				mBlackView.setLayoutParams(layout);
			}
			mFeedListView.addFooterView(mBlackView);
		}
	}
	/**
	 * 
	 * 去掉自动加载控件
	*/
	public void removeFeedLoadMoreItem(){
		if(mAddItem!=null){
			Logger.logd("lij", "removeLoadMoreItem");
			mFeedListView.removeFooterView(mAddItem);
			mAddItem = null;
			if(mBlackView ==null){
				mBlackView = new View(mContext);
				android.widget.AbsListView.LayoutParams layout = new android.widget.AbsListView.LayoutParams(-1, DipUtil.calcFromDip(100));				mBlackView.setLayoutParams(layout);
			}
			mFeedListView.addFooterView(mBlackView);
			
		}
	}

	public void addTop10Black(){
		if(mBlackView ==null){
			mBlackView = new View(mContext);
			android.widget.AbsListView.LayoutParams layout = new android.widget.AbsListView.LayoutParams(-1, DipUtil.calcFromDip(100));				mBlackView.setLayoutParams(layout);
		}
		mFeedListView.addFooterView(mBlackView);
	}
	public void setFeedCommentClickListener(FeedCommentClickListener listener){
		if(mFeedAdapter!=null)
			mFeedAdapter.setCommentClickListener(listener);
	}
	
	/**
	 *
	 * 设置AdapterData，初始化List，可能List中无数据
	 */
	public void setFeedAdapter() {
		mFeedListView.setAdapter(mFeedAdapter);
		mFeedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				/*// TODO 点击调转评论
				Logger.logd("x2", "item click:"+arg2);
				Intent intent = new Intent();
				intent.setClass(mContext,CommentActivity.class);
				Bundle bundle = new Bundle();
				FeedModel model = null;
				if(mFeedAdapter.ismFeedStatus()){
					model = mFeedAdapter.mModelList.get(arg2-1);
				}
				else{
					model = mFeedAdapter.mTop10List.get(arg2-1);
				}
				bundle.putSerializable("feedmodel",model);
				bundle.putInt("feed_layout_id", R.layout.feed_adapter_item_layout);
				bundle.putString("itemClickTyle", FeedAdapter.FEED_ITEMCLICK_TYLE);
				intent.putExtras(bundle);
				mContext.startActivity(intent);*/
			}
		});

		mFeedListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Logger.logd("x2", "setOnItemSelectedListener:"+arg2);
				// TODO 点击调转评论
				/*Logger.logd("x2", "item click:"+arg2);
				Intent intent = new Intent();
				intent.setClass(mContext,CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("feedmodel", mFeedAdapter.mModelList.get(arg2-1));
				bundle.putInt("feed_layout_id", R.layout.feed_adapter_item_layout);
				intent.putExtras(bundle);
				mContext.startActivity(intent);*/
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		initEvent();
		mRootView.addOnScrollListener(mOnScrollListener);
		//mFeedListView.setOnTouchListener(mOnTouchListener);
	}

	private void initEvent(){
		mOnScrollListener = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mScrillState = scrollState;
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					if(mFeedAdapter!=null){
						FeedAdapter.isScrolling = false;
						mFeedAdapter.setScroll(false);
						RenrenChatApplication.getUiHandler().post(new Runnable() {

							@Override
							public void run() {
								mFeedAdapter.notifyDataSetChanged();
							}
						});
					}
				}
				else{
					FeedAdapter.isScrolling = true;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Logger.logd("x2222", "onScroll:"+mAddItem);
				if (mAddItem != null && mAddItem.getVisibility() == View.VISIBLE && !mAddItem.isLoading()
						&& (firstVisibleItem + visibleItemCount == totalItemCount)) {
					Logger.logd("x2222", "feedview was click");
					mAddItem.wasClick();
				}
			}
		};

//		mOnTouchListener = new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction() == MotionEvent.ACTION_MOVE&&mScrillState != OnScrollListener.SCROLL_STATE_FLING){
//					if(mFeedAdapter!=null){
//						mFeedAdapter.setScroll(false);
//						mFeedAdapter.notifyDataSetChanged();
//					}
//				}
//				else{
//					if(mFeedAdapter!=null){
//						mFeedAdapter.setScroll(true);
//					}
//				}
//				return false;
//			}
//		};
		
		
	}

	public View getView(){
		return mRootView;
	}

	/*@Override
	public void onClick(View v) {

		switch(v.getId()){
		//新鲜事按钮
		case R.id.feed_main_feedbutton:
			Logger.logd("x22", "onclick feed:"+mFeedStates);
			if(mFeedStates != true){//不是Feed界面
				mFeedAdapter.setCount(1000);
			}
			mFeedAdapter.notifyDataSetChanged();
			mFeedStates = true;
			break;
	    //Top10按钮
		case R.id.feed_main_top10button:
			if(mFeedStates != false){//不是Top10界面
				mFeedAdapter.setCount(10);
			}
			mFeedAdapter.notifyDataSetChanged();
			mFeedStates = false;
			break;
		}
	}
*/
	}

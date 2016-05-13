package com.renren.mobile.x2.components.comment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.afpro.jni.speex.CommentHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.refresh.OnRefreshListener;
import com.renren.mobile.x2.components.chat.PluginAdapter;
import com.renren.mobile.x2.components.chat.PluginAdapter.PluginListenter;
import com.renren.mobile.x2.components.chat.util.AutoSwitchMode;
import com.renren.mobile.x2.components.chat.util.AutoSwitchMode.OnProximityChangeListenner;
import com.renren.mobile.x2.components.chat.util.IInputBarListenner;
import com.renren.mobile.x2.components.chat.util.ThreadPool;
import com.renren.mobile.x2.components.chat.util.ToastAnimationListennerImpl;
import com.renren.mobile.x2.components.chat.view.InputBar;
import com.renren.mobile.x2.components.chat.view.VoiceView;
import com.renren.mobile.x2.components.comment.CommentAdapter.OnFingerTouchListener;
import com.renren.mobile.x2.components.home.HomeActivity;
import com.renren.mobile.x2.components.home.feed.FeedAdapter;
import com.renren.mobile.x2.components.home.feed.FeedManager;
import com.renren.mobile.x2.components.home.feed.FeedModel;
import com.renren.mobile.x2.components.home.feed.FeedViewHolder;
import com.renren.mobile.x2.components.home.profile.ProfileActivity;
import com.renren.mobile.x2.components.imageviewer.ImageViewActivity;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.emotion.EmotionString;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.mas.UGCActionModel;
import com.renren.mobile.x2.network.mas.UGCContentModel;
import com.renren.mobile.x2.network.mas.UGCImgModel;
import com.renren.mobile.x2.network.mas.UGCModel;
import com.renren.mobile.x2.network.mas.UGCPlaceModel;
import com.renren.mobile.x2.network.mas.UGCTagModel;
import com.renren.mobile.x2.network.mas.UGCTextModel;
import com.renren.mobile.x2.network.mas.UGCUserModel;
import com.renren.mobile.x2.network.mas.UGCVoiceModel;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DateFormat;
import com.renren.mobile.x2.utils.DipUtil;
import com.renren.mobile.x2.utils.FileUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.Response;
import com.renren.mobile.x2.utils.img.ImageLoader.UiResponse;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PCMPlayerSetting;
import com.renren.mobile.x2.utils.voice.PlayerThread;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnSwitchPlayModeListenner;
import com.renren.mobile.x2.utils.voice.PlayerThread.PlayRequest;
import com.renren.mobile.x2.utils.voice.VoiceManager;
import com.renren.mobile.x2.view.ITitleBar;
import com.renren.mobile.x2.view.LoadMoreViewItem;
import com.renren.mobile.x2.view.LoadMoreViewItem.onLoadListener;
import com.renren.mobile.x2.view.RefresherListView;

/**
 * 评论的ui界面
 * 
 * @author hwp
 * 
 */
public class CommentFragment extends BaseFragment<List<UGCContentModel>> implements 
                    OnClickListener,
                    OnScrollListener,
                    OnTouchListener,
                    IInputBarListenner,
                    PluginListenter,
                    OnRefreshListener,
                    OnFingerTouchListener,
                    OnProximityChangeListenner,
                    OnSwitchPlayModeListenner{

	public static  List<UGCContentModel> DATA ;
    public static final int EMOTION_EDITTEXT_LENGTH=240;
    public static final int FEED_COMMENT_PASS=1;
	public UGCContentModel model=null;
	public static final String voicePath = "/mnt/sdcard/x2/voice";

	public String UGCid;
	private Handler mHandler=new Handler();
    private List<PluginListenter> listenters = new ArrayList<PluginListenter>();
    /** 选择照片的操作。*/
    private Comment_SelectPhoto mSelectPhoto;
    /** 选择拍照的操作*/
    private Comment_TakePhoto mTakePhoto;
	/** 选择录音的操作*/
    private Comment_Recorder mRecord;
    
	private CommentActivity mActivity;
	private View mRootView;
	public ListView mListView;
	private View mAddModeLayout;
	private View mAddMore;
	private View mLikeHeader;
	private View mFeedCommentDrive;
	public ITitleBar mTitleBar=null;
	//底部输入框。。。
	public InputBar mInputBar=null;
	public InputMethodManager imm;
	public  ProgressBar mProgress=null;
	private Bundle mFeedBundle;
	/** 提示切换成听筒或扬声器 */
	private LinearLayout mRoot_Switch_Toast = null;
	/**不可点击的浮层 */
	private LinearLayout mRoot_unAble_View = null;
	/** 录音时候中间弹出提示*/
	public VoiceView mRoot_VoiceView = null;
	 /* 播放模式装换器 */
    public  AutoSwitchMode  mSwitchMode= null;
    /*开启扬声器开关*/
    private boolean  mIsOpenReceiver= false;
    /**切换到扬声器或听筒时的文本 */
   	private TextView mSwitchTextView;
   	
	/* 用于记录不锁屏前的window性质 */
	public int mOldAttributeParams = 0;
    
    ToastAnimationListennerImpl mToastImpl=new ToastAnimationListennerImpl();
    
	protected int mPage = 0;

	private String mTime = "";
	
	private int commentCount;

	protected boolean mIsRefresh = true;
	// path中用到的view
	private ImageView ivCamera, ivWith, ivPlace, plug;
	
	public FeedModel mFeedDataModel;
	private String mFeedClickComment;
	private String mFeedClickLike;
	private FeedViewHolder mFeedholder;
	private ImageLoader mFeedImageLoader=null;
	private ImageLoader mFeedHeadImageLoader=null;
	
	
	public CommentAdapter mAdapter;
	//载入更多。
	public ImageView emptyView;
	public  LoadMoreViewItem addMore;
	private View mHeaderView;
	private CommentManager manger;
	private boolean isScroll=false;
	public static AtomicBoolean LOCK = new AtomicBoolean(false);
	//下拉刷新。
	private RefresherListView mRefresh;
	
	public  LoginInfo mLoginInfo;
	private  CommentItem mCommentItem;
	public  Builder builder;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		this.mActivity=(CommentActivity) getActivity();		
		this.mFeedBundle = getArguments();
		DATA = new ArrayList<UGCContentModel>();
		mSelectPhoto=new Comment_SelectPhoto(CommentFragment.this);
		mTakePhoto=new Comment_TakePhoto(CommentFragment.this);
		mRecord=new Comment_Recorder(CommentFragment.this);
		initView();
		manger=new CommentManager(CommentFragment.this);
		setRefreshListener(this);
		builder=new AlertDialog.Builder(mActivity);
		
		Logger.logStrictModeThread();
		
		return getRootView();
	}

	// 初始化界面
	public void initView() {
		imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		mRootView = View.inflate(mActivity, R.layout.comment_main_layout, null);
		//刷新的初始化。
		mRefresh=new RefresherListView(mActivity);
		
		//刷新的初始化。
		mRefresh=(RefresherListView) mRootView.findViewById(R.id.listview);
		mListView=mRefresh.getListView();

		mListView.setDrawingCacheEnabled(false);
		mListView.setHorizontalFadingEdgeEnabled(false);
		mListView.setScrollingCacheEnabled(false);
		mListView.setAlwaysDrawnWithCacheEnabled(false);
		mListView.setWillNotCacheDrawing(true);
		
		//播放模式转换器。
		mSwitchMode=new AutoSwitchMode(mActivity);
		mSwitchMode.setOnProximityChangeListenner(this);
		VoiceManager.getInstance().setOnSwitchPlayerListenner(this);
		mSwitchTextView = (TextView)mRootView.findViewById(R.id.cdw_chat_switch_toast_text);
		
		
		addMore=LoadMoreViewItem.getLoadMoreViewItem(mActivity);
		addMore.setOnLoadListener(new onLoadListener() {
			@Override
			public void onLoad() {
				    mPage++;
					mIsRefresh = false;
					manger.getCommentListFromNet(mIsRefresh, mPage);
			}
		});
//		mListView.addFooterView(addMore);

		mTitleBar=getTitleBar();
		mHeaderView=getHeaderView();
		if(mHeaderView!=null){
			mHeaderView.setBackgroundColor(getResources().getColor(R.color.wiate));
			mListView.addHeaderView(mHeaderView);
			mListView.setHeaderDividersEnabled(true);
			
		}
		mListView.setFooterDividersEnabled(false);
		mListView.setDivider(mActivity.getResources().getDrawable(R.color.wiate));
		initProgressBar();
		
		//发送
		mInputBar=(InputBar) mRootView.findViewById(R.id.chatmain_inputbar);
		mInputBar.updateHit(R.string.InputBar_Hit_1);
	    mInputBar.setPlugViewVisible();//此方法发让“+”插件消失。
		
		
		mTitleBar.setLeftAction(R.drawable.v1_comment_title_left_unpress, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hideKeyBoard();
					rebackFeedinfo();
				}
			});
		
		mTitleBar.setRightAction(R.drawable.v1_comment_title_right_unpress, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					builder.setTitle(R.string.comment_jubao);
					builder.setMessage(R.string.comment_jubao_message);
					builder.setNegativeButton(R.string.comment_jubao_yes, new  DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CommonUtil.toast(R.string.comment_jubao_toast);
						}
					});
					builder.setPositiveButton(R.string.comment_jubao_no, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							builder.create().cancel();
						}
					});
					builder.create().show();
				}
			});
	}
	
	public void rebackFeedinfo() {
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		if (mAdapter != null) {
			bundle.putSerializable("commentlist",
					(Serializable) mAdapter.getTwoComments());
			bundle.putInt("commentcount",commentCount);
			bundle.putBoolean("islike", mFeedDataModel.ismIsLike());
			
			intent.putExtras(bundle);
		}
		mActivity.setResult(FEED_COMMENT_PASS, intent);
		clean();
		mActivity.finish();
	}
	
	//得到登陆信息。。
	public void getLoginInfo(){
		mLoginInfo=LoginManager.getInstance().getLoginInfo();
	}

	public  View getRootView(){
		return mRootView;
	}
	
	private View getHeaderView(){
		View mHeaderView=View.inflate(getActivity(), mFeedBundle.getInt("feed_layout_id"), null);
		mFeedholder=new FeedViewHolder();
		Logger.l(mHeaderView.findViewById(R.id.feed_username));
		ViewMapUtil.viewMapping(mFeedholder, mHeaderView);
		mFeedDataModel=(FeedModel) mFeedBundle.getSerializable("feedmodel");
		mFeedDataModel.getmPostContentModel();
		setHolderHeadData(mFeedholder,mFeedDataModel);
		return mHeaderView;
	}
	
	//对头部view设置数据。
	public void setHolderHeadData(final FeedViewHolder viewHolder,final FeedModel model){	
	   //评论title为发布者的名称
		mTitleBar.setTitle(model.getmPostContentModel().user.mName);
		// 初始话头部，内容图片展示方式
		
		FeedAdapter feedAdapter = new FeedAdapter(mActivity);
		feedAdapter.setViewHolderData(viewHolder, model, mListView);
		viewHolder.mCommentLayout.setVisibility(View.GONE);

		// 评论的图标消失。
		viewHolder.mCommentImg.setVisibility(View.GONE);
		viewHolder.mCommentCountText.setVisibility(View.GONE);
		
		//喜欢数消失
		viewHolder.mLikeCountText.setVisibility(View.GONE);
		// timeline去掉。
	    mFeedholder.mFeedLeftTimeline.setVisibility(View.GONE);
	    mFeedholder.mFeedHeader.setPadding(20, 0, 12, 0);
	    mFeedholder.mHeaderImage.setImageResource(R.drawable.v1_feed_headimg_online);
	    //去掉性别
	    mFeedholder.mGenderImg.setVisibility(View.GONE);
	    //头部跟listview的分割线。
	    mFeedCommentDrive=viewHolder.mCommentFeedDrive;
	    mFeedCommentDrive.setVisibility(View.VISIBLE);
	    //加载更多
	//    mAddModeLayout=viewHolder.mCommentsRelativeLayout;
	    mAddModeLayout=viewHolder.mCommentAddmorLayout;
	    mAddMore=viewHolder.mCommentsAddmore;
	    
	    viewHolder.mComment1Layout.setVisibility(ViewGroup.GONE);
	    viewHolder.mComment2Layout.setVisibility(ViewGroup.GONE);
	    
	    int likeCount=model.getmLikeCount();
	    if(likeCount!=0){
	    	viewHolder.mLikeCountText.setVisibility(View.VISIBLE);
	  //  	viewHolder.mLikeCountText.setText(model.getmLikeCount());
	    	viewHolder.mLikeImg.setImageResource(R.drawable.feed_like_like);
	    }
	    
	
	    mAddModeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				    mPage++;
					mIsRefresh = false;
					manger.LoadComment(mIsRefresh, mPage);
			}
		});
	    
	}
	
	//在feed界面中点击评论图标，进入到评论界面，评论界面中的键盘弹起。显示的是最后一条记录。
	public void showNewsComment(){
		imm.showSoftInput(mInputBar.mView_TextEdit, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);//
		mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
	    mInputBar.mView_TextEdit.setFocusable(true);
	}
	
	public void showEmptyComment(){
		imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mInputBar.mView_TextEdit, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
		mInputBar.mView_TextEdit.setFocusable(true);
	}
	
	public void hideKeyBoard(){
		imm.hideSoftInputFromWindow(mInputBar.mView_TextEdit.getWindowToken(), 0);
	}
	
	
	public void showAddMore(boolean isShow){
		if (isShow) {
			addMore.setVisibility(View.VISIBLE);
		} else {
			addMore.setVisibility(View.GONE);
		}
	}
	/*
	 * 初始话进入评论界面的progerss。
	 */
	public void initProgressBar(){
		mProgress=(ProgressBar) mRootView.findViewById(R.id.comment_progressBar);
		
		mProgress.setVisibility(View.VISIBLE);
	}
	
	

	public void setEmptyView(){
		Log.v("--hwp--","no comments so show view emptyview");
		mFeedholder.mTishi.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.GONE);
		mProgress.setEnabled(false);
		
		emptyView= new ImageView(mActivity);
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		emptyView.setImageResource(R.drawable.v1_home_menu_logo);
		emptyView.setVisibility(View.GONE);
		
		mListView.addView(emptyView);
	}
	
	/**
	 * 加载数据前，ui预处理。
	 */
	@Override
	protected void onPreLoad() {
		Log.v("--hwp--","go");
		mAdapter = new CommentAdapter(getActivity(), mFeedBundle);
		
		mListView.setEmptyView(emptyView);

		mListView.setAdapter(mAdapter);
		
		mAdapter.getFeedId(mFeedDataModel);
		
		mAdapter.setOnFingerTouchListener(this);
		mListView.setOnScrollListener(this);
		mListView.setOnTouchListener(this);
		mListView.setOnItemLongClickListener(mAdapter);
		
		
		mRoot_Switch_Toast=(LinearLayout)mRootView.findViewById(R.id.cdw_chat_switch_toast);
		mRoot_unAble_View=(LinearLayout) mRootView.findViewById(R.id.cdw_chat_main_unable_view);
		mRoot_unAble_View.setVisibility(View.GONE);
		mRoot_unAble_View.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		//为输入布局注册监听事件
		mInputBar.registerInputBarLisenner(this);
		
		mRoot_VoiceView=(VoiceView)mRootView.findViewById(R.id.chatmain_voiceview);
		mRoot_VoiceView.monitorView(mInputBar.mView_VoiceRecord);
		mRoot_VoiceView.setMonitorListener(mRecord);
		mRoot_VoiceView.setOnPreTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					mRecord.onStartCommand();
				}
				return true;
			}
		});
		
		
	    PluginAdapter adapter = new PluginAdapter();
	    adapter.register(this);
		mInputBar.setAdapter(adapter);
		
		//加载喜欢的人的想过操作。
		initLikeView();
		mFeedClickComment=(String) mFeedBundle.getSerializable("commentButtonType");
      
		if(mFeedClickComment!=null){
			Log.v("--hwp--","mFeedClickComment "+mFeedClickComment);
			showEmptyComment();
		}
	}

	public void initLikeView(){
		  //喜欢人的布局。
	    mLikeHeader=mFeedholder.mCommentLikeHeader;
		mFeedholder.mLikePeopleMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("--hwp--","显示更多喜欢的人");
				CommonUtil.toast("显示更多喜欢的人");
			}
		});
		for(int i=1;i<=5;i++){
			ImageView view=null;
			switch(i){
			case 1:
				view=mFeedholder.mLikePeople1;
				break;
			case 2:
				view=mFeedholder.mLikePeople2;
				break;
			case 3:
				view=mFeedholder.mLikePeople3;
				break;
			case 4:
				view=mFeedholder.mLikePeople4;
				break;
			case 5:
				view=mFeedholder.mLikePeople5;
				break;
			}
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d("--hwp--","likepeople onclick");
					CommonUtil.toast("点击了喜欢的人的头像。转入个人主页中");
				}
			});
		}
	}
	
	
	public void refreshComment() {

	}
	
	public void returnTop() {
		if (mListView != null || mAdapter != null) {
			mListView.setSelection(0);
		}
	}

	public void setCommentAdapter(List<UGCContentModel> model){
		Log.v("--hwp--", "setCommentAdapter");
	}
	
	@Override
	protected void onFinishLoad(List<UGCContentModel> data) {
		//用于对加载喜欢的人内容作的数据处理。
//		int likeCount=10;
//		 if(likeCount>0){//用户对
//	    	 mLikeHeader.setVisibility(View.VISIBLE);
//	    }else{
//	    	 mLikeHeader.setVisibility(View.GONE);
//	    }
		
		Log.e("--hwp--","thread2 "+Thread.currentThread().getName().toString());
		mAdapter.notifyDataSetChanged();
	}
	//向服务其发送数据请求
	public  void getCommentDataFromLoca(){
		
		manger.getCommentListFromNet(false,0);
	}
		
	@Override
	protected List<UGCContentModel> onLoadInBackground() {
		
		if (DATA != null) {
			DATA.clear();//确保每次进入评论页面时，都是网络上请求数据，清空点入点出是，由于activity的destory没有销毁数据操作的数据不会0；
		}
		getLoginInfo();
		getCommentDataFromLoca();
		//可以进行其他大数据的请求操作。
		List<UGCContentModel> commentModel = null;
		commentModel = DATA;
		return commentModel;
	}

	//commengMenger中发送请求回来的数据中调用。
	public void reset(final JSONArray array, final boolean isRefresh,final int countNum,final int total_count) {
		if (array == null || array.length() == 0) {
					return;
				}
				if (isRefresh) {
					clean();
				}
					RenrenChatApplication.getUiHandler().post(new Runnable() {
						@Override
						public void run() {
							JSONObject object = null;
							for (int i = 0; i < array.length(); i++) {
								try {
									commentCount=total_count;
									if(mFeedClickComment!=null){
											if(commentCount!=0){//当前有评论信息，从feed点评论进来键盘弹起。
												Log.v("--hwp--","tanqi");
												showNewsComment();
											}
										}
									mAddMore.setVisibility(View.VISIBLE);
									mFeedholder.mNumCommentPeople.setText(""+total_count);//设置评论数
									if(countNum<10){//用于对加载更多评论进行的设置
										mAddModeLayout.setVisibility(View.GONE);
									}else{
										mAddModeLayout.setVisibility(View.VISIBLE);
									}
									object = array.getJSONObject(i);
									model = new UGCContentModel(object);
									if(i==array.length()-1){
										UGCid=model.id;
									}
									
									if(mActivity.isFinishing()==false){
										if(model.state.equals("Normal")){
											DATA.add(model);
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
//							Log.v("--hwp--","data "+DATA.size());
//							for(int i=0;i<DATA.size();i++){
//								if(!DATA.get(i).state.equals("Normal")){
//									Log.v("--hwp--","shanchu  "+((UGCTextModel)DATA.get(i).contentInfo.get(UGCModel.UGCType.TEXT)).mText);
//									DATA.remove(i);
//								}
//							}
							mProgress.setVisibility(View.GONE);
							
							Log.v("--hwp--","shanchu hou  "+DATA.size());
							mAdapter.setCommentData(DATA,mListView);
							Log.e("--hwp--","thread1 "+Thread.currentThread().getName().toString());
							mAdapter.notifyDataSetChanged();
						}
					});
	}
	
	public void clean(){
		if (mAdapter != null) {
			mAdapter.adapterClear();
		}
	}

	
	
	@Override
	protected void onDestroyData() {
//		if(!DATA.isEmpty()){
//			Log.v("--hwp--","onDestroyData");
//			DATA.size();
//			DATA.clear();
//			clean();
//		}
	}
    //添加一条评论。
	public void addCommentIten(CommentItem item){
		mAdapter.setOwenCommentData(item);
	}
	
	public void addCommentItem(UGCContentModel contentModel){
		mAdapter.setData(contentModel);
	}
	
	@Override
	public void onClick(View v) {
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch(scrollState){
		case SCROLL_STATE_IDLE:
			mAdapter.setScroll(true);
			isScroll=true;
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					Log.e("--hwp--","thread3 "+Thread.currentThread().getName().toString());
					mAdapter.notifyDataSetChanged();
				}
			});
			break;
		case SCROLL_STATE_FLING:
			isScroll=false;
			mAdapter.setScroll(false);
			break;
		case SCROLL_STATE_TOUCH_SCROLL:
			isScroll=false;
			mAdapter.setScroll(false);
			break ;
		}
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
//		if(DATA.size()!=0){
//			if (addMore != null && addMore.getVisibility() == View.VISIBLE && !addMore.isLoading()
//					&& (firstVisibleItem + visibleItemCount == totalItemCount)) {
//				addMore.setBackgroundResource(0);
//				addMore.wasClick();
//			}
//		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
//	    if(!mInputBar.isFocused()){
//	    	mInputBar.onBack();
//	    }  触摸listview输入框不收回，产品定的。
		return false;
	}

	/**
	 * 引入底部输入框布局后需要实现的方法。
	 */
	
	//表情选中。
	@Override
	public void onEmotionSelect(String emotion) {
		// TODO Auto-generated method stub
		int selectNum=mInputBar.mView_TextEdit.getSelectionEnd();
		int num=selectNum+emotion.length();
		if(num<=EMOTION_EDITTEXT_LENGTH){
			mInputBar.mView_TextEdit.insertEmotion(emotion);
			mInputBar.mView_TextEdit.setSelection(selectNum+emotion.length());
		}
	}
    //表情删除。
	@Override
	public void mDelBtnClick() {
		// TODO Auto-generated method stub
		mInputBar.mView_TextEdit.delLastCharOrEmotion();
	}
   //
	@Override
	public void onTyping() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTypingCancel() {
		// TODO Auto-generated method stub
		
	}
	
	UGCContentModel sendModel;
	int count=1;
    //输入框的发送事件。
	@Override
	public void onSend(String message) {
		// TODO Auto-generated method stub
		mFeedholder.mTishi.setVisibility(View.GONE);
		long createTime=System.currentTimeMillis();
		sendModel=new UGCContentModel();
		
		mCommentItem=new CommentItem();
		if(mInputBar!=null){
			UGCUserModel user=new UGCUserModel(mLoginInfo.mUserName, mLoginInfo.mUserId,
					mLoginInfo.mGender, mLoginInfo.mMediumUrl);
			user.build();
			sendModel.createTime=createTime;
			sendModel.user=user;
			sendModel.state="Normal";
			
			mCommentItem.setUserName(mLoginInfo.mUserName);
			mCommentItem.setUserId(mLoginInfo.mUserId);
			mCommentItem.setUserHeadUrl(mLoginInfo.mMediumUrl);
			
			String content = mInputBar.mView_TextEdit.getText()!=null?""+mInputBar.mView_TextEdit.getText():null;
			if(content!=null&& content.trim().length()>0){
				mCommentItem.setContent(content);
				UGCTextModel textModel=new UGCTextModel(content);
				textModel.build();
				sendModel.contentInfo.put(UGCModel.UGCType.TEXT, textModel);
			}
			
			mCommentItem.setCreateTime(createTime);
			if(commentCount==0){
				mAddMore.setVisibility(View.VISIBLE);
			}
			
			mFeedholder.mNumCommentPeople.setText(""+(commentCount+count));//设置评论数
			
			//doAddCommentAnimation(mCommentItem);
			
		}
		mInputBar.mView_TextEdit.setText("");
       
		String feedId=mFeedDataModel.getFeedId()+"";
        JSONObject content;
      	JsonObjectCommentItem object=new JsonObjectCommentItem(mCommentItem);
      	content=object.toJsonObject();
      	HttpMasService.getInstance().postComment(LoginManager.getInstance().getLoginInfo().mSchool_id, feedId, content, sendComment);
      	count++;
      	addCommentItem(sendModel);
      	hideKeyBoard();
	}

	
	public  INetResponse  sendComment = new INetResponse() {
		
		@Override
		public void response(INetRequest req, JSONObject obj) {
			// TODO Auto-generated method stub
			final JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				//toast("success:"+data.toString());
				mCommentItem.setUgc_id(data.optString("ugc_id"));
				sendModel.id=data.optString("ugc_id");
				mAdapter.notifyUpData(sendModel);
				Log.v("--hwp--", "success "+data.toString());
			}else{
				CommonUtil.toast("error:"+data.toString());

			}
		}
	};
	
	public void doAddCommentAnimation(CommentItem item){
		  mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);//
		  mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
		  Animation animation = (Animation)AnimationUtils.loadAnimation(mActivity, R.anim.list_anim);
	      LayoutAnimationController controller = new LayoutAnimationController(animation);
	      controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
	      controller.setDelay(0.5f);//注意这个地方是以秒为单位，是浮点型数据，所以要加f
	  
	//      mListView.setLayoutAnimation(controller);
	      addCommentIten(item);
	}
	
	
	
	@Override
	public void onChangeLayout(int w, int h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyBoardShow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onViewShow() {
		// TODO Auto-generated method stub
		
	}

	//相应拍照，区图片的事件。
	@Override
	public void startPlugin(byte type, Bundle mBundle) {
		switch (type) {
		case PluginListenter.PLUGIN_SELECT_PHOTO:
			mSelectPhoto.onStartCommand();
			break;
		case PluginListenter.PLUGIN_TAKE_PHOTO:
			mTakePhoto.onStartCommand();
			break;
		default:
			break;
		}
	}

	//点击拍照，区图片后的回调方法。执行返回的结果的操作。
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode!=mActivity.RESULT_OK){
			return;
		}
		Map<String,Object> returnData=new HashMap<String, Object>(2);
		switch(requestCode){
		case ImageViewActivity.TAKE_PHOTO:
			returnData.put(Comment_TakePhoto.NEED_RETURN_DATA.RESULT_CODE_INT, resultCode);
			mTakePhoto.onEndCommand(returnData);
			break;
		case ImageViewActivity.SELECT_PHOTO:
			  returnData.put(Comment_SelectPhoto.NEED_RETURN_DATA.RESULT_CODE_INT, resultCode);
              mSelectPhoto.onEndCommand(returnData);
              break;
		}
	}

	/*
	 * 下拉刷新实现的三个接口。
	 * 
	 * @see com.renren.mobile.x2.base.refresh.OnRefreshListener#onPreRefresh()
	 */
	@Override
	public void onPreRefresh() {
		Log.v("--hwp--", "onPreRefresh");
	}

	@Override
	public void onRefreshData() {
		Log.v("--hwp--","onRefreshData "+DATA.size());
	//	manger.LoadComment(true, 0);
	}

	@Override
	public void onRefreshUI() {
		Log.v("--hwp--","onRefreshUI "+DATA.size());
//		mAdapter.setCommentData(DATA);
	}

/**
 * 注册下拉刷新监听事件。
 * @param refreshListener
 */
	public void setRefreshListener(OnRefreshListener refreshListener) {
		mRefresh.setOnRefreshListener(this);
	}
//滑动事件。
	@Override
	public void onFingerTouch() {
		Log.v("--hwp--", "ttttt");
		mInputBar.onBack();
	}

	/**
	 * 听筒切换的相应事件，实现，实现的是onproximityChangeListenner 接口。
	 */
	private boolean mEnable=true;
	
	@Override
	public void onCloseEar() {
       ThreadPool.obtain().executeMainThread(new Runnable() {
		
		@Override
		public void run() {
			mEnable=false;
			mRoot_unAble_View.setVisibility(View.VISIBLE);
			  mSwitchTextView.setText("已切换至听筒模式");
              mToastImpl.toast(mRoot_Switch_Toast);
		   }
	    });
	}

	@Override
	public void onOverEar() {
		  if (!mEnable) {
	            ThreadPool.obtain().executeMainThread(new Runnable() {
	                public void run() {
	                    mEnable = true;
	                    mRoot_unAble_View.setVisibility(View.GONE);
	                    mSwitchTextView.setText("已切换至扬声器模式");
	                    mToastImpl.toast(mRoot_Switch_Toast);
	                }
	            });
	        }
	}
    /**
     * 选择播放模式相应的事件实现，实现的是onswitchplaymodeListenner接口。
     */
	@Override
	public void onOpen() {
		 if (!mIsOpenReceiver) {
	            mSwitchMode.registorSensor();
	        }
	        mHandler.post(new Runnable() {
	            public void run() {
	                keepScreenOn();
	            }
	        });
	}

	@Override
	public void onClose() {
		  mHandler.post(new Runnable() {
	            public void run() {
	                stopKeepScreenOn();
	            }
	        });
	        if (!mIsOpenReceiver) {
	            mSwitchMode.unregisterSensor();
	            this.initVoiceSetting();
	        }
	}
	//初始化声音设置。
	 private void initVoiceSetting() {
	        mHandler.post(new Runnable() {
	            public void run() {
	                mRoot_unAble_View.setVisibility(View.GONE);
	            }
	        });
	        if (mIsOpenReceiver) {
	            PCMPlayerSetting.switchStreamType(AudioManager.STREAM_VOICE_CALL);
	            mActivity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
	        } else {
	            PCMPlayerSetting.switchStreamType(AudioManager.STREAM_MUSIC);
	            mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	        }
	    }
	
	
	
	
	//保持屏幕常亮的操作。
	public void keepScreenOn() {
		mOldAttributeParams = mActivity.getWindow().getAttributes().flags;
		mActivity.getWindow().setFlags(mOldAttributeParams, ~mOldAttributeParams);
		mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	//关闭屏幕常亮。
	public void stopKeepScreenOn() {
		mActivity.getWindow().setFlags(0, ~0);
		mActivity.getWindow().setFlags(mOldAttributeParams, mOldAttributeParams);
	}

	@Override
	public void onCoolEmotionSelect(String emotion) {
		// TODO Auto-generated method stub
		
	}

	//长安item中弹出dialog
	
	
}

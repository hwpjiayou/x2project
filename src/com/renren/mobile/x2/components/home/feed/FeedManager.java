package com.renren.mobile.x2.components.home.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.refresh.OnRefreshListener;
import com.renren.mobile.x2.components.chat.PluginAdapter.PluginListenter;
import com.renren.mobile.x2.components.chat.util.AutoSwitchMode;
import com.renren.mobile.x2.components.chat.util.AutoSwitchMode.OnProximityChangeListenner;
import com.renren.mobile.x2.components.chat.util.IInputBarListenner;
import com.renren.mobile.x2.components.chat.util.ThreadPool;
import com.renren.mobile.x2.components.chat.util.ToastAnimationListennerImpl;
import com.renren.mobile.x2.components.chat.view.InputBar;
import com.renren.mobile.x2.components.chat.view.VoiceView;
import com.renren.mobile.x2.components.comment.CommentFragment;
import com.renren.mobile.x2.components.comment.CommentItem;
import com.renren.mobile.x2.components.comment.Comment_Recorder;
import com.renren.mobile.x2.components.comment.JsonObjectCommentItem;
import com.renren.mobile.x2.components.home.HomeFragment;
import com.renren.mobile.x2.components.home.HomeFragment.OnActivityResultListener;
import com.renren.mobile.x2.components.home.HomeTab;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.mas.UGCModel;
import com.renren.mobile.x2.network.mas.UGCTextModel;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PCMPlayerSetting;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnSwitchPlayModeListenner;
import com.renren.mobile.x2.utils.voice.VoiceManager;
import com.renren.mobile.x2.view.CoverWrappedListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed管理器
 * @author jia.xia
 *
 */

public class FeedManager implements HomeTab {

	public List<FeedModel>fList = new ArrayList<FeedModel>();
	private FeedView mFeedView;
	
	private Context mContext;
	
	public static int FEED_CURRENT_PAGE = 1;
	public static final int FEED_DEFAULT_PAGE = 1;
	public static final int FEED_MAX_PAGE_SIZE =4;
	/**
	 * 成功的Code
	 */
	public static final int ACTIVTIY_RESULT_CODE = 10;
	/**
	 * 请求Code
	 */
	public static final int ACTIVITY_REQUEST_CODE = 9;
	
	private View mRootView;
	private Button mFeedButton;
	private Button mTop10Button;
	private LinearLayout mListLayout;
	public ProgressBar mProgressBar;
	
	/** 选择录音的操作*/
    private Comment_Recorder mRecord;
    //底部输入框。。。
  	public InputBar mInputBar=null;
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
	private OnClickListener mClickListener  = null; 
	
	private IInputBarListenner mInputBarListener = null;
	private OnProximityChangeListenner mOnProximityListener = null;
	private OnSwitchPlayModeListenner mOnSwitchPlayListener = null;
	private PluginListenter mPluginListener = null;
	private FeedCommentClickListener mFeedCommentClickListener = null;
	
	private String mFeedId ;
	private CommentItem mCommentItem;
	public FeedManager(){
		
	}
	/**
	 * 获取Feed列表
	 * @param isRefresh 是否要刷新
	 */
	public void getFeedListFromNet(boolean isRefresh){
		Logger.logd("xj", "feed getFeed");
		getFeedListFromNet(isRefresh, 0,false);
	}
	/**
	 * 
	 * @param isRefresh
	 * @param pageNum
	 * @param isFirstLoading
	 */
	public void getFeedListFromNet(final boolean isRefresh,int pageNum,final boolean isFirstLoading){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				INetResponse response = new INetResponse() {

					@Override
					public void response(INetRequest req, JSONObject obj) {
						Logger.logd("json", "feed json:"+obj.toString());
						RenrenChatApplication.getUiHandler().post(new Runnable() {
							
							@Override
							public void run() {
//								mFeedView.mRefreshButton.stop();
//								mFeedView.mRefreshButton.stopdelay(2000);
//								mFeedView.mRefreshButton.setImageResource(R.drawable.feed_tab_refresh_bg);
								mFeedView.removeFooterView();
								//mFeedView.mRefreshProgress.setVisibility(View.GONE);
							}
						});
						
						if(null!=obj){
							if(Methods.checkNoError(req, obj)){
								try {
									JSONArray array = obj.getJSONArray("feeds");
									int count = obj.optInt("count");
									if(array!=null){
										Logger.logd("x1", "feed json:"+array.toString());
										mFeedView.mFeedAdapter.reset(array, isRefresh,isFirstLoading,count);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								
							}
							else{   //信息错误时，也要进行AddItem的完成操作
								Logger.logd("res", "response error:"+obj.toString());
								RenrenChatApplication.getUiHandler().post(new Runnable() {
									
									@Override
									public void run() {
										if(mFeedView.mAddItem!=null)
											mFeedView.mAddItem.syncNotifyLoadComplete();
										mFeedView.removeFeedLoadMoreItem();
									}
								});

							}
						}
					}
				};
				
				
				//old
				if(isRefresh||isFirstLoading){  //第一次加载或下拉刷新会加载基本加载数量的2倍
//					HttpMasService.getInstance().getFeedList(FEED_CURRENT_PAGE,
//							2*FEED_MAX_PAGE_SIZE, null, response);
					//new
					String school_id = LoginManager.getInstance().getLoginInfo().mSchool_id;
					Logger.logd("log", "school_id:"+school_id);
					//String school_id = "1002";
					HttpMasService.getInstance().getSchoolFeeds(school_id, 20, null, null, response);
				}
				else{
					//预加载
					boolean isLoad = mFeedView.mFeedAdapter.copyNextPager();
//					if(!isLoad)   //没有后续新鲜事，则不进行网络请求
//						return;
					//old
//					HttpMasService.getInstance().getFeedList(FEED_CURRENT_PAGE,
//							FEED_MAX_PAGE_SIZE, null, response);
					//new
					String school_id = LoginManager.getInstance().getLoginInfo().mSchool_id;
					//String school_id = "1002";
					List<FeedModel>list = mFeedView.mFeedAdapter.mModelList;
					String beforeFeedId = null;
					int size = list.size();
					if(size>0){
						beforeFeedId = Long.toString(list.get(size-1).getFeedId());
					}
					HttpMasService.getInstance().getSchoolFeeds(school_id, 10, beforeFeedId, null, response);
				}
			}
		}).start();
		
	}
	/**
	 * 获取校园10大Feed
	 */
	public void getTop10FeedFromNet(){
		INetResponse response = new INetResponse() {
			
			@Override
			public void response(INetRequest req, JSONObject obj) {
				RenrenChatApplication.getUiHandler().post(new Runnable() {
					
					@Override
					public void run() {
//						mFeedView.mRefreshButton.stopdelay(2000);
//						mFeedView.mRefreshButton.setImageResource(R.drawable.feed_tab_refresh_bg);
						mFeedView.removeFooterView();//删除Loading
					}
				});
				if(null!=obj){
					if(Methods.checkNoError(req, obj)){
						try {
							JSONArray array = obj.getJSONArray("feeds");
							if(array!=null){
								Logger.logd("xj", "feed json:"+array.toString());
								mFeedView.mFeedAdapter.reset(array);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
				else{
					RenrenChatApplication.getUiHandler().post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mFeedView.removeFeedLoadMoreItem();
						}
					});
				}
			}
		};
		String school_id = LoginManager.getInstance().getLoginInfo().mSchool_id;
		HttpMasService.getInstance().getTopTen(school_id, null, response);
	}

	@Override
	public int getNameResourceId() {
		// TODO Auto-generated method stub
		return R.string.feed_tab_name;
	}

	@Override
	public int getIconResourceId() {
		// TODO Auto-generated method stub
		return R.drawable.v1_home_menu_feed_selector;
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return mFeedView.getView();
		//return mRootView;
	}

	@Override
	public View onCreateView(Context context) {
		this.mContext = context;
//		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		mRootView = inflater.inflate(R.layout.feed_main_layout, null);
		
		/*****************start 输入框和监听事件************************/
//		initInputBarEvent();
//		mInputBar = (InputBar)mRootView.findViewById(R.id.feed_chatmain_inputbar);
//		mRoot_Switch_Toast = (LinearLayout)mRootView.findViewById(R.id.feed_chatmain_voiceview);
//		mInputBar.registerInputBarLisenner(mInputBarListener);
//		mRoot_VoiceView.monitorView(mInputBar.mView_VoiceRecord);
//		mRoot_VoiceView.setMonitorListener(mRecord);
//		mRoot_VoiceView.setOnPreTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction()==MotionEvent.ACTION_DOWN){
//					mRecord.onStartCommand();
//				}
//				
//				return true;
//			}
//		});
		/***********************end 输入框和监听事件********************************/
		
		/**********start ListView*********/
//		mListLayout = (LinearLayout)mRootView.findViewById(R.id.feed_main_linearlayout);
//		mProgressBar = (ProgressBar)mRootView.findViewById(R.id.feed_main_progressbar);
		
		mFeedView = new FeedView(context,this);
		//mListLayout.addView(mFeedView.getView());
		
		CoverWrappedListView view = (CoverWrappedListView)mFeedView.getView();
		view.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefreshUI() {
				
			}

            @Override
            public void onPreRefresh() {
            }

            @Override
			public void onRefreshData() {
            	Logger.logd("x2222", "CoverWrappedListView: onRefreshData");
            	RenrenChatApplication.getUiHandler().post(new Runnable() {
					
					@Override
					public void run() {
						//mFeedView.mRefreshButton.setImageResource(R.drawable.feed_tab_refresh_unpress);
//		            	mFeedView.mRefreshButton.start(40);
					}
				});
            	
				if(mFeedView.mFeedAdapter.ismFeedStatus()){
					getFeedListFromNet(true);
				}
				else{
					getTop10FeedFromNet();
				}
				
			}
		});
		Logger.logd("xjj", "finishFeedsData");
		mFeedView.setFeedAdapter();
		//设置点击Comment呼出评论的弹出框监听事件
		//mFeedView.setFeedCommentClickListener(mFeedCommentClickListener);
		/************end listview*************/
		
		
		return mFeedView.getView();
		//return mRootView;
	}

	private void initInputBarEvent() {
		
		mFeedCommentClickListener = new FeedCommentClickListener() {
			
			@Override
			public void commentClick(String feedId) {
				mFeedId = feedId;
				mInputBar.setVisibility(View.VISIBLE);
			}
		};
		
		mInputBarListener = new IInputBarListenner() {
			
			//表情选中。
			@Override
			public void onEmotionSelect(String emotion) {
				// TODO Auto-generated method stub
				int selectNum=mInputBar.mView_TextEdit.getSelectionEnd();
				int num=selectNum+emotion.length();
				if(num<=CommentFragment.EMOTION_EDITTEXT_LENGTH){
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
			@Override
			public void onChangeLayout(int w, int h) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onViewShow() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onTypingCancel() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onTyping() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSend(String message) {
				
				INetResponse sendComment = new INetResponse() {
					
					@Override
					public void response(INetRequest req, JSONObject obj) {
						if(obj!=null){
							if(Methods.checkNoError(req, obj)){
								//Logger.logd(R.string.success_push);
							}
						}
					}
				};
				long createTime=SystemClock.currentThreadTimeMillis();
				LoginInfo loginInfo = LoginManager.getInstance().getLoginInfo();
				mCommentItem=new CommentItem();
				if(mInputBar!=null){
					mCommentItem.setUserName(loginInfo.mUserName);
					mCommentItem.setUserId(loginInfo.mUserId);
					mCommentItem.setUserHeadUrl(loginInfo.mOriginal_Url);
					
					String content = mInputBar.mView_TextEdit.getText()!=null?""+mInputBar.mView_TextEdit.getText():null;
					if(content!=null&& content.trim().length()>0){
						mCommentItem.setContent(content);
					}
					
					mCommentItem.setCreateTime(createTime);
					//addCommentIten(mCommentItem);
				}
				mInputBar.mView_TextEdit.setText("");
				InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
		        imm.hideSoftInputFromWindow(mInputBar.getWindowToken(),0);
		        
		        String feedId=mFeedId+"";
		        Log.v("--hwp--","fid "+feedId);
		        JSONObject content;
		      	JsonObjectCommentItem object=new JsonObjectCommentItem(mCommentItem);
		      	content=object.toJsonObject();
		      	HttpMasService.getInstance().postComment(loginInfo.mSchool_id, feedId, content, sendComment);
			}
			
			@Override
			public void onKeyBoardShow() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public View getRootView() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public void onCoolEmotionSelect(String emotion) {
				// TODO Auto-generated method stub
				
			}
		};
		
		mOnProximityListener = new OnProximityChangeListenner() {
			
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
		   
		};
		
		mOnSwitchPlayListener = new OnSwitchPlayModeListenner() {
			
			 /**
		     * 选择播放模式相应的事件实现，实现的是onswitchplaymodeListenner接口。
		     */
			@Override
			public void onOpen() {
				 if (!mIsOpenReceiver) {
			            mSwitchMode.registorSensor();
			        }
			        RenrenChatApplication.getUiHandler().post(new Runnable() {
			            public void run() {
			                keepScreenOn();
			            }
			        });
			}

			@Override
			public void onClose() {
				RenrenChatApplication.getUiHandler().post(new Runnable() {
			            public void run() {
			                stopKeepScreenOn();
			            }
			        });
			        if (!mIsOpenReceiver) {
			            mSwitchMode.unregisterSensor();
			            initVoiceSetting();
			        }
			}
		};
	}
	@Override
	public void onLoadData() {
		//getFeedListFromNet(false);
		getFeedListFromNet(true, 0, true);
	}

	@Override
	public void onFinishLoad() {
		mFeedView.mFeedAdapter.refresh();
	}

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    	VoiceManager.getInstance().stopAllPlay();
    }

    
    
    @Override
    public void onDestroyData() {
    	mFeedView.mFeedAdapter.mModelList.clear();
    	mFeedView.mFeedAdapter.mModelList = null;
    }

    @Override
    public HomeFragment.OnActivityResultListener onActivityResult() {
    	OnActivityResultListener resultListener = new OnActivityResultListener() {
			
			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				Logger.logd("man", "onActivityResult start"+requestCode+"|"+resultCode+"|"+data);
				if(data == null) return;
				if(requestCode == ACTIVITY_REQUEST_CODE){
					List<FeedCommentContent> commentList = (List<FeedCommentContent>)data.getSerializableExtra("commentlist");
					if(commentList == null||commentList.size() == 0)
						return;
					Logger.logd("man", "onActivityResult commentList:"+commentList.size());
					FeedModel model = (FeedModel)mFeedView.mFeedAdapter.getItem(FeedAdapter.mItemPosition);
					model.addCommentCount(commentList.size());
					model.setmChatFeedCommentContent(commentList);
					mFeedView.mFeedAdapter.refresh();
				}
				
			}
		};
        return resultListener;
    }

	/**
	 * 听筒切换的相应事件，实现，实现的是onproximityChangeListenner 接口。
	 */
	private boolean mEnable=true;
	
	
	//初始化声音设置。
	 private void initVoiceSetting() {
		 RenrenChatApplication.getUiHandler().post(new Runnable() {
	            public void run() {
	                mRoot_unAble_View.setVisibility(View.GONE);
	            }
	        });
	        if (mIsOpenReceiver) {
	            PCMPlayerSetting.switchStreamType(AudioManager.STREAM_VOICE_CALL);
	            ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
	        } else {
	            PCMPlayerSetting.switchStreamType(AudioManager.STREAM_MUSIC);
	            ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);
	        }
	    }
	
	
	
	
	//保持屏幕常亮的操作。
	public void keepScreenOn() {
		mOldAttributeParams = ((Activity) mContext).getWindow().getAttributes().flags;
		((Activity) mContext).getWindow().setFlags(mOldAttributeParams, ~mOldAttributeParams);
		((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	//关闭屏幕常亮。
	public void stopKeepScreenOn() {
		((Activity) mContext).getWindow().setFlags(0, ~0);
		((Activity) mContext).getWindow().setFlags(mOldAttributeParams, mOldAttributeParams);
	}
	
	//输入框的发送事件。
			// TODO Auto-generated method stub
			
		
		
}

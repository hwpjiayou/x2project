package com.renren.mobile.x2.components.chat;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.components.chat.BaseChatListAdapter.OnNotifyCallback;
import com.renren.mobile.x2.components.chat.PluginAdapter.PluginListenter;
import com.renren.mobile.x2.components.chat.chatmessages.ChatMessagesActivity;
import com.renren.mobile.x2.components.chat.chatmessages.ChatMessagesView;
import com.renren.mobile.x2.components.chat.command.Command_Recoder;
import com.renren.mobile.x2.components.chat.face.ICanTalkable;
import com.renren.mobile.x2.components.chat.face.IMessageOnClickListener;
import com.renren.mobile.x2.components.chat.holder.ChatItemSelectHolder;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem;
import com.renren.mobile.x2.components.chat.message.ChatBaseItem.MESSAGE_TYPE;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper.OnLongClickCommandMapping;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_FlashEmotion;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Image;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Text;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper_Voice;
import com.renren.mobile.x2.components.chat.message.Subject;
import com.renren.mobile.x2.components.chat.net.ChatMessageSender;
import com.renren.mobile.x2.components.chat.util.AutoSwitchMode;
import com.renren.mobile.x2.components.chat.util.AutoSwitchMode.OnProximityChangeListenner;
import com.renren.mobile.x2.components.chat.util.ChatDataHelper;
import com.renren.mobile.x2.components.chat.util.ChatItemSelectPopupWindow;
import com.renren.mobile.x2.components.chat.util.ChatPaintThread;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.util.CurrentChatSetting;
import com.renren.mobile.x2.components.chat.util.DataPool;
import com.renren.mobile.x2.components.chat.util.IInputBarListenner;
import com.renren.mobile.x2.components.chat.util.ItemLongClickDialogProxy;
import com.renren.mobile.x2.components.chat.util.ItemLongClickDialogProxy.LONGCLICK_COMMAND;
import com.renren.mobile.x2.components.chat.util.ResendDialog;
import com.renren.mobile.x2.components.chat.util.ScrollingLock;
import com.renren.mobile.x2.components.chat.util.ThreadPool;
import com.renren.mobile.x2.components.chat.util.ToastAnimationListennerImpl;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread;
import com.renren.mobile.x2.components.chat.view.InputBar;
import com.renren.mobile.x2.components.chat.view.ListViewWarpper;
import com.renren.mobile.x2.components.chat.view.ListViewWarpper.OnDataChangeListener;
import com.renren.mobile.x2.components.chat.view.ListViewWarpper.OnFingerTouchListener;
import com.renren.mobile.x2.components.chat.view.VoiceView;
import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;
import com.renren.mobile.x2.components.imageviewer.ImageViewActivity;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.db.dao.ChatHistoryDAO;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener.SEND_TEXT_STATE;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PCMPlayerSetting;
import com.renren.mobile.x2.utils.voice.PlayerThread;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnSwitchPlayModeListenner;
import com.renren.mobile.x2.utils.voice.PlayerThread.PlayRequest;
import com.renren.mobile.x2.utils.voice.VoiceManager;
import com.renren.mobile.x2.view.ITitleBar;

public final class RenRenChatActivity extends
		BaseFragmentActivity<List<ChatMessageWarpper>> implements 
		OnNotifyCallback, 
		IInputBarListenner,
		OnFingerTouchListener,
		OnProximityChangeListenner,
		OnSwitchPlayModeListenner,
		PluginListenter,
		IMessageOnClickListener
		 {

	public ChatAdapter mChatListAdapter = new ChatAdapter(this);
	private List<ChatMessageWarpper> mLoadFromHistoryData;
	private boolean mIsLoadData = false;
	
	private static boolean isFromSetting =false;
	
	public Handler mHandler = new Handler();
	
    private   ICanTalkable mToChatUser  = null;
    private  long mLocalUserId;
    /*转发图片路径*/
    public String mForwardFilePath;
    
    /* 绘制线程 */
    ChatPaintThread mPaintThread = null;
    
    /* (产品要求)进来默认加载10条 */
    private static final int PAGE_SIZE = 10;

	private LinearLayout mRoot_Linearlayout = null;
	private ITitleBar mRoot_Title = null;
	/** 提示切换成听筒或扬声器 */
	private LinearLayout mRoot_Switch_Toast = null;
	/**不可点击的浮层 */
	private LinearLayout mRoot_unAble_View = null;
	/**聊天的list */
	public ListViewWarpper mRoot_ChatList = null;
	/** 最底层的一排按钮 */
	public  InputBar mRoot_InputBar = null;
	/** 录音时候中间弹出提示*/
	public VoiceView mRoot_VoiceView = null;
	/**中间内容的布局*/
	private View mRoot_FrameLayout;
    /**切换到扬声器或听筒时的文本 */
	private TextView mSwitchTextView;
	
	
	/* 用于记录不锁屏前的window性质 */
	public int mOldAttributeParams = 0;
	
	/* 命令 */
	private  Command_Recoder     mRecord_Command      = new Command_Recoder(this);
	//public  Command_Brush       mBrush_Command       = new Command_Brush(this);
	
    /* 回调管理器,该Activity的部分回调都在这个类里面 */
   // private             ChatCallbackManager mCallbackManager     = new ChatCallbackManager(this);
    public  ItemLongClickDialogProxy mLongClickDialogProxy = null;
    /* 重发弹出窗口 */
    public  ResendDialog             mResendDialog         = null;
    /* 播放模式装换器 */
    public  AutoSwitchMode           mSwitchMode           = null;
    /*开启扬声器开关*/
    private boolean                  mIsOpenReceiver       = false;
    
    
    /* 弹出条目选择界面 */
    public              ChatItemSelectPopupWindow mChatItemSelectPopWindow = null;
    
    ToastAnimationListennerImpl mToastImpl = new ToastAnimationListennerImpl();

	public static final int EMOTIONEDITTEXT_LENGTH = 240;

	@Override
	protected ChatFrament onCreateContentFragment() {
		getData();
		ChatFrament mainFrament = new ChatFrament(this);
		initView(mainFrament.getRootView());
		return mainFrament;
	}

	private void getData() {
		mToChatUser =  DataPool.getTmpCanTalkable();
		if(mToChatUser == null || mToChatUser.getUId() == 0){
			if(Logger.mDebug){
				Logger.errord("没人可对话");
			}
			this.finish();
			return;
		}
		if(TextUtils.isEmpty(LoginManager.getInstance().getLoginInfo().mUserId)){
			if(Logger.mDebug){
				Logger.errord("登陆账号的id没有取到！！！");
			}
			this.finish();
			return;
		}
		mLocalUserId = Long.parseLong(LoginManager.getInstance().getLoginInfo().mUserId);
		ChatHistoryDAO dao = DAOFactoryImpl.getInstance().buildDAO(ChatHistoryDAO.class);
        mChatListAdapter.setToChatUserId(mToChatUser.getUId());
        mChatListAdapter.setDefaultHeadUrl(mToChatUser.getHeadUrl());
        mChatListAdapter.attachToDAO(dao);
        this.mForwardFilePath = DataPool.getmForwardFilePath();
        
        mSwitchMode = new AutoSwitchMode(this);
        mSwitchMode.setOnProximityChangeListenner(this);
        VoiceManager.getInstance().setOnSwitchPlayerListenner(this);
	}

	private void initView(View rootView) {
		if (Logger.mDebug) {
			Logger.mark();
		}
		mRoot_Linearlayout = (LinearLayout) rootView.findViewById(R.id.cdw_chat_main_root);
		mRoot_Title = getTitleBar();
		mRoot_Switch_Toast = (LinearLayout) rootView.findViewById(R.id.cdw_chat_switch_toast);
		mRoot_unAble_View = (LinearLayout) rootView.findViewById(R.id.cdw_chat_main_unable_view);
		mRoot_ChatList = (ListViewWarpper) rootView.findViewById(R.id.cdw_chat_main_chatlist_baselistview);
		mRoot_InputBar = (InputBar) rootView.findViewById(R.id.chatmain_inputbar);
		mRoot_VoiceView = (VoiceView) rootView.findViewById(R.id.chatmain_voiceview);
		mRoot_FrameLayout = rootView.findViewById(R.id.id_framelayout);
		mSwitchTextView = (TextView) rootView.findViewById(R.id.cdw_chat_switch_toast_text);
		mRoot_ChatList.setAdapter(mChatListAdapter);
		
		
		mRoot_InputBar.updateHit(R.string.InputBar_Hit_1);
        mRoot_ChatList.hideHeader();
        mRoot_unAble_View.setVisibility(View.GONE);
        mRoot_unAble_View.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (mChatListAdapter != null) {
            mChatListAdapter.clear();
        }
        mLongClickDialogProxy = new ItemLongClickDialogProxy(this);
        mResendDialog = new ResendDialog(this);
        mChatItemSelectPopWindow = new ChatItemSelectPopupWindow(this,
                getRootView(),
                new ChatItemSelectHolder(this, R.layout.chat_item_choice_popwindow));
      //  mCallbackManager.init();
        mChatListAdapter.setNotifyCallback(this);
        mRoot_ChatList.setAdapter(mChatListAdapter);
        
        mRoot_Title.setTitle(mToChatUser.getName());
        mRoot_Title.setRightAction(R.drawable.chat_title_info_selector,new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RenRenChatActivity.this,ChatMessagesActivity.class);
				intent.putExtra(ChatMessagesView.USER_HEAD,mToChatUser.getHeadUrl());
				intent.putExtra(ChatMessagesView.USER_NAME,mToChatUser.getName());
				intent.putExtra(ChatMessagesView.USER_TOID,mToChatUser.getUId());
				RenRenChatActivity.this.startActivity(intent);
			}
        });
        mRoot_Title.setLeftAction(R.drawable.title_back, new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
        mRoot_InputBar.registerInputBarLisenner(this);
        mRoot_VoiceView.monitorView(mRoot_InputBar.mView_VoiceRecord);
        mRoot_ChatList.setOnFingerTouchListener(this);
        mRoot_VoiceView.setMonitorListener(mRecord_Command);
        mRoot_VoiceView.setOnPreTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    mRecord_Command.onStartCommand();
                }
                return true;
            }
        });
        
        
        mRoot_ChatList.setOnDataChangeListener(new OnDataChangeListener() {
            public void onDataLoadStart() {
            	mRoot_ChatList.disable();
                queryHistory();
                CommonUtil.waitTime(100);
                ThreadPool.obtain().executeMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mRoot_ChatList.onLoadDataOver();
                    }
                });
                mRoot_ChatList.enable();
            }

            public void onDataChange() {
                mChatListAdapter.addChatMessageAndNotifyFromHead(mLoadFromHistoryData, false);
                mChatListAdapter.notifyDataSetInvalidated();

                mIsLoadData = mLoadFromHistoryData.size() >= PAGE_SIZE;
                if (!mIsLoadData) {
                    mRoot_ChatList.addHeader();
                }
                mRoot_ChatList.setSelectionFromTop(mLoadFromHistoryData.size(), 0);
                mLoadFromHistoryData.clear();

            }

            public boolean onIsLoadData() {
                return mIsLoadData;
            }
        });
//        mRoot_ChatList.setHeaderOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) { //同步历史聊天记录
////                showDialog();
////                C_Syn.getInstance().syn(
////                        (mToChatUser.isGroup() != MESSAGE_ISGROUP.IS_SINGLE),
////                        mToChatUser.getUId(),
////                        mToChatUser.getName(),
////                        RenRenChatActivity.this);
//            }
//        });
        
     //   ChatDataHelper.getInstance().registorOnSupportFeedListener(this);
        //Log.d(TAG, "@onNewIntent RoomInfosData");
     //   mIsGetMessage = intent.getBooleanExtra(PARAM_NEED.IS_GET_MESSAGE, false);
        //Log.d(TAG, "@onNewIntent IS_GET_MESSAGE");
        PluginAdapter adapter = new PluginAdapter();
        adapter.register(this);
        //Log.d(TAG, "@onNewIntent PluginAdapter");
        mRoot_InputBar.setAdapter(adapter);
	}
	
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        if (mPaintThread == null) {
	            mPaintThread = new ChatPaintThread(this);
	            mPaintThread.start();
	        }
	        CurrentChatSetting.CHAT_ID = this.mToChatUser.getUId();
	        CurrentChatSetting.onChatActivityResume();
	        this.mRecord_Command.mIsSend = true;
	        ThreadPool.obtain().execute(new Runnable() {
	            public void run() {
	                ChatDataHelper.getInstance().updateMessageByUserId(mToChatUser.getUId());
	            }
	        });
	        ScrollingLock.unlock();
	        if(Logger.mDebug){
	        	Logger.mark();
	        }
	    }
	  @Override
	    protected void onPause() {
	        super.onPause();
	        VoiceDownloadThread.getInstance().stopToAutoPlay();
	        VoiceManager.getInstance().stopAllPlay();
	        VoiceManager.getInstance().stopRecord(false);
	        mRecord_Command.mIsSend = false;
	        mLongClickDialogProxy.dismiss();
	        CurrentChatSetting.onChatActivityStop();
	        mRoot_VoiceView.hide();
	        ScrollingLock.lock();
	        if(Logger.mDebug){
	        	Logger.mark();
	        }
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	try {
//	    		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
//	                mLoadingDialog.dismiss();
//	            }
	            mChatListAdapter.distachToDAO();
	            mChatListAdapter.clear();
	            mChatListAdapter.resetData();
	            System.gc();
	           // RenrenChatApplication.unregistorChatStateCallback(this);
	          VoiceManager.getInstance().unregistorSwitchPlayerListenner(this);
	            mPaintThread.stopPaint();
	            mPaintThread = null;
	           // RenrenChatApplication.popStack(this);
	          //  RenrenChatApplication.sForwardMessage = null;
	          //  EmotionManager.getInstance().clearEmotionCallBack(); <TODO cf> 问小超是否要清理
			} catch (Exception e) {}
	        super.onDestroy();
	    }
	    
	  
	    
	 public static void show(Context context, ICanTalkable contactModel, boolean isSetting) {
		 isFromSetting= isSetting;
		 show(context, contactModel);
	}
	    
	public static void show(Context context, ICanTalkable contactModel) {
		if (Logger.mDebug) {
			Logger.logd("contactModel name=" + contactModel.getName()+ "#id=" + contactModel.getUId() + "#headurl="+ contactModel.getHeadUrl());
		}
		DataPool.clear();
		DataPool.setTmpCanTalkable(contactModel);
		Intent intent = new Intent(context, RenRenChatActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void show(Context context, ICanTalkable contactModel,
			ChatMessageWarpper autoMessage) {
		if (Logger.mDebug) {
			Logger.logd("contactModel name=" + contactModel.getName()+ "#id=" + contactModel.getUId() + "#headurl="+ contactModel.getHeadUrl());
		}
		DataPool.clear();
		DataPool.setTmpCanTalkable(contactModel);
		DataPool.setAddedMessage(autoMessage);
		Intent intent = new Intent(context, RenRenChatActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	 protected void queryHistory() {
            mLoadFromHistoryData = ChatDataHelper.getInstance()
                    .queryChatHistory(mLocalUserId,
                    		mToChatUser.getUId(),
                            PAGE_SIZE,
                            mChatListAdapter.getEarlyId());
            if (Logger.mDebug) {
            	if(mLoadFromHistoryData==null){
            		Logger.mark(" no history");
            	}else{
            		Logger.mark("size="+mLoadFromHistoryData.size());
            	}
			}

          //  ChatDataHelper.getInstance().getFalseData();     
	}


	 @Override
	    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
	        //SystemUtil.log("show", "0:"+keyCode);
	        switch (keyCode) {
	            case KeyEvent.KEYCODE_BACK:
//	            	if(hideDialog()){
//	            		SystemUtil.log("show", "1");
//	            		return true;
//	            	}
//	            	if(hideVoiceView()){
//	            		SystemUtil.log("show", "2");
//	            		return true;
//	            	}
	                if (mRoot_InputBar.onBack()) {
	                	Logger.logd("onback");
	                    return true;
	                } 
	                else {
	                	Logger.logd(" no  onback");
	                	//MainFragmentActivity.show(this, MainFragmentActivity.Tab.SIXIN);
	                    return super.onKeyDown(keyCode, event);
	                }
	            case KeyEvent.KEYCODE_MENU:
	                break;
	        }
	        return false;
	    }
	 
//    public class QueryHistoryRunnable implements Runnable {
//        public CanTalkable mCanTalkModel = null;
//        public boolean mIsNotify;
//
//        public QueryHistoryRunnable(CanTalkable model, boolean isNotify) {
//            mCanTalkModel = model;
//            this.mIsNotify = isNotify;
//        }
//
//        public void run() {}
//    }
    
    protected void addToAdapter(List<ChatMessageWarpper> list, boolean isNotify) {
    	if (Logger.mDebug) {
			Logger.mark();
		}
        ThreadPool.obtain().executeMainThread(new InsertAdapterRunnable(list, isNotify));
    }

	    public class InsertAdapterRunnable implements Runnable {
	        List<ChatMessageWarpper> mList = null;
	        public boolean mIsNotify;

	        public InsertAdapterRunnable(List<ChatMessageWarpper> list, boolean isNotify) {
	            mList = list;
	            this.mIsNotify = isNotify;
	            mIsLoadData = mList.size() >= PAGE_SIZE;
	        }

        @Override
        public void run() {
            mChatListAdapter.addChatMessageAndNotifyFromHead(mList, false);
            mChatListAdapter.notifyDataSetInvalidated();
            if (!mIsLoadData) {
                mRoot_ChatList.addHeader();
            }
            mRoot_ChatList.setSelection(mList.size());
			// if (RenrenChatApplication.sForwardMessage != null) {
			// final ChatMessageWarpper m =
			// RenrenChatApplication.sForwardMessage;
			// RenrenChatApplication.sForwardMessage = null;
			// m.mGroupId = mToChatUser.getUId();
			// m.mIsGroupMessage = mToChatUser.isGroup();
			// m.mUserName = mToChatUser.getName();
			// m.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT;
			// m.mToChatUserId = mToChatUser.getUId();
			// m.mHeadUrl = mToChatUser.getHeadUrl();
			// m.mDomain = mToChatUser.getDomain();
			// m.mMessageReceiveTime = -1L;
			// m.mMessageKey = "";
			// ChatMessageSender.getInstance().sendMessageToNet(m, true);
			// }
			// if (mForwardFilePath != null) {
			// mLocalSelect_Command.sendMessage(mForwardFilePath);
			// mForwardFilePath = null;
			// }
			// if (mNewsfeedMessage != null) {
			// mChatListAdapter.setFeed(mNewsfeedMessage,
			// RenRenChatActivity.this);
			// mRoot_InputBar.updateHit(R.string.InputBar_Hit_2);
			// }
            mRoot_ChatList.enable();
        }
    }
	

	@Override
	protected void onPreLoad() {
		if (Logger.mDebug) {
			Logger.mark();
		}
	}

	@Override
	protected void onFinishLoad(List<ChatMessageWarpper> data) {
		if (Logger.mDebug) {
			Logger.mark("data size=" + data.size());
		}
		//mChatListAdapter.setdata(data);
		//mChatListAdapter.notifyDataSetChanged();
		
		 if (mLoadFromHistoryData != null && mLoadFromHistoryData.size() > 0) {
             addToAdapter(mLoadFromHistoryData, true);
         } else {
//         	final ChatMessageWarpper m = DataPool.getAddedMessage();
//             if ( m != null) {
//                 DataPool.setAddedMessage(null);
//                 m.mGroupId = mToChatUser.getUId();
//                 m.mIsGroupMessage = 1;
//                 m.mUserName = mToChatUser.getName();
//                 m.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT;
//                 m.mToChatUserId = mToChatUser.getUId();
//                 m.mHeadUrl = mToChatUser.getHeadUrl();
//                 m.mMessageReceiveTime = -1L;
//                 m.mMessageKey = "";
//                 ChatMessageSender.getInstance().sendMessageToNet(m, true);
//             }
//             if (mForwardFilePath != null) {
//                 mLocalSelect_Command.sendMessage(mForwardFilePath);
//                 mForwardFilePath = null;
//             }
//             if (mNewsfeedMessage != null) {
//                 mChatListAdapter.setFeed(mNewsfeedMessage, RenRenChatActivity.this);
//                 mRoot_InputBar.updateHit(R.string.InputBar_Hit_2);
//             }
             if (Logger.mDebug) {
 				Logger.mark();
 			 }
             ThreadPool.obtain().executeMainThread(new Runnable() {
                 @Override
                 public void run() {
                 	 mRoot_ChatList.addHeader();
                 }
             });
         }
	}

	@Override
	protected List<ChatMessageWarpper> onLoadInBackground() {
		if (Logger.mDebug) {
			Logger.mark();
		}
		queryHistory();
		if(isFromSetting && (mLoadFromHistoryData == null || mLoadFromHistoryData.size()==0)){
         	ChatMessageWarpper_Text message =new ChatMessageWarpper_Text();
			message.mLocalUserId = Long.valueOf(LoginManager.getInstance().getLoginInfo().mUserId);
			message.parseUserInfo(mToChatUser);
			message.mMessageContent = "x22222";
			message.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL;
         	ChatDataHelper.getInstance().insertToTheDatabase(message);
//         	ChatMessageWarpper_Voice voiceMessage =new ChatMessageWarpper_Voice();
//         	voiceMessage.mMessageContent =	"" ;
         }
		isFromSetting = false;
		return mLoadFromHistoryData;
	}

	@Override
	protected void onDestroyData() {
		if (Logger.mDebug) {
			Logger.mark();
		}
	}
	
	@Override
	public void onNotify(int count) {
		if (Logger.mDebug) {
			Logger.mark();
		}
		mRoot_ChatList.setSelection(count);
	}

	@Override
	public void onAllDataDelete() {
		if (Logger.mDebug) {
			Logger.mark();
		}
		ThreadPool.obtain().executeMainThread(new Runnable() {
			public void run() {
				mRoot_ChatList.addHeader();
			}
		});
		mIsLoadData = false;
	}
	
	
	 
	 @Override
     public void onFingerTouch() {
	       mRoot_InputBar.onBack();
	  }
	 
	 @Override
	 public void onKeyBoardShow() {
	        mRoot_ChatList.setSelection(mChatListAdapter.getCount() - 1);
	 }

	 @Override
	    public void onTyping() {
//	        // TODO Auto-generated method stub
//	        if (this.mToChatUser.isGroup() != CanTalkable.GROUP.GROUP.Value) {
//	            StateMessageSender.getInstance().send(
//	                    this.mToChatUser.getName(),
//	                    this.mToChatUser.getLocalUId(),
//	                    this.mToChatUser.getUId(),
//	                    this.mToChatUser.getDomain(),
//	                    StateMessageModel.STATE_TYPE.TYPING);
//	        }
	    }

	    @Override
	    public void onTypingCancel() {
//	        // TODO Auto-generated method stub
//	        if (this.mToChatUser.isGroup() != CanTalkable.GROUP.GROUP.Value) {
//	            StateMessageSender.getInstance().send(
//	                    this.mToChatUser.getName(),
//	                    this.mToChatUser.getLocalUId(),
//	                    this.mToChatUser.getUId(),
//	                    this.mToChatUser.getDomain(),
//	                    StateMessageModel.STATE_TYPE.CANCELED);
//	        }
	    }
	    
    @Override
    public void onViewShow() {
        ThreadPool.obtain().executeMainThread(new Runnable() {
            public void run() {
                onKeyBoardShow();
            }
        });
    }
    
    /**
     * 表情选中
     */
    @Override
    public void onEmotionSelect(String emotion) {
        int selectNum = mRoot_InputBar.mView_TextEdit
                .getSelectionEnd();
        int num = selectNum + emotion.length();
        if (num <= EMOTIONEDITTEXT_LENGTH) {
            mRoot_InputBar.mView_TextEdit.insertEmotion(emotion);
            mRoot_InputBar.mView_TextEdit.setSelection(selectNum
                    + emotion.length());
        } 
    }

    /**
     * 删除表情
     */
    @Override
    public void mDelBtnClick() {
        mRoot_InputBar.mView_TextEdit.delLastCharOrEmotion();
    }
    
    @Override
    public void onChangeLayout(int w, int h) {
        mRoot_FrameLayout.measure(w, h);
        mRoot_FrameLayout
                .layout(0, 0, mRoot_FrameLayout.getWidth(), mRoot_FrameLayout.getHeight());
    }

    @Override
    public View getRootView() {
        return mRoot_Linearlayout;
    }
    
    @Override
    public void onCloseEar() {
        ThreadPool.obtain().executeMainThread(new Runnable() {
            public void run() {
                mEnable = false;
                mRoot_unAble_View.setVisibility(View.VISIBLE);
                mSwitchTextView.setText(R.string.chat_headphone);
                mToastImpl.toast(mRoot_Switch_Toast);
            }
        });
    }

    private boolean mEnable = true;

    @Override
    public void onOverEar() {
        if (!mEnable) {
            ThreadPool.obtain().executeMainThread(new Runnable() {
                public void run() {
                    mEnable = true;
                    mRoot_unAble_View.setVisibility(View.GONE);
                    mSwitchTextView.setText(R.string.chat_speaker);
                    mToastImpl.toast(mRoot_Switch_Toast);
                }
            });
        }
    }
    
    @Override
    public void onOpen() {
    	if(Logger.mDebug){
    		Logger.logd(Logger.PLAY_VOICE,"switchmode onpen mIsOpenReceiver="+mIsOpenReceiver);
    	}
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
    	if(Logger.mDebug){
    		Logger.logd(Logger.PLAY_VOICE,"switchmode onClose mIsOpenReceiver="+mIsOpenReceiver);
    	}
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

    private void initVoiceSetting() {
        mHandler.post(new Runnable() {
            public void run() {
                mRoot_unAble_View.setVisibility(View.GONE);
            }
        });
        if (mIsOpenReceiver) {
            PCMPlayerSetting.switchStreamType(AudioManager.STREAM_VOICE_CALL);
            this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        } else {
            PCMPlayerSetting.switchStreamType(AudioManager.STREAM_MUSIC);
            this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }


	public ICanTalkable getmToChatUser() {
		return mToChatUser;
	}
	
	public long getmLocalUserId() {
		return mLocalUserId;
	}
	
	public void keepScreenOn() {
		mOldAttributeParams = getWindow().getAttributes().flags;
		getWindow().setFlags(mOldAttributeParams, ~mOldAttributeParams);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	public void stopKeepScreenOn() {
		getWindow().setFlags(0, ~0);
		getWindow().setFlags(mOldAttributeParams, mOldAttributeParams);
	}

	

	/////////////////////////消息点击事件处理/////////////////////////
	@Override
	public void onClick(ChatMessageWarpper message) {
		if(message==null){
			return ;
		}
		if(Logger.mDebug){
			Logger.logd("消息被点击  type="+message.mMessageType);
		}
		switch (message.mMessageType) {
			case MESSAGE_TYPE.VOICE:
				clickVoice((ChatMessageWarpper_Voice) message);
				return ;
			case MESSAGE_TYPE.IMAGE:
				clickImage((ChatMessageWarpper_Image) message);
				return ;
		}
	}

	private void clickImage(ChatMessageWarpper_Image chatmessage ) {
		if(chatmessage.mComefrom==ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL){
			ImageViewActivity.show(RenRenChatActivity.this,chatmessage.mMessageContent,true);
		}else{
			String imgPath = null;
			if (Logger.mDebug) {
				Logger.logd("image item click path="+chatmessage.mMessageContent);
			}
			if(chatmessage.mMessageContent.endsWith(".jpg")){
				imgPath = chatmessage.mMessageContent.substring(0, chatmessage.mMessageContent.length()-4);
			}else{
				imgPath = chatmessage.mMessageContent;
			}
			File file = new File(imgPath);
			if(file.exists()){
				ImageViewActivity.show(RenRenChatActivity.this,chatmessage.mMessageContent,true);
			}else{
				if(chatmessage.mLargeUrl!=null){
					ImageViewActivity.show(RenRenChatActivity.this, chatmessage.mLargeUrl, false);
				}else{
					CommonUtil.toast(R.string.chat_imgfile_lost);		//ImageItemOnClickListenner_java_1=图片文件丢失; 
				}
			}
		}
	}

	private void clickVoice(final ChatMessageWarpper_Voice chatmessage) {
		if(chatmessage.mSendTextState ==SEND_TEXT_STATE.SEND_PREPARE 
				&& 
			chatmessage.mComefrom == ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL){
			VoiceDownloadThread.getInstance().stopToAutoPlay(chatmessage);
			return;
		}
		File file = new File(chatmessage.mMessageContent);
		if(Logger.mDebug){
			Logger.logd("音频文件地址="+chatmessage.mMessageContent);
		}
		if(file.exists()&& file.length()>0 ){
			final PlayRequest request = new PlayRequest();
			request.mAbsVoiceFileName = chatmessage.mMessageContent;
			request.mPlayListenner = chatmessage;
			if (chatmessage.mMessageState!=Subject.COMMAND.COMMAND_PLAY_VOICE_OVER) {
				VoiceDownloadThread.getInstance().stopToAutoPlay(chatmessage);
			}else{
				VoiceDownloadThread.getInstance().stopToAutoPlay();
			}
			int state = chatmessage.mMessageState;
			if(PlayerThread.getInstance().forceToPlay(request) && state==Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_OVER){
				PlayerThread.getInstance().onAddPlay(request);
			};
		}else{
			/*外部发过来的错误处理*/
			if(chatmessage.mComefrom==ChatBaseItem.MESSAGE_COMEFROM.OUT_TO_LOCAL){
				if(chatmessage.mMessageState==Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_ERROR){
					AlertDialog.Builder builder = 
							new AlertDialog.Builder(this);
					AlertDialog dialog = builder.setMessage(ChatUtil.getText(R.string.chat_whether,R.string.chat_redownload))		//VoiceOnClickListenner_java_2=是否重新下载; 
						.setPositiveButton(ChatUtil.getText(R.string.ok),new DialogInterface.OnClickListener(){		//VoiceOnClickListenner_java_3=确定; 
							public void onClick(DialogInterface dialog,
									int which) {
								chatmessage.download(true);
							}
						}).setNegativeButton(ChatUtil.getText(R.string.cancel), null).create();		//ChatMessageWarpper_FlashEmotion_java_4=取消; 
					dialog.show();
				}else{
					chatmessage.download(true);
				}
			}else {
				CommonUtil.toast(R.string.chat_voicefile_lost);		//VoiceOnClickListenner_java_4=本地音频文件丢失; 
			}
		}
	}

	@Override
	public void onLongClick(ChatMessageWarpper message) {
		if(message==null){
			return ;
		}
		if(Logger.mDebug){
			Logger.logd("消息被长按  type="+message.mMessageType);
		}
		show(message);
	}

	@Override
	public void onReSendClick(ChatMessageWarpper message) {
		if(message==null){
			return ;
		}
		if(Logger.mDebug){
			Logger.logd("消息重发  type="+message.mMessageType);
		}
		mResendDialog.update(message);
	}
	
	public void show(final ChatMessageWarpper message) {
		AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
		final List<OnLongClickCommandMapping> list = message.getOnClickCommands();
		if(list==null){
			return;
		}
		final String[] texts = new String[list.size()];
		int i = 0;
		for (OnLongClickCommandMapping m : list) {
			texts[i++] = m.mText;
		}
		AlertDialog mDialog = mDialogBuilder.setItems(texts, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				OnLongClickCommandMapping m = list.get(which);
				processCommand(m.mCommand,message);
			}
		}).create();
		mDialog.show();
	}
	
	private void processCommand(LONGCLICK_COMMAND command,ChatMessageWarpper message) {
		switch (command) {
		case DELETE:
			deleteMessage(message);
			break;
		case COPY:
			copyMessage(message);
			break;
		case RESEND:
			resendMessage(message);
			break;
		case REDOWNLOAD:
			redownloadMessage(message);
			break;
		}
	}

	/* 删除消息 */
	public void deleteMessage(ChatMessageWarpper message) {
		ChatDataHelper.getInstance().deleteToTheDatabase(message);
		message.onDelete();
	}

	/* 复制 */
	public void copyMessage(ChatMessageWarpper message) {
		ChatDataHelper.getInstance().copyTheMessage(this, message.getMessageContent());

	}

	/* 重新发送 */
	public void resendMessage(ChatMessageWarpper message) {
	   mResendDialog.update(message);
	}

	/* 重新下载 */
	public void redownloadMessage(final ChatMessageWarpper message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog dialog = builder.setMessage(ChatUtil.getText(R.string.chat_whether,R.string.chat_redownload)).setPositiveButton(ChatUtil.getText(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				message.download(true);
			}
		}).setNegativeButton(ChatUtil.getText(R.string.cancel), null).create();
		dialog.show();
	}
    /////////////////////end 消息点击事件处理//////////////////////////////////////////

	/////////////////////消息发送相关////////////////////////////////////////////////////////////
	@Override
	public void onCoolEmotionSelect(String emotion) {
		if(Logger.mDebug){
			ErrLog.ll("onCoolEmotionSelected " +emotion);
			Logger.logd(Logger.SEND_TEXT,emotion);
		}
		 ChatMessageWarpper_FlashEmotion flashMessage = new ChatMessageWarpper_FlashEmotion();
		        flashMessage.mMessageContent = emotion;
		        flashMessage.parseUserInfo(mToChatUser);
		        flashMessage.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT;
		        flashMessage.mLocalUserId = Long.valueOf(LoginManager.getInstance().getLoginInfo().mUserId);
		        ChatMessageSender.getInstance().sendMessageToNet(flashMessage, true);
	}
	
	@Override
	public void onSend(String msg) {
		if (Logger.mDebug) {
			Logger.logd(Logger.SEND_TEXT, "send message=" + msg);
		}
		if (!TextUtils.isEmpty(msg)) {
			ChatMessageWarpper_Text message = new ChatMessageWarpper_Text();
			message.mLocalUserId = getmLocalUserId();
			message.parseUserInfo(getmToChatUser());
			message.mMessageContent = msg;
			message.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT;
			ChatMessageSender.getInstance().sendMessageToNet(message, true);
			clearInputText();// 清空输入框
		}
	}

	private void clearInputText() {
		mRoot_InputBar.mView_TextEdit.setNotNotify();
		mRoot_InputBar.setText("");
	}
	
	@Override
	public void startPlugin(byte type, Bundle mBundle) {
		switch (type) {
		case PluginListenter.PLUGIN_SELECT_PHOTO:
			startSelectPhoto();
			break;
		case PluginListenter.PLUGIN_TAKE_PHOTO:
			startTakePhoto();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case ImageViewActivity.TAKE_PHOTO:
			endTakePhoto();
			break;
		case ImageViewActivity.SELECT_PHOTO:
			endSelcetPhoto();
			break;
		}
	}
	
	private String mTmpTakeFile = null;
	private void startTakePhoto(){
		if(!DeviceUtil.getInstance().isSDCardHasEnoughSpace()){
			DeviceUtil.getInstance().toastNotEnoughSpace();
			return;
		}
		Intent intent = new Intent(this,ImageViewActivity.class);
		mTmpTakeFile = ChatUtil.getUserPhotos(getmToChatUser().getUId())+
				"renren_"+ String.valueOf(System.currentTimeMillis()); 
		intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_LOCAL_URI, mTmpTakeFile);
		intent.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.TAKE_PHOTO);
		startActivityForResult(intent, ImageViewActivity.TAKE_PHOTO);
	}
	
	private void endTakePhoto(){
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO,"onEndCommand");
		}
		ChatMessageWarpper_Image message =new ChatMessageWarpper_Image();
		message.mLocalUserId = Long.valueOf(LoginManager.getInstance().getLoginInfo().mUserId);
		message.mMessageContent = mTmpTakeFile;
		message.parseUserInfo(getmToChatUser());
		message.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT;
		String imgPath = mTmpTakeFile;
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO,"imagePath="+imgPath);
		}
		File file =  new File(imgPath);
		byte[] bytes = new byte[(int)file.length()];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			fis.read(bytes);
			fis.close();
		} catch (Exception e) {
			if(Logger.mDebug){
				Logger.errord(e.toString());
				e.printStackTrace();
			}
		}
		ChatDataHelper.getInstance().insertToTheDatabase(message);
		ChatMessageSender.getInstance().uploadData(message, bytes);		
	}
	
	private String mTmpSelectFile = null;
	private void startSelectPhoto(){
		Intent intent = new Intent(this,ImageViewActivity.class);
		intent.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.SELECT_PHOTO);
		mTmpSelectFile = ChatUtil.getUserPhotos(getmToChatUser().getUId())+
				"renren_"+ String.valueOf(System.currentTimeMillis()); 
		intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_LOCAL_URI, mTmpSelectFile);
		startActivityForResult(intent, ImageViewActivity.SELECT_PHOTO);
	}
	
	private void endSelcetPhoto(){
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO,"onEndCommand");
		}
		ChatMessageWarpper_Image message =new ChatMessageWarpper_Image();
		message.mLocalUserId = Long.parseLong(LoginManager.getInstance().getLoginInfo().mUserId);
		message.mToChatUserId = getmToChatUser().getUId();
		message.mMessageContent = mTmpSelectFile;
		message.mUserName = getmToChatUser().getName();
		message.mComefrom = ChatBaseItem.MESSAGE_COMEFROM.LOCAL_TO_OUT;
		message.mHeadUrl = getmToChatUser().getHeadUrl();
		String imgPath = mTmpSelectFile;
		if(Logger.mDebug){
			Logger.logd(Logger.SEND_PHOTO,"imagePath="+imgPath);
		}
		File file =  new File(imgPath);
		byte[] bytes = new byte[(int)file.length()];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			fis.read(bytes);
			fis.close();
		} catch (Exception e) {}
		if(bytes.length==0){
			if(Logger.mDebug){
				Logger.errord("发送图片失败");
			}
			return;
		}
		ChatDataHelper.getInstance()
						.insertToTheDatabase(message);
		ChatMessageSender.getInstance()
						.uploadData(message, bytes);
	}
	
   //////////////////end 消息发送////////////////////////////////////////////////////////////
}

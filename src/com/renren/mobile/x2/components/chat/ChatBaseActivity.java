package com.renren.mobile.x2.components.chat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author dingwei.chen
 * @说明  聊天界面Activity基类,负责注册全局量和其他功能
 * */
public abstract class ChatBaseActivity extends FragmentActivity {

	protected Set<String> mDataNeed = new HashSet<String>();
	public static final String INTENT_DATA_MAP = "INTENT_DATA_MAP";
	public static Handler sHandler = new Handler();
	protected Map<String,Object> mData = null;
	public Handler mHandler = new Handler();
	protected boolean mIsStartPaint = false;
	
	/* (产品要求)进来默认加载10条 */
	public static final int PAGE_SIZE = 10;
	/* 用于记录不锁屏前的window性质 */
	public int mOldAttributeParams = 0;
	
	/* Loading阻塞框 */
	public ProgressDialog mLoadingDialog = null;
	
	public ChatBaseActivity(){
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mLoadingDialog = new ProgressDialog(this);
	};
	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		this.registorGlobalValue();//注册全局量
	}
	/**
	 * 注册全局量
	 * */
	private void registorGlobalValue(){
		//GlobalValue.registorCurrentActivity(this);
	}

	
	public void addParam(String paramName){
		mDataNeed.add(paramName);
	}
	
	/* 加入判断录音和播放都消失 */
	@Override
	protected void onStop() {
		super.onStop();
	//	GlobalValue.getInstance().sIsChatActivityTop = false;
	//	GlobalValue.getInstance().sChatAdapter = null;
	//	GlobalValue.unRegistorCurrentActivity(this);
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
	
}

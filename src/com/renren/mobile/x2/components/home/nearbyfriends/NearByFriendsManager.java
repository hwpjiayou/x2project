package com.renren.mobile.x2.components.home.nearbyfriends;

import android.content.Context;
import android.view.View.OnClickListener;

import android.content.SharedPreferences;
import android.view.View;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.refresh.OnRefreshListener;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.dao.NearByFriendDAO;
import com.renren.mobile.x2.utils.location.NewLocationManager;
import com.renren.mobile.x2.utils.location.RenrenLocationManager;

import java.util.ArrayList;
/***
 * 附近的人
 * @author xiaochao.zheng
 *
 */
public class NearByFriendsManager implements OnRefreshListener{
	private static NearByFriendsManager mInstance ;
	private Context mContext;
	private NearByFriendsScreen mScreen ;
	private NearByFData mData;
	private NearByFriendDAO mDao;
	private boolean isRequestting = false;
	private long lastRequsettime;
	private long currentTime;
	private SharedPreferences mPreferences;
	private final static String tag = "nearbyfriends";
	private boolean isAccessLocation = false;
	private boolean isFirst = true;
	private boolean isShowDialog = false;
	private byte[] mLock = new byte[1];
	private boolean checked = true;

	private NearByfCallBack mNearByfCallBack = new NearByfCallBack() {
		
		@Override
		public void onDataLoadingSuccess(final NearByFData data) {
			mData =data;
			synchronized (mLock) {
				mLock.notifyAll();
			}
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				
				@Override
				public void run() {
					if(isShowDialog){
						mScreen.hidProgressBar();
					}
					mScreen.resetData(data);
					mScreen.mImageReflashBtn.stopdelay(2000);
				}
			});
		}
		
		@Override
		public void onDataLoadingError(final NearByFData data) {
			mData =data;
			synchronized (mLock) {
				mLock.notifyAll();
			}
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				
				@Override
				public void run() {
					if(isShowDialog){
						mScreen.hidProgressBar();
					}
					mScreen.resetData(data);
					mScreen.mImageReflashBtn.stopdelay(2000);
				}
			});
		}
	};
	
	private NearByFriendsManager(Context c){
		this.mContext = c;
		this.mData = new NearByFData();
		RenrenLocationManager.getRenrenLocationManager(mContext);
		NewLocationManager.getInstance(mContext);
		mDao = DAOFactoryImpl.getInstance().buildDAO(NearByFriendDAO.class);
		this.initFromDatabase();

	}
	public static NearByFriendsManager getInstance(Context c){
//		if(c!=null || mInstance==null){
		if(mInstance == null){
			mInstance = new NearByFriendsManager(c);
		}
		return mInstance;
	}
	/****
	 * 销毁实例，保证每次的实例都是最新的
	 */
	public static void destoryInstance(){
		if(mInstance != null){
			   mInstance.mContext = null;
				mInstance = null;
		}
	}
	/*
	 *  判断时间 超过五分钟就向服务器发起请求 
	 */
	public void checkandRefresh(){
		if((System.currentTimeMillis()-lastRequsettime)>5*60*1000){
//			getFromNet();
			NearByFriendFactory.getInstance(mContext).getFromNet(mDao, mNearByfCallBack);
		}
	}
	
	public void LocationAlert(){
		boolean iscanLocation = NewLocationManager.getInstance(mContext).startLocation(null);
		
		if(!iscanLocation){
			mScreen.hidmainview();
			mScreen.showLocationAlert();
		}else{
		}
		
	}
	public boolean checkLocationEnable(){
		return NewLocationManager.getInstance(mContext).startLocation(null);
	}
	
	/****
	 * 弹出警示对话框
	 * 包含打开定位，开启身边，第一期进入的时候没有数据进行加载
	 */
	public void showDialog(){
		if(mContext == null ){
			ErrLog.Print("mContext is null ");
			return ;
		}
		if(mScreen==null){
			return ;
		}
		mPreferences = mContext.getSharedPreferences(tag, Context.MODE_PRIVATE);
		if(isFirst){
			isAccessLocation = mPreferences.getBoolean(tag, false);
			isFirst = false;
		}
		if(isAccessLocation){
			mScreen.hidAlertDialog();
			mScreen.showmainview();
			return ;
		}
		ErrLog.Print("mContext is not null");
		mScreen.showAlertDialog();
		/***** to do ***/
		mScreen.mBtn_agree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isAccessLocation = true;
				mPreferences.edit().putBoolean(tag, checked).commit();
				if(mData.length == 0){
					RenrenChatApplication.getUiHandler().post(new Runnable() {
						@Override
						public void run() {
							mScreen.showProgressBar();
							isShowDialog = true;
							mScreen.hidAlertDialog();
						}
					});
					NearByFriendFactory.getInstance(mContext).getFromNet(mDao, mNearByfCallBack);				}
			}
		});
		
	}
	/***
	 * 从数据库初始化数据
	 */
	private void initFromDatabase(){
		ArrayList<NearByFModel> list = mDao.queryAll();
		if(list == null ){
			return ;
		}
		ErrLog.Print("list size ()   "+ list.size());
		for(int i = 0; i < list.size();++i){
			mData.add(list.get(i).url, list.get(i).userstatus, list.get(i).userid,
					list.get(i).username_headchar, list.get(i).username,list.get(i).user_gender);
//			ErrLog.Print("query from database all "+ list.get(i).username_headchar);
		}
	}
	/**
	 * 加载数据
	 */
	public void onLoadingData(){
		
	}
	/***
	 * 数据加载结束   进行UI调用
	 */
	public void onFinishLoadData(){

	}
	/***
	 * 保证每次调用的View都返回的是一个新的View
	 * @return
	 */
	public View getView(){
		this.mScreen=new NearByFriendsScreen(mContext,mData);
		this.mScreen.mImageReflashBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mScreen.mImageReflashBtn.start(40.0f);
				NearByFriendFactory.getInstance(mContext).getFromNet(mDao, mNearByfCallBack);			}
		});
		this.mScreen.setReFlashListener(this);
		return mScreen.getView();
	}
	public void hidalertLocation(){
		if(mScreen!=null){
			mScreen.hidLocationAlert();
		}
	}
	public void popupKeyboard(){
//		((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(WidgetSearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		mScreen.popKeyboard();
	}
	public void hidKeyboard(){
		mScreen.hidKeyboard();
	}

    @Override
    public void onPreRefresh() {
    }

    /* (non-Javadoc)
      * @see com.renren.mobile.x2.base.refresh.OnRefreshListener#onRefreshData()
      */
	@Override
	public void onRefreshData() {
		ErrLog.Print("onRefresh Data");
		boolean canget = NearByFriendFactory.getInstance(mContext).getFromNet(mDao, mNearByfCallBack);
		if(!canget){
//			CommonUtil.toast("距离上次刷新不足30秒");
			return ;
		}
		synchronized (mLock) {
			try {
				mLock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/* (non-Javadoc)
	 * @see com.renren.mobile.x2.base.refresh.OnRefreshListener#onRefreshUI()
	 */
	@Override
	public void onRefreshUI() {
		ErrLog.Print("onRefresh UI");		
		mScreen.resetData(mData);
	}
}

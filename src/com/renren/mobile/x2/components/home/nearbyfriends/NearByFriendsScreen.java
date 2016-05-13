package com.renren.mobile.x2.components.home.nearbyfriends;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.R.color;
import com.renren.mobile.x2.base.refresh.OnRefreshListener;
import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.view.OnSlipButtonStateChangedListener;
import com.renren.mobile.x2.components.chat.view.ProgressImageButton;
import com.renren.mobile.x2.components.chat.view.SlipButtonGroup;
import com.renren.mobile.x2.components.home.nearbyfriends.NearByFData.NearByFNnode;
import com.renren.mobile.x2.components.home.profile.ProfileActivity;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DipUtil;
import com.renren.mobile.x2.view.RefresherGridView;

/****
 *  
 * @author xiaochao.zheng
 * 附近的人Screen
 *
 */
public class NearByFriendsScreen {
	private final static int GIRL = 0;
	private final static int BOY = 1;
	private final static int ALL = -1;
//	private ListView mListView;
	private GridView mGridView;
	private RefresherGridView mRefresherGridView;
	private FrameLayout mRootView;
	private LinearLayout mLinearlayout;
	private LinearLayout mAlertView;
	private RelativeLayout mTopView;
	private LinearLayout mBtnsView;
	private LinearLayout mAccessLocationLayout;
//	private EditText mEditText;
	private FrameLayout mFramLayout;
	private RelativeLayout mProgressbar;
	private Context mContext;
	private NearByFAdapter mAdapter;
	private LayoutInflater mInflater;
	private NearByFData mData;
//	private TextView mBtn_grils;
//	private TextView mBtn_all;
//	private TextView mBtn_boys;
	private SlipButtonGroup mGenderSelector;
	public Button mBtn_agree;
	public Button mBtn_Accesslocation;
//	public Button mBtn_disagree;
//	public CheckBox mCheckBox;
	public ProgressImageButton mImageReflashBtn;
	private int state =-1;//-1代表全部附近的人，0代表女，1代表男
	public NearByFriendsScreen(Context context, NearByFData data) {
		mInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		mData = data;
		assert mData != null;
		this.mAdapter = new NearByFAdapter(mContext,mData);
		this.createView();
	}
	/***
	 * 重置数据
	 * @param data
	 */
	public void resetData(NearByFData data){
		ErrLog.Print("reset Data");
		Log.d("zxc","reset data ");
		this.mData = data;
		this.mAdapter.resetData(data.getDatawithgender(state));
		
	}
	/***
	 * 返回基类view，就是带covey的那个
	 * @return
	 */
	public View getView(){
		return mRootView;
	}
	/***
	 * 返回listview
	 * @return
	 */
	public GridView getListView(){
//		return mListView;
		return mGridView;
	}
	/***
	 * 显示刷新bar
	 */
	public void showProgressBar(){
		if(mProgressbar!=null && mProgressbar.getVisibility() == View.GONE){
			mProgressbar.setVisibility(View.VISIBLE);
			this.hidmainview();
		}
	}
	/***
	 *  隐藏刷新bar
	 */
	public void hidProgressBar(){
		if(mProgressbar!=null && mProgressbar.getVisibility() == View.VISIBLE){
			mProgressbar.setVisibility(View.GONE);
		}
		showmainview();
		
	}
	/***
	 * 注册下拉刷新
	 * @param listener
	 */
	public void setReFlashListener(OnRefreshListener listener ){
		mRefresherGridView.setOnRefreshListener(listener);
	}
	/***
	 * 创建view
	 */
	private void createView(){
		mRefresherGridView = new RefresherGridView(mContext);
		
		mRootView = (FrameLayout)mInflater.inflate(R.layout.nearbyfriends_layout, null);
		mFramLayout = (FrameLayout) mRootView.findViewById(R.id.nearbyf_view_container);
		mLinearlayout = (LinearLayout) mRootView.findViewById(R.id.nearbyf_linearlayout);
		mTopView = (RelativeLayout) mRootView.findViewById(R.id.nearby_top_container);
		mBtnsView = (LinearLayout)mRootView.findViewById(R.id.nearby_btn_container);
		mProgressbar = (RelativeLayout) mInflater.inflate(R.layout.nearbyfprogressbar, null);
		mAlertView = (LinearLayout) mInflater.inflate(R.layout.nearbyfalertdialog, null);
		mAccessLocationLayout = (LinearLayout) mInflater.inflate(R.layout.nearbyfaccesslocationdialog, null);
		mBtn_Accesslocation = (Button) mAccessLocationLayout.findViewById(R.id.nearby_accesslocation_des);
		mBtn_agree = (Button) mAlertView.findViewById(R.id.nearby_positive_des);
//		mBtn_disagree = (Button) mAlertView.findViewById(R.id.nearby_negative_des);
//		mCheckBox = (CheckBox)mAlertView.findViewById(R.id.nearbyf_remeber);
		mImageReflashBtn = (ProgressImageButton) mRootView.findViewById(R.id.nearby_reflash_btn);
		mImageReflashBtn.setPressSelector(R.drawable.v1_common_refresh_bg_press, R.drawable.v1_common_refresh_bg_unpress);
		mImageReflashBtn.setNormalAndRuningRes(R.drawable.v1_common_refresh_press, R.drawable.v1_common_refrash_unpress);
		mFramLayout.addView(mRefresherGridView);
		mRootView.addView(mProgressbar);
		mRootView.addView(mAlertView);
		mRootView.addView(mAccessLocationLayout);
		mGenderSelector = (SlipButtonGroup)mRootView.findViewById(R.id.nearbyf_genderselector);
		mGenderSelector.setOnDrawabel(new BitmapDrawable(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_common_tab_button_bg)));
		mGenderSelector.setTexts("全部", "校草", "校花");
		switch (state) {
		case -1:
			mGenderSelector.setPosition(0);
			break;
		case 0:
			mGenderSelector.setPosition(1);
			break;
		case 1:
			mGenderSelector.setPosition(2);
			break;
		default:
			break;
		}
		mRefresherGridView.setBackgroundResource(R.color.transparent);
        mGridView = mRefresherGridView.getGridView();
        mGridView.setBackgroundResource(R.color.white);
//        mGridView
//        mGridView.setBackgroundResource(R.drawable.v1_common_side_bg);
		mGridView.setNumColumns(3);
        mGridView.setBackgroundResource(R.color.white);
		mGridView.setVerticalSpacing(DipUtil.calcFromDip(20));
		mGridView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mGridView.setAdapter(mAdapter);
		mGridView.setDrawingCacheEnabled(false);
		mGridView.setVerticalFadingEdgeEnabled(false);
		mGridView.setClickable(false);
		mBtn_Accesslocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) mContext)
						.startActivityForResult(
								new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
								0);
			}
		});
		
		mGenderSelector.setOnStateChangedListener(new OnSlipButtonStateChangedListener() {
			
			@Override
			public void OnStateChanged(int position) {
				if(position == 0){
					 mAdapter.resetData(mData.getDatawithgender(ALL));
				}else if(position == 1){
					 mAdapter.resetData(mData.getDatawithgender(BOY));
				}else if(position == 2){
					 mAdapter.resetData(mData.getDatawithgender(GIRL));
				}
			}
		});
		
	}
	
	protected void showLocationAlert(){
		if(mAccessLocationLayout!=null){
			mAccessLocationLayout.setVisibility(View.VISIBLE);
			hidAlertDialog();
		}
	}
	protected void hidLocationAlert(){
		if(mAccessLocationLayout!=null){
			mAccessLocationLayout.setVisibility(View.GONE);
		}
	}
	
	protected void hidmainview(){
		if(mLinearlayout!=null){
			mLinearlayout.setVisibility(View.GONE);
		}
	}
	protected void showmainview(){
		if(mLinearlayout != null){
			mLinearlayout.setVisibility(View.VISIBLE);
		}
	}
	protected void showAlertDialog() {
		if(mAlertView!=null&&mLinearlayout!=null)
		{
			mLinearlayout.setVisibility(View.GONE);
			mAlertView.setVisibility(View.VISIBLE);
		}
	}
	protected void hidAlertDialog(){
		if(mAlertView!=null){

			mAlertView.setVisibility(View.GONE);
		}

	}
	

	/***
	 * 隐藏键盘
	 */
	public void hidKeyboard(){
		ErrLog.Print("hid Key board ");
	    InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
	}
	public void popKeyboard(){
		ErrLog.Print("pop Key board ");
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		
	}
}

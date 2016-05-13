package com.renren.mobile.x2.emotion;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;
import com.renren.mobile.x2.emotion.IEmotionManager.OnEmotionSelectCallback;

public class EmotionScreen {
	private Context mContext;
	private ArrayList<ImageView> mListButton;
	private ArrayList<ViewPager> mListViewPager;
	private ArrayList<EmotionPointer> mPointerViewList;
	private Node mNode;
	private int mNormalPageSize = EmotionConfig.PER_PAGE_NUM;
	private int mCoolPageSize = 8;
	private int mTabNums;
	private int mPerTabheight;
	private int mScreenWidth = 480;////?????暂时还没有值 啊
	private int mScreenHeight = 800;
	private View mRootView;
	private OnEmotionSelectCallback mEmotionSelectCallback;
	private LinearLayout mLinearLayoutContainer;
	private LinearLayout mLinearLayoutMain;
	private LinearLayout mLinearLayoutTab;
	private LayoutInflater mInflater;
	private boolean mIsneedGif = false;
	private boolean mIshasGif = false;
	private int imageViewRid[];
	private int selectedDrawable[];
	private int unSelectedDrawable[];
	private int unselectedbg[];
	public EmotionScreen(Context context,boolean isneedgif) {
		this.mIsneedGif=isneedgif;
		ErrLog.ll("isneedgif " + mIsneedGif);
		this.mContext = context;
		initRes();
		mInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE); 
		mRootView = mInflater.inflate(R.layout.emotion_layout, null);
		mLinearLayoutContainer = (LinearLayout) mRootView.findViewById(R.id.emotion_container);
		mLinearLayoutMain = (LinearLayout) mRootView.findViewById(R.id.emotion_main_container);
		mLinearLayoutTab = (LinearLayout) mRootView.findViewById(R.id.emotion_tab_container);
		mListViewPager = new ArrayList<ViewPager>();
		mPointerViewList = new ArrayList<EmotionPointer>();
		mListButton = new ArrayList<ImageView>();
		mNode = EmotionNameList.getEmotionNameList(1);
		mIshasGif = mNode.ishasGif();
		mTabNums = mNode.getThemeSum();
		EmotionConfig.SCREENHEIGHT = RenrenChatApplication.getScreenHeight();
		EmotionConfig.SCREENWIDTH = RenrenChatApplication.getScreenWidth();
		mScreenWidth = EmotionConfig.SCREENWIDTH;
		mScreenHeight = EmotionConfig.SCREENHEIGHT;
		ErrLog.Print(mScreenHeight+" "+ mScreenWidth);
		//todo
		this.createMainView();
		ErrLog.ll("createMainView cotext" + mContext);
	}
	
	
	private void initRes(){
		imageViewRid = new int[4];
		imageViewRid[0] = R.id.emotion_face_imageview;
		imageViewRid[1] = R.id.emotion_ring_imageview;
		imageViewRid[2] = R.id.emotion_flower_imageview;
		imageViewRid[3] = R.id.emotion_cube_imageview;
		selectedDrawable = new int[4];
		selectedDrawable[0] = R.drawable.v1_emotion_face_selected;
		selectedDrawable[1] = R.drawable.v1_emotion_ring_selected;
		selectedDrawable[2] = R.drawable.v1_emotion_flower_selected;
		selectedDrawable[3] = R.drawable.v1_emotion_cube_selected;
		unSelectedDrawable = new int[4];
		unSelectedDrawable[0] = R.drawable.v1_emotion_face_unselected;
		unSelectedDrawable[1] = R.drawable.v1_emotion_ring_unselected;
		unSelectedDrawable[2] = R.drawable.v1_emotion_flower_unselected;
		unSelectedDrawable[3] = R.drawable.v1_emotion_cube_unselected;
		unselectedbg = new int[4];
		unselectedbg[0]=R.drawable.v1_emotion_tab_left;
		unselectedbg[1]=R.drawable.v1_emotion_tab_mid;
		unselectedbg[2]=R.drawable.v1_emotion_tab_mid;
		unselectedbg[3]=R.drawable.v1_emotion_tab_right;
		
	}
	
	/**
	 * 注册接口,会回调点击添加表情和删除表情
	 * @param listener
	 */
	public void registerCallback(OnEmotionSelectCallback listener){
		this.mEmotionSelectCallback = listener;
	}
	/***
	 * 根据主题的个数创建相应的viewpager 和下面的Tab
	 */
	private void createMainView(){
		int offset = 0;
		ArrayList<Integer> themeList = mNode.getThemeList();
		for(int i = 0; i < themeList.size();++i){
			if(!mIsneedGif){
				if(themeList.get(i)==EmotionConfig.EMOTION_COOL_STYLE){
					ErrLog.ll("jump cool " + themeList.get(i)
							);
					offset++;
					continue;
				}
			}
			ErrLog.ll("createMainView"+(i-offset)+ mPerTabheight + EmotionConfig.getTabViewHeight());
			ImageView img = (ImageView) mRootView.findViewById(imageViewRid[i-offset]);
			mListButton.add(img);
			if(!mIshasGif  && !mIsneedGif && i-offset == 2){
				ErrLog.ll("setBackres " + (i-offset));
				img.setBackgroundResource(R.drawable.v1_emotion_cube_selector);
			}
			if(mIshasGif && mIsneedGif &&i-offset == 3){
				img.setVisibility(View.VISIBLE);
			}
			this.setOnClickListener(img, i-offset);
			ArrayList<GridView> gridviewlist = new ArrayList<GridView>();
		    int sum = this.createChildView(themeList.get(i-offset),gridviewlist);
			ViewPagerAdapter adapter = null;
			if(mIshasGif && mIsneedGif && themeList.get(i) == EmotionConfig.EMOTION_COOL_STYLE){
				adapter = new ViewPagerAdapter(gridviewlist,themeList.get(i-offset),i, true);
			}else{
				adapter= 	new ViewPagerAdapter(gridviewlist,themeList.get(i-offset),i,false);
			}
			ViewPager viewpager = (ViewPager) mInflater.inflate(R.layout.emotion__viewpager, null);
			viewpager.setAdapter(adapter);
			mListViewPager.add(viewpager);
			final EmotionPointer pointerview = new EmotionPointer(mContext, sum, 0);
			mPointerViewList.add(pointerview);
			viewpager.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					pointerview.movetoIndex(arg0);
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
		}
		mLinearLayoutMain.addView(mPointerViewList.get(0).getPointView());
		mLinearLayoutMain.addView(mListViewPager.get(0));
		

	}
	/***
	 * 创建每个viewpager下面的gridview
	 */
	private int createChildView(int themeid, ArrayList<GridView> list){
		int layoutId =0;
		int pagenums = 0;
		if(themeid == EmotionConfig.EMOTION_COOL_STYLE){
			layoutId = R.layout.emotion_cube_gridview_layout;
			pagenums = mNode.getPageNum(mCoolPageSize, themeid);
			for(int i = 0 ; i < pagenums;++i ){
				GridView gridview = (GridView) mInflater.inflate(layoutId, null);
				list.add(gridview);
				gridview.setClickable(false);
			}
		}else{
			layoutId = R.layout.emotion_gridview_layout;
			pagenums = mNode.getPageNum(mNormalPageSize, themeid);
			for(int i = 0 ; i < pagenums;++i ){
				GridView gridview = (GridView) mInflater.inflate(layoutId, null);
				list.add(gridview);
			}
		}
		return pagenums;
	}
	/***
	 * 数据在此处被设置
	 * @param position 应该设置成和position一致
	 * @param viewlist
	 */
	private void setAdapter(int themeid,int position, boolean isgif,ArrayList<GridView> viewlist){
		int pagesize = isgif?mCoolPageSize:mNormalPageSize;
		EmotionGridViewAdapter adapter = new EmotionGridViewAdapter(mNode.getCodeArrayByTheme(pagesize, position, themeid,!isgif),
				mNode.getPathArrayByTheme(pagesize, position, themeid,!isgif),isgif, mContext,mEmotionSelectCallback);
		viewlist.get(position).setAdapter(adapter);
	}
	private void setOnClickListener(ImageView btn, final int index){
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("zxc","click tab " + index);
				setDefaultExcept(index);
				mListButton.get(index).setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), selectedDrawable[index]));
				mListButton.get(index).setBackgroundResource(R.drawable.v1_emotion_tab_pressed);
				mLinearLayoutMain.removeAllViews();
				mLinearLayoutMain.addView(mPointerViewList.get(index).getPointView());
				mLinearLayoutMain.addView(mListViewPager.get(index));
			}
		});
	}
	
	private void setDefaultExcept(int index){
		for(int i = 0; i < mListButton.size();++i){
			if(i==index){
				continue;
			}
			mListButton.get(i).setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), unSelectedDrawable[i]));
			mListButton.get(i).setBackgroundResource(unselectedbg[i]);
		}
	}
	public View getview(){
		ErrLog.ll("return view" + mRootView);
		return mRootView;
	}
//	public void setNumColumns(int nums){
//		for(int i = 0; i < mViewlist.size();++i){
//			mViewlist.get(i).setNumColumns(nums);
//		}
//	}
	
	public class ViewPagerAdapter extends PagerAdapter{
		private int themeid;
		private ArrayList<GridView> mList;
		private int pageindex;
		private boolean isgif= false;
		public ViewPagerAdapter(ArrayList<GridView> list, int themeid,int pageindex,boolean isgif) {
			this.mList = list;//持有此引用
			this.themeid = themeid;
			this.pageindex = pageindex;
			this.isgif = isgif;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView(mList.get(position));
		}
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager)container).addView(mList.get(position));
			setAdapter(themeid,position,isgif, mList);
			return mList.get(position);
		}
		
	}
	
	
	
	
	
}

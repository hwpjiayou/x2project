package com.renren.mobile.x2.components.home.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.refresh.OnRefreshListener;
import com.renren.mobile.x2.components.home.setting.SettingBlackFragment.BlackListDataModel;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.TagResponse;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.view.RefresherListView;

/**
 * @author yayun.wei
 * @time 11.09.2012
 * @introduction 设置模块中的黑名单列表Fragment
 * */
public class SettingBlackFragment extends BaseFragment<List<BlackListDataModel>> implements OnRefreshListener, OnScrollListener{
	
	private static final String TAG = "SettingBlackFragment";
	
	/*用来存储黑名单列表数据的List*/
	private List<BlackListDataModel> mBlackListDataList;
	
	/*黑名单界面用于返回给Fragment的根View*/
	private View mRootView;
	
	/*可以下拉刷新的用来展现黑名单列表的自定义View*/
	private RefresherListView mRefresherListView;
	
	/*从RefresherListView中得到的实际的用于展现数据的ListView*/
	private ListView mBlackListView;
	
	/*用来展现黑名单列表的Adapter*/
	private BlackListAdapter mBlackListAdapter;
	
	/*是否确认将对方移出黑名单的提示Dialog*/
	private AlertDialog mConfirmDialog;
	
	/*装载此Fragment的Activity*/
	private Activity mActivity;
	
	/*用来实例化布局的LayoutInflater*/
	private LayoutInflater mInflater;
	
	/*图片下载器*/
	private ImageLoader mHeadImageLoader;
	
	/*是否允许加载图片的标志位，当且仅当列表停止滑动时为true*/
	private boolean isAllowDownload = true;
	
	/*要移出黑名单的目标的ID*/
	private long deleteId;
	
	/*要移出黑名单的目标在List中的位置*/
	private int deletePosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initFields(inflater);
		initView();
		return mRootView;
	}
	
	/**
	 * 初始化必须的属性值
	 * */
	private void initFields(LayoutInflater inflater) {
		this.mActivity = (SettingInfoActivity) getActivity();
		this.mInflater = inflater;
	}
	
	/**
	 * 初始化View
	 * */
	private void initView() {
		getTitleBar().setTitle(mActivity.getText(R.string.setting_blacklist));
		mRootView = mInflater.inflate(R.layout.setting_blacklist_main_layout, null);
		mRefresherListView = (RefresherListView) mRootView.findViewById(R.id.setting_blacklist_listview);
		mRefresherListView.setOnRefreshListener(this);
		mBlackListView = mRefresherListView.getListView();
		mBlackListView.setDrawingCacheEnabled(false);
		mBlackListView.setHorizontalFadingEdgeEnabled(false);
		mBlackListView.setScrollingCacheEnabled(false);
		mBlackListView.setAlwaysDrawnWithCacheEnabled(false);
		mBlackListView.setWillNotCacheDrawing(true);
		mBlackListView.setDivider(null);
		mBlackListView.setOnScrollListener(this);
		initDialog();
	}
	
	/**
	 * 初始化移除操作时弹出的确认Dialog
	 * */
	private void initDialog() {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);
		mBuilder.setMessage(R.string.setting_blacklist_out_dialog);
		mBuilder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mConfirmDialog.dismiss();
			}
		});
		mBuilder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteFromBlackList();
				deleteSucceeded();
			}
		});
		mConfirmDialog = mBuilder.create();
		mConfirmDialog.setCanceledOnTouchOutside(false);
	}
	
	/**
	 * 显示确认移出黑名单的Dialog
	 * */
	private void showConfirmDialog() {
		if(mConfirmDialog == null) {
			initDialog();
		}else if(!mConfirmDialog.isShowing()) {
			mConfirmDialog.show();
		}
	}

	@Override
	protected void onPreLoad() {
		CommonUtil.log(TAG, "=====onPreLoad=====");
		if(mHeadImageLoader == null) {
			mHeadImageLoader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD, mActivity);
		}
		
		if(mBlackListAdapter == null) {
			mBlackListAdapter = new BlackListAdapter();
		}
		
		if(mBlackListDataList == null) {
			mBlackListDataList = new ArrayList<BlackListDataModel>();
		}
		mBlackListDataList.clear();
		
		mBlackListView.setAdapter(mBlackListAdapter);
	}

	@Override
	protected void onFinishLoad(List<BlackListDataModel> data) {
		CommonUtil.log(TAG, "=====onFinishLoad=====");
		if(data != null) {
			CommonUtil.log("blacklist", "=====onFinishLoad  data != null=====");
			mBlackListDataList.addAll(data);
		}
		mBlackListAdapter.notifyDataSetChanged();
	}

	@Override
	protected List<BlackListDataModel> onLoadInBackground() {
		CommonUtil.log(TAG, "=====onLoadInBackground=====");
		List<BlackListDataModel> fakeData = new ArrayList<BlackListDataModel>();
		
		BlackListDataModel model1 = new BlackListDataModel(446412354, "苍井空", "http://hdn.xnimg.cn/photos/hdn321/20111022/1855/tiny_mkRe_77024k019117.jpg");
		fakeData.add(model1);
		
		BlackListDataModel model2 = new BlackListDataModel(446412354, "小泽玛利亚", "http://hdn.xnimg.cn/photos/hdn121/20120403/1920/tiny_9B29_88110c019118.jpg");
		fakeData.add(model2);
		
		BlackListDataModel model3 = new BlackListDataModel(446412354, "泷泽萝拉", "http://hdn.xnimg.cn/photos/hdn521/20120504/1100/tiny_dbpD_64536j019118.jpg");
		fakeData.add(model3);
		
		BlackListDataModel model4 = new BlackListDataModel(446412354, "大桥未久", "http://hdn.xnimg.cn/photos/hdn321/20120706/0000/tiny_8yfE_1b4400018a801375.jpg");
		fakeData.add(model4);
		
		BlackListDataModel model5 = new BlackListDataModel(446412354, "濑亚美莉", "http://hdn.xnimg.cn/photos/hdn221/20110806/2255/tiny_TNYS_44715p019118.jpg");
		fakeData.add(model5);
		CommonUtil.log(TAG, "=====onLoad Over=====");
		return fakeData;
	}
	
	/**
	 * 将目标从黑名单中移除操作方法
	 * */
	private void deleteFromBlackList() {
		mBlackListDataList.remove(deletePosition);
	}
	
	/**
	 * 移除操作成功后的更新
	 * */
	private void deleteSucceeded() {
		CommonUtil.toast(R.string.setting_blacklist_out_succeeded);
		mBlackListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroyData() {
		CommonUtil.log(TAG, "=====onDestroyData=====");
		if(mBlackListDataList != null) {
			mBlackListDataList.clear();
		}
		
		if(mBlackListView != null) {
			mBlackListView = null;
		}
		
		if(mRefresherListView != null) {
			mRefresherListView = null;
		}
		
		if(mRootView != null) {
			mRootView = null;
		}
		
		if(mConfirmDialog != null) {
			mConfirmDialog = null;
		}
		
		if(mHeadImageLoader != null) {
			mHeadImageLoader =null;
		}
	}

	@Override
	public void onPreRefresh() {
		CommonUtil.log(TAG, "=====onPreRefresh=====");
	}

	@Override
	public void onRefreshData() {
		CommonUtil.log(TAG, "=====onRefreshData=====");
	}

	@Override
	public void onRefreshUI() {
		CommonUtil.log(TAG, "=====onRefreshUI=====");
	}
	
	public class BlackListDataModel {
		
		private long userId;
		private String name;
		private String headUrl;
		
		public BlackListDataModel(long id, String name, String headUrl) {
			this.userId = id;
			this.name = name;
			this.headUrl = headUrl;
		}
		
		public long getId() {
			return this.userId;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getHeadUrl() {
			return this.headUrl;
		}
	}

	public class BlackListAdapter extends BaseAdapter {
		
		class ViewHolder {
			@ViewMapping(ID = R.id.setting_blacklist_item_head)
			public ImageView mHeadImageView;
			
			@ViewMapping(ID = R.id.setting_blacklist_item_name)
			public TextView mNameView;
			
			@ViewMapping(ID = R.id.setting_blacklist_item_out)
			public Button mOutBtn;
		}

		@Override
		public int getCount() {
			return mBlackListDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BlackListDataModel model = mBlackListDataList.get(position);
			ViewHolder mHolder = null;
			if(convertView == null) {
				mHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.setting_blacklist_item_layout, null);
				ViewMapUtil.viewMapping(mHolder, convertView);
				convertView.setTag(mHolder);
			}else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			
			if(mHolder != null) {
				wrapView(mHolder, model, position);
			}
			
			return convertView;
		}
		
		/**
		 * 给View塞数据
		 * */
		private void wrapView(ViewHolder viewHolder, final BlackListDataModel model, final int position) {
			viewHolder.mNameView.setText(model.getName());
			setHeadImage(viewHolder.mHeadImageView, model.getHeadUrl());
			viewHolder.mOutBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CommonUtil.log(TAG, "onclick name = "+model.getName());
					deleteId = model.getId();
					deletePosition = position;
					showConfirmDialog();
				}
			});
		}
		
		/**
		 * 加载头像
		 * */
		private void setHeadImage(final ImageView headImageView, final String headUrl) {
//			headImageView.setTag(headUrl);
			HttpImageRequest request = new HttpImageRequest(headUrl, isAllowDownload);
			Bitmap bitmap = mHeadImageLoader.getMemoryCache(request);
			
			if(bitmap != null) {
				headImageView.setImageBitmap(bitmap);
			}else{
				TagResponse<String> headurlRes = new TagResponse<String>(headUrl) {
					@Override
					public void failed() {
						CommonUtil.log(TAG, "=====onDownloadHead Failed=====");
					}
					@Override
					protected void success(final Bitmap bitmap, String tag) {
						if (isAllowDownload != false && headUrl.equals(tag)) {
							RenrenChatApplication.getUiHandler().post(
									new Runnable() {

										@Override
										public void run() {
											// ImageView image = (ImageView) listView.findViewWithTag(headUrl);
											if (headImageView != null) {
												headImageView.setImageBitmap(bitmap);
												notifyDataSetChanged();
											}
										}
									});
						}
					}
				};
				
				mHeadImageLoader.get(request, headurlRes);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch(scrollState){
		case SCROLL_STATE_IDLE:
			isAllowDownload = true;
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					mBlackListAdapter.notifyDataSetChanged();
				}
			});
			break;
		case SCROLL_STATE_FLING:
			isAllowDownload = false;
			break;
		case SCROLL_STATE_TOUCH_SCROLL:
			isAllowDownload = false;
			break ;
		}
	}
}

package com.renren.mobile.x2.components.home.profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.refresh.OnRefreshListener;
import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.util.ContactModel;
import com.renren.mobile.x2.components.comment.CommentActivity;
import com.renren.mobile.x2.components.home.HomeFragment;
import com.renren.mobile.x2.components.home.HomeFragment.OnActivityResultListener;
import com.renren.mobile.x2.components.home.HomeTab;
import com.renren.mobile.x2.components.home.feed.FeedModel;
import com.renren.mobile.x2.components.home.profile.ProfileFragment.PhotoUploadSuccessListener;
import com.renren.mobile.x2.components.imageviewer.ImageViewActivity;
import com.renren.mobile.x2.components.imageviewer.PhotoUploadManager;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.components.photoupload.HeadEditActivity;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.view.CoverWrappedListView;
import com.renren.mobile.x2.view.LoadMoreViewItem;
import com.renren.mobile.x2.view.LoadMoreViewItem.onLoadListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProfileView implements HomeTab {
	public static final String USER_ID = "user_id";
	private Activity mActivity;
	private ListView mProfileFeedView;
	private View mReView;
	protected LayoutInflater mInflater;
	private String mUserId;
	private ProfileDataHolder mHolder;
	private ProfileDataModel mModel;
	private PhotoUploadSuccessListener listener;
	private boolean isMeProfile;
	private List<FeedModel> list = new ArrayList<FeedModel>();;
	private ProfileFeedAdapter mAdapter;
	private CoverWrappedListView mRootView;
	public LoadMoreViewItem mAddFeedItem ;
	private OnScrollListener mOnScrollListener = null;
	private View progressBar;
	private int mScrollState;
//	private GeneralPopupWindow popup;
//	PopupHolder popupHolder;
	private ImageLoader loader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD, mActivity);

	public ProfileView(Activity activity) {
		this.mActivity = activity;
		mUserId = mActivity.getIntent().getStringExtra(USER_ID);
		isMeProfile = false;
		initView();
		initData();
	}

	public ProfileView() {
		listener = new PhotoUploadSuccessListener() {
			public void updateUI_Photo() {
				RenrenChatApplication.getUiHandler().post(new Runnable() {
					@Override
					public void run() {
						mHolder.mHeadImg.setImageBitmap(null);
						loadHeadImg(mHolder.mHeadImg, LoginManager.getInstance().getLoginInfo().mMediumUrl, false);
					}
				});
			}
		};
	}

	private void initData() {
		if(isMeProfile==true){
			initMyInfo();
			getFeedListFromNet(false,0,true);
			initEvent();
		}else{
//			HttpMasService.getInstance().getUserInfoById(new getUserInfoResponse(), mUserId);
			HttpMasService.getInstance().getProfile(mUserId, new getUserInfoResponse());
			getFeedListFromNet(false,0,true);
		}
		mAdapter = new ProfileFeedAdapter(mActivity, list, mProfileFeedView);
		RenrenChatApplication.getUiHandler().post(new Runnable() {

			@Override
			public void run() {
				mProfileFeedView.setAdapter(mAdapter);
			}
		});
		mRootView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefreshUI() {
				
			}
			
			@Override
			public void onRefreshData() {
				getFeedListFromNet(true,0,false);
				
			}
			
			@Override
			public void onPreRefresh() {
			}
		});
	}
	private void initMyInfo() {
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				LoginInfo mLoginInfo = LoginManager.getInstance().getLoginInfo();
				mHolder.mSchoolName.setVisibility(View.VISIBLE);
				mHolder.mSchoolName.setText(mLoginInfo.mSchool);
				mHolder.mUserName.setVisibility(View.VISIBLE);
				mHolder.mUserName.setText(mLoginInfo.mUserName);
				mHolder.mBirthday.setVisibility(View.VISIBLE);
				mHolder.mBirthday.setText(mLoginInfo.mBirthday);
				mHolder.mDepartment.setVisibility(View.VISIBLE);
				mHolder.mDepartment.setText(mLoginInfo.mDepartment);
				mHolder.mEnrollTime.setVisibility(View.VISIBLE);
				mHolder.mEnrollTime.setText(mLoginInfo.mEnrollyear);
				mHolder.mGender.setVisibility(View.VISIBLE);
				if (mLoginInfo.mGender == 1) {
					mHolder.mGender.setImageResource(R.drawable.v1_userinfo_contact_male);
				} else {
					mHolder.mGender.setImageResource(R.drawable.v1_userinfo_contact_famale);
				}
				loadHeadImg(mHolder.mHeadImg,mLoginInfo.mMediumUrl, false);
//				loadHeadImg(mHolder.mReLayout,mLoginInfo.mCoverUrl, true);
				
			}
		});
		
		
	}

	public void getFeedListFromNet(final boolean isRefresh,int pageNum,final boolean isFirstLoading){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				INetResponse response = new INetResponse() {

					@Override
					public void response(INetRequest req, JSONObject obj) {
						CommonUtil.log("lu","obj"+obj.toString());
						if(null!=obj){
							removeProgressBar();
							if(Methods.checkNoError(req, obj)){
								try {
									JSONArray array = obj.getJSONArray("feeds");
									if(array!=null){
										reset(array, isRefresh,isFirstLoading);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								
							}
							else{   //信息错误时，也要进行AddItem的完成操作
//								RenrenChatApplication.getUiHandler().post(new Runnable() {
//									
//									@Override
//									public void run() {
//										setLoadMoreItem();
//										if(mAddFeedItem!=null)
//											mAddFeedItem.syncNotifyLoadComplete();
//									}
//								});
								removeLoadMoreItem();

							}
						}
					}
				};
				//mFeedView.mFeedAdapter.copyNextPager();
				if(isRefresh||isFirstLoading){  //第一次加载或下拉刷新会加载基本加载数量的2倍
					if(isMeProfile==true){
						HttpMasService.getInstance().getMyFeeds(10, null, null, response);
					}else{
						HttpMasService.getInstance().getSomeOnesFeed(mUserId, null, 10, null, response);
					}
					
				}
				else{
					List<FeedModel>list = mAdapter.mModelList;
					String beforeFeedId = null;
					int size = list.size();
					if(size>0){
						beforeFeedId = Long.toString(list.get(size-1).getFeedId());
					}
					if(isMeProfile==true){
						HttpMasService.getInstance().getMyFeeds(10, beforeFeedId, null, response);
					}else{
						HttpMasService.getInstance().getSomeOnesFeed(mUserId, null, 10, beforeFeedId, response);
					}
					
				}
			}
		}).start();
		
	}
	private void removeProgressBar(){
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				mProfileFeedView.removeFooterView(progressBar);
				
			}
		});
	}
	 public void reset(final JSONArray array, final boolean isRefresh,
				final boolean isFirstLoading) {

			CommonUtil.log("x11", "reset:" + mAdapter.mModelList.size());
			if (array == null || array.length() == 0) {
				removeLoadMoreItem();
				return;
			}
			if (isRefresh || isFirstLoading) { // 如果是下拉刷新，则要清空当前List和缓存List
				CommonUtil.log("xj", "isRefresh || isFirstLoading");
				RenrenChatApplication.getUiHandler().post(new Runnable() {

					@Override
					public void run() {
						mAdapter.mModelList.clear();      //清空ModelList
						try {
							JSONObject object = null;
							for (int i = 0; i < array.length(); i++) {
								object = array.getJSONObject(i);
								FeedModel model = new FeedModel(object);
								mAdapter.mModelList.add(model);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if(array.length() == 10){
							setLoadMoreItem();
						}else{
							removeLoadMoreItem();
						}
						if(mAddFeedItem!=null)
							mAddFeedItem.syncNotifyLoadComplete();
						mAdapter.notifyDataSetChanged();
					}
				});

			} else {  //自动加载返回的数据
				CommonUtil.log("lu", "is not isRefresh || isFirstLoading");
				
				
				RenrenChatApplication.getUiHandler().post(new Runnable() {
					
					@Override
					public void run() {
						try {
							JSONObject object = null;
							for (int i = 0; i < array.length(); i++) {
								object = array.getJSONObject(i);
								FeedModel model = new FeedModel(object);
								mAdapter.mModelList.add(model);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						CommonUtil.log("lu","array.length()"+array.length()+(array.length() == 10));
						if(array.length() == 10){
							setLoadMoreItem();
						}else{
							removeLoadMoreItem();
						}
						if(mAddFeedItem!=null)
							mAddFeedItem.syncNotifyLoadComplete();
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		}
	 /**
		 * 加入自动加载控件
		 */
		public void setLoadMoreItem(){
			if(mAddFeedItem == null){
				mAddFeedItem = LoadMoreViewItem.getLoadMoreViewItem(mActivity);
				mAddFeedItem.setOnLoadListener(new onLoadListener() {

					@Override
					public void onLoad() {
						getFeedListFromNet(false,0,false);
//						HttpMasService.getInstance().getProfileFeedById(new getProfileFeedResponse(), mUserId);
					}
				});
				mProfileFeedView.addFooterView(mAddFeedItem);
				//mAddFeedItem.setProgressVisible(false);
			}
		}
		/**
		 * 去掉自动加载控件
		 */
		public void removeLoadMoreItem(){
			if(mAddFeedItem!=null){
				CommonUtil.log("lu", "removeLoadMoreItem");
				mProfileFeedView.removeFooterView(mAddFeedItem);
				mAddFeedItem = null;
			}
		}
	private void initEvent() {
			mHolder.mHeadImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (true == isMeProfile) {
						selectHeadDialog(mActivity);
					} else {
						ImageViewActivity.show(mActivity, mModel.headImg, false);
					}
				}
			});
				mProfileFeedView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO 点击调转评论
						if(arg2==0){
							if(true == isMeProfile){
								selectDialog(mActivity);
							}else{
								
							}
						}else{
							if(mAdapter.mModelList.size()!=0){
								Intent intent = new Intent();
								intent.setClass(mActivity,CommentActivity.class);
								Bundle bundle = new Bundle();
								FeedModel model = null;
										model = mAdapter.mModelList.get(arg2-1);
								if(model!=null){
									bundle.putSerializable("feedmodel",model);
									bundle.putInt("feed_layout_id", R.layout.feed_adapter_item_layout);
									intent.putExtras(bundle);
									mActivity.startActivity(intent);
								}
						}
						}
						
						
					}
				});
			
			mOnScrollListener = new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					mScrollState = scrollState;
					if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
						if(mAdapter!=null){
//							FeedAdapter.isScrolling = true;
//							mAdapter.setScroll(false);
							RenrenChatApplication.getUiHandler().post(new Runnable() {

								@Override
								public void run() {
									mAdapter.notifyDataSetChanged();
								}
							});
						}
					}
					else{
//						FeedAdapter.isScrolling = true;
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (mAddFeedItem != null && mAddFeedItem.getVisibility() == View.VISIBLE && !mAddFeedItem.isLoading()
							&& (firstVisibleItem + visibleItemCount == totalItemCount)) {
						mAddFeedItem.wasClick();
					}
				}
			};
		}
	/** 选择修改头像 */
	public void selectHeadDialog(final Activity activity) {
		final String items[] = { mActivity.getResources().getString(R.string.profile_changehead_viewimg),
				mActivity.getResources().getString(R.string.profile_changehead_local),
				mActivity.getResources().getString(R.string.profile_changehead_camera) };
		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO
				Intent intent = null;
				switch (which) {
				case 0://查看大图
					ImageViewActivity.show(activity,LoginManager.getInstance().getLoginInfo().mOriginal_Url,false);
					break;
				case 1:// 拍照上传
					String fileName = "head_" + String.valueOf(System.currentTimeMillis());
					intent = PhotoUploadManager.getInstance().getTakePhotoIntent("/sixin/", fileName, ".jpg");
					activity.startActivityForResult(intent, PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO);
					break;
				case 2:// 本地上传
					activity.startActivityForResult(PhotoUploadManager.getInstance().getChooseFromGalleryIntent(),
							PhotoUploadManager.REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY);
					break;
				default:
					break;
				}
			}
		};
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
					PhotoUploadManager.getInstance().createUploadDialog(activity,
							mActivity.getResources().getString(R.string.profile_changehead), items,
							dialogClickListener);
			}
		});
	}
	/** 选择修改cover */
	public void selectDialog(final Activity activity) {
		final String items[] = { mActivity.getResources().getString(R.string.profile_changehead_local),
				mActivity.getResources().getString(R.string.profile_changehead_camera) };
		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO
				Intent intent = null;
				switch (which) {
				case 0:// 拍照上传
					String fileName = "head_" + String.valueOf(System.currentTimeMillis());
					intent = PhotoUploadManager.getInstance().getTakePhotoIntent("/sixin/", fileName, ".jpg");
					activity.startActivityForResult(intent, PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO);
					break;
				case 1:// 本地上传
					activity.startActivityForResult(PhotoUploadManager.getInstance().getChooseFromGalleryIntent(),
							PhotoUploadManager.REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY);
					break;
				default:
					break;
				}
			}
		};
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
					PhotoUploadManager.getInstance().createUploadDialog(activity,
							mActivity.getResources().getString(R.string.profile_changecover), items,
							dialogClickListener);
			}
		});
	}

	private void initView() {
		mInflater = LayoutInflater.from(mActivity);
		mRootView = new CoverWrappedListView(mActivity);
//		View root = mInflater.inflate(R.layout.chat_userinfo_popup,null);
//		popup = new GeneralPopupWindow(mRootView,root);
//		popupHolder = new PopupHolder();
//		ViewMapUtil.viewMapping(popupHolder, root);
		mRootView.setBackgroundResource(R.drawable.chatlist_item_normal);
		progressBar = ((LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.feed_progressbar, null);
		mProfileFeedView = mRootView.getListView();
		mProfileFeedView.addFooterView(progressBar);
		initTopView(mRootView);
	}
    public void toChat(){
    	if(mModel!=null){
			ContactModel tmp = new ContactModel(mModel.userName, Long.parseLong(mUserId),
					mModel.headImg);
			RenRenChatActivity.show(mActivity, tmp);}
    }
	/**
	 * 初始化Cover的布局
	 * 
	 * @param mRootView2
	 *            :CoverWrappedListView
	 */
	private void initTopView(final CoverWrappedListView rootView) {
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mReView = (View) inflater.inflate(R.layout.profile_layout, null);
		rootView.setTopView(mReView);
		mHolder = new ProfileDataHolder();
		mHolder.mReLayout = (LinearLayout) mReView.findViewById(R.id.profile_userinfo_relative);
		mHolder.mReLayout.setLayoutParams(new LayoutParams(RenrenChatApplication.getScreenWidth(), LayoutParams.WRAP_CONTENT));
		mHolder.mBirthday = (TextView) mReView.findViewById(R.id.profile_birthday);
		mHolder.mDepartment = (TextView) mReView.findViewById(R.id.profile_department);
		mHolder.mEnrollTime = (TextView) mReView.findViewById(R.id.profile_enrolltime);
		mHolder.mSchoolName = (TextView) mReView.findViewById(R.id.profile_scholl);
		mHolder.mUserName = (TextView) mReView.findViewById(R.id.profile_name);
		mHolder.mHeadImg = (ImageView) mReView.findViewById(R.id.profile_head_img);
		mHolder.mGender = (ImageView) mReView.findViewById(R.id.profile_gender);
	}

	private class getUserInfoResponse implements INetResponse {

		@Override
		public void response(INetRequest request, JSONObject result) {
			final JSONObject obj = (JSONObject) result;
			final INetRequest req = (INetRequest) request;
			CommonUtil.log("lu",obj.toString() );
			RenrenChatApplication.getUiHandler().post(new Runnable() {

				@Override
				public void run() {
					if (Methods.checkNoError(req, obj)) {
						try {
							mModel = new ProfileDataModel();
							JSONArray schoolMessage = obj.getJSONArray("school");
							JSONObject schoolName = (JSONObject) schoolMessage.get(0);
							mModel.schoolName = schoolName.getString("name");
							JSONObject  headImg = obj.getJSONObject("profile_image");
							mModel.userName = obj.getString("name");
							mModel.birthday = obj.getString("birth_display");
							mModel.headImg = headImg.getString("medium_url");
							mModel.department = schoolName.getString("department_name");
							mModel.enrollTime = schoolName.getString("enroll_year");
							mModel.gender = obj.getLong("gender");
							mModel.coverImg = obj.getString("cover_url");
							mHolder.mSchoolName.setVisibility(View.VISIBLE);
							mHolder.mSchoolName.setText(mModel.schoolName);
							mHolder.mUserName.setVisibility(View.VISIBLE);
							mHolder.mUserName.setText(mModel.userName);
							mHolder.mBirthday.setVisibility(View.VISIBLE);
							mHolder.mBirthday.setText(mModel.birthday);
							mHolder.mDepartment.setVisibility(View.VISIBLE);
							mHolder.mDepartment.setText(mModel.department);
							mHolder.mEnrollTime.setVisibility(View.VISIBLE);
							mHolder.mEnrollTime.setText(mModel.enrollTime);
							mHolder.mGender.setVisibility(View.VISIBLE);
							if (mModel.gender == 1) {
								mHolder.mGender.setImageResource(R.drawable.v1_userinfo_contact_male);
							} else {
								mHolder.mGender.setImageResource(R.drawable.v1_userinfo_contact_famale);
							}
							loadHeadImg(mHolder.mHeadImg, mModel.headImg, false);
//							loadHeadImg(mHolder.mReLayout, mModel.coverImg, true);
							initEvent();
							mRootView.addOnScrollListener(mOnScrollListener);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
					}

				}
			});
		}
	}
	private void loadHeadImg(final View imageView, String imageUrl, final Boolean isCover) {
		if(TextUtils.isEmpty(imageUrl)){
			
		}else{
			loader.get(new ImageLoader.HttpImageRequest(imageUrl, true), new ImageLoader.UiResponse() {
				@Override
				public void uiSuccess(Bitmap mBitmap) {
					final Bitmap bitmap = mBitmap;
					RenrenChatApplication.getUiHandler().post(new Runnable() {

						@Override
						public void run() {
							if (isCover == false) {
								((ImageView) imageView).setImageBitmap(bitmap);
							} else {
								Bitmap backImg = creatBimap(bitmap);
								Bitmap newImg = Bitmap.createBitmap(backImg, 0, 0, RenrenChatApplication.getScreenWidth(),
										(int) (230 * RenrenChatApplication.getdensity()));
								mRootView.setCoverImageBitmap(newImg);
							}
						}
					});
				}

				@Override
				public void failed() {
				}
			});
		}
		

	}

	private Bitmap creatBimap(Bitmap bitmap) {
		Bitmap img = null;
		int screenWidth = RenrenChatApplication.getScreenWidth();
		img = scaleImg(bitmap, screenWidth);

		return img;
	}

	private static Bitmap scaleImg(Bitmap img, float quality) {

		if (img == null) {
			return null;
		}
		float sHeight = 0;
		float sWidth = 0;

		Bitmap result = img;

		if (img.getWidth() != quality) {

			sWidth = quality;
			sHeight = (quality * img.getHeight()) / img.getWidth();
			result = Bitmap.createScaledBitmap(img, (int) sWidth, (int) sHeight, true);

			if (null != img && !img.isRecycled() && !img.equals(result)) {
//				img.recycle();
			}
		}

		return result;
	}

	@Override
	public int getNameResourceId() {
		return R.string.home_tab_me;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.v1_home_menu_me_selector;
	}

	@Override
	public View getView() {
		return mRootView;
	}

	@Override
	public View onCreateView(Context context) {
		this.mActivity = (Activity) context;
		isMeProfile = true;
		mUserId = LoginManager.getInstance().getLoginInfo().mUserId;
		ProfileFragment.registerPhotoUploadSuccessListener(listener);
		initView();
		return mRootView;
	}

	@Override
	public void onLoadData() {
		initData();
	}

	@Override
	public void onFinishLoad() {
		mRootView.addOnScrollListener(mOnScrollListener);
	}

    @Override
    public void onResume() {
    }

    @Override
	public void onPause() {
		
	}

	@Override
	public void onDestroyData() {

	}

	@Override
	public HomeFragment.OnActivityResultListener onActivityResult() {
		OnActivityResultListener resultListener = new OnActivityResultListener() {
			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
					switch (requestCode) {
					/* 拍照上传头像 */
					case PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO:
						Intent intent = new Intent(mActivity, HeadEditActivity.class);
						intent.putExtra(Intent.EXTRA_STREAM, PhotoUploadManager.getInstance().mUri);
						mActivity.startActivity(intent);
						break;
					/* 本地上传头像 */
					case PhotoUploadManager.REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY:
						if(null!=data){
						Intent intent1 = new Intent(mActivity, HeadEditActivity.class);
						intent1.putExtra(Intent.EXTRA_STREAM, data.getData());
						mActivity.startActivity(intent1);
						break;
					}
				}
				
			}
		};
		return resultListener;
	}
}

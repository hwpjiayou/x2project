package com.renren.mobile.x2.components.home.feed;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.comment.CommentActivity;
import com.renren.mobile.x2.components.home.profile.ProfileActivity;
import com.renren.mobile.x2.components.imageviewer.ImageViewActivity;
import com.renren.mobile.x2.components.login.LoginManager;
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
import com.renren.mobile.x2.utils.DateFormat;
import com.renren.mobile.x2.utils.DipUtil;
import com.renren.mobile.x2.utils.FileUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.Response;
import com.renren.mobile.x2.utils.img.ImageLoader.TagResponse;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PlayerThread;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnPlayerListenner;
import com.renren.mobile.x2.utils.voice.PlayerThread.PlayRequest;

/**
 * 
 * @author jia.xia
 * 
 */
public class FeedAdapter extends BaseAdapter {

	// 当前Adapter中的list数据
	public List<FeedModel> mModelList = new ArrayList<FeedModel>();
	// 预加载的数据存放地点
	public List<FeedModel> mNextModelList = new ArrayList<FeedModel>();
	// Top10的数据存放地点
	public List<FeedModel> mTop10List = new ArrayList<FeedModel>();
	/**
	 * Feed和Top10 的状态（ true：Feed |false ：Top10 ）由点击Button触发 默认为True。
	 */
	protected boolean mFeedStatus = true;

	public static final int FEED_ONE_PAGE_COUNT = 10;
	public static final int FEED_MAX_COUNT = 200;
	/**
	 * 点击评论按钮触发的类型
	 */
	public static final String FEED_COMMENTBUTTON_TYLE = "commentButtonType";
	/**
	 * 点击整个Item触发的类型
	 */
	public static final String FEED_ITEMCLICK_TYLE = "itemClickTyle";
	
	public static final int FEED_COUNT_MAX = 10;
	private  Context mContext;
	protected LayoutInflater mInflater;
	private int mCount = 1000;
	public static boolean isScrolling = false;

	// 请求头像的ImageLoader
	private  ImageLoader mHeadImageLoader = null;
	// 请求非头像的ImageLoader
	private  ImageLoader mFeedImageLoader = null;

	private String mImageUrl = null;
	
	private FeedCommentClickListener mFeedCommentClickListener = null;
	Bitmap testDefault = null;
	FeedView mFeedView;
	/**
	 * 存储当点击进入评论后，重新回到Feed中用到
	 */
	public static int mItemPosition = -1;
	
	public static final int IMG_WIDTH_MAX = 384;
	public static final int IMG_WIDTH_MIN = 174;
	public static final int IMG_HEIGHT_MAX = 384;
	public static final int IMG_HEIGHT_MIN = 87;
	
	
	private int mVoiceCount = 0;
	Handler mHandler = null;
	public static final int VOICE_PALY = 1;  //播放状态
	public static final int VOICE_STOP = 2;  //暂停状态
	public static final int VOICE_PREPRE = 3;   //默认状态（没有播放或者已经播放完的状态）
	public int voicePlayStatus = VOICE_PREPRE;   
	
	
	/**
	 * 当前正在播放的语音model
	 */
	public UGCVoiceModel mCurrentVoiceModel = null ;

	public FeedAdapter(Context context, List<FeedModel> list) {
		setContext(context);
		this.mModelList = list;
	}

	public FeedAdapter(Context context,FeedView view) {
		this.mFeedView = view;
		setContext(context);
	}
	
	public FeedAdapter(Context context){
		setContext(context);
	}
	
	public boolean ismFeedStatus() {
		return mFeedStatus;
	}                         

	public synchronized void setmFeedStatus(boolean mFeedStatus) {
		this.mFeedStatus = mFeedStatus;
	}

	public void setCommentClickListener(FeedCommentClickListener listener){
		this.mFeedCommentClickListener = listener;
	}
	
	/**
	 * 设置Context
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		this.mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initImageLoader();
		//initMessage();
	}
/**
 * notifyDataSetChanged();
 */
	public void refresh() {
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
		
	}

	@Override
	public int getCount() {
		if (mFeedStatus)
			return mModelList.size();
		else {
			return mTop10List.size();
		}
	}

	public void setScroll(boolean isScrolling) {
		this.isScrolling = isScrolling;
	}

	@Override
	public Object getItem(int position) {
		if (mFeedStatus) {
			return mModelList.get(position);
		} else {
			return mTop10List.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Logger.logd("jxx",
				"getView:" + position + "||listsize:" + mModelList.size());
		Logger.logd("jxx", "getView:position:" + position);
		//记录位置
		
		FeedViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.feed_adapter_item_layout,
					null);
			holder = new FeedViewHolder();
			ViewMapUtil.viewMapping(holder, convertView);
		} else {
			holder = (FeedViewHolder) convertView.getTag();
		}
		convertView.setTag(holder);
		FeedModel feedDataModel = null;
		if (mFeedStatus) {
			feedDataModel = mModelList.get(position);
		} else {
			feedDataModel = mTop10List.get(position);
		}
		//记录位置
		feedDataModel.setmPosition(position);
		
		Logger.logd("x", "getView:url:" + holder.url);
		setViewHolderData(holder, feedDataModel,
				mFeedView.mFeedListView);
		
		if(position == 0){  //将头部竖线去掉
			holder.mHeadVerticalLine.setVisibility(View.GONE);
		}
		else{
			holder.mHeadVerticalLine.setVisibility(View.VISIBLE);
		}
		
		if(position == mModelList.size()-1){
			//holder.mVerticalLastLine.
			holder.mVerticalLastLine.setVisibility(View.VISIBLE);
		}
		else{
			holder.mVerticalLastLine.setVisibility(View.GONE);
		}
		final FeedModel m = feedDataModel;
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 点击调转评论
				setItemPosition(position); //存储位置
				Intent intent = new Intent();
				intent.setClass(mContext,CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("feedmodel",m);
				bundle.putInt("feed_layout_id", R.layout.feed_adapter_item_layout);
				bundle.putString("itemClickTyle", FeedAdapter.FEED_ITEMCLICK_TYLE);
				intent.putExtras(bundle);
				((Activity)mContext).startActivityForResult(intent, FeedManager.ACTIVITY_REQUEST_CODE);
			}
		});
		
		return convertView;
	}

	
	public void setItemPosition(int position){
		mItemPosition = position;
	}
	
	public void initImageLoader() {
		if (mFeedImageLoader == null) {
			mFeedImageLoader = ImageLoaderManager.get(
					ImageLoaderManager.TYPE_FEED, mContext);
		}
		if (mHeadImageLoader == null) {
			mHeadImageLoader = ImageLoaderManager.get(
					ImageLoaderManager.TYPE_HEAD, mContext);
		}
	}
/**
 * 设置头像类图片
 * @param image ImageView  
 * @param listview  ListView
 * @param url   String
 */
	public void setHeadImageUtil(final ImageView image,final ListView listview ,final String url,int gender){
		if(url  == null ||url.equals(""))
			return;
		image.setTag(url);
		HttpImageRequest request = new HttpImageRequest(
				url, !FeedAdapter.isScrolling);
		Bitmap bitmap = mHeadImageLoader.getMemoryCache(request);
		if (bitmap != null) {
			image.setImageBitmap(bitmap);
		} else {
			TagResponse<String> headResponse = new TagResponse<String>(
					url) {

				@Override
				public void failed() {

				}

				@Override
				protected void success(final Bitmap bitmap, String tag) {
					if (!FeedAdapter.isScrolling
							&& url.equals(tag)) {
						RenrenChatApplication.getUiHandler().post(
								new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										ImageView image = (ImageView) listview
												.findViewWithTag(url);
										if (image != null){
											image.setImageBitmap(bitmap);
										}
									}
								});

					}
				}
			};
			if(gender == UGCUserModel.GENDER_FAMALE){
				image.setImageResource(R.drawable.v1_default_famale);
			}
			else{
				image.setImageResource(R.drawable.v1_default_male);
			}
			mHeadImageLoader.get(request, headResponse);
		}
	}
	
	
	/**
	 * 设置非头像类图片
	 * @param image
	 * @param listview
	 * @param url
	 */
	public void setImageUtil(final ImageView image,final ListView listview ,final String url){
		if(url == null || url.equals("")){
			return;
		}
		image.setTag(url);
		
		HttpImageRequest request = new HttpImageRequest(
				url, !FeedAdapter.isScrolling);
		Bitmap bitmap = mFeedImageLoader.getMemoryCache(request);
		if (bitmap != null) {
			
			image.setImageBitmap(bitmap);
		} else {
			Response response = new Response() {

				@Override
				public void success(final Bitmap bitmap) {

					RenrenChatApplication.getUiHandler().post(new Runnable() {

						@Override
						public void run() {

							if (!FeedAdapter.isScrolling) {
								ImageView image = (ImageView) listview
										.findViewWithTag(url);
								if (image != null) {
									image.setImageBitmap(bitmap); 
								}

							}
						}
					});
				}

				@Override
				public void failed() {

				}
			};
			image.setImageDrawable(null);
			mFeedImageLoader.get(request, response);
		}
	}
	/**
	 * 根据服务器端下发的w和H设置Layout
	 * @param width
	 * @param height
	 * @return
	 */
	public android.widget.FrameLayout.LayoutParams getImageLayoutParams(int width,int height){
		
		android.widget.FrameLayout.LayoutParams layout = null;
		
		int srceenwidth = RenrenChatApplication.getScreenWidth();
		int newWidth = srceenwidth-130;
//		//宽度下于设置值，则取自身
//		if(width<=newWidth){ 
//			newWidth = width;
//		}
		//根据宽度计算高度
		int newHeight =  ((height*100/width)*(newWidth))/100;
		//高度不能高于宽度的2倍
		if(newHeight/newWidth>=2){
			newHeight = newWidth*2;
		}
//		else{   
//			//小于新的高度，取本身高度
//			if(height<newHeight)
//				newHeight = height;
//		}
//		float scaleWidth = (float)newWidth/width;
//		float scalHeight = (float)newHeight/height;
		
		layout = new android.widget.FrameLayout.LayoutParams(newWidth, newHeight);
		Logger.logd("layout", "h:"+height+"|w:"+width);
		Logger.logd("layout", "h:"+newHeight+"|w:"+newWidth);
		return layout;
	}
	
	/**
	 * 设置headUrl username 数据和事件
	 */
	public void setHeadHolderData(final FeedViewHolder holder,
			final FeedModel userModel, boolean isScrolling,
			final ListView listview) {
		final UGCUserModel model = userModel.getmPostContentModel().user;
		Logger.logd("xxx", "setHeadHolderData:" + userModel);
		Logger.logd("xxx", "setHeadHolderData:headurl"
				+ model.mSmalHeadlUrl);
		
		setHeadImageUtil(holder.mHeadImage, listview, model.mSmalHeadlUrl,model.mGender);
		holder.mHeaderImage.setImageResource(R.drawable.v1_feed_headimg_bg);
		holder.mHeadImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mContext!=null){
					ProfileActivity.show(mContext, model.mId);
				}
			
			}
		});
		holder.mUserName.setText(model.mName);

	}

	/**
	 * 设置评论数据
	 * 
	 * @param holder
	 * @param feedDataModel
	 */
	public void setCommentHolderData(final FeedViewHolder holder,
			final FeedModel feedDataModel,ListView listview) {
		/********************** start comment layout ****************************/
		int commentCount = 0;
		if(feedDataModel.getmChatFeedCommentContent()!=null){
			commentCount = feedDataModel.getmChatFeedCommentContent().size();
		}
		
		if (commentCount > 0) {
			holder.mCommentLayout.setVisibility(View.VISIBLE);
			List<FeedCommentContent> commentList = feedDataModel
					.getmChatFeedCommentContent();
			if (commentList != null && commentList.size() > 0) {
				// comment1
				FeedCommentContent comment1 = commentList.get(0);
				holder.mComment1Layout.setVisibility(View.VISIBLE);
				//设置头像
				setHeadImageUtil(holder.mComment1HeadImg, listview, comment1.mUser.mSmalHeadlUrl,comment1.mUser.mGender);
				//设置人名
				if(comment1.mUser.mName!=null&&comment1.mUser.mName.length()>0){
					holder.mComment1Username.setText(comment1.mUser.mName);
				}
				//设置文本
				UGCTextModel tModel = (UGCTextModel)comment1.contentInfo.get(UGCModel.UGCType.TEXT);
				if(tModel != null){
					holder.mComment1Content.setVisibility(View.VISIBLE);
					//((FeedComment1ViewHolder)holder).mVoiceLayout.setVisibility(View.GONE);
					Logger.logd("comment", "text1:"+tModel.mText);
					holder.mComment1Content.setText(new EmotionString(tModel.mText));
				}
				
//				UGCVoiceModel voiceModel = (UGCVoiceModel)comment1.contentInfo.get(UGCModel.UGCType.VOICE);
//				if(voiceModel!=null){
//					holder.mComment1Content.setVisibility(View.GONE);
//					((FeedComment1ViewHolder)holder).mVoiceLayout.setVisibility(View.VISIBLE);
//				}
					
				
				//String commentData = DateFormat.getNowStr(comment1.getTime());
				
				// /comment2
				if (commentList.size() > 1) {
					FeedCommentContent comment2 = commentList.get(1);
					holder.mComment2Layout.setVisibility(View.VISIBLE);
					setHeadImageUtil(holder.mComment2HeadImg, listview, comment2.mUser.mSmalHeadlUrl,comment2.mUser.mGender);
					if(comment2.mUser.mName!=null&&comment2.mUser.mName.length()>0)
						holder.mComment2Username.setText(comment2.mUser.mName);
					
					UGCTextModel t2Model = (UGCTextModel)comment2.contentInfo.get(UGCModel.UGCType.TEXT);
					if(t2Model != null){
						Logger.logd("comment", "text2:"+t2Model.mText);
						holder.mComment2Content.setText(new EmotionString(t2Model.mText));
					}
						
					
				}

			}

		}

		/********************** end comment layout ****************************/
	}

	/**
	 * 设置Post数据
	 * @param holder       FeedViewHolder
	 * @param postList   Map<String,PostModel>
	 * @param isScrolling  boolean 
	 * @param listview
	 */
	public void setPostHolderData(final FeedViewHolder holder,
			final Map<String, UGCModel> postList, boolean isScrolling,
			ListView listview) {
		Logger.logd("jxx", "setPostHolderData:" + postList);
		boolean hasImage = false;  // 图片控件标志位
		boolean hasVoice = false;  //语音控件标志位
		
		// TODO 图片的放置，处理很多 ，优先级很高

		UGCImgModel postImage = (UGCImgModel) postList
						.get(UGCModel.UGCType.IMG);
		if (postImage != null&&postImage.mOriginalUrl!=null&&postImage.mOriginalUrl.length()>0) {
			hasImage = true;
			setPostImageData(holder, postImage, isScrolling, listview);
		}
		
		// TODO 语音的放置，处理也很多
		UGCVoiceModel postVoice = (UGCVoiceModel) postList
						.get(UGCModel.UGCType.VOICE);
		if (postVoice != null) {
			hasVoice = true;
			holder.mBottomLayout.setVisibility(View.VISIBLE);
			holder.mPhotoVoiceLayout.setVisibility(View.VISIBLE);
			holder.mVoiceLayout.setVisibility(View.VISIBLE);
			setPostVoiceData(holder, postVoice,hasImage);
		}
		String tagName = "";
		// TODO Tag的放置
		UGCTagModel postTag = (UGCTagModel) postList
						.get(UGCModel.UGCType.TAG);
		if (postTag != null) {
			tagName = postTag.mName;
		}
		setPostTagData(holder, postTag);
//		else{ //无Tag时显示默认Tag
//			CommonUtil.log("xj", "setPosttagData: posttag == null");
//			holder.mBottomLayout.setVisibility(View.VISIBLE);
//			holder.mTagText.setText(tagName);
//		}
		//TODO  文本的放置
		UGCTextModel postText = (UGCTextModel) postList
				.get(UGCModel.UGCType.TEXT);
		if (postText != null) {
			setPostTextData(holder, postText,tagName,hasImage,hasVoice);
		}

		if(postImage == null||postImage.mOriginalUrl==null||postImage.mOriginalUrl.length()==0){  //when has not image
			if(postVoice ==null||postVoice.mUrl ==null||postVoice.mUrl.length() == 0){  //when has not voice
				Logger.logd("jxx", "Gone");
				holder.mTagText.setVisibility(View.GONE);
			}
			else{   //when has voice
				Logger.logd("jxx", "background null");
				//LayoutParams layout = (RelativeLayout.LayoutParams)holder.mTagText.getLayoutParams();
				LayoutParams layout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layout.addRule(RelativeLayout.RIGHT_OF, R.id.feed_bottom_tag_text);
				holder.mPhotoVoiceLayout.setLayoutParams(layout);
				LayoutParams taglayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				taglayout.topMargin=DipUtil.calcFromDip(9);
				taglayout.leftMargin = 0;
				holder.mTagText.setLayoutParams(taglayout);
				if(postTag!=null){
					holder.mTagText.setText(postTag.mName+": ");
				}
				
				holder.mTagText.setBackgroundDrawable(null);
				holder.mTagText.setTextColor(Color.BLACK);
				
			}
		}
		else{
			Logger.logd("jxx", "has image");
			if(tagName.length()>0){
				holder.mTagText.setVisibility(View.VISIBLE);
				holder.mTagText.setTextColor(Color.WHITE);
				LayoutParams layout = (LayoutParams)holder.mTagText.getLayoutParams();
				layout.leftMargin=DipUtil.calcFromDip(4);
				layout.topMargin=DipUtil.calcFromDip(4);
				holder.mTagText.setBackgroundResource(R.drawable.feed_tag_bg);
			}
			else{
				holder.mTagText.setVisibility(View.GONE);
			}
			
			
		}
		// TODO Place的放置
				UGCPlaceModel postPlace = (UGCPlaceModel) postList
						.get(UGCModel.UGCType.PLACE);
				if (postPlace != null) {
					setPostPlaceData(holder, postPlace);
				}
		/************以下部分现在基本用不到*******************/
		

		// TODO Action的放置
		UGCActionModel postAction = (UGCActionModel) postList
				.get(UGCModel.UGCType.ACTION);
		if (postAction != null) {
			setPostActionData(holder, postAction);
		}

	}
	
	/**
	 *  设置文本的放置布局
	 * @param holder :FeedViewHolder
	 * @param textModel :UGCTextModel
	 */
	public void setPostTextData(final FeedViewHolder holder,
			final UGCTextModel textModel,String tagname,boolean hasImage,boolean hasVoice){
		holder.mBottomLayout.setVisibility(View.VISIBLE);
		holder.mContentText.setVisibility(View.VISIBLE);
		Logger.logd("xj", "setPostTextData:"+textModel.mText);
		//当图片和语音都没有时，需对文本进行特殊的处理。当两者都有或有其一时，都无需进行处理
		if(!hasImage&&!hasVoice&&!tagname.equals("")){
			EmotionString e = new EmotionString(tagname+": "+textModel.mText);
			e.setTextSize(0, tagname.length(), 18, false);
			holder.mContentText.setText(e);
		}
		else{
			holder.mContentText.setText(new EmotionString(textModel.mText));
		}
		
	}
	
	/**
	 * 设置Action显示
	 * 
	 * @param holder
	 * @param actionModel
	 */
	public  void setPostActionData(final FeedViewHolder holder,
			final UGCActionModel actionModel) {
		
	}

	/**
	 * 设置Place显示
	 * 
	 * @param holder
	 * @param placeModel
	 */
	public void setPostPlaceData(final FeedViewHolder holder,
			final UGCPlaceModel placeModel) {
		Logger.logd("log", "place:"+placeModel.mName);
		
		if(placeModel.mName!=null&&!placeModel.mName.equals("")){
			holder.mLocationLayout.setVisibility(View.VISIBLE);
			holder.mLocationNameText.setText(placeModel.mName);
		}
		else{
			//holder.mLocationNameText.setText("服务器没有下发数据");
			holder.mLocationLayout.setVisibility(View.GONE);
		}
		
	}

	/**
	 * 设置Tag显示
	 * 
	 * @param holder
	 * @param tagModelset
	 */
	public void setPostTagData(final FeedViewHolder holder,
			final UGCTagModel tagModel) {
		if(tagModel!=null){
			Logger.logd("log", "tag:"+tagModel.mName);
			holder.mBottomLayout.setVisibility(View.VISIBLE);
			
			if(tagModel.mName!=null&&tagModel.mName.length()>0)
			{
				holder.mTagText.setVisibility(View.VISIBLE);
				holder.mTagText.setText(tagModel.mName);
			}
			else{
				holder.mTagText.setVisibility(View.GONE);
			}
		}
		else{
			holder.mTagText.setVisibility(View.GONE);
		}
		
	}

	/**
	 * 设置语音显示
	 */
	public static final String voicePath = "/mnt/sdcard/x2/voice";

	public  void setPostVoiceData(final FeedViewHolder holder,
			final UGCVoiceModel voiceModel,boolean hasimage) {
//		holder.mBottomLayout.setVisibility(View.VISIBLE);
//		holder.mPhotoVoiceLayout.setVisibility(View.VISIBLE);
//		holder.mVoiceLayout.setVisibility(View.VISIBLE);
	
		Logger.logd("xjjj", "setPostVoiceData:"+hasimage);
		if(voiceModel.isDefault){   //默认状态，则显示时长，隐藏pb
			holder.mVoiceImg.setImageResource(R.drawable.v1_voice_play);
			holder.mVoiceTimeText.setVisibility(View.VISIBLE);
			holder.mVoicePlayBar.setVisibility(View.GONE);
			holder.mVoiceTimeText.setText(voiceModel.mLength+"“");
		}
		else if(voiceModel.isPlay){   //播放状态
			holder.mVoiceImg.setImageResource(R.drawable.v1_voice_stop);
			holder.mVoiceTimeText.setVisibility(View.GONE);
			holder.mVoicePlayBar.setVisibility(View.VISIBLE);
		}else if(voiceModel.isStop){//停止状态
			holder.mVoiceImg.setImageResource(R.drawable.v1_voice_play);
			holder.mVoiceTimeText.setVisibility(View.VISIBLE);
			holder.mVoicePlayBar.setVisibility(View.GONE);
			holder.mVoiceTimeText.setText(voiceModel.mLength+"“");
		}
		
		holder.mVoiceLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(voiceModel.isDefault){   //默认——>播放
					Logger.logd("xjjj", "setPostVoiceData:default-> play");
					holder.mVoiceTimeText.setVisibility(View.GONE);
					holder.mVoicePlayBar.setVisibility(View.VISIBLE);
					holder.mVoiceImg.setImageResource(R.drawable.v1_voice_stop);
					voiceModel.setPlayStatus();
					playVoiceUtil(holder,voiceModel);
				}
				else if(voiceModel.isPlay){   //播放->暂停
					Logger.logd("xjjj", "setPostVoiceData:paly-> stop");
					holder.mVoiceTimeText.setVisibility(View.VISIBLE);
					holder.mVoicePlayBar.setVisibility(View.GONE);
					holder.mVoiceImg.setImageResource(R.drawable.v1_voice_play);
					voiceModel.setStopStatus();
					playVoiceUtil(holder,voiceModel);
				}
				else if(voiceModel.isStop){   //暂停->播放
					Logger.logd("xjjj", "setPostVoiceData:stop-> play");
					holder.mVoiceTimeText.setVisibility(View.GONE);
					holder.mVoicePlayBar.setVisibility(View.VISIBLE);
					holder.mVoiceImg.setImageResource(R.drawable.v1_voice_stop);
					voiceModel.setPlayStatus();
					playVoiceUtil(holder,voiceModel);
				}
				
//				holder.mVoicePlayBar.setIndeterminate(false);
//				holder.mVoicePlayBar.setPressed(false);
//				//holder.mVoicePlayBar.setMax(58);
//				holder.mVoicePlayBar.setProgress(20);
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						Logger.logd("adapter", "voice click");
//						playVoiceUtil(holder,voiceModel);
//					}
//				}).start();
				
			}
		});
	}
	/**
	 * 播放语音
	 * @param path
	 * @param model  :UGCVoiceModel
	 */
	public void playVoice(String path,UGCVoiceModel model){
		stopVoicePlay(model);
		mCurrentVoiceModel = model;
		
		
		
		File file = new File(path);
		if(file.exists()&&file.length()>0){
			final PlayRequest request = new PlayRequest();
			request.mAbsVoiceFileName = path;
			if(model.mPlayListener == null){
				model.mPlayListener =new OnPlayerListenner() {
					
					@Override
					public void onPlayStop() {
						
					}
					
					@Override
					public void onPlayStart() {
						
					}
					
					@Override
					public void onPlayPlaying() {
						
					}
				};
			}
			request.mPlayListenner = model.mPlayListener;
			if (PlayerThread.getInstance().forceToPlay(request)) {
				PlayerThread.getInstance().onAddPlay(request);
			}
		}
		else{
			downVoice(model.mUrl, path, model);
		}
	}
	
	public void stopVoicePlay(UGCVoiceModel model){
		if(model!=null){
			//非现在这个Model
			if(mCurrentVoiceModel!=null&&!model.mUrl.equals(mCurrentVoiceModel.mUrl))
				mCurrentVoiceModel.setStopStatus();
		}
		
		
		PlayerThread.getInstance().stopCurrentPlay();
	}
	
	public void downVoice(final String url,final String path,final UGCVoiceModel model){
		/*OnDownloadListenner listener = new OnDownloadListenner() {
			
			@Override
			public void onDownloadSuccess(DownloadReuqest request) {
				Logger.logd("adapter", "onDownloadSuccess");
//				final PlayRequest requestPlay = new PlayRequest();
//				requestPlay.mAbsVoiceFileName = request.mSaveFilePath;
//				if (PlayerThread.getInstance().forceToPlay(requestPlay)) {
//					PlayerThread.getInstance().onAddPlay(requestPlay);
//				}
//				RenrenChatApplication.getUiHandler().post(new Runnable() {
//					
//					@Override
//					public void run() {
//						holder.mVoicePlayBar.setPressed(true);
//						holder.mVoicePlayBar.setMax(length);
//						int i=1;
//						while(i<(length+1)){
//							Logger.logd("adapter", "progress i:"+i);
//							holder.mVoicePlayBar.setProgress(i);
//							i++;
//						}
//					}
//				});
				
				playVoice(path, model);
				
			}
			
			@Override
			public void onDownloadStart() {
				
			}
			
			@Override
			public void onDownloadPrepare() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDownloadError() {
				// TODO Auto-generated method stub
				Logger.logd("adapter", "onDownloadVoiceError");
			}
		};
		DownloadReuqest request = new DownloadReuqest();
		request.mVoiceUrl = url;
		request.mListenner = listener;
		request.mSaveFilePath = path;
		VoiceDownloadThread.getInstance().addDownloadRequest(request);
		*/
		
	
		try {
			InputStream is = mContext.getAssets().open(
					"1229120320_e9c355aa438e1223.spx");
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			while (is.read(buffer) != -1) {
				swapStream.write(buffer);
			}
			FileUtil.getInstance().saveFile(
					swapStream.toByteArray(), path);
			File files = new File(path);
			if (files.exists() && files.length() > 0) { 
				final PlayRequest request = new PlayRequest();
				request.mAbsVoiceFileName = path;
				if (PlayerThread.getInstance().forceToPlay(
						request)) {
					PlayerThread.getInstance().onAddPlay(
							request);
				}
				;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 开始语音播放的逻辑
	 * @param holder :FeedViewHolder
	 * @param model :UGCVoiceModel
	 */
	public void playVoiceUtil(final FeedViewHolder holder,final UGCVoiceModel model){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				/************进行下载或直接播放操作************************/
				model.mUrl = "1229120320_e9c355aa438e1223.spx";
				final int length = model.mLength;
				//final String url = "http://v.m.renren.com/20121109/1134/1249512497_73938f99b29c89b2.spx";
				//end test
				String filePath = voicePath + "/" + model.mUrl.hashCode();
				if(model.isPlay){   //播放
					playVoice(filePath,model);
				}
				else if(model.isStop){ //停止
					stopVoicePlay(model);
				}
				
				/*File fileRoot = new File(voicePath);
				if (!fileRoot.exists()) {
					fileRoot.mkdirs();
				}
				//String url = model.mUrl;
				//final int length = model.mLength;
				//test
				
				File file = new File(filePath);
				try {
					if (file.exists() && file.length() > 0) { // 存在则开始播放
						Logger.logd("adapter", "onplay");
						final PlayRequest request = new PlayRequest();
						request.mAbsVoiceFileName = filePath;
						if (PlayerThread.getInstance().forceToPlay(request)) {
							PlayerThread.getInstance().onAddPlay(request);
						}
						RenrenChatApplication.getUiHandler().post(new Runnable() {
							
							@Override
							public void run() {
								
								//holder.mVoicePlayBar.setProgress(0);
								holder.mVoicePlayBar.setTag(url);
								//另起线程进行进度控制
								openVoiceThread(length,url);
							}
						});
						
					} else {  //开始下载
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	*/
				
			}
		}).start();
		
	}
	/**
	 * 开启语音进度条线程
	 * @param length   时长
	 * @param handler
	 */
	public void openVoiceThread(final int length,final String url){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Logger.logd("adapter", "thread open");
				for(int i = 0;i<length;i++){
					Logger.logd("adapter", "thread :"+i);
					mVoiceCount = (i+1);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message m = new Message();
					m.what = VOICE_PALY;
					m.obj = url;
					//mHandler.sendMessage(m);
				}
			}
		}).start();
	}
	
	/**
	 * 
	 * @param CommentViewHolder
	 * @param UGCImgModel
	 */
	public  void setPostImageData(final FeedViewHolder holder,
			final UGCImgModel imgModel, boolean isScrolling,
			final ListView listview) {
		holder.mBottomLayout.setVisibility(View.VISIBLE);
		holder.mPhotoVoiceLayout.setVisibility(View.VISIBLE);
		holder.mSinglePhotoLayout.setVisibility(View.VISIBLE);
		holder.mSinglePhotoImage.setVisibility(View.VISIBLE);
		holder.mSinglePhotoFrontBg.setVisibility(View.VISIBLE);
		holder.mPhotoVoiceLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		Logger.logd("layout", imgModel.mOriginalWidth+"|"+imgModel.mOriginalHeight);
		android.widget.FrameLayout.LayoutParams layout = getImageLayoutParams(imgModel.mOriginalWidth, imgModel.mOriginalHeight);
		holder.mSinglePhotoImage.setLayoutParams(layout);
		holder.mSinglePhotoFrontBg.setLayoutParams(layout);
		
		setImageUtil(holder.mSinglePhotoImage, listview, imgModel.mOriginalUrl);
		
		holder.mSinglePhotoImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageViewActivity.show(mContext, imgModel.mOriginalUrl,false);
			}
		});
		// }
	}

	/**
	 * 设置赞按钮和发消息按钮消息
	 * 
	 * @param holder
	 * @param feedDataModel
	 */
	public void setLikeAndReblogHolderData(final FeedViewHolder holder,
			final FeedModel feedDataModel) {
		
	}
/**
 * 设置发布时间，评论数，喜欢数以及内部逻辑
 * @param holder:FeedViewHolder
 * @param feedDataModel :FeedModel
 */
	public void setTimeCommentCountLikeCountData(final FeedViewHolder holder,
			final FeedModel feedDataModel){
		Map<String,UGCModel>content = feedDataModel.getmPostContentModel().contentInfo;
		if(content == null ) return;
		
		UGCTagModel tagModel = 	(UGCTagModel)content.get(UGCModel.UGCType.TAG);
		if(tagModel!=null&&tagModel.getIconUnPressResourceIdForFeed()!=0){
			Logger.logd("type", "id:"+tagModel);
			holder.mTagTypeImg.setImageResource(tagModel.getIconUnPressResourceIdForFeed());
		}
		else{
			Logger.logd("type", "type is null");
			holder.mTypeHLine.setVisibility(View.VISIBLE);
			holder.mTypeVLine.setVisibility(View.VISIBLE);
			holder.mTagTypeImg.setImageResource(R.drawable.v1_tag_default);
		}
		
		String date = DateFormat.getNowStr(feedDataModel.mCreateTime);
		holder.mPublisherTime.setText(date);
		/*******************Like*****************************/
		int commentCount = feedDataModel.getmCommentCount();
		int likeCount = feedDataModel.getmLikeCount();
		Logger.logd("comment", "commentCount:"+commentCount);
		if(commentCount == 0){
			holder.mCommentCountText.setVisibility(View.GONE);
		}
		else{
			holder.mCommentCountText.setVisibility(View.VISIBLE);
		}
		if(likeCount == 0){
			holder.mLikeCountText.setVisibility(View.GONE);
		}
		else{
			holder.mLikeCountText.setVisibility(View.VISIBLE);
		}
		String cCount = Integer.toString(commentCount);
		String lCount = Integer.toString(likeCount);
		
		if(feedDataModel.getmCommentCount()>=10000){
			cCount = RenrenChatApplication.getApplication().getResources().getString(R.string.feed_count_max);
		}
		
		
		if(feedDataModel.getmLikeCount()>=10000){
			lCount = RenrenChatApplication.getApplication().getResources().getString(R.string.feed_count_max);
		}
//		//test
//		likeCount = RenrenChatApplication.getApplication().getResources().getString(R.string.feed_count_max);
//		commentCount = RenrenChatApplication.getApplication().getResources().getString(R.string.feed_count_max);
//		//end test
		holder.mLikeCountText.setText(lCount);
		
		holder.mCommentCountText.setText(cCount);
		//TODO   这个地方有没有服务器的字段进行mLikeImg的图片设置
		
		setLikeButton(holder, feedDataModel, false);
		
		holder.mLikeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logger.logd("x123", "like clike");
				setLikeButton(holder,feedDataModel,true);
			}
		});
		
		holder.mCommentImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logger.logd("feedadapter", "commentButton click:");
				Intent intent = new Intent();
				intent.setClass(mContext,CommentActivity.class);
				Bundle bundle = new Bundle();
				FeedModel model = feedDataModel;
				//设置点击所在的Item
				mItemPosition = feedDataModel.getmPosition();
				
				bundle.putSerializable("feedmodel",model);
				bundle.putInt("feed_layout_id", R.layout.feed_adapter_item_layout);
				bundle.putString("commentButtonType", FEED_COMMENTBUTTON_TYLE);
				intent.putExtras(bundle);
				((Activity)mContext).startActivityForResult(intent, FeedManager.ACTIVITY_REQUEST_CODE);
			}
		});
	}
	
	/**
	 * 设置喜欢按钮状态
	 * @param holder
	 * @param feedDataModel
	 * @param isClick   
	 */
	/**
	 * @param holder
	 * @param feedDataModel
	 * @param isClick
	 */
	public void setLikeButton(FeedViewHolder holder,FeedModel feedDataModel,boolean isClick){
		boolean isLike = feedDataModel.ismIsLike();
		if(isLike){ 
			if(isClick){ //设置为不喜欢，发送网络请求
				int count = feedDataModel.getmLikeCount()-1;
				
				if(count>=10000){
					holder.mLikeCountText.setVisibility(View.VISIBLE);
					holder.mLikeCountText.setText(RenrenChatApplication.getApplication().getResources().getString(R.string.feed_count_max));
				}
				else{
					if(count>0){
						holder.mLikeCountText.setVisibility(View.VISIBLE);
						holder.mLikeCountText.setText(Integer.toString(count));
					}
					else{
						holder.mLikeCountText.setVisibility(View.GONE);
					}
					
				}
				
				
				feedDataModel.setmLikeCount(count);
				holder.mLikeImg.setImageResource(R.drawable.feed_unlike_unpress);
				feedDataModel.setmIsLike(!isLike);
				sendUnLike(feedDataModel);
			}
			else{
				holder.mLikeImg.setImageResource(R.drawable.feed_like_unpress);
			}
		}
		else{ 
			if(isClick){ //设置为喜欢，发送网络请求。
				holder.mLikeCountText.setVisibility(View.VISIBLE);
				int count = feedDataModel.getmLikeCount()+1;
				if(count>=10000){
					holder.mLikeCountText.setText(RenrenChatApplication.getApplication().getResources().getString(R.string.feed_count_max));
				}
				else{
					holder.mLikeCountText.setText(Integer.toString(count));
				}
				feedDataModel.setmLikeCount(count);
				holder.mLikeImg.setImageResource(R.drawable.feed_like_unpress);
				feedDataModel.setmIsLike(!isLike);
				sendLike(feedDataModel);
			}
			else{
				holder.mLikeImg.setImageResource(R.drawable.feed_unlike_unpress);
			}
			
		}
		
	}
	/**
	 * 取消喜欢操作，网络请求
	 * @param feedModel
	 */
	public void sendUnLike(FeedModel feedModel){
		
	}
	
	/**
	 * 发送喜欢请求
	 * @param feedModel FeedModel
	 */
	public void sendLike(FeedModel feedModel){
		INetResponse response = new INetResponse() {
			
			@Override
			public void response(INetRequest req, JSONObject obj) {
				if(obj!=null){
					if(Methods.checkNoError(req, obj)){
						Logger.logd("log", "send Like return json:"+obj.toString());
					}
				}
			}
		};
		String school_id = LoginManager.getInstance().getLoginInfo().mSchool_id;
		String feedId = Long.toString(feedModel.getFeedId());
		HttpMasService.getInstance().postLike(school_id, feedId, response);
	}
	
	/**
	 * 设置View的数据
	 */
	public void setViewHolderData(final FeedViewHolder holder,
			final FeedModel feedDataModel,ListView listview) {
		holder.mBottomLayout.setVisibility(View.GONE);
		holder.mPhotoVoiceLayout.setVisibility(View.GONE);
		holder.mSinglePhotoFrontBg.setVisibility(View.GONE);
		holder.mSinglePhotoLayout.setVisibility(View.GONE);
		holder.mVoiceLayout.setVisibility(View.GONE);
		holder.mTagText.setVisibility(View.GONE);
		holder.mContentText.setVisibility(View.GONE);
		holder.mLocationLayout.setVisibility(View.GONE);
		holder.mCommentLayout.setVisibility(View.GONE);
		holder.mComment1Layout.setVisibility(View.GONE);
		holder.mComment2Layout.setVisibility(View.GONE);
		
		setTimeCommentCountLikeCountData(holder,feedDataModel);
		
		UGCContentModel UGCModel = feedDataModel.getmPostContentModel();
		
		if(UGCModel.user!=null)
			setHeadHolderData(holder, feedDataModel, isScrolling, listview);

		
		if(UGCModel.contentInfo!=null)
			setPostHolderData(holder, UGCModel.contentInfo, isScrolling, listview);
		
		setCommentHolderData(holder, feedDataModel,listview);
		
		setLikeAndReblogHolderData(holder, feedDataModel);

	}
	
	
	/**
	 * 重置Top10
	 * 
	 * @param array
	 */
	public void reset(final JSONArray array) {
		if (array == null || array.length() == 0)
			return;
		RenrenChatApplication.getUiHandler().post(new Runnable() {

			@Override
			public void run() {
				mTop10List.clear();
				try {
					JSONObject object = null;
					for (int i = 0; i < array.length(); i++) {
						object = array.getJSONObject(i);
						FeedModel model = new FeedModel(object);
						mTop10List.add(model);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				notifyDataSetChanged();
//				if (mFeedView.mFeedManager.mProgressBar.isShown())
//					mFeedView.mFeedManager.mProgressBar
//							.setVisibility(View.GONE);
				mFeedView.addTop10Black();
			}
		});
	}

	/**
	 * 重置FeedList
	 * 
	 * @param array
	 *            JSONArray
	 * @param isRefresh
	 *            是否刷新
	 */
	public void reset(final JSONArray array, final boolean isRefresh,
			final boolean isFirstLoading,final int count) {

		Logger.logd("x11", "reset:count:" + count);
		if (array == null || array.length() == 0) {
			return;
		}
		if (isRefresh || isFirstLoading) { // 如果是下拉刷新，则要清空当前List和缓存List
			Logger.logd("xj", "isRefresh || isFirstLoading");
			RenrenChatApplication.getUiHandler().post(new Runnable() {

				@Override
				public void run() {
					clear();      //清空ModelList
					changeToTwoList(array);   //将NextList中的数据分一半给ModelList
					Logger.logd("xj", "UI thread changtoTwo");
					/*try {
						JSONObject object = null;
						for (int i = 0; i < array.length(); i++) {
							object = array.getJSONObject(i);
							FeedModel model = new FeedModel(object);
							mModelList.add(model);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}*/
					notifyDataSetChanged();
					if(count >= FEED_ONE_PAGE_COUNT){  //当等于最大数时，才加入更多的按钮
						mFeedView.setLoadMoreItem();
						if(mFeedView.mAddItem!=null)
							mFeedView.mAddItem.syncNotifyLoadComplete();
					}
					
				}
			});

		} else {  //自动加载返回的数据
			Logger.logd("xj", "is not isRefresh || isFirstLoading");
			
			setDataToNextModeList(array);
			
			/*RenrenChatApplication.getUiHandler().post(new Runnable() {
				
				@Override
				public void run() {
					try {
						JSONObject object = null;
						for (int i = 0; i < array.length(); i++) {
							object = array.getJSONObject(i);
							FeedModel model = new FeedModel(object);
							mModelList.add(model);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					notifyDataSetChanged();
					if(count>=FEED_COUNT_MAX){   //加入自动加载控件
						mFeedView.setLoadMoreItem();
						if(mFeedView.mAddItem!=null)
							mFeedView.mAddItem.syncNotifyLoadComplete();
					}
					else{   //不足，则去掉控件
						if(mFeedView.mAddItem!=null)
							mFeedView.mAddItem.syncNotifyLoadComplete();
						mFeedView.removeLoadMoreItem();
					}
					
				}
			});*/
		}
	}
	/**
	 * 主要用于第一次网络请求或者刷新操作
	 * 将JSon数据放入2个List中
	 * @param array JSONArray
	 */
	public void changeToTwoList(JSONArray array) {
		if(array == null) return;
		Logger.logd("feed", "changeToTwoList:"+array.length());
		JSONObject object = null;
		int length = array.length();
		for (int i = 0; i < length; i++) {
			object = array.optJSONObject(i);
			FeedModel model = new FeedModel(object);
			if(i<FEED_ONE_PAGE_COUNT){   
				mModelList.add(model);
			}
			else{
				mNextModelList.add(model);
			}
		}
	}
	/**
	 * 将Json文件放入缓存List中
	 * @param array JSONArray
	 */
	public void setDataToNextModeList(JSONArray array){
		Logger.logd("feed", "setDataToNextModeList");
		if(array == null||array.length() == 0) return;
			
		JSONObject object = null;
		synchronized (mNextModelList) {
			int length = array.length();
			for (int i = 0; i < length; i++) {
				object = array.optJSONObject(i);
				FeedModel model = new FeedModel(object);
				mNextModelList.add(model);
			}
		}
		
		if(mFeedView.mAddItem == null){  //数据比显示要晚到来，则需要把加载更多加上，并且通知
			copyNextPager();
		}
		
	}
	

	/**
	 * 将缓存中的10条数据放入Adapter中。
	 * return boolean :(true:还有后续新鲜事)(false : 没有后续新鲜事)
	 */
	public boolean copyNextPager() {
		int count = mNextModelList.size();
		Logger.logd("feed", "copyNextPager:"+count);
		//主要是让快速滑动停止
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//int count = mNextModelList.size();
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				int nextCount = mNextModelList.size();
//				Logger.logd("jj", "copyNextPager count:"+nextCount);
				if (nextCount > 0) {
					synchronized (mNextModelList) {
						for (int i = 0; i < nextCount; i++) {
							FeedModel model = (FeedModel) mNextModelList.get(i);
							mModelList.add(model);
						}
						// 清空缓存，通知UI Thread
						mNextModelList.clear();
					}
					//对加载更多的控件进行操作（加上或者去掉）
					refresh();
				}
				if(nextCount>=FEED_ONE_PAGE_COUNT){   //加入自动加载控件
					Logger.logd("feed", "copyNextPager: more");
					mFeedView.setLoadMoreItem();
					if(mFeedView.mAddItem!=null)
						mFeedView.mAddItem.syncNotifyLoadComplete();
				}
				else{   //不足，则去掉控件
					Logger.logd("feed", "copyNextPager: no more:"+mFeedView.mAddItem);
					if(mFeedView.mAddItem!=null)
						mFeedView.mAddItem.syncNotifyLoadComplete();
					mFeedView.removeFeedLoadMoreItem();
				}
				
			
			}
		});
		if(count >= FEED_ONE_PAGE_COUNT){ 
			return true;
		}

		return false;
	}

	/**
	 * 下拉刷新清空当前列表
	 */
	protected void clear() {
		mModelList.clear();
		mNextModelList.clear();
	}

	
	ProgressBar mProgress;

	public void initMessage() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				Logger.logd("adapter", "handler msg");
				switch (msg.what) {
				case 0:
					RenrenChatApplication.getUiHandler().post(new Runnable() {

						@Override
						public void run() {
							Logger.logd("jj", "handler setProgressVisible false");
							mProgress.setVisibility(View.GONE);
							mFeedView.setLoadMoreItem();
							if(mFeedView.mAddItem!=null)
								mFeedView.mAddItem.syncNotifyLoadComplete();
							notifyDataSetChanged();
						}
					});
					break;
				case VOICE_PALY:
					final String url = (String)msg.obj;
							ProgressBar p = (ProgressBar)mFeedView.mFeedListView.findViewWithTag(url);
							Logger.logd("adapter", "handler find"+p);
							if(p!=null){
								Logger.logd("adapter", "handler find progressbar:"+mVoiceCount);
								p.setProgress(mVoiceCount+25);
								//p.invalidate();
							}
					break;
				default:
					super.handleMessage(msg);
					break;
				}

			}

		};
		
		mProgress = new ProgressBar(mContext);
		mProgress.setVisibility(View.GONE);
	}

}

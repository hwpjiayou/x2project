package com.renren.mobile.x2.components.comment;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread.DownloadReuqest;
import com.renren.mobile.x2.components.chat.util.VoiceDownloadThread.OnDownloadListenner;
import com.renren.mobile.x2.components.chat.view.ListViewWarpper.OnFingerTouchListener;
import com.renren.mobile.x2.components.home.feed.FeedCommentContent;
import com.renren.mobile.x2.components.home.feed.FeedModel;
import com.renren.mobile.x2.components.home.feed.FeedViewHolder;
import com.renren.mobile.x2.components.home.profile.ProfileActivity;
import com.renren.mobile.x2.components.home.profile.ProfileView;
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
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DateFormat;
import com.renren.mobile.x2.utils.FileUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.TagResponse;
import com.renren.mobile.x2.utils.img.ImageLoader.UiResponse;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PlayerThread;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnPlayerListenner;
import com.renren.mobile.x2.utils.voice.VoiceManager;
import com.renren.mobile.x2.utils.voice.PlayerThread.PlayRequest;


public class CommentAdapter extends BaseAdapter implements
                           OnItemLongClickListener{

	//header .content布局类型。
	private  static final int TYPE_LAYOUT1=0;
	private  static final int TYPE_LAYOUT2=1;
	
	private Bitmap mBitmap;
	private CommentActivity mActivity;
	private Context mContext;
	private CommentFragment mView;
	//评论的数据
	private int mCount=200;
	private LayoutInflater mInflater;
	public TextView mName,mContent,mComment_name_comemt;
	public Button mComment_button;
	private CommentViewHolder mHolder;
	private ListView mListView;
	private ImageLoader mHeadImageLoader,mContentImageLoader;
	private  boolean isScroll=false;
	//feed 传递过来的数据。
	public List<UGCContentModel> mCommentModel=null;
	public List<UGCContentModel> mTwoComment=null;
 	public List<CommentItem> mItemList=null;
 	public UGCContentModel sendModel;
 	public List<FeedCommentContent> mFeedItem;
	public CommentItem item;
	public int mItemCoun=0;
	private FeedModel mFeedModel;
	private FeedViewHolder viewHolder;
	
	public CommentAdapter(Context context,Bundle feedData){
		this.mContext=context;
		mInflater=(LayoutInflater) this.mContext.getSystemService(this.mContext.LAYOUT_INFLATER_SERVICE);
	    mCommentModel=new ArrayList<UGCContentModel>();
	    mItemList=new ArrayList<CommentItem>();
	    mTwoComment=new ArrayList<UGCContentModel>();
	    mFeedItem=new ArrayList<FeedCommentContent>();
	}
	//设置评论数据，
	public void setCommentData(List<UGCContentModel> model,ListView listView){
		this.mCommentModel=model;
		this.mListView=listView;
		notifyDataSetChanged();
	}
	//添加自己的评论。
	public void setOwenCommentData(CommentItem item){
		this.mItemList.add(item);
		notifyDataSetChanged();
	}
	
	public void setData(UGCContentModel model){
		sendModel=model;
		mCommentModel.add(0, model);
		notifyDataSetChanged();
	}
	
	public void notifyUpData(UGCContentModel model){
		if (model != null && !model.id.equals(null)) {
			notifyDataSetChanged();
		}
		
	}
	
	public void setScroll(boolean isScroll){
		this.isScroll=isScroll;
	}
	
	public int  getMcount(){
		return mCommentModel.size();
	}
	@Override
	public int getCount() {
		return getMcount();
	}

	@Override
	public Object getItem(int position) {
		return mCommentModel.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public int getItemViewType(int position) {
		if(position==0){
			return TYPE_LAYOUT1;
		}else{
			return TYPE_LAYOUT2;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	
	public List<FeedCommentContent> getTwoComments(){
		FeedCommentContent item;
		if(mCommentModel!=null&&mCommentModel.size()>0){
			int position=mCommentModel.size()-1;
			if(mCommentModel.size()>=2){
				for(int i=0;i<2;i++){
					item=new FeedCommentContent();
					item.setTime(mCommentModel.get(mCommentModel.size()-position-i).createTime);
					item.setmUser(mCommentModel.get(mCommentModel.size()-position-i).user);
				    
					UGCTextModel tm=((UGCTextModel)mCommentModel.get(mCommentModel.size()-position-i).contentInfo.get(UGCModel.UGCType.TEXT));
					if(tm!=null){
						String text=tm.mText;
						UGCTextModel textModel=new UGCTextModel(text);
						if(textModel!=null){
							textModel.build();
							if(!text.equals(null)&&text.length()>0){
								mCommentModel.get(mCommentModel.size()-position-i).contentInfo.put(UGCModel.UGCType.TEXT,textModel);
								item.setContentInfo(mCommentModel.get(mCommentModel.size()-position-i).contentInfo);
							}
						}
					}
					
					UGCVoiceModel vm=((UGCVoiceModel)mCommentModel.get(mCommentModel.size()-position-i).contentInfo.get(UGCModel.UGCType.VOICE));
					if(vm!=null){
						String voice=vm.mUrl;
						int length=vm.mLength;
						UGCVoiceModel voiceModel=new UGCVoiceModel(voice, length);
						if(voiceModel!=null){
							voiceModel.build();
							if(!voice.equals(null)&&voice.length()>0){
								mCommentModel.get(mCommentModel.size()-position-i).contentInfo.put(UGCModel.UGCType.VOICE,voiceModel);
								item.setContentInfo(mCommentModel.get(mCommentModel.size()-position-i).contentInfo);
						}
					  }
					}
					mFeedItem.add(item);
				}
			}else{
				item=new FeedCommentContent();
				item.setTime(mCommentModel.get(position).createTime);
				item.setmUser(mCommentModel.get(position).user);
				
				UGCTextModel tm=((UGCTextModel)mCommentModel.get(position).contentInfo.get(UGCModel.UGCType.TEXT));
				if(tm!=null){
					String text=tm.mText;
					UGCTextModel textModel=new UGCTextModel(text);
					if(textModel!=null){
						textModel.build();
						if(!text.equals(null)&&text.length()>0){
							mCommentModel.get(position).contentInfo.put(UGCModel.UGCType.TEXT,textModel);
							item.setContentInfo(mCommentModel.get(position).contentInfo);
						}
					}
				}
				
				UGCVoiceModel vm=((UGCVoiceModel)mCommentModel.get(position).contentInfo.get(UGCModel.UGCType.VOICE));
				if(vm!=null){
					String voice=vm.mUrl;
					int length=vm.mLength;
					UGCVoiceModel voiceModel=new UGCVoiceModel(voice, length);
					if(voiceModel!=null){
						voiceModel.build();
						if(!voice.equals(null)&&voice.length()>0){
							mCommentModel.get(position).contentInfo.put(UGCModel.UGCType.VOICE,voiceModel);
							item.setContentInfo(mCommentModel.get(position).contentInfo);
					}
				  }
				}
				mFeedItem.add(item);
			}
		}
		return mFeedItem;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
			if(convertView==null){
				convertView=mInflater.inflate(R.layout.comment_content,null);
				mHolder=new CommentViewHolder();
				ViewMapUtil.viewMapping(mHolder, convertView);
				convertView.setTag(mHolder);
			}else{
				mHolder=(CommentViewHolder) convertView.getTag();
			}
			UGCContentModel commentModel=mCommentModel.get(mCommentModel.size()-1-position);//-倒叙显示
			setHolderContentData(mHolder, commentModel,position,isScroll,mListView);
	    return convertView;
	}
	
	
	private void setonContentclick(CommentViewHolder holder,final int position){
		holder.mCommenterUserphoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//个人信息展示
				ProfileActivity.show(mContext,mCommentModel.get(mCommentModel.size()-1-position).user.mId);
			}
		});
		
		holder.mCommentImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//图片浏览
				Intent intent=new Intent(mContext,ImageViewActivity.class);
				intent.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.VIEW_LARGE_HEAD);				
				intent.putExtra(ImageViewActivity.NEED_PARAM.DEFAULT_SHOW, true);
				if(mItemList.size()!=0){
					if(((UGCImgModel)mCommentModel.get(position-mItemList.size()).contentInfo.get(UGCModel.UGCType.IMG)).mOriginalUrl!=null){
						intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_PORTRAIT_URL,
								((UGCImgModel)mCommentModel.get(position-mItemList.size()).contentInfo.get(UGCModel.UGCType.IMG)).mOriginalUrl);
			        }
				}else{
					if(((UGCImgModel)mCommentModel.get(position).contentInfo.get(UGCModel.UGCType.IMG)).mOriginalUrl!=null){
						intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_PORTRAIT_URL,
								((UGCImgModel)mCommentModel.get(position).contentInfo.get(UGCModel.UGCType.IMG)).mOriginalUrl);
			        }
				}
				mContext.startActivity(intent);
			}
		});
	}
    //添加自己的回复；
	public void setOwenContentData(final CommentViewHolder viewHolder,final CommentItem item,boolean isScroll,final ListView listView,final int position){
		//从本地取。。不是从网络上得到。修改下面的方法。
		viewHolder.mCommenterUserphoto.setTag(item.getUserHeadUrl());
		HttpImageRequest request=new HttpImageRequest(item.getUserHeadUrl(),isScroll);
		//ImageLoaderManager用于代表一个特定业务 比如一个界面或者一个service 在这个holder被回收的时候 返回的ImageLoader也会被回收
		mHeadImageLoader=ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD,mContext);
		Bitmap bitmap=mHeadImageLoader.getMemoryCache(request);
		if(bitmap!=null){
			viewHolder.mCommenterUserphoto.setImageBitmap(bitmap);
		}else{
              UiResponse headurl=new UiResponse() {
				
				@Override
				public void failed() {
					// TODO Auto-generated method stub
				//	CommonUtil.toast("加载信息图片失败");
				}
				
				@Override
				public void uiSuccess(final Bitmap bitmap) {
					// TODO Auto-generated method stub
					RenrenChatApplication.getUiHandler().post(
							new Runnable() {
								@Override
								public void run() {
								ImageView image=(ImageView)listView.findViewWithTag(item.getUserHeadUrl());	
								if(image!=null){
									viewHolder.mCommenterUserphoto.setImageBitmap(bitmap);	
								}
							}
					   });
				}
			};
			if(LoginManager.getInstance().getLoginInfo().mGender==0){
				viewHolder.mCommenterUserphoto.setImageResource(R.drawable.v1_default_famale);
 			}else{
 				viewHolder.mCommenterUserphoto.setImageResource(R.drawable.v1_default_male);
 			}
			mHeadImageLoader.get(request, headurl);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<font color=\"#388FC3\">").append(item.getUserName()).append("</font>").append(" ");
	  
		viewHolder.mCommenterName.setText(item.getUserName());
	    String date = DateFormat.getNowStr(item.getCreateTime());
	    viewHolder.mCommenterTime.setText(date);
	    if(item.getContent()!=null){
	    	viewHolder.mCommentContentLayout.setVisibility(View.VISIBLE);
	  //  	viewHolder.mCommentTextDelete.setVisibility(View.VISIBLE);
	    	viewHolder.mCommenterContent.setText(new EmotionString(item.getContent()));
	    }else{
	    	viewHolder.mCommentContentLayout.setVisibility(View.GONE);
	    }
	    if(item.getImageLocationUrl()!=null){
	    	viewHolder.mCommentImage.setVisibility(View.VISIBLE);
	    	String imageTag=(String) viewHolder.mCommentImage.getTag();
	    	if(!item.getImageLocationUrl().equals(imageTag)){
	    		viewHolder.mCommentImage.setTag(item.getImageLocationUrl());
	    		viewHolder.mCommentImage.setImageBitmap(BitmapFactory.decodeFile(item.getImageLocationUrl()));
	    	}
	    	
	    }else{
	    	viewHolder.mCommentImage.setVisibility(View.GONE);
	    }
	    if(item.getVoiceContent()!=null){
	    	viewHolder.mCommentVoice.setVisibility(View.VISIBLE);
	   // 	viewHolder.mCommentVoiceDelete.setVisibility(View.VISIBLE);
	    }else{
	    	viewHolder.mCommentVoice.setVisibility(View.GONE);
	    }
	    if(item.getAction()!=null){
//	    	viewHolder.mCommentLikeImage.setVisibility(View.VISIBLE);
	    }else{
//	    	viewHolder.mCommentLikeImage.setVisibility(View.GONE);
	    }
	    
	    
	    viewHolder.mCommenterUserphoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//个人信息展示
				ProfileActivity.show(mContext,mItemList.get(position-mCommentModel.size()).getUserId());
			}
		});
	    viewHolder.mCommentImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//图片浏览
				Intent intent=new Intent(mContext,ImageViewActivity.class);
				intent.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.VIEW_LOCAL_TO_OUT_LARGE_IMAGE);				
				intent.putExtra(ImageViewActivity.NEED_PARAM.DEFAULT_SHOW, true);
				if(mItemList.get(mItemList.size()-1-position).getImageLocationUrl()!=null){
					intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_LOCAL_URI,
							mItemList.get(mItemList.size()-1-position).getImageLocationUrl());
		        }
				mContext.startActivity(intent);
			}
		});
	    //去本地的语音文件进行播放，则直接选取文件就好了，当要是在网络上获得语音文件，则需要在语音线程中区获取等操作。
	    viewHolder.mCommentVoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mItemList.get(position-mCommentModel.size()).getVoiceContent()!=null){
					File file=new File(mItemList.get(position-mCommentModel.size()).getVoiceContent());
					if(Logger.mDebug){
						Logger.logd("音频文件地址="+mItemList.get(position-mCommentModel.size()).getVoiceContent());
					}
					if(file.exists()&&file.length()>0){
						final PlayRequest request=new PlayRequest();
						request.mAbsVoiceFileName=mItemList.get(position-mCommentModel.size()).getVoiceContent();
						if(PlayerThread.getInstance().forceToPlay(request)){
							PlayerThread.getInstance().onAddPlay(request);
						};
					}
				}
				
			}
		});
	}
	
	
	//对回复view设计数据
    private void setHolderContentData(final CommentViewHolder viewHolder,UGCContentModel model,int position,final boolean isScroll,ListView listView){
      
    	if(model!=null){
    		setHeadUrl(viewHolder,model,isScroll,listView);
    		
            viewHolder.mCommenterName.setText(model.user.mName);
    	
    		String date = DateFormat.getDateByChatSession(model.createTime);
    		if (!date.equals("") && !date.equals("昨天") && !date.equals("前天")) {
    			viewHolder.mCommenterTime.setText(date);
    		} else {
    			viewHolder.mCommenterTime.setText(date + " "
    					+ DateFormat.getNowStr(model.createTime));
    		}
    		setContent(viewHolder, model, isScroll,listView);
    		
    //		setDeleteComment(viewHolder,model);
    	}
		setonContentclick(viewHolder,position);
	}
	//设置头部图片
	public void setHeadUrl(final CommentViewHolder viewHolder,final UGCContentModel model,final boolean isScroll,final ListView listView){
		viewHolder.mCommenterUserphoto.setTag(model.user.mSmalHeadlUrl);
		//加载图片的方式。httpimageRequest(url,allowDownload)；url是要加载图片的地址，allowDownload是否下载，如果当前是滑动的话，不加载图片。
		HttpImageRequest request=new HttpImageRequest(model.user.mSmalHeadlUrl,isScroll);
		
		mHeadImageLoader=ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD,mContext);
		Bitmap bitmap=mHeadImageLoader.getMemoryCache(request);
		if(bitmap!=null){
			viewHolder.mCommenterUserphoto.setImageBitmap(bitmap);
		}else{
			TagResponse<String> headurlRes = new TagResponse<String>(
					model.user.mSmalHeadlUrl) {
				@Override
				public void failed() {
				//	CommonUtil.toast("加载信息图片失败 headurl");
				}
				@Override
				protected void success(final Bitmap bitmap, String tag) {
					if (isScroll!=false&&model.user.mSmalHeadlUrl.equals(tag)) {
						RenrenChatApplication.getUiHandler().post(
								new Runnable() {

									@Override
									public void run() {
									ImageView image=(ImageView)listView.findViewWithTag(model.user.mSmalHeadlUrl);
									if(image!=null){
										viewHolder.mCommenterUserphoto.setImageBitmap(bitmap);
										notifyDataSetChanged();
									  }
									}
								});
					}
				}
			};
			if(model.user.mGender==0){
				viewHolder.mCommenterUserphoto.setImageResource(R.drawable.v1_default_famale);
 			}else{
 				viewHolder.mCommenterUserphoto.setImageResource(R.drawable.v1_default_male);
 			}
		
			mHeadImageLoader.get(request, headurlRes);
		}
	}
	//设置评论内容数据。
    public void setContent(final CommentViewHolder viewHolder,UGCContentModel model,boolean isScroll,ListView listView){
            if(model.contentInfo!=null){
			
			final UGCTextModel textModel=(UGCTextModel) model.contentInfo.get(UGCModel.UGCType.TEXT);
		    final UGCImgModel  imageModel=(UGCImgModel) model.contentInfo.get(UGCModel.UGCType.IMG);
		    final UGCVoiceModel voiceModel=(UGCVoiceModel) model.contentInfo.get(UGCModel.UGCType.VOICE);
			final UGCActionModel actionModel=(UGCActionModel) model.contentInfo.get(UGCModel.UGCType.ACTION);
			final UGCPlaceModel placeModel=(UGCPlaceModel) model.contentInfo.get(UGCModel.UGCType.PLACE);
			final UGCTagModel tagModel=(UGCTagModel) model.contentInfo.get(UGCModel.UGCType.TAG);
			
			setTextModel(textModel,viewHolder);
			
			setVoiceModel(voiceModel, viewHolder);
			
	//	    setImageModel(imageModel, viewHolder,isScroll,listView);
		    
		    setActionModel(actionModel, viewHolder);
		    
		    setPlaceModel(placeModel,viewHolder);
		    
		    setTagModel(tagModel,viewHolder);
			
          }
		}
    
    //设置文本内容。
    public void setTextModel(UGCTextModel textModel,CommentViewHolder viewHolder){
    	if(textModel!=null){
			viewHolder.mCommentContentLayout.setVisibility(View.VISIBLE);
			viewHolder.mCommenterContent.setText(new EmotionString(textModel.mText));
		}else{
			viewHolder.mCommentContentLayout.setVisibility(View.GONE);
		}
    }
	//设置图片内容。
    public void setImageModel(final UGCImgModel imageModel,final CommentViewHolder viewHolder,final boolean isScroll,final ListView listView){
    	if (imageModel!= null) {
			viewHolder.mCommentImage.setVisibility(View.VISIBLE);
			viewHolder.mCommentImage.setTag(imageModel.mOriginalUrl);
			HttpImageRequest request=new HttpImageRequest(imageModel.mOriginalUrl,isScroll);
			mContentImageLoader = ImageLoaderManager.get(
					ImageLoaderManager.TYPE_FEED, mContext);
			Bitmap bitmap=mContentImageLoader.getMemoryCache(request);
			if(bitmap!=null){
				viewHolder.mCommentImage.setImageBitmap(bitmap);
			}else{
				TagResponse<String> feedRes = new TagResponse<String>(
						imageModel.mOriginalUrl) {
					@Override
					public void failed() {
					//	CommonUtil.toast("加载信息图片失败imagemodel");
					}
					@Override
					protected void success(final Bitmap bitmap, String tag) {
						if (imageModel.mOriginalUrl.equals(tag)&&isScroll!=false) {
							RenrenChatApplication.getUiHandler().post(
									new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											ImageView image=(ImageView) listView.findViewWithTag(imageModel.mOriginalUrl);
											if(image!=null){
												viewHolder.mCommentImage
												    .setImageBitmap(bitmap);
											}
										}
									});
						}
					}
				};
				viewHolder.mCommentImage.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.test_default));
				mContentImageLoader.get(request, feedRes);
			}
		}else{
			viewHolder.mCommentImage.setVisibility(View.GONE);
		}
    }
    public static final String VOICEPATH = "/mnt/sdcard/x2/voice";
    //设置语音内容。
    public void setVoiceModel(final UGCVoiceModel voiceModel,final CommentViewHolder viewHolder){
    	
		if(voiceModel!=null){
			viewHolder.mCommentVoice.setVisibility(View.VISIBLE);
			viewHolder.mCommentVoice.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//点击语音按钮后开始下载语音，
					Log.v("--hwp--","voice coi "+voiceModel.mUrl);
					
					String voiceUrl=voiceModel.mUrl;
					int voiceTime=voiceModel.mLength;
					
					String filePath=VOICEPATH+"/"+voiceUrl.hashCode();
					
					voicePlay(voiceModel, filePath);
					if(filePath.equals("")){
							try {
								InputStream is= mContext.getAssets().open("1229120320_e9c355aa438e1223.spx");
								ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
								byte[]buffer =new byte[1024];
								while(is.read(buffer)!=-1){
									swapStream.write(buffer);
								}
								FileUtil.getInstance().saveFile(swapStream.toByteArray(), filePath);
								File files = new File(filePath);
								if (files.exists() && files.length() > 0) {   //存在则开始播放
									final PlayRequest request = new PlayRequest();
									request.mAbsVoiceFileName = filePath;
									if(PlayerThread.getInstance().forceToPlay(request)){
										PlayerThread.getInstance().onAddPlay(request);
									};
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				
				}
			});				
		}else{
			viewHolder.mCommentVoice.setVisibility(View.GONE);
		}
    }
    
    public void voicePlay(UGCVoiceModel voiceModel,String filePath){
    	Log.d("--hwp--","xxxx ");
    	try {
    		File file = new File(filePath);
			if (file.exists() && file.length() > 0) {   //存在则开始播放
				final PlayRequest request = new PlayRequest();
				request.mAbsVoiceFileName = filePath;
				
				if(voiceModel.mPlayListener==null){
					voiceModel.mPlayListener=new OnPlayerListenner(){

						@Override
						public void onPlayStart() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onPlayPlaying() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onPlayStop() {
							// TODO Auto-generated method stub
							
						}
						
					};
				}
				request.mPlayListenner=voiceModel.mPlayListener;
				if(PlayerThread.getInstance().forceToPlay(request)){
					PlayerThread.getInstance().onAddPlay(request);
				};
			}
			else{
				downVoice(voiceModel, filePath);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void downVoice(final UGCVoiceModel voiceModel,final String filePath){
    	OnDownloadListenner downVoice=new OnDownloadListenner() {
			
			@Override
			public void onDownloadSuccess(DownloadReuqest request) {
				// TODO Auto-generated method stub
				voicePlay(voiceModel, filePath);
			}
			
			@Override
			public void onDownloadStart() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDownloadPrepare() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDownloadError() {
				// TODO Auto-generated method stub
				
			}
		};
		DownloadReuqest request=new DownloadReuqest();
		request.mVoiceUrl=voiceModel.mUrl;
		request.mListenner=downVoice;
		request.mSaveFilePath=filePath;
		VoiceDownloadThread.getInstance().addDownloadRequest(request);
	
    }
    
    //设置action动作。
    public void setActionModel(UGCActionModel actionModel,CommentViewHolder viewHolder){
    	if(actionModel!=null){
//			viewHolder.mCommentLikeImage.setVisibility(View.VISIBLE);
//			viewHolder.mCommentLikeImage.setBackgroundResource(R.drawable.test_comment_like);
		}else{
//			viewHolder.mCommentLikeImage.setVisibility(View.GONE);
		}
    }
    //删除自己的评论
    public void setDeleteComment(int position){
    	String schoolId=LoginManager.getInstance().getLoginInfo().mSchool_id;
    	String feedId=mFeedModel.getFeedId()+"";
    	String commentId=mCommentModel.get(mCommentModel.size()-position).id;
    	Log.v("--hwp--","shanchu comment d "+commentId);
    	HttpMasService.getInstance().deleteComment(schoolId, feedId, commentId, response);
    	mCommentModel.remove(mCommentModel.size()-position);
    	
    	notifyDataSetChanged();
    }
    
    private INetResponse response = new INetResponse(){

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				//toast("success:"+data.toString());
				CommonUtil.toast("success:"+data.toString());
			}else{
				//toast("error"+data.toString());
				CommonUtil.toast("error:"+data.toString());

			}
		}
		
	};
    
    
    
    public void getFeedId(FeedModel model){
    	this.mFeedModel=model;
    }
    
   //设置地点的动作。
    public void setPlaceModel(UGCPlaceModel placeModel,CommentViewHolder viewHolder){
    	
    }
    //设置tag的动作.
    public void setTagModel(UGCTagModel tagModel,CommentViewHolder viewHolder){
    	
    }

	public void adapterClear() {
		if (mCommentModel != null) {
			mCommentModel.clear();
		}
		if(mItemList!=null){
			mItemList.clear();
		}
		
		PlayerThread.getInstance().stopAllPlay();
		VoiceDownloadThread.getInstance().stopToAutoPlay();
		VoiceManager.getInstance().stopAllPlay();
		VoiceManager.getInstance().stopRecord(false);
	//	notifyDataSetChanged();
	}
	
	//回收资源，将bitmap占用的内存释放掉。
		public void DestoryBitmap(){	
			this.isRecycle(mBitmap);
		}
		
		public void isRecycle(Bitmap bitmap){
			if(null!=bitmap&&!bitmap.isRecycled()){
				bitmap.recycle();
			}
		}
		
		public static interface OnFingerTouchListener{
			public void onFingerTouch();
		}
		OnFingerTouchListener mOnFingerTouchListener;
		public void setOnFingerTouchListener(OnFingerTouchListener listener){
			mOnFingerTouchListener = listener;
		}
		public void onFingerTouch(){
			if(this.mOnFingerTouchListener!=null){
				mOnFingerTouchListener.onFingerTouch();
			}
		}
	/*
	 * 布局holder
	 */
		class CommentViewHolder{
		
			@ViewMapping(ID=R.id.content_root)
			public LinearLayout mCommeneterRoot;
			
			@ViewMapping(ID=R.id.content_people_photo)
			public ImageView mCommenterUserphoto;
			
			@ViewMapping(ID=R.id.name)
			public TextView mCommenterName;
			
			@ViewMapping(ID=R.id.time)
			public TextView mCommenterTime;	
			
			@ViewMapping(ID=R.id.content)
			public TextView mCommenterContent;
			
			@ViewMapping(ID=R.id.content_image)
			public ImageView mCommentImage ;

			@ViewMapping(ID=R.id.content_voice)
			public LinearLayout mCommentVoice;
			
			@ViewMapping(ID=R.id.content_layout)
			public LinearLayout mCommentContentLayout;
			
			public String url ="";
		}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final int pos=position;
		if(pos!=0){
			Builder builderItem=new AlertDialog.Builder(mContext);
			builderItem.setTitle("what you want me");
			if(LoginManager.getInstance().getLoginInfo().mUserId.equals(mCommentModel.get(mCommentModel.size()-pos).user.mId)){
				if(!mCommentModel.get(mCommentModel.size()-pos).id.equals(null)){
					builderItem.setItems(new String[]{"复制","删除"}, new  DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							switch(which){
							case 0:
								CommonUtil.toast("复制成功！");
								break;
							case 1:
								setDeleteComment(pos);
								CommonUtil.toast("删除成功！");
								break;
							}
								
						}
					});
				}else{
					builderItem.setItems(new String[]{"复制"}, new  DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CommonUtil.toast("复制成功！");
						}
					});
				}
			}else{
				builderItem.setItems(new String[]{"复制"}, new  DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
							CommonUtil.toast("复制文本内容成功！");
					}
				});
			}
			builderItem.create().show();
		}
		
		return false;
	}
}

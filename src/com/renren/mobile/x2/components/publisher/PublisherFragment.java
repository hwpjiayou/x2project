package com.renren.mobile.x2.components.publisher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.publisher.voice.IVoiceChanged;
import com.renren.mobile.x2.components.publisher.voice.PublishRecorder;
import com.renren.mobile.x2.components.publisher.voice.PublishVoiceView;
import com.renren.mobile.x2.emotion.EmotionEditText;
import com.renren.mobile.x2.emotion.EmotionManager;
import com.renren.mobile.x2.emotion.IEmotionManager.OnEmotionSelectCallback;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.mas.UGCPlaceModel;
import com.renren.mobile.x2.network.mas.UGCTagModel;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.RRSharedPreferences;
import com.renren.mobile.x2.utils.log.Logger;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class PublisherFragment extends BaseFragment<Object> implements OnEmotionSelectCallback, IVoiceChanged  {
	
	private static final String TAG = "pa";
	/**
	 * 当发送UGC时需要弹出的ProgressDialog
	 */
	private ProgressDialog mDialog;

	// titlebar中的三个按钮
	/**
	 * 取消按钮
	 */
	private ImageButton mCancelButton;
	/**
	 * 活动类型选择按钮
	 */
	private ImageButton mTypeSelectButton;
	/**
	 * 发布按钮
	 */
	private Button mSendButton;
	/**
	 * 文字输入框
	 */
	private EmotionEditText mEditText;
	/**
	 * 拍照按钮&拍照预览图
	 */
	private ImageView mCameraView;

	/**
	 * 定位按钮
	 */
	private ImageButton mLocationButton;
	/**
	 * 音频按钮
	 */
	private ImageButton mVoiceButton;
	/**
	 * 表情按钮
	 */
	private ImageButton mEmotionButton;

	/**
	 * tag选择框
	 */
	private LinearLayout mPop;
	private ViewPager mPopPager;
	private ArrayList<GridView> mPagerContainer = new ArrayList<GridView>();

	private LinearLayout mLowerLayer;
	
	/**
	 * 总容器
	 */
	private LinearLayout root;
	/**
	 * 表情面板的容器
	 */
	public LinearLayout mEmotioncontainer;
	/**
	 * 音频输入模块容器
	 */
	public PublishVoiceView mVoiceContainer;
	/* 用于记录不锁屏前的window性质 */
	public int mOldAttributeParams = 0;
	
	/**
	 * 附加模块的容器的容器包括表情和语音
	 */
	public FrameLayout mPluginContainer;
	/**
	 * 表情按钮和附加容器的集合，用来控制动画。
	 */
	public LinearLayout mBottomContainer;
	// ***************Animation*****************************
	private Animation mAnimRotate90Clock;
	private Animation mAnimRotate90AntiClock;
	private Animation mAnimTransIn;
	private Animation mAnimTransOut;
	/**
	 * 输入法管理器
	 */
	private InputMethodManager imm;
	private OnRelayoutListener mRelayoutListener;
	private Handler mHandler;
	private Activity mActivity;
	private LayoutInflater mInflater;
	/**
	 * 用于处理表情面板开关的标志位
	 */
	private boolean mLock = false;
	/**
	 * 拍摄完成未经过处理的图片的路径
	 */
	protected String strFilePath;
	// ********************数据相关*********************
	
	private byte[] mbyteImage; //存放最终上传的图片byte数据
	
	private UGCTagModel mTag = null;

    private UGCPlaceModel mPlace;
	
	private RRSharedPreferences mRRsp = null;
	// request code
	public static final int REQUESTCODE_TAKEPHOTO = 0;
	public static final int REQUESTCODE_SELECTPHOTO = 1;
	public static final int REQUESTCODE_EDITPHOTO = 2;
	public static final int REQUESTCODE_FILTER = 3;

	private INetResponse response = new INetResponse() {

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject) obj;
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mDialog != null && mDialog.isShowing()) {
						mDialog.dismiss();
					}
				}
			});
			if (Methods.checkNoError(req, data)) {
				CommonUtil.toast("发布成功");
				Log.d("jason", data.toString());
				mActivity.finish();
				//这里的退出动画出问题
//				mActivity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						mActivity.overridePendingTransition(0, R.anim.transout_to_bottom);
//					}
//				});
			} else {
				CommonUtil.toast("发布失败");
				Log.d("jason", "error is:" + data.toString());

			}
		}

	};
	private  PublishRecorder  mRecord_Command = new PublishRecorder(this);

	@Override
	public void onAttach(Activity activity) {
		mActivity = activity;
		super.onAttach(activity);
	}

	@Override
	public View getView() {
		return super.getView();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mInflater = inflater;
		root = (LinearLayout) inflater.inflate(R.layout.publisher_layout, null);
		mHandler = new Handler();
		iniView();
		iniAnimation();
		iniClickListener();
		iniTypePop();
		imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		EmotionManager.setContext(mActivity);
		EmotionManager.getInstance();// /初始化数据
		mRelayoutListener = new OnRelayoutListener() {

			@Override
			public void onKeyBoardShow() {
				// 隐藏表情面板
				onKeyBoradShow();
			}
			@Override
			public int getHight() {
				// 以根View的底部为基准
				return root.getBottom();
			}
		};
		mEditText.setOnRelayoutListener(mRelayoutListener);
		if(mRRsp==null){
			mRRsp = new RRSharedPreferences(mActivity, "publisher");
		}
		//只有当第一次的时候弹出tag选择器
		if(mRRsp.getBooleanValue("isFirst", true)){
			showTypePopWindow();
			mRRsp.putBooleanValue("isFirst", false);
		}
		return root;
	}

	/**
	 * 初始化View
	 */
	private void iniView() {
		mCancelButton = (ImageButton) root.findViewById(R.id.publisher_cancel);
		mTypeSelectButton = (ImageButton) root.findViewById(R.id.publisher_type_selector);
		mSendButton = (Button) root.findViewById(R.id.publisher_finish);
		mEditText = (EmotionEditText) root.findViewById(R.id.publisher_emotion_edittext);
		mCameraView = (ImageView) root.findViewById(R.id.publisher_camera);
		mLocationButton = (ImageButton) root.findViewById(R.id.publisher_lbs);
		mVoiceButton = (ImageButton) root.findViewById(R.id.publisher_voice);
		mEmotionButton = (ImageButton) root.findViewById(R.id.publisher_b5);
		root = (LinearLayout) root.findViewById(R.id.publisher_root);
		mPop = (LinearLayout) root.findViewById(R.id.publisher_pop);
		mLowerLayer = (LinearLayout) root.findViewById(R.id.publisher_lower_layer);
		mPopPager = (ViewPager) root.findViewById(R.id.publisher_type_pager);
		root = (LinearLayout) root.findViewById(R.id.publisher_root);
		mEmotioncontainer = (LinearLayout) root.findViewById(R.id.publisher_emotion_container);
		mBottomContainer = (LinearLayout) root.findViewById(R.id.publisher_bottom_conteiner);
		mVoiceContainer = (PublishVoiceView)root.findViewById(R.id.publisher_voice_container);
	}

	/**
	 * 初始化动画
	 */
	private void iniAnimation() {
		mAnimRotate90Clock = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.roatte90_clockwise);
		mAnimRotate90Clock.setFillAfter(true);
		mAnimRotate90AntiClock = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.roatte90_anticlock);
		mAnimRotate90AntiClock.setFillAfter(true);
		mAnimTransIn = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.trans_in);
		mAnimTransOut = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.trans_out);
	}

	/**
	 * 初始化按钮点击事件
	 */
	private void iniClickListener() {
		mCancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 退出当前Activity,如果有内容加入提示
				exit();
			}
		});
		mTypeSelectButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 类型选择弹框开关
				showTypePopWindow();
			}
		});
		mSendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String text = mEditText.getText().toString().trim();
				if (TextUtils.isEmpty(text)) {
					// TODO 随机文字
					String[] randomtext = getResources().getStringArray(R.array.publisher_random_text);
					Random r = new Random();
					text = randomtext[r.nextInt(randomtext.length)];
				}
				if (mDialog != null) {
					mDialog.show();
				}
				String voiceFile = mVoiceContainer.getRecordFile();
				Log.v("@@@", "voiceFile is:" + voiceFile);
				hideInputMethod();
				if (mbyteImage != null && mbyteImage.length > 0) {
					PublisherRequestSend.sendUGC(response, text, mbyteImage, TextUtils.isEmpty(voiceFile) ? "" : voiceFile, mTag, mPlace);
				} else {
					PublisherRequestSend.sendUGC(response, text, null, TextUtils.isEmpty(voiceFile) ? "" : voiceFile, mTag, mPlace);
				}
				// 发送成功保存tag供下次使用
				if (mTag != null) {
					mRRsp.putIntValue("tag", mTag.mId);
				}
				mActivity.finish();
			}
		});

		mCameraView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 准备上传的图片的存在性的判断
				if(mbyteImage!=null&&mbyteImage.length>0){
					showPhotoDialog(true);
				}else{
					showPhotoDialog(false);
				}
			}
		});
		mLocationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PublisherLocationActivity.show(mActivity);
			}
		});
		mVoiceButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//显示声音输入框
				if(mVoiceContainer.getVisibility()==View.VISIBLE){
					mVoiceContainer.setVisibility(View.GONE);
				}else{
					showVoicePannel();
				}

			}
		});
		mEmotionButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 显示表情输入框 这部分有待整理
				//策略，如有表情，隐藏表情，显示键盘，如果没有表情，显示表情，如果有语音，收回语音加入表情
				if (mEmotioncontainer.getVisibility() == View.VISIBLE) {
					// 隐藏表情
					mEmotioncontainer.setVisibility(View.GONE);
				} else {

					// 隐藏输入法，显示表情
					if (mEmotioncontainer.getChildCount() == 0) {
						View ev = EmotionManager.getInstance().getView(mActivity,
								false,
						PublisherFragment.this);
						// container.setl
						mEmotioncontainer.addView(ev);

					}
					mLock = true;
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							Log.d("test", "show");
							hideInputMethod();
							mVoiceContainer.setVisibility(View.GONE);
							mEmotioncontainer.setVisibility(View.VISIBLE);
							mBottomContainer.startAnimation(AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.transin_from_bottom_high_speed));
						}
					});
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							mLock = false;
						}
					}, 200);

				}
			}
		});
		
		//mVoiceContainer.monitorView(mRoot_InputBar.mView_VoiceRecord);
		mVoiceContainer.setMonitorListener(mRecord_Command);
		mVoiceContainer.setVoiceChangedListenter(this);
//		mVoiceContainer.setOnPreTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View arg0, MotionEvent ev) {
//                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//                	if(Logger.mDebug){
//                		Logger.errord(Logger.PLAY_VOICE,"mVoiceContainer onTouch");
//                	}
//                    mRecord_Command.onStartCommand();
//                }
//                return true;
//            }
//        });

	}

	/**
	 * 初始化活动类型弹框 
	 */
	private void iniTypePop() {

		// 初始化活动数据
		final ArrayList<UGCTagModel> taglist = UGCTagModel.getTagList();
		//读取上次的tag
		if(mRRsp==null){
			mRRsp = new RRSharedPreferences(mActivity, "publisher");
		}
		int lastTagId = mRRsp.getIntValue("tag", -1);
		if(lastTagId!=-1){
			for(UGCTagModel tag:taglist){
				if(tag.mId==lastTagId){
					mTypeSelectButton.setImageResource(tag.getIconResourceId());
					mTag = tag;
				}
			}
		}
		
		for (int i = 0; i < taglist.size() / 8 + 1; i++) {
			final int pageIndex = i;
			GridView grid = (GridView) mInflater.inflate(R.layout.publisher_type_grid_layout, null);
			mPagerContainer.add(grid);
			// 初始化GridView
			grid.setAdapter(new BaseAdapter() {

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					final UGCTagModel tag = taglist.get(position + pageIndex * 8);
					View v = mInflater.inflate(R.layout.publisher_tag_item_layout, null);
					ImageButton icon = (ImageButton)v.findViewById(R.id.publisher_tag_item_button);
					final ImageView bgSelect = (ImageView)v.findViewById(R.id.publisher_tag_select_bgs);
					tag.setImageView(bgSelect);
					icon.setImageResource(tag.getIconResourceId());
					icon.setTag(tag);
					if(mTag!=null){
						if(tag.mId==mTag.mId){
							bgSelect.setVisibility(View.VISIBLE);
						}
						
					}
					icon.setBackgroundColor(Color.TRANSPARENT);
					icon.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							//关于二次选中的判断
							if(mTag==null||mTag.mId!=tag.mId){
								// 设置图标
								mTypeSelectButton.setImageResource(tag.getIconResourceId());
								// 收回下拉菜单
								showTypePopWindow();
								tag.setSelected();
								// 设置当前的tag类型
								if(mTag!=null){
									mTag.setUnSelected();
								}
								mTag = tag;
								mEditText.setHint(tag.mDesc);
							}else{
								mTypeSelectButton.setImageResource(R.drawable.publisher_type_selector);
								if(mTag!=null){
									mTag.setUnSelected();
								}
								mTag = null;
								mEditText.setHint(null);
								showTypePopWindow();
							}
						}
					});
					return v;
				}

				@Override
				public long getItemId(int position) {
					return 0;
				}

				@Override
				public Object getItem(int position) {
					return null;
				}

				@Override
				public int getCount() {
					// 8是每页的最大个数
					if ((pageIndex + 1) * 8 <= taglist.size()) {
						return 8;
					} else {
						return taglist.size() - pageIndex * 8;
					}
				}
			});
		}
		// 初始化ViewPager
		mPopPager.setAdapter(new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mPagerContainer.size();
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				((ViewPager) container).addView(mPagerContainer.get(position));
				return mPagerContainer.get(position);
			}

		});
	}

	public void exit(){
		if (!TextUtils.isEmpty(mEditText.getText().toString().trim())||isTagChanged() || mbyteImage!=null || !TextUtils.isEmpty(mVoiceContainer.getRecordFile()) || mPlace != null) {
			Log.d("fuck","cancel dialog");
			AlertDialog.Builder builder = new Builder(mActivity);
			builder.setMessage("确认退出吗？");

			builder.setTitle("提示");

			builder.setPositiveButton("确认", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					hideInputMethod();
					mActivity.finish();
					mActivity.overridePendingTransition(0, R.anim.transout_to_bottom);
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});

			builder.create().show();
		}else{
			hideInputMethod();
			mActivity.finish();
			mActivity.overridePendingTransition(0, R.anim.transout_to_bottom);
		}
	}
	
	/**
	 * 显示类型选择弹框开关
	 */
	private void showTypePopWindow() {
		if (mPop.getVisibility() == View.VISIBLE) {
			// 动画效果
			mTypeSelectButton.startAnimation(mAnimRotate90AntiClock);
			hidePop();
		} else {
			// 动画效果
			mTypeSelectButton.startAnimation(mAnimRotate90Clock);
			showPop();

		}
	}

	private void showPop() {
		mPop.setVisibility(View.VISIBLE);
		mPop.startAnimation(mAnimTransIn);
		viewAbleHandle(mLowerLayer, false);
	}

	private void hidePop() {
		mPop.startAnimation(mAnimTransOut);
		mPop.setVisibility(View.GONE);
		viewAbleHandle(mLowerLayer, true);

	}

	/**
	 * 自动隐藏表情面板
	 */
	private void onKeyBoradShow() {
		if (!mLock && (mEmotioncontainer.getVisibility() == View.VISIBLE||mVoiceContainer.getVisibility()==View.VISIBLE)) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// 隐藏表情
					Log.d("test", "hide emotion");
					mEmotioncontainer.setVisibility(View.GONE);
					mVoiceContainer.setVisibility(View.GONE);
				}
			});
		}
	}
	private boolean isTagChanged(){
		if(mTag==null){
			return false;
		}else{
			if(mRRsp==null){
				mRRsp = new RRSharedPreferences(mActivity, "publisher");
			}
			int lastTagIndex = mRRsp.getIntValue("tag", -1);
			if(mTag.mId==lastTagIndex){
				//无变化
				return false;
			}
			return true;
		}
	}
	/**
	 * 隐藏输入法
	 */
	private void hideInputMethod() {
		Log.d("fuck","hide!");
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}

	/**
	 * 弹出语音面板
	 */
	private void showVoicePannel(){
		mLock = true;
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Log.d("test", "show");
				hideInputMethod();
				Log.d(TAG,"@showVoicePannel 隐藏表情面板");
				mEmotioncontainer.setVisibility(View.GONE);
				Log.d(TAG,"@showVoicePannel 设置语音面板为可见");
				//弹出语音面板
				mVoiceContainer.setVisibility(View.VISIBLE);
				Log.d(TAG,"@showVoicePannel 语音面板弹出动画");
				mBottomContainer.startAnimation(AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.transin_from_bottom_high_speed));
			}
		});
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mLock = false;
			}
		}, 200);
		
		
	}
	
	@Override
	public void onEmotionSelect(String emotion) {
		mEditText.insertEmotion(emotion);
	}

	@Override
	public void mDelBtnClick() {
		mEditText.delLastCharOrEmotion();
	}

	// 下面代码有问题

	/**
	 * 照片dialog
	 */
	private void showPhotoDialog(boolean isExistPhoto) {
		CharSequence[] items;
		if(isExistPhoto){
			//已经存在图片的情况下 三个选项
			items = new CharSequence[] {getResources().getString(R.string.publisher_dialog_take),getResources().getString(R.string.publisher_dialog_select_from_album),getResources().getString(R.string.publisher_dialog_delete)};
		}else{
			//不存在图片的情况下 两个选项
			items = new CharSequence[] {getResources().getString(R.string.publisher_dialog_take),getResources().getString(R.string.publisher_dialog_select_from_album)};
		}
		
		AlertDialog dlg = new AlertDialog.Builder(mActivity).setTitle(getResources().getString(R.string.publisher_dialog_title)).setItems(items, new
		DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// 这里item是根据选择的方式，
				// 在items数组里面定义了两种方式，拍照的下标为1所以就调用拍照方法
				//对路径的一个预处理
				if(item==0||item==1){
					//拍照
					Log.d("pa","拍照 path");
					// 将拍照得到的图片首先保存在SD卡指定的位置中
					String strPath = Environment.getExternalStorageDirectory().toString()+"/x2/upload";
					//文件夹的检测和创建
					File path = new File(strPath);
					if (!path.exists())
						path.mkdirs();
					Log.d("pa","拍照 nomedia");
					//.nomedia文件的检测和创建
					File noMedia = new File(strPath+"/.nomedia");
					if(!noMedia.exists()){
						try {
							noMedia.createNewFile();
						} catch (IOException ignore) {
						}
					}
					// TODO 有待修改，指定的目录和随机的名字？
					String strFileName = System.currentTimeMillis()+".jpg";
					Log.d("pa","拍照 filename:"+strFileName);
					strFilePath = strPath + "/" + strFileName;
				}
				switch(item){
				case 0:
					File file = new File(strFilePath);
					Uri uri = Uri.fromFile(file);
					Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
					getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					Log.d("pa","拍照 start");
					startActivityForResult(getImageByCamera, REQUESTCODE_TAKEPHOTO);
					break;
				case 1:
					//从相册中选取
					Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
					getImage.addCategory(Intent.CATEGORY_OPENABLE);
					getImage.setType("image/jpeg");
					startActivityForResult(getImage, REQUESTCODE_SELECTPHOTO);
					break;
				case 2:
					//删除当前准备上传的图片
					mbyteImage = null;
					//默认图片
					mCameraView.setImageResource(R.drawable.publisher_camera_selector);
					//
					break;
				}
				
			}
		}).create();
		dlg.show();
	}


	/**
	 * 内容类型
	 * 
	 * @author zhenning.yang
	 * 
	 */
	public enum ContentType {
		TEXT, PLACE, VOICE, TAG, IMAGE
	}

	/**
	 * 内容存在状态的控制器
	 * 
	 * @author zhenning.yang
	 * 
	 */
	public class ContentExistingHandler {

		private boolean isHasText = false;
		private boolean isHasPlace = false;
		private boolean isHasVoice = false;
		private boolean isHasTag = false;
		private boolean isHasImage = false;

		public void changeContentExistingStatus(ContentType type, boolean status) {
			switch (type) {
			case TEXT:
				isHasText = status;
				break;
			case PLACE:
				isHasPlace = status;
				break;
			case VOICE:
				isHasVoice = status;
				break;
			case TAG:
				isHasTag = status;
				break;
			case IMAGE:
				isHasImage = status;
				break;

			}
			// 内容改变状态的回调
			notifyContentExistingStatusChanged();
		}

		private void notifyContentExistingStatusChanged() {
			// TODO 具体的内容逻辑，变化图标等等
			if (isHasText) {
				
			} else {

			}
			if (isHasPlace) {

			} else {

			}
			if (isHasVoice) {

			} else {

			}
			if (isHasTag) {

			} else {

			}
			if (isHasImage) {

			} else {

			}

		}
	}
	
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("pa","@PublisherFragment onActivityResult");
		Logger logger = new Logger("NCS");
		logger.d("requestCode:"+requestCode);
		Log.d("pa","requestCode "+requestCode);
		if(data==null){
			Log.d("pa","data is null");
		}
		switch (requestCode) {
		case REQUESTCODE_SELECTPHOTO:
			try {
				// 获得图片的uri
				Uri originalUri = data.getData();
				Intent sIntent = new Intent();
				sIntent.setData(originalUri);
				sIntent.putExtra("destination", strFilePath);
				sIntent.setClass(mActivity.getApplicationContext(), FilterActivity.class);
				startActivityForResult(sIntent, REQUESTCODE_FILTER);
			} catch (Exception ignore) {
			}
			break;
		case REQUESTCODE_TAKEPHOTO:
			// 通过文件的存在性判断拍照是否成功
			try {
				File file = new File(strFilePath);
				if (file.exists()) {
					Intent sIntent = new Intent();
					sIntent.putExtra("path", strFilePath);
					sIntent.putExtra("destination", strFilePath);
					sIntent.setClass(mActivity.getApplicationContext(), FilterActivity.class);
					startActivityForResult(sIntent, REQUESTCODE_FILTER);
				}
			} catch (Exception ignore) {
			}
			break;
		case REQUESTCODE_FILTER:
			Log.d("pa","@REQUEST_FILTER set mCameraView bitmap");
			boolean success =false;
			if(data!=null){
				success = data.getBooleanExtra("success", false);
			}
			if(success){
				//TODO 数据指向为处理过的图片
				Log.d("pa","decodeFile :"+strFilePath);
				Bitmap bitmap = BitmapFactory.decodeFile(strFilePath);
				mCameraView.setImageBitmap(bitmap) ;
				mbyteImage = Bitmap2Bytes(bitmap);
//				logger.d("byte image length:"+mbyteImage.length);
			}else{
				mCameraView.setImageResource(R.drawable.publisher_camera_selector);
				mbyteImage = null;
				//数据指向为空
			}
			break;
        case PublisherLocationActivity.START_LOCATION_REQUEST_CODE:
            if(resultCode == Activity.RESULT_OK) {
                mPlace = (UGCPlaceModel)data.getSerializableExtra(PublisherLocationFragment.PLACE_MODEL_KEY);
                mLocationButton.setImageResource(R.drawable.publisher_location_able_selector);
            }
            break;
		}
	}
	
	private byte[] Bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            return bytes;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

	// ************************utils************************
	private void viewAbleHandle(View v, boolean isAble) {
		if (v instanceof ViewGroup) {
			v.setEnabled(isAble);
			int childCount = ((ViewGroup) v).getChildCount();
			for (int i = 0; i < childCount; i++) {
				viewAbleHandle(((ViewGroup) v).getChildAt(i), isAble);
			}
		} else {
			v.setEnabled(isAble);
		}
	}

	@Override
	public void onCoolEmotionSelect(String emotion) {
		
	}
	// ************************以下代码暂时没有使用********************************

	@Override
	protected void onPreLoad() {

	}

	@Override
	protected void onFinishLoad(Object data) {

	}

	@Override
	protected Object onLoadInBackground() {
		return null;
	}

	@Override
	protected void onDestroyData() {

	}
	
	//保持屏幕常亮的操作。
		public void keepScreenOn() {
			mOldAttributeParams = mActivity.getWindow().getAttributes().flags;
			mActivity.getWindow().setFlags(mOldAttributeParams, ~mOldAttributeParams);
			mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		//关闭屏幕常亮。
		public void stopKeepScreenOn() {
			mActivity.getWindow().setFlags(0, ~0);
			mActivity.getWindow().setFlags(mOldAttributeParams, mOldAttributeParams);
		}

		/**录音完成*/
		@Override
		public void voiceAdded() {
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					if(Logger.mDebug){
						Logger.logd(Logger.PLAY_VOICE,"修改录音样式：录音完成");
					}
					mVoiceButton.setImageResource(R.drawable.publisher_voice_able_unpress);
				}
			});
			
		}

		/**录音文件被删除*/
		@Override
		public void voiceDel() {
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					if(Logger.mDebug){
						Logger.logd(Logger.PLAY_VOICE,"修改录音样式：录音文件被删除");
					}
					mVoiceButton.setImageResource(R.drawable.publisher_voice_unable_press);
				}
			});
			
		}

	

}

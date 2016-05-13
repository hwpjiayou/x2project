package com.renren.mobile.x2.components.publisher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.emotion.EmotionEditText;
import com.renren.mobile.x2.emotion.EmotionManager;
import com.renren.mobile.x2.emotion.IEmotionManager.OnEmotionSelectCallback;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * 提供了发布图片和文字的功能，并能够对图片进行滤镜和旋转的处理
 * 
 * @author zhenning.yang
 * 
 */
public class PublisherActivity extends Activity implements OnEmotionSelectCallback {
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
	private ImageButton mFinishButton;
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

	private LinearLayout mPop;

	private ViewPager mPopPager;
	private ArrayList<GridView> mPagerContainer = new ArrayList<GridView>();

	private LinearLayout mLowerLayer;
	/**
	 * 表情面板的容器
	 */
	private LinearLayout container, root,bottomContainer;
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
	/**
	 * 用于处理表情面板开关的标志位
	 */
	private boolean mLock = false;
	protected String strFilePath;
	//********************数据相关*********************
	private PublisherTags.Tag mTag = null;
	// request code
	public static final int REQUESTCODE_TAKEPHOTO = 0;
	public static final int REQUESTCODE_SELECTPHOTO = 1;
	public static final int REQUESTCODE_EDITPHOTO = 2;
	public static final int REQUESTCODE_FILTER = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
		mHandler = new Handler();
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("内容发送中，请稍等...");
		setContentView(R.layout.publisher_layout);
		iniView();
		iniAnimation();
		iniClickListener();
		iniTypePop();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		EmotionManager.setContext(this);
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
		showTypePopWindow();
	}

	/**
	 * 初始化View
	 */
	private void iniView() {
		mCancelButton = (ImageButton) findViewById(R.id.publisher_cancel);
		mTypeSelectButton = (ImageButton) findViewById(R.id.publisher_type_selector);
		mFinishButton = (ImageButton) findViewById(R.id.publisher_finish);
		mEditText = (EmotionEditText) findViewById(R.id.publisher_emotion_edittext);
		mCameraView = (ImageView) findViewById(R.id.publisher_camera);
		mLocationButton = (ImageButton) findViewById(R.id.publisher_lbs);
		mVoiceButton = (ImageButton) findViewById(R.id.publisher_voice);
		mEmotionButton = (ImageButton) findViewById(R.id.publisher_b5);
		root = (LinearLayout) findViewById(R.id.publisher_root);
		mPop = (LinearLayout) findViewById(R.id.publisher_pop);
		mLowerLayer = (LinearLayout) findViewById(R.id.publisher_lower_layer);
		mPopPager = (ViewPager) findViewById(R.id.publisher_type_pager);
		root = (LinearLayout) findViewById(R.id.publisher_root);
		container = (LinearLayout) findViewById(R.id.publisher_emotion_container);
		bottomContainer = (LinearLayout)findViewById(R.id.publisher_bottom_conteiner);
	}

	/**
	 * 初始化动画
	 */
	private void iniAnimation() {
		mAnimRotate90Clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.roatte90_clockwise);
		mAnimRotate90Clock.setFillAfter(true);
		mAnimRotate90AntiClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.roatte90_anticlock);
		mAnimRotate90AntiClock.setFillAfter(true);
		mAnimTransIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.trans_in);
		mAnimTransOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.trans_out);
	}

	private INetResponse response = new INetResponse() {

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject) obj;
			runOnUiThread(new Runnable() {
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
				finish();
			} else {
				CommonUtil.toast("发布失败");
				Log.d("jason", "error is:" + data.toString());

			}
		}

	};

	/**
	 * 初始化按钮点击事件
	 */
	private void iniClickListener() {
		mCancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 退出当前Activity
				finish();
				overridePendingTransition(0, R.anim.transout_to_bottom);
			}
		});
		mTypeSelectButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 类型选择弹框开关
				showTypePopWindow();
			}
		});
		mFinishButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    if(mDialog != null) {
                        mDialog.show();
                    }
                    PublisherRequestSend.sendUGC(response, mEditText.getText().toString().trim(), null, "", null, null);
                } else {
                    CommonUtil.toast("输入的内容不能为空");
                }
			}
		});
		mCameraView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 拍照，动画还没有加入
				showPhotoDialog();
			}
		});
		mLocationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 显示地理位置

			}
		});
		mVoiceButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 显示声音输入框

			}
		});
		mEmotionButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 显示表情输入框
				if (container.getVisibility() == View.VISIBLE) {
					// 隐藏表情
					container.setVisibility(View.GONE);  
				} else {

					// 隐藏输入法，显示表情
					mLock = true;
					if (container.getChildCount() == 0) {
						View ev = EmotionManager.getInstance().getView(PublisherActivity.this,false, PublisherActivity.this);
						// container.setl
						container.addView(ev);
						
					}
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							Log.d("test", "show");
							hideInputMethod();
							container.setVisibility(View.VISIBLE);
							bottomContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.transin_from_bottom));
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

	}

	/**
	 * 初始化活动类型弹框 TODO Adapter部分的代码写的比较仓促，后期整理
	 */
	private void iniTypePop() {
		
		// 初始化活动数据
		final ArrayList<PublisherTags.Tag> taglist= PublisherTags.getTagList();
		for(int i = 0;i<taglist.size()/8+1;i++){
			final int pageIndex = i;
			GridView grid = (GridView) getLayoutInflater().inflate(R.layout.emotion_gridview_layout, null);
			mPagerContainer.add(grid);
			grid.setAdapter(new BaseAdapter() {
				
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					final PublisherTags.Tag tag = taglist.get(position+pageIndex*8);
					ImageButton v=new ImageButton(getApplicationContext());
					v.setImageResource(tag.iconResourceId);
					v.setTag(tag);
					v.setBackgroundColor(Color.TRANSPARENT);
					v.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//设置图标
							mTypeSelectButton.setImageResource(tag.iconResourceId);
							//收回下拉菜单
							showTypePopWindow();
							//设置当前的tag类型
							mTag = tag;
							mEditText.setHint(tag.desc);
						}
					});
					return v;
				}
				
				@Override
				public long getItemId(int position) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public int getCount() {
					// 8是每页的最大个数
					if((pageIndex+1)*8<=taglist.size()){
						return 8;
					}else{
						return taglist.size()-pageIndex*8;
					}
				}
			});
		}
		// 初始化GridView
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
		if (!mLock && container.getVisibility() == View.VISIBLE) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// 隐藏表情
					Log.d("test", "hide emotion");
					container.setVisibility(View.GONE);
				}
			});
		}
	}

	/**
	 * 隐藏输入法
	 */
	private void hideInputMethod() {
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}

	/**
	 * 照片dialog
	 */
	private void showPhotoDialog() {
		final CharSequence[] items = { "相册", "拍照" };
		AlertDialog dlg = new AlertDialog.Builder(PublisherActivity.this).setTitle("选择图片").setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// 这里item是根据选择的方式，
				// 在items数组里面定义了两种方式，拍照的下标为1所以就调用拍照方法
				if (item == 1) {
					// 将拍照得到的图片首先保存在SD卡指定的位置中
					String strPath = Environment.getExternalStorageDirectory().toString();
					File path = new File(strPath);
					if (!path.exists())
						path.mkdirs();
					// TODO 有待修改，指定的目录和随机的名字？
					String strFileName = "test.jpg";
					strFilePath = strPath + "/" + strFileName;
					File file = new File(strFilePath);
					Uri uri = Uri.fromFile(file);
					Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
					getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(getImageByCamera, REQUESTCODE_TAKEPHOTO);
				} else if (item == 0) {
					Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
					getImage.addCategory(Intent.CATEGORY_OPENABLE);
					getImage.setType("image/jpeg");
					startActivityForResult(getImage, REQUESTCODE_SELECTPHOTO);
				}
			}
		}).create();
		dlg.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 处理返回
		super.onActivityResult(requestCode, resultCode, data);

		Bitmap myBitmap = null;
		switch (requestCode) {
		case REQUESTCODE_SELECTPHOTO:
			try {
				// 获得图片的uri
				Uri originalUri = data.getData();
				Intent sIntent = new Intent();
				sIntent.setData(originalUri);
				sIntent.setClass(getApplicationContext(), FilterActivity.class);
				startActivityForResult(sIntent, REQUESTCODE_FILTER);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case REQUESTCODE_TAKEPHOTO:
			try {
				// ******************这个方法处理的是缩略图，废弃***********************
				// Bundle extras = data.getExtras();
				// myBitmap = (Bitmap) extras.get("data");
				// byte[] d = PublisherUtils.getByteFromPic(myBitmap, 100);
				// Log.d("filter", " size " + d.length);
				// sIntent.putExtra("image_byte_data", d);
				Intent sIntent = new Intent();
				sIntent.putExtra("path", strFilePath);
				sIntent.setClass(getApplicationContext(), FilterActivity.class);
				startActivityForResult(sIntent, REQUESTCODE_FILTER);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case REQUESTCODE_FILTER:
			// 跳转到编辑
			byte[] s = data.getByteArrayExtra("image_byte_data");
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), ImageEditActivity.class);
			intent.putExtra("image_byte_data", s);
			startActivityForResult(intent, REQUESTCODE_EDITPHOTO);
			Log.d("filter", " to edit");
			break;
		case REQUESTCODE_EDITPHOTO:
			byte[] byteImage = data.getByteArrayExtra("image_byte_data");
			// 将字节数组转换为ImageView可调用的Bitmap对象
			myBitmap = PublisherUtils.getPicFromBytes(byteImage, null);
			// //把得到的图片绑定在控件上显示
			mCameraView.setImageBitmap(myBitmap);
			mCameraView.setVisibility(View.VISIBLE);
			break;
		}

	}

	@Override
	public void onEmotionSelect(String emotion) {
		mEditText.insertEmotion(emotion);
	}

	@Override
	public void mDelBtnClick() {
		mEditText.delLastCharOrEmotion();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO 处理表情回弹和退出弹框
		if (keyCode == KeyEvent.KEYCODE_BACK && container.getVisibility() == View.VISIBLE) {
			container.setVisibility(View.GONE);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// 退出动画
		overridePendingTransition(0, R.anim.transout_to_bottom);
	}

	@Override
	protected void onResume() {
		// TODO 进入动画，没有对OnActivityResult进行处理
		super.onResume();
		overridePendingTransition(R.anim.transin_from_bottom, R.anim.transout_freez);
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
		// TODO Auto-generated method stub
		
	}
}

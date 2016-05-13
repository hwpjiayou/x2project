package com.renren.mobile.x2.components.imageviewer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.photoupload.HeadEditActivity;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.img.ImageLoader.Request;
import com.renren.mobile.x2.utils.img.ImageLoader.Response;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.img.ImageUtil;

public class ImageViewActivity extends Activity implements IModeSwitchable {
	public static final int TOKEN = ImageViewActivity.class.hashCode();
	public static final int mNotificationId = "sixin_android".hashCode();
	public static final int TAKE_PHOTO = 0;
	public static final int SELECT_PHOTO = 1;
	public static final int VIEW_LARGE_HEAD = 3;
	public static final int VIEW_LOCAL_TO_OUT_LARGE_IMAGE = 4;
	private NotificationManager mNotificationManager;
	private Notification mNotificationUploading;
	private Notification mNotificationSucceed;
	private Notification mNotificationFailed;
	private int mRequestCode;
	private Button titleLeftButton;
	private ImageButton saveButton;
	private Button titleRightButton;
	private View mTitleBar;
	private View mOperationBar;
	private View mDefaultImageLoading;
	private int orientation = 0;
	private int mOrientaion = 0;
	private File mTmpFile;
	private Bitmap mBitmap;
	private Bitmap mBitmapSend = null;
	private int mBitmapRotateCount = 0;
	private String mFilePath;
	private String mFileUrl;
	private int mMode;
	private String rePath;
	private static final int MODE_FULL_SCREEN = 0;
	private static final int MODE_SHOW_OPERATION_BAR = 1;
	private FullImageAdapter mAdapter;
    private Boolean isLocalFile;
	private static Context mContext;

	public static interface NEED_PARAM {
		String REQUEST_CODE = "REQUEST_CODE_INT";
		String SMALL_LOCAL_URI = "SMALL_LOCAL_URI";
		String LARGE_LOCAL_URI = "LARGE_LOCAL_URI";
		String SMALL_PORTRAIT_URL = "SMALL_PORTRAIT";
		String LARGE_PORTRAIT_URL = "LARGE_PORTRAIT";

		String DEFAULT_SHOW = "default_show";
	}

	/** TODO true情况下显示上传头像的选项 */
	private boolean defaultShow = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_image);
		defaultShow = getIntent().getBooleanExtra(NEED_PARAM.DEFAULT_SHOW, false);
		mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test_imageviewer_default_image_loading);
		mBitmapSend = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.test_imageviewer_default_image_loading);
		mTitleBar = findViewById(R.id.title_bar);
		mOperationBar = findViewById(R.id.operation_bar);
		mDefaultImageLoading = findViewById(R.id.default_image_loading);
		         findViewById(R.id.full_image_gallery);
		titleLeftButton = (Button) findViewById(R.id.title_left_button);
		titleRightButton = (Button) findViewById(R.id.title_right_button);
		saveButton = (ImageButton) findViewById(R.id.save_bar);
		mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		mContext = getApplicationContext();
		initImageViewCase();
		setMode(MODE_FULL_SCREEN);
		setupFullImageGallery();
		setupTitle();
		setupClickEvents();
		
	}

	private void isLoadingImage(boolean isLoading) {
		if (isLoading) {
			mDefaultImageLoading.setVisibility(View.VISIBLE);
		} else {
			mDefaultImageLoading.setVisibility(View.GONE);
		}
	}

	private void initImageViewCase() {
		mRequestCode = this.getIntent().getIntExtra(NEED_PARAM.REQUEST_CODE, 0);
		switch (mRequestCode) {
		case TAKE_PHOTO:
			mFilePath = this.getIntent().getStringExtra(NEED_PARAM.LARGE_LOCAL_URI);
			takePhoto(mFilePath);
			break;
		case SELECT_PHOTO:
			mFilePath = this.getIntent().getStringExtra(NEED_PARAM.LARGE_LOCAL_URI);
			selectPhoto();
			break;
		case VIEW_LARGE_HEAD:
			mTitleBar.setVisibility(View.GONE);
			mOperationBar.setVisibility(View.GONE);
			saveButton.setVisibility(View.VISIBLE);
			mFileUrl = this.getIntent().getStringExtra(NEED_PARAM.LARGE_PORTRAIT_URL);
			if (TextUtils.isEmpty(mFileUrl)) {
				break;
			}
			CommonUtil.log("lu","111111");
			setPath(mFileUrl);
//			String sdFilePath = Environment.getExternalStorageDirectory() + "/x2/ImageCache/";
//			mFilePath = sdFilePath + mFileUrl.hashCode();
//			if (!FileUtil.getInstance().isExistFile(sdFilePath)) {
//				FileUtil.getInstance().createFile(sdFilePath);
//			}
//			File fileLargeHead = new File(mFilePath);
//			if (fileLargeHead.isFile()) {
//			} else if (fileLargeHead.isDirectory()) {
//			}
//			if (!fileLargeHead.exists()) {
//				try {
//					fileLargeHead.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
			isLocalFile = false;
			isLoadingImage(true);
			downloadBitmap(mFilePath, mFileUrl);
			break;
		case VIEW_LOCAL_TO_OUT_LARGE_IMAGE:
			mTitleBar.setVisibility(View.GONE);
			mOperationBar.setVisibility(View.GONE);
			saveButton.setVisibility(View.VISIBLE);
			String path = this.getIntent().getStringExtra(NEED_PARAM.LARGE_LOCAL_URI);
			if (path.endsWith(".jpg")) {
				mFilePath = path.substring(0, path.length() - 4);
			} else {
				mFilePath = path;
			}
			try {
				ExifInterface exif = new ExifInterface(mFilePath);
				if (exif != null) {
					//orientation = (int) Shared.exifOrientationToDegrees();
					int type=exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					if (type == ExifInterface.ORIENTATION_ROTATE_90) {
						orientation = 90;
					} else if (type == ExifInterface.ORIENTATION_ROTATE_180) {
						orientation = 180;
					} else if (type == ExifInterface.ORIENTATION_ROTATE_270) {
						orientation = 270;
					}
				}
			} catch (Exception ex) {
			}
			setPath(mFilePath);
			isLocalFile = true;
			ImageLoaderManager.get(ImageLoaderManager.TYPE_FEED, ImageViewActivity.this)
			.get(request, response);
			break;
		}
	}
	Response response = new Response(){

		@Override
		public void success(Bitmap bitmap) {
			// TODO Auto-generated method stub
			mBitmap=bitmap;
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				
				@Override
				public void run() {
					mAdapter.notifyDataSetChanged();
					isLoadingImage(false);
					CommonUtil.log("lu","333333");
					
				}
			});
		}

		@Override
		public void failed() {
			CommonUtil.log("lu","44444asd");
			
		}
		
	};
	public void setPath(String path){
		rePath=path;
	}
	Request request = new Request(){
		@Override
		public int type() {
			// TODO Auto-generated method stub
			if(isLocalFile==true){
				return TYPE_FILE;
			}else{
				return TYPE_HTTP;
			}
		}

		@Override
		public int resId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String path() {
			// TODO Auto-generated method stub
			return rePath;
		}

		@Override
		public boolean allowDownload() {
			// TODO Auto-generated method stub
			return true;
		}
		
	};

	private void takePhoto(String filepath) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mTmpFile = new File(filepath);
		Uri mImageCaptureUri = Uri.fromFile(mTmpFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		startActivityForResult(intent, TAKE_PHOTO);
	}

	private void selectPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_PHOTO);
	}

	private void setupTitle() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(74, 34);

		switch (mRequestCode) {
		case TAKE_PHOTO:
		case SELECT_PHOTO:
			setTitleButton();
			break;
		case PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO:
			setTitleButton();
			break;
		default:
			break;
		}
	}

	private void setTitleButton() {
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(74, 34);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(74, 34);
// titleLeftButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.test_imageviewer_title_button_bg));
		params1.leftMargin = 10;
		params1.addRule(RelativeLayout.CENTER_VERTICAL);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//		titleLeftButton.setLayoutParams(params1);
// titleRightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ykn_imageviewer_title_button_bg));
		params2.addRule(RelativeLayout.CENTER_VERTICAL);
		//params2.rightMargin = getResources().getDimensionPixelSize(10);
		params2.rightMargin = getResources().getDimensionPixelSize(R.dimen.imageview_title_button_margin);
		params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		titleRightButton.setLayoutParams(params2);
		titleLeftButton.setText(R.string.chatlist_cancel); 
	    titleRightButton.setText(R.string.imageviewer_send);
	}

	private void setupClickEvents() {
		titleLeftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleRightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (mRequestCode) {
				case TAKE_PHOTO:
					storeImageToFile(mBitmapSend, mFilePath, false);
					ImageViewActivity.this.setResult(RESULT_OK);
					finish();
					break;
				case SELECT_PHOTO:
					storeImageToFile(mBitmapSend, mFilePath, false);
					ImageViewActivity.this.setResult(RESULT_OK);
					finish();
					break;
				default:
					if (defaultShow) {// add by xiangchao.fan
//				               selectDialog(ImageViewActivity.this);
						break;
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewActivity.this);
					builder.setItems(new String[] { mContext.getResources().getString(R.string.imageviewer_save),
							mContext.getResources().getString(R.string.imageviewer_share_renren),
							mContext.getResources().getString(R.string.imageviewer_transfer),
							mContext.getResources().getString(R.string.chatlist_cancel) },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
									case 0:
										saveBitmapToLocal();
										break;
									case 1:
										confirmShareDialog();
										break;
									case 2:
										if (!IsSdCardFeasible()) {
											CommonUtil.toast(mContext.getResources().getString(
													R.string.imageviewer_SDenable)); // ImageViewActivity_java_1=SD卡不可用;
											break;
										}
										mBitmapSend = ImageUtil.image_Compression(mFilePath,
												RenrenChatApplication.getdensity());
										storeImageToFile(mBitmapSend, mFilePath, false);
										/**
										 * MultiChatForwardActivity.show(
										 * ImageViewActivity.this, null,
										 * mFilePath);
										 */
										// D_SelectGroupChatContactActivity.show(ImageViewActivity.this,
										// mFilePath, true);

										break;
									case 3:
										break;
									}
								}
							});
					builder.show();
					break;
				}
			}
		});
		findViewById(R.id.turn_left).setOnClickListener(new OnClickListener() {
			Bitmap tempBitmap = null;

			@Override
			public void onClick(View v) {
				mBitmapRotateCount++;
				/*
				 * mOrientaion -= 90; mFullImageGallery.rotateToLeft();
				 */
				switch (mRequestCode) {
				case TAKE_PHOTO:
				case SELECT_PHOTO:
					mOrientaion -= 90;
					tempBitmap = mBitmapSend;
					mBitmapSend = Util.rotate(mBitmapSend, -90);
					if (null != tempBitmap) {
//						tempBitmap.recycle();
//						tempBitmap = null;
					}
					mFullImageGallery.rotateToLeft();
					break;
				default:
					mOrientaion -= 90;
					tempBitmap = mBitmapSend;
					mBitmapSend = Util.rotate(mBitmap, mOrientaion);
					/*
					 * if (null != tempBitmap) { tempBitmap.recycle();
					 * tempBitmap = null; }
					 */
					mFullImageGallery.rotateToLeft();
					break;
				}
			}
		});
		findViewById(R.id.turn_right).setOnClickListener(new OnClickListener() {
			Bitmap tempBitmap = null;

			@Override
			public void onClick(View v) {
				mBitmapRotateCount++;
				/*
				 * mOrientaion += 90; mFullImageGallery.rotateToRight();
				 */
				switch (mRequestCode) {
				case TAKE_PHOTO:
				case SELECT_PHOTO:
					mOrientaion += 90;
					tempBitmap = mBitmapSend;
					mBitmapSend = Util.rotate(mBitmapSend, 90);
					if (null != tempBitmap) {
						tempBitmap.recycle();
						tempBitmap = null;
					}
					mFullImageGallery.rotateToRight();
					break;
				default:
					mOrientaion += 90;
					tempBitmap = mBitmapSend;
					mBitmapSend = Util.rotate(mBitmap, mOrientaion);
					/*
					 * if (null != tempBitmap) { tempBitmap.recycle();
					 * tempBitmap = null; }
					 */
					mFullImageGallery.rotateToRight();
					break;
					
				}
				
			}
		});
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveBitmapToLocal();
				
			}
		});
		/**
		 * findViewById(R.id.zoom_out).setOnClickListener(new OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(View v) { mFullImageGallery.zoomOut();
		 *           } }); findViewById(R.id.zoom_in).setOnClickListener(new
		 *           OnClickListener() {
		 * @Override public void onClick(View v) { mFullImageGallery.zoomIn(); }
		 *           });
		 */
	}
/**************************************************************************/
	
//	/** 选择修改头像 */
//    public void selectDialog(final Activity activity) {
//		final String items[] = { mContext.getResources().getString(R.string.profile_changehead_camera), mContext.getResources().getString(R.string.profile_changehead_local) };
//		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO 
//				Intent intent = null;
//				switch (which) {
//				case 0://拍照上传
//					String fileName = "head_"
//							+ String.valueOf(System.currentTimeMillis());
//					intent = PhotoUploadManager.getInstance()
//							.getTakePhotoIntent("/sixin/", fileName,
//									".jpg");
//					activity.startActivityForResult(intent,
//							PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO);
//					break;
//				case 1://本地上传
//					activity.startActivityForResult(
//									PhotoUploadManager.getInstance()
//											.getChooseFromGalleryIntent(),
//									PhotoUploadManager.REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY);
//					break;
//				default:
//					break;
//				}
//			}
//		};
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				PhotoUploadManager.getInstance().createUploadDialog(activity,
//						mContext.getResources().getString(R.string.profile_changehead), items, dialogClickListener);
//			}
//		});
//	

	private void confirmShareDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewActivity.this);
		builder.setTitle(mContext.getResources().getString(R.string.imageviewer_share_renren)); // ImageViewActivity_java_2=分享到人人网;
		builder.setMessage(mContext.getResources().getString(R.string.imageviewer_uploadimg_renren)); // ImageViewActivity_java_3=上传图片到人人网手机相册;
		builder.setPositiveButton(mContext.getResources().getString(R.string.imageviewer_ensure), new DialogInterface.OnClickListener() { // MultiChatForwardScreen_java_6=确认;
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						Bitmap bitmap;
						if (mBitmapRotateCount > 0) {
							bitmap = mBitmapSend;
						} else {
							bitmap = mBitmap;
						}
						if (bitmap != null) {
							// TODO add by xiangchao.fan
							// BindInfo bindInfoRenren =
							// LoginManager.getInstance().getLoginInfo().mBindInfoRenren;
							// if(bindInfoRenren == null ||(bindInfoRenren !=
							// null &&
							// TextUtils.isEmpty(bindInfoRenren.mBindId))){
							// Intent intent = new
							// Intent(ImageViewActivity.this,BindRenrenAccountActivity.class);
							// intent.putExtra(BindRenrenAccountActivity.COME_FROM,
							// BindRenrenAccountActivity.BIND_PHOTO);
							// ImageViewActivity.this.startActivity(intent);
							// CommonUtil.toast("请先绑定人人帐号");
							// }else{
							// notifyUploading();
							// bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
							// os);
							// McsServiceProvider.getProvider().postPhoto(os.toByteArray(),
							// 0, 0, 0, "", "", "", "", response);
							// }
						} else {
							Toast.makeText(ImageViewActivity.this, mContext.getResources().getString(R.string.imageviewer_sharefail), Toast.LENGTH_SHORT); // ImageViewActivity_java_4=分享失败;
						}
					}
				});
		builder.setNegativeButton(mContext.getResources().getString(R.string.chatlist_cancel), new DialogInterface.OnClickListener() { // ChatMessageWarpper_FlashEmotion_java_4=取消;
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.show();
	}

	private void setupFullImageGallery() {
		if (null == mBitmap) {
			mBitmap = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.test_imageviewer_default_image_loading);
			mBitmapSend = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.test_imageviewer_default_image_loading);
		}
		mFullImageGallery = (ImageNavigatorView) findViewById(R.id.full_image_gallery);
		mAdapter = new FullImageAdapter(this);
		mFullImageGallery.setAdapter(mAdapter);
		mFullImageGallery.setSelection(0);
		mFullImageGallery.setHostActivity(this);
		if (!TextUtils.isEmpty(mFilePath) && (mFilePath.contains("brush") || mFilePath.contains(".png"))) {
			this.mFullImageGallery.changeBackgroundColorTo(Color.WHITE);
		}
	}

	private ImageNavigatorView mFullImageGallery;

	private class FullImageAdapter extends BaseAdapter {
		private Context mContext;

		public FullImageAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			if (mBitmap != null) {
				return 1;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mBitmap != null) {
				return mBitmap;
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new MultiTouchView(mContext);
			}
			MultiTouchView i = (MultiTouchView) convertView;
			i.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			i.setBitmap(mBitmap, orientation);
			return i;
		}
	}

	protected void onDestroy() {
		if (mFullImageGallery != null) {
//			mFullImageGallery.recycle();
		}
		if (mBitmap != null && !mBitmap.isRecycled()) {
//			mBitmap.recycle();
//			mBitmap = null;
		}
		if (mBitmapSend != null && !mBitmapSend.isRecycled()) {
//			mBitmapSend.recycle();
//			mBitmapSend = null;
		}
		// RenrenChatApplication.popStack(ImageViewActivity.this);
		super.onDestroy();
	}

	public void switchMode() {
		if(mRequestCode==VIEW_LARGE_HEAD||mRequestCode==VIEW_LOCAL_TO_OUT_LARGE_IMAGE){
			finish();
//			ImageViewActivity.this.overridePendingTransition(0,R.anim.scale_alpha_out);
		}else{
			setMode(MODE_FULL_SCREEN + MODE_SHOW_OPERATION_BAR - mMode);
		}
		
	}

	private void setMode(int mode) {
		if (mode == MODE_FULL_SCREEN) {
	       mTitleBar.setVisibility(View.INVISIBLE);
			mOperationBar.setVisibility(View.INVISIBLE);
		} else if (mode == MODE_SHOW_OPERATION_BAR) {
			mTitleBar.setVisibility(View.VISIBLE);
			mOperationBar.setVisibility(View.VISIBLE);
		} else {
			throw new IllegalArgumentException("the mode is invalid " + mode);
		}
		mMode = mode;
	}

	// @Override
	// public void onOptionsMenuClosed(Menu menu) {
	// switchMode();
	// super.onOptionsMenuClosed(menu);
	// }
	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// switchMode();
	// return super.onPrepareOptionsMenu(menu);
	// }
	/**
	 * Save image to SD card.
	 */
	/*
	 * private void storeImageToFile(Bitmap bitmap, String path, boolean
	 * isThumb) { String imgPath = null; if (path.endsWith(".jpg")) { imgPath =
	 * path.substring(0, path.length() - 4); } else { imgPath = path; } if
	 * (bitmap == null || bitmap.isRecycled()) { return; } int count = 15; File
	 * file = null; FileOutputStream accessFile = null; do { file = new
	 * File(imgPath); count--; } while (!file.exists() && count > 0);
	 * ByteArrayOutputStream steam = new ByteArrayOutputStream();
	 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, steam); byte[] buffer =
	 * steam.toByteArray(); if (!isThumb) { Options op = new Options();
	 * op.inSampleSize = getSampleSize(bitmap.getWidth(), bitmap.getHeight(),
	 * 100); Bitmap thumb = BitmapFactory.decodeByteArray(steam.toByteArray(),
	 * 0, steam.toByteArray().length, op); // thumb); storeImageToFile(thumb,
	 * imgPath + "_small", true); } try { accessFile = new
	 * FileOutputStream(file); accessFile.write(buffer); accessFile.flush(); }
	 * catch (Exception e) { } try { accessFile.close(); steam.close(); } catch
	 * (IOException e) { } int i = buffer.length / 1024; }
	 */
	/**
	 * sdcard是否可读写
	 * 
	 * @return boolean
	 * @author xiangchao.fan
	 */
	public boolean IsSdCardFeasible() {
		try {
			return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Store image to SD card.
	 */
	public static void storeImageToFile(Bitmap bitmap, String path, boolean isThumb) {
		String imgPath = null;
		if (path.endsWith(".jpg")) {
			imgPath = path.substring(0, path.length() - 4);
		} else {
			imgPath = path;
		}
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}
		File file = new File(imgPath);
		FileOutputStream accessFile = null;
		try {
			accessFile = new FileOutputStream(file);
			if (path.contains("brush") || path.contains("_brush_png")) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, accessFile);
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, accessFile);
			}
			accessFile.flush();
			accessFile.close();
		} catch (Exception e) {
			CommonUtil.toast(mContext.getResources().getString(R.string.imageviewer_filesavefail)); // ImageViewActivity_java_5=图片文件存储失败;
			return;
		}
		if (!isThumb) {
			Options op = new Options();
			op.inSampleSize = getSampleSize(bitmap.getWidth(), bitmap.getHeight(), 100);
			Bitmap thumb = BitmapFactory.decodeFile(file.toString(), op);
			storeImageToFile(thumb, imgPath + "_small", true);
//			thumb.recycle();
		}
	}

	private void storePNGImageToFile(Bitmap bitmap, String path, boolean isThumb) {
		String imgPath = null;
		if (path.endsWith(".png")) {
			imgPath = path.substring(0, path.length() - 4);
		} else {
			imgPath = path;
		}
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}
		File file = new File(imgPath);
		FileOutputStream accessFile = null;
		try {
			accessFile = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, accessFile);
			accessFile.flush();
			accessFile.close();
		} catch (Exception e) {
			CommonUtil.toast(mContext.getResources().getString(R.string.imageviewer_filesavefail)); // ImageViewActivity_java_5=图片文件存储失败;
			return;
		}
		if (!isThumb) {
			Options op = new Options();
			op.inSampleSize = getSampleSize(bitmap.getWidth(), bitmap.getHeight(), 100);
			Bitmap thumb = BitmapFactory.decodeFile(file.toString(), op);
			storePNGImageToFile(thumb, imgPath + "_small", true);
			thumb.recycle();
		}
	}
	public void finishActivity(){
		ImageViewActivity.this.finish();
	}

	/**
	 * 通过指定的默认值，获取缩放比例
	 * */
	public static int getSampleSize(int width, int heigth, int defaultSize) {
		int realityDefaultSize = Methods.dip2px(mContext, defaultSize);
		int maxEdageSize;
		if (width <= realityDefaultSize && heigth <= realityDefaultSize) {
			return 1;
		} else {
			if (width > heigth) {
				maxEdageSize = width;
			} else {
				maxEdageSize = heigth;
			}
			return maxEdageSize / realityDefaultSize;
		}
	}

	private void notifyUploading() {
		mNotificationUploading = new Notification(R.drawable.test_imageviewer_notification_upload, mContext.getResources().getString(R.string.imageviewer_uploading_img),
				System.currentTimeMillis()); // ImageViewActivity_java_6=正在上传照片...;
		Intent intent = new Intent();
		mNotificationUploading.setLatestEventInfo(ImageViewActivity.this, "私信", mContext.getResources().getString(R.string.imageviewer_img_uploading),
				PendingIntent.getActivity(ImageViewActivity.this, 0, intent, 0)); // app_name=私信;
																					// //ImageViewActivity_java_7=照片上传中...;
		mNotificationManager.notify(mNotificationId, mNotificationUploading);
	}

	//
	// private INetProgressResponse response = new INetProgressResponse() {
	//
	// @Override
	// public void response(INetRequest req, JsonValue obj) {
	// if (obj instanceof JsonObject) {
	// JsonObject map = (JsonObject) obj;
	//
	// int error_code = (int) map.getNum("error_code");
	// String error_msg = map.getString("error_msg");
	//
	// if (error_code == 0) {
	// mNotificationManager.cancel(mNotificationId);
	// mNotificationSucceed = new Notification(R.drawable.notification_succeed,
	// RenrenChatApplication.getmContext().getResources().getString(R.string.ImageViewActivity_java_8),
	// System.currentTimeMillis()); //ImageViewActivity_java_8=上传成功;
	// Intent intent = new Intent();
	// mNotificationSucceed.setLatestEventInfo(ImageViewActivity.this,
	// RenrenChatApplication.getmContext().getResources().getString(R.string.app_name),
	// RenrenChatApplication.getmContext().getResources().getString(R.string.ImageViewActivity_java_9),
	// PendingIntent.getActivity(ImageViewActivity.this, 0, intent, 0));
	// //app_name=私信; //ImageViewActivity_java_9=图片已上传到人人网;
	// mNotificationSucceed.flags = Notification.FLAG_AUTO_CANCEL;
	// mNotificationManager.notify(mNotificationId - 1, mNotificationSucceed);
	// } else {
	// mNotificationManager.cancel(mNotificationId);
	// mNotificationFailed = new Notification(R.drawable.notification_fail,
	// RenrenChatApplication.getmContext().getResources().getString(R.string.ImageViewActivity_java_10),
	// System.currentTimeMillis()); //ImageViewActivity_java_10=上传失败;
	// Intent intent = new Intent();
	// mNotificationFailed.setLatestEventInfo(ImageViewActivity.this,
	// RenrenChatApplication.getmContext().getResources().getString(R.string.app_name),
	// RenrenChatApplication.getmContext().getResources().getString(R.string.ImageViewActivity_java_11),
	// PendingIntent.getActivity(ImageViewActivity.this, 0, intent, 0));
	// //app_name=私信; //ImageViewActivity_java_11=图片上传失败;
	// mNotificationFailed.flags = Notification.FLAG_AUTO_CANCEL;
	// mNotificationManager.notify(mNotificationId - 2, mNotificationFailed);
	// }
	// }
	// }
	// };

	/**
	 * when the users browsing pictures, they could press the save button to
	 * save picture to local
	 */
	public void saveBitmapToLocal() {
		String fileName = null;
		if (DeviceUtil.getInstance().isMountSDCard()) {
			String sdFilePath = Environment.getExternalStorageDirectory() + "/x2_img/";
			fileName = sdFilePath + getFileName() + ".jpg";
			File filePath = new File(sdFilePath);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			File file = new File(fileName);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (DeviceUtil.getInstance().isSDCardHasEnoughSpace()) {
				saveBitmapToLocal(mBitmap, fileName);
			} else {
				CommonUtil.toast(mContext.getResources().getString(R.string.imageviewer_java_1)); // ImageViewActivity_java_12=存储卡已满，无法保存图片。;
			}
		} else {
			Toast.makeText(this, mContext.getResources().getString(R.string.imageviewer_java_2), 1).show(); // ImageViewActivity_java_13=存储卡被移除，无法保存图片。;
		}
	}

	/**
	 * achieve hash code value to name the picture
	 */
	public String getFileName() {
		if (mFileUrl != null) {
			return String.valueOf(mFileUrl.hashCode());
		} else
			return String.valueOf(mFilePath.hashCode());
	}

	/**
	 * saveBitmap to SD card
	 */
	private void saveBitmapToLocal(Bitmap bitmap, String path) {
		if (bitmap == null) {
			return;
		}
		File file = new File(path);
		FileOutputStream accessFile = null;
		try {
			accessFile = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, accessFile);
			accessFile.flush();
		} catch (Exception e) {
			CommonUtil.toast(mContext.getResources().getString(R.string.imageviewer_java_3)); // ImageViewActivity_java_14=创建文件失败;
			return;
		}
		try {
			accessFile.close();
		} catch (IOException e) {
		}
		try {
			ContentValues values = new ContentValues();
			values.put(Media.DATA, path);
			values.put(Media.DESCRIPTION, "Image from renren_android");
			values.put(Media.MIME_TYPE, "image/jpeg");
			this.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(this, mContext.getResources().getString(R.string.imageviewer_java_4) + path, 1).show(); // ImageViewActivity_java_15=图片已保存至;
	}

	/**
	 * if the image we browsing is not downloaded, we will download it right now
	 * 
	 * @param filepath
	 * @param fileurl
	 */
	private void downloadBitmap(String filepath, String fileurl) {
//		if (filepath != null) {
//			File file = new File(filepath);
//			if (file.isDirectory()) {
//				return;
//			}
//			if (!file.exists() || file.length() == 0) {
//				if (TextUtils.isEmpty(fileurl)) {
//					CommonUtil.toast(mContext.getResources().getString(R.string.imageviewer_java_5)); // ImageViewActivity_java_16=图片预览错误;
//					return;
//				}
//				downloadImage(fileurl, filepath);
//				Log.v("lu","load");
//			} else {
//				setPath(filepath);
////				new ImageLoader().get(request,response );
//				
//				
//				/*
//				 * mBitmap = ImageUtil.image_Compression(filepath,
//				 * RenrenChatApplication.screenResolution);
//				 */
//				isLoadingImage(false);
//				if (null == mBitmap) {
//					CommonUtil.toast(mContext.getResources().getString(R.string.imageviewer_java_6)); // ImageViewActivity_java_17=图片无法显示;
//					return;
//				}
//			}
//		}

			CommonUtil.log("lu","222222");
			ImageLoaderManager.get(ImageLoaderManager.TYPE_FEED, ImageViewActivity.this).get(request, response);
	}

	/**
	 * sending download request
	 * 
	 * @param url
	 * @param savePath
	 */
	private static Handler sHandler = new Handler();
	protected void downloadImage(String url, final String savePath) {
		class ImageResponse implements INetResponse {
			@Override
			public void response(INetRequest request, JSONObject result) {
				if(result instanceof JSONObject) {
					final JSONObject obj = (JSONObject) result;
					final INetRequest req=(INetRequest) request;
					sHandler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							isLoadingImage(false);
							if(Methods.checkNoError(req, obj)){
								try {
									byte[] imgData = (byte[]) obj.get(IMG_DATA);
									if(imgData != null){
										mBitmap = ImageUtil.decodeByteArray(imgData);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (DeviceUtil.getInstance().isMountSDCard()) {
									if (DeviceUtil.getInstance().isSDCardHasEnoughSpace() && null !=
										 mBitmap) {
										saveBitmap(mBitmap, savePath ,savePath.endsWith(".png"));
									}else{
									}
								}else{	
								}
							}
							if(null == mBitmap) {
							}{
								setupFullImageGallery();
							}
						}
					});
				}
				
			}
			
		}
		 HttpMasService.getInstance().getImage(url, new ImageResponse());
	}


	/**
	 * when the picture we intend to browsing is downloaded auto save it to
	 * local sd card
	 */
	private int saveBitmap(Bitmap bitmap, String path, boolean isPng) {
		if (bitmap == null) {
			return -1;
		}
		File file = new File(path);
		FileOutputStream accessFile = null;
		try {
			accessFile = new FileOutputStream(file);
			if (isPng)
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, accessFile);
			else
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, accessFile);
			accessFile.flush();
		} catch (Exception e) {
			CommonUtil.toast(mContext.getResources().getString(R.string.imageviewer_java_7)); // ImageViewActivity_java_18=保存失败;
			return -1;
		}
		try {
			accessFile.close();
		} catch (IOException e) {
		}
		return 0;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mRequestCode = requestCode;
		if ((requestCode == PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO || requestCode == PhotoUploadManager.REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY)
				&& resultCode == RESULT_CANCELED) {
			return;
		}
		if (resultCode == RESULT_CANCELED) {
			finish();
			return;
		}
		switch (requestCode) {
		case TAKE_PHOTO:
			String filePath = mTmpFile.toString();
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			AsyncTask<Object, String, Bitmap> task = new AsyncTask<Object, String, Bitmap>() {
				@Override
				protected void onPreExecute() {
					isLoadingImage(true);
					super.onPreExecute();
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (null != result) {
						setupFullImageGallery();
					}
					isLoadingImage(false);
					super.onPostExecute(result);
				}

				@Override
				protected Bitmap doInBackground(Object... params) {
					mBitmap = ImageUtil.image_Compression(String.valueOf(params[0]), (DisplayMetrics) params[1]);
					mBitmapSend = ImageUtil.image_Compression(String.valueOf(params[0]), (DisplayMetrics) params[1]);
					storeImageToFile(mBitmapSend, mFilePath, false);
					return mBitmap;
				}
			};
			task.execute(filePath, dm);
			switchMode();
			break;
		case SELECT_PHOTO:
			String tmpPath = null;
			if (data != null) {
				Cursor cursor = null;
				try {
					Uri originalUri = data.getData();

					String[] proj = { MediaStore.Images.Media.DATA };
					cursor = managedQuery(originalUri, proj, null, null, null);
					if (cursor != null) {
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						tmpPath = cursor.getString(column_index);
					} else {
						tmpPath = originalUri.getPath();
					}
				} catch (Exception e) {
					finish();
					Toast.makeText(ImageViewActivity.this, mContext.getResources().getString(R.string.imageviewer_java_8) + e, 1).show(); // ImageViewActivity_java_19=图片选择失败！;
				} finally {
					if (cursor != null)
						cursor.close();
				}
				if (tmpPath != null && tmpPath.trim().length() > 0) {
					File temp = new File(tmpPath);
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
					if (temp.exists() && temp.length() > 0) {
						mBitmap = ImageUtil.image_Compression(tmpPath, RenrenChatApplication.getdensity());
						mBitmapSend = ImageUtil.image_Compression(tmpPath, RenrenChatApplication.getdensity());
						storeImageToFile(mBitmapSend, mFilePath, false);
						if (mBitmap == null) {
							finish();
						}
					} else {
						finish();
					}
				} else {
					finish();
				}
			}
			if (null != mBitmap) {
				setupFullImageGallery();
			}
			switchMode();
			break;

		// add by xiangchao.fan
		/* 拍照上传头像 */
		case PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO:
			// Intent intent = new Intent(this, PhotoUploadActivity.class);
			// intent.setData(PhotoUploadManager.getInstance().mUri);
			// startActivityForResult(intent,
			// PhotoUploadManager.REQUEST_CODE_CUT);
			// startActivity(intent);
			Intent intent = new Intent(ImageViewActivity.this, HeadEditActivity.class);
            intent.putExtra(Intent.EXTRA_STREAM, PhotoUploadManager.getInstance().mUri);
            ImageViewActivity.this.startActivity(intent);
            finish();
			break;
		/* 本地上传头像 */
		case PhotoUploadManager.REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY:
			// intent = new Intent(this, PhotoUploadActivity.class);

//			Uri selectedImage = data.getData();
			// String[] filePathColumn = { MediaStore.Images.Media.DATA };
			// Cursor cursor = getContentResolver().query(selectedImage,
			// filePathColumn, null, null, null);
			// cursor.moveToFirst();
			// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			// String picturePath = cursor.getString(columnIndex);
			// cursor.close();
			//
			//
			// intent.setData(Uri.parse(picturePath));
			// intent.setData(selectedImage);
			// startActivityForResult(intent,
			// PhotoUploadManager.REQUEST_CODE_CUT);
			// startActivity(intent);
			Intent intent1 = new Intent(ImageViewActivity.this, HeadEditActivity.class);
			intent1.putExtra(Intent.EXTRA_STREAM, data.getData());
            ImageViewActivity.this.startActivity(intent1);
            finish();
			break;
		/* 剪切图片返回 */
		/*
		 * case PhotoUploadManager.REQUEST_CODE_CUT: filePath = "/sixin/" +
		 * LoginManager.getInstance().getLoginInfo().mUserId + "/"; String file
		 * = "head_" + System.currentTimeMillis(); ContentValues values =
		 * PhotoUploadManager.getInstance() .getContentValues(data, filePath,
		 * file, ".jpg", "Image from head"); this.getContentResolver().insert(
		 * MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); Uri uri =
		 * PhotoUploadManager.getInstance().getUri(filePath, file, ".jpg");
		 * break;
		 */
		}
	}

	public static void show(Context context,String url,boolean isLocal){
		Intent intent = new Intent(context, ImageViewActivity.class);
		if(isLocal == true){
			intent.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.VIEW_LOCAL_TO_OUT_LARGE_IMAGE);
			intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_LOCAL_URI,url);
		}else{
			intent.putExtra(ImageViewActivity.NEED_PARAM.REQUEST_CODE, ImageViewActivity.VIEW_LARGE_HEAD);
			intent.putExtra(ImageViewActivity.NEED_PARAM.LARGE_PORTRAIT_URL,url);
		}
		
		intent.putExtra(ImageViewActivity.NEED_PARAM.DEFAULT_SHOW, true);
		
		context.startActivity(intent);
		((Activity)context).overridePendingTransition(R.anim.scale_alpha_in,0);
	}

}

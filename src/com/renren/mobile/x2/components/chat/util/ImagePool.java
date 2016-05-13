package com.renren.mobile.x2.components.chat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.message.Subject;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.FileUtil;
import com.renren.mobile.x2.utils.FileUtil.SAVE_STATE;
import com.renren.mobile.x2.utils.Methods;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * @author dingwei.chen
 * @说明 图片池
 * */
public final class ImagePool {

	/* 下载相关 */
	public Map<String, List<Subject>> mRequestsOfHeadImage = new HashMap<String, List<Subject>>();
	public Map<String, List<OnDownloadImageListenner>> mRequestsOfImage = new HashMap<String, List<OnDownloadImageListenner>>();
	public static final int NORMAL_RECYCLE_NUMBER = 2;
	public static final int NORMAL_MAX_IMAGE_NUMBER = 5;
	public static final int IMAGE_MAX_WIDTH_PX = 480;
	public static final int IMAGE_MAX_HEIGHT_DIP = 312;
	public BitmapSetLRU mLRUInstance = new BitmapSetLRU(NORMAL_RECYCLE_NUMBER,
			NORMAL_MAX_IMAGE_NUMBER);

	// public BitmapSetLRU mHeadInstance = new
	// BitmapSetLRU(HEAD_RECYCLE_NUMBER,HEAD_MAX_IMAGE_NUMBER);

	private ImagePool() {
	}

	private static ImagePool sInstance = new ImagePool();

	public static ImagePool getInstance() {
		return sInstance;
	}

	public static class KeyBitmap {
		public String url = null;
		public Bitmap bitmap = null;

		public KeyBitmap(String url, Bitmap bitmap) {
			this.url = url;
			this.bitmap = bitmap;
		}

		@Override
		public boolean equals(Object o) {
			// TODO Auto-generated method stub
			if (o instanceof KeyBitmap) {
				KeyBitmap bit = (KeyBitmap) o;
				return bit.url.equals(this.url);
			}
			if (o instanceof String) {
				return o.equals(this.url);
			}
			return super.equals(o);
		}

		public void recycle() {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
			bitmap = null;
		}
	}

	public int size() {
		return mLRUInstance.size();
	}

	/**
	 * 用于主动回收图片（新鲜事图片占用内存过大，易内存溢出）
	 * 
	 * @author liuchou
	 */
	public void recycleSingleImage(String url) {
		mLRUInstance.recycle(url);
	}

	public static class BitmapSetLRU {
		LinkedList<KeyBitmap> mSet = new LinkedList<ImagePool.KeyBitmap>();
		public int mRecycleNumber = 0;
		public int mMaxImageNumber = 0;

		public BitmapSetLRU(int recycleNumber, int maxImageNumber) {
			mRecycleNumber = recycleNumber;
			mMaxImageNumber = maxImageNumber;
		}

		public int size() {
			return mSet.size();
		}

		public Bitmap containUrl(String keyUrl) {
			KeyBitmap index = null;
			for (KeyBitmap keymap : mSet) {
				if (keymap.equals(keyUrl)) {
					index = keymap;
				}
			}
			if (index != null) {
				mSet.remove(index);
				mSet.add(index);// 将该对象加入对味
				return index.bitmap;
			}
			return null;
		}

		public void addBitmap(String url, Bitmap bitmap) {
			KeyBitmap key = new KeyBitmap(url, bitmap);
			if (mSet.size() + 1 > this.mMaxImageNumber) {
				this.recycleLRU();// 回收运算
			}
			this.mSet.add(key);
		}

		private void recycleLRU() {
			int k = 0;
			while (k < this.mRecycleNumber && k < mSet.size()) {
				KeyBitmap map = mSet.remove(0);
				map.recycle();
				k++;
			}
		}

		public void recycleALL() {
			for (KeyBitmap keymap : mSet) {
				keymap.recycle();
			}
			mSet.clear();
		}

		public void remove(String url) {
			KeyBitmap index = null;
			for (KeyBitmap keymap : mSet) {
				if (keymap.equals(url)) {
					index = keymap;
					break;
				}
			}
			if (index != null) {
				index.recycle();
			}
			mSet.remove(index);
		}

		public void recycle(String url) {
			KeyBitmap index = null;
			for (KeyBitmap keymap : mSet) {
				if (keymap.equals(url)) {
					index = keymap;
					break;
				}
			}
			if (index != null) {
				index.recycle();
			}
		}

	}

	public Bitmap getBitmapFromLocal(String bitmapPath) {
		return getBitmapFromLocalNotCompress(bitmapPath);
	}

	public Bitmap getBitmapFromLocalNotCompress(String bitmapPath) {
		Bitmap bitmap = mLRUInstance.containUrl(bitmapPath);
		;
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;
		} else {
			if (bitmap != null && bitmap.isRecycled()) {
				mLRUInstance.remove(bitmapPath);
				bitmap = null;
			}
			File file = new File(bitmapPath);
			if (!file.exists()) {
				return null;
			}
			FileInputStream fis = null;
			try {
				byte[] data = new byte[(int) file.length()];
				fis = new FileInputStream(file);
				fis.read(data);
				try {
					bitmap = BitmapFactory.decodeByteArray(data, 0,
							(int) file.length());
				} catch (OutOfMemoryError e) {
					bitmap = null;
				}
			} catch (Exception e) {
				bitmap = null;
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
			mLRUInstance.addBitmap(bitmapPath, bitmap);
		}
		return bitmap;
	}

	public Bitmap getBitmapFromLocal(String bitmapPath, boolean isRead) {
		Bitmap bitmap = mLRUInstance.containUrl(bitmapPath);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;
		} else {
			if (bitmap != null && bitmap.isRecycled()) {
				mLRUInstance.remove(bitmapPath);
				bitmap = null;
			}
			if (isRead) {
				File file = new File(bitmapPath);
				if (!file.exists()) {
					return null;
				}
				FileInputStream fis = null;
				try {
					byte[] data = new byte[(int) file.length()];
					fis = new FileInputStream(file);
					fis.read(data);
					try {
						bitmap = imageCompression(BitmapFactory
								.decodeByteArray(data, 0, (int) file.length()));
					} catch (OutOfMemoryError e) {
						bitmap = null;
					}
				} catch (Exception e) {
					bitmap = null;
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
						}
					}
				}
				mLRUInstance.addBitmap(bitmapPath, bitmap);
			}
			return bitmap;
		}
	}

	// 压缩图片
	public Bitmap imageCompression(Bitmap bitmap) {
		int originWidth = bitmap.getWidth();
		int originHeight = bitmap.getHeight();
		final Bitmap old = bitmap;
		// 裁剪图片
//		try{
		int tmp = Methods.dip2px(RenrenChatApplication.getApplication(), ImagePool.IMAGE_MAX_HEIGHT_DIP);
		if (originWidth > IMAGE_MAX_WIDTH_PX) {
			if (tmp != 0
					&& originHeight > tmp) {
				// 压缩图片
				if (originWidth * 1.0 / IMAGE_MAX_WIDTH_PX > originHeight * 1.0
						/ tmp) {
					// 如果宽度超出比例大于高度,则安高度压缩
					double i = originHeight * 1.0
							/ tmp;
					int width = (int) Math.floor(originWidth / i);
					bitmap = Bitmap.createScaledBitmap(bitmap, width,
							tmp, false);
					old.recycle();
					Bitmap temp = bitmap;
					bitmap = Bitmap
							.createBitmap(
									bitmap,
									(width - IMAGE_MAX_WIDTH_PX) >> 1,
									0,
									IMAGE_MAX_WIDTH_PX,
									tmp);
					temp.recycle();
				} else {
					double i = originWidth * 1.0 / IMAGE_MAX_WIDTH_PX;
					int height = (int) Math.floor(originHeight / i);
					bitmap = Bitmap.createScaledBitmap(bitmap,
							IMAGE_MAX_WIDTH_PX, height, false);
					old.recycle();
					Bitmap temp = bitmap;
					bitmap = Bitmap
							.createBitmap(
									bitmap,
									0,
									(height - tmp) >> 1,
									IMAGE_MAX_WIDTH_PX,
									tmp);
					temp.recycle();
				}

//				bitmap = Bitmap
//						.createBitmap(
//								bitmap,
//								(originWidth - IMAGE_MAX_WIDTH_PX) >> 1,
//								(originHeight - tmp) >> 1,
//								IMAGE_MAX_WIDTH_PX,
//								tmp);
//				old.recycle();
			} else {
				bitmap = Bitmap.createBitmap(bitmap,
						(originWidth - IMAGE_MAX_WIDTH_PX) >> 1, 0,
						IMAGE_MAX_WIDTH_PX, originHeight);
				old.recycle();
			}
		} else if (tmp != 0
				&& originHeight > tmp) {
			bitmap = Bitmap
					.createBitmap(
							bitmap,
							0,
							(originHeight - tmp) >> 1,
							originWidth,
							tmp);
			old.recycle();
		}
//		 }catch(OutOfMemoryError e){
//		 bitmap = null;
//		 }finally{
//		
//		 }

		return bitmap;
	}

	public void putBitmap(String bitmapPath, Bitmap bitmap) {
		mLRUInstance.addBitmap(bitmapPath, bitmap);
	}

	public void recycle() {
		mLRUInstance.recycleALL();
	}

	public void downloadImage(DownloadImageRequest request) {
		if (!DeviceUtil.getInstance().isSDcardEnable()) {
			request.onError();
		}
		List<OnDownloadImageListenner> list = mRequestsOfImage
				.get(request.mImageUrl);
		if (list == null) {
			// 若队列不存在
			list = new LinkedList<OnDownloadImageListenner>();
			list.add(request.mListenner);
			mRequestsOfImage.put(request.mImageUrl, list);
			request.onStart();
		} else {
			if (request.mListenner != null) {
				// 若队列存在
				synchronized (list) {
					if (!list.contains(request.mListenner)) {
						// 拦截重复请求
						list.add(request.mListenner);
						request.onStart();
					} else {
						return;
					}
				}
			}
		}// end of else
		//DownloadImageResponse response = new DownloadImageResponse(
		//		request.mImageUrl, request.mToSaveFileName);
		//response.mIsCompress = request.mIsCompress;
		//McsServiceProvider.getProvider().getImage(request.mImageUrl, response);
	}

	public void downloadHead(DownloadImageRequest request) {
		if (!DeviceUtil.getInstance().isSDcardEnable()) {
			request.onError();
		}
		List<OnDownloadImageListenner> list = mRequestsOfImage
				.get(request.mImageUrl);
		if (list == null) {
			// 若队列不存在
			list = new LinkedList<OnDownloadImageListenner>();
			list.add(request.mListenner);
			mRequestsOfImage.put(request.mImageUrl, list);
			request.onStart();
		} else {
			if (request.mListenner != null) {
				// 若队列存在
				synchronized (list) {
					if (!list.contains(request.mListenner)) {
						// 拦截重复请求
						list.add(request.mListenner);
						request.onStart();
					} else {
						return;
					}
				}
			}
		}// end of else
//		McsServiceProvider.getProvider().getImage(
//				request.mImageUrl,
//				new DownloadImageResponse(request.mImageUrl,
//						request.mToSaveFileName));
	}

	// public static boolean flagDebug = true;
//	public class DownloadImageResponse extends INetReponseAdapter {
//		public String mImageUrl = null;
//		public String mToSavePath = null;
//		public boolean mIsCompress = true;
//
//		public DownloadImageResponse(String imageUrl, String saveAbsPath) {
//			this.mImageUrl = imageUrl;
//			this.mToSavePath = saveAbsPath;
//		}
//
//		@Override
//		public void onSuccess(INetRequest req, JSONObject data) {
//			try {
//			      // data.
//				//byte[] imgData = data.getBytes(IMG_DATA);
//				if (!DeviceUtil.getInstance().isSDcardEnable()) {
//					throw new Exception();
//				}
//				if (FileUtil.getInstance().saveFile(imgData, mToSavePath) == SAVE_STATE.SAVE_OK) {
//				} else {
//					throw new Exception();
//				}
//				;
//				if (imgData != null) {
//					Bitmap img = null;
//					if (mIsCompress) {
//						SystemUtil.log("onmea", " compress ");
//						img = imageCompression(ImageUtil
//								.createImageByBytes(imgData));
//					} else {
//
//						img = ImageUtil.createImageByBytes(imgData);
//						SystemUtil
//								.log("onmea", "no compress " + img.getWidth());
//					}
//					if (img == null) {
//						throw new Exception();
//					}
//					List<OnDownloadImageListenner> list = mRequestsOfImage
//							.get(mImageUrl);
//					synchronized (list) {
//						for (OnDownloadImageListenner lis : list) {
//							lis.onDownloadSuccess(mImageUrl, img);
//						}
//					}
//					mLRUInstance.addBitmap(mToSavePath, img);
//				}
//			} catch (Exception e) {
//				List<OnDownloadImageListenner> list = mRequestsOfImage
//						.get(mImageUrl);
//				synchronized (list) {
//					for (OnDownloadImageListenner lis : list) {
//						lis.onDownloadError();
//					}
//				}
//			}
//
//			mRequestsOfImage.remove(mImageUrl);
//		}
//
//		@Override
//		public void onError(INetRequest req, JsonObject data) {
//			List<OnDownloadImageListenner> list = mRequestsOfImage
//					.get(mImageUrl);
//			if (list != null) {
//				synchronized (list) {
//					for (OnDownloadImageListenner lis : list) {
//						lis.onDownloadError();
//					}
//				}
//				mRequestsOfImage.remove(mImageUrl);
//			}
//		}
//
//	}

	// 下载图片请求
	public static class DownloadImageRequest {
		public String mImageUrl;
		public String mToSaveFileName;
		public OnDownloadImageListenner mListenner = null;
		public boolean mIsCompress = true;

		public DownloadImageRequest(String imageUrl,
				OnDownloadImageListenner listenner, String absFileName) {
			this.mImageUrl = imageUrl;
			this.mListenner = listenner;
			mToSaveFileName = absFileName;
		}

		public void onStart() {
			if (mListenner != null) {
				mListenner.onDownloadStart();
			}
		}

		public void onSuccess(String imageUrl, Bitmap bitmap) {
			if (mListenner != null) {
				mListenner.onDownloadSuccess(imageUrl, bitmap);
			}
		}

		public void onError() {
			if (mListenner != null) {
				mListenner.onDownloadError();
			}
		}
	}

	public static interface OnDownloadImageListenner {
		public void onDownloadStart();

		public void onDownloadSuccess(String imageUrl, Bitmap bitmap);

		public void onDownloadError();
	}
}

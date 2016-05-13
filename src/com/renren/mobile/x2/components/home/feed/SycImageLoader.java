package com.renren.mobile.x2.components.home.feed;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.Response;
import com.renren.mobile.x2.utils.log.Logger;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

/****
 * 异步下载，可以用Lock控制下载情况 lock（）则停止下载 unlock（）则开始下载
 * 
 * @author jia.xia @ 2012-4-12
 */
public class SycImageLoader {

	public Handler handler = new Handler();

	public Handler mHandler = new Handler();

	public Object mlock = new Object();

	private boolean mAllowload = true;

	/** 当前屏幕的第一张需要下载的图片 */
	private int mStartLoadLimit;
	/** 当前屏幕的最后一张需要下载的图片 */
	private int mStopLoadLimit;
	/** 是否是第一次下载 **/
	private boolean isFirstload = true;

	public boolean ismAllowload() {
		return mAllowload;
	}

	public void restore() {
		mAllowload = true;
		isFirstload = true;
	}

	/***
	 * @author xiaochao.zheng 2012-4-12 lock当前所有下载线程
	 */
	public void lock() {
		mAllowload = false;
		isFirstload = false;
	}

	/***
	 * @author xiaochao.zheng 2012-4-12 唤醒线程
	 */
	public void unlock() {
		mAllowload = true;
		synchronized (mlock) {
			mlock.notifyAll();// /唤醒所有线程
		}
	}

	/***
	 * 计算当前显示页面，优先下载当前页面
	 * 
	 * @author xiaochao.zheng 2012-4-12
	 * @param startLoadLimit
	 *            当前所显示的ViewGroup的第一个item
	 * @param stopLoadLimit
	 *            当前所显示的ViewGroup的最后一个item
	 * 
	 */
	public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
		if (startLoadLimit > stopLoadLimit) {
			return;
		}
		mStartLoadLimit = startLoadLimit;
		mStopLoadLimit = stopLoadLimit;
	}

	/***
	 * 下载图片，当lock的时候停止下载，当unlock的时候开始下载
	 * 
	 * @author xiaochao.zheng 2012-4-13
	 * @param url
	 * @param mView
	 */
	public void loadImage(final String url, final ImageView mView,
			int position, final ImageLoader loader) {
		final int currposition = position;
		/*new Thread(new Runnable() {

			@Override
			public void run() {
				if (!mAllowload) {
					synchronized (mlock) {
						try {
							mlock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					Logger.logd("ll", "thread id:"+Thread.currentThread().getId());
					Logger.logd("ll", "downloadImage:unlock:"+mAllowload+"||"+isFirstload);
					Logger.logd("ll", "downloadImage:currposition:"+currposition);
					Logger.logd("ll", "downloadImage:mStopLoadLimit:"+mStopLoadLimit);
					Logger.logd("ll", "downloadImage:mStartLoadLimit:"+mStartLoadLimit);
					if (mAllowload && currposition <= mStopLoadLimit+1 && currposition >= mStartLoadLimit-1) {
						// 下载
						Logger.logd("ll", "downloadImage:thread");
						downloadImage(url, mView, loader);
					}
				}
			}
		}).start();
*/		
		Logger.logd("ll", "loadImage:"+mAllowload);
		//if (currposition <= mStopLoadLimit+1 && currposition >= mStartLoadLimit-1) {
			// 下载
			Logger.logd("ll", "downloadImage: no  thread");
			downloadImage(url, mView, loader);
		//}
	}

	protected void downloadImage(final String url, final ImageView mView,
			ImageLoader loader) {
		Logger.logd("ll", "downloadImage:"+mAllowload+"||"+isFirstload);
		String mtag = (String) mView.getTag();
		//if (!url.equals(mtag)) {
			final long start = System.currentTimeMillis();
			/*TagResponse<String> response = new TagResponse<String>(url) {

				@Override
				public void failed() {
					Logger.logd("ll", "downloadImage  is failed");
				}

				@Override
				protected void success(final Bitmap bitmap, String tag) {
					if (tag.equals(url)) {
						RenrenChatApplication.getUiHandler().post(
								new Runnable() {

									@Override
									public void run() {
										
										mView.setTag(url);
										mView.setImageBitmap(bitmap);
										Logger.logd("l", "loadimage time:"+(System.currentTimeMillis()-start));
									}
								});

					}
				}
			};*/
			Response response = new Response() {
				
				@Override
				public void success(final Bitmap bitmap) {
					
					RenrenChatApplication.getUiHandler().post(
							new Runnable() {

								@Override
								public void run() {
//									AlphaAnimation ani = new AlphaAnimation(0, 1);
//									ani.setDuration(1000);
//									mView.startAnimation(ani);
									
									mView.setTag(url);
									mView.setImageBitmap(bitmap);
									Logger.logd("l", "loadimage time:"+(System.currentTimeMillis()-start));
								}
							});
				}
				
				@Override
				public void failed() {
					// TODO Auto-generated method stub
					
				}
			};
			loader.get(new HttpImageRequest(url, mAllowload), response);
	//	}

	}
}
package com.renren.mobile.x2.base;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.network.talk.binder.LocalBinder;
import com.renren.mobile.x2.utils.BackgroundUtils;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.view.ITitleBar;
import com.renren.mobile.x2.view.TitleBar;

/**
 * @author lu.yu
 */
public abstract class BaseFragmentActivity<DATA> extends FragmentActivity {
	private static final String TAG = "BaseFragmentActivity";

	private TitleBar mTitleBar;
	private BaseFragment mContentFragment;

	private AsyncTask mDataAsyncTask;


	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.logd("man", "homeActivity onCreate");
		setContentView(R.layout.base);
		mTitleBar = (TitleBar) findViewById(R.id.title_bar);

		mContentFragment = onCreateContentFragment();

		getSupportFragmentManager().beginTransaction().add(R.id.container, mContentFragment).commitAllowingStateLoss();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mDataAsyncTask == null) {
			mDataAsyncTask = new DataAsyncTask();
			mDataAsyncTask.execute((Void[]) null);
		}
	}

	/**
	 * 加载内容Fragment
	 *
	 * @return 内容Fragment
	 */
	protected abstract BaseFragment onCreateContentFragment();

	/**
	 * 得到内容Fragment
	 *
	 * @return 内容Fragment
	 */
	public final BaseFragment getContentFragment() {
		return mContentFragment;
	}

	/**
	 * 得到操作标题栏的接口
	 *
	 * @return 接口
	 */
	public final ITitleBar getTitleBar() {
		return mTitleBar;
	}

	/**
	 * 加载数据之前的回调,可做预处理,位于UI线程之中
	 */
	protected abstract void onPreLoad();

	/**
	 * 加载数据之后的回调,将数据加载到UI,位于UI线程之中
	 */
	protected abstract void onFinishLoad(DATA data);

	/**
	 * 加载数据的操作,该方法已经在非UI线程中,不会阻塞UI.可以进行网络,数据库,文件读取等操作.
	 *
	 * @return 返回的数据
	 */
	protected abstract DATA onLoadInBackground();

	protected abstract void onDestroyData();

	/**
	 * 重新加载数据.
	 */
	protected final void reload() {
		if (mDataAsyncTask == null || mDataAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
			mDataAsyncTask = new DataAsyncTask();
			mDataAsyncTask.execute((Void[]) null);
		}

	}


	@Override
	protected void onStart() {
		super.onStart();
		if (LocalBinder.getInstance().isContainBinder()) {
			try {
				LocalBinder.getInstance().obtainBinder().changeAppGround(true);
			} catch (RemoteException ignored) {
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (LocalBinder.getInstance().isContainBinder()) {
			try {
				LocalBinder.getInstance().obtainBinder().changeAppGround(BackgroundUtils.getInstance().isAppOnForeground());
			} catch (RemoteException ignored) {
			}
		}
	}


	private final class DataAsyncTask extends AsyncTask<Void, Void, DATA> {

		@Override
		protected void onPreExecute() {
			onPreLoad();
		}

		@Override
		protected DATA doInBackground(Void... params) {
			return onLoadInBackground();
		}

		@Override
		protected void onPostExecute(DATA data) {
			onFinishLoad(data);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyData();
	}
}

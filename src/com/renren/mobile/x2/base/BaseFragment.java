package com.renren.mobile.x2.base;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.renren.mobile.x2.view.ITitleBar;

/**
 * @author lu.yu
 */
public abstract class BaseFragment<DATA> extends Fragment {

  private static final String TAG = "BaseFragment";

  private DataAsyncTask mDataAsyncTask;

  @Override
  public void onResume() {
    Log.d(TAG, "@onResume");
    super.onResume();
    if (mDataAsyncTask == null) {
      mDataAsyncTask = new DataAsyncTask();
      mDataAsyncTask.execute((Void[]) null);
    }
  }

  private class DataAsyncTask extends AsyncTask<Void, Void, DATA> {
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

  /**
   * 释放数据
   */
  protected abstract void onDestroyData();

  protected void reload() {
    if (mDataAsyncTask == null ||
        mDataAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
      mDataAsyncTask = new DataAsyncTask();
      mDataAsyncTask.execute((Void[]) null);
    }
  }

  @Override
  public void onDestroy() {
    onDestroyData();
    super.onDestroy();
  }

  protected ITitleBar getTitleBar() {
    return ((BaseFragmentActivity) getActivity()).getTitleBar();
  }
}

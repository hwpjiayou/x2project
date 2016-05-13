package com.renren.mobile.x2.components.home;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.utils.Config;


/**
 * @author lu.yu
 */
public class HomeActivity extends BaseFragmentActivity<Integer> {
  private static final String TAG = "HomeActivity";
  private HomeFragment mHomeFragment;

  @Override
  protected BaseFragment onCreateContentFragment() {
    if (Config.DEVELOPMENT && Build.VERSION.SDK_INT >= 10) {
      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
          .detectAll()
          .penaltyLog()
          .build());
    }
    mHomeFragment = new HomeFragment();
    return mHomeFragment;
  }

  @Override
  protected void onPreLoad() {
  }

  @Override
  protected void onFinishLoad(Integer integer) {
  }

  @Override
  protected Integer onLoadInBackground() {
    return null;
  }

  @Override
  protected void onDestroyData() {
  }

  @Override
  protected void onResume() {
    super.onResume();
    getTitleBar().hide();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mHomeFragment.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onBackPressed() {
    if (!mHomeFragment.onBackPressed()) {
      finish();
    }
  }
}

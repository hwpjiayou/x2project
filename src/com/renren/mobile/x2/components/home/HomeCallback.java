package com.renren.mobile.x2.components.home;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * @author lu.yu
 * @see HomeCallbackImpl 它对应的实现类
 */
interface HomeCallback {

  View onCreateSideTab(Context context);

  void onLoadSideData();

  void onFinishSideData(View view);

  void onPublisherClick(View view);

  void onAnimateLeftStart();

  void onAnimateLeftEnd();

  void onAnimateRightStart();

  void onAnimateRightEnd();

  void onAnimateOpenStart();

  void onAnimateOpenEnd();

  void onDestroyData();

  void onActivityResult(int requestCode, int resultCode, Intent data);
}

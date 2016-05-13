package com.renren.mobile.x2.components.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.home.nearbyfriends.NearByFriendsManager;
import com.renren.mobile.x2.components.publisher.PublisherActivity;
import com.renren.mobile.x2.components.publisher.PublisherFragmentActivity;

/**
 */
class HomeCallbackImpl implements HomeCallback {


    @Override
    public View onCreateSideTab(final Context context) {
    	View view = NearByFriendsManager.getInstance(context).getView();
    	NearByFriendsManager.getInstance(null).LocationAlert();
    	NearByFriendsManager.getInstance(null).showDialog();
        return view;
    }

    @Override
    public void onLoadSideData() {
    	NearByFriendsManager.getInstance(null).onLoadingData();
    }

    @Override
    public void onFinishSideData(final View view) {
    	NearByFriendsManager.getInstance(null).onFinishLoadData();
    }

    @Override
    public void onPublisherClick(final View view) {
        Context context = view.getContext();
        Intent i = new Intent(context, PublisherFragmentActivity.class);
//        Intent i = new Intent(context, PublisherFragmentActivity.class);
        context.startActivity(i);
    }

    @Override
    public void onAnimateLeftStart() {
    	long start = System.currentTimeMillis();
    	NearByFriendsManager.getInstance(null).LocationAlert();
    	Log.d("zxc","Animation start time " +(System.currentTimeMillis()-start));
    	NearByFriendsManager.getInstance(null).showDialog();
    }

    @Override
    public void onAnimateLeftEnd() {
    	long start = System.currentTimeMillis();
    	NearByFriendsManager.getInstance(null).checkandRefresh();
    	Log.d("zxc","Animation end time " +(System.currentTimeMillis()-start));

    }

    @Override
    public void onAnimateRightStart() {
    }

    @Override
    public void onAnimateRightEnd() {
    }

    @Override
    public void onAnimateOpenStart() {
    }

    @Override
    public void onAnimateOpenEnd() {
    }

    @Override
    public void onDestroyData() {
    	NearByFriendsManager.destoryInstance();
    }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  Log.d("zxc","onActivityResult " + requestCode + "  " + resultCode+ "  "+data);
	  if(NearByFriendsManager.getInstance(null).checkLocationEnable()){
	  NearByFriendsManager.getInstance(null).hidalertLocation();}
	  
  }
}

package com.renren.mobile.x2.components.publisher;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;

/**
 * PublisherLocationActivity
 * @author  xiaoguang.zhang
 * Date: 12-11-9
 * Time: 下午2:41
 * To change this template use File | Settings | File Templates.
 */
public class PublisherLocationActivity extends BaseFragmentActivity<Object>{

//    public static String LATLON_BUNDLE_KEY = "latlon_bundle";

    public final static int START_LOCATION_REQUEST_CODE = 7;

    private PublisherLocationFragment mFragment;

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, PublisherLocationActivity.class);
//        Bundle data = new Bundle();
//        data.putDouble(PublisherLocationFragment.LAT_ITUDE_KEY, latIturde);
//        data.putDouble(PublisherLocationFragment.LONG_ITUDE_KEY, longIturde);
//        intent.putExtra(LATLON_BUNDLE_KEY, data);
        activity.startActivityForResult(intent, START_LOCATION_REQUEST_CODE);
        activity.overridePendingTransition(R.anim.transin_from_bottom, R.anim.transout_freez);;
    }

    @Override
    protected BaseFragment onCreateContentFragment() {
        this.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        mFragment = new PublisherLocationFragment();
//        mFragment.setArguments(getIntent().getBundleExtra(LATLON_BUNDLE_KEY));
        return mFragment;
    }

    @Override
    protected void onPreLoad() {

    }

    @Override
    protected void onFinishLoad(Object o) {

    }

    @Override
    protected Object onLoadInBackground() {
        return null;
    }

    @Override
    protected void onDestroyData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                if (mFragment.getView().dispatchKeyEvent(event)) {
                    return true;
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                    overridePendingTransition(0, R.anim.transout_to_bottom);
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }
}

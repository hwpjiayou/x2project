package com.renren.mobile.x2.components.publisher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;

public class PublisherSelectorActivity extends BaseFragmentActivity<Object> {

	private PublisherSelectorFragment mFragment;

    public final static int SLECTE_REQUEST_CODE = 0x000010;

    public static String SELECT_BUNDLE_KEY = "select_bundle";

    public static String SLECTE_LOCATION_TYPE_KEY = "select_location_type";

	@Override
	protected BaseFragment onCreateContentFragment() {
		this.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
		mFragment = new PublisherSelectorFragment();
        mFragment.setArguments(getIntent().getBundleExtra(SELECT_BUNDLE_KEY));
		return mFragment;
	}
	
    public static void show(Activity activity, String locationKey) {
        Intent intent = new Intent(activity, PublisherSelectorActivity.class);
        Bundle data = new Bundle();
        data.putString(SLECTE_LOCATION_TYPE_KEY, locationKey);
        intent.putExtra(SELECT_BUNDLE_KEY,data);
        activity.startActivityForResult(intent, SLECTE_REQUEST_CODE);
        activity.overridePendingTransition(R.anim.transin_from_bottom, R.anim.transout_freez);;
    }

	@Override
	protected void onPreLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFinishLoad(Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object onLoadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onDestroyData() {
		// TODO Auto-generated method stub

	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.transout_freez, R.anim.transout_to_bottom);
    }
}

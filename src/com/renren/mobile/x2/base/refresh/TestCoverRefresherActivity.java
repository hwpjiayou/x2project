package com.renren.mobile.x2.base.refresh;

import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;

public class TestCoverRefresherActivity extends BaseFragmentActivity<Void> {

    @Override
    protected BaseFragment onCreateContentFragment() {
        return new TestCoverRefresherFragment();
    }

    @Override
    protected void onPreLoad() {
    }

    @Override
    protected void onFinishLoad(Void aVoid) {
    }

    @Override
    protected Void onLoadInBackground() {
        return null;
    }

    @Override
    protected void onDestroyData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTitleBar().setTitle("CoverRefresher");
    }
}

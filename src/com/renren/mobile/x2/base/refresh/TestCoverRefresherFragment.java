package com.renren.mobile.x2.base.refresh;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.home.feed.FeedAdapter;
import com.renren.mobile.x2.components.home.feed.FeedModel;
import com.renren.mobile.x2.view.CoverWrappedListView;

import java.util.ArrayList;
import java.util.List;


public class TestCoverRefresherFragment extends BaseFragment<Void> {
    private static final List<FeedModel> DATA = new ArrayList<FeedModel>();

   /* static {
    	FeedModel model;
        for (int i = 0; i < 100; i++) {
            model = new FeedTextModel(null);
            model.content = String.valueOf(SystemClock.uptimeMillis());
            model.publisherTime = "00:00";
            model.userName = "FUCKER";
            DATA.add(model);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CoverWrappedListView coverWrappedListView = new CoverWrappedListView(getActivity());
        coverWrappedListView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        coverWrappedListView.getListView().setAdapter(new FeedAdapter(getActivity(), DATA));
        return coverWrappedListView;
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
}

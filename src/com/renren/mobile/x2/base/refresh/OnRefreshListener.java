package com.renren.mobile.x2.base.refresh;

public interface OnRefreshListener {
    void onPreRefresh();

    void onRefreshData();

    void onRefreshUI();
}

package com.renren.mobile.x2.components.publisher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.chat.view.ProgressImageButton;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.dao.LocationNameDao;
import com.renren.mobile.x2.network.mas.UGCPlaceModel;
import com.renren.mobile.x2.network.mas.UGCUserModel;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;
import com.renren.mobile.x2.utils.location.RenrenLocationManager;
import com.renren.mobile.x2.utils.log.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * PublisherLocationFragment
 * @author  xiaoguang.zhang
 * Date: 12-11-9
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */
@ViewMapping(ID = R.layout.publisher_location_layout)
public class PublisherLocationFragment extends BaseFragment<HashMap<String, ArrayList<LocationNameItem>>> {

    private int pagerSize;

    private ArrayList<UGCPlaceModel> placeTypelist;

    private ArrayList<GridAdapter> gridAdapters = new ArrayList<GridAdapter>();

    @ViewMapping(ID = R.id.location_cover)
    public ImageView mLocationCover;

    @ViewMapping(ID = R.id.location_info)
    public FrameLayout mLocationInfo;

    @ViewMapping(ID = R.id.location_info_name)
    public EditText mLocationInfoName;

    @ViewMapping(ID = R.id.refresh_location_rote)
    public ProgressImageButton mRefreshLocationRote;

    @ViewMapping(ID = R.id.location_info_select_btn)
    public Button mLocationInfoSelectBtn;

    @ViewMapping(ID = R.id.location_info_user_head)
    public ImageView mLocationInfoUserHead;

    @ViewMapping(ID = R.id.refresh_location_btn)
    public Button mRefreshLocationBtn;

    @ViewMapping(ID = R.id.publisher_location_type_pager)
    public ViewPager mLocationTypePager;

    private Map<String, ArrayList<LocationNameItem>> mLocationNameMap;

    private boolean mIsLocationNameFinish;

    private boolean mIsLocationDone;

    private LocationNameDao locationNameDao;

    private ArrayList<GridView> mPagerContainer = new ArrayList<GridView>();

    private Handler mHandler = new Handler();

//    private double mLatitude;
//
//    private double mLongitude;

    private JSONObject mLatLon;

    public static String PLACE_MODEL_KEY = "place_model";

//    public static String LONG_ITUDE_KEY = "long_iturde";
//
//    public static String LAT_ITUDE_KEY = "lat_iturde";

    private LayoutInflater mInflater;

    private UGCPlaceModel mPlaceModel;

    private Activity mActivity;

    @Override
    protected void onPreLoad() {
        locationNameDao = DAOFactoryImpl.getInstance().buildDAO(LocationNameDao.class);
    }

    @Override
    protected void onFinishLoad(HashMap<String, ArrayList<LocationNameItem>> stringArrayListHashMap) {
        mLocationNameMap = stringArrayListHashMap;
        mIsLocationNameFinish = true;
        for(GridAdapter adapter : gridAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected HashMap<String, ArrayList<LocationNameItem>> onLoadInBackground() {

        HashMap<String, ArrayList<LocationNameItem>> map = new HashMap<String, ArrayList<LocationNameItem>>();
        for(String key : UGCPlaceModel.names) {
            map.put(key, locationNameDao.queryAllByType(key));
        }
        return map;
    }

    @Override
    protected void onDestroyData() {
        if(mLocationNameMap != null) {
            mLocationNameMap.clear();
            mLocationNameMap = null;
        }
        if(placeTypelist != null) {
            placeTypelist.clear();
            placeTypelist = null;
        }
        if(gridAdapters != null) {
            for(GridAdapter adapter : gridAdapters) {
                adapter = null;
            }
            gridAdapters.clear();
            gridAdapters = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PublisherSelectorActivity.SLECTE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                mLocationInfoName.setText(data.getStringExtra(PublisherSelectorFragment.LOCATION_CUSTOM_NAME_KEY));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mActivity.setResult(Activity.RESULT_CANCELED);
    }

    private void initData() {
        RenrenLocationManager.getRenrenLocationManager(mActivity).
                getLatLon(new RenrenLocationManager.GetLatLonListener() {
                    @Override
                    public void getLatLon(JSONObject LatLon) {
                        mLatLon = LatLon;
                        if(mLatLon != null) {
                            mIsLocationDone = true;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mLocationInfoSelectBtn.setVisibility(View.VISIBLE);
                                    mRefreshLocationBtn.setVisibility(View.GONE);
                                    mRefreshLocationRote.stopdelay(1000);
                                    mRefreshLocationRote.setVisibility(View.GONE);
                                    mLocationInfoName.setText("请选择位置");
                                    if(gridAdapters != null) {
                                        for(GridAdapter adapter : gridAdapters) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        } else {
                            mRefreshLocationRote.stopdelay(1000);
                            CommonUtil.toast("获取定位信息失败");
                        }
                    }
                });
    }

    private void initView() {
        if(mLatLon != null) {
            mRefreshLocationBtn.setVisibility(View.GONE);
            mRefreshLocationRote.setVisibility(View.GONE);
            mLocationInfoSelectBtn.setVisibility(View.VISIBLE);
            mLocationInfoName.setText("请选择位置");
        } else {
            mRefreshLocationBtn.setVisibility(View.VISIBLE);
            mRefreshLocationRote.setVisibility(View.VISIBLE);
            mRefreshLocationRote.start(20);
            mLocationInfoSelectBtn.setVisibility(View.GONE);
            mLocationInfoName.setText("没有定位信息请等待定位");
        }
        mLocationInfoName.setEnabled(false);
        mLocationInfoSelectBtn.setEnabled(false);
        final String headUrl  = LoginManager.getInstance().getLoginInfo().mMediumUrl;

        if(headUrl == null) {
            return;
        }

        Logger.logd("log", "headUrl:" + headUrl);
        mLocationInfoUserHead.setTag(headUrl);
        ImageLoader.HttpImageRequest request = new ImageLoader.HttpImageRequest(headUrl, true);
        ImageLoader  loader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD, mActivity);

        Bitmap bitmap = loader.getMemoryCache(request);
        if(bitmap != null){
            mLocationInfoUserHead.setImageBitmap(bitmap);
        }
        else{
            ImageLoader.Response response = new ImageLoader.Response() {

                @Override
                public void success(final Bitmap bitmap) {
                    RenrenChatApplication.getUiHandler().post(new Runnable() {

                        @Override
                        public void run() {
                            mLocationInfoUserHead.setImageBitmap(bitmap);
                        }
                    });
                }

                @Override
                public void failed() {

                }
            };
            if(LoginManager.getInstance().getLoginInfo().mGender == UGCUserModel.GENDER_FAMALE)
                mLocationInfoUserHead.setImageResource(R.drawable.v1_default_famale);
            else{
                mLocationInfoUserHead.setImageResource(R.drawable.v1_default_male);
            }
            loader.get(request, response);
        }
        getTitleBar().showLeft();
        getTitleBar().showRight();
        getTitleBar().setTitle("位置");
        initEvent();
        initPublisherTypePager();
    }

    private void initEvent() {
        getTitleBar().setLeftAction(R.drawable.publisher_cancel_selector, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.setResult(Activity.RESULT_CANCELED);
                mActivity.finish();
                mActivity.overridePendingTransition(0, R.anim.transout_to_bottom);
            }
        });
        getTitleBar().setRightAction(R.drawable.v1_publisher_location_ok_selector, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                String tempName = mLocationInfoName.getText().toString().trim();
                if("".equals(tempName)) {
                    CommonUtil.toast("位置名称不能为空");
                    return;
                }
                if(!tempName.equals(mPlaceModel.mName)) {
                    if(locationNameDao.queryByName(tempName) == null) {
                        LocationNameItem item = new LocationNameItem();
                        item.customName = tempName;
                        item.locationTypeName = mPlaceModel.mName;
                        locationNameDao.saveLocationData(item);
                    }
                }
                mPlaceModel.mName = tempName;
                data.putExtra(PLACE_MODEL_KEY, mPlaceModel);
                mActivity.setResult(Activity.RESULT_OK, data);
                mActivity.finish();
                mActivity.overridePendingTransition(0, R.anim.transout_to_bottom);
            }
        });
        getTitleBar().setRightEnable(false);
        mRefreshLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshLocationRote.start(20);
                initData();
            }
        });
        mLocationInfoSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublisherSelectorActivity.show(mActivity, mPlaceModel.mName);
            }
        });
    }



    @ViewMapping(ID = R.layout.publisher_location_grid_item)
    private class GridItemHolder {

        @ViewMapping(ID = R.id.location_type_icon)
        public ImageView locationTypeIcon;

        @ViewMapping(ID = R.id.location_type_name)
        public TextView locationTypeName;
    }

    private class GridAdapter extends BaseAdapter {

        private int mPageIndex;

        private GridView gridView;

        public GridAdapter(int pageIndex, GridView grid) {
            super();
            mPageIndex = pageIndex;
            gridView = grid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final UGCPlaceModel placeType = placeTypelist.get(position + mPageIndex * 9);
            final GridItemHolder holder;
//            if (convertView != null) {
//                holder = (GridItemHolder) convertView.getTag();
//                Log.v("@@@", "convertView != null" + position);
//            } else {
//            Log.v("@@@", "convertView == null" + position);
            holder = new GridItemHolder();
            convertView = ViewMapUtil.viewMapping(holder, mInflater, null);
//            }
            convertView.setTag(holder);
            int height = gridView.getHeight();
            GridView.LayoutParams params = new GridView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height / 3);
            convertView.setLayoutParams(params);
            holder.locationTypeName.setText(placeType.mName);
            if(mIsLocationDone && mIsLocationNameFinish) {
                if (placeType.mIsSelected) {
                    holder.locationTypeIcon.setImageResource(placeType.mIconSelected);
                    Log.v("@@@", "v.setSelected(true):" + placeType.mIsSelected + "  " + convertView);
                } else {
                    holder.locationTypeIcon.setImageResource(placeType.mIconSelector);
                    Log.v("@@@", "v.setSelected(false):" + placeType.mIsSelected + "  " + convertView);
                }
                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 设置图标
//                        Log.v("@@@", "v.onClick!");
                        if (!placeType.mIsSelected) {
//                            Log.v("@@@", "!placeType.mIsSelected:" + placeType.mIsSelected);
                            for (UGCPlaceModel model : placeTypelist) {
                                model.mIsSelected = false;
                            }
                            placeType.mIsSelected = true;
                            mPlaceModel = placeType;
                            mPlaceModel.mLatLon = mLatLon.toString();
                            mLocationInfoName.setEnabled(true);
                            mLocationInfoName.setText(mPlaceModel.mName);
                            if(mLocationNameMap.get(placeType.mName) != null && mLocationNameMap.get(placeType.mName).size() > 0) {
                                mLocationInfoSelectBtn.setEnabled(true);
                            } else {
                                mLocationInfoSelectBtn.setEnabled(false);
                            }
                            getTitleBar().setRightEnable(true);
//                            Log.v("@@@", "placeType.mIsSelected is:" + placeType.mIsSelected);
                            for(GridAdapter adapter : gridAdapters) {
                                adapter.notifyDataSetChanged();
                            }
//                            Log.v("@@@", "notifyDataSetChanged!");
                        }
                    }
                });
            } else {
                Log.v("@@@", "mIsLocationDone is:" + mIsLocationDone + " and  mIsLocationNameFinish is:" + mIsLocationNameFinish);
                holder.locationTypeIcon.setImageResource(placeType.mIconId);
            }

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            // 9是每页的最大个数
            if ((mPageIndex + 1) * 9 <= placeTypelist.size()) {
                return 9;
            } else {
                return placeTypelist.size() - mPageIndex * 9;
            }
        }
    }

    private void initPublisherTypePager() {
        // 初始化活动数据
        placeTypelist = UGCPlaceModel.getPlaceModelList();
        pagerSize = placeTypelist.size() % 9 == 0?placeTypelist.size() / 9 : placeTypelist.size() / 9 + 1;
        for (int i = 0; i < pagerSize; i++) {
            final int pageIndex = i;
            final GridView grid = (GridView) mInflater.inflate(R.layout.location_gridview_layout, null);
            mPagerContainer.add(grid);
            GridAdapter adapter = new GridAdapter(pageIndex, grid);
            gridAdapters.add(adapter);
            // 初始化GridView
            grid.setAdapter(adapter);
        }


        // 初始化ViewPager
        mLocationTypePager.setAdapter(new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return mPagerContainer.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(mPagerContainer.get(position));
                return mPagerContainer.get(position);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View v = ViewMapUtil.viewMapping(this, inflater, container);
        initView();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mActivity.setResult(Activity.RESULT_CANCELED);
                    mActivity.finish();
                    mActivity.overridePendingTransition(0, R.anim.transout_to_bottom);
                    return true;
                } else {
                    return false;
                }
            }
        });
        v.requestFocus();
        initData();
        return v;
    }
}

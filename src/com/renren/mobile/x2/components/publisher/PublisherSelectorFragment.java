package com.renren.mobile.x2.components.publisher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.dao.LocationNameDao;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;

import java.util.ArrayList;
import java.util.List;

@ViewMapping(ID = R.layout.publisher_selector_layout)
public class PublisherSelectorFragment extends BaseFragment<ArrayList<LocationNameItem>> {

    public static String LOCATION_CUSTOM_NAME_KEY = "location_custom_type_name";
	
    @ViewMapping(ID = R.id.publisher_selector_listview)
    public ListView mListView;

    private String locationKey;

    private LocationNameDao locationNameDao;
	
	private Activity mActivity;
	private LayoutInflater mInflater;
	private PublisherSelectorAdapter mAdapter;
	private List<LocationNameItem> mDataList = new ArrayList<LocationNameItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        locationKey = getArguments().getString(PublisherSelectorActivity.SLECTE_LOCATION_TYPE_KEY);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View v = ViewMapUtil.viewMapping(this, inflater, container);
        initView();
        return v;
    }
    
    private void initView() {
        getTitleBar().showLeft();
        getTitleBar().setTitle("历史位置");
        
        mListView.setItemsCanFocus(true);
        mListView.setAddStatesFromChildren(true);
        mListView.setFocusableInTouchMode(true);
        mListView.setVerticalFadingEdgeEnabled(false);
        mListView.setCacheColorHint(0);
        
        mAdapter = new PublisherSelectorAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra(LOCATION_CUSTOM_NAME_KEY, mDataList.get(position).customName);
                mActivity.setResult(Activity.RESULT_OK, data);
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.transout_freez, R.anim.transout_to_bottom);
            }
        });
        initEvent();
    }
    
    private void initEvent() {
        getTitleBar().setLeftAction(R.drawable.publisher_cancel_selector, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.setResult(Activity.RESULT_CANCELED);
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.transout_freez, R.anim.transout_to_bottom);
            }
        });
    }
    
	@Override
	protected void onPreLoad() {
        locationNameDao = DAOFactoryImpl.getInstance().buildDAO(LocationNameDao.class);
	}

	@Override
	protected void onFinishLoad(ArrayList<LocationNameItem> data) {
        mDataList = data;
        mAdapter.notifyDataSetChanged();
	}

	@Override
	protected ArrayList<LocationNameItem> onLoadInBackground() {
        ArrayList<LocationNameItem> list = locationNameDao.queryAllByType(locationKey);
        return list;
	}

	@Override
	protected void onDestroyData() {
        mDataList.clear();
        mDataList = null;
	}
	
    @ViewMapping(ID = R.layout.publisher_selector_list_item)
    private class ViewHolder {

        @ViewMapping(ID = R.id.publisher_selector_text)
        public TextView mSelectorText;
    }
	
	public class PublisherSelectorAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.publisher_selector_list_item, null);
				ViewMapUtil.viewMapping(holder, convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final String s = mDataList.get(position).customName;
			holder.mSelectorText.setText(s);
			
			return convertView;
		}
		 
	}

}

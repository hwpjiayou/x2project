package com.renren.mobile.x2.components.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.SystemService;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectCollegeAdapter extends BaseAdapter {

	public class ViewHolder{
		@ViewMapping(ID=R.id.college_name)
		public TextView mCollegeName;
		 
	}
	
	private List<SchoolModel> mData = new ArrayList<SchoolModel>();
	
	private Context mContext;
	
	private ViewHolder mHolder;
	
	public SelectCollegeAdapter(Context context){
		mContext = context;
	}
	
	public void setAdapterData(List<SchoolModel> data){
		mData.clear();
		if(data != null){
			mData.addAll(data);
		}
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			mHolder = new ViewHolder();
			convertView = SystemService.sInflaterManager.inflate(R.layout.f_select_college_item, null);
			ViewMapUtil.viewMapping(mHolder, convertView);
			convertView.setTag(mHolder);
		}
		mHolder = (ViewHolder)convertView.getTag();
		
		mHolder.mCollegeName.setText(mData.get(position).getName());
		 
		return convertView;
	}
	
	/**
	 * school model
	 * @author shichao.song
	 *
	 */
	public static class SchoolModel {
		String id;
		String name;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return "SelectCollegeAdapter " + getId() + " " + getName();
		}
		
		
		
	}
	
	

}

package com.renren.mobile.x2.components.home.profile;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.home.feed.FeedAdapter;
import com.renren.mobile.x2.components.home.feed.FeedModel;
import com.renren.mobile.x2.components.home.feed.FeedViewHolder;
import com.renren.mobile.x2.network.mas.UGCTagModel;
import com.renren.mobile.x2.utils.DipUtil;
import com.renren.mobile.x2.utils.ViewMapUtil;

public class ProfileFeedAdapter extends FeedAdapter{
private ListView mProfileFeedList;
	public ProfileFeedAdapter(Context context, List<FeedModel> list, ListView listView) {
		super(context, list);
		mProfileFeedList = listView;
		mFeedStatus = true;
	}
	@Override
	public void setHeadHolderData(FeedViewHolder holder, FeedModel userModel, boolean isScrolling, ListView listview) {
	}
    @Override
    public void setPostTagData(FeedViewHolder holder, UGCTagModel tagModel) {
    	holder.mBottomLayout.setVisibility(View.VISIBLE);
    	holder.mHeadImageLayout.setVisibility(View.GONE);
    	holder.mGenderImg.setVisibility(View.GONE);
    	holder.mTypeVLine.setVisibility(View.GONE);
    	if(tagModel!=null){
    		if(!TextUtils.isEmpty(tagModel.mName)){
        		holder.mUserName.setText(tagModel.mName);
        	}
    	}else{
    		holder.mUserName.setText("学习啦");
    	}
		
    }
    @Override
    public void reset(JSONArray array) {
    	try {
			JSONObject object = null;
			mModelList.clear();
			for (int i = 0; i < array.length(); i++) {
				object = array.getJSONObject(i);
				FeedModel model = new FeedModel(object);
				mModelList.add(model);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			
			@Override
			public void run() {
				notifyDataSetChanged();
				mProfileFeedList.invalidate();
				}
			});
		}
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FeedViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.feed_adapter_item_layout,
					null);
			holder = new FeedViewHolder();
			ViewMapUtil.viewMapping(holder, convertView);
		} else {
			holder = (FeedViewHolder) convertView.getTag();
		}
		convertView.setTag(holder);
		FeedModel feedDataModel = null;
			feedDataModel = mModelList.get(position);
		setViewHolderData(holder, feedDataModel,
				mProfileFeedList);
		holder.mHorizonLine.setVisibility(View.GONE);
		holder.mTagText.setVisibility(View.GONE);
		if(position == 0){  //将头部竖线去掉
			holder.mHeadVerticalLine.setVisibility(View.GONE);
		}
		else{
			holder.mHeadVerticalLine.setVisibility(View.VISIBLE);
		}
		holder.mTypeLayout.setPadding(DipUtil.calcFromDip(8), DipUtil.calcFromDip(8),0, 0);
		return convertView;
		}
    }


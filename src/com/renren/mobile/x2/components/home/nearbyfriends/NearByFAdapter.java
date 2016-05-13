package com.renren.mobile.x2.components.home.nearbyfriends;


import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.home.nearbyfriends.NearByFData.NearByFNnode;
import com.renren.mobile.x2.components.home.profile.ProfileActivity;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/****
 * 
 * @author xiaochao.zheng 
 * 附近人的Adapter
 *
 */
public class NearByFAdapter extends BaseAdapter{
	private final static int offset = 3;
	private NearByFData mNearByFData;
	private Context mContext;
	private ImageLoader mImageLoader;
	private LayoutInflater mInflater;
	private Bitmap mBitmapDefault;
	/**
	 * @param context 上下文
	 * @param data 数据，不能为null
	 */
	public NearByFAdapter(Context context,NearByFData data) {
		this.mContext = context;
		try {
			this.mNearByFData = data.clone();
			ErrLog.Print("data in NearByFAdapter " + data.length + "  "+ mNearByFData.length);
		} catch (CloneNotSupportedException e) {
			ErrLog.Print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		mInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		mImageLoader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD, mContext);
		mBitmapDefault = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test_widget_default_head);
	}
	/***
	 * 重新设置数据，是异步数据好了之后调用
	 * @param data
	 */
	public void resetData(NearByFData data){
		ErrLog.Print("resetData and notifyDatasetChanged "  + mNearByFData.length);
		Log.d("zxc","resetData and notifyDatasetChanged "  + mNearByFData.length);
//		this.mNearByFData.clear();
		if(data== null){
			return ;
		}
		try {
			this.mNearByFData = data.clone();
		} catch (CloneNotSupportedException e) {
			ErrLog.Print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		ErrLog.Print("after length data legth" +data.length +"  mNearData length  "+mNearByFData.length);
		this.notifyDataSetChanged();//通知数据更新
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNearByFData.length+offset;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(position<3){
			View view = mInflater.inflate(R.layout.nearbyfriends_item_supplement, null);
			return view;
		}else{
			View view  = mInflater.inflate(R.layout.nearbyfriends_item,null);
		final ImageView img = (ImageView) view.findViewById(R.id.nearbyf_user_head_img);
		if(mNearByFData.ishasbitmap(position-offset)){
			img.setImageBitmap(mNearByFData.getBitmap(position-offset));
		}else{
			//下载
			if(mBitmapDefault!=null){
				img.setImageBitmap(mBitmapDefault);
			}
			img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					NearByFNnode node = mNearByFData.getNode(position-offset);
					ProfileActivity.show(mContext, node.getUId()+"");
					
				}
			});
			mImageLoader.get(new ImageLoader.HttpImageRequest(mNearByFData.getImageUrl(position-offset), true), new ImageLoader.UiResponse() {
				@Override
				public void failed() {
//					Toast.makeText(mContext, "下载图片失败撒", Toast.LENGTH_SHORT);
	            	ErrLog.Print("download img failed ");
					Log.d("abc"," position " + position);
				}
				@Override
				public void uiSuccess(Bitmap bitmap) {
//					Log.d("abc"," position " + position +"   " +bitmap);
					Log.d("abc",position + " " + bitmap);
					img.setImageBitmap(bitmap);			
				}
			});
		}
		ImageView gender = (ImageView) view.findViewById(R.id.nearbyf_gender);
		if(mNearByFData.getGender(position-offset)==1){
			gender.setImageResource(R.drawable.feed_famale);
		}else{
			gender.setImageResource(R.drawable.feed_male);
		}
		TextView distance = (TextView) view.findViewById(R.id.nearbyf_distance);
		TextView time = (TextView) view.findViewById(R.id.nearbyf_time);
		distance.setText(mNearByFData.getDistance(position-offset)+"m");
//		time.setText(""+Methods.longToString(mNearByFData.getTime(position)));
		TextView usernametext = (TextView) view.findViewById(R.id.nearbyf_username);
		usernametext.setText(mNearByFData.getUsername(position-offset));
		return view;
	
		}
		
	}
	private class ViewHolder {
		public ImageView mHeadPhoto;
		public TextView mUserName;
		public TextView mStatus;
		public TextView mPlace;
	}

}

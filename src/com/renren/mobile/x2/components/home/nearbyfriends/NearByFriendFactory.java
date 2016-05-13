package com.renren.mobile.x2.components.home.nearbyfriends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.db.dao.NearByFriendDAO;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.Pinyin;
import com.renren.mobile.x2.utils.PinyinUtils;
import com.renren.mobile.x2.utils.location.NewLocationListener;
import com.renren.mobile.x2.utils.location.NewLocationManager;
import com.renren.mobile.x2.utils.location.RenrenLocationData;
import com.renren.mobile.x2.utils.location.RenrenLocationManager;
import com.renren.mobile.x2.utils.location.RenrenLocationManager.RenrenLocationListener;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class NearByFriendFactory {
	private static NearByFriendFactory mInstance;
	private static Context mContext;
	private boolean isRequestting = false;
	public static NearByFriendFactory getInstance(Context c){
		if(mInstance == null){
			mInstance=new NearByFriendFactory();
		}
		mContext = c;
		return mInstance;
	}
	
	public boolean getFromNet(final NearByFriendDAO dao, final NearByfCallBack nearlistener){
		Log.d("zxc","getFromNet");
		final NearByFData data = new NearByFData();
		if(isRequestting){
			return false;
		}else{
			isRequestting = true;//表示现在正在进行网络请求
		}
		RenrenLocationListener listener = new RenrenLocationListener() {

			@Override
			public void onLocationSucceeded(RenrenLocationData location) {
				Log.d("zxc","onLocationSucceeded");
				INetResponse response = new INetResponse() {
					@Override
					public void response(INetRequest req, JSONObject obj) {
						if(Methods.checkNoError(req, obj)){

							Log.d("zxc","ret json " + obj.toString()); 
							ErrLog.Print(obj.toString());
							isRequestting = false;
							ErrLog.Print("jsonresponse "+obj.toString());
							assert dao!=null;
							dao.clearData();
							data.clear();
							try {
								
								JSONArray ja = obj.getJSONArray("friends_list");
								JSONObject jo;
								for (int i = 0; i < ja.length(); ++i) {
									NearByFModel model = new NearByFModel();
									jo = ja.getJSONObject(i);
									String url = jo.getString("head_url");
									model.url=url;	
									String signature= "";
									if(jo.has("status_text")){
										signature = jo.getString("status_text");
										model.userstatus = signature;
									}
									long uid = jo.getLong("user_id");
									model.userid = uid;
									String username = jo.getString("user_name");
									model.username=username;
									int gender =-1;///暂时的男女策略,
									if(jo.has("gender")){
										gender = jo.getInt("gender");
									}
									model.user_gender= gender;
									Log.d("zxc","gender " + gender);
									Pinyin re = PinyinUtils.getPinyin(username);
									assert re!=null;
									String usernameheadchar = "";
									char[][] array = re.getMultiPinYin();
									assert array!=null;
									for(int j = 0 ; j < array.length;++j){
										usernameheadchar += array[j][0];
									}
									model.username_headchar= usernameheadchar;
									ErrLog.Print("pinyinheadchar "+usernameheadchar + "     ");
									try{
										dao.saveNearByfData(model);
									}catch(Exception e){
										ErrLog.Print(e.getLocalizedMessage());
									}
									data.add(url, signature, uid, usernameheadchar,
											username,gender);
								}
//								synchronized (mLock) {
//									mLock.notifyAll();
//								}
//								RenrenChatApplication.getUiHandler().post(new Runnable() {
//									
//									@Override
//									public void run() {
//										if(isShowDialog){
//											mScreen.hidProgressBar();
//										}
//										mScreen.resetData(mData);
//										mScreen.mImageReflashBtn.stopdelay(2000);
//									}
//								});
//								
								nearlistener.onDataLoadingSuccess(data);
							} catch (JSONException e) {
								nearlistener.onDataLoadingError(data);
								e.printStackTrace();
								ErrLog.Print(
										"jsonexception error "
												+ e.getLocalizedMessage());
							}
						
						}
					}
				};
				HttpMasService.getInstance().getNearbyFriendsList(null,
						location.getGpsLat(), location.getGpsLon(), 1, 1, 2000,
						0, "time", "all", response, false);
			}

			@Override
			public void onLocatedFailed(String errorMessage, int errorCode) {
				// TODO Auto-generated method stub
				isRequestting=false;//set false while failed
			}

			@Override
			public void onLocatedCancel() {
				// TODO Auto-generated method stub
				isRequestting=false;//set false while canceled;
			}
		};
		Log.d("zxc","start location ");
		RenrenLocationManager.getRenrenLocationManager(mContext)
				.startLocateSingle(listener, false, false);
		NewLocationListener newlistener = new NewLocationListener() {
			
			@Override
			public void onLocationSuccess(Location location) {
				Log.d("location","Location success " + location.getLatitude() + "  " + location.getLongitude());
			}
			
			@Override
			public void onLocationFailed(int errcode) {
				// TODO Auto-generated method stub
				Log.d("location","location failed ");
				
			}
		};
		boolean iscanLocation = NewLocationManager.getInstance(mContext).startLocation(newlistener);///可以定位的则返回true，不可以就返回
		if(!iscanLocation){
			Log.d("zxc","can not location ");
		}
		return iscanLocation;
	
	}
}

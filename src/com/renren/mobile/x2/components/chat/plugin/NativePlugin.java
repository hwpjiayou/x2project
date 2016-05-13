package com.renren.mobile.x2.components.chat.plugin;

import android.view.View;
import android.view.View.OnClickListener;
import com.renren.mobile.x2.components.chat.PluginAdapter;
import com.renren.mobile.x2.components.chat.PluginAdapter.PluginListenter;
import com.renren.mobile.x2.utils.DeviceUtil;
import com.renren.mobile.x2.utils.log.Logger;

public abstract class NativePlugin{

	private int mIcon  ;
	private String mName;
	public OnClickListener mClick;

	public NativePlugin(int icon,String name){
		mIcon = icon;
		mName = name;
		this.initEvent();
	}

	void initEvent(){
		mClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Logger.mDebug){
					Logger.logd("initEvent");
				}
				if(DeviceUtil.getInstance().isMountSDCard()&&DeviceUtil.getInstance().isSDCardHasEnoughSpace()){
					startPlugin();
				}else{
					if(!DeviceUtil.getInstance().isMountSDCard()){
						DeviceUtil.getInstance().toastNotMountSDCard();
					}
					if(!DeviceUtil.getInstance().isSDCardHasEnoughSpace()){
						DeviceUtil.getInstance().toastNotEnoughSpace();
					}
				}
			}
		};
	}

	public int getIconId(){
		return this.mIcon;
	}

	public abstract void startPlugin();


	public static class NativePlugin_TakePhoto extends NativePlugin {
		PluginAdapter adapter =null;
		public NativePlugin_TakePhoto(int icon, String name,PluginAdapter adapter) {
			super(icon, name);
			this.adapter= adapter;
		}
		@Override
		public void startPlugin() {
			if(adapter!=null){
				adapter.startPlugin(PluginListenter.PLUGIN_TAKE_PHOTO, null);
			}
		}
	}


	public static class NativePlugin_SelectPhoto extends NativePlugin {
		PluginAdapter adapter =null;
		public NativePlugin_SelectPhoto(int icon, String name,PluginAdapter adapter) {
			super(icon, name);
			this.adapter= adapter;
		}
		@Override
		public void startPlugin() {
			if(adapter!=null){
				adapter.startPlugin(PluginListenter.PLUGIN_SELECT_PHOTO, null);
			}
		}
	}


	public String getName() {
		return mName;
	}
	
}

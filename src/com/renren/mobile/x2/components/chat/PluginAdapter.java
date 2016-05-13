package com.renren.mobile.x2.components.chat;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.plugin.NativePlugin;
import com.renren.mobile.x2.components.chat.util.ChatUtil;
import com.renren.mobile.x2.components.chat.view.PluginGroup.AbstractPluginAdapter;
import com.renren.mobile.x2.components.chat.view.PluginItemView;
import com.renren.mobile.x2.components.chat.plugin.NativePlugin.NativePlugin_TakePhoto;
import com.renren.mobile.x2.components.chat.plugin.NativePlugin.NativePlugin_SelectPhoto;
import com.renren.mobile.x2.utils.log.Logger;



/**
 * @author dingwei.chen
 * */
public final class PluginAdapter extends AbstractPluginAdapter {
		
	
	private List<PluginListenter> listenters = new ArrayList<PluginListenter>();

	List<NativePlugin> mPlugins = new ArrayList<NativePlugin>();
	public  PluginAdapter(){
		mPlugins.add(new NativePlugin_TakePhoto(R.drawable.test_plugin_takephoto, ChatUtil.getText(R.string.chat_take_photo),this));
		mPlugins.add(new NativePlugin_SelectPhoto(R.drawable.test_plugin_selectphoto, ChatUtil.getText(R.string.chat_album),this));
	}
	
	@Override
	public int getCount() {
		return mPlugins.size();
	}
	public NativePlugin getData(int position){
		return mPlugins.get(position);
	}

	@Override
	public View getView(Context context,int position) {
		NativePlugin model = mPlugins.get(position);
		PluginItemView view = new PluginItemView(context);
		view.setPluginSource(model.getIconId(), model.getName());
		view.setOnClickListener(model.mClick);
		return view;
	}

	
	public interface  PluginListenter {
		public static byte PLUGIN_TAKE_PHOTO = 1; 
		public static byte PLUGIN_SELECT_PHOTO = 2;
		public void startPlugin(byte type,Bundle mBundle);
	}
	
	public void register(PluginListenter listenter){
		if(listenter!=null){
			listenters.add(listenter);
		}
	}
	
	public void unRegister(PluginListenter listenter){
		if(listenter!=null){
			listenters.remove(listenter);
		}
	}
	
	public void startPlugin(byte type,Bundle mBundle){
		if(Logger.mDebug){
			Logger.logd("startPlugin type="+type+"#size="+listenters.size());
		}
		for (PluginListenter listenter : listenters) {
			listenter.startPlugin(type, mBundle);
		}
	}
	
}

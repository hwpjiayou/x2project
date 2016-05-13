package com.renren.mobile.x2.components.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;

public final class ChatFrament extends BaseFragment<Object>{

	private View mRootView;
	
	public ChatFrament(Context context){	
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.chat_main,null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return getRootView();
	}
	
	public View getRootView(){
		return mRootView;
	}

	@Override
	protected void onPreLoad() {
		
	}

	@Override
	protected void onFinishLoad(Object data) {		
	}

	@Override
	protected Object onLoadInBackground() {
		return null;
	}

	@Override
	protected void onDestroyData() {
	}

	

}

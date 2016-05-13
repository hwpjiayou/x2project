package com.renren.mobile.x2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.renren.mobile.x2.RenrenChatApplication;

/**
 * 此类主要是为了统一SharedPreferences的使用，每个人创建的SharedPreferences文件以及文件中的每个key对应的意义，
 * 在下面的注释中补全
 * 
 * FileName:renren_setting bt_notify_vibrate 是否震动 bt_notify_led 是否显示led
 * 
 * FileName:demo2 key1 注释1 key2 注释2
 * 
 * @author tian.wang
 * **/
public class RRSharedPreferences {
	private String mFileName;
	private SharedPreferences mSp;
	private SharedPreferences.Editor mEditor;

	public RRSharedPreferences(Context context) {
		this.mSp = PreferenceManager.getDefaultSharedPreferences(context);
		this.mEditor = this.mSp.edit();
	}

	public RRSharedPreferences(Context context, String name) {
		if (name == null || name.length() == 0) {
			return;
		}
		this.mFileName = name;
		this.mSp = RenrenChatApplication
				.getApplication()
				.getSharedPreferences(
						name,
						RenrenChatApplication.getApplication().MODE_WORLD_READABLE
								| RenrenChatApplication.getApplication().MODE_WORLD_WRITEABLE);
		this.mEditor = this.mSp.edit();
	}

	public void clear() {
		this.mEditor.clear();
	}

	public int getIntValue(String key, int defValue) {
		return mSp.getInt(key, defValue);
	}

	public void putIntValue(String key, int value) {
		mEditor.putInt(key, value);
		commit();
	}

	public boolean getBooleanValue(String key, boolean defValue) {
		return mSp.getBoolean(key, defValue);
	}

	public void putBooleanValue(String key, boolean value) {
		mEditor.putBoolean(key, value);
		commit();
	}

	public float getFloatValue(String key, float defValue) {
		return mSp.getFloat(key, defValue);
	}

	public void putFloatValue(String key, float value) {
		mEditor.putFloat(key, value);
		commit();
	}

	public long getLongValue(String key, long defValue) {
		return mSp.getLong(key, defValue);
	}

	public void putLongValue(String key, long value) {
		mEditor.putLong(key, value);
		commit();
	}

	public String getStringValue(String key, String defValue) {
		return mSp.getString(key, defValue);
	}

	public void putStringValue(String key, String value) {
		mEditor.putString(key, value);
		commit();
	}

	public void put(String key, String value) {
		mEditor.putString(key, value);
		commit();
	}
	
//此处只进行put,由调用者进行手动commit,以提高commit的效率
	public void putStringValue2(String key, String value) {
		mEditor.putString(key, value);
	}
	
	public void commit() {
		mEditor.commit();
	}

	public Editor getEditor() {
		return mEditor;
	}

	public void remove(String key) {
		this.mEditor.remove(key);
		commit();
	}
}

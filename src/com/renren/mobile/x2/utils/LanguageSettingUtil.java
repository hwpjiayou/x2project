package com.renren.mobile.x2.utils;

import java.util.Locale;

import android.content.res.Configuration;
import android.text.TextUtils;

import com.renren.mobile.x2.RenrenChatApplication;

/**
 * @author xiangcho.fan
 * @description 本地语言设置
 */
public class LanguageSettingUtil {

	private static LanguageSettingUtil sInstance = new LanguageSettingUtil();

	public static LanguageSettingUtil getInstance() {
		return sInstance;
	}

	/** sharedPreferenc本地存储名 */
	public static final String SP_NAME = "language_setting_sp";

	public static final String LANGUAGE_MODE = "language_mode";

	public static interface LanguageType {
		int DEFAULT_VALUE = -1;
		int SIMPLE_CHINESE = 0;
		int COMPLEX_CHINESE = 1;
		int ENGLISH = 2;
	}

	private int mLanguageType;

	private RRSharedPreferences mSP;

	private LanguageSettingUtil() {
		mSP = new RRSharedPreferences(RenrenChatApplication.getApplication(),SP_NAME);
	}

	public int getLanguageType() {
		mLanguageType = mSP.getIntValue(LANGUAGE_MODE,LanguageType.DEFAULT_VALUE);
		return mLanguageType;
	}

	public void setLanguageType(int languageType) {
		mSP.putIntValue(LANGUAGE_MODE, languageType);
	}

	public String getLanguageStr(int selectLanguageType) {
		String language = "";
		switch (selectLanguageType) {
		case LanguageType.SIMPLE_CHINESE:
			language = getLocaleString(Locale.SIMPLIFIED_CHINESE);
			break;
		case LanguageType.COMPLEX_CHINESE:
			language = getLocaleString(Locale.TAIWAN);
			break;

		case LanguageType.ENGLISH:
			language = getLocaleString(Locale.US);
			break;
		}
		return language;
	}

	public String getLanguage() {
		String str;
		if ("com.renren.mobile.x2".equals(RenrenChatApplication
				.getApplication().getPackageName())) {
			str = getLocaleString(Locale.SIMPLIFIED_CHINESE);
		} else {
			str = getLanguageStr(getLanguageType());
			if (TextUtils.isEmpty(str)) {
				str = getLocaleString(Config.DEFAULT_LOCALE);
			}
		}
		return str;
	}

	public String getLocaleString(Locale locale) {
		return String.format("%s_%s", locale.getLanguage(), locale.getCountry());
	}
	
	
//	国际版调用
	public Locale getLocale(){
		Locale locale=Locale.US;
		switch (getLanguageType()) {
		case LanguageType.SIMPLE_CHINESE:
			locale = Locale.SIMPLIFIED_CHINESE;
			break;
		case LanguageType.COMPLEX_CHINESE:
			locale = Locale.TAIWAN;
			break;
		case LanguageType.ENGLISH:
			locale = Locale.US;
			break;
		}
		return locale;
	}
	
//	国际版调用
	public void updateConfiguration(){
		Configuration config = RenrenChatApplication.getApplication().getResources().getConfiguration();
		config.locale=getLocale();
		RenrenChatApplication.getApplication().getResources().updateConfiguration(config, null);
	}
}

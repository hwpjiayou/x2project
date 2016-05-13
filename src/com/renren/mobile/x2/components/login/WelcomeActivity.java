/**
 * 
 */
package com.renren.mobile.x2.components.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginActivity;
import com.renren.mobile.x2.components.login.RenrenLoginActivity;
import com.renren.mobile.x2.emotion.EmotionManager;

/**
 * @author Administrator
 * 
 */
public class WelcomeActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		this.initScreen();// 初始化屏幕

		this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome_layout);
		
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				EmotionManager.setContext(WelcomeActivity.this);
				EmotionManager.getInstance();
				
				
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent();
						intent.setClass(WelcomeActivity.this,
								RenrenLoginActivity.class);
						startActivity(intent);
						// overridePendingTransition(R.anim.zoomin,
						// R.anim.zoomout);
						WelcomeActivity.this.finish();

					}
				}, 800);
			}
		}).start();

	}
 
	@Override
	protected void onDestroy() {
		super.onDestroy();
	} 

	private void initScreen() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		RenrenChatApplication.initScreenInfomation(dm.heightPixels,
				dm.widthPixels);
	}

}

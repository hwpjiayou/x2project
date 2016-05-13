package com.renren.mobile.x2.components.publisher;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;

public class PublisherFragmentActivity extends BaseFragmentActivity<Object> {
	PublisherFragment mFragment;

    private boolean isFirstComein;

	@Override
	protected BaseFragment onCreateContentFragment() {
		this.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
		mFragment = new PublisherFragment();
		getTitleBar().hide();
        isFirstComein = true;
		return mFragment;
	}

	/**
	 * TODO 后期完善
	 */
	private void iniTitleBar() {
		getTitleBar().setLeftAction(R.drawable.publisher_cancel_unpress, null);
		getTitleBar().setRightAction(R.drawable.publisher_send_unpress, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
        if(isFirstComein) {
		    overridePendingTransition(R.anim.transin_from_bottom, R.anim.transout_freez);
            isFirstComein = false;
        }

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// 处理表情回弹和退出弹框
		if (keyCode == KeyEvent.KEYCODE_BACK && (mFragment.mEmotioncontainer.getVisibility() == View.VISIBLE || mFragment.mVoiceContainer.getVisibility() == View.VISIBLE)) {
			mFragment.mEmotioncontainer.setVisibility(View.GONE);
			mFragment.mVoiceContainer.setVisibility(View.GONE);
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			mFragment.exit();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// 退出动画
		overridePendingTransition(0, R.anim.transout_to_bottom);
	}

	
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		Log.d("pa","@PublisherFragmentActivity onActivityResult");
		super.onActivityResult(arg0, arg1, arg2);
        mFragment.onActivityResult(arg0, arg1, arg2);
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

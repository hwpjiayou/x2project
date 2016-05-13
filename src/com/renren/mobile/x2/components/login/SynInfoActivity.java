package com.renren.mobile.x2.components.login;

import android.content.Intent;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;
import com.renren.mobile.x2.components.imageviewer.PhotoUploadManager;
import com.renren.mobile.x2.components.photoupload.HeadEditActivity;

public class SynInfoActivity extends BaseFragmentActivity<DATA>{

	@Override
	protected BaseFragment onCreateContentFragment() {
		SynInfoFragment synInfoFragment = new SynInfoFragment();
		return synInfoFragment;
	}

	@Override
	protected void onPreLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onFinishLoad(DATA data) {
		// TODO Auto-generated method stub
		
	}
	

		
				

	@Override
	protected DATA onLoadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onDestroyData() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
			switch (requestCode) {
			
			/* 拍照上传头像 */
			case PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO:
				Intent intent = new Intent(this,
						HeadEditActivity.class);
				intent.putExtra(Intent.EXTRA_STREAM,
						PhotoUploadManager.getInstance().mUri);
				startActivity(intent);
				break;
				
			
			/* 本地上传头像 */
			case PhotoUploadManager.REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY:
				if(data != null){
				Intent intent1 = new Intent(this,
						HeadEditActivity.class);
				intent1.putExtra(Intent.EXTRA_STREAM, data.getData());
				startActivity(intent1);
				 
				break;
			}
		}
	}
	
}

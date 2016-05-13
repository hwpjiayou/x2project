package com.renren.mobile.x2.components.home.profile;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.network.talk.binder.ISender;
import com.renren.mobile.x2.utils.CommonUtil;

public class ProfileFragment extends BaseFragment<ProfileDataModel>{
	ProfileView profileView;
	private String mUserId;

	public ProfileFragment(String id) {
		mUserId = id;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		profileView = new ProfileView(getActivity());
		return profileView.getView();
	}

	@Override
	protected void onPreLoad() {
		getTitleBar().setTitle(getString(R.string.profile_title));
		if(!mUserId.equals(LoginManager.getInstance().getLoginInfo().mUserId)){
			getTitleBar().setRightAction(R.drawable.v1_chat_title_unselect, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					profileView.toChat();
					
				}
			});
		}
		
		getTitleBar().setLeftAction(R.drawable.v1_comment_title_left_unpress, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
	}

	@Override
	protected void onFinishLoad(ProfileDataModel data) {
		
	}

	@Override
	protected ProfileDataModel onLoadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onDestroyData() {
		// TODO Auto-generated method stub
		
	}
	 /** 头像上传成功回调接口 */
    public interface PhotoUploadSuccessListener {
        public void updateUI_Photo();
    }

    private static final List<PhotoUploadSuccessListener> PHOTO_UPLOAD_SUCCESS_LISTENERS =
            new ArrayList<PhotoUploadSuccessListener>();

    public static void registerPhotoUploadSuccessListener(PhotoUploadSuccessListener listener) {
        if (listener != null && !(PHOTO_UPLOAD_SUCCESS_LISTENERS.contains(listener))) {
            PHOTO_UPLOAD_SUCCESS_LISTENERS.add(listener);
        }
    }

    public static void notifyAllPhotoUploadSuccessListeners() {
        for (PhotoUploadSuccessListener listener : PHOTO_UPLOAD_SUCCESS_LISTENERS)
            listener.updateUI_Photo();
    }

    public static void unregisterPhotoUploadSuccessListener(PhotoUploadSuccessListener listener) {
        PHOTO_UPLOAD_SUCCESS_LISTENERS.remove(listener);
    }
}


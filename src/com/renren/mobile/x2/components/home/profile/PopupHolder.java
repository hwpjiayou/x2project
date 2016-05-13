package com.renren.mobile.x2.components.home.profile;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.ViewMapping;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupHolder {
	@ViewMapping(ID = R.id.profile_popup)
	public TextView mPopupName;
	@ViewMapping(ID = R.id.profile_changeimg_viewimg)
	public Button mViewImag;
	@ViewMapping(ID = R.id.profile_changeimg_local)
	public Button mCamera;
	@ViewMapping(ID = R.id.profile_changeimg_camera)
	public Button mLocal;
	@ViewMapping(ID = R.id.profile_changeimg_cancel)
	public Button mCancel;
}

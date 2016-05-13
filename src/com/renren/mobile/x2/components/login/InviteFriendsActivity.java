package com.renren.mobile.x2.components.login;

import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;

/**
 * 登陆流程的邀请好友
 * @author shichao.song
 *
 */
public class InviteFriendsActivity extends BaseFragmentActivity<DATA> {

	@Override
	protected BaseFragment onCreateContentFragment() {
		return new InviteFriendsFragment();
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

}


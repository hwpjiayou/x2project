package com.renren.mobile.x2.components.chat.holder;

import android.content.Context;
import android.widget.TextView;



/**
 * @author dingwei.chen
 * @说明 条件选择视图持有器
 * */
public class ChatItemSelectHolder extends BaseHolder {

	
	public TextView mDelete;
	public TextView mCopy;
	public TextView mForward;
	
	public static interface VIEW_ID{
//		int DELETE_TEXTVIEW_ID=R.id.cdw_chat_choice_select_container_delete_textview;
//		int COPY_TEXTVIEW_ID=R.id.cdw_chat_choice_select_container_copy_textview;
//		int FORWARD_TEXTVIEW_ID = R.id.cdw_chat_choice_select_container_forward_textview;
		//<TODO cf>
		int DELETE_TEXTVIEW_ID = 0;
		int COPY_TEXTVIEW_ID  = 0 ;
		int FORWARD_TEXTVIEW_ID = 0;
	}
	
	
	public ChatItemSelectHolder(Context context, int layoutID) {
		super(context, layoutID);
		this.initViews();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mDelete =(TextView)this.mRoot.findViewById(VIEW_ID.DELETE_TEXTVIEW_ID);
		mCopy =(TextView)this.mRoot.findViewById(VIEW_ID.COPY_TEXTVIEW_ID);
		mForward =(TextView)this.mRoot.findViewById(VIEW_ID.FORWARD_TEXTVIEW_ID);
	}

}

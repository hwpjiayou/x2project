package com.renren.mobile.x2.components.chat.holder;

import android.view.View;
import android.widget.ImageView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;


/**
 * @author dingwei.chen
 * @分离目的 为了保证viewmaping映射正常
 * */
public class InitViewHolder_From extends BaseInitHolder{
		
		@ViewMapping(ID=R.id.cdw_chat_listview_item_voice_unlisten)	
		public ImageView mMessage_Voice_Unlisten_ImageView;//发送失败的icon
		
		
		public InitViewHolder_From(View view) {
			ViewMapUtil.viewMapping(this, view);
		}
		

		@Override
		public void initViewBySubClass(ChatItemHolder holder, int messageType) {
			holder.mMessage_Voice_Unlisten_ImageView = mMessage_Voice_Unlisten_ImageView;
		}
	
}

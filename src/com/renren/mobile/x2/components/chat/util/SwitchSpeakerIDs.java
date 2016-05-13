package com.renren.mobile.x2.components.chat.util;

import com.renren.mobile.x2.R;



/**
 * @author dingwei.chen
 * @说明 说话话筒资源ID
 * */
public interface SwitchSpeakerIDs {

	public static final int LEFT_SOURCE_LENGTH = 4;
	public static final int RIGHT_SOURCE_LENGTH = 4;
	
	int[] LEFT_SOURCE_IDS=new int[]{
			R.drawable.v1_chat_voice_item_left_3,
			R.drawable.v1_chat_voice_item_left_2,
			R.drawable.v1_chat_voice_item_left_1,
			R.drawable.v1_chat_voice_item_left_0,
	};
	
	int[] RIGHT_SOURCE_IDS=new int[]{
			R.drawable.v1_chat_voice_item_right_3,
			R.drawable.v1_chat_voice_item_right_2,
			R.drawable.v1_chat_voice_item_right_1,
			R.drawable.v1_chat_voice_item_right_0,
	};
	
}

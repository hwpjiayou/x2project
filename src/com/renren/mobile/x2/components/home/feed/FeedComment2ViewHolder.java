package com.renren.mobile.x2.components.home.feed;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.ViewMapping;

/**
 * Feed中Comment2中Voice的Holder
 * @author jia.xia
 *
 */
public class FeedComment2ViewHolder extends FeedViewHolder {

	@ViewMapping(ID = R.id.feed_comment2_voice_layout)
	public FrameLayout mVoiceLayout;
		//语音图像展示
		@ViewMapping(ID = R.id.feed_comment2_voice_img)
		public ImageView mVoiceImg;
		//语音时长展示
		@ViewMapping(ID = R.id.feed_comment2_voice_time_text)
		public TextView mVoiceTimeText;
		//语音的进度条
		@ViewMapping(ID = R.id.feed_comment2_voice_play_text)
		public ProgressBar mVoicePlayBar;
}

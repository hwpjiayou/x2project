package com.renren.mobile.x2.components.home.feed;

import java.io.Serializable;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.ViewMapping;

/**
 * 
 * @author jia.xia
 *
 */
public class FeedViewHolder {


	/**
	 * 头部竖线
	 */
	@ViewMapping(ID = R.id.feed_bottom_vertical_line)
	public ImageView mHeadVerticalLine;
	
	@ViewMapping(ID = R.id.feed_user_head_layout)
	public FrameLayout mHeadImageLayout;
	
	@ViewMapping(ID  = R.id.feed_type_img)
	public ImageView mTagTypeImg;
	
	@ViewMapping(ID=R.id.feed_user_head_img)
	public ImageView mHeadImage;
	
	@ViewMapping(ID=R.id.feed_username)
	public TextView mUserName;
	
	@ViewMapping(ID=R.id.feed_publishtime)
	public TextView mPublisherTime;
	
	@ViewMapping(ID=R.id.feed_left_bottom_layout)
	public RelativeLayout mFeedLeftTimeline;
	
	@ViewMapping(ID=R.id.header)
	public LinearLayout mFeedHeader;
	
	@ViewMapping(ID=R.id.headerimage)
	public ImageView mHeaderImage;
	
	//type布局
	@ViewMapping(ID = R.id.feed_type_layout)
	public FrameLayout mTypeLayout;
	//头像旁边的横杠
	@ViewMapping(ID = R.id.feed_horizontal_line)
	public ImageView mHorizonLine ;
	//多出来的竖线
	@ViewMapping(ID = R.id.feed_type_hline_img)
	public ImageView mTypeHLine;
	//多出来的横线
	@ViewMapping(ID = R.id.feed_type_vline_img)
	public ImageView mTypeVLine;
	
	@ViewMapping(ID = R.id.feed_vertical_line)
	public ImageView mVerticalLine;
	
	//当Item为最后一个时显示的圆球布局
	@ViewMapping(ID = R.id.feed_vertical_last_line)
	public ImageView mVerticalLastLine;
	
	//性别图像展示
	@ViewMapping(ID = R.id.feed_gender_img)
	public ImageView mGenderImg;
	
	//评论图像展示
	@ViewMapping(ID = R.id.feed_comment_img)
	public ImageView mCommentImg;
	//评论数展示
	@ViewMapping(ID = R.id.feed_commentcount_text)
	public TextView mCommentCountText;
	
	@ViewMapping(ID = R.id.feed_like_layout)
	public LinearLayout mLikeLayout;
	
	//赞图像展示
	@ViewMapping(ID = R.id.feed_like_img)
	public ImageView mLikeImg;
	//赞数量展示
	@ViewMapping(ID = R.id.feed_likecount_text)
	public TextView mLikeCountText;
	
	@ViewMapping(ID = R.id.feed_bottom_layout)
	public RelativeLayout mBottomLayout;
	
	//图片、Tag、语音的综合布局。程序中需要有大量布局设置
	@ViewMapping(ID = R.id.feed_photo_voice_layout)
	public FrameLayout mPhotoVoiceLayout;
	
	//图片布局
	@ViewMapping(ID=R.id.feed_single_photo_layout)
	public FrameLayout mSinglePhotoLayout;
	
	@ViewMapping(ID = R.id.feed_single_frontBg)
	public ImageView mSinglePhotoFrontBg;
	
	//图片展示	
	@ViewMapping(ID = R.id.feed_single_img)
	public NewImageView mSinglePhotoImage;
	//语音布局
	@ViewMapping(ID = R.id.feed_voice_layout)
	public FrameLayout mVoiceLayout;
	//语音图像展示
	@ViewMapping(ID = R.id.feed_voice_img)
	public ImageView mVoiceImg;
	//语音时长展示
	@ViewMapping(ID = R.id.feed_voice_time_text)
	public TextView mVoiceTimeText;
	//语音的进度条
	@ViewMapping(ID = R.id.feed_voice_play_text)
	public ProgressBar mVoicePlayBar;
	//Tag展示：位置基本不变
	@ViewMapping(ID = R.id.feed_bottom_tag_text)
	public TextView mTagText;
	//文本内容展示
	@ViewMapping(ID = R.id.feed_content)
	public TextView mContentText;
	//位置布局
	@ViewMapping(ID = R.id.feed_location_layout)
	public LinearLayout	mLocationLayout;
	//位置文本展示
	@ViewMapping(ID = R.id.feed_location_name)
	public TextView mLocationNameText;

	/////////////////////////comment layout /////////////////////////
	@ViewMapping(ID = R.id.feed_item_comment_layout)
	public LinearLayout mCommentLayout;
	
	////////////////////////comment 1/////////////////////////////////
	@ViewMapping(ID = R.id.feed_item_comment_comment1_layout)
	public LinearLayout mComment1Layout;
	
	@ViewMapping(ID = R.id.feed_item_comment_comment1_headimg)
	public ImageView mComment1HeadImg;
	
	@ViewMapping(ID = R.id.feed_item_comment_comment1_content)
	public TextView mComment1Content;
	//////////////////////////comment1语音///////////////////////////
	@ViewMapping(ID = R.id.feed_comment1_voice_layout)
	public FrameLayout mComment1VoiceLayout;
	
	@ViewMapping(ID =R.id.feed_comment1_voice_img)
	public ImageView mComment1VoiceImage;
	
	@ViewMapping(ID = R.id.feed_comment1_voice_play_text)
	public ProgressBar mComment1VoiceProgress;
	
	@ViewMapping(ID = R.id.feed_comment1_voice_time_text)
	public TextView mComment1VoiceTimeText;
	
	@ViewMapping(ID = R.id.feed_item_comment_comment1_username)
	public TextView mComment1Username;
////////////////////////comment 2/////////////////////////////////
	@ViewMapping(ID = R.id.feed_item_comment_comment2_layout)
	public LinearLayout mComment2Layout;
	
	@ViewMapping(ID = R.id.feed_item_comment_comment2_headimg)
	public ImageView mComment2HeadImg;
	
	@ViewMapping(ID = R.id.feed_item_comment_comment2_content)
	public TextView mComment2Content;
	//////////////////////////comment2 语音//////////////////////////////////
	
	@ViewMapping(ID = R.id.feed_comment2_voice_layout)
	public FrameLayout mComment2VoiceLayout;
	
	@ViewMapping(ID = R.id.feed_comment2_voice_img)
	public ImageView mComment2VoiceImg;
	
	@ViewMapping(ID = R.id.feed_comment2_voice_play_text)
	public ProgressBar mComment2VoiceProgress;
	
	@ViewMapping(ID = R.id.feed_comment2_voice_time_text)
	public TextView mComment2VoiceTime;
	
	@ViewMapping(ID = R.id.feed_item_comment_comment2_username)
	public TextView mComment2Username;
	
	/**
	 * 下面的定义在评论界面中需要用到，
	 */
	
	@ViewMapping(ID = R.id.addmore_header)
	public LinearLayout mCommentAddmorLayout;
	
	@ViewMapping(ID = R.id.like_header)
	public LinearLayout mCommentLikeHeader;
	
	@ViewMapping(ID = R.id.comments_addmore)
	public LinearLayout mCommentsAddmore;
	
	@ViewMapping(ID = R.id.show_commentes_info)
	public RelativeLayout mCommentsRelativeLayout;
	
	@ViewMapping(ID = R.id.num_comment_people)
	public TextView mNumCommentPeople;
	
	@ViewMapping(ID = R.id.feed_comment_drive)
	public LinearLayout mCommentFeedDrive;
	
	@ViewMapping(ID = R.id.tishi)
	public LinearLayout mTishi;
	
	@ViewMapping(ID = R.id.feed_like_people1)
	public ImageView mLikePeople1;
	
	@ViewMapping(ID = R.id.feed_like_people2)
	public ImageView mLikePeople2;
	
	@ViewMapping(ID = R.id.feed_like_people3)
	public ImageView mLikePeople3;
	
	@ViewMapping(ID = R.id.feed_like_people4)
	public ImageView mLikePeople4;
	
	@ViewMapping(ID = R.id.feed_like_people5)
	public ImageView mLikePeople5;
	
	@ViewMapping(ID = R.id.feed_like_people_more)
	public ImageView mLikePeopleMore;
	
	
	public String url ="";
}

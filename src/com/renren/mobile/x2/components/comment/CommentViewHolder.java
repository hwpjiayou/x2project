package com.renren.mobile.x2.components.comment;

import java.io.Serializable;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.view.CircleView;
import com.renren.mobile.x2.components.home.feed.NewImageView;
import com.renren.mobile.x2.utils.ViewMapping;

/**
 * 
 * @author hwp
 *
 */
public class CommentViewHolder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9181372419894830799L;

	
	@ViewMapping(ID = R.id.feed_user_head_layout)
	public FrameLayout mHeadImageLayout;
	
	@ViewMapping(ID=R.id.feed_user_head_img)
	public ImageView mHeadImage;
	
	@ViewMapping(ID=R.id.feed_username)
	public TextView mUserName;
	
	@ViewMapping(ID=R.id.feed_publishtime)
	public TextView mPublisherTime;
	
	//性别图像展示
	@ViewMapping(ID = R.id.feed_gender_img)
	public ImageView mGenderImg;
	
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

	
	/**
	 * 下面的定义在评论界面中需要用到，
	 */
	
	@ViewMapping(ID = R.id.addmore_header)
	public LinearLayout mCommentAddmorLayout;
	
	@ViewMapping(ID = R.id.like_header)
	public RelativeLayout mCommentLikeHeader;
	
	@ViewMapping(ID = R.id.comments_addmore)
	public LinearLayout mCommentsAddmore;
	
	@ViewMapping(ID = R.id.num_comment_people)
	public TextView mNumCommentPeople;
	
	@ViewMapping(ID = R.id.feed_comment_drive)
	public LinearLayout mCommentFeedDrive;
	
	@ViewMapping(ID = R.id.comment_like_image)
	public ImageView mCommentLike;
	
	@ViewMapping(ID = R.id.num_like_people)
	public TextView mNumLikePeople;
	
	@ViewMapping(ID = R.id.comment_like_layout)
	public LinearLayout mCommentLayout;
	
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

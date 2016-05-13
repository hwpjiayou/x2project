package com.renren.mobile.x2.components.home.feed;

/**
 * Feed中点击发评论监听事件
 * @author jia.xia
 *
 */
public interface FeedCommentClickListener {
	
	/**
	 * 点击评论响应
	 * @param feedId      String
	 */
	public void commentClick(String feedId);
}

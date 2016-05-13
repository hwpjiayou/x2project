package com.renren.mobile.x2.components.home;

import com.renren.mobile.x2.components.home.chatlist.ChatListManager;
import com.renren.mobile.x2.components.home.feed.FeedManager;
import com.renren.mobile.x2.components.home.profile.ProfileView;
import com.renren.mobile.x2.components.home.setting.SettingManager;
import com.renren.mobile.x2.components.message.MessageManager;

/**
 * Config of HomeTab
 */
public final class Config {

  /**
   * Prevent instance creation.
   */
  private Config() {
  }

  /**
   * Define class types of HomeTab.
   */
  public static final Class<?>[] TABS = new Class<?>[]{
      FeedManager.class,
      MessageManager.class,
      ChatListManager.class,
      ProfileView.class,
      SettingManager.class};
}

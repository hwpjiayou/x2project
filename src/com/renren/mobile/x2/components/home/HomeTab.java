package com.renren.mobile.x2.components.home;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Context;
import android.view.View;

/**
 * 该接口定义了一个Tab在{@link HomeActivity}中的一些必备的回调方法,{@link HomeActivity}会在适当的时机调用这些方法.它可以被理解成一个
 * 最简单的类似与Activity和Fragment的单元性类.想要在HomeActivity中加入Tab,都应该实现这个接口.并且在{@link Config}中定义它的类型.
 *
 * @author lu.yu
 */
public interface HomeTab {

  /**
   * 返回Tab的名字字符串的资源ID
   */
  int getNameResourceId();

  /**
   * 返回Tab图标的资源ID
   */
  int getIconResourceId();

  /**
   * 在第一次创建之后,{@link HomeActivity}会调用getView()来得到该Tab的根View.
   *
   * @return 根View
   */
  View getView();

  /**
   * 返回根View.这个View将会作为{@link HomeActivity}的一个Tab被加入到{@link ContentLayout}下.
   *
   * @param context 这个Context可以保存在你的类的字段中,方便以后使用.但是大部分和context相关的东西的创建都尽量在这个方法里面做.
   *                如果你确定不在以后的回调方法里面使用到这个context,你就可以不放在字段中,这样可以少一个引用.而且也不需要
   *                在onDestroyData里面把它设置成null. 这个context实际上是{@link HomeActivity}. 可以强转.
   * @return 新创建的根View
   */
  View onCreateView(Context context);

  /**
   * <b>切记这个方法是在非UI线程被调用的.</b></p>
   * 你的数据加载和长时间的操作都应该在这个方法里面进行.在这里千万不要操作UI,虽然不会必崩,但是几率性的Bug和FC最终会让你走向灭亡.
   * 事实证明,在更新的系统版本中,非UI线程操作UI不一定会崩溃,但是会出现各种非正常的UI行为.
   */
  void onLoadData();

  /**
   * 当onLoadData()方法执行完之后,才会调用onFinishLoad(),并且这个方法会在UI线程中调用.
   */
  void onFinishLoad();

  /**
   * {@link HomeActivity}里面会有多个Tab存在,当用户从另一个tab切换到目前的Tab的时候,这个方法就会被调用.
   */
  void onResume();

  /**
   * 这个方法和onResume()相反,从当前Tab切换到别的Tab的时候,这个方法会被调用.
   */
  void onPause();

  /**
   * 当{@link HomeActivity}被释放的时候,所有的Tab都会它们各自的onDestroyData()方法.你在这里可以把变量的引用都释放掉.
   */
  void onDestroyData();

  /**
   * 实现这个方法,返回一个监听事件,如果它不是空的话,就会在{@link HomeActivity#onActivityResult(int, int, android.content.Intent)}里面被调用.
   *
   * @return 监听事件
   */
  HomeFragment.OnActivityResultListener onActivityResult();
}


//~ Formatted by Jindent --- http://www.jindent.com

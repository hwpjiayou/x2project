package com.renren.mobile.x2.view;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 */
public interface ITitleBar {
    static final int MODE_TITLE = 1000;
    static final int MODE_ICON = 1001;
    
    /**
     * 得到现在Title处于的模式
     */
    int getMode();

    /**
     * 设置TitleBar的模式
     */
    void setMode(int mode);

    /**
     * 设置Title模式的标题字符串,调用这个方法会把模式从任何模式切换到MODE_TITLE
     */
    void setTitle(String title);

    /**
     * @see #setTitle(String)
     */
    void setTitle(CharSequence title);

    /**
     * 设置标题图标，调用这个方法会把模式从任何模式切换到MODE_ICON
     *
     * @param icon 图标
     * @param listener 监听
     */
    void setIcon(Drawable icon, View.OnClickListener listener);

    /**
     * @see #setIcon(Drawable, View.OnClickListener)
     */
    void setICon(int id, View.OnClickListener listener);

    /**
     * 显示TitleBar
     */
    void show();

    /**
     * 隐藏TitleBar
     */
    void hide();

    /**
     * 设置左按钮的图标和点击监听
     *
     * @param drawable 图标
     * @param listener 点击监听
     */
    void setLeftAction(Drawable drawable, View.OnClickListener listener);

    /**
     * @see #setLeftAction(Drawable, View.OnClickListener)
     * @param id resource id
     * @param listener OnClickListener
     */
    void setLeftAction(int id, View.OnClickListener listener);

    /**
     * 设置右按钮的图标和点击监听
     *
     * @param drawable 图标
     * @param listener 监听
     */
    void setRightAction(Drawable drawable, View.OnClickListener listener);

    /**
     * @see #setRightAction(Drawable, android.view.View.OnClickListener)
     * @param id resource id
     * @param listener OnClickListener
     */
    void setRightAction(int id, View.OnClickListener listener);

    /**
     * 显示左按钮
     */
    void showLeft();

    /**
     * 显示右按钮
     */
    void showRight();

    /**
     * 隐藏左按钮
     */
    void hideLeft();

    /**
     * 隐藏右按钮
     */
    void hideRight();

    /**
     * 标志左按钮是否可用
     * @param enable 表示是否可用，true为可有，false为不可用
     */
    void setLeftEnable(boolean enable);

    /**
     * 标志右按钮是否可用
     * @param enable 表示是否可用，true为可有，false为不可用
     */
    void setRightEnable(boolean enable);
}

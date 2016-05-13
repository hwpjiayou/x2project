package com.renren.mobile.x2.utils;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import com.renren.mobile.x2.utils.log.Logger;

/**
 * @author dingwei.chen
 * @说明 视图和ID映射关系工具
 * @说明 解除了ID映射间的重复代码和类型装换, 这样可以把重心转移到业务逻辑 在继承上不侵入任何类
 * @使用 1.为了提高效率将需要映射的View控件可见域置为public 2.对于需要映射的对象中的属性导入:
 * {@link com.renren.mobile.x2.utils.ViewMapping} 3.分离视图
 */
public final class ViewMapUtil {

    /**
     * @param object   要映射对象
     * @param rootView 要映射对象所要查询的根控件
     * @author dingwei.chen
     */
    public static void viewMapping(Object object, View rootView) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getFields();// 必须是public
        for (Field f : fields) {
            ViewMapping mapping = f.getAnnotation(ViewMapping.class);
            int id = 0;
            if (mapping != null) {
                try {
                    id = mapping.ID();
                    f.setAccessible(true);
                    f.set(object, rootView.findViewById(id));
                } catch (Exception e) {
                    CommonUtil.log("viewmap", "view map error = 0x" + Integer.toHexString(id) + ":" + e);
                    throw new RuntimeException();
                }
            }
        }
    }

    public static View viewMapping(Object object) {
        return viewMapping(object, SystemService.sInflaterManager, null);
    }

    public static View viewMapping(Object object, LayoutInflater inflater, ViewGroup root) {
    	
        View rootView = inflater.inflate(object.getClass().getAnnotation(ViewMapping.class).ID(), root, false);
        viewMapping(object, rootView);
        return rootView;
    }

    /**
     * 根据clazz新建对应的对象，并填入对应的View控件，然后调用activity的setContentView方法
     *
     * @param activity 当前的activity，接下来会调用setContentView
     * @param clazz    要返回ViewHolder的Class对象
     * @author yang-chen
     */
    public static <T> T viewMapping(Activity activity, Class<T> clazz) {
        Pair<T, View> pair = viewMapping(clazz);
        if (activity != null) {
            activity.setContentView(pair.second);
        }
        return pair.first;
    }

    /**
     * 根据ViewHolder的Class对象，新建一个ViewHolder类和对应Layout的View对象
     *
     * @return Pair.first是对应的ViewHolder，Pair.second是ViewHolder注解里面的Layout对应的View
     * @author yang-chen
     */
    public static <T> Pair<T, View> viewMapping(Class<T> clazz, LayoutInflater inflater, ViewGroup root) {
        Pair<T, View> pair = null;
        T object = null;
        try {
            object = clazz.getConstructor(new Class[]{}).newInstance(
                    new Object[]{});
            View rootView = inflater.inflate(
                    object.getClass().getAnnotation(ViewMapping.class).ID(), root, false);
            pair = new Pair<T, View>(object, rootView);
            viewMapping(object, rootView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pair;
    }

    public static <T> Pair<T, View> viewMapping(Class<T> clazz, LayoutInflater inflater) {
        return viewMapping(clazz, inflater, null);
    }

    public static <T> Pair<T, View> viewMapping(Class<T> clazz) {
        return viewMapping(clazz, SystemService.sInflaterManager, null);
    }

    public static <T> Pair<T, View> viewMapping(Class<T> clazz, ViewGroup root) {
        return viewMapping(clazz, SystemService.sInflaterManager, root);
    }
}

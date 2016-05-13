package com.renren.mobile.x2.components.message;

import com.renren.mobile.x2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * author yuchao.zhang
 *
 * 消息 视图
 */
public class MessageHolder {

    public MessageHolder(Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = inflater.inflate(R.layout.message_listview_item, null);
        head = (ImageView) root.findViewById(R.id.message_listview_item_head_image);
        name = (TextView) root.findViewById(R.id.message_listview_item_name);
        content = (TextView) root.findViewById(R.id.message_listview_item_content);
        time = (TextView) root.findViewById(R.id.message_listview_item_time);
        previewImage = (ImageView) root.findViewById(R.id.message_listview_item_preview_image);
    }

    /**
     * 根View
     */
    public View root;

    /**
     * 头像
     */
    public ImageView head;

    /**
     * 名字
     */
    public TextView name;

    /**
     * 内容
     */
    public TextView content;

    /**
     * 时间
     */
    public TextView time;

    /**
     * 新鲜事图片
     */
    public ImageView previewImage;
}

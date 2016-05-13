package com.renren.mobile.x2.components.message;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.home.feed.FeedAdapter;
import com.renren.mobile.x2.components.message.object.BaseMessageModel;
import com.renren.mobile.x2.db.dao.MessageDAO;
import com.renren.mobile.x2.db.dao.MessageObserver;
import com.renren.mobile.x2.utils.DateFormat;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoader.HttpImageRequest;
import com.renren.mobile.x2.utils.img.ImageLoader.TagResponse;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author yuchao.zhang
 *
 * 消息 视图与数据的适配器
 */
public class MessageAdapter extends BaseAdapter implements MessageObserver {

    private List<BaseMessageModel> mMessageItems = new ArrayList<BaseMessageModel>();
    private Context mContext;
    private ListView mListView;
    private MessageDAO mSubject;
    
 // 请求头像的ImageLoader
    private  ImageLoader mHeadImageLoader = null;
    
    public MessageAdapter(Context context, ListView listview) {

        mContext = context;
        mListView = listview;
        initImageLoader();
    }
    
    public void initImageLoader() {
        if (mHeadImageLoader == null) {
            mHeadImageLoader = ImageLoaderManager.get(
                    ImageLoaderManager.TYPE_HEAD, mContext);
        }
    }

    /**
     * @return the mMessageItems
     */
    public List<BaseMessageModel> getMessageItems() {
        return mMessageItems;
    }

    /**
     * @param messageItems the mMessageItems to set
     */
    public void setMessageItems(List<BaseMessageModel> messageItems) {
        this.mMessageItems = messageItems;
    }

    @Override public int getCount() {
        if(mMessageItems != null) {
            return mMessageItems.size();
        }
       return 0;
    }

    @Override public Object getItem(int position) {
        return mMessageItems.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup) {

        MessageHolder holder;

        if(null == convertView){

            holder = new MessageHolder(mContext);
            convertView = holder.root;
        } else {

            holder = (MessageHolder)convertView.getTag();
        }

        convertView.setTag(holder);

        BaseMessageModel item = mMessageItems.get(position);
        setData(holder, item);

        return convertView;
    }

    /**
     * 数据与视图绑定
     * @param holder
     * @param item
     */
    private void setData(MessageHolder holder, BaseMessageModel item){

        setHead(holder, item);
        setName(holder, item);
        setContent(holder, item);
        setTime(holder, item);
        setImage(holder, item);
    }

    /**
     * 头像的 数据与视图绑定
     * @param holder
     * @param item
     */
    private void setHead(final MessageHolder holder, BaseMessageModel item){

        final String headUrl = item.getHeadUrl();
        if(!TextUtils.isEmpty(headUrl)){

            holder.head.setVisibility(View.VISIBLE);
        } else {

            holder.head.setVisibility(View.GONE);
            return;
        }
        
        holder.head.setTag(headUrl);
        HttpImageRequest request = new HttpImageRequest(
                headUrl, !FeedAdapter.isScrolling);
        Bitmap bitmap = mHeadImageLoader.getMemoryCache(request);
        if (bitmap != null) {
            holder.head.setImageBitmap(bitmap);
        } else {
            TagResponse<String> headResponse = new TagResponse<String>(
                    headUrl) {

                @Override
                public void failed() {

                }

                @Override
                protected void success(final Bitmap bitmap, String tag) {
                    if (!FeedAdapter.isScrolling
                            && holder.head.equals(tag)) {
                        RenrenChatApplication.getUiHandler().post(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        ImageView image = (ImageView)  mListView
                                                .findViewWithTag(headUrl);
                                        if (image != null){
                                            image.setImageBitmap(bitmap);
                                        }
                                    }
                                });

                    }
                }
            };
            holder.head.setImageDrawable(null);
            mHeadImageLoader.get(request, headResponse);
        }
    }

    /**
     * 名字的 数据与视图绑定
     * @param holder
     * @param item
     */
    private void setName(MessageHolder holder, BaseMessageModel item){

        String name = item.getName();
        if(!TextUtils.isEmpty(name)){

            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        } else {

            holder.name.setVisibility(View.GONE);
        }
    }

    /**
     * 内容的 数据与视图绑定
     * @param holder
     * @param item
     */
    private void setContent(MessageHolder holder, BaseMessageModel item){

        String content = item.getContent();
        if(!TextUtils.isEmpty(content)){

            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(content);
        } else {

            holder.content.setVisibility(View.GONE);
        }
    }

    /**
     * 时间的 数据与视图绑定
     * @param holder
     * @param item
     */
    private void setTime(MessageHolder holder, BaseMessageModel item){

        String time = DateFormat.getDateByChatSession(item.getTime());
        if(!TextUtils.isEmpty(time)){

            holder.time.setVisibility(View.VISIBLE);
           
            holder.time.setText(time);
        } else {

            holder.time.setVisibility(View.GONE);
        }
    }

    /**
     * 图片的 数据与视图绑定
     * @param holder
     * @param item
     */
    private void setImage(MessageHolder holder, BaseMessageModel item){

        String image = item.getImageUrl();
        if(!TextUtils.isEmpty(image)){

            holder.previewImage.setVisibility(View.VISIBLE);
        } else {

            holder.previewImage.setVisibility(View.GONE);
        }
    }

    public void registerMessagePushObserver(MessageDAO subject){
        
        mSubject = subject;
        mSubject.registorObserver(this);
    }

    public void unRegisterMessagePushObserver(){
        if(this.mSubject!=null){
            this.mSubject.unregistorObserver(this);
        }
    }

    @Override
    public void onInsert(BaseMessageModel message) {
        
        if (message != null && mMessageItems != null) {
            mMessageItems.add(message);
            
            RenrenChatApplication.getUiHandler().post(new Runnable() {
                
                @Override
                public void run() {
                    notifyDataSetChanged();
                    
                }
            });
            
        }
        
    }

    @Override
    public void onDeleteAll() {
        // TODO Auto-generated method stub
        
    }
}

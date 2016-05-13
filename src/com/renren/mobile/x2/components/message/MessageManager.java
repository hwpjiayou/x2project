package com.renren.mobile.x2.components.message;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.home.HomeFragment.OnActivityResultListener;
import com.renren.mobile.x2.components.home.HomeTab;
import com.renren.mobile.x2.components.message.object.BaseMessageModel;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageManager implements HomeTab{
    
    private MessageView mRootView;

    @Override
    public int getNameResourceId() {
        return R.string.home_tab_messages;
    }

    @Override
    public int getIconResourceId() {
        return R.drawable.v1_home_menu_messages_selector;
    }

    @Override
    public View getView() {
        return mRootView.getView();
    }

    @Override
    public View onCreateView(Context context) {
        mRootView = new MessageView(context);
        return mRootView.getView();
    }

    @Override
    public void onLoadData() {
//        getMessageListFromNet();
    }

    @Override
    public void onFinishLoad() {
        mRootView.refresh();
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
        
    }

    @Override
    public void onDestroyData() {

        /* MessagePush的监听者 解除注册 */
        unRegisterMessagePushObserver();
    }

    @Override
    public OnActivityResultListener onActivityResult() {
        return null;
    }
    
    private void getMessageListFromNet() {
        INetResponse response = new INetResponse() {
            
            @Override
            public void response(INetRequest req, JSONObject obj) {
                Log.w("lx", "message list response: " + obj.toString());
                try {
                    JSONArray array = obj.getJSONArray("message");

                    List<BaseMessageModel> messages = new ArrayList<BaseMessageModel>();

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
//                        MessageItem item = new MessageItem(object);
//                        messages.add(item);
                    }
                    mRootView.setData(messages);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
            }
        };
        
        HttpMasService.getInstance().getNewsList(response);
    }

    public void unRegisterMessagePushObserver(){

        mRootView.unRegisterMessagePushObserver();
    }

}

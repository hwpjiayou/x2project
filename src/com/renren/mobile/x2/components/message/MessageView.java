package com.renren.mobile.x2.components.message;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.message.object.BaseMessageModel;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.dao.MessageDAO;
import com.renren.mobile.x2.network.talk.TalkServerEmulator;

public class MessageView {
    
    private Context mContext;
    private View mRootView;
    private ListView mMessageListView;
    private MessageAdapter mAdapter;
    private Long mUid;
    private Handler mHandler;
    
    public MessageView(Context context) {
        
        mContext = context;
        initView();
        initData();
        getData();
    }
    
    public View getView() {
        return mRootView;
    }

    private void getData(){

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override public void run() {
                
                TalkServerEmulator.simulateServerPush("comments");
            }
        }, 100);
    }
    
    private void initView() {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.message_list_view, null);
        mMessageListView = (ListView) mRootView.findViewById(R.id.message_list_view);
        mAdapter = new MessageAdapter(mContext, mMessageListView);
        mMessageListView.setAdapter(mAdapter);
    }
    
    private void initData() {
        String id = LoginManager.getInstance().getLoginInfo().mUserId;
        if(!TextUtils.isEmpty(id)) {
            mUid = Long.valueOf(id);
        }
        MessageDAO dao = DAOFactoryImpl.getInstance().buildDAO(MessageDAO.class);
        mAdapter.registerMessagePushObserver(dao);
        
        List<BaseMessageModel> dataList = dao.queryAll(mUid);
        if(dataList != null) {
            mAdapter.setMessageItems(dataList);
            mAdapter.notifyDataSetChanged();
        }
     
    }
    
    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }
    
    public void setData(List<BaseMessageModel> list) {
        mAdapter.setMessageItems(list);
    }

    public void unRegisterMessagePushObserver(){

        mAdapter.unRegisterMessagePushObserver();
    }
}

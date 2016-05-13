package com.renren.mobile.x2.components.comment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.base.BaseFragmentActivity;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.core.xmpp.node.XMPPNodeFactory;

import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.talk.*;
import com.renren.mobile.x2.service.PushMessagesReceiver;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.SystemService;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;
import com.renren.mobile.x2.utils.log.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiaobo.yuan
 * Date: 10/15/12
 * Time: 7:13 PM
 */
public class ChatActivityDemo extends BaseFragmentActivity{



    @ViewMapping(ID= R.layout.chat_activity_demo)
    public static class ViewHolder{
        @ViewMapping(ID=R.id.demo_text)
        public EditText text;
        @ViewMapping(ID=R.id.demo_contacts)
        public ListView listView;
        @ViewMapping(ID=R.id.demo_function)
        public Button button;

    }

    private ViewHolder mHolder;
    private ContactAdapter adapter;

    private static Logger logger = new Logger("yxb");
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mHolder = ViewMapUtil.viewMapping(this, ViewHolder.class);
//        getData();
//    }

    @Override
    protected BaseFragment onCreateContentFragment() {
        return new BaseFragment() {

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                mHolder = ViewMapUtil.viewMapping(ChatActivityDemo.this, ViewHolder.class);
                mHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        LoginManager.getInstance().logout();
                        TalkServerEmulator.simulateServerPush("comments");
                    }
                });
                getData();
                return inflater.inflate(R.layout.chat_activity_demo, null);
            }

            @Override
            protected void onPreLoad() {

            }

            @Override
            protected void onFinishLoad(Object o) {
            }

            @Override
            protected Object onLoadInBackground() {
                return null;
            }

            @Override
            protected void onDestroyData() {
            }
        };
    }

    @Override
    protected void onPreLoad() {
    }

    @Override
    protected void onFinishLoad(Object o) {
    }

    @Override
    protected Object onLoadInBackground() {
        return null;
    }

    @Override
    protected void onDestroyData() {
    }

    private void getData(){
        HttpMasService.getInstance().getContactList(new INetResponse() {
            @Override
            public void response(INetRequest req, JSONObject obj) {
                try {
                    ArrayList<ContactModel> models = new ArrayList<ContactModel>();
                    JSONArray ja = ((JSONObject) obj).getJSONArray("contact_list");
                    int length = ja.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jo = (JSONObject) ja.get(i);
                        String name = jo.getJSONObject("profile_info").getString("name");
                        long userId = jo.getJSONObject("profile_info").getLong("user_id");
                        ContactModel model = new ContactModel(userId, name);
                        models.add(model);


                    }
                    adapter = new ContactAdapter(ChatActivityDemo.this, models);
                    ChatActivityDemo.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mHolder.listView.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                }
            }
        }, 1, 2000, false);

    }

    private class ContactAdapter extends BaseAdapter{
        protected ArrayList<ContactModel> contacts;
        protected Context context;

        public ContactAdapter(Context context, ArrayList<ContactModel> data){
            this.context = context;
            this.contacts = data;

        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ContactModel model = contacts.get(position);
            if(model != null){
                convertView = SystemService.sInflaterManager.inflate(R.layout.contact_item_demo, null);
                TextView text = (TextView)convertView.findViewById(R.id.user_name);
                Button buton = (Button) convertView.findViewById(R.id.send_button);
                text.setText(model.userName);
                buton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                       final NetRequestListener request =
//                                    RequestConstructorDicProxy.getInstance(IReqeustConstructor.class).sendSynMessage(Long.parseLong(
//                                            LoginManager.getInstance().getLoginInfo().mUserId),
//                                            model.userId, DomainUrl.SIXIN_DOMAIN, DomainUrl.SIXIN_DOMAIN, new MessageManager.OnSendTextListener() {
//                                        @Override
//                                        public void onSendTextPrepare() {
//                                        }
//
//                                        @Override
//                                        public void onSendTextSuccess() {
//                                        }
//
//                                        @Override
//                                        public void onSendTextError() {
//                                        }
//
//                                        @Override
//                                        public boolean hasNewsFeed() {
//                                            return false;
//                                        }
//
//                                        @Override
//                                        public List<XMPPNode<?>> getNetPackage() {
//                                            XMPPNode body = XMPPNodeFactory.createXMPPNode("body");
//                                            body.addAttribute("type", "text");
//                                            XMPPNode text = XMPPNodeFactory.createXMPPNode("text");
//                                            text.mValue = mHolder.text.getText().toString();
//                                            body.addChildNode(text);
//                                            ArrayList<XMPPNode<?>> list = new ArrayList<XMPPNode<?>>();
//                                            list.add(body);
//                                            return list;
//                                        }
//                                    });
                        NetRequestListener request = new NetRequestListener() {
                            @Override
                            public void onNetError(String erroMeg) {
                                logger.v("send text error");
                            }

                            @Override
                            public void onNetSuccess() {
                                logger.v("send text successfully");
                            }

                            @Override
                            public String getSendNetMessage() {
                                XMPPNode message = XMPPNodeFactory.createXMPPNode("message");
                                message.addAttribute("from", LoginManager.getInstance().getLoginInfo().mUserId+"@"+DomainUrl.SIXIN_DOMAIN);
                                message.addAttribute("to", model.userId+"@"+DomainUrl.SIXIN_DOMAIN);
                                message.addAttribute("type", "chat");
                                message.addAttribute("fname", LoginManager.getInstance().getLoginInfo().mUserName);
                                message.addAttribute("id", getId());
                                XMPPNode body = XMPPNodeFactory.createXMPPNode("body");
                                body.addAttribute("type", "text");
                                XMPPNode text = XMPPNodeFactory.createXMPPNode("text");
                                text.mValue = mHolder.text.getText().toString();
                                body.addChildNode(text);
                                message.addChildNode(body);
                                return message.toXMLString();
                            }

							@Override
							public void onSuccessRecive(String data) {
								
							}
                        };
                        MessageManager.sendMessage(request);

                    }
                });
                return convertView;


            }
            return null;
        }

    }

    public static class ContactModel{
        public long userId;
        public String userName;

        public ContactModel(long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }
    }
}

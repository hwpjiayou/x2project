package com.renren.mobile.x2.components.chat.chatmessages;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.home.profile.ProfileActivity;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;

public class ChatMessagesView extends BaseFragment<ChatMessagesModel> {
	public static final String USER_NAME = "USER_NAME";
	public static final String USER_HEAD = "USER_HEAD";
	public static final String USER_TOID = "USER_TOID";
	private ProgressDialog mDialog;
	private String mToChatName;
	private String mToChatHead;
	private long mToChatId;
	private Activity mActivity;
	private View mRootView;
	private ViewHolder holder;
	protected LayoutInflater mInflater;
	private ChatMessageAdapter mAdapter;
	private ChatMessagesHolder mHolder;
	public List<ChatMessagesModel> mMembers = new ArrayList<ChatMessagesModel>();
	private ImageLoader loader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD, mActivity);

	public class ViewHolder {
		ImageView mHeadImg;
		TextView mUserName;
	}

	public static interface CREAT_ALERT_DIALOG_TYPE {
		int DEL_GROUP_FROM_FRIENDS_LIST = 0;
		int CLEAR_CHAT_HISTORY = 1;
		int SYN_CHAT_HISTORY = 2;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mActivity = getActivity();
		mDialog = new ProgressDialog(mActivity);
		initView();
		return mRootView;
	}

	private void initView() {
		mInflater = LayoutInflater.from(this.mActivity);
		mRootView = mInflater.inflate(R.layout.chat_messages, null);
		mHolder = new ChatMessagesHolder();
		mHolder.mGrideView = (GridView) mRootView.findViewById(R.id.gride);
		mHolder.mClearRecord = (FrameLayout) mRootView.findViewById(R.id.clear_record_frameLayout);
		mHolder.mSynRecord = (LinearLayout) mRootView.findViewById(R.id.syn_record_linearlayout);
		mHolder.mSynRecordTime = (TextView) mRootView.findViewById(R.id.syn_record_info);
		mAdapter = new ChatMessageAdapter();
	}

	@Override
	protected void onPreLoad() {
		Intent intent = mActivity.getIntent();
		mToChatName = intent.getStringExtra(USER_NAME);
		mToChatHead = intent.getStringExtra(USER_HEAD);
		mToChatId = intent.getLongExtra(USER_TOID,0);
		initToChatData();
		addMySelf();
	}

	private void addMySelf() {
		ChatMessagesModel addMessagesModel = new ChatMessagesModel();
		LoginInfo mLoginInfo = LoginManager.getInstance().getLoginInfo();
		addMessagesModel.userHead = mLoginInfo.mMediumUrl;
		addMessagesModel.userName = mLoginInfo.mUserName;
		mMembers.add(0, addMessagesModel);
	}

	private void initToChatData() {
		ChatMessagesModel MyMessagesModel = new ChatMessagesModel();
		MyMessagesModel.userHead = mToChatHead;
		MyMessagesModel.userName = mToChatName;
		mMembers.add(MyMessagesModel);

	}

	@Override
	protected void onFinishLoad(ChatMessagesModel data) {
		initEvent();
		mHolder.mGrideView.setAdapter(mAdapter);
	}

	private void initEvent() {
		mHolder.mClearRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showAlertDialog(mActivity.getText(R.string.chat_tip).toString(),mActivity.getText(R.string.chat_clear_messages).toString(),CREAT_ALERT_DIALOG_TYPE.CLEAR_CHAT_HISTORY,mToChatId);
			}
		});
		mHolder.mSynRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showAlertDialog(mActivity.getText(R.string.chat_get_cloud_messages).toString(), mActivity.getText(R.string.chat_get_last_cloud_messages).toString(), CREAT_ALERT_DIALOG_TYPE.SYN_CHAT_HISTORY, mToChatId);
			}
		});
	}

	private void showAlertDialog(String titleString, String messageString, final int type, final long toId) {
		AlertDialog.Builder builder = new Builder(mActivity);
        final CheckBox checkBox;
        if(CREAT_ALERT_DIALOG_TYPE.CLEAR_CHAT_HISTORY == type) {
            LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.chat_message_alert_dialog, null);
            checkBox = (CheckBox) layout.findViewById(R.id.delete_chat_history_from_server);
            checkBox.setChecked(false);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        if(!Methods.checkNet(mActivity, false)) {
                            checkBox.setChecked(false);
                            CommonUtil.toast(mActivity.getText(R.string.chat_clear_cloud_messages_error).toString());//"当前网络不可用不可以清空云端聊天记录"
                        }
                    }
                }
            });
            builder.setView(layout);
        } else {
            checkBox = null;
            builder.setMessage(messageString);		//ChatMessagesActivity_java_9=是否删除该对话？（只清空聊天记录和删除对话，但不会退出该群聊）;
        }
        builder.setTitle(titleString);		
        builder.setPositiveButton(mActivity.getText(R.string.ok).toString(), new DialogInterface.OnClickListener() {		//MultiChatForwardScreen_java_6=确认;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (type) {
                case CREAT_ALERT_DIALOG_TYPE.DEL_GROUP_FROM_FRIENDS_LIST:
//                    RoomInfosData.getInstance().deleteRoomFromContactOnNet(toId);
//                    finish();
                    break;
                case CREAT_ALERT_DIALOG_TYPE.CLEAR_CHAT_HISTORY:
//                    showDialog("正在清除...");
//                    isClearChatMessageNow = true;
                    if(checkBox != null && checkBox.isChecked()) {
                        Log.v("@@@", "清空云端加本地！");
//                        clearServerHistoryUitl.clearServerChatHistory(true, toId, isGroup);
                    } else {
                        Log.v("@@@", "只清空本地！");
//                        clearServerHistoryUitl.clearServerChatHistory(false, toId, isGroup);
                    }
                    break;
                case CREAT_ALERT_DIALOG_TYPE.SYN_CHAT_HISTORY:
//                    showDialog(RenrenChatApplication.mContext.getResources().getString(R.string.chatmessages_layout_13));
//                    isSynChatMessageNow = true;
//                    C_Syn.getInstance().syn(isGroup, toId, null, D_ChatMessagesActivity.this);
                    break;
                }
            }
        });
        builder.setNegativeButton(mActivity.getText(R.string.cancel).toString(), new DialogInterface.OnClickListener() {		//ChatMessageWarpper_FlashEmotion_java_4=取消;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
	}
	/*
	 * 弹出dialog
	 */
	private void showDialog(String text) {
		mDialog.setMessage(text);
		mDialog.show();
	}

	@Override
	protected ChatMessagesModel onLoadInBackground() {
		return null;
	}

	@Override
	protected void onDestroyData() {
		mMembers.clear();

	}

	public class ChatMessageAdapter extends BaseAdapter {
		public ChatMessageAdapter() {
		}

		@Override
		public int getCount() {
			return mMembers.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.group_member_item, null);
				holder.mHeadImg = (ImageView) convertView.findViewById(R.id.group_member_head);
				holder.mUserName = (TextView) convertView.findViewById(R.id.group_member_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ChatMessagesModel userInfoModel = mMembers.get(position);
			if (userInfoModel != null) {
				holder.mUserName.setText(userInfoModel.userName);
				if (!TextUtils.isEmpty(userInfoModel.userHead)) {
					loadHeadImg(userInfoModel.userHead, holder.mHeadImg);
				}
				setOnclick(holder,userInfoModel);
			}
			
			return convertView;
		}

		private void setOnclick(ViewHolder holder,ChatMessagesModel userInfoModel) {
			holder.mHeadImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ProfileActivity.show(mActivity, String.valueOf(mToChatId));
				}
			});
		}

		private void loadHeadImg(String head, final ImageView imageview) {
			loader.get(new ImageLoader.HttpImageRequest(head, true), new ImageLoader.UiResponse() {
				@Override
				public void uiSuccess(Bitmap mBitmap) {
					final Bitmap bitmap = mBitmap;
					RenrenChatApplication.getUiHandler().post(new Runnable() {

						@Override
						public void run() {
							imageview.setImageBitmap(bitmap);
						}
					});
				}

				@Override
				public void failed() {
				}
			});

		}
	}
}

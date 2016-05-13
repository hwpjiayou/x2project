package com.renren.mobile.x2.components.home.chatlist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.chatmessages.ChatMessagesActivity;
import com.renren.mobile.x2.components.chat.chatmessages.ChatMessagesView;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;
import com.renren.mobile.x2.components.chat.message.Subject;
import com.renren.mobile.x2.components.chat.notification.ChatNotificationManager;
import com.renren.mobile.x2.components.chat.util.ChatDataHelper;
import com.renren.mobile.x2.components.chat.util.ContactModel;
import com.renren.mobile.x2.components.chat.util.ThreadPool;
import com.renren.mobile.x2.db.dao.ChatHistoryDAO;
import com.renren.mobile.x2.db.dao.ChatHistoryDAOObserver;
import com.renren.mobile.x2.db.table.ChatHistory_Column;
import com.renren.mobile.x2.db.table.ChatList_Column;
import com.renren.mobile.x2.emotion.EmotionString;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DateFormat;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话列表Adapter
 * 
 * @author niulu
 * */

public final class ChatListAdapter extends BaseAdapter implements ChatHistoryDAOObserver {
	private List<Object> arr = new ArrayList<Object>();
	private Context mContext;
	private AlertDialog.Builder menu;
	protected LayoutInflater mInflater;
	private ChatHistoryDAO mSubject = null;
	private ChatListManager chatListManager = new ChatListManager();
	private ImageLoader loader = ImageLoaderManager.get(ImageLoaderManager.TYPE_HEAD, mContext);
	private Handler mHandler;

	private class ViewHolder {
		ImageView mHeadImg;
		TextView mUserName;
		TextView mTime;
		TextView mContent;
		LinearLayout mChatItemLayout;
		TextView mNotificationCount;
		ImageView mFail;
		ImageView mItemDivider;
		ImageView mLastDivider;
	}

	@Override
	public int getCount() {
		return arr.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public ChatListAdapter(Context context, List<ChatListDataModel> data, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
		menu = new AlertDialog.Builder(mContext);
		mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arr.clear();
		if (null != data) {
			arr.addAll(data);
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ChatListDataModel chatListDataModel = (ChatListDataModel) arr.get(position);
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chatlistview_item, null);
			mHolder = new ViewHolder();
			mHolder.mChatItemLayout = (LinearLayout) convertView.findViewById(R.id.chatlistview_item_layout);
			mHolder.mHeadImg = (ImageView) convertView.findViewById(R.id.head_img);
			mHolder.mUserName = (TextView) convertView.findViewById(R.id.chat_session_username);
			mHolder.mTime = (TextView) convertView.findViewById(R.id.chat_session_lasttime);
			mHolder.mNotificationCount = (TextView) convertView.findViewById(R.id.head_notificaiton_count);
			mHolder.mFail = (ImageView) convertView.findViewById(R.id.head_fail);
			mHolder.mContent = (TextView) convertView.findViewById(R.id.chat_session_lastcontent);
			mHolder.mLastDivider = (ImageView) convertView.findViewById(R.id.last_divider);
			mHolder.mItemDivider = (ImageView) convertView.findViewById(R.id.item_divider);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.mHeadImg.setTag(chatListDataModel.mToId);
		setChatListData(position,mHolder, chatListDataModel);
		mHolder.mChatItemLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ContactModel tmp = new ContactModel(chatListDataModel.mUserName, chatListDataModel.mToId,
						chatListDataModel.mHeadImg);
				RenRenChatActivity.show(mContext, tmp);
			}
		});
		mHolder.mChatItemLayout.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showMenu(chatListDataModel, position);
				return true;
			}
		});

		return convertView;
	}

	private void showMenu(final ChatListDataModel item, final int position) {
		String[] content = { mContext.getResources().getString(R.string.chatlist_chatmessage),
				mContext.getResources().getString(R.string.chatlist_deletechat) };
		menu.setTitle(item.mUserName);
		menu.setItems(content, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					Intent intent = new Intent(mContext, ChatMessagesActivity.class);
					intent.putExtra(ChatMessagesView.USER_HEAD, item.mHeadImg);
					intent.putExtra(ChatMessagesView.USER_NAME, item.mUserName);
					intent.putExtra(ChatMessagesView.USER_TOID, item.mToId);
					mContext.startActivity(intent);
				} else {
					deleteChatRecord(item.getmToId(), position, item);
				}
			}
		});
		menu.show();
	}

	private void deleteChatRecord(final long userId, final int position, final ChatListDataModel chatSessionDataModel) {
		new AlertDialog.Builder(mContext)
				.setTitle(mContext.getResources().getString(R.string.chatlist_warn))
				.setMessage(mContext.getResources().getString(R.string.chatlist_warncontent))
				.setPositiveButton(mContext.getResources().getString(R.string.chatlist_ensure),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								ChatListManager.mChatList.remove(chatSessionDataModel);
								arr.remove(chatSessionDataModel);
								if (ChatListManager.mChatList.size() == 0) {
									// mHandler.sendEmptyMessage(ChatListView.REFRESH_TEXT_VIEW);
								}
								ChatNotificationManager.getInstance().removeNotificationByGroupId(
										chatSessionDataModel.mToId);
								ChatDataHelper.getInstance().deleteChatMessageByUserId(chatSessionDataModel.mToId);
								ChatListHelper.getInstance().deleteChatSessionByUserId(chatSessionDataModel.mToId);
								notifyDataSetChanged();
							}
						})
				.setNegativeButton(mContext.getResources().getString(R.string.chatlist_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						}).create().show();
	}

	public void setChatListData(int position,final ViewHolder holder, final ChatListDataModel chatListDataModel) {
		if(position==getCount()-1){
			holder.mItemDivider.setVisibility(View.GONE);
			holder.mLastDivider.setVisibility(View.VISIBLE);
		}else{
			holder.mItemDivider.setVisibility(View.VISIBLE);
			holder.mLastDivider.setVisibility(View.GONE);
			
		}
		holder.mUserName.setText(chatListDataModel.mUserName);
		String date = DateFormat.getDateByChatSession(chatListDataModel.mLastTime);
		if (!date.equals("") && !date.equals(mContext.getResources().getString(R.string.chatlist_yesterday))
				&& !date.equals(mContext.getResources().getString(R.string.chatlist_theday_before))) {
			holder.mTime.setText(date);
		} else
			holder.mTime.setText(date + " " + DateFormat.getNowStrByChat(chatListDataModel.mLastTime));
		if (date.equals(mContext.getResources().getString(R.string.chatlist_yesterday) + "Special")) {
			holder.mTime.setText(DateFormat.getDateByChatSession1(chatListDataModel.mLastTime));
		}
		CommonUtil.log("lu",chatListDataModel.mHeadImg+chatListDataModel.mUserName);
		if (!TextUtils.isEmpty(chatListDataModel.mHeadImg)) {
			loader.get(new ImageLoader.HttpImageRequest(chatListDataModel.mHeadImg, true),
					new ImageLoader.UiResponse() {
						@Override
						public void uiSuccess(Bitmap mBitmap) {
							final Bitmap bitmap = mBitmap;
							RenrenChatApplication.getUiHandler().post(new Runnable() {

								@Override
								public void run() {
									ImageView img = (ImageView) holder.mHeadImg.findViewWithTag(chatListDataModel.mToId);
									img.setImageBitmap(bitmap);					
								}
							});
						}

						@Override
						public void failed() {
						}
					});
		} else {
			ImageView img = (ImageView) holder.mHeadImg.findViewWithTag(chatListDataModel.mToId );
			img.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_default_famale));
		}
		if (chatListDataModel.mSendState == Subject.COMMAND.COMMAND_DOWNLOAD_IMAGE_ERROR
				|| chatListDataModel.mSendState == Subject.COMMAND.COMMAND_MESSAGE_ERROR
				|| chatListDataModel.mSendState == Subject.COMMAND.COMMAND_UPLOAD_IMAGE_ERROR
				|| chatListDataModel.mSendState == Subject.COMMAND.COMMAND_UPLOAD_VOICE_ERROR
				|| chatListDataModel.mSendState == Subject.COMMAND.COMMAND_DOWNLOAD_VOICE_ERROR) {
			holder.mNotificationCount.setVisibility(View.GONE);
			holder.mFail.setVisibility(View.VISIBLE);
		} else {
			if (chatListDataModel.unReadCount == 0) {
				holder.mNotificationCount.setVisibility(View.GONE);
				holder.mFail.setVisibility(View.GONE);
			} else {
				holder.mNotificationCount.setVisibility(View.VISIBLE);
				holder.mFail.setVisibility(View.GONE);
				if (chatListDataModel.unReadCount > 99) {
					holder.mNotificationCount.setText("99+");
				} else {
					holder.mNotificationCount.setText(String.valueOf(chatListDataModel.unReadCount));
				}
			}
		}
		if (chatListDataModel.mChatContent != null) {
			holder.mContent.setText(new EmotionString(chatListDataModel.mChatContent));
		} else {
			holder.mContent.setText("");
		}
	}

	List<ChatMessageWarpper> mMessageCache = new ArrayList<ChatMessageWarpper>();
	InsertRunnable mInsertRunnable = new InsertRunnable();

	public class InsertRunnable implements Runnable {
		@Override
		public void run() {
			synchronized (mMessageCache) {
				if (mMessageCache.size() > 0) {
					onInsert(mMessageCache);
					mMessageCache.clear();
				}
			}
		}
	}

	@Override
	public void onInsert(ChatMessageWarpper message) {
		CommonUtil.log("lu","insert2");
		int row = ChatListHelper.getInstance().updateMessage(message, message.getDescribe());
		if (row <= 0) {
			String old = message.mMessageContent;
			message.mMessageContent = message.getDescribe();
			ChatListHelper.getInstance().insertToTheDatabase(message);
			message.mMessageContent = old;
		}
		synchronized (mMessageCache) {
			mMessageCache.add(message);
		}
		ThreadPool.obtain().removeCallbacks(mInsertRunnable);
		ThreadPool.obtain().executeMainThread(mInsertRunnable);
	}

	@Override
	public void onInsert(List<ChatMessageWarpper> message) {
		if (message.size() <= 0) {
			return;
		}
		Map<Long, ChatMessageWarpper> map = this.obtainMap();
		ChatMessageWarpper m = null;
		for (int i = message.size() - 1; i > -1; i--) {
			m = message.get(i);
			if (!map.containsKey(m.mToChatUserId)) {
				map.put(m.mToChatUserId, m);
			}
		}
		for (Map.Entry<Long, ChatMessageWarpper> entry : map.entrySet()) {
			ChatListDataModel chatListDataModel = new ChatSessionDataModelAdapter(entry.getValue());
			chatListManager.addMessage(chatListDataModel);

		}
		CommonUtil.log("lu","insert1");
		mHandler.sendEmptyMessage(ChatListView.REFRESH_LIST);
		this.recycle(map);
	}

	@Override
	public void onDelete(String columnName, long _id) {
		if (columnName.equals(ChatHistory_Column.TO_CHAT_ID)) {
			int count = getCount();
			ChatListDataModel chatSessionDataModel;
			for (int i = 0; i < count; i++) {
				chatSessionDataModel = (ChatListDataModel) arr.get(i);
				if (chatSessionDataModel.mToId == _id) {
					chatSessionDataModel.mChatContent = "";
					this.updateList(_id);

					updataSessionState(chatSessionDataModel);

					ChatNotificationManager.getInstance().removeNotificationByGroupId(chatSessionDataModel.mToId);
					break;
				}
			}
			RenrenChatApplication.getUiHandler().post(new Runnable() {
				@Override
				public void run() {
					notifyDataSetChanged();
				}
			});
		}
		if (columnName.equals(ChatHistory_Column._ID)) {
			int count = getCount();
			ChatListDataModel chatSessionDataModel;
			for (int i = 0; i < count; i++) {
				chatSessionDataModel = (ChatListDataModel) arr.get(i);
				if (chatSessionDataModel.mMessageId == _id) {
					Log.v("aa", "end delete");
					ChatMessageWarpper messageWarpper;
					messageWarpper = ChatDataHelper.getInstance()
							.queryLastMessageByToChatId(chatSessionDataModel.mToId);
					if (messageWarpper == null) {
						Log.v("aa", "last data");
						chatSessionDataModel.mChatContent = "";
						updataSessionState(chatSessionDataModel);
						this.notifyDataSetChanged();
						Log.v("fff", "ondelete1");
						return;
					}
					ChatListHelper.getInstance().updateMessage(messageWarpper, messageWarpper.getDescribe());
					chatSessionDataModel.mMessageId = messageWarpper.mMessageId;
					chatSessionDataModel.mChatContent = messageWarpper.getDescribe();
					chatSessionDataModel.mUserName = messageWarpper.mUserName;
					chatSessionDataModel.mId = messageWarpper.mMessageId;
					chatSessionDataModel.mType = messageWarpper.mMessageType;
					chatSessionDataModel.mComeFrom = messageWarpper.mComefrom;
					chatSessionDataModel.mSendState = messageWarpper.mMessageState;
					chatSessionDataModel.mLastTime = messageWarpper.mMessageReceiveTime;

					ChatSessionDataModelAdapter dataModel = new ChatSessionDataModelAdapter(messageWarpper);
					chatListManager.delMessage(dataModel, chatSessionDataModel);
					break;
				}
			}
		}
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onUpdate(String columnName, long _id, ContentValues value) {
		if (columnName.equals(ChatHistory_Column.TO_CHAT_ID)) {
			this.updateList(_id);
		}
		if (columnName.equals(ChatHistory_Column._ID)) {
			this.updataMessageState(_id, value.getAsInteger(ChatHistory_Column.MESSAGE_STATE));
			chatListManager.updateMessageState(_id, value.getAsInteger(ChatHistory_Column.MESSAGE_STATE));
			ChatListHelper.getInstance().updateMessageState(_id, value);
		}

	}

	public void updateList(long id) {
		int count = getCount();
		for (int i = 0; i < count; i++) {
			ChatListDataModel chatSessionDataModle = (ChatListDataModel) arr.get(i);
			if (chatSessionDataModle.mToId == id) {
				chatSessionDataModle.unReadCount = 0;
				break;
			}
		}
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	private void updataSessionState(ChatListDataModel chatSessionDataModel) {
		this.updataMessageState(chatSessionDataModel.mMessageId, Subject.COMMAND.COMMAND_MESSAGE_OVER);
		ContentValues value = new ContentValues(1);
		value.put(ChatList_Column.MESSAGE_STATE, Subject.COMMAND.COMMAND_MESSAGE_OVER);
		ChatListHelper.getInstance().updateMessageState(chatSessionDataModel.mMessageId, value);
	}

	/**
	 * 更新一条信息的发送状态（失败还是成功）
	 * */
	public void updataMessageState(long messageId, int state) {
		int count = getCount();
		ChatListDataModel chatSessionDataModel;
		for (int i = 0; i < count; i++) {
			chatSessionDataModel = (ChatListDataModel) arr.get(i);
			if (chatSessionDataModel.mId == messageId) {
				chatSessionDataModel.mSendState = state;
				RenrenChatApplication.getUiHandler().post(new Runnable() {
					public void run() {
						notifyDataSetChanged();
					}
				});
				break;
			}
		}
	}

	private Map<Long, ChatMessageWarpper> mMap = new LinkedHashMap<Long, ChatMessageWarpper>();

	public Map<Long, ChatMessageWarpper> obtainMap() {
		return this.mMap;
	}

	public void recycle(Map<Long, ChatMessageWarpper> map) {
		map.clear();
	}

	public void attachToDAO(ChatHistoryDAO dao) {
		this.mSubject = dao;
		this.mSubject.registorObserver(this);
	}

	public void removeAll() {
		arr.clear();
	}

	public void addLinkList(List<Object> data) {
		arr.addAll(data);
		notifyDataSetChanged();
	}
}

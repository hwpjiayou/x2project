package com.renren.mobile.x2.components.chat.message;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.RenRenChatActivity;
import com.renren.mobile.x2.components.chat.facade.ChatItemFacader;
import com.renren.mobile.x2.components.chat.facade.ChatItemFacaderFactory;
import com.renren.mobile.x2.components.chat.face.ICanTalkable;
import com.renren.mobile.x2.components.chat.face.IMessageOnClickListener;
import com.renren.mobile.x2.components.chat.holder.ChatItemHolder;
import com.renren.mobile.x2.components.chat.holder.ChatItemSelectHolder;
import com.renren.mobile.x2.components.chat.net.ChatMessageSender;
import com.renren.mobile.x2.components.chat.util.C_NetPackageBuilder;
import com.renren.mobile.x2.components.chat.util.ChatDataHelper;
import com.renren.mobile.x2.components.chat.util.ChatItemSelectPopupWindow;
import com.renren.mobile.x2.components.chat.util.DownLoadable;
import com.renren.mobile.x2.components.chat.util.ItemLongClickDialogProxy.LONGCLICK_COMMAND;
import com.renren.mobile.x2.components.chat.util.MessageCopyUtil;
import com.renren.mobile.x2.components.chat.util.Resendable;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.network.talk.MessageManager.OnSendTextListener;
import com.renren.mobile.x2.utils.log.Logger;



/**
 * @author dingwei.chen
 * @说明 消息模型的包装器
 * 			由于消息类型都要支持文本发送,所以要支持文本发送的回调
 * 			{@link com.renren.mobile.chat.base.inter.OnSendTextListener}
 * @回调说明 
 * @see #onAddToAdapter() 当该模型被添加到适配器的时候回调
 * @see #onDelete() 当消息被删除的时候回调(对于语音消息需要复写这个方法用来删除语音文件)
 * @see #onSendTextPrepare() 当消息准备发送的时候回调用来生成进度框
 * @see #onSendTextSuccess() 文本消息发送成功回调
 * @see #onSendTextError() 		文本消息发送失败回调
 * @see #warpView(ChatItemHolder, OnItemLongClickCallback, OnErrorCallback, boolean) 适配器适配控件的时候将调用该方法
 * 
 * */
public abstract  class ChatMessageWarpper 
			extends ChatBaseItem implements 
			Subject,//被观察者接口
			Resendable,//重发接口
			DownLoadable,//可下载接口
			OnSendTextListener
{
	
	
	public void resetData(){
		mIsShowTime = false;; 
		mIsShowHead = true;
		mIsSuccessInsert = true;
		mSendTextState = SEND_TEXT_STATE.SEND_OVER;
//		mNewsFeedMessage = null;
		mIsError = false;
		mIsOffline = false;
	}
	
	/*是否显示时间控件*/
	public boolean mIsShowTime = false;; 
	/*是否显示头像控件*/
	public boolean mIsShowHead = true;
	/*是否成功插入数据库*/
	public boolean mIsSuccessInsert = true;
	/*文本消息发送状态*/
	public int mSendTextState = SEND_TEXT_STATE.SEND_OVER;
//	/*携带新鲜事模型*/
//	public NewsFeedWarpper mNewsFeedMessage = null;
	
	public boolean mIsError = false;
	
	public boolean mIsOffline = false;
	
	public boolean mIsSyn = false;
	
	
	//通过轮询下发的XML数据来构建自身的模型在轮询数据解析的时候被调用
//	public abstract void parseFromXMLData(Node node,Node parentNode);
	
	public abstract void swapDataFromXML(Message message);
	
	//发送消息的时候需要拼接成为的协议串
	
	public List<XMPPNode>  getNetPackage(){
		return C_NetPackageBuilder.getInstance().build(this);
	}
	
	//发送文本准备(未进行网络请求)
	public void onSendTextPrepare(){
		if(Logger.mDebug){
			Logger.logd("准备开始发送");
		}
		this.mSendTextState = SEND_TEXT_STATE.SEND_PREPARE;
		observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_OVER,null);
		observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_LOADING, null);
	}
	
	//发送文本成功
	public void onSendTextSuccess(){
		if(Logger.mDebug){
			Logger.logd("发送成功");
		}
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
		observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_OVER,null);
		observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING, null);
		updateChatMessageState(Subject.COMMAND.COMMAND_MESSAGE_OVER,true);
	}
	
	//发送文本失败
	public void onSendTextError(){
		if(Logger.mDebug){
			Logger.errord("发送失败");
		}
		this.mSendTextState = SEND_TEXT_STATE.SEND_OVER;
		updateChatMessageState(Subject.COMMAND.COMMAND_MESSAGE_ERROR,true);
		observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_ERROR,null);
		observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING, null);
	}
	
	//处理界面,在Adapter生成界面的时候被调用
	public void facadeHolder(ChatItemHolder holder,final  IMessageOnClickListener iClick) {
		holder.mMessage_Content_LinearLayout.setOnClickListener(null);
		ChatItemFacader facader = ChatItemFacaderFactory.getInstance().createFacader(mMessageType);
		if(this.mSendTextState==SEND_TEXT_STATE.SEND_PREPARE){
				holder.mMessage_Loading.setVisibility(View.VISIBLE);
		}else{
				holder.mMessage_Loading.setVisibility(View.GONE);
		}
		facader.facadeMessage(holder, this, iClick);
		if(iClick!=null){
			holder.mMessage_Content_ViewGroup.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					iClick.onLongClick(ChatMessageWarpper.this);
					return false;
				}
			});
		}
	}
	
	
	//拷贝一份数据
	private ChatMessageWarpper copy(){
		return MessageCopyUtil.getInstance()
				.createCloneMessage(this);
	}
	
	
	
	//得到该对象的数据类型
	/**
	 * @see com.renren.mobile.chat.base.model.ChatBaseItem.MESSAGE_TYPE
	 * */
	public int getMessageType(){
		return this.mMessageType;
	}
	
	
	
	public String getMessageContent(){
		return this.mMessageContent;
	}
	//处理弹出的条目框
	public void processItemSelectHolder(final RenRenChatActivity chatActivity){
		final ChatItemSelectPopupWindow popupWindow = chatActivity.mChatItemSelectPopWindow;
		ChatItemSelectHolder holder = chatActivity.mChatItemSelectPopWindow.getHolder();
		holder.mDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatDataHelper.getInstance().deleteToTheDatabase(ChatMessageWarpper.this);
				popupWindow.dismiss();
			}
		});
		holder.mCopy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatDataHelper.getInstance()
							.copyTheMessage(chatActivity,
											ChatMessageWarpper.this.getMessageContent());
				popupWindow.dismiss();
			}
		});
		holder.mForward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChatMessageSender.getInstance().sendMessageToNet(ChatMessageWarpper.this.copy(),true);
				popupWindow.dismiss();
			}
		});
	}
	
	/**
	 * 界面相关
	 * */
	public void warpView(ChatItemHolder holder,
			IMessageOnClickListener click){
		this.processOnErrorItemCallback(holder,click);
		this.facadeHolder(holder,click);
	}
	
	protected void processOnItemLongClickCallback(ChatItemHolder holder,final IMessageOnClickListener click){
		if(click!=null){
			holder.mMessage_Content_LinearLayout.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					click.onLongClick(ChatMessageWarpper.this);
					return false;
				}
			});
		}
	}
	
	protected void processOnErrorItemCallback(ChatItemHolder holder,final IMessageOnClickListener click){
		if(click!=null&&holder.mMessage_Error_ImageView!=null){
			holder.mMessage_Error_ImageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					click.onReSendClick(ChatMessageWarpper.this);
				}
			});
		}
	}
	
	/**
	 * 数据库相关
	 * */
	//数据库解析(公共解析)
	public void parseMessageFromDatabase(Cursor cursor){
		ORMUtil.getInstance().ormQuery(getClass(), this, cursor);
	}
	
	//数据库插入数据对(公共数据)
	public ContentValues getValueToInsert(){
		ContentValues values = new ContentValues();
		ORMUtil.getInstance().ormInsert(getClass(), this, values);
		return values;
	}

	public Observer mObserver = null;//holder
	
	public boolean notifyUpdate(ContentValues value){
		return (ORMUtil.getInstance().ormUpdate(this.getClass(), this,value));
	}
	
	@Override
	public void registorObserver(Observer observer){
		this.mObserver = observer;
		observer.registorSubject(this);
	}
	
	@Override
	public void unregistorObserver(Observer observer){
		if(this.mObserver==observer){
			this.mObserver = null;
		}
	}
	public void unregistor(){
		if(this.mObserver!=null){
			mObserver.registorSubject(null);
			this.mObserver = null;
		}
	}
	
	
	public void copyToBaseField(ChatMessageWarpper message){
		Field[] fields = 
				ChatBaseItem.class.getDeclaredFields();
		for(Field f: fields){
			try {
				f.set(message, f.get(this));
			} catch (Exception e) {
			} 
		}
	}
	
	/**
	 * 添加到适配器时候回调
	 * */
	public void onAddToAdapter(){}
	
	/**
	 * 重发接口~对于特殊的图片和voice要覆盖该方法因为图片和语音有可能是因为上传失败
	 * */
	public void resend(){
		this.updateChatMessageState(Subject.COMMAND.COMMAND_MESSAGE_OVER,false);
		ChatMessageSender.getInstance()
				.sendMessageToNet(ChatMessageWarpper.this,false);
	}
	
	public void reflash(){
	}
	
	
	
	public static class OnLongClickCommandMapping{
		public String mText;
		public LONGCLICK_COMMAND mCommand;
		public OnLongClickCommandMapping(String text,LONGCLICK_COMMAND command){
			this.mText = text;
			this.mCommand = command;
		}
	}
	
	
	/*消息中注入用户的信息*/
	public void parseUserInfo(ICanTalkable user){
		this.mHeadUrl = user.getHeadUrl();
		this.mToChatUserId = user.getUId();
		this.mUserName = user.getName();
	//	this.mGroupId = user.getUId();
	//	this.mDomain = user.getDomain();
	}
	
	public boolean isReflash(){
		return false;
	}
	public void onDelete(){}
	
	/**更新数据库状态并通知 */
	public void updateChatMessageState(int state){
		ChatDataHelper.getInstance().updateMessageState(this, state, true);
	}
	/**更新数据库状态*/
	public void updateChatMessageState(int state,boolean isNotify){
		ChatDataHelper.getInstance().updateMessageState(this, state, isNotify);
	}
	public void observerUpdate(final int command,final Map<String,Object> data){
		RenrenChatApplication.getUiHandler().post(new Runnable() {
			public void run() {
				if(mObserver!=null){
					mObserver.update(command, data);
				}
			}
		});
	}
	public boolean hasNewsFeed(){
		return false;
	}
	public void setSendState(int sendState){
		int pre = this.mSendTextState;
		this.mSendTextState = sendState;
		if(pre!=sendState){
			switch (sendState) {
				case SEND_TEXT_STATE.SEND_PREPARE:
					this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_LOADING,null);
					break;
	
				case SEND_TEXT_STATE.SEND_OVER:
					this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING,null);
					break;
			}
		}
	}
	public boolean isError(){
		mIsError = false;
		this.onErrorCode();
		return mIsError;
	}
	public ChatMessageWarpper addErrorCode(int code){
		if(!mIsError){
			if(Logger.mDebug){
				Logger.logd("mMessageState="+mMessageState+"#code="+code);
			}
			if(this.mMessageState==code){
				this.mIsError = true;
			}
		}
		return this;
	}
//	public void setNewsFeedModel(NewsFeedWarpper model){
//		if(model!=null){
//			this.mFeedId = model.mFeedId;
//		}else{
//			this.mFeedId = -1L;
//		}
//		this.mNewsFeedMessage = model;
//	}
	public void showLoading(boolean isShow){
		if(isShow){
			this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_SHOW_LOADING, null);
		}else{
			this.observerUpdate(Subject.COMMAND.COMMAND_MESSAGE_SEND_HIDE_LOADING, null);
		}
	}
	public boolean isHideBackground(){
		return false;
	}
	
	public List<OnLongClickCommandMapping> getOnClickCommands() {
		return null;
	}

	public void onErrorCode() {}

	public String getDescribe() {
		return null;
	}
	public void download(boolean isForceDownload) {}
	
	public boolean isSyn(){
		return true;
	}

	//public void onSuccessRecive(NULL data){}
	
	public void onPreInsertDB(){}
	
	public long getFromId(){
		return this.mLocalUserId;
	}
	public long getToId(){
		return this.mToChatUserId;
	}
	
	public boolean isShowCover(){
		return false;
	}
	public void post(Runnable action){
		RenrenChatApplication.getUiHandler().removeCallbacks(action);
		RenrenChatApplication.getUiHandler().post(action);
	}
}

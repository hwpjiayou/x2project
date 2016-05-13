package com.renren.mobile.x2.components.chat.message;

import com.renren.mobile.x2.components.chat.util.StateMessageModel;

import java.util.List;



/**
 * @author dingwei.chen1988@gmail.com
 * @说明 轮询数据回调
 * @see #onRecive(List)   			       收到普通消息
 * @see #onRecive(StateMessageModel)   收到状态消息
 * */
public class C_PollDataCallbackImpl{
	public void onRecive(final List<ChatMessageWarpper> messages){
//		RenrenChatApplication.sHandler.post(new Runnable() {
//			public void run() {
//				ChatNotificationManager.getInstance()
//						.handleNewMessage(messages);
//			}
//		});
	}
	public void onRecive(StateMessageModel stateModel) {
		//RenrenChatApplication.onStateUpdateCallback(stateModel);
	}
//	@Override
//	public void onRecive(long[] feeds) {
//		ObservableImpl.getInstance().notifyObservers(feeds);
//	}
//	@Override
//	public void onRecive_create_room(RoomInfoModelWarpper roomInfoModel) {
//		RoomInfosData.getInstance().addRoomInfo(roomInfoModel);
//		
//	}
//	@Override
//	public void onRecive_invite_to_old_member(long roomId,NotifyInviteModel notifyInviteModel) {
//		RoomInfosData.getInstance().updateRoomInfoByNotifyInvite(roomId,notifyInviteModel);
//		
//	}
//	@Override
//	public void onRecive_invite_to_new_member(RoomInfoModelWarpper roomInfoModelWarpper) {
//		RoomInfosData.getInstance().addRoomInfo(roomInfoModelWarpper);
//		
//	}
//	@Override
//	public void onRecive_delete_to_member(long roomId) {
//		RoomInfosData.getInstance().updateRoomInfoByNotifyDeleted(roomId);
//		
//	}
//	@Override
//	public void onRecive_leave_room(long roomId,String subject,int version, long userId) {
//		RoomInfosData.getInstance().updateRoomInfoByNotifyMemberLeave(roomId,subject,version, userId);
//		
//	}
//	@Override
//	public void onRecive_destory_room(long roomId) {
//		RoomInfosData.getInstance().updateRoomInfoByNotifyRoomDestory(roomId);
//		
//	}
//	@Override
//	public void onRecive_delete_to_other(long roomId, NotifyDeleteModel notifyDeleteModel) {
//		RoomInfosData.getInstance().updateRoomInfoByNotifyDeleteMember(roomId, notifyDeleteModel);
//		
//	}
//	@Override
//	public void onRecive_update_subject(long roomId, int version, String subject) {
//		RoomInfosData.getInstance().updateRoomSubject(roomId, subject);
//		
//	}
	
	
}

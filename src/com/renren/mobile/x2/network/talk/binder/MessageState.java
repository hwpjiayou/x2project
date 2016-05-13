package com.renren.mobile.x2.network.talk.binder;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageState implements Parcelable {

	public long mKey;
	public int mState;
	public String mAttachMsg = null;
	public transient boolean mIsSyn = true;
	public int mErrorCode;

	public static interface SEND_STATE {
		static final int SEND_OVER = 0;
		static final int SEND_ERROR = 1;
	}

	public MessageState() {
	}

	public MessageState(long key, int errorCode, int state) {
		mKey = key;
		mState = state;
		mErrorCode = errorCode;
	}

	public MessageState(long key, int state, String attachMsg) {
		mKey = key;
		mState = state;
		this.mAttachMsg = attachMsg;
	}

	public MessageState(Parcel source) {
		mKey = source.readLong();
		mState = source.readInt();
		mAttachMsg = source.readString();
	}

	public static final Parcelable.Creator<MessageState> CREATOR = new Parcelable.Creator<MessageState>() {

		@Override
		public MessageState createFromParcel(Parcel source) {
			return new MessageState(source);
		}

		@Override
		public MessageState[] newArray(int size) {
			return null;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mKey);
		dest.writeInt(mState);
		dest.writeString(mAttachMsg);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean isSuccess = (this.mState == SEND_STATE.SEND_OVER);
		builder.append(mKey + " 消息发送 " + (isSuccess ? "成功" : "失败")
				+ (isSuccess ? "" : "原因是:" + this.mAttachMsg));
		return super.toString();
	}
}

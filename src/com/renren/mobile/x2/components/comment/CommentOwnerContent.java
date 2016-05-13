package com.renren.mobile.x2.components.comment;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentOwnerContent implements Serializable {

	private static final long serialVersionUID = 8322229812608467994L;

	protected String ownerName;

	protected long ownerId;

	protected long id;

	protected String status;

	public CommentOwnerContent(JSONObject objs) throws JSONException {
		ownerName = objs.getString(CommentModel.COMMENT_COLUMN_OWNER_USERNAME);
		ownerId = objs.getLong(CommentModel.COMMENT_COLUMN_OWNER_ID);
		id = objs.getLong(CommentModel.COMMENT_COLUMN_OWNER_ID);
//		status = objs.getString(CommentModel.);
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public long getId() {
		return id;
	}

	public void setId(long Id) {
		this.id = Id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

package com.renren.mobile.x2.components.comment;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author hwp
 * 本地位置信息model
 */
public class CommetLocationContent implements Serializable {

	private static final long serialVersionUID = -5059551926088477495L;

	private String placeName;

	private long id;

	private String placeId;

	private long longitude;

	private long latitude;

	public CommetLocationContent(JSONObject objs) throws JSONException {
		this.placeName = objs.getString(CommentModel.COMMENT_COLUMN_PLACE_NAME);
		this.id = objs.getLong(CommentModel.COMMENT_COLUMN_PLACE_ID);
		this.placeId = objs.getString(CommentModel.COMMENT_COLUMN_PLACE_PID);
		this.longitude = objs.getLong(CommentModel.COMMENT_COLUMN_PLACE_LONGITUDE);
		this.latitude = objs.getLong(CommentModel.COMMENT_COLUMN_PLACE_LATITUDE);
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	public long getLatitude() {
		return latitude;
	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

}

package com.renren.mobile.x2.components.home.feed;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedLocationContent implements Serializable {

	private static final long serialVersionUID = -5059551926088477495L;

	private String placeName;

	private long id;

	private String placeId;

	private long longitude;

	private long latitude;

	public FeedLocationContent(JSONObject objs) {
		try {
			this.placeName = objs.getString(FeedModel.FEED_COLUMN_PLACE_NAME);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.id = objs.getLong(FeedModel.FEED_COLUMN_PLACE_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.placeId = objs.getString(FeedModel.FEED_COLUMN_PLACE_PID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.longitude = objs.getLong(FeedModel.FEED_COLUMN_PLACE_LONGITUDE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.latitude = objs.getLong(FeedModel.FEED_COLUMN_PLACE_LATITUDE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

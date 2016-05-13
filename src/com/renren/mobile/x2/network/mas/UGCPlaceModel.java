package com.renren.mobile.x2.network.mas;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UGCPlaceModel extends UGCModel {

    public static final String[] names = RenrenChatApplication.getApplication().getResources().getStringArray(R.array.publisher_location_tye_names);
    private static final int[] iconResourceSelectorIds = { R.drawable.v1_publisher_location_meadow_selector, R.drawable.v1_publisher_location_teaching_building_selector,
            R.drawable.v1_publisher_location_mess_selector, R.drawable.v1_publisher_location_library_selector, R.drawable.v1_publisher_location_supermarket_selector,
            R.drawable.v1_publisher_location_swimming_pool_selector, R.drawable.v1_publisher_location_gymnaisum_selector, R.drawable.v1_publisher_location_actioncenter_selector,
            R.drawable.v1_publisher_location_other_selector};
    private static final int[] iconResourceSelectedIds = { R.drawable.v1_publisher_location_meadow_selected, R.drawable.v1_publisher_location_teaching_building_selected,
            R.drawable.v1_publisher_location_mess_selected, R.drawable.v1_publisher_location_library_selected, R.drawable.v1_publisher_location_supermarket_selected,
            R.drawable.v1_publisher_location_swimming_pool_selected, R.drawable.v1_publisher_location_gymnasium_selected, R.drawable.v1_publisher_location_actioncenter_selected,
            R.drawable.v1_publisher_location_other_selected};
    private static final int[] iconResourceIds = { R.drawable.v1_publisher_location_meadow_unselect, R.drawable.v1_publisher_location_teaching_building_unselect,
            R.drawable.v1_publisher_location_mess_unselect, R.drawable.v1_publisher_location_library_unselect, R.drawable.v1_publisher_location_supermarket_unselect,
            R.drawable.v1_publisher_location_swimming_pool_unselect, R.drawable.v1_publisher_location_gymnasium_unselect, R.drawable.v1_publisher_location_actioncenter_unselect,
            R.drawable.v1_publisher_location_other_unselect};
	
	private static final long serialVersionUID = -865207453291214572L;
	public String mName;
	public double mLatitude;
	public double mLongitude;
    public String mLatLon;
    public int mIconId;
    public int mIconSelector;
    public int mIconSelected;
    public boolean mIsSelected;
	
	public UGCPlaceModel(JSONObject object) {
		super(object);
	}
	
	public UGCPlaceModel(String name, String latlon) {
		this.mType = UGCModel.UGCType.PLACE;
		this.mName = name;
		this.mLatLon = latlon;
	}

    public UGCPlaceModel(String name, String latlon, int iconId, int iconSelected, int iconSelector) {
        this.mType = UGCModel.UGCType.PLACE;
        this.mName = name;
        this.mIconId = iconId;
        this.mIconSelector = iconSelector;
        this.mIconSelected = iconSelected;
        this.mLatLon = latlon;
    }

    public static ArrayList<UGCPlaceModel> getPlaceModelList() {
        ArrayList<UGCPlaceModel> result = new ArrayList<UGCPlaceModel>();
        for (int i = 0; i < names.length ; i++) {
            UGCPlaceModel placeModel = new UGCPlaceModel(names[i], "", iconResourceIds[i], iconResourceSelectedIds[i], iconResourceSelectorIds[i]);
            result.add(placeModel);
        }
        return result;
    }
	
	@Override
	public UGCPlaceModel parse(JSONObject object) {
		if (object == null) return this;
		mType = object.optString("type");
		JSONObject content = object.optJSONObject("content");
		if (content != null) {
			mName = content.optString("name");
			mLatitude = content.optDouble("latitude");
			mLongitude = content.optDouble("longitude");
			mLatLon  = content.optString("data");
		}
		return this;
	}

	@Override
	public JSONObject build() {
		JSONObject object = new JSONObject();
		try {	
			object.put("type",mType);
			JSONObject content = new JSONObject();
			content.put("name", mName);
			content.put("latitude",mLatitude);
			content.put("longitude",mLongitude);
			content.put("data", mLatLon);
			object.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}

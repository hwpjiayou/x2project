package com.renren.mobile.x2.db.table;


import com.renren.mobile.x2.core.db.Column;
import com.renren.mobile.x2.core.db.DatabaseColumn;
import com.renren.mobile.x2.core.db.DatabaseTypeConstant;

public interface NearByfColumn extends DatabaseColumn{
	@Column(defineType=DatabaseTypeConstant.INT+" "+DatabaseTypeConstant.PRIMARY)
	public static final String _ID = "_id";
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String IMAGE_URL = "image_url";	
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String USER_NAME = "user_name";
	
	@Column(defineType=DatabaseTypeConstant.LONG)
	public final String USER_ID = "user_id";

	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String USER_STATUS = "user_status";
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String USER_NAME_HEADCHAR = "user_name_headchar";
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String USER_GENDER = "user_gender";
}

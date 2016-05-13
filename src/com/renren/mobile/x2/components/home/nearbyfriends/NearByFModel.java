package com.renren.mobile.x2.components.home.nearbyfriends;

import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.db.table.NearByfColumn;
/***
 * 数据
 * @author 
 *
 */
public class NearByFModel {
	public NearByFModel() {
		// TODO Auto-generated constructor stub
	}
	@ORM(mappingColumn = NearByfColumn.IMAGE_URL)
	public String url="";
	
	@ORM(mappingColumn = NearByfColumn.USER_ID)
	public long userid = 0;
	
	@ORM(mappingColumn = NearByfColumn.USER_NAME)
	public String username ="";

	@ORM(mappingColumn = NearByfColumn.USER_STATUS)
	public String userstatus = "";
		
	@ORM(mappingColumn = NearByfColumn.USER_NAME_HEADCHAR)
	public String username_headchar;

	@ORM(mappingColumn = NearByfColumn.USER_GENDER)
	public int user_gender;
	
}

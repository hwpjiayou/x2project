package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.Column;
import com.renren.mobile.x2.core.db.DatabaseColumn;
import com.renren.mobile.x2.core.db.DatabaseTypeConstant;

public interface ImageColumn extends DatabaseColumn{
	
	@Column(defineType=DatabaseTypeConstant.INT+" "+DatabaseTypeConstant.PRIMARY)
	public static final String _ID = "_id";
	
	@Column(defineType=DatabaseTypeConstant.TEXT +" " + DatabaseTypeConstant.UNIQUE)
	public final String IMAGE_URL = "image_url";	//图片网络路径
	
	@Column(defineType=DatabaseTypeConstant.TEXT)
	public final String IMAGE_PATH = "image_path";	//图片本地路径
	
	@Column(defineType=DatabaseTypeConstant.LONG)
	public final String IMAGE_CREATE_TIME = "image_create_time";	//图片创建时间
	
	@Column(defineType=DatabaseTypeConstant.LONG)
	public final String IMAGE_MODIFIED_TIME = "image_modified_time";	//图片修改时间
		
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String IMAGE_WIDTH = "image_width";	//图片宽度
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String IMAGE_HEIGHT = "image_height";	//图片高度
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String IMAGE_SIZE = "image_filesize";	//图片大小
	
	@Column(defineType=DatabaseTypeConstant.INT)
	public final String IMAGE_COUNTER = "image_counter";	//图片实例化次数
}

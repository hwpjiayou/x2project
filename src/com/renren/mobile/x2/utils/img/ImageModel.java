package com.renren.mobile.x2.utils.img;

import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.db.table.ImageColumn;

public class ImageModel {

	public ImageModel() {
	}

	@ORM(mappingColumn = ImageColumn.IMAGE_URL)
	public String url;
	
	@ORM(mappingColumn = ImageColumn.IMAGE_PATH)
	public String path;
	
	@ORM(mappingColumn = ImageColumn.IMAGE_WIDTH)
	public int width;
	
	@ORM(mappingColumn = ImageColumn.IMAGE_HEIGHT)
	public int height;
	
	@ORM(mappingColumn = ImageColumn.IMAGE_SIZE)
	public int fileSize;
	
	@ORM(mappingColumn = ImageColumn.IMAGE_COUNTER)
	public int counter;
	
	@ORM(mappingColumn = ImageColumn.IMAGE_CREATE_TIME)
	public long createTime;
	
	@ORM(mappingColumn = ImageColumn.IMAGE_MODIFIED_TIME)
	public long modifiedTime;
}

package com.renren.mobile.x2.components.publisher;

import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.db.table.LocationNameColumn;

/**
 * LocationNameItem
 * @author  xiaoguang.zhang
 * Date: 12-11-13
 * Time: 上午11:07
 * 数据库查出的LocationName的对象
 */
public class LocationNameItem {

    @ORM(mappingColumn= LocationNameColumn._ID,isInsert=false)
    public int locationNameId;

    @ORM(mappingColumn= LocationNameColumn.CUSTOM_NAME)
    public String customName;

    @ORM(mappingColumn= LocationNameColumn.LOCATION_TYPE_NAME)
    public String locationTypeName;
}

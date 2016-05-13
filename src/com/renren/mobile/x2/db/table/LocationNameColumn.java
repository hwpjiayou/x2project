package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.Column;
import com.renren.mobile.x2.core.db.DatabaseTypeConstant;

/**
 * LocationNameColumn
 * @author  xiaoguang.zhang
 * Date: 12-11-13
 * Time: 上午10:43
 * 位置表的列名
 */
public interface LocationNameColumn {
    @Column(defineType= DatabaseTypeConstant.INT+" "+DatabaseTypeConstant.PRIMARY)
    public static final String _ID = "_id";

    @Column(defineType=DatabaseTypeConstant.TEXT)
    public final String CUSTOM_NAME = "custom_name";//自定义位置名称

    @Column(defineType=DatabaseTypeConstant.TEXT)
    public final String LOCATION_TYPE_NAME = "location_type_name";//位置类别的名称
}

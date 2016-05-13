package com.renren.mobile.x2.db.table;

import com.renren.mobile.x2.core.db.BaseDB;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.Table;
import com.renren.mobile.x2.db.dao.LocationNameDao;

/**
 * LocationNameTable
 * @author  xiaoguang.zahng
 * Date: 12-11-13
 * Time: 上午10:49
 * 存储位置信息的数据表
 */
@Table(
        TableName = LocationNameTable.TABLE_NAME, //表名
        TableColumns = LocationNameColumn.class,//表对应的列
        DAO = LocationNameDao.class
)
public class LocationNameTable extends BaseDBTable{

    public final static String TABLE_NAME = "location_name_table";

    public LocationNameTable(BaseDB database) {
        super(database);
    }
}

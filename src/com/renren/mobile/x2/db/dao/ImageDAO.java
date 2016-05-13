package com.renren.mobile.x2.db.dao;

import android.content.ContentValues;

import com.renren.mobile.x2.core.db.BaseDAO;
import com.renren.mobile.x2.core.db.BaseDBTable;
import com.renren.mobile.x2.core.db.DAO;
import com.renren.mobile.x2.core.orm.ORMUtil;
import com.renren.mobile.x2.db.sql.QueryImage;
import com.renren.mobile.x2.db.table.ImageColumn;
import com.renren.mobile.x2.utils.img.ImageModel;


/**
 * @author xingchen.li
 * */
public class ImageDAO extends BaseDAO {

	
	QueryImage mQueryImage = new QueryImage(this);
	
	public ImageDAO(BaseDBTable table) {
		super(table);
	}
	
	/**
	 * 通过网络url获取图片信息
	 * */
	public ImageModel queryByImageUrl(String url) {
		String whereStr = ImageColumn.IMAGE_URL + " = '" + url+"'";
		ImageModel imageModel = mQueryImage.query(null, whereStr, null, null, null, ImageModel.class);
		return imageModel;
	}
	
	/**
	 * 通过本地路径获取图片信息
	 * */
	public ImageModel queryByImagePath(String path) {
		String whereStr = ImageColumn.IMAGE_PATH + " = '" + path +"'";
		ImageModel imageModel = mQueryImage.query(null, whereStr, null, null, null, ImageModel.class);
		return imageModel;
	}
	
	/**
	 * 获取最大的图片信息
	 * */
	public ImageModel getTheMonsterImage() {
		ImageModel imageModel = mQueryImage.query(null, null, null, ImageColumn.IMAGE_SIZE +" " + DAO.ORDER.DESC, "1", ImageModel.class);
		return imageModel;
	}
	
	/**
	 * 获取最小的图片信息
	 * */
	public ImageModel getThePygmyImage() {
		ImageModel imageModel = mQueryImage.query(null, null, null, ImageColumn.IMAGE_SIZE +" " + DAO.ORDER.ASC, "1", ImageModel.class);
		return imageModel;
	}
	
	/**
	 * 存储一张图片信息到数据库
	 * */
	public synchronized void saveAnImage(ImageModel model) {
		ContentValues values = new ContentValues();
		ORMUtil.getInstance().ormInsert(ImageModel.class, model, values);
		mInsert.insert(values);
	}
	
	/**
	 * 根据图片的本地路径 ,对counter字段增加1
	 * */
	public void updateImageCounterByPath(String path) {
		ImageModel model = queryByImagePath(path);
		if (model != null) {
			ContentValues values = new ContentValues();
			values.put(ImageColumn.IMAGE_COUNTER, ++ model.counter);
			String whereStr = ImageColumn.IMAGE_PATH +" = '" + path +"'";
			mUpdate.update(values, whereStr);
		}
	}
	
	/**
	 * 根据图片的网络地址 ,对counter字段增加1
	 * */
	public void updateImageCounterByUrl(String url) {
		ImageModel model = queryByImageUrl(url);
		if(model != null) {
			ContentValues values = new ContentValues();		
			values.put(ImageColumn.IMAGE_COUNTER, ++ model.counter);
			String whereStr = ImageColumn.IMAGE_URL +" = '" + url +"'";
			mUpdate.update(values, whereStr);
		}		
	}
	
	/**
	 * 根据图片的网络地址 ,删除图片信息
	 * */
	public void deleteByImageUrl(String url) {
		String whereStr = ImageColumn.IMAGE_URL + " = '" + url + "'";
		delete2(whereStr);
	}
	
	/**
	 * 根据图片的本地地址 ,删除图片信息
	 * */
	public void deleteByImagePath(String path) {
		String whereStr = ImageColumn.IMAGE_PATH + " = '" + path + "'";
		delete2(whereStr);
	}
	
	/**
	 * 根据图片的网络地址修改ImageColumn.IMAGE_MODIFIED_TIME
	 * */
	public void setModifiedTimeByUrl(String url, long time) {
		ImageModel model = queryByImageUrl(url);
		if(model != null) {
			ContentValues values = new ContentValues();		
			values.put(ImageColumn.IMAGE_MODIFIED_TIME, time);
			String whereStr = ImageColumn.IMAGE_URL +" = '" + url +"'";
			mUpdate.update(values, whereStr);
		}	
	}
	
	/**
	 * 根据图片的本地路径修改ImageColumn.IMAGE_MODIFIED_TIME
	 * */
	public void setModifiedTimeByPath(String path, long time) {
		ImageModel model = queryByImagePath(path);
		if (model != null) {
			ContentValues values = new ContentValues();		
			values.put(ImageColumn.IMAGE_MODIFIED_TIME, time);
			String whereStr = ImageColumn.IMAGE_URL +" = '" + path +"'";
			mUpdate.update(values, whereStr);
		}
	}
	/**
	 * 清空图片数据库 
	 * */
	public void clear() {
		delete2(null);
	}
}

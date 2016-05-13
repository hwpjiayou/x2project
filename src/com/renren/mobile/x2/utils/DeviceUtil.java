package com.renren.mobile.x2.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;

/**
 * @author dingwei.chen
 * @说明 设备工具
 * 		用来鉴别SD卡是否挂载,
 * 		sd卡容量是否低于限制
 * 		限制通过{@link com.commom.tuils.DeviceUtil#MIN_SDCARD_SPACE} 单位为M
 * */
public final class DeviceUtil {

	private static DeviceUtil sInstance = new DeviceUtil();
	private static final int MB_OFFSET = 20;//byte to M单位便宜
	private static final int MIN_SDCARD_SPACE = 10;//10M
	
	private DeviceUtil(){}
	
	public static DeviceUtil getInstance(){
		return sInstance;
	}
	
	/**
	 * 查看sdcard是否被挂载
	 * @return true 挂载成功
	 * */
	public boolean isMountSDCard(){
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	/**
	 * 查看sdcard容量是否低于最小值
	 * @return true:表示SD卡有足够的空间
	 * */
	public boolean isSDCardHasEnoughSpace(){
		if(isMountSDCard()){
			String sdcard = Environment.getExternalStorageDirectory().getPath();
			StatFs statFs = new StatFs(sdcard);
			long blockSize = statFs.getBlockSize();
			long blocks = statFs.getAvailableBlocks();
			long availableSpare = (blocks * blockSize) >>MB_OFFSET;//字节转化为MB,移位运算是为了提高效率
			if (MIN_SDCARD_SPACE > availableSpare) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	public boolean isSDcardEnable(){
		return isSDCardHasEnoughSpace();
	}
	
	/**
	 * 得到本地的MAC号
	 * */
	public String getLocalMacAddress(Context context) {
		String mac = "000000";
		if (context != null) {
			WifiManager wifi = SystemService.sWifiManager;
			if (wifi != null) {
				WifiInfo info = wifi.getConnectionInfo();
				if (info != null) {
					mac = info.getMacAddress();
				}
			}
		}
		return mac;
	}
	
	
	public void toastNotMountSDCard(){
		CommonUtil.toast("存储卡被移除，私信的图片、语音功能暂时不可使用。");
	}
	public void toastNotEnoughSpace(){
		CommonUtil.toast("存储卡空间过小，请手动清理。");
	}
	
	
	
}

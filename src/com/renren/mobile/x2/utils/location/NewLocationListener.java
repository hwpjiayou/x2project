/**
 * 
 */
package com.renren.mobile.x2.utils.location;

import android.location.Location;

/**
 * @author xiaochao.zheng
 *
 */
public interface NewLocationListener {
	public void onLocationSuccess(Location location);
	public void onLocationFailed(int errcode);
}

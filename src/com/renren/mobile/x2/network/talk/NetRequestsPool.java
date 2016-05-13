package com.renren.mobile.x2.network.talk;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.RemoteException;
import android.text.TextUtils;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.network.talk.binder.LocalBinder;
import com.renren.mobile.x2.network.talk.binder.PollBinder;
import com.renren.mobile.x2.network.talk.messagecenter.ConnectionManager;
import com.renren.mobile.x2.network.talk.messagecenter.base.Connection;
import com.renren.mobile.x2.utils.SystemService;

/**
 * @author dingwei.chen1988@gmail.com
 * @说明 网络请求池 (超时监控)
 * */
public final class NetRequestsPool{

	private static NetRequestsPool sPool = new NetRequestsPool();
	public static NetRequestsPool getInstance(){return sPool;}
	
	private NetRequestsPool(){} 
	
	private List<NetRequestCheckTimeoutWarpper> mRequestTimeoutCheckQueue = new LinkedList<NetRequestCheckTimeoutWarpper>();
	private WatchTimeOutThread mCheckTimeOutThread = new WatchTimeOutThread();
	protected Map<Long,NetRequestListener> mRequestPool = new HashMap<Long,NetRequestListener>();
	
	/*添加一个网络请求*/
	public void sendNetRequest(NetRequestListener request){
        NetworkInfo mobile = SystemService.sConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = SystemService.sConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        int networkInfo = ConnectionManager.getNetworkType(mobile, wifi, RenrenChatApplication.getApplication());
        if(networkInfo == Connection.NONETWORK){
            request.onNetError("没有网络");
            return;
        }

		if(TextUtils.isEmpty(request.getId())){
			request.setKey(System.currentTimeMillis());
		}
		synchronized (mRequestTimeoutCheckQueue) {
			if(mCheckTimeOutThread.getState()==State.NEW){
				mCheckTimeOutThread.start();
			}
			mRequestTimeoutCheckQueue.add(new NetRequestCheckTimeoutWarpper(request));
			mRequestTimeoutCheckQueue.notify();
		}
		synchronized (mRequestPool) {
			mRequestPool.put(request.getKey(), request);
		}
		if (LocalBinder.getInstance().isContainBinder()) {
			PollBinder binder = LocalBinder.getInstance().obtainBinder();
			try {
				binder.send(request.getKey(), request.getSendNetMessage());
			} catch (RemoteException e) {}
		}
	}

	protected void notifyTimeOutError(long key) {
        onError(key, "网络超时");
    }

	private static class NetRequestCheckTimeoutWarpper{
		public NetRequestListener mNetRequest = null;
		public long mTimeOutTime = -1L;
		public NetRequestCheckTimeoutWarpper(NetRequestListener request){
			mNetRequest = request;
			mTimeOutTime = request.getNetTimeOutTime();
		}
	}

	/*超时检测线程*/
	private class WatchTimeOutThread extends Thread{
		private byte[] LOCK = new byte[0];
		private NetRequestCheckTimeoutWarpper mCurrentRequest = null;
		public void removeKey(long key){
			synchronized (mRequestTimeoutCheckQueue) {
				if(mCurrentRequest!=null&&mCurrentRequest.mNetRequest.getKey()==key){
					synchronized (LOCK) {
						LOCK.notify();
					}
					return;
				}
				NetRequestCheckTimeoutWarpper r = null;
				for(NetRequestCheckTimeoutWarpper w:mRequestTimeoutCheckQueue){
					if(w.mNetRequest.getKey()==key){
						r = w;
					}
				}
				if(r!=null){
					mRequestTimeoutCheckQueue.remove(r);
				}
			}
		}

		@Override
		public void run() {
			while(true){
				NetRequestCheckTimeoutWarpper request = null;
				synchronized (mRequestTimeoutCheckQueue) {
					if(mRequestTimeoutCheckQueue.size()>0){
						request = mRequestTimeoutCheckQueue.remove(0);
					}else{
						try {
							mRequestTimeoutCheckQueue.wait();
						} catch (InterruptedException e){}
					}
				}
				if(request!=null){
					synchronized (LOCK) {
						mCurrentRequest = request;
						long wait_time = request.mTimeOutTime;
						if(wait_time > 0){
							try {
								LOCK.wait(wait_time);
							} catch (InterruptedException e) {}
						}
						notifyTimeOutError(request.mNetRequest.getKey());
//						onError(request.mNetRequest.getKey(), "网络超时");
					}
				}
			}
		}
	}

	public void onError(Long key, String msg){
		synchronized (mRequestPool) {
			NetRequestListener r = mRequestPool.get(key);
			if(r!=null){
				r.onNetError(msg);
			}
			mCheckTimeOutThread.removeKey(key);
			mRequestPool.remove(key);
		}
	}
	
	public void onSuccess(Long key){
		synchronized (mRequestPool) {
			NetRequestListener r = mRequestPool.get(key);
			if(r!=null){
				r.onNetSuccess();
            }
			mCheckTimeOutThread.removeKey(key);
			mRequestPool.remove(key);
		}
	}
	
}

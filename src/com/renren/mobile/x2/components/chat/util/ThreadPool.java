package com.renren.mobile.x2.components.chat.util;

import java.util.LinkedList;
import java.util.List;

import com.renren.mobile.x2.RenrenChatApplication;

/**
 * @author dingwei.chen
 * */
public final class ThreadPool implements Runnable{

	private List<Runnable> mCommands = new LinkedList<Runnable>();
	private Thread mCoreThread = new Thread(this);
	private boolean mIsStop = false;
	private ThreadPool(){
		mCoreThread.start();
	}
	private static ThreadPool sInstance = new ThreadPool();
	public static ThreadPool obtain(){
		return sInstance;
	}
	
	
	public void execute(Runnable runable){
		synchronized (mCommands) {
			mCommands.add(runable);
			mCommands.notify();
		}
	}
	public void executeMainThread(Runnable r){
		this.removeCallbacks(r);
		RenrenChatApplication.getUiHandler().post(r);
	}
	public void executeMainThread(Runnable r,long time){
		RenrenChatApplication.getUiHandler().postDelayed(r,time);
	}
	
	public void removeCallbacks(Runnable r){
		RenrenChatApplication.getUiHandler().removeCallbacks(r);
	}
	public void shutDown(){
		mCoreThread.interrupt();
	}
	@Override
	public void run() {
			while(!mIsStop){
				Runnable command = this.obtainCommandSyn();
				command.run();
			}
		
	}
	
	private Runnable obtainCommandSyn(){
		while(true){
			synchronized (mCommands) {
				Runnable currentCommand = null;
				if(mCommands.size()>0){
					currentCommand = mCommands.remove(0);
					return currentCommand;
				}else{
					try {
						mCommands.wait();
					} catch (InterruptedException e) {}
				}
			}
		}
	}
	
	
}

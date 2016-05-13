package com.renren.mobile.x2.components.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


/**
 * @author dingwei.chen
 * */
public class PluginGroup extends AbstractPluginGroup{

	private float mStartX = 0;
	private float mPreX = 0;
	private int mCurrentPage = 0;//0,1,2
	private int mOldPageSize = 0;
	private DataObserver mData = new DataObserver(){
		@Override
		public void dataChange() {
			onDataChange();
			postInvalidate();
		}
	};
	public PluginGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onDown(float x, float y) {
		mStartX = x;
		mPreX = mStartX;
	}

	@Override
	public void onMove(float x, float y) {
		float offsetX = mPreX - x;
		mPreX = x;
		this.scrollBy((int)offsetX, 0);
	}

	@Override
	public void onUp(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMovePrePage(float x) {
		// TODO Auto-generated method stub
		mIsBack = false;
		int offsetX = (int)((mCurrentPage)*mWidth-x);
		mScroller.startScroll(getScrollX(), 0, offsetX-mWidth, 0);
	}

	@Override
	public void onMoveNextPage(float x) {
		mIsBack = false;
		int offsetX = (int)((mCurrentPage+1)*mWidth-x);
		mScroller.startScroll(getScrollX(), 0, offsetX, 0);
	}

	@Override
	public void onMoveOver(boolean isNextPage) {
		if(!mIsBack){
			if(isNextPage){
				mCurrentPage++;
			}else{
				mCurrentPage--;
			}
			this.onPageChange();
			mIsBack = false;
		}
		
	}

	@Override
	public int onGetMinOffsetWidth() {
		// TODO Auto-generated method stub
		return getWidth()>>1;
	}
	private boolean mIsBack = false;
	@Override
	protected void onBackOldPosition(float x, DIR dir,float fingermove) {
		mIsBack = true;
		int offset = (int)((mCurrentPage+1)*mWidth-x)-mWidth;
		this.move((int)x, offset);
	}

	@Override
	protected int getCurrentPage() {
		return mCurrentPage;
	}

	@Override
	protected int getPageCount() {
		return this.getChildCount();
	}
	
	public abstract static class AbstractPluginAdapter {
		DataObserver mObserver = null;
		public abstract int getCount();
		public abstract View getView(Context context,int position);
		public void notifyDataChange(){
			if(mObserver!=null){
				mObserver.dataChange();
			}
		}
		public void setObserver(DataObserver observer){
			mObserver = observer;
		}
	}
	public static abstract class DataObserver{
		public abstract void dataChange();
	}
	AbstractPluginAdapter mAdapter;
	public void setAdapter(AbstractPluginAdapter adapter){
		if(adapter!=null){
			mAdapter = adapter;
			mAdapter.setObserver(mData);
			mAdapter.notifyDataChange();
		}
	}
	
	/**
	 * @param page 0-N
	 * */
	public void setPage(int page){
		if(page>=getPageCount()){
			page = getPageCount()-1;
		}
		if(page<0){
			page = 0;
		}
		if(page!=getCurrentPage()){
			int number = page-getCurrentPage();
			this.move(getScrollX(), number*mWidth);
			mCurrentPage = page;
			mIsBack = true;
			onPageChange();
		}
	}
	public int getPageSize(){
		if(mAdapter!=null){
			return ((mAdapter.getCount()-1)/PAGE_MAX_NUMBER)+1;
		}else{
			return 0;
		}
	}
	
	public void onDataChange(){
		this.recycle();// don't new PluginPager EveryTime;
		if(mAdapter!=null && mAdapter.getCount()>0){
			int count = this.mAdapter.getCount();
			int pageSize = ((count-1)/PAGE_MAX_NUMBER)+1;
			this.obtainPagers(pageSize);
			int pageIndex = 0;
			while(pageIndex<pageSize){
				PluginPager pager = mPagers.get(pageIndex);
				pager.removeAllViews();
				int offPage = pageIndex*PAGE_MAX_NUMBER;
				int index = offPage;
				while(index<count&&index<(offPage+PAGE_MAX_NUMBER)){
					pager.addView(mAdapter.getView(getContext(),index));
					index++;
				}
				pageIndex++;
			}
			if(this.mOldPageSize>pageSize){
				if(mCurrentPage==this.mOldPageSize-1){
					this.setPage(pageSize-1);
				}
			}
			if(this.mOldPageSize!=pageSize){
				onPageSizeChange();
			}
			this.mOldPageSize = pageSize;
		}
	}
	
	

}

package com.renren.mobile.x2.components.chat.view;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.renren.mobile.x2.R;


/**
 * @author dingwei.chen
 * */
public abstract class AbstractPluginGroup extends ViewGroup{

	protected Scroller mScroller;
	VelocityTracker mVelocityTracker = null;
	private static final float ABS_MIN_VELOCITY = 1000.0F;
	protected int mWidth = 0;
	PAGER_STATE mState = PAGER_STATE.STOP;
	boolean mIsNextPage = true;
	private float mStartX = 0;
	private float mOffsetX = 0;
	public static final int PAGE_MAX_NUMBER = 6;
	public static final int LINE_MAX_NUMBER = 3;
	
	
	protected List<PluginPager> mPagers = new LinkedList<PluginPager>();
	protected List<PluginPager> mRecyclePagers = new LinkedList<PluginPager>(); 
	private static final int DEFUALT_BACK_COLOR = 0XFFeaeaea;
//	private int mBottomHeight = 0;
	private static Bitmap sLineBitmap = null;
	private int mLineHeight = 0;
	private int mPadding = PluginPager.DEFAULT_PADDING;
	Rect mRect = new Rect();
	private static final Paint PAINT = new Paint();

	
	
	public AbstractPluginGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
		this.setOnClickListener(null);
		this.setBackgroundColor(DEFUALT_BACK_COLOR);
		sLineBitmap=PluginPager.getBitmap(context, sLineBitmap, R.drawable.test_plugin_linex);
		mLineHeight = sLineBitmap.getHeight();
	}
	public static enum DIR{
		LEFT,
		RIGHT
	}
	public static enum PAGER_STATE{
		MOVE,
		STOP
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
//		this.drawBottomLine(canvas);
	};
	private Rect obtainRect(int left, int top, int right, int bottom){
	     mRect.left = left;
	     mRect.top = top;
	     mRect.right = right;
	     mRect.bottom = bottom;
	     return mRect;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.measureChildren(widthMeasureSpec, heightMeasureSpec);
	};
	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		if(this.mState==PAGER_STATE.MOVE){
			this.mScroller.abortAnimation();
			this.postInvalidate();
			return true;
		}
		int currentPage = this.getCurrentPage();
		int countPage = this.getPageCount();
		
		
		if(mVelocityTracker==null){
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			if((event.getX()>=(this.getRight()-1))||(event.getX()<=(this.getLeft()+1))){
				//SystemUtil.log("draw", "outside ");
				event.setAction(MotionEvent.ACTION_UP);
			}
		}
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				//SystemUtil.log("draw", "down");
				this.onDown(event.getX(),event.getY());
				this.mStartX = event.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				//SystemUtil.log("draw", "move "+event.getEdgeFlags());
				if(Math.abs(event.getX()-mStartX)>20){
					this.onMove(event.getX(),event.getY());
				}
				break;
			case MotionEvent.ACTION_UP:
				//SystemUtil.log("draw", "up");
				mVelocityTracker.computeCurrentVelocity(1000);
				float vX  = mVelocityTracker.getXVelocity();
				DIR vDirect = vX>0?DIR.RIGHT:DIR.LEFT;
				this.onUp(event.getX(),event.getY());
				this.mOffsetX = event.getX() - mStartX;
				if(Math.abs(vX)>ABS_MIN_VELOCITY){
					switch(vDirect){
						case  LEFT:
							if(currentPage+1<countPage){
								this.mIsNextPage = true;
								this.onMoveNextPage(this.getScrollX());
							}else{
								this.onBackOldPosition(this.getScrollX(), DIR.LEFT, mOffsetX);
							}
							break;
						case RIGHT:
							if(currentPage>0){
								this.mIsNextPage = false;
								this.onMovePrePage(this.getScrollX());
							}else{
								this.onBackOldPosition(this.getScrollX(), DIR.LEFT, mOffsetX);
							}
							break;
					}
					mState = PAGER_STATE.MOVE;
					this.postInvalidate();
				}else{
					DIR offsetDir = mOffsetX>0?DIR.RIGHT:DIR.LEFT;
					int min = this.onGetMinOffsetWidth();
					if(Math.abs(mOffsetX)>=min){
						switch(offsetDir){
						case  LEFT:
							if(currentPage+1<countPage){
								this.mIsNextPage = true;
								this.onMoveNextPage(this.getScrollX());
							}else{
								this.onBackOldPosition(this.getScrollX(), DIR.LEFT, mOffsetX);
							}
							break;
						case RIGHT:
							if(currentPage>0){
								this.mIsNextPage = false;
								this.onMovePrePage(this.getScrollX());
							}else{
								this.onBackOldPosition(this.getScrollX(), DIR.LEFT, mOffsetX);
							}
							break;
						}
						mState = PAGER_STATE.MOVE;
						this.postInvalidate();
					}else{
						//return old state
						this.onBackOldPosition(getScrollX(), offsetDir,mOffsetX);
					}
				}
				this.onEndDrag();
				break;
		}
		super.onTouchEvent(event);
		event.setAction(MotionEvent.ACTION_CANCEL);
		return true;
	};
	
	/**
	 * @param dir :offset direct
	 * */
	protected abstract void onBackOldPosition(float x,DIR dir,float fingerMoveOffset);
	
	protected void move(int start,int offset){
		this.mScroller.startScroll(start, 0, offset, 0);
		this.mState = PAGER_STATE.MOVE;
		this.postInvalidate();
	}
	
	
	protected abstract void onDown(float x,float y);
	protected abstract void onMove(float x,float y);
	protected abstract void onUp(float x,float y);
	protected abstract void onMovePrePage(float x);
	protected abstract void onMoveNextPage(float x);
	protected abstract void onMoveOver(boolean isNextPage);
	protected abstract int  onGetMinOffsetWidth();
	protected abstract int  getCurrentPage();
	protected abstract int  getPageCount();
	
	
	@Override
	public void computeScroll() {
		if(mState == PAGER_STATE.STOP){
			return;
		}
		if(!mScroller.isFinished()&& mScroller.computeScrollOffset()){
			this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			this.postInvalidate();
		}else{
			mScroller.abortAnimation();
			this.scrollTo(mScroller.getFinalX(), mScroller.getFinalY());
			if(mState == PAGER_STATE.MOVE){
				this.onMoveOver(mIsNextPage);
				mState = PAGER_STATE.STOP;
				this.postInvalidate();
			}
		}
	};
	
	
	protected void onEndDrag(){
		if(this.mVelocityTracker!=null){
			this.mVelocityTracker.clear();
			this.mVelocityTracker.recycle();
			this.mVelocityTracker = null;
		}
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//SystemUtil.log("contain", "t="+t);
		int count = this.getChildCount();
		int index = 0;
		int pagerWidth = this.getWidth();
		mWidth = pagerWidth;
		int offsetHeight = mLineHeight;
		while(index<count){
			View v = this.getChildAt(index);
			l = index*pagerWidth;
			v.layout(l, 0, l+pagerWidth, getHeight()-offsetHeight);
			index++;
		}
	}
	
	
	
	private View mTarget = null;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int x = (int)ev.getX();
		int y = (int)ev.getY();
		if(ev.getAction()==MotionEvent.ACTION_UP){
			if(mTarget!=null){
				Rect r = new Rect();
				mTarget.getHitRect(r);
				if(r.contains(x, y)){
					mTarget.dispatchTouchEvent(ev);
				}else{
					int oldAction = ev.getAction();
					ev.setAction(MotionEvent.ACTION_CANCEL);
					mTarget.dispatchTouchEvent(ev);
					ev.setAction(oldAction);
				}
				mTarget = null;
			}
		}else if(ev.getAction()==MotionEvent.ACTION_DOWN){
			//SystemUtil.log("cdw", "child count = "+this.getChildCount()+","+getCurrentPage());
			ViewGroup group = (ViewGroup)this.getChildAt(getCurrentPage());
			int count = group.getChildCount();
			int index = 0;
			Rect r = new Rect();
			while(index<count){
				mTarget = group.getChildAt(index);
				mTarget.getHitRect(r);
				if(r.contains(x, y)){
					mTarget.dispatchTouchEvent(ev);
					break;
				}
				index++;
			}
			if(index==count){
				mTarget = null;
			}
		}
		this.onTouchEvent(ev);
		return true;
	}
	
	@Override
	public void addView(View child) {
		if(!(child instanceof ViewGroup)){
			throw new RuntimeException("add view must be subclass of ViewGroup");
		}
		super.addView(child);
	}
	
	public void addPager(PluginPager pager){
		this.addView(pager);
		this.mPagers.add(pager);
	}
	
	protected void recycle(){
		this.removeAllViews();
		this.mRecyclePagers.addAll(this.mPagers);
		for(PluginPager p:this.mPagers){
			p.removeAllViews();
			p.layout(0, 0, 0, 0);
		}
		this.mPagers.clear();
	}
	
	protected void obtainPagers(int size){
		int k = 0;
		while(k<size && mRecyclePagers.size()>0){
			addPager(mRecyclePagers.remove(0));
			k++;
		} 
		if(k!=size){
			while(k<size){
				addPager(new PluginPager(getContext()));
				k++;
			}
		}
		mRecyclePagers.clear();
	}
	OnPagerListenner mPagerListener = null;
	public void setOnPagerListenner(OnPagerListenner listener){
		mPagerListener = listener;
	}
	public static interface OnPagerListenner{
		public void onPageChange(int page);
		public void onPageSizeChange(int pageSize);
	}
	
	protected void onPageSizeChange(){
		if(mPagerListener!=null){
			mPagerListener.onPageSizeChange(getPageCount());
		}
	}
	protected void onPageChange(){
		if(mPagerListener!=null){
			mPagerListener.onPageChange(getCurrentPage());
		}
	}
	
	
	
}

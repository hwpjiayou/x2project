package com.renren.mobile.x2.components.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.util.ScrollingLock;
import com.renren.mobile.x2.components.chat.view.InnerListView.OnPositionListener;
import com.renren.mobile.x2.utils.CommonUtil;


/**
 * @author dingwei.chen
 * */
public class ListViewWarpper extends LinearLayout implements OnScrollListener,OnPositionListener{

	LayoutInflater mInflater = null;
	InputMethodManager mImm = null;
	private InnerListView mListView = null;
	private View mHeader =null;
	private View mHeaderView = null;
	//private View mSynButton = null;
	private Scroller mScroller;
	private int mHeaderHeight = 0;
	private boolean  mIsLoadingData = false;
	public ListViewWarpper(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.chat_listview_warpper, this);
		mListView = (InnerListView)this.findViewById(R.id.inner_listview);
		mHeader = this.findViewById(R.id.innter_header);
		mListView.setOnScrollListener(this);
		mListView.setScrollingCacheEnabled(false);
		mListView.setDivider(null);
		mListView.setDrawingCacheEnabled(false);
		mListView.setFadingEdgeLength(0);
		mListView.setVerticalFadingEdgeEnabled(false);
		mListView.setOnPositionListener(this);
		mScroller = new Scroller(context);
		mImm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		mHeaderView = mInflater.inflate(R.layout.chat_listview_head, null);
		//mSynButton = mHeaderView.findViewById(R.id.syn_btn);
		this.setOrientation(VERTICAL);
	}
//	public void setHeaderOnClickListener(OnClickListener listener){
//		mSynButton.setOnClickListener(listener);
//	}
	
	
	protected void hideKeyBoard(){
		mImm.hideSoftInputFromWindow(this.getWindowToken(),0);
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		CommonUtil.log("layout", "layout :"+t+","+b+","+this.getMeasuredHeight());
		mListView.layout(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
		mHeaderHeight = mHeader.getMeasuredHeight();
		mHeader.layout(getLeft(), this.getTop()-mHeaderHeight, getRight(), this.getTop());
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

	};
	
	
	
	public void resetLayout(){
		this.onLayout(true, getLeft(), getTop(), getRight(), getBottom());
	}
	
	
	boolean isLoadData(){
		if(mListener!=null){
			return mListener.onIsLoadData();
		}
		return false;
	}

	
	boolean flag = false;

	public void setAdapter(BaseAdapter adapter){
		mListView.setAdapter(adapter);
		f = false;
	}

	boolean f = false;
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {
		case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
			if(!this.istoBottom()&&!this.istoTop()){
				ScrollingLock.lock();
			}else{
				ScrollingLock.unlock();
			}
			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
			ScrollingLock.unlock();
			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			ScrollingLock.lock();
			break;
		default:
			break;
		}
		f = true;
	}
	int mOldHeight = 0;
	private int mState = STATE_STOP;
	private static final int STATE_MOVE = 0;
	private static final int STATE_STOP =1;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		CommonUtil.log("scroll", "scroll start1 ");
		if(!mIsEnable){
			return;
		}
		CommonUtil.log("scroll", "scroll start2 ");
		if(view.getChildCount()>0){
			boolean isTop = view.getChildAt(0).getTop()==this.getTop();
			flag = (firstVisibleItem==0)&&isTop;
			CommonUtil.log("scroll", "flag = "+flag+","+f+","+isLoadData());
			if(flag&&f&&isLoadData()){
				mState = STATE_MOVE;
				onLoadDataStart();
				f = false;
			}else{
				if(!isLoadData()){
					this.addHeader();
				}
			}
		}
	}
	private boolean mIsEnable = false;
	public void enable(){
		mIsEnable = true;
	}
	public void disable(){
		mIsEnable = false;
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
			if(this.mState == STATE_MOVE){
				CommonUtil.log("queuem", "is move");
				return true;
			}
			CommonUtil.log("queuem", "not move");
			if(ev.getAction()==MotionEvent.ACTION_DOWN){
				onFingerTouch();
			}
			this.hideKeyBoard();
			return  super.dispatchTouchEvent(ev);
	}
	
	public void moveBy(float y){
		this.scrollBy(0, (int)y);
		this.onMoveBy();
	}
	public void onMoveBy(){
		boolean flag = false;
		if(getOffsetScrollY()<mHeaderHeight){
			flag = false;
		}else{
			flag = true;
		}
		if(flag!=this.mIsLoadingData){
			mIsLoadingData = flag;
		}
	}
	private float getOffsetScrollY(){
		return this.getTop()-this.getScrollY();
	}
	
	
	public void smoothMove(float offsetY){
		this.smoothMove(offsetY, -1);
	}
	public void smoothMove(float offsetY,int time){
		CommonUtil.log("over", "smoothMove");
		mScroller.abortAnimation();
		if(time==-1){
			mScroller.startScroll(0, this.getScrollY(), 0, (int)offsetY);
		}else{
			mScroller.startScroll(0, this.getScrollY(), 0, (int)offsetY,time);
		}
		this.postInvalidate();
	}
	
	
	@Override
	public void computeScroll() {
		if(mScroller.computeScrollOffset()&& !mScroller.isFinished()){
			int curY = mScroller.getCurrY();
			if(mIsLoadingData){
				this.moveTo(curY);
				CommonUtil.log("over", "animation "+curY+","+mScroller.getFinalY());
				this.postInvalidate();
			}else{
				mScroller.abortAnimation();
			}
		}else {
			mScroller.abortAnimation();
			if(this.mListener!=null&&mIsLoadingData){
				this.mListener.onDataLoadStart();
				mIsLoadingData =false;
			}
		}
	};
	
	public void onDataChange(){
		f = false;
		if(mListener!=null){
			mListener.onDataChange();
		}
	}
	public static interface OnDataChangeListener{
		public void onDataChange();
		public void onDataLoadStart();
		public boolean onIsLoadData();
	}
	public OnDataChangeListener mListener = null;
	public void setOnDataChangeListener(OnDataChangeListener listener){
		this.mListener = listener;
	}
	
	
	protected void moveTo(float y){
		this.scrollTo(0, (int)y);
	}
	
	public void onLoadDataOver(){
		this.moveTo(getTop());
		mIsLoadingData = false;
		this.onDataChange();
		this.mState = STATE_STOP;
	}
	private boolean mIsNotifyDataChange = false;
	@Override
	public void onTop() {
//		mBeginDrag = true;
	}
	public void onLoadDataStart(){
		CommonUtil.log("over", "onloaddata start");
		this.smoothMove(-mHeaderHeight,500);
		mIsLoadingData = true;
	}
	int select  = 0;
	public void relayout(final int select){
		CommonUtil.log("relayout", "relayout "+select);
		this.select = select;
//		this.mListView.setSelectionFromTop(select,-mHeaderHeight);
	}
	
	
	public void smoothMovePosition(int position){
		mListView.smoothScrollToPosition(position);
	}
	
//	@Override
//	public void onNotify(int count) {
//		this.mListView.setSelection(count);
//		CommonUtil.log("notify", "cout = "+count);
////		this.mListView.smoothScrollToPosition(count);
//	}
	
	public int getFirstVisiblePosition(){
		return this.mListView.getFirstVisiblePosition();
	}
	public int getListItemCount(){
		return this.mListView.getChildCount();
	}
	public void addHeader(){
		mListView.addHeader(mHeaderView);
	}
	
	public static interface OnFingerTouchListener{
		public void onFingerTouch();
	}
	OnFingerTouchListener mOnFingerTouchListener;
	public void setOnFingerTouchListener(OnFingerTouchListener listener){
		mOnFingerTouchListener = listener;
	}
	public void onFingerTouch(){
		if(this.mOnFingerTouchListener!=null){
			mOnFingerTouchListener.onFingerTouch();
		}
	}
	public void setSelection(int select){
		mListView.setSelection(select);
	}
	
	public void setSelectionFromTop(int position ,int y){
		this.mListView.setSelectionFromTop(position+1, mHeaderHeight+y);
	}
	public void hideHeader(){
		this.mListView.hideHeader();
	}
//	@Override
//	public void onAllDataDelete() {
//		ThreadPool.obtain().executeMainThread(new Runnable() {
//			public void run() {
//				addHeader();
//			}
//		});
//	}
	public boolean istoTop(){
		return this.mListView.isTop();
	}
	public boolean istoBottom(){
		return this.mListView.isBottom();
	}
}

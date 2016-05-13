package com.renren.mobile.x2.components.chat.view;

import com.renren.mobile.x2.R.color;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.utils.DipUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
/***
 * 
 * @author xiaochao.zheng
 *
 */
public class SlipButtonGroup extends LinearLayout{
	private TextView leftBtn;
	private TextView midBtn;
	private TextView rightBtn;
//	private Bitmap bm ;
	private float oldx;
	private float oldy;
	private float x;
	private float y;
	private final static int DOWN= 0;
	private final static int UP = 1;
	private final static int MOVE = 2;
	private final static int CANCLE = 3;
	private final static int LEFT= 0;
	private final static int MID = 1;
	private final static int RIGHT = 2;
	private final static int SLIP = 0;
	private final static int CLICK = 1;
	private int action;
	private int position= 0;///0///1///2//;
	private int lastposition = 0;
	private int state =CLICK;
	private int maxwidth=0;
	private int maxheight=0;
	private int drawheight =0;
	private int top=0;
	private Paint mPaint = new Paint();
	private boolean isFirst=true;
	private LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	private Drawable mDrawable;
	Rect rect = new Rect();
	private final static int offset = DipUtil.calcFromDip(4);
	private OnSlipButtonStateChangedListener mListener;
	public SlipButtonGroup(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs);
		leftBtn = new TextView(context);
		midBtn = new TextView(context);
		rightBtn = new TextView(context);
//		leftBtn.setText("全部");
//		midBtn.setText("校草");
//		rightBtn.setText("校花");
//		leftBtn.setClickable(true);
//		midBtn.setClickable(true);
//		rightBtn.setClickable(true);
		rightBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t5));
		leftBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t5));
		midBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t5));
		leftBtn.setGravity(Gravity.CENTER);
		midBtn.setGravity(Gravity.CENTER);
		rightBtn.setGravity(Gravity.CENTER);
		leftBtn.setPadding(0, 0, 0, DipUtil.calcFromDip(2));
		midBtn.setPadding(0, 0, 0, DipUtil.calcFromDip(2));
		rightBtn.setPadding(0, 0, 0, DipUtil.calcFromDip(2));
		lp.weight=1;
//		lp.setMargins(0, 0, 0, DipUtil.calcFromDip(1));
		leftBtn.setLayoutParams(lp);
		midBtn.setLayoutParams(lp);
		rightBtn.setLayoutParams(lp);
		this.setGravity(HORIZONTAL);
		this.addView(leftBtn);
		this.addView(midBtn);
		this.addView(rightBtn);
		this.position=LEFT;
		this.setOrientation(HORIZONTAL);
		this.setWeightSum(3);
		x=0;
		y=0;
		state = CLICK;
	}

	public SlipButtonGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlipButtonGroup(Context context) {
		this(context, null, 0);
	}
	
//	public void setOnBitmap(Bitmap bm){
//		this.bm  = bm;
//	}		
	public void setOnDrawabel(Drawable drawable){
		this.mDrawable = drawable;
	}

	public void setPosition(int p) {
		this.position = p;
		if (p == LEFT) {
			leftBtn.setTextColor(RenrenChatApplication.getApplication()
					.getResources().getColor(color.t4));
		} else if (p == MID) {
			midBtn.setTextColor(RenrenChatApplication.getApplication()
					.getResources().getColor(color.t4));
		} else if (p == RIGHT) {
			rightBtn.setTextColor(RenrenChatApplication.getApplication()
					.getResources().getColor(color.t4));
		}
	}
	public void setTexts(String text1,String text2, String text3){
		leftBtn.setText(text1);
		midBtn.setText(text2);
		rightBtn.setText(text3);
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		this.maxwidth=r-l;
		this.maxheight =  b-t;
		super.onLayout(changed, l, t, r, b);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int act = event.getAction();
		Log.d("zxc","onInterceptTouchevent action " + act);
		x = event.getX();
		y = event.getY();
		switch (act) {
		case MotionEvent.ACTION_DOWN:
//			leftBtn.setText("onDown");
			oldx=x;
			oldy=y;
			if(getposition(x, y)!=-1){
				if(getposition(x, y) == position){//点击在button上面，代表是准备滑动
					state = CLICK;
				}else{///没有点击在button上面 
					state = CLICK;
				}
			}else{
				//没有在界限内不做任何处理
			}
			action = DOWN;
			position = getposition(x, y);
			break;
		case MotionEvent.ACTION_UP:
//			leftBtn.setText("onUP");
			action = UP;
			Log.d("slip","position " + position + "  lastposition " + lastposition);
			position = getposition(x, y);
			if(position == lastposition){
				break;
			}
			lastposition = position;
			mListener.OnStateChanged(position);
			if(position==LEFT){
				setTextcolorDefault();
				leftBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t4));
			}else if(position == MID){
				setTextcolorDefault();
				midBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t4));
			}else if(position == RIGHT){
				setTextcolorDefault();
				rightBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t4));
			}else if(position == -2 ){
				setTextcolorDefault();
				leftBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t4));
			}else{
				setTextcolorDefault();
				rightBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t4));
			}

			break;
		case MotionEvent.ACTION_MOVE:
			state = SLIP;
//			leftBtn.setText("onMove");
			action = MOVE;
			break;
		default:
			break;
		}
		this.invalidate();
		return true;
	}
	
	private void setTextcolorDefault(){
		rightBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t5));
		leftBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t5));
		midBtn.setTextColor(RenrenChatApplication.getApplication().getResources().getColor(color.t5));	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		Log.d("zxc","drawheight " + drawheight +  " top " + top);
		if(isFirst){
//			drawheight = maxheight
			
		leftBtn.getHitRect(rect);
		Log.d("zxc","rect " +rect.top + "  bottom " + rect.bottom);
			top = rect.top - DipUtil.calcFromDip(5);
//		top=0;
			drawheight = leftBtn.getMeasuredHeight();
//			top = (maxheight-drawheight)/2;
			isFirst=false;
			switch (position) {
			case LEFT:
				mDrawable.setBounds(0, top, maxwidth/3, rect.bottom);
				mDrawable.draw(canvas);
				break;
			case MID:
				mDrawable.setBounds(leftBtn.getMeasuredWidth(), top, maxwidth-maxwidth/3, rect.bottom+offset);
				mDrawable.draw(canvas);		
				break;
			case RIGHT:
				mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth/3, rect.bottom+offset);
				mDrawable.draw(canvas);	
				break;

			default:
				break;
			}
			
		}else{

			Log.d("zxc","dispatchDraw action " + action);
			if(state == SLIP){
				float dx,dy;
				if(action == MOVE){
					dx=x-oldx;
					dy=y-oldy;
//					if()
					Log.d("zxc","onmove");
					
					switch (position) {
					case 0:
						if(dx>maxwidth){
							dx=maxwidth;
						}
						if(dx>0&& dx <= midBtn.getMeasuredWidth()+rightBtn.getMeasuredWidth())
//						canvas.drawBitmap(bm, dx, 0, mPaint);
						{
							mDrawable.setBounds((int) dx, top, (int)(maxwidth/3+dx), rect.bottom+offset);
							mDrawable.draw(canvas);
						}else if (dx>midBtn.getMeasuredWidth()+rightBtn.getMeasuredWidth()){
							mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth, rect.bottom+offset);
							mDrawable.draw(canvas);
						}else if(dx<=0){
							mDrawable.setBounds(0, top, leftBtn.getMeasuredWidth(), rect.bottom+offset);
							mDrawable.draw(canvas);
						}
						break;
					case 1:

						if (dx + leftBtn.getMeasuredWidth() > 0
								&& dx <= rightBtn.getMeasuredWidth()) {
//							canvas.drawBitmap(bm, leftBtn.getMeasuredWidth() + dx,
//									0, mPaint);
							mDrawable.setBounds((int)(leftBtn.getMeasuredWidth()+dx), top, (int)(maxwidth-maxwidth/3+dx), rect.bottom+offset);
							mDrawable.draw(canvas);
						} else if (dx > rightBtn.getMeasuredWidth()) {
//							canvas.drawBitmap(bm, maxwidth, 0, mPaint);
							mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth, rect.bottom+offset);
							mDrawable.draw(canvas);
						}else if(dx+leftBtn.getMeasuredWidth()<=0){
							mDrawable.setBounds(0, top, leftBtn.getMeasuredWidth(), rect.bottom+offset);
							mDrawable.draw(canvas);
						}
						break;
					case 2:////有问题
						Log.d("zxc","draw 2 ");
						if(dx+leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth() > 0
								&&dx<=0){
//							canvas.drawBitmap(bm, dx+leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), 0, mPaint);
							mDrawable.setBounds((int)(dx+leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth()),
									top, (int)(maxwidth+dx), rect.bottom+offset);
							mDrawable.draw(canvas);
						}else if(dx>0){
//							canvas.drawBitmap(bm, maxwidth, 0, mPaint);
							Log.d("zxc","beyond right bounds ");
							mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth, rect.bottom+offset);
							mDrawable.draw(canvas);
						}else if(dx+leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth()<=0){
							mDrawable.setBounds(0, top, maxwidth/3, rect.bottom+offset);
							mDrawable.draw(canvas);
						}
						break;
					default:
						break;
					}
				}else if(action == UP){
					int pos = getposition(x, y);
					switch (pos) {
					case LEFT:
//						canvas.drawBitmap(bm, 0, 0, mPaint);
						mDrawable.setBounds(0, top, maxwidth/3, rect.bottom+offset);
						mDrawable.draw(canvas);
						break;
					case MID:
//						canvas.drawBitmap(bm, leftBtn.getMeasuredWidth(), 0, mPaint);
						mDrawable.setBounds(leftBtn.getMeasuredWidth(), top, maxwidth-maxwidth/3, rect.bottom+offset);
						mDrawable.draw(canvas);					
						break;
					case RIGHT:
//						canvas.drawBitmap(bm, leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), 0, mPaint);
						mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth, rect.bottom+offset);
						mDrawable.draw(canvas);	
						break;
					case -1:///
//						canvas.drawBitmap(bm, leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), 0, mPaint);
						mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth, rect.bottom+offset);
						mDrawable.draw(canvas);	
						break;
					case -2:///
//						canvas.drawBitmap(bm, 0, 0, mPaint);
						mDrawable.setBounds(0, top, maxwidth/3, rect.bottom+offset);
						mDrawable.draw(canvas);
						break;
					default:
						break;
					}
				}
			}else if(state == CLICK){
					Log.d("zxc"," X " + x);
					int pos = getposition(x, y);
					switch (pos) {
					case LEFT:
//						canvas.drawBitmap(bm, 0, 0, mPaint);
						mDrawable.setBounds(0, top, maxwidth/3, rect.bottom+offset);
						mDrawable.draw(canvas);
						break;
					case MID:
						mDrawable.setBounds(leftBtn.getMeasuredWidth(), top, maxwidth-maxwidth/3, rect.bottom+offset);
						mDrawable.draw(canvas);
						break;
					case RIGHT:
//						canvas.drawBitmap(bm, leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), 0, mPaint);
						mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth, rect.bottom+offset);
						mDrawable.draw(canvas);
						break;
					case -1:///
//						canvas.drawBitmap(bm, leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), 0, mPaint);
						mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth, rect.bottom+offset);
						mDrawable.draw(canvas);
						break;
					case -2:///
//						canvas.drawBitmap(bm, 0, 0, mPaint);
						mDrawable.setBounds(leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth(), top, maxwidth, rect.bottom+offset);
						mDrawable.draw(canvas);
						break;
					default:
						break;
					}
				
				
			}
		}
		super.dispatchDraw(canvas);
	}
	private int getposition(float xx, float yy){
		if(xx<leftBtn.getMeasuredWidth()&& xx>=0){
			return LEFT;
		}
		if(xx<midBtn.getMeasuredWidth() +leftBtn.getMeasuredWidth()&& xx>=leftBtn.getMeasuredWidth()){
			return MID;
		}
		if(xx<leftBtn.getMeasuredWidth()+midBtn.getMeasuredWidth()+rightBtn.getMeasuredWidth() && xx >=midBtn.getMeasuredWidth() +leftBtn.getMeasuredWidth() ){
			return RIGHT;
		}
		if(xx<0){
			return -2;
		}
		return -1;
	}
	
	public void setOnStateChangedListener(OnSlipButtonStateChangedListener listener){
		this.mListener=listener;
	}
}

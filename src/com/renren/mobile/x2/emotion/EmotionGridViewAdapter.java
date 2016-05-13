package com.renren.mobile.x2.emotion;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.chat.view.GifImageView;
import com.renren.mobile.x2.emotion.IEmotionManager.OnEmotionSelectCallback;
import com.renren.mobile.x2.utils.CommonUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
/****
 * 
 * @author xiaochao.zheng 
 *
 */
public class EmotionGridViewAdapter extends BaseAdapter{
	private OnClickListener mDelClickListener;
	private OnTouchListener mOntouchListener;
	private String[] codes;
	private String[] paths;
	private int size;
	private Context mContext;
	private OnEmotionSelectCallback mListener;
	private int last;
	private LayoutInflater mInflater;
	private boolean mIsgif;
	private PopupWindow mPopupWindow;
	private GifImageView gifImageView;
	private GIFRunnable runnable ;
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.d("emotion","action " + event.getAction());
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d("emotion","DOWN ");
				
				break;
			case MotionEvent.ACTION_UP:
				Log.d("emotion","up");
				stopGifView();
				break;
			case MotionEvent.ACTION_CANCEL:
				stopGifView();
				break;
			default:
				Log.d("emotion","default ");
				break;
			}
			return false;
		}
	};
	/***
	 * 
	 * @param strcodes 编码
	 * @param strpaths 路径
	 * @param isneedgif 是否需要gif表情
	 * @param context
	 * @param listener  坚挺表情选中 
	 */
	public EmotionGridViewAdapter(String[] strcodes, String[] strpaths, boolean isneedgif,Context context, OnEmotionSelectCallback listener) {
		this.codes = strcodes;
		this.paths = strpaths;
		this.size = codes.length==paths.length?codes.length:0;
		this.mContext = context;
		this.mListener= listener;
		this.mIsgif = isneedgif;
		this.last =size-1;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		this.codes[size]=EmotionConfig.delString;
		this.initdelBitmap();
		gifImageView = (GifImageView) mInflater.inflate(R.layout.emotion_gifpreview, null);
		mPopupWindow = new PopupWindow(gifImageView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		mDelClickListener	= new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mListener != null){
					mListener.mDelBtnClick();
				}
			}
		};
		mOntouchListener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					((ImageView) v).setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_emotion_del_pressed));
					break;
				case MotionEvent.ACTION_UP:
					((ImageView) v).setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_emotion_del_unpressed));

					break;
				default:
					break;
				}
				return false;
			}
		};
	}
	private void initdelBitmap(){
//		delpress = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_delete_press);
//		delunpress = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_delete_unpress);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView emotionView = null;
		if(mIsgif){
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.emotion_cube_item, null);
			}
			emotionView = (ImageView) convertView.findViewById(R.id.v1_emotion_cube_imageview);
			if(paths[position]!=null){
				Bitmap bm = EmotionPool.getInstance().getEmotion(paths[position]).next();
				if(bm!=null){
					emotionView.setImageBitmap(bm);
				}
				emotionView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mListener!=null){
							mListener.onCoolEmotionSelect(codes[position]);
						}
					}
				});
				emotionView.setOnTouchListener(mOnTouchListener);
				emotionView.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						Log.d("emotion","onLongClick ");
						stopGifView();
						popGifView(v, codes[position]);
						return true;
					}
				});
			}
		}else{
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.emotion_item, null);
			}
			emotionView = (ImageView) convertView.findViewById(R.id.v1_emotion_imageview);
			if(paths[position]!=null){
				Bitmap bm = EmotionPool.getInstance().getEmotion(paths[position]).next();
				if(bm!=null){
					emotionView.setImageBitmap(bm);
				}
				convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mListener!=null){
							mListener.onEmotionSelect(codes[position]);
						}
					}
				});
				
			}else if(position==last){
				emotionView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.v1_emotion_del_unpressed));
				emotionView.setOnClickListener(mDelClickListener);
				emotionView.setOnTouchListener(mOntouchListener);
			}
		}
		return convertView;
	
	}
	/****
	 * 延时两秒种触发
	 * @param v
	 * @param emotioncode
	 */
	private void popGifView(final View v,final String emotioncode){
		runnable = new GIFRunnable(emotioncode, v);
		RenrenChatApplication.getUiHandler().postDelayed(runnable, 1000);
		
	
	}
	
	
	/****
	 * 停止gif预览
	 */
	private void stopGifView(){
		Log.d("emotion","dismiss popupwindow");
		if(runnable!=null){
			Log.d("emotion","remove runnable");

		RenrenChatApplication.getUiHandler().removeCallbacks(runnable);}
		gifImageView.stop();
		mPopupWindow.dismiss();
	}
	private class GIFRunnable implements Runnable{
		String emotioncode;
		private View view;
		public GIFRunnable(String code, View view) {
			this.emotioncode =code;
			this.view = view;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("emotion","start cool emotion");
			gifImageView.start(emotioncode);
//			mPopupWindow.showAtLocation(view, Gravity.TOP, 0, 0);
			mPopupWindow.showAsDropDown(view, -50, -view.getMeasuredHeight()*2);
		}
	}
}

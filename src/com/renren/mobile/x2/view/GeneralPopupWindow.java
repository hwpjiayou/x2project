package com.renren.mobile.x2.view;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.SystemService;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

/**
 * @author xiangchao.fan
 * @description popup window from bottom
 */
public class GeneralPopupWindow{
	
	protected PopupWindow mWindow;
	
	/** popupWindow's content */
	private View mRoot;
	
	/** popupWindow's pop position */
	protected View mAnchor;
	
	private Context mContext;
	
	/** whether can be touched */
	private boolean mTouchable = true;
	
	/** whether can obtain focus */
	private boolean mFocusable = true;
	
	/** dim dialog */
	private Dialog dialog;
	
	private View emptyDialog = SystemService.sInflaterManager.inflate(R.layout.popup_empty, null);
	
	private int mPopType;
	
	public static interface POP_TYPE{
		public static final int POP_WITHOUT_DARK = 0;
		public static final int POP_WITH_DARK = 1;
	}
	
	/** 
	 * @description constructor without dim dialog 
	 * @param context
	 * @param root
	 * */
	public GeneralPopupWindow(View anchor, View root) {
		this.mPopType = POP_TYPE.POP_WITHOUT_DARK;
		
		this.mAnchor = anchor;
		this.mWindow = new PopupWindow(anchor.getContext());
		mContext = anchor.getContext();
		mRoot = root;
		
		initPopupWindow();
	}
	
	/**
	 * @description parent window become dark
	 * @param context
	 * @param root
	 */
	public GeneralPopupWindow(Context context, View root) {
		this.mPopType = POP_TYPE.POP_WITH_DARK;
		
		this.mAnchor = emptyDialog;
		this.mWindow = new PopupWindow(context);
		mContext = context;
		mRoot = root;

		initPopupWindow();
		initDialog();
	}
	
	/**
	 * when a touch even happens outside of the window
	 * make the window go away
	 */
	private void initPopupWindow(){
		mWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					GeneralPopupWindow.this.mWindow.dismiss();
					return true;
				}
				return false;
			}
		});
	}
	
	private void initDialog(){
		dialog = new Dialog(mContext,android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.dimAmount = 0.5f;
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialog.setContentView(emptyDialog);
		dialog.setCanceledOnTouchOutside(true);
		
		mWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
		
		dialog.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				popupTheWindow();
			}
		});
	}
	
	private void popupTheWindow(){
		setContentView(mRoot);
		setPopupWindowStyle();
		mWindow.showAtLocation(mAnchor, Gravity.BOTTOM, 0, 0);
	}
	
	private void setPopupWindowStyle(){
		mWindow.setBackgroundDrawable(mContext.getResources().getDrawable(android.R.color.transparent));
		//window.setBackgroundDrawable(new ColorDrawable(0x7DC0C0C0));
		mWindow.setWidth(WindowManager.LayoutParams.FILL_PARENT);
		mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setTouchable(true);
		mWindow.setFocusable(mFocusable);
		mWindow.setOutsideTouchable(mTouchable);
		mWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
	}

	/**
	 * Sets the content view. Probably should be called from {@link onCreate}
	 * 
	 * @param root
	 *            the view the popup will display
	 */
	public void setContentView(View root) {
		this.mRoot = root;
		
		mWindow.setContentView(root);
	}


	public void setOutSideTouchable(boolean touchable){
		mTouchable = touchable;
	}
	
	public void setFocusable(boolean focusable){
		mFocusable = focusable;
	}
	
	public boolean isShowing(){
		return mWindow.isShowing();
	}
	
	/**
	 * @description pop up the window
	 */
	public void show(){
		if(!isShowing()){
			switch(mPopType){
			case POP_TYPE.POP_WITHOUT_DARK:
				show_without_dark();
				break;
			case POP_TYPE.POP_WITH_DARK:
				show_with_dark();
				break;
			}
		}
	}
	
	/**
	 * @description dismiss the pop window
	 */
	public void dismiss() {
		mWindow.dismiss();
	}
	
	private void show_without_dark(){
		popupTheWindow();
	}
	
	private void show_with_dark(){
		dialog.show();
	}
}

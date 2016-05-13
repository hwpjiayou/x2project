package com.renren.mobile.x2.components.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.renren.mobile.x2.components.chat.util.ThreadPool;
import com.renren.mobile.x2.emotion.EmotionManager;
import com.renren.mobile.x2.utils.log.Logger;



/**
 * 聊天消息界面最下面的一排按钮
 */
public class InputBar extends AbstractInputBar{
	
	int mHeight = 0;
	
	boolean isFromComment;
	public InputBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public InputBar(){
		super(null, null);
		
	}
	/**这个方法只用于评论 */
	public void setPlugViewVisible(){
		mView_Plugins.setVisibility(View.GONE);
		isFromComment  = true;
	}
	
	public void setVisible(final View...views){
		this.setVisible(true,views);
	}
	public void setVisible(boolean isRunMain,final View...views){
		if(isRunMain){
			ThreadPool.obtain().executeMainThread(new Runnable() {
				public void run() {
					for(View v:views){
						v.setVisibility(View.VISIBLE);
					}
					onViewShow();
				}
			},200);
		}else{
			for(View v:views){
				v.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	
	
	public void setGone(final View...views){
		this.setGone(true, views);
	}
	
	public void setGone(boolean isRunMain,final View...views){
		if(isRunMain){
			ThreadPool.obtain().executeMainThread(new Runnable() {
				public void run() {
					for(View v:views){
						v.setVisibility(View.GONE);
					}
				}
			});
		}else{
			for(View v:views){
				v.setVisibility(View.GONE);
				if(v==mView_TextInputButton){
					mView_TextInputButton.setVisibility(View.INVISIBLE);
				}
			}
		}
	}
	
	
	
	
	@Override
	public void initMonitor() {
		mView_TextInputButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setVisible(false,mView_TextEdit,
						mView_VoiceInputButton);
				setGone(false,mView_VoiceRecord
					   ,mView_TextInputButton);
				setFocusable(true);
				mView_TextEdit.setFocusable(true);
				if(mView_TextEdit.getText().toString().trim().length()>0){
					mView_TextInputButton.setVisibility(View.GONE);
					mView_VoiceInputButton.setVisibility(View.GONE);
					mView_Send.setVisibility(View.VISIBLE);
				}
				mView_TextEdit.requestFocus();
				showKeyBoard();
			}
		});
		mView_VoiceInputButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setGone(mView_PluginViewGroup);
				mView_EmotionsGroup.setVisibility(View.GONE);
				
				setGone(false,mView_TextEdit,
						mView_VoiceInputButton);
				setVisible(false,mView_VoiceRecord
					   ,mView_TextInputButton);
				setGone(false, mView_KeyBoard);
				setVisible(false, mView_Emotion);
				hideKeyBoard();
			}
		});
		
		mView_Emotion.setOnClickListener(new OnClickListener() { //打开表情
			
			@Override
			public void onClick(View v) {
				if(Logger.mDebug){
					Logger.logd("打开表情 count="+mView_PluginViewGroup.getChildCount());
				}
				hideKeyBoard();
				
				if(mView_EmotionsGroup.getChildCount() == 0){
					long starttime = System.currentTimeMillis();
					View emotionView = EmotionManager.getInstance().getView(mView_EmotionsGroup.getContext(), !isFromComment,InputBar.this);
			        
			        if(Logger.mDebug){
			        	Logger.errord("加载表情空间时间="+(System.currentTimeMillis()-starttime));
			        }
			        mView_EmotionsGroup.addView(emotionView);
				}		
				
				mView_PluginViewGroup.setVisibility(View.GONE);
				mView_EmotionsGroup.setVisibility(View.VISIBLE);
				
				
				
				
				mView_Emotion.setVisibility(View.GONE);
				mView_KeyBoard.setVisibility(View.VISIBLE);
				
				mView_TextEdit.setVisibility(View.VISIBLE);
				mView_VoiceRecord.setVisibility(View.GONE);
				
				if(mView_TextEdit.getText().toString().trim().length()>0){
					setVisible(false, mView_Send);
					setGone(false, mView_VoiceInputButton);
					setGone(false, mView_TextInputButton);
				}else{
					setVisible(false, mView_VoiceInputButton);
					setGone(false, mView_TextInputButton);
				}
			}
		});
		mView_KeyBoard.setOnClickListener(new OnClickListener() { //关闭表情
			
			@Override
			public void onClick(View v) {
				if(Logger.mDebug){
					Logger.logd("关闭表情");
				}
				showKeyBoard();
				
				mView_PluginViewGroup.setVisibility(View.GONE);
				mView_EmotionsGroup.setVisibility(View.GONE);
				
				mView_Emotion.setVisibility(View.VISIBLE);
				mView_KeyBoard.setVisibility(View.GONE);
			}
		});
		mView_Plugins.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mView_PluginViewGroup.getVisibility()==View.GONE){
					if(Logger.mDebug){
						Logger.logd("打开附加组件");
					}
					hideKeyBoard();
					setVisible(mView_PluginViewGroup);
					setVisible(false, mView_Emotion);
					setGone(mView_EmotionsGroup);
					setGone(false, mView_KeyBoard);
					onViewShow();
				}else{
					if(Logger.mDebug){
						Logger.logd("关闭附加组件");
					}
					setGone(false, mView_PluginViewGroup);
				}
			}
		});
		
	}
	
	public void onViewShow(){
		if(inputBarLisenner!=null){
			inputBarLisenner.onViewShow();
		}
	}
	
	
	
	public boolean onBack(){
		boolean flag = isVisible(mView_PluginViewGroup)||isVisible(mView_EmotionsGroup);
		this.setGone(false,mView_PluginViewGroup,mView_EmotionsGroup);
		this.setGone(false,mView_KeyBoard);
		this.setVisible(false, mView_Emotion);
		
		return flag;
	}
	
	@Override
	protected void onKeyBoardShow(int height) {
		if(Logger.mDebug){
			Logger.logd(Logger.INPUT_BAR,"height="+height);
		}
		if(height>mHeight){
			mHeight = height;
		}
		if(height>0){
			//setHeight(mHeight, this.mView_EmotionsGroup,this.mView_PluginGroup);
		}
		onBack();
		if(inputBarLisenner!=null){
			inputBarLisenner.onKeyBoardShow();
		}
	}
	public void setHeight(int height,View...views){
		for(View v:views){
			android.view.ViewGroup.LayoutParams params = v.getLayoutParams();
			if(params!=null){
				params.height = height;
				v.setLayoutParams(params);
			}
		}
	}
	
}

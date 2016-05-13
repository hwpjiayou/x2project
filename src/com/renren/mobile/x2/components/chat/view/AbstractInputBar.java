package com.renren.mobile.x2.components.chat.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.chat.util.IInputBarListenner;
import com.renren.mobile.x2.components.chat.view.AbstractPluginGroup.OnPagerListenner;
import com.renren.mobile.x2.components.chat.view.PluginGroup.AbstractPluginAdapter;
import com.renren.mobile.x2.emotion.EmotionEditText;
import com.renren.mobile.x2.emotion.IEmotionManager.OnEmotionSelectCallback;
import com.renren.mobile.x2.utils.log.Logger;


/** 
 * 聊天最下面的输入条
 */
public abstract class AbstractInputBar extends LinearLayout implements OnEmotionSelectCallback {

	InputMethodManager mImm = null;
	
	/**附加组件按钮  即：拍照 相册 毛笔字 */
	View mView_Plugins;
	
	/**打开表情按钮 */
	View mView_Emotion ;
	/**关闭表情 即键盘按钮 */
	View mView_KeyBoard ;
	
	/**输入聊天内容的文本框 */
	public EmotionEditText mView_TextEdit;
	/**中间点击录音的按钮 */
	public View mView_VoiceRecord;
	
	/**录音或发送按钮的父布局 */
	//View mView_SendButton;
	/**发送按钮 */
	View mView_Send;
	/**录音按钮 */
	View mView_VoiceInputButton;
	/**文本按钮T */
	View mView_TextInputButton;
	
	/**整个附加组件 和 表情组件的父布局 */
	//View mGroup ;
    /**附加组件的父布局 */
	LinearLayout mView_PluginViewGroup;
    /**附加组件的布局   即：拍照 相册 毛笔字*/
	PluginGroup mView_PluginGroup ;
	/**表情的父布局 */
	LinearLayout mView_EmotionsGroup;
	
	IInputBarListenner inputBarLisenner;
	
	protected boolean isFromComment = false;
	
	public AbstractInputBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.inputbar, this);
		mImm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		//this.setBackgroundResource(R.drawable.inputbar_background);
		this.setOrientation(LinearLayout.VERTICAL);
		this.initViews();
		this.initMonitor();
		this.initListener();
	}

	
		
	private void initViews(){
		mView_Emotion 			 = this.findViewById(R.id.inputbar_emotions);
		mView_KeyBoard 			 = this.findViewById(R.id.inputbar_keyboard);
		mView_Plugins 			 = this.findViewById(R.id.inputbar_plugins);
		mView_TextEdit 			 = (EmotionEditText)this.findViewById(R.id.inputbar_textedit);
		mView_VoiceRecord 		 = this.findViewById(R.id.inputbar_record);
		mView_VoiceInputButton 	 = this.findViewById(R.id.inputbar_voiceinput);
		mView_TextInputButton	 = this.findViewById(R.id.inputbar_textinput);
		mView_PluginGroup 		 = (PluginGroup)this.findViewById(R.id.inputbar_plugin_group);
		mView_PluginViewGroup	 = (LinearLayout)this.findViewById(R.id.inputbar_plugin_viewgroup);
		//mView_SendButton 		 = this.findViewById(R.id.inputbar_textsend);
		mView_Send		 		 = this.findViewById(R.id.inputbar_send);
	//	mGroup 					 = this.findViewById(R.id.inputbar_groups);
		mView_EmotionsGroup 	 = (LinearLayout)this.findViewById(R.id.inputbar_emotions_group);
		
		
//		long starttime = System.currentTimeMillis();
//		
//		EmotionManager.setContext(this.getContext());
//        EmotionManager.getInstance();///初始化数据
//        mView_EmotionsGroup = EmotionManager.getInstance().getView(this.getContext(), this);
//        
//        if(CommonUtil.mDebug){
//        	CommonUtil.errord("加载表情空间时间="+(System.currentTimeMillis()-starttime));
//        }
        
//        int btn_w = (int)this.getContext().getResources().getDimension(R.dimen.chat_inputbar_plugin_and_emotion);
//      //  LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//      //  ll.height = btn_w;
//        if(CommonUtil.mDebug){
//        	CommonUtil.logd("表情高度="+btn_w);
//        }
//		if(mView_PluginViewGroup.getChildCount()> 1){
//			if(CommonUtil.mDebug){
//				CommonUtil.logd("如果出现这种情况  请查找原因");
//			}
//	        for(int len= mView_PluginViewGroup.getChildCount()-1;len>1;len++){
//	        	mView_PluginViewGroup.removeViewAt(len);
//	        }
//		}
//		//mView_PluginViewGroup.setVisibility(View.GONE);
//	    mView_PluginViewGroup.addView(mView_EmotionsGroup,1);
	    
	    
        
		mView_TextEdit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int pre = mView_TextEdit.getPreTextNumber();
				int number = s!=null?s.toString().trim().length():0;
				if(Logger.mDebug){
					Logger.logd(Logger.INPUT_BAR,"number="+number);
				}
				if(number>0){
					if(mView_TextEdit.getVisibility()==View.VISIBLE){
						if(Logger.mDebug){
							Logger.logd(Logger.INPUT_BAR,"显示发送按钮");
						}
						mView_Send.setVisibility(View.VISIBLE);
						mView_VoiceInputButton.setVisibility(View.GONE);
					}
					if(pre==0){
						onTyping();
					}
				}else{
					if(Logger.mDebug){
						Logger.logd(Logger.INPUT_BAR,"显示录音按钮");
					}
					mView_Send.setVisibility(View.GONE);
					mView_VoiceInputButton.setVisibility(View.VISIBLE);
					if(number==0&&pre>0){
						if(mView_TextEdit.isNotify()){
							onTypingCancel();
						}
					}
				}
				mView_TextEdit.setTextNumber(number);
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
	}
	
	private void initListener(){
		mView_Send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = mView_TextEdit.getText()!=null?""+mView_TextEdit.getText():null;
				if(message!=null&& message.length()>0){
					message=message.trim();
					onSend(message);
				}
			}
		});
	}
	
	protected  boolean isVisible(View view){
		return view.getVisibility()==View.VISIBLE;
	}
	
	public void setAdapter(AbstractPluginAdapter adapter){
		mView_PluginGroup.setAdapter(adapter);
	}
	
	public void setOnPagerListenner(OnPagerListenner listener){
		mView_PluginGroup.setOnPagerListenner(listener);
	}
	
	public abstract void initMonitor();
	
	protected void showKeyBoard(){
		mImm.showSoftInput(mView_TextEdit, InputMethodManager.SHOW_FORCED);
		mView_TextEdit.setFocusable(true);
		
	}
	public void hideKeyBoard(){
		mImm.hideSoftInputFromWindow(mView_TextEdit.getWindowToken(),0);
	}
//	public boolean onBack(){
//		return isVisible(mView_PluginViewGroup);
//	}
	protected abstract void onKeyBoardShow(int height);
	private int mMax = 0;
	int mOldTop = 0;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.mWidthMeasureSpec = widthMeasureSpec;
		this.mHeightMeasureSpec = heightMeasureSpec;
	};
	private int mWidthMeasureSpec = 0;
	private int mHeightMeasureSpec = 0;
	private boolean mIsKeyboardHide = true;
	@Override
	protected void onLayout(boolean changed,final int l, int t,final int r, int b) {
		
			int height = getRootViewHeight();
			if(Logger.mDebug){
				Logger.logd(Logger.INPUT_BAR,"height="+height+"#"+mMax+"#mOldTop="+mOldTop);
			}
			if(mMax<=height){
				mMax = height;
				if(!mIsKeyboardHide){
					mIsKeyboardHide = true;
				}
			}else{//keyboard show
				if(height!=mOldTop){
					onKeyBoardShow(mOldTop-height);
					mIsKeyboardHide = false;
					mOldTop = height;
					this.onMeasure(mWidthMeasureSpec, mHeightMeasureSpec);
					this.onChangeLayout(mWidthMeasureSpec,mHeightMeasureSpec);
					return;
				}
			}
			mOldTop = height;
		super.onLayout(changed, l, t, r, b);
	}
	public void onChangeLayout(int w,int h){
		if(inputBarLisenner!=null){
			inputBarLisenner.onChangeLayout(w,h);
		}
	}
	
	public int getRootViewHeight(){
		if(inputBarLisenner!=null){
			View view = this.inputBarLisenner.getRootView();
			if(view!=null){
				return view.getHeight();
			}
		}
		if(this.getContext() instanceof Activity){
			Window window = (Window)((Activity)this.getContext()).getWindow();
			ViewGroup group = (ViewGroup)window.getDecorView();
			return group.getBottom();
		}
		return 0;
	}
	
	public void onSend(String message){
		if(inputBarLisenner!=null){
			inputBarLisenner.onSend(message);
		}
	}
	public String getText(){
		return mView_TextEdit.getText()+"";
	}
	public void setText(String text){
		this.mView_TextEdit.setText("");
	}
	
	public void onTyping(){
		if(this.inputBarLisenner!=null){
			inputBarLisenner.onTyping();
		}
	}
	public void onTypingCancel(){
		if(this.inputBarLisenner!=null){
			inputBarLisenner.onTypingCancel();
		}
	}
	
	public static class RecordButton extends Button{

		String mTextDown;
		String mTextUp;
		public RecordButton(Context context, AttributeSet attrs) {
			super(context, attrs);
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RecordButton);
			this.initArray(array);
			array.recycle();
		}
		void initArray(TypedArray array){
			mTextDown = array.getString(R.styleable.RecordButton_text_down);
			mTextUp = array.getString(R.styleable.RecordButton_text_up);
			this.setText(mTextUp);
		}
		
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				this.setText(mTextDown);
			}
			if(event.getAction()==MotionEvent.ACTION_UP){
				this.setText(mTextUp);
			}
			return super.onTouchEvent(event);
		}
	}
	
	public void updateHit(int id){
		this.mView_TextEdit.setHint(id);
	}
	
	
	@Override
	public void onEmotionSelect(String emotion) {
		if(inputBarLisenner!=null){
			inputBarLisenner.onEmotionSelect(emotion);
		}
	}
	
	@Override
	public void onCoolEmotionSelect(String emotion) {
		if(inputBarLisenner!=null){
			inputBarLisenner.onCoolEmotionSelect(emotion);
		}
	}
	
	@Override
	public void mDelBtnClick() {
		if(inputBarLisenner!=null){
			inputBarLisenner.mDelBtnClick();
		}
	}

	public void unregistergetInputBarLisenner() {
		inputBarLisenner = null;
	}

	public void registerInputBarLisenner(IInputBarListenner inputBarLisenner) {
		this.inputBarLisenner = inputBarLisenner;
	}
	
}

package com.renren.mobile.x2.emotion;

import com.renren.mobile.x2.components.publisher.OnRelayoutListener;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
/****
 * 
 * @author xiaochao.zheng
 *
 */
public class EmotionEditText extends EditText {
	private static final int EMOTIONEDITTEXT_LENGTH = 240;
	private int mLastTextlength = 0;
	private int mModel = 0;///1 在最后插入表情 2表示删除表情模式0代表默认无状态模式
	public EmotionString mEmotionString = new EmotionString();
	public TextWatcherImpl mTextWatcherImpl = new TextWatcherImpl();
	private boolean mIsNotify = false;
	private int action = 0;
	private OnRelayoutListener mRelayoutListener;
	private int mOriginalHeight = -1;
	public EmotionEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.addTextChangedListener(mTextWatcherImpl);

	}

	public boolean isNotify(){
		boolean flag = this.mIsNotify;
		this.mIsNotify = true;
		return flag;
	}
	
	public void setNotNotify(){
		this.mIsNotify = false;
	}
	
	/**
	 * 重绘输入的界面
	 * */
	public void reflash() {
//		mEmotionString.reflash();
		this.setText(mEmotionString);
	}

	/**
	 * 设置消息
	 * */
	public void setMessage(String string) {
		this.setText(string);
	}
	
	
	/**
     *文本框删除表情
     */
    public void delLastCharOrEmotion() {
		int start = this.getSelectionStart();
		int num = 0;
		if(start == 0){//如果光标处于输入框的最左边，则不删除任何
			return ;
		}
		if(start == this.getText().length()){//在结尾处删除
			if(mModel != 2){
				mEmotionString.updateText(this.getText().toString());
				mModel = 2;//进入删除模式
			}
			num = mEmotionString.deleteLastEmotion();
			if (num != -1) {
				this.setText(mEmotionString);
			}
		}else{//在中间进行删除
			num = mEmotionString.deleteEmotion(this.getText().toString(), start);
			if(num != -1){
				this.setText(mEmotionString);
			}
		}
		if(num != -1){
			this.setSelection(start-num);
		}

	}
	public void insertEmotion(String emotion) {
		Log.d("emotion", emotion);
		StringBuffer sb = new StringBuffer();
		sb.append(this.getText().toString());
		int currentNum = getSelectionStart();
		if(currentNum < this.getText().length()){
			sb.insert(currentNum, emotion);
			mEmotionString.updateText(sb.toString());
		}else{
			mEmotionString.addEmotion(emotion);
		}
		mModel = 1;
		Log.d("emotion",mEmotionString.toString());
		this.setText(mEmotionString);
		this.setSelection(currentNum+emotion.length());
		
	}

	public static interface OnEditTextChangeSendBtnCallBack {
		public void setAble();

		public void setInable();
	}

	public OnEditTextChangeSendBtnCallBack mEditTextChangeSendBtnCallBack = null;

	public void setOnEditTextChangeSendBtnCallBack(
			OnEditTextChangeSendBtnCallBack onEditTextChangeSendBtnCallBack) {
		this.mEditTextChangeSendBtnCallBack = onEditTextChangeSendBtnCallBack;
	}

	private void setAble() {
		if (mEditTextChangeSendBtnCallBack != null) {
			mEditTextChangeSendBtnCallBack.setAble();
		}
	}

	private void setInable() {
		if (mEditTextChangeSendBtnCallBack != null) {
			mEditTextChangeSendBtnCallBack.setInable();
		}
	}

	/**
	 * 文本监控回调 复制粘贴的时候会用到
	 * */
	class TextWatcherImpl implements TextWatcher {

		private int selection = 0;
		// 判断新添加的字符串是否含有表情，专门用于粘贴事件
//		private boolean hasEmotionOrNot = false;

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// 在s内，count个以start开始的字符串将会被长度为after的新字符串替换。
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// 在s内，count个以start开始的字符串已经替换了长度为before的旧的字符串。
			if (before == 0) {
				selection = start + count;				
			} else {
				if (count == 0) {
					selection = start;
				} else {
					selection = start + count;
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			removeTextChangedListener(this);
			if(action == EmotionConfig.PASTE){
				action = 0;
				mModel=0;
				mEmotionString.updateText(s.toString());
				setText(mEmotionString);
			}
			int length = s.toString().trim().length();
            if (length <= 240) {
                if (length != 0) {
                    setAble();
                    if(mModel ==1){//插入模式
                        mModel = 0;
                    }else if(mModel == 2 && s.length() == mEmotionString.length()){//删除模式
                        
                    }else{   ///发送模式
                        mModel=0;
                        mEmotionString.updateText(s.toString());
                    }
                } else {
                    mEmotionString.updateText(s.toString());
                    setText(mEmotionString);
                    setInable();
                }
            } else {
                mEmotionString.updateText(s.subSequence(0,
                        240 )
                        .toString());
                setText(mEmotionString);
                selection = 240;
            }
			setSelection(selection);
			addTextChangedListener(this);
		}
	}
	
	/*add by dingwei.chen*/
	int mPreTextNumber;
	public void setTextNumber(int number){
		this.mPreTextNumber = number;
	}
	public int getPreTextNumber(){
		return this.mPreTextNumber;
	}
	
	
	@Override
	public boolean onTextContextMenuItem(int id) {
		action = id;
		return super.onTextContextMenuItem(id);
	}
	
	public void setOnRelayoutListener(OnRelayoutListener listener){
		this.mRelayoutListener = listener;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		if(mRelayoutListener!=null){
			//初始化初始高度
			if(mOriginalHeight == -1){
				mOriginalHeight = mRelayoutListener.getHight();
			}
			//如果被压缩，一定程度上说明是键盘弹起了
			if(mOriginalHeight > mRelayoutListener.getHight()){
				if(EmotionConfig.INPUTMETHOD_HEIGHT==-1){
					EmotionConfig.INPUTMETHOD_HEIGHT = mOriginalHeight-mRelayoutListener.getHight();
				}
				mRelayoutListener.onKeyBoardShow();
			}
		}
		super.onLayout(changed, left, top, right, bottom);
	}
	
}
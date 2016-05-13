package com.renren.mobile.x2.emotion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/***
 * 
 *	modify by zxc
 * 	EmotionString 维护了一个链表，里面包含了输入框中所有内容的所占字节信息，方便快速删除，
 * 	删除最后一个字符的时候最好使用delLastEmotion这样比较快
 *
 */
public class EmotionString extends SpannableStringBuilder {///整体写的思路还不是很清晰，有时间就重构下
	
	public static Context mContext;
	
	private boolean mIsHasEmotion = false;
	private final static Pattern SHORT_LINK_PATTERN = Pattern
			.compile("http://rrurl.cn/[a-z0-9A-Z]{6}");// 短连接匹配模式

	public final int LINK_COLOR = 0xff2F82d6;
	AlertDialog.Builder builder;
	private LinkedList<EmotionText> mEmotionSpans = new LinkedList<EmotionText>();// 动态表情队列
	private List<Integer> mEmotionSizeList = new ArrayList<Integer>();//存的是每个表情所占的字符个数

	public EmotionString() {
	}
	
	
	public void setTextSize(int start, int end, int size,boolean isdip){
		if(start < end && start >= 0 && end < this.length()-1){
			this.setSpan(new AbsoluteSizeSpan(size,isdip), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	/**
	 * 更新表情，会刷新整个EmotionString，速度比较慢
	 * 
	 * @param text
	 */
	public void updateText(String text) {
		
		this.clear();
		this.clearSpans();
		this.append(text);
		mEmotionSpans.clear();
		this.InitEmotionSizeList(this.length());
			this.parseEmotionText(this.toString(), EmotionConfig.SMALL_PACKAGE_ID,true);
		this.checkHasEmotion();
	}
	/****
	 * 添加表情,只是在最后添加，不会刷新整个EmotionString
	 * @param text
	 */
	public void addEmotion(String text) {
		this.append(text);
		this.addEmotionSizeList(text.length());
			this.parseEmotionText(text, EmotionConfig.SMALL_PACKAGE_ID, false);
		this.checkHasEmotion();
	
	}
	/***
	 * 初始化存储每个字符大小链表的list 初始值为1
	 * @param size
	 */
	private void InitEmotionSizeList(int size) {
		mEmotionSizeList.clear();
		for (int i = 0; i < size; ++i) {
			mEmotionSizeList.add(1);
		}
	}
	
	/****
	 * 添加到 mEmotionSizeList 添加值为1
	 * @param size
	 */
	private void addEmotionSizeList(int size){
		if(mEmotionSizeList!=null){
			for(int i =0; i <size;++i){
				mEmotionSizeList.add(1);
			}
		}
	}
	/*****
	 * 设置mEmotionSizeList的某个值 ，设置为 此处表情的字节个数
	 * @param leftindex 此表情的左边位置
	 * @param rightindex 此表情的右边位置
	 */
	private void setEmotionSizeList(int leftindex, int rightindex) {
		if (leftindex != rightindex) {
			mEmotionSizeList.set(rightindex - 1, rightindex - leftindex);
		}
	}
	/****
	 * 获取一个经过html.fromHtml处理过的EmotionString
	 * 使用了此方法之后不能使用此类中其他的方法
	 * @param str 
	 * @return
	 */
//	private EmotionString getHtmlString(String str){
//		this.clear();
//		this.clearSpans();
//		this.append(Html.fromHtml(str));
//		this.processUrl();
//		mEmotionSpans.clear();
//		this.InitEmotionSizeList(this.length());
//			this.parseEmotionText(this.toString(), EmotionConfig.SMALL_PACKAGE_ID,true);
//		this.checkHasEmotion();
//		return this;
//	}
	
	public EmotionString(Spanned spanned){
		super(spanned);
		InitEmotionSizeList(this.length());
		this.parseEmotionText(this.toString(), EmotionConfig.SMALL_PACKAGE_ID,true);
		processUrl(this);
		this.checkHasEmotion();
	}
	
	/**
	 * @param text
	 *            传入的参数将自动被转化成为带表情的文本
	 * */
	public EmotionString(String text) {
		super(text);
		InitEmotionSizeList(this.length());

		long oldtime = System.currentTimeMillis();
		this.parseEmotionText(this.toString(), EmotionConfig.SMALL_PACKAGE_ID,true);
		this.processUrl(this);
		this.checkHasEmotion();
		long cost = System.currentTimeMillis() - oldtime;
	}
	
	private void checkHasEmotion() {
		mIsHasEmotion = this.mEmotionSpans.size() > 0;
	}
	/****
	 * 获取不包含表情的字符串
	 * @param str
	 * @return
	 */
	public String getStringWithOutEmotion(String str){
		if(str == null){
			return null;
		}
		String localvalue = str;
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for(int i = 0; i < mEmotionSpans.size();++i){
			sb.append(localvalue.substring(index, mEmotionSpans.get(i).mStartIndex));
			index = mEmotionSpans.get(i).mEndIndex;
		}
		sb.append(localvalue.substring(index));
		return sb.toString();
	}
	/*****
	 * 获取一个将表情替换成特殊字符的方法
	 * @param special
	 * @return
	 */
	public String getStringWithOutSpecialString( String special){
		if(special==null){
			special = "";
		}
		String localvalue = this.toString();
		
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for(int i = 0; i < mEmotionSpans.size();++i){
			sb.append(localvalue.substring(index, mEmotionSpans.get(i).mStartIndex));
			sb.append(special);
			index = mEmotionSpans.get(i).mEndIndex;
		}
		sb.append(localvalue.substring(index));
		return sb.toString();
	}
	/****
	 * 
	 * @param text 解析的文本
	 * @param package_id 包id
	 * @param isreflash 是否要刷新， 如果是整体解析的话就进行刷新，如果是单个进行解析，就单个刷新
	 */
	private void parseEmotionText(String text, int package_id, boolean isreflash) {
		if (text == null || text.length() == 0) {
			return;
		}
		int index = 0;
		while (index < text.length()) {
				int end = text.length() > index + EmotionConfig.PRASE_SINGLE_MAX ? index + EmotionConfig.PRASE_SINGLE_MAX : text.length();
				int tmp = process(text.substring(index, end), index, package_id,isreflash);// 对于截取的字符串可以优化
				if (tmp != -1) {
					if(!isreflash){///是单独添加
							this.reflasSingle(text.substring(index,index+tmp), package_id);
					}
					index += tmp;
					continue;
				}
			index++;
		}

		this.checkHasEmotion();
		if(isreflash){
		this.reflash();}
	}

	/**
	 * 返回比较之后的size
	 * add by zxc
	 * @param strDes
	 * @param strRes
	 * @param isreflash 如果是调用的是单个表情添加的方法   addEmotion 时，仅仅刷新当前表情，不调用reflash方法
	 * @return
	 */
	private int process(String strDes, int from, int package_id,boolean isreflash) {
		int lastindex = 0;
		if(EmotionNameList.getEmotionNameList(package_id)==null){
			return -1;
		}
		if(-1 != (lastindex = EmotionNameList.getEmotionNameList(package_id).isContain(strDes))){///查询是否有这个表情
			if(isreflash){
				this.setSpanFromEmotionName(EmotionNameList.getEmotionNameList(package_id).path[lastindex],from, from + strDes.length(), 1);

			}
			return strDes.length();
		} else {
			if (strDes.length() == 0) {// /需要处理
				return -1;
			}
			return process(strDes.substring(0, strDes.length() - 1), from,//递归查找，线性查找
					package_id,isreflash);
		}
	}

	/***
	 * 将表情的详细信息添加到一个链表中 {@link EmotionText}
	 * @param emotionPath 表情的路径
	 * @param leftindex 左边位置
	 * @param rightindex 右边位置
	 * @param package_id 包id
	 */
	private void setSpanFromEmotionName(String emotionPath, int leftindex,
			int rightindex, int package_id) {
		if (emotionPath == null) {
			return;
		}
		Emotion emotion = EmotionPool.getInstance().getEmotion(emotionPath);
		if (emotion != null) {
			this.setEmotionSizeList(leftindex, rightindex);
			EmotionText emotionNode = new EmotionText(emotionPath, 0,
					emotion.mFrameSize, leftindex, rightindex);
			mEmotionSpans.add(emotionNode);
			
		} else {
			return;
		}
	}
	/****
	 * 将字符set成icon
	 * @param emotionPath
	 * @param leftindex
	 * @param rightindex
	 */
	private void updateSpanFromEmotionName(String emotionPath, int leftindex,
			int rightindex) {
		Emotion emotion = EmotionPool.getInstance().getEmotion(emotionPath);
		if (emotion != null) {

			EmotionSpan span = EmotionPool.getInstance()
					.getEmotionSpan(emotion);
			this.setSpan(span.copy(), leftindex, rightindex,
					SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	/****
	 * 	表情的详细信息
	 * @author Administrator
	 *
	 */
	public class EmotionText {
		public String mEmotionPath;
		public int mEmotionIndex;
		public int mEmotionSize;
		public int mStartIndex;
		public int mEndIndex;

		public EmotionText(String mEmotionPath, int mEmotionIndex,
				int mEmotionSize, int mStartIndex, int mEndIndex) {
			this.mEmotionPath = mEmotionPath;
			this.mEmotionIndex = mEmotionIndex;
			this.mEmotionSize = mEmotionSize;
			this.mStartIndex = mStartIndex;
			this.mEndIndex = mEndIndex;
		}

		public int next() {
			mEmotionIndex++;
			mEmotionIndex %= mEmotionSize;
			return mEmotionIndex;
		}
		/***
		 * 获取一个表情所占的字符数
		 * @return
		 */
		public int getDifference(){
			return mEndIndex - mStartIndex;
		}

	}


	// 刷新UI
	private void reflash() {
		if (mIsHasEmotion) {
			this.clearSpans();
			for (EmotionText node : mEmotionSpans) {
				this.updateSpanFromEmotionName(node.mEmotionPath,
						node.mStartIndex, node.mEndIndex);
			}

		}

	}
	/***
	 * 单独刷新一个表情
	 * @param str
	 * @param package_id
	 */
	private void reflasSingle(String str, int package_id){
		this.setEmotionSizeList(this.length()-str.length(), this.length());
		this.setSpanFromEmotionName(EmotionNameList.getEmotionNameList(package_id).getPath(str), this.length()- str.length(), this.length(), package_id);
		this.updateSpanFromEmotionName(EmotionNameList.getEmotionNameList(package_id).getPath(str), this.length()- str.length(), this.length());
	}
	
	/***
	 * 删除表情
	 * @param str 要进行操作的字符串
	 * @param index 删除的表情所占的字符数
	 * @return
	 */
	public int deleteEmotion(String str, int index) {
		this.updateText(str);
		if (index > str.length() || index <= 0) {
			return -1;
		}
		if (index > this.mEmotionSizeList.size()) {
			return -1;
		}
		int delnum = this.mEmotionSizeList.get(index - 1);
		String tmp;
		tmp = str.substring(index);
		str = str.substring(0, index);
		if (delnum != -1) {
			this.updateText(str.substring(0, str.length() - delnum) + tmp);
		}
		return delnum;
	}
	
	/*****
	 * 删除最后一个，当且仅当从最后一个删除的时候
	 * @return
	 */
	public int deleteLastEmotion() {
		if (mEmotionSizeList != null && mEmotionSizeList.size() != 0) {
			int delnum = this.mEmotionSizeList.get(mEmotionSizeList.size() - 1);
			if (delnum > 1 || mEmotionSpans.size()!=0&&mEmotionSpans.getLast().getDifference() ==1&&mEmotionSpans.getLast().mEndIndex==mEmotionSizeList.size() ) {
				for (int i = 0; i < delnum; ++i) {
					this.mEmotionSizeList.remove(mEmotionSizeList.size() - 1);
				}
				EmotionText node = mEmotionSpans.removeLast();
				this.replace(node.mStartIndex, node.mEndIndex, "");
			}else if(delnum == 1){

				this.replace(mEmotionSizeList.size()-1, mEmotionSizeList.size(), "");
				this.mEmotionSizeList.remove(mEmotionSizeList.size()-1);
				
			}
			return delnum;
		} else {
			return -1;
		}
	}
	private void processUrl(EmotionString emotionString) {
		String str = emotionString.toString();
		processUrl(emotionString,str,0);
	}
	
	private void processUrl(EmotionString emotionString, String str, int indexToAdd){
		int indexTemp = str.indexOf("http://");
		if (indexTemp != -1) {
			String sub = str.substring(indexTemp);
			if (sub.startsWith("http://rrurl.cn/")) {
				if (sub.length() >= "http://rrurl.cn/".length() + 6) {
					final String nextsub = sub.substring(0,
							"http://rrurl.cn/".length() + 6);
					Matcher m = SHORT_LINK_PATTERN.matcher(nextsub);
					if (m.matches()) {
						String nextString = sub.substring(nextsub.length());
						int start = indexTemp + indexToAdd;
						int end = start + nextsub.length();
						OnClickListener click = new OnClickListener() {
							public void onClick(View v) {
								runBrowser(nextsub);
							}
						};
						TextViewClickableSpan span = new TextViewClickableSpan(
								LINK_COLOR, click);
						emotionString.setSpan(span, start, end,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						indexToAdd += indexTemp + nextsub.length();
						processUrl(emotionString, nextString,indexToAdd);
					}
				}
			} else {// 非短连接

				int endindex = this.getEndIndex(sub);
				if (endindex != -1) {
					final String nextsub = sub.substring(0, endindex);
					String nextString = sub.substring(nextsub.length());
					int start = indexTemp + indexToAdd;
					int end = start + endindex;
					OnClickListener click = new OnClickListener() {
						public void onClick(View v) {
							runBrowser(nextsub);
						}
					};
					TextViewClickableSpan span = new TextViewClickableSpan(
							LINK_COLOR, click);
					emotionString.setSpan(span, start, end,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					indexToAdd += indexTemp + nextsub.length();
					processUrl(emotionString, nextString,indexToAdd);
				}
			}
		}
	}

	/***
	 * 开启浏览器
	 * 
	 * @param url
	 */
	public void runBrowser(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		AbstractRenrenApplication.getAppContext().startActivity(intent);
	}

	private int getEndIndex(String sub) {
		int length = sub.length();
		int index = "http://".length();
		while (index < length) {
			if (isUrlChar(sub.charAt(index))) {
				index++;
				continue;
			} else {
				break;
			}
		}
		return index;
	}

	private boolean isUrlChar(char c) {
		int k = (int) c;
		return Character.isDigit(c) || (65 <= k && k <= 90)
				|| (97 <= k && k <= 122) || c == '%' || (c == '/')
				|| (c == '.');
	}
	/****
	 * 获取长度
	 * @return 长度
	 */
	public int getLength(){
		return this.mEmotionSizeList.size();
	}
}
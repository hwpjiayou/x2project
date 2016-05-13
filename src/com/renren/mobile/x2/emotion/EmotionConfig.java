package com.renren.mobile.x2.emotion;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;

/**
 * Emotion的常量类
 * @author jiaxia
 *
 */
public class EmotionConfig {
/**
 * 包或者表情所属类别为   -->>小表情
 */
	public static final int EMOTION_PACKAGE_SMALL = 0;
	/**
	 * 包或者表情所属类别为   -->>中表情(玄酷表情)
	 */
	public static final int EMOTION_PACKAGE_COOL = 1;
	/**
	 * 表情包或者表情隐藏
	 */
	public static final int EMOTION_HIDDEN = 0;
	/**
	 * 表情包或者表情显示
	 */
	public static final int EMOTION_SHOW = 1;
	/**
	 * Emoji表情的编码类型--> -1
	 */
	public static final int EMOTION_CODE_EMOJI = -1;
	/**
	 * 除Emoji表情的小表情编码类型 -->  0
	 */
	public static final int EMOTION_CODE_SMALL_OTHER = 0;
	/**
	 * 中表情的编码类型  ->>1
	 */
	public static final int EMOTION_CODE_COOL = 1;
	/**
	 * 老表情模式
	 */
	public static final int VERSION_EMOTION_OLD = 1;
	 /**
	  * 新表情模式
	  */
	public static final int VERSION_EMOTION_NEW = 2;
	/**
	 * 当前模式
	 */
	public static final int VERSION_EMOTION_CURRENT = VERSION_EMOTION_OLD;
	/**
	 * 小表情的包ID
	 */
	public static final int SMALL_PACKAGE_ID = 1;
	
	/**
	 * 中表情的包ID
	 */
	public static final int COOL_PACKAGE_ID = 2;
	/**
	 * 小表情每页Page的行数(4)
	 */
	public static final int SMALL_ROW_NUM = 4;
	/**
	 *小表情 每页Page的列数(6)
	 */
	public static final int SMALL_COLUMN_NUM = 6;
	/**
	 * 小表情每页Page表情总数(24)
	 */
	public static final int SMALL_ONE_PAGE_SUM = 24;
	/**
	 * 小表情的标识符(用于样式判断)
	 */
	public static final int EMOTION_SMALL_STYLE = 0;
	/**
	 * 中表情的标识符(用于样式判断)
	 */
	public static final int EMOTION_COOL_STYLE = 10;
	/**
	 * 中表情每页page的行数(2)
	 */
	public static final int COOL_ROW_NUM = 2;
	/**
	 * 中表情每页Page的列数(4)
	 */
	public static final int COOL_COLUMN_NUM = 4;
	/**
	 * 中表情Page表情总数(8)
	 */
	public static final int COOL_ONE_PAGE_SUM = 8;
	/**
	 * 删除按钮标识符
	 */
	public static final String delString = "delemotion";
	
	/***
	 * 单个表情最大字符数
	 */
	public static final int PRASE_SINGLE_MAX = 7;
	/***
	 * 
	 */
	public static final int EMOTIONREF_SZIE = 100;
	
	public static final int PASTE = 16908322;
	
	public static int INPUTMETHOD_HEIGHT = -1;
	
	public static final int TAB_MIN_HEIGHT = 250;
	
	public static final int PER_PAGE_NUM = 23;
	
	public static int SCREENWIDTH;
	
	public static int SCREENHEIGHT;
	
	public static final int COLNUMS = 5;
	
	private static int EMOTIONVIEW_HEIGHT=0;
	
	private static int GRIDVIEWITEMHEIGHT=0;
	
	private static int GRIDVIEWITEMWIDTH=0;
	
	private static int TABVIEWHEIGHT = 0;
	
	private static int MAIN_CONTAINERHEIGHT = 0;
	
	public static int PADDING=0;
	
	public static int OFFSET = 20;
	
	public final static int NORMAL_EMOTION_SIZE =179;
	
	public static int getTabViewWidth(){
		return SCREENWIDTH;
	}
	public static int getTabViewHeight(){
			if(TABVIEWHEIGHT == 0){
				TABVIEWHEIGHT = getTotalEmotionViewHeight()/COLNUMS;
			}
			ErrLog.Print(TABVIEWHEIGHT+"");
		return TABVIEWHEIGHT;
	}
	public static int getGridViewItemHeight(){
		if(GRIDVIEWITEMHEIGHT == 0){
			GRIDVIEWITEMHEIGHT = (getTotalEmotionViewHeight()-getGridViewItemPadding()*3)/COLNUMS;
		}
		ErrLog.Print(GRIDVIEWITEMHEIGHT+"");

		return GRIDVIEWITEMHEIGHT;
	}
	public static int getGridViewItemPadding(){
		if(PADDING == 0){
			PADDING = SCREENWIDTH/18;
		}
		ErrLog.Print("PADDING "+PADDING);
		return PADDING;
	}
	public static int getGridViewItemWidth(){
		if(GRIDVIEWITEMWIDTH==0){
			GRIDVIEWITEMWIDTH =getTotalEmotionViewHeight()/COLNUMS - getGridViewItemPadding();
		}
		return GRIDVIEWITEMWIDTH;
	}
	public static int getMainContainerHeight(){
		if(MAIN_CONTAINERHEIGHT== 0){
			MAIN_CONTAINERHEIGHT=SCREENHEIGHT- getTotalEmotionViewHeight()/COLNUMS;
		}
		return MAIN_CONTAINERHEIGHT;
	}
	public static int getTotalEmotionViewHeight(){
		if(EMOTIONVIEW_HEIGHT == 0){
			SCREENHEIGHT=RenrenChatApplication.getScreenHeight()/COLNUMS*2;
		}
		return SCREENHEIGHT;
	}
	/////表情大概占屏幕的2/5
}

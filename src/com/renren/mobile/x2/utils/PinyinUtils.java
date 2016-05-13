package com.renren.mobile.x2.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.renren.mobile.x2.utils.pinyin4j.PinyinHelper;
import com.renren.mobile.x2.utils.pinyin4j.format.HanyuPinyinCaseType;
import com.renren.mobile.x2.utils.pinyin4j.format.HanyuPinyinOutputFormat;
import com.renren.mobile.x2.utils.pinyin4j.format.HanyuPinyinToneType;
import com.renren.mobile.x2.utils.pinyin4j.format.HanyuPinyinVCharType;
import com.renren.mobile.x2.utilsj.format.exception.BadHanyuPinyinOutputFormatCombination;


import android.text.TextUtils;

/**
 * @author eason Lee 
 * 汉字转化拼音工具类
 */

public final class PinyinUtils {
	/** 名字里面第一个字优先从这个map里面取 */
	private static HashMap<String, String> firstNameMap = new HashMap<String, String>();
	/** 所有从库里解析出的字的拼音*/
	private static HashMap<Character, String[]> pinyinCache = new HashMap<Character, String[]>();
	/** 可以加入特殊字的拼音（改bug用）*/
	private static HashMap<Character, String[]> specialPinyin = new HashMap<Character, String[]>();
	/**  好友列表刷新时候读拼音缓存*/
	private static HashMap<String, Pinyin> friendsCache = new HashMap<String, Pinyin>();

	private static final String wordSeparator = "哈";
	private static final String syllableSeparator = "囧";
	private static final char a='a';
	private static final char z='z';
	private static final String UNICODE="unicode";
	public  static final char default_aleph = '#';

	private static void pinyinMapInit() {
		firstNameMap.put("赵", "zhao");
		firstNameMap.put("钱", "qian");
		firstNameMap.put("孙", "sun");
		firstNameMap.put("李", "li");
		firstNameMap.put("周", "zhou");
		firstNameMap.put("吴", "wu");
		firstNameMap.put("郑", "zheng");
		firstNameMap.put("王", "wang");
		firstNameMap.put("冯", "feng");
		firstNameMap.put("陈", "chen");
		firstNameMap.put("褚", "chu");
		firstNameMap.put("卫", "wei");
		firstNameMap.put("蒋", "jiang");
		firstNameMap.put("沈", "shen");
		firstNameMap.put("韩", "han");
		firstNameMap.put("杨", "yang");
		firstNameMap.put("朱", "zhu");
		firstNameMap.put("秦", "qin");
		firstNameMap.put("尤", "you");
		firstNameMap.put("许", "xu");
		firstNameMap.put("何", "he");
		firstNameMap.put("吕", "lv");
		firstNameMap.put("施", "shi");
		firstNameMap.put("张", "zhang");
		firstNameMap.put("孔", "kong");
		firstNameMap.put("曹", "cao");
		firstNameMap.put("严", "yan");
		firstNameMap.put("华", "hua");
		firstNameMap.put("金", "jin");
		firstNameMap.put("魏", "wei");
		firstNameMap.put("陶", "tao");
		firstNameMap.put("姜", "jiang");
		firstNameMap.put("戚", "qi");
		firstNameMap.put("谢", "xie");
		firstNameMap.put("邹", "zou");
		firstNameMap.put("喻", "yu");
		firstNameMap.put("柏", "bai");
		firstNameMap.put("水", "shui");
		firstNameMap.put("窦", "dou");
		firstNameMap.put("章", "zhang");
		firstNameMap.put("云", "yun");
		firstNameMap.put("苏", "su");
		firstNameMap.put("潘", "pan");
		firstNameMap.put("葛", "ge");
		firstNameMap.put("奚", "xi");
		firstNameMap.put("范", "fan");
		firstNameMap.put("彭", "peng");
		firstNameMap.put("郎", "lang");
		firstNameMap.put("鲁", "lu");
		firstNameMap.put("韦", "wei");
		firstNameMap.put("昌", "chang");
		firstNameMap.put("马", "ma");
		firstNameMap.put("苗", "miao");
		firstNameMap.put("凤", "feng");
		firstNameMap.put("花", "hua");
		firstNameMap.put("方", "fang");
		firstNameMap.put("俞", "yu");
		firstNameMap.put("任", "ren");
		firstNameMap.put("袁", "yuan");
		firstNameMap.put("柳", "liu");
		firstNameMap.put("酆", "feng");
		firstNameMap.put("鲍", "bao");
		firstNameMap.put("史", "shi");
		firstNameMap.put("唐", "tang");
		firstNameMap.put("费", "fei");
		firstNameMap.put("廉", "lian");
		firstNameMap.put("岑", "cen");
		firstNameMap.put("薛", "xue");
		firstNameMap.put("雷", "lei");
		firstNameMap.put("贺", "he");
		firstNameMap.put("倪", "ni");
		firstNameMap.put("汤", "tang");
		firstNameMap.put("滕", "teng");
		firstNameMap.put("殷", "yin");
		firstNameMap.put("罗", "luo");
		firstNameMap.put("毕", "bi");
		firstNameMap.put("郝", "hao");
		firstNameMap.put("邬", "wu");
		firstNameMap.put("安", "an");
		firstNameMap.put("常", "chang");
		firstNameMap.put("乐", "le");
		firstNameMap.put("于", "yu");
		firstNameMap.put("时", "shi");
		firstNameMap.put("傅", "fu");
		firstNameMap.put("皮", "pi");
		firstNameMap.put("卞", "bian");
		firstNameMap.put("齐", "qi");
		firstNameMap.put("康", "kang");
		firstNameMap.put("伍", "wu");
		firstNameMap.put("余", "yu");
		firstNameMap.put("元", "yuan");
		firstNameMap.put("卜", "bu");
		firstNameMap.put("顾", "gu");
		firstNameMap.put("孟", "meng");
		firstNameMap.put("平", "ping");
		firstNameMap.put("黄", "huang");
		firstNameMap.put("和", "he");
		firstNameMap.put("穆", "mu");
		firstNameMap.put("萧", "xiao");
		firstNameMap.put("尹", "yin");
		firstNameMap.put("姚", "yao");
		firstNameMap.put("邵", "shao");
		firstNameMap.put("堪", "kan");
		firstNameMap.put("汪", "wang");
		firstNameMap.put("祁", "qi");
		firstNameMap.put("毛", "mao");
		firstNameMap.put("禹", "yu");
		firstNameMap.put("狄", "di");
		firstNameMap.put("米", "mi");
		firstNameMap.put("贝", "bei");
		firstNameMap.put("明", "ming");
		firstNameMap.put("臧", "zang");
		firstNameMap.put("计", "ji");
		firstNameMap.put("伏", "fu");
		firstNameMap.put("成", "cheng");
		firstNameMap.put("戴", "dai");
		firstNameMap.put("谈", "tan");
		firstNameMap.put("宋", "song");
		firstNameMap.put("茅", "mao");
		firstNameMap.put("庞", "pang");
		firstNameMap.put("熊", "xiong");
		firstNameMap.put("纪", "ji");
		firstNameMap.put("舒", "shu");
		firstNameMap.put("屈", "qu");
		firstNameMap.put("项", "xiang");
		firstNameMap.put("祝", "zhu");
		firstNameMap.put("董", "dong");
		firstNameMap.put("粱", "liang");
		firstNameMap.put("杜", "du");
		firstNameMap.put("阮", "ruan");
		firstNameMap.put("闵", "min");
		firstNameMap.put("席", "xi");
		firstNameMap.put("季", "ji");
		firstNameMap.put("麻", "ma");
		firstNameMap.put("强", "qiang");
		firstNameMap.put("贾", "jia");
		firstNameMap.put("路", "lu");
		firstNameMap.put("娄", "lou");
		firstNameMap.put("危", "wei");
		firstNameMap.put("江", "jiang");
		firstNameMap.put("童", "tong");
		firstNameMap.put("颜", "yan");
		firstNameMap.put("郭", "guo");
		firstNameMap.put("梅", "mei");
		firstNameMap.put("盛", "sheng");
		firstNameMap.put("林", "lin");
		firstNameMap.put("刁", "diao");
		firstNameMap.put("钟", "zhong");
		firstNameMap.put("徐", "xu");
		firstNameMap.put("丘", "qiu");
		firstNameMap.put("骆", "luo");
		firstNameMap.put("高", "gao");
		firstNameMap.put("夏", "xia");
		firstNameMap.put("蔡", "cai");
		firstNameMap.put("田", "tian");
		firstNameMap.put("樊", "fan");
		firstNameMap.put("胡", "hu");
		firstNameMap.put("凌", "ling");
		firstNameMap.put("霍", "huo");
		firstNameMap.put("虞", "yu");
		firstNameMap.put("万", "wan");
		firstNameMap.put("支", "zhi");
		firstNameMap.put("柯", "ke");
		firstNameMap.put("昝", "zan");
		firstNameMap.put("管", "guan");
		firstNameMap.put("卢", "lu");
		firstNameMap.put("莫", "mo");
		firstNameMap.put("经", "jing");
		firstNameMap.put("房", "fang");
		firstNameMap.put("裘", "qiu");
		firstNameMap.put("缪", "miao");
		firstNameMap.put("干", "gan");
		firstNameMap.put("解", "xie");
		firstNameMap.put("应", "ying");
		firstNameMap.put("宗", "zong");
		firstNameMap.put("丁", "ding");
		firstNameMap.put("宣", "xuan");
		firstNameMap.put("贲", "ben");
		firstNameMap.put("邓", "deng");
		firstNameMap.put("郁", "yu");
		firstNameMap.put("单", "shan");
		firstNameMap.put("杭", "hang");
		firstNameMap.put("洪", "hong");
		firstNameMap.put("包", "bao");
		firstNameMap.put("诸", "zhu");
		firstNameMap.put("左", "zuo");
		firstNameMap.put("石", "shi");
		firstNameMap.put("崔", "cui");
		firstNameMap.put("吉", "ji");
		firstNameMap.put("钮", "niu");
		firstNameMap.put("龚", "gong");
		firstNameMap.put("程", "cheng");
		firstNameMap.put("嵇", "ji");
		firstNameMap.put("邢", "xing");
		firstNameMap.put("滑", "hua");
		firstNameMap.put("裴", "pei");
		firstNameMap.put("陆", "lu");
		firstNameMap.put("荣", "rong");
		firstNameMap.put("翁", "weng");
		firstNameMap.put("荀", "xun");
		firstNameMap.put("羊", "yang");
		firstNameMap.put("於", "yu");
		firstNameMap.put("惠", "hui");
		firstNameMap.put("甄", "zhen");
		firstNameMap.put("魏", "wei");
		firstNameMap.put("家", "jia");
		firstNameMap.put("封", "feng");
		firstNameMap.put("芮", "rui");
		firstNameMap.put("羿", "yi");
		firstNameMap.put("储", "chu");
		firstNameMap.put("靳", "jin");
		firstNameMap.put("汲", "ji");
		firstNameMap.put("邴", "bing");
		firstNameMap.put("糜", "mi");
		firstNameMap.put("松", "song");
		firstNameMap.put("井", "jing");
		firstNameMap.put("段", "duan");
		firstNameMap.put("富", "fu");
		firstNameMap.put("巫", "wu");
		firstNameMap.put("乌", "wu");
		firstNameMap.put("焦", "jiao");
		firstNameMap.put("巴", "ba");
		firstNameMap.put("弓", "gong");
		firstNameMap.put("牧", "mu");
		firstNameMap.put("隗", "wei");
		firstNameMap.put("山", "shan");
		firstNameMap.put("谷", "gu");
		firstNameMap.put("车", "che");
		firstNameMap.put("侯", "hou");
		firstNameMap.put("宓", "mi");
		firstNameMap.put("蓬", "peng");
		firstNameMap.put("全", "quan");
		firstNameMap.put("郗", "xi");
		firstNameMap.put("班", "ban");
		firstNameMap.put("仰", "yang");
		firstNameMap.put("秋", "qiu");
		firstNameMap.put("仲", "zhong");
		firstNameMap.put("伊", "yi");
		firstNameMap.put("宫", "gong");
		firstNameMap.put("宁", "ning");
		firstNameMap.put("仇", "qiu");
		firstNameMap.put("栾", "luan");
		firstNameMap.put("暴", "bao");
		firstNameMap.put("甘", "gan");
		firstNameMap.put("钭", "dou");
		firstNameMap.put("厉", "li");
		firstNameMap.put("戎", "rong");
		firstNameMap.put("祖", "zu");
		firstNameMap.put("武", "wu");
		firstNameMap.put("符", "fu");
		firstNameMap.put("刘", "liu");
		firstNameMap.put("景", "jing");
		firstNameMap.put("詹", "zhan");
		firstNameMap.put("束", "shu");
		firstNameMap.put("龙", "long");
		firstNameMap.put("叶", "ye");
		firstNameMap.put("幸", "xing");
		firstNameMap.put("司", "si");
		firstNameMap.put("韶", "shao");
		firstNameMap.put("郜", "gao");
		firstNameMap.put("黎", "li");
		firstNameMap.put("蓟", "ji");
		firstNameMap.put("薄", "bo");
		firstNameMap.put("印", "yin");
		firstNameMap.put("宿", "xiu");
		firstNameMap.put("白", "bai");
		firstNameMap.put("怀", "huai");
		firstNameMap.put("蒲", "pu");
		firstNameMap.put("台", "tai");
		firstNameMap.put("从", "cong");
		firstNameMap.put("鄂", "e");
		firstNameMap.put("索", "suo");
		firstNameMap.put("咸", "xian");
		firstNameMap.put("籍", "ji");
		firstNameMap.put("赖", "lai");
		firstNameMap.put("卓", "zhuo");
		firstNameMap.put("蔺", "lin");
		firstNameMap.put("屠", "tu");
		firstNameMap.put("蒙", "meng");
		firstNameMap.put("池", "chi");
		firstNameMap.put("乔", "qiao");
		firstNameMap.put("阴", "yin");
		firstNameMap.put("郁", "yu");
		firstNameMap.put("胥", "xu");
		firstNameMap.put("能", "neng");
		firstNameMap.put("苍", "cang");
		firstNameMap.put("双", "shuang");
		firstNameMap.put("闻", "wen");
		firstNameMap.put("莘", "shen");
		firstNameMap.put("党", "dang");
		firstNameMap.put("翟", "di");
		firstNameMap.put("谭", "tan");
		firstNameMap.put("贡", "gong");
		firstNameMap.put("劳", "lao");
		firstNameMap.put("逄", "pang");
		firstNameMap.put("姬", "ji");
		firstNameMap.put("申", "shen");
		firstNameMap.put("扶", "fu");
		firstNameMap.put("堵", "du");
		firstNameMap.put("冉", "ran");
		firstNameMap.put("宰", "zai");
		firstNameMap.put("郦", "li");
		firstNameMap.put("雍", "yong");
		firstNameMap.put("却", "que");
		firstNameMap.put("璩", "qu");
		firstNameMap.put("桑", "sang");
		firstNameMap.put("桂", "gui");
		firstNameMap.put("濮", "pu");
		firstNameMap.put("牛", "niu");
		firstNameMap.put("寿", "shou");
		firstNameMap.put("通", "tong");
		firstNameMap.put("边", "bian");
		firstNameMap.put("扈", "hu");
		firstNameMap.put("燕", "yan");
		firstNameMap.put("冀", "ji");
		firstNameMap.put("郏", "jia");
		firstNameMap.put("浦", "pu");
		firstNameMap.put("尚", "shang");
		firstNameMap.put("农", "nong");
		firstNameMap.put("温", "wen");
		firstNameMap.put("别", "bie");
		firstNameMap.put("庄", "zhuan");
		firstNameMap.put("晏", "yan");
		firstNameMap.put("柴", "chai");
		firstNameMap.put("瞿", "qu");
		firstNameMap.put("阎", "yan");
		firstNameMap.put("充", "chong");
		firstNameMap.put("慕", "mu");
		firstNameMap.put("连", "lian");
		firstNameMap.put("茹", "ru");
		firstNameMap.put("习", "xi");
		firstNameMap.put("宦", "huan");
		firstNameMap.put("艾", "ai");
		firstNameMap.put("鱼", "yu");
		firstNameMap.put("容", "rong");
		firstNameMap.put("向", "xiang");
		firstNameMap.put("古", "gu");
		firstNameMap.put("易", "yi");
		firstNameMap.put("慎", "shen");
		firstNameMap.put("戈", "ge");
		firstNameMap.put("廖", "liao");
		firstNameMap.put("庾", "yu");
		firstNameMap.put("终", "zhong");
		firstNameMap.put("暨", "ji");
		firstNameMap.put("居", "ju");
		firstNameMap.put("衡", "heng");
		firstNameMap.put("步", "bu");
		firstNameMap.put("都", "dou");
		firstNameMap.put("耿", "geng");
		firstNameMap.put("满", "man");
		firstNameMap.put("弘", "hong");
		firstNameMap.put("匡", "kuang");
		firstNameMap.put("国", "guo");
		firstNameMap.put("文", "wen");
		firstNameMap.put("寇", "kou");
		firstNameMap.put("广", "guang");
		firstNameMap.put("禄", "lu");
		firstNameMap.put("阙", "que");
		firstNameMap.put("东", "dong");
		firstNameMap.put("殴", "ou");
		firstNameMap.put("殳", "shu");
		firstNameMap.put("沃", "wo");
		firstNameMap.put("利", "li");
		firstNameMap.put("蔚", "wei");
		firstNameMap.put("越", "yue");
		firstNameMap.put("夔", "kui");
		firstNameMap.put("隆", "long");
		firstNameMap.put("师", "shi");
		firstNameMap.put("巩", "gong");
		firstNameMap.put("厍", "she");
		firstNameMap.put("聂", "nie");
		firstNameMap.put("晁", "chao");
		firstNameMap.put("勾", "gou");
		firstNameMap.put("敖", "ao");
		firstNameMap.put("融", "rong");
		firstNameMap.put("冷", "leng");
		firstNameMap.put("訾", "zi");
		firstNameMap.put("辛", "xin");
		firstNameMap.put("阚", "kan");
		firstNameMap.put("那", "na");
		firstNameMap.put("简", "jian");
		firstNameMap.put("饶", "rao");
		firstNameMap.put("空", "kong");
		firstNameMap.put("曾", "zeng");
		firstNameMap.put("毋", "wu");
		firstNameMap.put("沙", "sha");
		firstNameMap.put("乜", "nie");
		firstNameMap.put("养", "yang");
		firstNameMap.put("鞠", "ju");
		firstNameMap.put("须", "xu");
		firstNameMap.put("丰", "feng");
		firstNameMap.put("巢", "chao");
		firstNameMap.put("关", "guan");
		firstNameMap.put("蒯", "kuai");
		firstNameMap.put("相", "xiang");
		firstNameMap.put("查", "cha");
		firstNameMap.put("后", "hou");
		firstNameMap.put("荆", "jing");
		firstNameMap.put("红", "hong");
		firstNameMap.put("游", "you");
		firstNameMap.put("竺", "zhu");
		firstNameMap.put("权", "quan");
		firstNameMap.put("逯", "lu");
		firstNameMap.put("盖", "gai");
		firstNameMap.put("后", "hou");
		firstNameMap.put("桓", "huan");
		firstNameMap.put("公", "gong");
		specialPinyin.put('嗯', new String[] { "en" });
		specialPinyin.put('哼', new String[] { "heng" });
	}
		
	/**
	 * 根据na获得首字母
	 * 从服务器获取联系人后 要根据名字取的姓氏拼音的首字母并存入数据库
	 */
	public static char getAleph(String name){
	    Pinyin pinyin =getPinyin(name);
	    if(pinyin == null){
	    	return default_aleph;
	    }else{
	    	 if(!TextUtils.isEmpty(pinyin.getQuanPin())){
	    		 char first=(pinyin.getQuanPin()).charAt(0);
	    		 if(first>=a&&first<=z){
	    			 return first;
	    		 }
	    	 }
	    }
	    return default_aleph;
	}

	public static void insertFriendsCache(String key, Pinyin value) {
		if (friendsCache.get(key) == null)
			friendsCache.put(key, value);
	}


	/**
	 * 获得汉字的拼音（只将汉字变成拼音，其他字符不变，会过滤空格）
	 * 
	 * @param src
	 *            源字符串
	 * @return Pinyin: re.quan//src第一个字的的拼音，多音字只取第一个读音 re.array//多音字包含全部读音
	 */
	public static Pinyin getPinyin(String src) {
		Pinyin reArray = friendsCache.get(src);
		if (reArray == null) {
			if (firstNameMap.size() == 0 || specialPinyin.size() == 0 || pinyinCache.size() == 0) {
				pinyinMapInit();
			}
			if (src != null && !src.trim().equalsIgnoreCase("")) {
				char[] srcChar;
				srcChar = src.toCharArray();
				// 汉语拼音格式输出类
				HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

				// 输出设置，大小写，音标方式等
				hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
				hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
				hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

				String[][] full = new String[src.length()][];
				boolean isFirstHanZi = true;
				String firstNamePinyin = null;
				for (int i = 0; i < srcChar.length; i++) {
					char c = srcChar[i];
					if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
						try {
							if (isFirstHanZi) {
								// 第一个优先取姓的读音
								firstNamePinyin = firstNameMap.get(String.valueOf(srcChar[i]));
								isFirstHanZi = false;
							}
							String[] kutmp = null;
							if ((kutmp = specialPinyin.get(srcChar[i])) == null) {
								if ((kutmp = pinyinCache.get(srcChar[i])) == null) {
									kutmp = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hanYuPinOutputFormat);
									pinyinCache.put(srcChar[i], kutmp);
								}
							}
							if(kutmp!=null){
								full[i] = kutmp;
							}else{
								full[i]= new String[] { " " };
							}
								
						} catch (BadHanyuPinyinOutputFormatCombination e) {
							e.printStackTrace();
						}
					} else if (((int) c >= 65 && (int) c <= 90) || ((int) c >= 97 && (int) c <= 122)) {
						full[i] = new String[] { String.valueOf(srcChar[i]).toLowerCase() };
						isFirstHanZi = false;
					} else {
						full[i] = new String[] { QBchange(String.valueOf(srcChar[i])) };
						isFirstHanZi = false;
					}
				}
				reArray = new Pinyin();
				reArray.setQuanPin(full[0][0]);
				char[][] init = new char[full.length][];
				for (int j = 0; j < full.length; j++) {
					String[] sa = full[j];
					ArrayList<Character> tmp = new ArrayList<Character>();
					if (j == 0 && !TextUtils.isEmpty(firstNamePinyin))
						tmp.add(firstNamePinyin.charAt(0));
					for (String s : sa) {
						if (!tmp.contains(s.charAt(0)))
							tmp.add(s.charAt(0));
					}
					char[] cr = new char[tmp.size()];
					int t = 0;
					for (char c : tmp) {
						cr[t++] = c;
					}
					init[j] = cr;
				}
				reArray.setMultiPinYin(init);
				if (friendsCache.get(src) == null) {
					friendsCache.put(src, reArray);
				}
			}
		}
		return reArray;
	}


	
	

	/**
	 * 将多音字数组转化成字符串
	 * 
	 * @param array
	 *            多音字数组
	 * @return reString 每个字用“哈”分隔，每个音用“囧”分隔
	 */
	public static String array2String(char[][] array) {
		if (!(array == null || array.length == 0)) {
			StringBuilder re = new StringBuilder();
			int i = 0;
			for (char[] ss : array) {
				int j = 0;
				for (char s : ss) {
					re.append(s);
					if (j != ss.length - 1)
						re.append(syllableSeparator);
					j++;
				}
				if (i != array.length - 1)
					re.append(wordSeparator);
				i++;
			}
			return re.toString();
		} else {
			return " ";
		}
	}

	/**
	 * 将字符串转化成多音字数组
	 * 
	 * @param s
	 *            每个字用“哈”分隔，每个音用“囧”分隔
	 * @return array 多音字数组
	 */
	public static char[][] string2Array(String s) {
		
		if (!TextUtils.isEmpty(s)) {
			//SystemUtil.logd("s="+s);
			String[] array1 = s.split(wordSeparator);
			char[][] re = new char[array1.length][];
			for (int i = 0; i < array1.length; i++) {
				//SystemUtil.logd(i+"="+array1[i]);
				String[] array2 = array1[i].split(syllableSeparator);
				re[i] = stringArray2CharArray(array2);
			}
			return re;
		} else {
			return null;
		}
	}

	private static char[] stringArray2CharArray(String[] s) {
		char[] ca = new char[s.length];
		int i = 0;
		for (String c : s) {
			ca[i++] = c.charAt(0);
		}
		//SystemUtil.logd("-------------------="+ String.valueOf(ca));
		return ca;
	}

	/**
	 * 生成索引的时候可以过滤掉名字里面的特殊字符(现在只有空格)
	 * 
	 * @param name
	 * @return
	 */
	public static String nameTrim(String name) {
		if(name == null){
			return "#";
		}
		name = name.trim();
		return name;
	}

	
	/**
	 * 返回数组长度
	 * @param name
	 * @return
	 */
	public static int nameLength(String name){
		return nameTrim(name).length();
	}


	/**
	 * 全角字符传化成半角
	 * 
	 * @param QJstr
	 *            全角字符
	 * @return 半角字符
	 */
	public static final String QBchange(String QJstr) {
		StringBuilder outStr = new StringBuilder();
		String Tstr = "";
		byte[] b = null;

		int len=QJstr.length();
		for (int i = 0; i < len ; i++) {
			try {
				Tstr = QJstr.substring(i, i + 1);
				b = Tstr.getBytes(UNICODE);
			} catch (java.io.UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (b[3] == -1) {
				b[2] = (byte) (b[2] + 32);
				b[3] = 0;
				try {
					//outStr = outStr + new String(b, "unicode");
					outStr.append(new String(b, UNICODE));
				} catch (java.io.UnsupportedEncodingException e) {
					//e.printStackTrace();
				}
			} else{
				//outStr = outStr + Tstr;
				outStr.append(Tstr);
			}
				
		}
		return outStr.toString();
	}
}

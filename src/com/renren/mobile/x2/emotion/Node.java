package com.renren.mobile.x2.emotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/***
 * 数据库中取出的表情信息全部放在这里
 * 
 * @author zxc
 */
public class Node {
	private boolean ishasGif = false;
	public int package_id;
	int length;
	public int currentlength;
	public int normallength;
	final static private int crease_size = 5;
	private static final String TAG = "Node";

	/***
	 * @param size
	 *            初始化时 path name code 数组的大小
	 */
	public Node(int size) {
		this.currentlength = size;
		this.length = this.currentlength+crease_size;
		path = new String[this.currentlength+crease_size];
		name = new String[this.currentlength+crease_size];
		code = new String[this.currentlength+crease_size];
		counter = new Integer[this.currentlength+crease_size];
		themeMap = new HashMap<Integer, ArrayList<Integer>>();
		templist = new ArrayList<Integer>(length);
		themeList = new ArrayList<Integer>();
	}

	public String[] path;
	public String[] name;
	public String[] code;
	public String[] chineseCode;// /刚开始不会初始化 ,getchineseCodeArray()时才会初始化
	public Integer[] counter;
	public ArrayList<Integer> templist;// /确保themeList是偶数个
	public ArrayList<Integer> themeList;
	public HashMap<Integer, ArrayList<Integer>> themeMap;
	private HashMap<String, Integer> compareMap;
	// public Integer] ishide ;
	int showlength;

	/***
	 * 设置主题 有此方法则必须有endSetTheme()
	 * 
	 * @param themeid
	 * @param i
	 */
	public void setTheme(Integer themeid, Integer i) {
		if (themeList != null && !themeList.contains(themeid)) {
			themeList.add(themeid);
		}
		templist.add(themeid);
		templist.add(i);
		// SystemUtil.log("xiaochao", "themeList.size " + templist.size());
	}

	/****
	 * 此方法与setTheme(...)对应的，有setTheme()则必须有endSetTheme()
	 */
	public void endSetTheme() {
		Integer tmpid = null;
		ArrayList<Integer> list = null;
		while (true) {
			for (Iterator<Integer> iter = templist.iterator(); iter.hasNext();) {
				Integer currentValue = iter.next();
				if (tmpid == null && !themeMap.containsKey(currentValue)) {// 当tmpid == null 并且当前themeid不在map中
					tmpid = currentValue;
					list = new ArrayList<Integer>();// /初始化List
					iter.remove();
					list.add(Integer.valueOf(iter.next()));
					iter.remove();
					continue;
				} else if (tmpid==currentValue) {
					iter.remove();
					list.add(Integer.valueOf(iter.next()));
					iter.remove();
				} else {
					iter.next();
				}
			}
			// 一次查找结束
////			SystemUtil.log("xiaochao",
//					"tmpid " + tmpid + "list size " + list.size());
			themeMap.put(tmpid, list);
			tmpid = null;

			if (templist.size() == 0) {
				break;
			}
		}
		templist.clear();
	}

	/***
	 * 根据主题获取此主题下所有表情的路径
	 * 
	 * @param themeid
	 *            主题id
	 * @return String[] 路径数组 如果没有返回空
	 */
	public String[] getEmotionPathArray(Integer themeid) {
		if (themeMap != null) {
			ArrayList<Integer> list = themeMap.get(themeid);
			if (list != null) {
				String[] ret = new String[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					ret[i] = path[list.get(i)];
				}
				return ret;
			}
		}
		return null;
	}

	public String[] getEmotionNameArray(Integer themeid){
		if (themeMap != null) {
			ArrayList<Integer> list = themeMap.get(themeid);
			if (list != null) {
				String[] ret = new String[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					ret[i] = name[list.get(i)];
				}
				return ret;
			}
		}
		return null;
	}

	public int getThemeSum(){
		if(themeMap==null ){
			return -1;
		}
		return themeMap.size();
	}
	public int getPageNum(int prepagesize, int themeid){
//		return this.currentlegth/prepagesize+ (this.currentlegth%prepagesize==0?0:1);
		if(themeMap.containsKey(themeid)){
			int size = themeMap.get(themeid).size();
			return size/prepagesize+(size%prepagesize==0?0:1);
		}else{
			return -1;
		}
	}
	public ArrayList<Integer> getThemeList(){
		if(themeList != null){
			return themeList;
		}
		return null;
	}
	
	public String[] getCodeArray(int pagesize, int pageindex){
		int size;
		size = this.currentlength-pageindex*pagesize > pagesize ? pagesize:this.currentlength-pageindex*pagesize ;
		String retcode[] = new String[pagesize+1];
		System.arraycopy(code, size*pageindex, retcode, 0, size);
		return retcode;
	}
	public String[] getPathArray(int pagesize, int pageindex){
		int size;
		size = this.currentlength-pageindex*pagesize > pagesize ? pagesize:this.currentlength-pageindex*pagesize ;
		String retpath[] = new String[pagesize+1];
		System.arraycopy(path, size*pageindex, retpath, 0, size);
		return retpath;
	}
	public String[] getCodeArrayByTheme(int pagesize, int pageindex, int themeid, boolean withdel){

		if(themeMap != null){
			ArrayList<Integer> list = themeMap.get(themeid);
			int size = list.size()-pageindex*pagesize>pagesize?pagesize:list.size()-pageindex*pagesize;
			if(list!= null){
				if(withdel){
					String[] ret = new String[pagesize+1];
					for(int i= 0; i < size;++i){
						ret[i]=code[pageindex*pagesize+list.get(i)];
					}
					return ret;
				}else{
					String[] ret = new String[size];
					for(int i= 0; i < size;++i){
						ret[i]=code[pageindex*pagesize+list.get(i)];
					}
					return ret;
				}
			}
		}
		return null;
	
	}
	
	public String[] getPathArrayByTheme(int pagesize, int pageindex, int themeid,boolean withdel){

		if(themeMap != null){
			ArrayList<Integer> list = themeMap.get(themeid);
			int size = list.size()-pageindex*pagesize>pagesize?pagesize:list.size()-pageindex*pagesize;
			if(list!= null){
				if(withdel){
					String[] ret = new String[pagesize+1];
					for(int i= 0; i < size;++i){
						ret[i]=path[pageindex*pagesize+list.get(i)];
					}
					return ret;
				}else{
					String[] ret = new String[size];
					for(int i= 0; i < size;++i){
						ret[i]=path[pageindex*pagesize+list.get(i)];
					}
					return ret;
				}
			}
		}
		return null;
	
	}
	
	/***
	 * 根据主题获取此主题下所有表情的转义符
	 * 
	 * @param themeid
	 *            主题ID
	 * @return String[] 编码数组， 如果没有返回空
	 */
	public String[] getEmotionCodeArray(Integer themeid) {
		if (themeMap != null) {
			ArrayList<Integer> list = themeMap.get(themeid);
			if (list != null) {
				String[] ret = new String[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					ret[i] = code[list.get(i)];
				}
				return ret;
			}
		}
		return null;
	}
	
	
	public String getCodeByTheme(int themeid,int position){
		if(themeMap!=null&&themeMap.containsKey(themeid)){
			int i = themeMap.get(themeid).get(position);
			if(i<currentlength-1 && i>=0){
			return code[i];
			}
		}
		return null;
	}
	
	
	public String getPathByTheme(int themeid,int position){
		if(themeMap!=null&&themeMap.containsKey(themeid)){
			int i = themeMap.get(themeid).get(position);
			if(i<currentlength-1 && i>=0){
			return path[i];
			}
		}
		return null;
	}

	/***
	 * 根据路径 获取编码
	 * 
	 * @param pathstr
	 *            路径
	 * @return 没有则会得到空，亲 ，记得判空啊
	 */
	public String getCode(String pathstr) {
		for (int i = 0; i < currentlength; ++i) {
			if (path[i] == pathstr) {
				return code[i];
			}
		}
		return null;
	}

	/**
	 * 根据编码获取路径
	 * 
	 * @param code
	 *            转义符，大表情的内容就是转义符
	 * @return 没有则会返回空，记得判空撒，亲
	 */
	public String getPath(String code) {
//		if(compareMap!=null&&compareMap.containsKey(code)){
//			return path[compareMap.get(code)];
//		}
		for (int i = 0; i < currentlength; ++i) {
//			SystemUtil.log("xcc", "code " + code + "  i" + i);
			if (this.code[i].equals(code)) {
//				SystemUtil.log("zxc", "return data  " + path[i]);
				return path[i];
			}
		}
		return null;
	}
	public String getPath(String code , Integer themeId){
		ArrayList<Integer> list = themeMap.get(themeId);
		if(list!= null){
			for(int i =0; i< list.size();++i){
				if(this.code[list.get(i)].equals(code)){
					return path[list.get(i)];
				}
			}
		}
		return null;
	}
	



	/***
	 * 获取此路径哈希值的 索引
	 * 
	 * @param key
	 *            路径的哈希值
	 * @return 返回此表情的index
	 */
	public int getIndex(int key) {
		for (int i = 0; i < this.currentlength; ++i) {
			if (key == path[i].hashCode()) {
				return i;
			}
		}
		return -1;
	}

	/****
	 * 查找表情编码
	 * 
	 * @param str
	 * @return if null then return -1 or return index
	 */
	public int isContain(String str) {
		if (compareMap == null) {
			compareMap = new HashMap<String, Integer>();
		}
		if (compareMap.size() == 0) {
			for (int i = 0; i < this.normallength; ++i) {
				compareMap.put(this.code[i], i);
			}
		}
		if (compareMap.containsKey(str)) {
			return compareMap.get(str);
		} else {
//			SystemUtil.log("xiaow", "get null from map " + str);
		}
		return -1;
	}
//	public int getPackageIdBy(int themeId){
//		Set set = themeMap.keySet();
//		for(Iterator<Integer> iter = set.iterator(); iter.hasNext();){
//			int pid = iter.next();
//			if(themeMap.get(pid).contains(themeId)){
//				return  pid;
//			}
//		}
//		return -1;
//	}
	/**
	 * 清空冗余数据
	 */
	public void clearCompareMap() {
		this.compareMap.clear();
	}

	private long getlong(String str) {
		int index = 0;
		long code = 0;
		while (index < str.length()) {
			code = code * 10 + str.charAt(index++);
		}
		return code;
	}
	// private int getHashCode(String str){
	// int hashcode = 0;
	// int seed = 13;
	// for(int i = 0; i<str.length();++i){
	// hashcode = hashcode >> seed+str.charAt(i);
	// }
	// return hashcode & 0X7FFFFFFF;
	// }
	
	public void add(String path,String name,String code,Integer themeid){
		if(this.currentlength >= this.length){
			increaseData();   //自增长数组
		}
		
			this.path[this.currentlength]=path;
			this.name[this.currentlength]= name;
			this.code[this.currentlength]=code;
			this.counter[this.currentlength]=0;
			this.setThemeincreased(themeid, this.currentlength);
			this.currentlength++;
	
	}
	/**
	 * 自动增长数组（crease_size）
	 */
	private void increaseData(){
		String[] cPath = new String[this.currentlength+crease_size];
		String[] cName = new String[this.currentlength+crease_size];
		String[] cCode = new String[this.currentlength+crease_size];
		Integer[] cCounter = new Integer[this.currentlength+crease_size];
		System.arraycopy(this.path, 0, cPath, 0, this.path.length);
		System.arraycopy(this.name, 0, cName, 0, this.name.length);
		System.arraycopy(this.code, 0, cCode, 0, this.code.length);
		System.arraycopy(this.counter, 0, cCounter, 0, this.counter.length);
		
		this.path = cPath;
		this.name = cName;
		this.code = cCode;
		this.counter  = cCounter;
		this.length = this.currentlength +crease_size;
	}
	
	public void setThemeincreased(Integer themeid, int index){
		if(themeMap.containsKey(themeid)){
			themeMap.get(themeid).add(index);
		}else{
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(index);
			themeMap.put(themeid, list);
			
		}
	}
	public void setishasGif(boolean ishas){
		this.ishasGif=ishas;
	}
	public boolean ishasGif(){
		return ishasGif;
	}
}

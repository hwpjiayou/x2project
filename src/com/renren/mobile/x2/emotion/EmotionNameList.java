package com.renren.mobile.x2.emotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;







/****
 * 初始化数据到内存中
 * @author xiaochao.zheng
 *
 */
public class EmotionNameList {
	/*存储Node key为包Id*/
	public static HashMap<Integer, Node> EmotionNameTable = new HashMap<Integer, Node>();
	/*包列表*/
	public static ArrayList<Integer> Package_idList = new ArrayList<Integer>();
     /***
      * 获取包id
      * @return
      */
     public static List<Integer> getPackageList(){
    	 
    	 if(Package_idList.size() <= 0){
    		 initPackList();
    	 }
    	 return Package_idList;
     }
     /****
      * 初始化表情包
      */
	public static void initPackList() {
		Package_idList.add(1);
	}
	/***	
//	 * 初始化 常用表情榜单
//	 * @param size 常用表情个数
//	 */
//	public static void initEmotionRank(int size) {}
	/***
	 * 初始化表情数据
	 */
	public static void initEmotion_NameList() {
		EmotionData data= EmotionData.getInstance();
		Node normalNode = new Node(data.code.length);
//		Log.d("emotion","normal emotion size " + data.code1.length);
		normalNode.normallength=EmotionConfig.NORMAL_EMOTION_SIZE;
//		normalNode.normallength = 
		for (int i = 0; i < data.code.length; i++) {
			normalNode.path[i]= data.path[i];
			normalNode.code[i] = data.code[i];
			normalNode.name[i] = data.name[i];
			normalNode.setTheme(data.themeid[i],i);
		}
		normalNode.endSetTheme();
		normalNode.package_id = 1;
		normalNode.setishasGif(true);
		EmotionNameTable.put(normalNode.package_id, normalNode);
		ErrLog.ll("node size "+normalNode.length);
		EmotionData.getInstance().destoryInstance();
	}

	/***
	 * 将传入的两位16进制转换成相应的byte[]，只能是两位的啊，
	 * add by zxc
	 * @param str
	 * @return
	 */
	public static byte[] getbyte(String str) {
		byte[] bytes = new byte[str.length()/2];
		int tmp;
		int sum = 0;
		for (int i = 0; i < str.length(); ++i) {

			char ch = str.charAt(i);

			if (i % 2 == 0) {
				if (ch <= '9' && ch >= '0') {
					tmp = ch - 48;
					sum = tmp * 16;
				} else if (ch <= 'F' && ch >= 'A') {//对于小写没有处理，所以小写不能处理
					tmp = ch - 55;
					sum = tmp * 16;
				}
			} else {
				if (ch <= '9' && ch >= '0') {
					tmp = ch - 48;
					sum += tmp;
				} else if (ch <= 'F' && ch >= 'A') {
					tmp = ch - 55;
					sum += tmp;
				}
				bytes[i/2] = (byte) sum;
				sum = 0;
			}
		}
		return bytes;
	}


	/***
	 * 获取package_id 下的表情数据
	 * @param package_id
	 * @return
	 */
	public static Node getEmotionNameList(int package_id){
		if( EmotionNameTable.size()<=0){
				initEmotion_NameList();
			}
		return EmotionNameTable.get(package_id);
	}
	/***
	 * 更新计数器
	 * @param package_id
	 * @param index
	 * @param counter
	 */
	public static void updateCounter(int package_id, int index, int counter){
		Node node = EmotionNameTable.get(package_id);
		node.counter[index] = counter;
		EmotionNameTable.put(package_id, node);
	}
	public static String getByteFromSb(String str){
		return (char)getInt(str) + "";
	}
	public static int getInt(String str){
		int tmp ;
		int sum =0;
		int ret=0;
		for (int i = 0; i < str.length(); ++i) {

			char ch = str.charAt(i);

				ret*=16;
				if (ch <= '9' && ch >= '0') {
					tmp = ch - 48;
					sum = tmp * 16;
				} else if (ch <= 'f' && ch >= 'a') {
					tmp = ch - 87;
					sum = tmp * 16;
				}
				 ret +=sum;
					sum = 0;
			
		}
//		CommonUtil.log("ret", "ret " + ret/16);
		return ret/16;
	}
	public static String unicodetoString(int num) {
		return String.valueOf(Integer.toHexString(num)).toUpperCase();
	}
	

}

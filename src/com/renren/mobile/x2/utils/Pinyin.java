package com.renren.mobile.x2.utils;

/**
 * 某个词组的全拼
 */
public final class Pinyin {
	/** 名字首字的拼音，多音字只取第一个读音 */
	private String quan;
	/** 多音字包含全部读音 */
	private char[][] array;
	public Pinyin() {}
	public Pinyin(String quan,char[][] array){
		this.quan = quan;
		this.array = array;
	}
	public void setQuanPin(String quanpin){
		this.quan=quanpin;
	}
	public String getQuanPin(){
		return this.quan;
	}
	
	public void setMultiPinYin(char[][] array){
		this.array=array;
	}
	public char[][] getMultiPinYin(){
		return this.array;
	}
	
}

package com.renren.mobile.x2.utils.voice;

/**
 * @author dingwei.chen
 * @说明 PCM帧模型
 * */
class PCMFrame {
	
	public static interface PCMOFFSET{
		int HEAD = -1;
		int MIDDLE=0;
		int TAIL = 1;
	}
	
	public short[] mPcmFrame;
	public int mSize ;
	public int mOffset = PCMOFFSET.MIDDLE;
	
	
	public PCMFrame(short[] pcmFrame,int size,int offset){
		this.mPcmFrame = pcmFrame;
		this.mSize = size;
		this.mOffset = offset;
	}
	
	public PCMFrame(short[] pcmFrame,int size){
		this.mPcmFrame = pcmFrame;
		this.mSize = size;
		mOffset = PCMOFFSET.MIDDLE;
	}

	/**
	 * 创建头部帧
	 * */
	public static PCMFrame createHeadFrame(){
		return new PCMFrame(null, 0);
	}
	/**
	 * 创建尾部帧
	 * */
	public static PCMFrame createTailFrame(){
		return new PCMFrame(null, 0);
	}
}

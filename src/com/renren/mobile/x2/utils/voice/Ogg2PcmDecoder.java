package com.renren.mobile.x2.utils.voice;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.afpro.utils.Decoder;


/**
 * @author dingwei.chen
 * Ogg 往PCM转换的解码器
 * */
class Ogg2PcmDecoder extends Decoder {

	private String mAbsFileName = null;
	private FileInputStream mFileInputStream = null;;
	private static final int FILE_BYTE_LENGTH = 512; 
	private boolean mIsEndOfFile = false;
	public boolean mIsExit = false;
	
	public Ogg2PcmDecoder(String absFileName){
		mAbsFileName =absFileName;
		try {
			mFileInputStream = new FileInputStream(mAbsFileName);
		} catch (FileNotFoundException e) {}
	}
	
	
	@Override
	protected byte[] read() {
		if(mFileInputStream==null){
			this.mIsEndOfFile = true;
			return new byte[0];
		}
		byte[] b = new byte[FILE_BYTE_LENGTH];
		try {
			int size = mFileInputStream.read(b);
			this.mIsEndOfFile = (size==-1);
			if(this.mIsEndOfFile){
				mFileInputStream.close();
				PCMFrame pcmFrame = new PCMFrame(null, 0);
				FramesPool.getInstance().addFrame2Pool(pcmFrame);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.mIsEndOfFile = true;
		}
		return b;
	}

	@Override
	public boolean eof() {
		return mIsEndOfFile||mIsExit;
	}

	public void stopDecoder(){
		mIsExit = true;
	}
	
	/**
	 * 解码后回调帧
	 * */
	@Override
	protected void frame(short[] frame) {
		//如果没有停止解码器就将帧放入帧池
		if(!mIsExit&&!mIsEndOfFile){
			PCMFrame pcmFrame = new PCMFrame(frame.clone(), frame.length);
			FramesPool.getInstance().addFrame2Pool(pcmFrame);
		}
	}


}

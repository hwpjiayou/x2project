package com.renren.mobile.x2.utils.voice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.afpro.utils.Encoder;

/**
 * @author dingwei.chen
 * */
class Pcm2OggEncoder extends Encoder {
	
	
	private static final int SAMPLE_RATE = 8000;
	private static final boolean WIDEBAND = false;
	private static final boolean VBR = true;
	private static final boolean PREPROCESS = true;
	private static final int FRAMES_IN_PACKET = 4;
	private String mAbsFileName = null;
	private ByteArrayOutputStream mRecordFileBytes = new ByteArrayOutputStream(128);
	private boolean mIsWriteToFile = true;
	
	public Pcm2OggEncoder(String absFileName){
		this(SAMPLE_RATE,WIDEBAND,VBR,PREPROCESS,FRAMES_IN_PACKET);
		this.mAbsFileName = absFileName;
	}
	public String getFileName(){
		return this.mAbsFileName;
	}
	
	/**
	 * {@hide}
	 * */
	public Pcm2OggEncoder(int samplingRate, boolean wideband, boolean vbr,
			boolean preprocess, int framesPerPacket) {
		super(samplingRate, wideband, vbr, preprocess, framesPerPacket);
		this.setOnePacketPerPage(true);//每一一个package(为了兼容)
	}
	
	@Override
	protected void page(byte[] header, byte[] body) {
		// TODO Auto-generated method stub
		try {
			mRecordFileBytes.write(header);
			mRecordFileBytes.write(body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setIsWriteToFile(boolean isWriteFile){
		this.mIsWriteToFile = isWriteFile;
	}
	

	@Override
	protected void onEncoderEnd() {
		try {
			byte[] bytes = null;
			if(mIsWriteToFile && mAbsFileName!=null){
				File file = new File(mAbsFileName);
				if(!file.exists()){
					file.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(mAbsFileName);
				bytes = mRecordFileBytes.toByteArray();
				fos.write(bytes);
				fos.flush();
				fos.close();
				mRecordFileBytes.close();
			}
			if(mListenner!=null){
				mListenner.onEncoderEnd(mAbsFileName, bytes,true);
				mListenner = null;
			}
		} catch (Exception e) {
			if(mListenner!=null){
				mListenner.onEncoderEnd(mAbsFileName, null,false);
				mListenner = null;
			}
//			SystemUtil.toast("暂时无法向您的SD卡中写入数据");
		}
		
	}
	@Override
	protected void onEncoderBegin() {}
	
	public static interface OnEncoderListenner{
		public void onEncoderEnd(String absFileName,byte[] data,boolean isEncodeSuccess);
	}
	private OnEncoderListenner mListenner = null;
	public void setEncoderListenner(OnEncoderListenner listenner){
		mListenner = listenner;
	}

}

package com.renren.mobile.x2.utils.voice;

import java.util.concurrent.atomic.AtomicBoolean;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.voice.Pcm2OggEncoder.OnEncoderListenner;

/**
 * @author dingwei.chen
 * @说明 录音工具,单纯用来处理PCM码( MLGB的异步)
 * @回调结果:产生PCM编码(short[])
 * 
 * */
// TODO 木有最终销毁AudioRecord
public final class RecordThread extends Thread {

	private AudioRecord mAudioRecord;// 底层录音工具
	private AudioRecord mOldAudioRecord;
	private volatile boolean mIsRecording = false;
	private short[] mPcmFrame = new short[AudioParams.ENCODING_PCM_NUM];// PCM帧大小
	private byte[] mLock = new byte[0];
	private AtomicBoolean mIsExit = new AtomicBoolean(false);
	private static RecordThread sInstance = new RecordThread();
	private Pcm2OggEncoder mEncoder = null;
	private int mOffset = 0;
	private String mAbsFilePath = null;
	private AtomicBoolean mIsSaveFile = new AtomicBoolean(true);

	private RecordThread() {
		// this.initParam();//初始化用到的参数
	}

	public static RecordThread getInstance() {
		return sInstance;
	}

	// 初始化各种参数
	private void initParam() {
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AudioParams.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioParams.ENCODING_PCM_NUM_BITS, AudioParams.getRecorderBufferSize(AudioParams.SAMPLE_RATE));
	}

	// 录音步骤
	public void run() {
		while (!isExit()) {
			lockWaitSyn();// 同步Lock锁进行唤醒等待；唤醒有可能是用于录音也有可能是用来退出
			if (isExit()) {
				break;
			}
			initParam();
			if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
				CommonUtil.log("init");
				// 这个回调用来弹出窗口,启动编码器
				this.onStart();
				// 开始录音
				mAudioRecord.startRecording();
				mOldAudioRecord = mAudioRecord;
				try {
					mOffset = 0;
					while (isRecording()) {// 两种原因的中断:1.要关闭这个录音服务2.停止录音
						int size = 0;
						if (isCanRecord()) {// 时间越界判定
							size = mAudioRecord.read(mPcmFrame, mOffset, AudioParams.FRAME_SIZE);
							mOffset += size;
							if (mOffset >= AudioParams.ENCODING_PCM_NUM) {
								onRecording(mPcmFrame, mOffset);// 对录音进行编码
								RecordEncoderPool.getInstance().addToList(mPcmFrame.clone());
								mOffset = 0;
							}
						} else {
							onRecording(mPcmFrame, 0);
						}
					}
					throw new Exception();
				} catch (Exception e) {// 通过异常来中断录音操作
					// 避免数据残留
					if (mOffset > 0) {
						this.onRecording(mPcmFrame, mOffset);
						RecordEncoderPool.getInstance().addToList(mPcmFrame.clone());
					}
					// 回调录音结束
					this.onRecordOver();
					release();
					mIsSaveFile.set(true);
				}
			} else {
				CommonUtil.log("not init");
				this.onStart();

				this.onRecordOver();
				release();
				continue;
			}
		}
	}

	// 释放资源
	public void release() {
		if (mOldAudioRecord != null) {
			mOldAudioRecord.release();
			mOldAudioRecord = null;
		}
		mAudioRecord = null;
	}

	// 加锁等待
	public void lockWaitSyn() {
		synchronized (mLock) {
			try {
				if (!isRecording()) {
					mLock.wait();
				}
			} catch (InterruptedException e) {}
		}
	}

	public void notifyWaitSyn() {
		synchronized (mLock) {
			mLock.notify();
		}
	}

	@Override
	public void start() {
		if (this.getState() == State.NEW) {
			super.start();
		}
	}

	public boolean isExit() {
		return mIsExit.get();
	}

	public synchronized boolean isRecording() {
		return mIsRecording;
	}

	long time = 0L;

	// 开启录音
	public synchronized void startRecord(String absFilePath) {
		time = System.currentTimeMillis();
		mIsRecording = true;
		mIsExit.set(false);
		this.mAbsFilePath = absFilePath;
		if (this.getState() == State.NEW) {
			this.start();
		}
		notifyWaitSyn();
		this.mIsSaveFile.set(true);
	}

	// 停止录音
	public void stopRecord(boolean isSaveFile) {
		this.mIsSaveFile.set(isSaveFile);
		if (isSaveFile) {
			 this.waitTime(500);//避免数据丢失
		}
		this.mIsRecording = false;
	}
	public void setIsSaveFile(boolean isSaveFile){
		this.mIsSaveFile.set(isSaveFile);
	}
	
	
	/**
	 * PCM录音回调
	 * */
	public static interface OnRecordListenner extends OnEncoderListenner {
		public void onRecordStart(String fileName);

		public void onRecording(int vsize);

		public void onRecordEnd(String fileName);

		public boolean isCanRecord();
	}

	OnRecordListenner mRecordListenner = null;

	public boolean isCanRecord() {
		if (mRecordListenner != null) {
			return mRecordListenner.isCanRecord();
		}
		return true;
	}

	public void setOnRecordListenner(OnRecordListenner listenner) {
		mRecordListenner = listenner;
	}

	public void onStart() {
		mEncoder = new Pcm2OggEncoder(mAbsFilePath);
		RecordEncoderPool.getInstance().setEncoder(mEncoder);
		mEncoder.beg();
		mEncoder.setEncoderListenner(mRecordListenner);
		if (mRecordListenner != null) {
			mRecordListenner.onRecordStart(mAbsFilePath);
		}
	}

	public void onRecording(final short[] pcmdata, final int size) {
		int v = 0;
		if (size != 0) {
			v = PCMUtil.getVolume(pcmdata, size);
		}
		if (mRecordListenner != null) {
			mRecordListenner.onRecording(v);
		}
	}

	public void onRecordOver() {
		// mEncoder.end();
		RecordEncoderPool.getInstance().stop(mIsSaveFile.get());
		mIsRecording = false;
		if (mRecordListenner != null) {
			mRecordListenner.onRecordEnd(mEncoder.getFileName());
		}
	}

	public void waitTime(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

}

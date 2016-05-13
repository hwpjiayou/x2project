package com.renren.mobile.x2.utils.voice;

import com.renren.mobile.x2.utils.voice.PlayerThread.OnPlayerListenner;
import com.renren.mobile.x2.utils.voice.PlayerThread.OnSwitchPlayModeListenner;
import com.renren.mobile.x2.utils.voice.PlayerThread.PlayRequest;
import com.renren.mobile.x2.utils.voice.RecordThread.OnRecordListenner;

/**
 * @语音管理器
 * */
public final class VoiceManager implements OnRecordListenner{

	private static VoiceManager sInstance = new VoiceManager();
	OnRecordListenner mRecordListener = null;
	private long mVoiceLength;
	private long mVoiceStartTime = 0L;
	private VoiceManager(){}
	public static VoiceManager getInstance(){
		return sInstance;
	}
	/*录音*/
	public void record(String absFilePath){
		mVoiceStartTime = System.currentTimeMillis();
		RecordThread.getInstance().startRecord(absFilePath);
	}
	public void stopRecord(boolean isSaveFile){
		mVoiceLength = System.currentTimeMillis() - mVoiceStartTime;
		RecordThread.getInstance().stopRecord(isSaveFile);
	}
	public void stopAllPlay(){
		PlayerThread.getInstance().clearRequests();
		PlayerThread.getInstance().clearAllDelayRequests();
		this.stopCurrentPlay();
	}
	
	public void addToPlay(String absVoiceFileName,OnPlayerListenner listener){
		PlayRequest request = new PlayRequest();
		request.mAbsVoiceFileName = absVoiceFileName;
		request.mPlayListenner = listener;
		PlayerThread.getInstance().addToPlay(request);
	}
	public void addToDelayPlay(String absVoiceFileName,OnPlayerListenner listener){
		PlayRequest request = new PlayRequest();
		request.mAbsVoiceFileName = absVoiceFileName;
		request.mPlayListenner = listener;
		PlayerThread.getInstance().addToDelayPlay(request);
	}
	public void replay(){
		PlayerThread.getInstance().replay();
	}
	public void stopCurrentPlay(){
		PlayerThread.getInstance().stopCurrentPlay();
	}
	public boolean isRecording(){
		return RecordThread.getInstance().isRecording();
	}
	
	public boolean forceToPlay(String absVoiceFileName,OnPlayerListenner listener){
		PlayRequest request = new PlayRequest();
		request.mAbsVoiceFileName = absVoiceFileName;
		request.mPlayListenner = listener;
		return PlayerThread.getInstance().forceToPlay(request);
	}
	/**
	 * @see android.media.AudioManager#STREAM_MUSIC		     扬声器
	 * @see android.media.AudioManager#STREAM_VOICE_CALL  听筒
	 * */
	public void switchPlayMethod(int method){
		PCMPlayerSetting.switchStreamType(method);
	}
	
	public void setOnSwitchPlayerListenner(OnSwitchPlayModeListenner listener){
		PlayerThread.getInstance().setOnSwitchPlayerListenner(listener);
	}
	
	public long getVoiceLength() {
		return mVoiceLength;
	}
	
	public void setRecordListener(OnRecordListenner listener){
		mRecordListener = listener;
		RecordThread.getInstance().setOnRecordListenner(this);
	}
	public void unRegistorRecordListener(){
		mRecordListener = null;
		RecordThread.getInstance().setOnRecordListenner(null);
	}
	@Override
	public void onEncoderEnd(String absFileName, byte[] data,
			boolean isEncodeSuccess) {
		mRecordListener.onEncoderEnd(absFileName, data, isEncodeSuccess);
	}
	@Override
	public void onRecordStart(String fileName) {
		mRecordListener.onRecordStart(fileName);
	}
	@Override
	public void onRecording(int vsize) {
		mRecordListener.onRecording(vsize);
	}
	@Override
	public void onRecordEnd(String fileName) {
		mRecordListener.onRecordEnd(fileName);	
	}
	@Override
	public boolean isCanRecord() {
		return mRecordListener.isCanRecord();
	}
	public void removePlayRequest(String url){
		PlayerThread.getInstance().removePlayRequest(url);
	}
	public void unregistorSwitchPlayerListenner(OnSwitchPlayModeListenner listenner){
		PlayerThread.getInstance().unregistorSwitchPlayerListenner(listenner);
	}
}

package com.renren.mobile.x2.utils.voice;

import android.media.AudioFormat;
import android.media.AudioTrack;


/**
 * @author dingwei.chen
 * @说明 播放器请求池 用于语音声道切换
 * */
public final class PCMPlayerPool{

	private AudioTrack mAudioPlayer;//底层播放工具
	private static PCMPlayerPool sPool  = new PCMPlayerPool();
	private boolean mIsCreatePlayer = false;
	private int mChannel = 0;
	private int mSample = 0;
	private int mStreamType = 0;
	private PCMPlayerPool(){}
	public static PCMPlayerPool getPool(){
		return sPool;
	}
	
	
	private AudioTrack createAudioTrack(int channel, int sample, int streamType) {
        int channelConfig = channel==2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
        
        int bufferSize = Math.max(
                sample,
                AudioTrack.getMinBufferSize(sample, channelConfig, AudioFormat.ENCODING_PCM_16BIT)
        );
        return new AudioTrack(
                streamType,
                sample,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
        );
    }
	/*请求一个播放器！一旦发生声道变化重新定义播放器*/
	public AudioTrack obtainPlayer(int channel, int sample, int streamType){
		if(channel!=mChannel||sample!=mSample||streamType!=mStreamType){
			if(mAudioPlayer!=null){
				mAudioPlayer.pause();
				mAudioPlayer.release();
			}
			mAudioPlayer = createAudioTrack( channel,  sample,  streamType) ;
			mChannel = channel;
			mSample = sample;
			mStreamType = streamType;
			mAudioPlayer.play();
		}
		if(mAudioPlayer==null){
			mAudioPlayer = createAudioTrack( channel,  sample,  streamType) ;
			mAudioPlayer.play();
		}
		return mAudioPlayer;
	}
	public void putPlayer(AudioTrack player){
		if(player == mAudioPlayer && player!=null){
			mAudioPlayer.pause();
			mAudioPlayer.release();
			mAudioPlayer = null;
			player = null;
		}else if(player!=null && player.getPlayState()==AudioTrack.PLAYSTATE_PLAYING){
			player.pause();
			mAudioPlayer.release();
		}
	}
	
	
}

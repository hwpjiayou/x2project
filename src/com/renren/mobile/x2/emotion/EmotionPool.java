package com.renren.mobile.x2.emotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.home.nearbyfriends.ErrLog;




/****
 * 存储的表情包括 png，gif，gif第一帧
 * @author zxc
 *	
 */
public class EmotionPool {
	public static final int NORMAL_EMOTION_BITMAP_WIDTH_MIDDIP = 24;//中屏手机对应的手机像素高度
	public static final int NORMAL_EMOTION_BITMAP_HEIGHT_MIDDIP = 24;//中屏手机对应的手机像素高度
	private static final String TAG = "zxc";
	private int max_gifsize;
	private static Context context;
	private HashMap<String, Emotion> mGifCache = new HashMap<String, Emotion>();
	public EmotionCache mEmotionCache = new EmotionCache(50);

	/**
	 * 载入表情数据
	 * 
	 * @throws IOException
	 * */
	public  void loadEmotionData() {
				if(EmotionNameList.Package_idList == null || EmotionNameList.Package_idList.size()<=0){
					EmotionNameList.initPackList();
				}
				if (EmotionPool.getInstance().mEmotionCache.size() <= 0) {
					for(int i = 0; i < EmotionNameList.Package_idList.size()&&EmotionNameList.Package_idList!=null;++i){
						loadEmotion(EmotionNameList.Package_idList.get(i));
					}
					
				}
	}
	

	/****
	 * 根据包id获取此包下的所有表情
	 * 载入所有Emotion数据库中的表情
	 * @param package_id 包id
	 */
	public void loadEmotion(int package_id){
		String strpath = "";
		Node nodetmp = EmotionNameList.getEmotionNameList(package_id);
		EmotionPool  emotionPool = EmotionPool.getInstance();
		Log.d("load","context " + context);
		for (int i = 0; nodetmp!=null&&i < nodetmp.currentlength;++i) {///???
			try {

					Emotion emotion = new Emotion(nodetmp.name[i]);
					emotion.mFrameSize = 1;
					emotion.counter = nodetmp.counter[i];
					 strpath = nodetmp.path[i];
					InputStream is = getContext().getAssets().open("normal/img/"+strpath+".png");
					emotion.addBitmap(BitmapFactory.decodeStream(is));
					emotionPool.addEmotion(nodetmp.path[i], emotion);
					is.close();
				
			} catch (Exception e) {
				Log.d("load","path " + strpath +"  "+ e.getLocalizedMessage());
			}
		}
	}
	/***
	 * 添加表情
	 * @param emotion_path 表情路径
	 * @param emotion 表情{@link Emotion}
	 */
	public void addEmotion(String emotion_path, Emotion emotion){
		if(emotion==null){
			return ;
		}
		mEmotionCache.put(emotion_path.hashCode(), emotion);
	}
	public void addCoolEmotion(String path, Emotion emotion){
		if(mGifCache==null){
			return;
		}
		if(mGifCache.size()>max_gifsize){
			mGifCache.clear();
		}
		mGifCache.put(path, emotion);
	}
	/***
	 * 根据emotion取出span
	 * @param emotion {@link Emotion}
	 * @return
	 */
	public EmotionSpan getEmotionSpan(Emotion emotion){
		EmotionSpan span = null;
		if(span!=null){//确保线程安全撒？
			return span;
		}else{
			Bitmap bitmap = emotion.next();
			Drawable drawable = new BitmapDrawable(bitmap);
			drawable.setBounds(0,0,
					DipUtil.calcFromDip(NORMAL_EMOTION_BITMAP_HEIGHT_MIDDIP),
					DipUtil.calcFromDip(NORMAL_EMOTION_BITMAP_HEIGHT_MIDDIP));
			span = new EmotionSpan(drawable);
//			span = new EmotionSpan(bitmap);
//			mEmotionSpanTable.put(emotion.mEmotionName, span);
			return span;
		}
	}
	/****
	 * 清空缓存
	 */
	public void clear(){
//		mEmotionSpanTable.clear();
		mEmotionCache.clear();
	}
	
	
	
	//表情对应表
//	public Map<Integer,Emotion> mEmotionTable = new HashMap<Integer,Emotion>();
	//文本表情对应表
//	public Map<String,EmotionSpan> mEmotionSpanTable = new HashMap<String,EmotionSpan>();

	
	private static EmotionPool sInstance = null;
	private EmotionPool(){
		max_gifsize = getSizeOfCache();
	}
	
	
	/**
	 * 获取实例。同时会加载表情数据
	 * @return
	 */
	public static EmotionPool getInstance(){
//		VMRuntime.getRuntime().setMinimumHeapSize(HEAP_SIZE);
		if(sInstance==null){
			sInstance = new EmotionPool();
			sInstance.loadEmotionData();
		}
		return sInstance;
	}
	

	/**
	 * 线程不安全啊
	 * @return Emotion 得到表情
	 *  @param emotion_path  此表情的路径
	 * */
	public Emotion getEmotion(String emotion_path){
		Emotion emotion = mEmotionCache.get(emotion_path.hashCode());
		if(emotion == null){
			emotion = loadEmotion(emotion_path);
		}
		return emotion;
	}
	
	public Emotion getCoolEmotionbycode(String code){
		Node node = EmotionNameList.getEmotionNameList(1);
		Emotion emotion =null;
		String path = node.getPath(code);
		if(node != null&& path !=null){
			emotion = getCoolEmotion(path+".gif");
			if(emotion == null){
				this.reloadAlEmotionByFlash(path, 1);
			}else{
				return emotion;
			}
		}
		return null;
	}
	
	public Emotion getCoolEmotion(String path){
		return mGifCache.get(path);
	}
//	public void loadFirstFrame() {
//		Node nodetmp  = EmotionNameList.getEmotionNameList(EmotionConfig.COOL_PACKAGE_ID);
//		for(int i = 0; nodetmp!=null&&i<nodetmp.length;++i){
//			try{
//				try{
//
//					GIFDecode decode = new GIFDecode();
//					String strpath = nodetmp.path[i];
//					InputStream is = AbstractRenrenApplication.getAppContext()
//							.getAssets().open(strpath + ".gif");
//					Emotion emotion = new Emotion(nodetmp.name[i]);
//					decode.readFirstFrame(is);
//					emotion.addBitmap(decode.getFrame(0));
//					emotion.mIsFlash = false;
//					emotion.mFrameSize=1;
//					this.addFirstFrameEmotion(strpath, emotion);
//				
//				}catch(OutOfMemoryError error){
//					CommonUtil.log("emotion","内存溢出");
//				}
//			} catch (FileNotFoundException e) {
//				CommonUtil.log(TAG,"loading error " );
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//	private Emotion loadFirstFrame(String path){
//		Node nodetmp = EmotionNameList.getEmotionNameList(EmotionConfig.COOL_PACKAGE_ID);
//		int i = nodetmp.getIndex(path.hashCode());
//		GIFDecode decode = new GIFDecode();
//		try{
//			try{
//				String strpath = nodetmp.path[i];
//				InputStream is = AbstractRenrenApplication.getAppContext()
//						.getAssets().open(strpath + ".gif");
//				Emotion emotion = new Emotion(nodetmp.name[i]);
//				decode.readFirstFrame(is);
//				emotion.addBitmap(decode.getFrame(0));
//				emotion.mIsFlash = true;
//				emotion.mFrameSize=1;
//				this.addFirstFrameEmotion(strpath, emotion);
//				return emotion;
//			}catch(OutOfMemoryError error){
//				CommonUtil.log("emotion","内存溢出");
//				return null;
//			}
//		} catch (FileNotFoundException e) {
//			CommonUtil.log(TAG,"loading error " );
//			/**普通的读取操作*/
//			try {
//				try{
//					File file = new File(path+".gif");
//					InputStream is = new FileInputStream(file);
//					String strpath = nodetmp.path[i];
//					Emotion emotion = new Emotion(nodetmp.name[i]);
//					decode.readFirstFrame(is);
//					emotion.addBitmap(decode.getFrame(0));
//					emotion.mIsFlash = true;
//					emotion.mFrameSize=1;
//					this.addFirstFrameEmotion(strpath, emotion);
//					return emotion;
//				}catch(OutOfMemoryError error){
//					CommonUtil.log("emotion","内存溢出");
//				}
//			} catch (FileNotFoundException e1) {
//				e1.printStackTrace();
//			}
//
//			return null;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			CommonUtil.log(TAG,"loading error:IOException " );
//			e.printStackTrace();
//			return null;
//		}
//	}
	public Emotion loadEmotion(String path){
		Emotion emotion = new Emotion("");
		try {
			try{

				
				emotion.mFrameSize = 1;
				emotion.counter = 0;
				String strpath = path;
				InputStream is = context.getAssets().open("normal/img/"+strpath+".png");
				emotion.addBitmap(BitmapFactory.decodeStream(is));
				addEmotion(path, emotion);
				is.close();

			}catch(OutOfMemoryError error){
			}
			
		} catch (Exception e) {
			
		}
		return emotion;
	}
	
	public Emotion reloadAlEmotionByFlash(String path, int pid){

		if(path !=null){
			GIFDecode decode = new GIFDecode();
			try {
				try{
					InputStream is = RenrenChatApplication.getApplication()
							.getAssets().open("normal/img/"+path + ".gif");
					Emotion emotion = new Emotion(EmotionNameList
							.getEmotionNameList(1)
							.name[EmotionNameList
							      .getEmotionNameList(1).
									getIndex(path.hashCode())]);
					emotion.mIsFlash = true;
					this.addCoolEmotion(path + ".gif", emotion);
					decode.readAndSetToEmotion(is, emotion);
					return emotion;
				}catch(OutOfMemoryError e){
				}
			} catch (FileNotFoundException e) {
				ErrLog.ll("reload error " + path);
				
			} catch (IOException e) {
				ErrLog.ll("reload error " + path);
			}
		}
		return null;
	
	}
	
	/**
	 * @return EmotionSpan
	 * @说明 mEmotionSpanTable是基于事件增加的表
	 * @param index:表情的索引号
	 * */
	public EmotionSpan getEmotionSpan(Emotion emotion,int index){
		EmotionSpan span = null;
		if(span!=null){
			return span;
		}else{
			Bitmap bitmap = emotion.mEmotionList.get(index);
			Drawable drawable = new BitmapDrawable(bitmap);
			drawable.setBounds(new Rect(0, 0,
					DipUtil.calcFromDip(NORMAL_EMOTION_BITMAP_WIDTH_MIDDIP),
					DipUtil.calcFromDip(NORMAL_EMOTION_BITMAP_HEIGHT_MIDDIP)));
			span = new EmotionSpan(drawable);
//			mEmotionSpanTable.put(emotion.mEmotionName+index, span);
			return span;
		}
	}
	
	public static void setContext(Context c){
		context = c;
	}
	private Context getContext(){
		if(context == null ){
			context = RenrenChatApplication.getApplication();
		}
		return context;
	}
	private int getSizeOfCache() {
		int width=EmotionConfig.SCREENWIDTH;
		int height=EmotionConfig.SCREENHEIGHT;
		int index = 0;
		
		if (width * height > 1280 * 800) {//屏幕分辨率大于1280*800 缓存 为12
			return 12;
		} else if (width * height > 640 * 960) {//屏幕分辨率大于640*960 缓存 为10
			return 10;
		} else if (width * height > 540 * 960) {//屏幕分辨率540*960 缓存 为8
			return 8;
		} else {//其余的屏幕为6
			return 6;
		}
	}
}

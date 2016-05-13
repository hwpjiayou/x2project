package com.renren.mobile.x2.components.chat.util;

import com.renren.mobile.x2.components.chat.face.ICanTalkable;
import com.renren.mobile.x2.components.chat.message.ChatMessageWarpper;

public final class DataPool{

	private static ICanTalkable tmpCanTalkable =null;
	private static ChatMessageWarpper addedMessage =null;
	private static String mForwardFilePath;
	
	public static  boolean hasCanTalkable(){
		return tmpCanTalkable!=null;
	}
	
	public static boolean hasAddedMessage(){
		return addedMessage!=null;
	}

	public static ICanTalkable getTmpCanTalkable() {
		return tmpCanTalkable;
	}

	public static void setTmpCanTalkable(ICanTalkable tmpCanTalkable) {
		DataPool.tmpCanTalkable = tmpCanTalkable;
	}
	
	public static ChatMessageWarpper getAddedMessage() {
		return addedMessage;
	}

	public static void setAddedMessage(ChatMessageWarpper addedMessage) {
		DataPool.addedMessage = addedMessage;
	}
	
	public static void clear(){
		tmpCanTalkable = null;
		addedMessage  = null;
	}

	public static String getmForwardFilePath() {
		return mForwardFilePath;
	}

	public static void setmForwardFilePath(String mForwardFilePath) {
		DataPool.mForwardFilePath = mForwardFilePath;
	}
	
	
//	private Map<String,Object> mData = new HashMap<String,Object>();
//	public static final DataPool POOL =new DataPool();
//	public static IDataPool sPool = null;
//	
//	public static IDataPool obtain(){
//		if(sPool==null){
//			sPool = (IDataPool)Proxy.newProxyInstance(IDataPool.class.getClassLoader(), new Class[]{IDataPool.class},new InvocationHandler() {
//				public Object invoke(Object proxy, Method method, Object[] args)
//						throws Throwable {
//					String methodName = method.getName();
//					String name = null;
//					Object o = null;
//					
//					if(args!=null&&args.length>0){
//						name = ""+args[0];
//					}
//					if(methodName.startsWith("get")){
//						o = POOL.mData.get(name);
//					}
//					if(methodName.startsWith("put")){
//						POOL.mData.put(name, args[1]);
//					}
//					
//					if(methodName.startsWith("size")){
//						return POOL.mData.size();
//					}
//					if(methodName.startsWith("clear")){
//						POOL.mData.clear();
//					}
//					return o;
//				}
//			});
//		}
//		return sPool;
//	}

	
	
}

package com.renren.mobile.x2.utils;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import java.io.*;

import com.renren.mobile.x2.utils.log.Logger;


/**
 * @author dingwei.chen
 * @说明 文件工具包(支持同步和异步的方式) 包括文件路径截取集成PathUtil
 * @注: abs打头的文件路径为绝对路径
 * @未完成 ：1.文件名验证 2011-12-06
 * */

public class FileUtil {

	private static FileUtil sInstance = new FileUtil();

	private FileUtil() {}

	public static FileUtil getInstance() {
		return sInstance;
	}

	/**
	 * 通过URL来获得文件名
	 * */
	public String getFileNameFromURL(String url) {
		if(null != url){
			String[] strs = url.split("[/]");
			// 如果之前有文件在播放,但是本文件不存在,那是否让它播放停止*?
			String fileName = strs[strs.length - 1];
			return fileName;
		}
		return null;
	}

	/**
	 * 是否存在该文件
	 * 
	 * @param absFileName
	 *            :文件的绝对路径
	 * */
	public boolean isExistFile(String absFileName) {
		File file = new File(absFileName);
		return file.exists();
	}
	public void createFile(String absFilePath){
		File file = new File(absFilePath);
		file.mkdirs();
	}
	
	/*** 获取文件夹大小 ***/
	public long getFIleSize(File f) {
		if (isExistFile(f.getAbsolutePath())) {
			return 0L;
		}
		long size = 0;
		File flist[] = f.listFiles();
		if (flist == null) {
			return 0L;
		}
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFIleSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		Logger logger = new Logger("NCS");
		logger.d(f.getAbsolutePath() + " size:"+size);
		return size;
	}
	
	/**
	 * 读取文件对应字节码
	 * 
	 * @throws FileNotFoundException
	 * */
	public byte[] readBytes(File file) throws Exception {
		byte[] bytes = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytes);
		fis.close();
		return bytes;
	}

	public static interface SAVE_STATE {
		int NOT_SDCARD = -1;// 没有SD
		int SAVE_OK = 0;// 存储成功
		int LOCK_SPACE = 1;// 缺少空间
		int SAVE_ERROR = 2;// 存储失败
	}

	public int saveFile(byte[] data, String absFilePath) {
		if (DeviceUtil.getInstance().isSDCardHasEnoughSpace()) {
			try {
				FileOutputStream fos = new FileOutputStream(absFilePath);
				fos.write(data);
				fos.flush();
				fos.close();
			} catch (Exception e) {
				return SAVE_STATE.SAVE_ERROR;
			}
			return SAVE_STATE.SAVE_OK;
		} else {
			return SAVE_STATE.NOT_SDCARD;
		}
	}
	
	public int writeStringtoSD(String content, String absFilePath) {
        PrintStream outputStream = null;
        if (DeviceUtil.getInstance().isSDCardHasEnoughSpace()) {
			try {
				File file = new File(absFilePath);
		        if (!file.getParentFile().exists()) {
		            file.getParentFile().mkdirs();
		        }
		        outputStream = new PrintStream(new FileOutputStream(file));
	            outputStream.print(content);
			} catch (Exception e) {
				e.printStackTrace();
				return SAVE_STATE.SAVE_ERROR;
			} finally {
	            if (outputStream != null) {
	                outputStream.close();
	            }
	        }
			return SAVE_STATE.SAVE_OK;
		} else {
			return SAVE_STATE.NOT_SDCARD;
		}
	}

	public boolean deleteFile(String absFilePath) {
		File file = new File(absFilePath);
		return file.delete();
	}

	public boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	public void delFolder(String folderPath) {
			try {
				delAllFile(folderPath); //删除完里面所有内容
				String filePath = folderPath;
				filePath = filePath.toString();
				java.io.File myFilePath = new java.io.File(filePath);
				myFilePath.delete(); //删除空文件夹
			} catch (Exception ignored) {}
	}
	
	public String readString(Context context , String filename){
		FileInputStream fis = null;
		try {
		    fis = context.openFileInput(filename);
		    byte[] buffer = new byte[1024];
		    int length = -1;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while ((length = fis.read(buffer)) != -1) {
		    	baos.write(buffer, 0, length);
		    }
		    byte[] b = baos.toByteArray();
		    baos.close();
		    fis.close();
		    return new String(b);

		} catch (Exception e) {
			return null;
		}
	}
	public void writeString(Context context , String filename,String content){
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(content.getBytes());
		} catch (Exception e) {}finally{
			if(fos!=null ){
				try {
					fos.close();
				} catch (IOException e) {}
			}
		}
	}
	public void writeString(Context context , String filename,String content,int m){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filename));
			fos.write(content.getBytes());
		} catch (Exception e) {}finally{
			if(fos!=null ){
				try {
					fos.close();
				} catch (IOException e) {}
			}
		}
	}

    public static void justClose(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}

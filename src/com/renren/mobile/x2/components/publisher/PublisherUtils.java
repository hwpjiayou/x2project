package com.renren.mobile.x2.components.publisher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.renren.mobile.x2.utils.img.branch.SDK7;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
/**
 * publisher 用到的一些工具
 * @author zhenning.yang
 *
 */
public class PublisherUtils {
	/**
	 * 从byte数组得到图片
	 * 
	 * @param bytes
	 * @param opts
	 * @return
	 */
	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	/**
	 * 将bitmap转化为byte[]
	 */
	public static byte[] getByteFromPic(Bitmap bm, int quality) {
		ByteBuffer buffer = ByteBuffer.allocate(SDK7.getByteCount(bm));
		bm.copyPixelsToBuffer(buffer);
		Log.d("ytest", "buffer size " + buffer.array().length);
		return buffer.array();
	}
	
	/**
	 * 关闭OutputStream
	 */
	public static void closeQuietly(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭InputStream
	 */
	public static void closeQuietly(InputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将input流转为byte数组，自动关闭
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] toByteArray(InputStream input) throws Exception {
		if (input == null) {
			return null;
		}
		ByteArrayOutputStream output = null;
		byte[] result = null;
		try {
			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 100];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			result = output.toByteArray();
		} finally {
			closeQuietly(input);
			closeQuietly(output);
		}
		return result;
	}
}

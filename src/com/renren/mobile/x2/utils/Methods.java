package com.renren.mobile.x2.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.Toast;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.network.mas.INetRequest;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class Methods {
	public static boolean logMode = true;

	public static Bitmap createImage(byte[] imgData) {
		Bitmap img = null;
		try {
			img = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
		} catch (Exception e) {
			recycleBitmap(img);
			img = null;
		} catch (OutOfMemoryError e) {
			recycleBitmap(img);
			img = null;
		}
		return img;
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
			byte[] buffer = new byte[1024];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			result = output.toByteArray();
			if (null != input) {

				input.close();
			}
			if (null != output) {

				output.close();
			}
		} catch (Exception e) {
		} finally {
			closeQuietly(input);
			closeQuietly(output);
		}
		return result;
	}

	/**
	 * 将数据保存到文件中
	 * */
	public static boolean toFile(File file, byte[] data) {
		if (null == file)
			return false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				return false;
			}
		}

		byte[] buffer = new byte[1024 * 100];
		int size = 0;// 记录已经复制的byte数
		try {
			while (size < data.length) {
				int length = (data.length - size > buffer.length) ? buffer.length
						: (data.length - size);// 每次复制的byte数
				System.arraycopy(data, size, buffer, 0, length);
				size += length;
				fos.write(buffer);
			}
		} catch (IOException e) {
		} finally {
			data = null;
			closeQuietly(fos);
		}
		return true;
	}

	/**
	 * 关闭InputStream
	 */
	public static void closeQuietly(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭InputStream
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

	public static void recycleBitmap(Bitmap img) {
		if (img != null && !img.isRecycled()) {
			img.recycle();
		}
	}

	/**
	 * 
	 * @return if api level >= level
	 */
	public static boolean fitApiLevel(int level) {
		try {
			int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
			if (sdkVersion >= level) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 仅仅判断网络请求是否发生错误，不做toast提醒
	 * **/
	public static boolean checkNoError(final INetRequest request, JSONObject rsp) {
		if (rsp.has("error_code")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 调用系统浏览器
	 * 
	 * @param url
	 */
	public static void runBrowser(String url) {
		// Uri uri = Uri.parse(url);
		// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// GlobalValue.getRootActivity().startActivity(intent);
	}

	/**
	 * 转义下列字符 &:&amp; <:&lt; >:&gt; ":&quot; ':&apos;
	 * */
	public static String htmlEncoder(String string) {
		if (string != null) {
			return TextUtils.htmlEncode(string);
		} else {
			return null;
		}
	}

	/**
	 * 解码html转义字符
	 * */
	public static String htmlDecoder(String string) {
		if (string != null) {
			return Html.fromHtml(string).toString();
		} else {
			return null;
		}
	}

	/**
	 * 获得跟屏幕大小匹配的文字大小，用于设置paint的文字大小
	 * */
	public static int getTextSizeOnPaint(int textSize) {
		Context c = RenrenChatApplication.getApplication();
		Resources r;
		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				textSize, r.getDisplayMetrics());
	}

	/**
	 * 获得跟屏幕大小匹配的距离大小
	 * */
	public static int getSizeOnDevice(int textSize) {
		Context c = RenrenChatApplication.getApplication();
		Resources r;
		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				textSize, r.getDisplayMetrics());
	}

	/**
	 * 将dip转化为实际的px
	 * */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static boolean logFlag = true;

	private static long cpuSerial = 95279527L;

	static {
		try {
			Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(
					process.getInputStream());
			BufferedReader br = new BufferedReader(ir);
			String line = null;
			String strSerial = null;
			while ((line = br.readLine()) != null) {
				if (line.indexOf("Serial") > -1) {
					strSerial = (line.substring(line.indexOf(":") + 1,
							line.length())).trim();
					break;
				}
				line = null;
			}
			if (strSerial != null) {
				String serial = strSerial.replaceAll("[^\\d]", "2");
				cpuSerial = Long.parseLong(serial);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
		}
	}

	public static long uidToIndex(long uid) {
		return uid ^ cpuSerial;
	}

	public static long indexToUid(long index) {
		return index ^ cpuSerial;
	}

	/**
	 * 解析字符串中有哪些带括号的子字符串(带括号)
	 * 
	 * @param content
	 * @return add by jia.xia
	 */
	public static List<String> parserBrackets(String content) {
		List<String> data = new ArrayList<String>();
		boolean islastLeftBracket = false;
		int length = 0;
		int end = 0;
		for (int i = 0; i < content.length(); ++i) {
			if (content.charAt(i) == '(') {
				islastLeftBracket = true;
				length = 0;
				length++;
			}
			if (content.charAt(i) == ')') {
				if (islastLeftBracket) {
					end = i;
					data.add(content.substring(end - length + 1, end + 1));
				}
			}
			if (islastLeftBracket) {
				length++;
			}
		}
		return data;
	}

	/**
	 * 根据（1-crying）or (阿俚-抱抱)来分析
	 * 
	 * @param content
	 * @return
	 */
	public static String[] parserBracketsPoints(String content) {
		CommonUtil.log("Methods", "content:" + content);
		if (content.length() > 1 && (content.length() - 1) > 0) {
			String childContent = content.substring(1, content.length() - 1);
			int index = childContent.indexOf("-");
			if (index != -1) {
				String[] str = new String[2];
				str[0] = childContent.substring(0, index);
				str[1] = childContent.substring(index + 1,
						childContent.length());
				CommonUtil.log("Methods", str[0] + "|" + str[1]
						+ "  subString:" + childContent);
				return str;
			} else {
				return null;
			}

		} else {
			return null;
		}
	}

	/**
	 * 解析括号字符串中的字符
	 * 
	 * @param content
	 * @return
	 */
	public static String parserInnerBrackets(String content) {

		boolean islastLeftBracket = false;
		int start = 0;
		int end = 0;
		String name = "";
		for (int i = 0; i < content.length(); ++i) {
			if (content.charAt(i) == '(') {
				islastLeftBracket = true;
				start = i;
			}
			if (content.charAt(i) == ')') {
				if (islastLeftBracket) {
					end = i;
					name = content.substring(start + 1, end);
				}
			}
		}
		return name;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @param isShowMess
	 *            是否要提示网络不可用的toast
	 * @return
	 */
	public static boolean checkNet(Context context, boolean isShowMess) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
		if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
			if (isShowMess) {
				Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
			}
			return false;
		} else {
			return true;
		}
	}
	/***
	 *  将毫秒值改为 yyyy-MM-dd HH:mm:ss格式
	 * @param time
	 * @return
	 */
	public static String longToString(Long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		System.out.println("TIME:::" + dateString);
		return dateString;
	}

}

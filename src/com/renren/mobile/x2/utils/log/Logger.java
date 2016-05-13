package com.renren.mobile.x2.utils.log;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.utils.SystemService;

import java.util.List;

import static android.util.Log.*;
import static java.lang.String.format;

/**
 * at 1:52 PM, 10/12/12
 *
 * @author apfro
 */
public class Logger {
	private final String tag;
	public static final boolean mDebug = true;

	@Deprecated
	public Logger() {
		StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
		if (stackTraceElements == null || stackTraceElements.length < 1) {
			tag = "Logger";
		} else {
			String className = stackTraceElements[1].getClassName();
			int nextSep = className.lastIndexOf('.');
			if (nextSep < 0) {
				tag = className;
			} else {
				tag = className.substring(nextSep + 1);
			}
		}
	}

	public Logger(Class<?> clazz) {
		assert clazz != null;
		tag = clazz.getName();
	}

	public Logger(String tag) {
		assert tag != null;
		this.tag = tag;
	}

	public static void log(String tag, int priority, String msg) {
		if (priority <= RenrenChatApplication.getLogPriority()) {
			return;
		}
		println(priority, tag, msg);
	}

	public static void log(String tag, int priority, String msg, Throwable throwable) {
		if (priority <= RenrenChatApplication.getLogPriority()) {
			return;
		}
		println(priority, tag, msg);
		if (throwable != null) {
			println(priority, tag, Log.getStackTraceString(throwable));
		}
	}

	public void print(int priority, String format, Object... params) {
		if (priority <= RenrenChatApplication.getLogPriority()) {
			return;
		}

		if (params == null || params.length == 0) {
			println(priority, tag, format);
		} else {
			println(priority, tag, String.format(format, params));
		}
	}

	public void log(int priority, String msg) {
		log(tag, priority, msg);
	}

	public void log(int priority, String msg, Throwable throwable) {
		log(tag, priority, msg, throwable);
	}

	public void v(String msg) {
		log(VERBOSE, msg);
	}

	public void v(String msg, Throwable t) {
		log(VERBOSE, msg, t);
	}

	public void printv(String format, Object... args) {
		print(VERBOSE, format, args);
	}

	public void d(String msg) {
		log(DEBUG, msg);
	}

	public void d(String msg, Throwable t) {
		log(DEBUG, msg, t);
	}

	public void printd(String format, Object... params) {
		print(DEBUG, format, params);
	}

	public void i(String msg) {
		log(INFO, msg);
	}

	public void i(String msg, Throwable t) {
		log(INFO, msg, t);
	}

	public void printi(String format, Object... args) {
		print(INFO, format, args);
	}

	public void w(String msg) {
		log(WARN, msg);
	}

	public void w(String msg, Throwable t) {
		log(WARN, msg, t);
	}

	public void printw(String format, Object... args) {
		print(WARN, format, args);
	}

	public void e(String msg) {
		log(ERROR, msg);
	}

	public void e(String msg, Throwable t) {
		log(ERROR, msg, t);
	}

	public void printe(String format, Object... args) {
		print(ERROR, format, args);
	}

	public void a(String msg) {
		log(ASSERT, msg);
	}

	public void a(String msg, Throwable t) {
		log(ASSERT, msg, t);
	}

	public void printa(String format, Object... args) {
		print(ASSERT, format, args);
	}

	/////////////////////////////////////////////////////
	// 从这开始往下不鼓励使用噻
	// 最好自己new一个Logger的实例 然后用上面的方法
	/////////////////////////////////////////////////////

	public static void net(String msg) {
		println(INFO, "net", String.format("%5d:%s\n\n", Thread.currentThread().getId(), msg));
	}

	public static void mas(String msg) {
		println(INFO, "mas", msg + "\n");
	}

	public static void pl(String key, String msg) {
		selfLog(INFO, key, 2, msg);
	}

	public static void pl(String key) {
		selfLog(INFO, key, 2);
	}

	public static void pl(String key, Object... objs) {
		selfLog(INFO, key, 2, objs);
	}

	public static void l(String msg) {
		selfLog(INFO, "wt", 2, msg);
	}

	public static void l() {
		selfLog(INFO, "wt", 2);
	}

	public static void l(Object... objs) {
		selfLog(INFO, "wt", 2, objs);
	}

	private static StackTraceElement whereCallFrom(int depth) {
		Throwable t = new Throwable();
		StackTraceElement[] stack = t.getStackTrace();
		return stack[1 + depth];
	}

	private static String formatException(Exception e) {
		return Log.getStackTraceString(e);
	}

	private static void selfLog(int log, String tag, int depth, Object... objs) {
		tag = format(tag, objs);
		try {
			if (objs != null) {
				for (int i = 0; i < objs.length; i++) {
					if (objs[i] instanceof Exception) {
						objs[i] = formatException((Exception) objs[i]);
					}
				}
			}
			StackTraceElement ori = whereCallFrom(depth);
			String result = format(
					"%d:%s.%s()->%d",
					Thread.currentThread().getId(),
					ori.getClassName().substring(
							ori.getClassName().lastIndexOf(".") + 1), ori
					.getMethodName(), ori.getLineNumber())
					+ "\n";
			if (objs != null && objs.length > 0) {
				if (objs[0] instanceof String) {
					Object[] args = new Object[objs.length];
					System.arraycopy(objs, 1, args, 0, objs.length - 1);
					String format = (String) objs[0];
					try {
						result += format(format, args);
					} catch (Exception e) {
						result += format;
					}
				} else {
					String out = "";
					for (int i = 0; i < objs.length; ++i) {
						out += "arg" + i + " = " + objs[i] + ";\t";
					}
					result += out;
				}
			}
			String logStr = result + "\n\n";
			SystemService.sDropBoxManager.addText(tag, logStr);
			println(log, tag, logStr);
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
	}


	public static final String CFTAG = "x2_cf";
	// public   final static boolean mDebug=true;
	//  public   final boolean DEBUG=false;
	// public static final byte MAS =1;
	// public static final byte MCS =2;
	// public static  byte TEST =MAS;
	// public final static byte TAG_SEND_PHOTO = 1;
	public final static String SEND_PHOTO = "send_photo";
	public final static String SEND_TEXT = "send_text";
	public final static String SEND_VOICE = "send_voice";
	public final static String GET_VOICE = "get_voice";
	public final static String RECEVICE_MESSAGE = "recevice_message";
	public final static String INPUT_BAR = "input_bar";
	public final static String PLAY_VOICE = "play_voice";
	public final static String RECORD = "record";

	public static void logd(String str) {
		if (mDebug) {
			Log.i(CFTAG, getTAG() + "---" + str);
		}
	}

	public static void logd(String tag, String str) {
		if (mDebug) {
			Log.i(tag, getTAG() + "---" + str);
		}
	}

	public static void errord(String str) {
		if (mDebug) {
			Log.e(CFTAG, getTAG() + "---" + str);
		}
	}

	public static void errord(String tag, String str) {
		if (mDebug) {
			Log.e(tag, getTAG() + "---" + str);
		}
	}

	public static void mark() {
		if (mDebug) {
			Log.w(CFTAG, getTAG());
		}
	}

	public static void mark(String str) {
		if (mDebug) {
			Log.w(CFTAG, getTAG() + "---" + str);
		}
	}

	public static void traces() {
		if (mDebug) {
			// StackTraceElement stack[] = (new Throwable()).getStackTrace();
			StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			if (stacks != null) {
				StackTraceElement ste = stacks[3];
				sb.append(ste.getClassName() + "." + ste.getMethodName() + "#line=" + ste.getLineNumber() + "的调用：\n");
				for (int i = 4; i < stacks.length && i < 15; i++) {
					ste = stacks[i];
					sb.append((i - 4) + "--" + ste.getClassName() + "." + ste.getMethodName() + "(...)#line:" +
							ste.getLineNumber() + "\n");
				}
			}
			Log.w(CFTAG, getTAG() + "--" + sb.toString());
		}

	}

	public static void traces(String tag) {
		if (mDebug) {
			// StackTraceElement stack[] = (new Throwable()).getStackTrace();
			StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			if (stacks != null) {
				StackTraceElement ste = stacks[3];
				sb.append(ste.getClassName() + "." + ste.getMethodName() + "#line=" + ste.getLineNumber() + "的调用：\n");
				for (int i = 4; i < stacks.length && i < 15; i++) {
					ste = stacks[i];
					sb.append((i - 4) + "--" + ste.getClassName() + "." + ste.getMethodName() + "(...)#line:" +
							ste.getLineNumber() + "\n");
				}
			}
			Log.w(tag, getTAG() + "--" + sb.toString());
		}

	}


	public static String getTAG() {
		// XXX this not work with proguard, maybe we cannot get the line number with a proguarded jar file.
		// I add a try/catch as a quick fixing.
		try {
			StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			if (stacks != null) {
				StackTraceElement ste = stacks[4];
				sb.append(ste.getFileName().subSequence(0, ste.getFileName().length() - 5) + "." + ste.getMethodName() +
						"#" + ste.getLineNumber());
			}
			return sb.toString();
		} catch (NullPointerException e) {
			return "PROGUARDED";
		}
	}

	public static void log(String str, byte[] bytes) {
		if (mDebug) {
			StringBuilder sb = new StringBuilder();
			sb.append(str).append('=');
			sb.append('[');
			if (bytes != null) {
				for (int i = 0; i < bytes.length; i++) {
					sb.append(Integer.toHexString(bytes[i]));
					if (i != bytes.length - 1) {
						sb.append(',');
					}
				}
			}
			sb.append(']');
			Log.i("mylog", getTAG() + "---" + sb.toString());
		}
	}


	public static void log(String str, short[] shorts) {
		if (mDebug) {
			StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			if (stacks != null) {
				StackTraceElement ste = stacks[3];
				sb.append(ste.getFileName() + "." + ste.getMethodName() + "#" + ste.getLineNumber());
			}
			String tmpTAG = sb.toString();
			sb = new StringBuilder();
			sb.append(str).append('=');
			sb.append('[');
			if (shorts != null) {
				for (int i = 0; i < shorts.length; i++) {
					// sb.append(Integer.toHexString(shorts[i]));
					sb.append(shorts[i]);
					if (i != shorts.length - 1) {
						sb.append(',');
					}
				}
			}
			sb.append(']');
			Log.i("mylog", tmpTAG + "---" + sb.toString());
		}
	}

	public static void log(String str, int[] ints) {
		if (mDebug) {
			StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			if (stacks != null) {
				StackTraceElement ste = stacks[3];
				sb.append(ste.getFileName() + "." + ste.getMethodName() + "#" + ste.getLineNumber());
			}
			String tmpTAG = sb.toString();
			sb = new StringBuilder();
			sb.append(str).append('=');
			sb.append('[');
			if (ints != null) {
				for (int i = 0; i < ints.length; i++) {
					// sb.append(Integer.toHexString(shorts[i]));
					sb.append(ints[i]);
					if (i != ints.length - 1) {
						sb.append(',');
					}
				}
			}
			sb.append(']');
			Log.i("mylog", tmpTAG + "---" + sb.toString());
		}
	}

	public static void log(String str, String[] strary) {
		if (mDebug) {
			StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			if (stacks != null) {
				StackTraceElement ste = stacks[3];
				sb.append(ste.getFileName() + "." + ste.getMethodName() + "#" + ste.getLineNumber());
			}
			String tmpTAG = sb.toString();
			sb = new StringBuilder();
			sb.append(str).append('=');
			sb.append('[');
			if (str != null) {
				for (int i = 0; i < strary.length; i++) {
					// sb.append(Integer.toHexString(shorts[i]));
					sb.append(strary[i]);
					if (i != strary.length - 1) {
						sb.append(',');
					}
				}
			}
			sb.append(']');
			Log.i("mylog", tmpTAG + "---" + sb.toString());
		}
	}

	public static void log(String str, List list) {
		if (mDebug) {
			StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			if (stacks != null) {
				StackTraceElement ste = stacks[3];
				sb.append(ste.getFileName() + "." + ste.getMethodName() + "#" + ste.getLineNumber());
			}
			String tmpTAG = sb.toString();
			sb = new StringBuilder();
			sb.append(str).append('=');
			sb.append('[');
			if (list != null) {
				int size = list.size();
				for (int i = 0; i < size; i++) {
					sb.append(list.get(i).toString());
					if (i != size - 1) {
						sb.append(',');
					}
				}
			}
			sb.append(']');
			Log.i("mylog", tmpTAG + "---" + sb.toString());
		}
	}

	public static void logStrictModeVM() {
		if (mDebug && Build.VERSION.SDK_INT >= 10) {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build());
		}
	}

	public static void logStrictModeThread() {
		if (mDebug && Build.VERSION.SDK_INT >= 10) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build());
		}
	}


}

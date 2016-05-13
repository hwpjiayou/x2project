package com.renren.mobile.x2.utils;

import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import android.widget.Toast;
import com.renren.mobile.x2.RenrenChatApplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CommonUtil {

    public static final String TAG = "SYSTEMUTIL";

    public static void toast(final String msg) {
        RenrenChatApplication.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(msg)) {
                    Toast.makeText(
                            RenrenChatApplication.getApplication(),
                            msg,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void toast(final int msgId) {
        RenrenChatApplication.getUiHandler().post(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(RenrenChatApplication.getApplication().getResources().getText(msgId))) {
                    Toast.makeText(
                            RenrenChatApplication.getApplication(),
                            RenrenChatApplication.getApplication().getResources().getText(msgId),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static void waitTime(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {}
    }

    /**
     * sleep一段时间，这段时间里，不管怎么Interrupt，都不会结束
     */
    public static void waitTimeWithoutInterrupt(final long time) {
        final long startTime = System.currentTimeMillis();
        do {
            try {
                Thread.sleep(time);
            } catch (InterruptedException ignored) {}
        } while ((System.currentTimeMillis() - startTime) <= time);
    }

    public static void log(String logMessage) {
        Log.v("cdw", logMessage);
    }

    public static void log(String logName, String logMessage) {
        Log.v(logName, logMessage);
    }


    public static void exception(String logName, Exception e, String tagName) {
        if (tagName == null) {
            return;
        }
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement ele : elements) {
            if (ele.getClassName().equals(tagName)) {
                error(logName, tagName + "的" + ele.getMethodName() + "()方法中的" + ele.getLineNumber() + "行异常:" + e);
            }
        }
    }


    public static void error(String logName, String logMessage) {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        StackTraceElement des = null;
        for (StackTraceElement e : stacks) {
            des = e;
            String className = e.getClassName();
            if (
                    !className.contains("dalvik.system.VMStack")
                            && !className.contains("java.lang.Thread")
                            && !className.contains("java.lang.Class")
                            && !className.contains("android.app.Instrumentation")
                            && !className.contains("SystemUtil")) {
                break;
            }
        }
        String pre = "";
        if (des != null) {
            int index = des.getClassName().lastIndexOf(".");
            String className = des.getClassName();
            if (index > 0) {
                className = className.substring(index);
            }
//			if(str.length>1){
            pre += className + "#" + des.getMethodName() + "()\r\n";
//			}
            Log.v(logName, "[Method]:" + pre);
        }

        Log.v(logName, "&&&E:" + logMessage);
    }

    public static void blue(String logName, String logMessage) {
        log(logName, logMessage);
    }


    public static String getProcessTime(long preTime) {
        return "" + (System.currentTimeMillis() - preTime);
    }

    public static String printStackElements() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        builder.append("堆栈为:");
        for (StackTraceElement e : elements) {
            builder.append(e.getClassName() + ":" + e.getMethodName() + "():" + e.getLineNumber() + "\r\n");
        }
        builder.append("--------------------------------------------------");
        return builder.toString();
    }

    public static String printStackElements(StackTraceElement[] elements) {
        StringBuilder builder = new StringBuilder();
        builder.append("堆栈为:");
        for (StackTraceElement e : elements) {
            builder.append(e.getClassName() + ":" + e.getMethodName() + "():" + e.getLineNumber() + "\r\n");
        }
        builder.append("--------------------------------------------------");
        return builder.toString();
    }


    private static long time = 0l;

    public static void beginTime() {
        time = System.currentTimeMillis();
    }

    public static void processTime(Object index) {
        CommonUtil.log("time", "process " + index + " :" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
    }

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
        } catch (Exception e) {} finally {
            closeQuietly(input);
            closeQuietly(output);
        }
        return result;
    }

    public static void closeQuietly(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(InputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setBoldTextView(TextView tv) {
        TextPaint paint = tv.getPaint();
        paint.setFakeBoldText(true);
    }

    public static String getMode(int measureSpec) {
        int m = MeasureSpec.getMode(measureSpec);
        switch (m) {
            case MeasureSpec.AT_MOST:
                return "AT_MOST";
            case MeasureSpec.EXACTLY:
                return "EXACTLY";
            case MeasureSpec.UNSPECIFIED:
                return "UNSPECIFIED";
            default:
                return "KNOWN";
        }
    }

    public static int getSize(int measureSpec) {
        return MeasureSpec.getSize(measureSpec);
    }


    public static String printAttrSet(AttributeSet set) {
        StringBuilder builder = new StringBuilder();
        int c = set.getAttributeCount();

        builder.append("attr set number = " + c + ",\r\n ClassAttribute="
                + set.getClassAttribute() + ",\r\nIdAttribute=" + set.getIdAttribute());
        int index = 0;
        try {
            while (index++ < c) {
                builder.append(set.getAttributeName(index) + ";\r\n");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        builder.append("------------------------------------");
        return builder.toString();
    }


}

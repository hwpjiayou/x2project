package com.renren.mobile.x2.network.talk.messagecenter.base;

import android.text.TextUtils;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import java.io.*;
import java.util.Calendar;
import java.util.Collection;

public class Utils {
    private static BufferedWriter writer;
    private static Calendar cal;
    private static int count = 0;

    private static void createLogFile() {
        try {
            cal = Calendar.getInstance();
            String time = cal.get(Calendar.DAY_OF_MONTH) + "_"
                    + cal.get(Calendar.HOUR_OF_DAY) + "_"
                    + cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND);
            File file = new File("/mnt/sdcard", String.format(
                    "/network_%s_%d.txt", time, count++));
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HttpClient createHttpClient() {
        BasicHttpParams params = new BasicHttpParams();
        params.setIntParameter(HttpConnectionParams.SO_TIMEOUT, 75000);
        params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 75000);
        params.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE, 8192);
        return new DefaultHttpClient(params);
    }

    public static HttpClient createHttpClient(int time) {
        BasicHttpParams params = new BasicHttpParams();
        params.setIntParameter(HttpConnectionParams.SO_TIMEOUT, time);
        params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, time);
        params.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE, 8192);
        return new DefaultHttpClient(params);
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
        return sb.toString();
    }

    public static void escapeXMLText(String text, StringBuilder to) {
        if (TextUtils.isEmpty(text) || to == null) {
            return;
        }

        final int length = text.length();
        for (int index = 0; index < length; index++) {
            char c = text.charAt(index);
            switch (c) {
                case '<':
                    to.append("&lt;");
                    break;
                case '>':
                    to.append("&gt;");
                    break;
                case '&':
                    to.append("&amp;");
                    break;
                case '\'':
                    to.append("&apos;");
                    break;
                case '"':
                    to.append("&quot;");
                    break;
                default:
                    to.append(c);
                    break;
            }
        }
    }

    public static String getTagName(String uri, String localName, String qName) {
        // {qName} | null(localName为空) | {localName}(uri为空) | {uri}:{localName}
        if (!TextUtils.isEmpty(qName)) {
            return qName;
        }
        if (TextUtils.isEmpty(localName)) {
            return null;
        }
        if (TextUtils.isEmpty(uri)) { // localName此时不为空
            return localName;
        }
        return String.format("%s:%s", uri, localName);
    }

    // private static void toLogFile(String logStr) {
    // Calendar cal = Calendar.getInstance();
    // String time = String.format("at %d:%d:%d \n", cal.get(Calendar.HOUR),
    // cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
    // try {
    // if (writer == null) {
    // createLogFile();
    // }else {
    // writer.write(time);
    // writer.write(logStr);
    // writer.flush();
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    /**
     * 结束Log的输出
     *
     * @parma isReCreateLog 是否重新创建Log文件
     */
    public static void stopLog(boolean isReCreateLog) {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isReCreateLog) {
            createLogFile();
        }
    }

    public static byte[] getBytes(InputStream is) {
        int len;
        int size = 4096;
        byte[] buf = null;
        try {
            if (is instanceof ByteArrayInputStream) {
                size = is.available();
                buf = new byte[size];
                len = is.read(buf, 0, size);
            } else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                buf = new byte[size];
                while ((len = is.read(buf, 0, size)) != -1)
                    bos.write(buf, 0, len);
                buf = bos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }

    public static <T extends XMPPNode> T parseString(String str, Class<T> nodeclass) throws Exception {
        if (TextUtils.isEmpty(str) || str.trim().length() == 0) {
            return null;
        }
        XMLParser<T> instance = new XMLParser<T>(nodeclass);
        instance.parse(str);
        return instance.getRoot();
    }

    public static <T extends XMPPNode> T parseStream(InputStream is, Class<T> nodeclass) throws Exception {
        XMLParser<T> instance = new XMLParser<T>(nodeclass);
        instance.parse(is);
        return instance.getRoot();
    }

    public static <U, T extends Collection<U>> void addItemWithNotify(T collection, U item) {
        synchronized (collection) {
            collection.add(item);
            collection.notifyAll();
        }
    }

}

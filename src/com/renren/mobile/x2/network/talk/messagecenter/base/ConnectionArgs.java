package com.renren.mobile.x2.network.talk.messagecenter.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: kent
 * Time:  11/7/12 11:02 AM
 */
public class ConnectionArgs {

    public HttpArgs mHttpArgs;
    public SocketArgs mSocketArgs;

    public ConnectionArgs() {
        mHttpArgs = new HttpArgs();
        mSocketArgs = new SocketArgs();
    }

    public ConnectionArgs(HttpArgs http, SocketArgs socket) {
        mHttpArgs = http;
        mSocketArgs = socket;
    }

    public ConnectionArgs(HttpArgs http) {
        this(http, new SocketArgs());
    }

    public ConnectionArgs(SocketArgs socket) {
        this(new HttpArgs(), socket);
    }

    private abstract static class AbstractArgs<K, V> {
        public HashMap<K, V> mArgs = new HashMap<K, V>();

        public AbstractArgs() {
            //使用默认值
            mArgs.putAll(getDefaultValue());
        }

        public AbstractArgs(Map.Entry<K, V>... args) {
            for (Map.Entry<K, V> arg : args) {
                mArgs.put(arg.getKey(), arg.getValue());
            }
            addOtherValue(mArgs, getDefaultValue());
        }

        protected abstract HashMap<K, V> getDefaultValue();

        public V getValue(K key) {
            return mArgs.get(key);
        }

        public V getValue(K key, V defaultValue) {
            if (mArgs.containsKey(key)) {
                return mArgs.get(key);
            }
            else {
                return defaultValue;
            }
        }

        public void setValue(K key, V value) {
            mArgs.put(key, value);
        }
    }

    public static class HttpArgs extends AbstractArgs<Integer, Integer> {
        public static final int WAITTIME = 0;//建立连接的等待时间，单位是s
        public static final int POLL_SPACE_TIME = 1;//轮循等待时间 <0表示不等待，单位是ms
        public static final int CLIENT_SO_TIME = 2;//HttpClient读超时，单位是ms
        public static final int CHANGE_APP_WAIT_TIME = 3;//当改变前后台后，多长时间才变化轮询等待时间
        public static final HashMap<Integer, Integer> sDefaultValue = new HashMap<Integer, Integer>();

        static {
            sDefaultValue.put(WAITTIME, 20);
            sDefaultValue.put(POLL_SPACE_TIME, -1);
            sDefaultValue.put(CLIENT_SO_TIME, 75000);
            sDefaultValue.put(CHANGE_APP_WAIT_TIME, 10 * 1000);
        }

        @Override
        protected HashMap<Integer, Integer> getDefaultValue() {
            return sDefaultValue;
        }
    }

    public static class SocketArgs extends AbstractArgs<Integer, Integer> {
        public static final int TIMEOUT_TIME = 0;
        public static final int POLL_SPACE_TIME = 1;
        public static final HashMap<Integer, Integer> sDefaultValue = new HashMap<Integer, Integer>();

        static {
            sDefaultValue.put(TIMEOUT_TIME, 45000);
            sDefaultValue.put(POLL_SPACE_TIME, 25000);
        }

        @Override
        protected HashMap<Integer, Integer> getDefaultValue() {
            return sDefaultValue;
        }
    }

    private static <K, V> void addOtherValue(HashMap<K, V> map, HashMap<K, V> fullMap) {
        Set<Map.Entry<K, V>> entrys = fullMap.entrySet();
        for (Map.Entry<K, V> entry : entrys) {
            if (!map.containsKey(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
    }
}

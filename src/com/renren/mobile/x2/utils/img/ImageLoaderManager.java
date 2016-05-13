package com.renren.mobile.x2.utils.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * at 下午4:22, 12-10-22
 *
 * @author afpro
 */
public class ImageLoaderManager {
    private static int getHash(Object obj, int defaultHash) {
        return obj == null ? defaultHash : obj.hashCode();
    }

    private static int getHash(Reference obj, int defaultHash) {
        return obj == null ? defaultHash : getHash(obj.get(), defaultHash);
    }

    static class CountLimit implements StrongRefImageLoader.Limits {
        final int count;
        final List<Bitmap> cache = new LinkedList<Bitmap>();

        CountLimit(int count) {
            assert count > 0;
            this.count = count;
        }

        @Override
        public synchronized void newBitmap(Bitmap bitmap) {
            assert bitmap != null;
            cache.add(bitmap);
            while (cache.size() > count && !cache.isEmpty()) {
                log.d(String.format("size %d > limit %d", cache.size(), count));
                cache.remove(0);
            }
        }
    }

    static class PixelsLimit implements StrongRefImageLoader.Limits {
        final long pixels;
        long currentPixels = 0;
        final List<Bitmap> cache = new LinkedList<Bitmap>();

        PixelsLimit(long pixels) {
            assert pixels > 0;
            this.pixels = pixels;
        }

        @Override
        public synchronized void newBitmap(Bitmap bitmap) {
            assert bitmap != null;
            cache.add(bitmap);
            currentPixels += bitmap.getWidth() * bitmap.getHeight();
            while (currentPixels > pixels && cache.size() > 1) {
                log.d(String.format("currentPixels %d > limit %d", currentPixels, pixels));
                final Bitmap bmp = cache.remove(0);
                currentPixels -= bmp.getWidth() * bmp.getHeight();
            }
        }
    }

    static class MemoryLimit implements StrongRefImageLoader.Limits {
        public final long memoryLimit;
        private long currentMemory = 0;
        final List<Bitmap> cache = new LinkedList<Bitmap>();

        MemoryLimit(long limits) {
            assert limits > 0;
            memoryLimit = limits;
        }

        @Override
        public synchronized void newBitmap(Bitmap bitmap) {
            assert bitmap != null;
            cache.add(bitmap);
            currentMemory += ImageUtil.getBitmapByteCount(bitmap);
            while (currentMemory > memoryLimit) {
                log.d(String.format("current memory %db > limit %db", currentMemory, memoryLimit));
                final Bitmap toRemove = cache.remove(0);
                currentMemory -= ImageUtil.getBitmapByteCount(toRemove);
            }
        }
    }

    static String getCacheDirByType(int type) {
        switch (type) {
            case TYPE_HEAD:
                return "head";
            case TYPE_FEED:
                return "feed";
            case TYPE_CHAT:
                return "chat";
            case TYPE_EMOTION:
                return "emotion";
            default:
                return "default";
        }
    }

    static class ImageLoaderMap {
        final Map<Integer, ImageLoader> map = new TreeMap<Integer, ImageLoader>();
        final StrongRefImageLoader.Limits commonLimits = new MemoryLimit(20 * 1024 * 1024); // 20M内存
        final StrongRefImageLoader.Limits emotionLimits = new MemoryLimit(2 * 1024 * 1024); // 2M内存

        public synchronized ImageLoader get(int type) {
            final ImageLoader got = map.get(type);
            if (got != null) {
                return got;
            }

            final ImageLoader newImageLoader = new StrongRefImageLoader(type == TYPE_EMOTION ? emotionLimits : commonLimits, getCacheDirByType(type));
            map.put(type, newImageLoader);
            return newImageLoader;
        }
    }

    public final static int TYPE_HEAD = 1;
    public final static int TYPE_FEED = 2;
    public final static int TYPE_CHAT = 3;
    public final static int TYPE_EMOTION = 4;

    private final static Set<Pair<WeakReference, ImageLoaderMap>> data = new TreeSet<Pair<WeakReference, ImageLoaderMap>>(new Comparator<Pair<WeakReference, ImageLoaderMap>>() {
        @Override
        public int compare(Pair<WeakReference, ImageLoaderMap> x, Pair<WeakReference, ImageLoaderMap> y) {
            assert x != null;
            assert y != null;
            assert x.first != null;
            assert y.first != null;
            final int xHash = getHash(x.first, 0);
            final int yHash = getHash(y.first, 0);
            return xHash - yHash;
        }
    });
    private final static ReadWriteLock lock = new ReentrantReadWriteLock();
    private final static ImageLoader headImageLoader = new StrongRefImageLoader(new CountLimit(100));
    private final static ImageLoaderMap defaultImageLoaderMap = new ImageLoaderMap();

    /**
     * 没有使用泛型的必要 使用泛型是因为直接{@code new WeakReference(o);}会有warning
     *
     * @see #TYPE_FEED
     * @see #TYPE_HEAD
     * @see #TYPE_CHAT
     */
    private static <T> ImageLoader realGet(final int type, T holder) {
        if (type == TYPE_HEAD) {
            return headImageLoader;
        }

        if (holder == null) {
            return defaultImageLoaderMap.get(type);
        }

        lock.readLock().lock();
        try {
            for (Pair<WeakReference, ImageLoaderMap> pair : data) {
                assert pair != null;
                assert pair.first != null;
                final Object obj = pair.first.get();
                if (obj != null && obj == holder) {
                    return pair.second.get(type);
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            final Iterator<Pair<WeakReference, ImageLoaderMap>> iterator = data.iterator();
            while (iterator.hasNext()) {
                final Pair<WeakReference, ImageLoaderMap> pair = iterator.next();

                assert pair != null;
                assert pair.first != null;
                final Object obj = pair.first.get();
                if (obj == null) {
                    iterator.remove();
                } else if (obj == holder) {
                    return pair.second.get(type);
                }
            }

            final ImageLoaderMap imageLoaderMap = new ImageLoaderMap();
            data.add(new Pair<WeakReference, ImageLoaderMap>(new WeakReference<T>(holder), imageLoaderMap));
            return imageLoaderMap.get(type);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @param holder 用于代表一个特定业务 比如一个界面或者一个service 在这个holder被回收的时候 返回的ImageLoader也会被回收
     * @see #TYPE_FEED
     * @see #TYPE_HEAD
     * @see #TYPE_CHAT
     */
    public static ImageLoader get(int type, Context holder) {
        return realGet(type, holder);
    }
}

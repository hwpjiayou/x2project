package com.renren.mobile.x2.utils.img;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.db.dao.DAOFactoryImpl;
import com.renren.mobile.x2.db.dao.ImageDAO;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.log.Logger;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.renren.mobile.x2.utils.img.ImageUtil.*;

/**
 * at 10:50 AM, 10/16/12
 *
 * @author apfro
 */
public class ImageLoader {
    // static classes
    static class BitmapInformation implements Comparable<Request> {
        final int type;
        final int resId;
        final String path;

        BitmapInformation(Request request) {
            assert request != null;
            type = request.type();
            resId = request.resId();

            final String path = request.path();
            this.path = path == null ? "" : path;
        }

        @Override
        public int compareTo(Request another) {
            if (another == null) {
                return 1;
            }

            if (type != another.type()) {
                return type - another.type();
            }

            switch (type) {
                case Request.TYPE_ASSET:
                case Request.TYPE_FILE:
                case Request.TYPE_HTTP:
                    final String anotherPath = another.path();
                    return anotherPath == null ? 1 : path.compareTo(anotherPath);
                case Request.TYPE_RESOURCE:
                    return resId - another.resId();
            }

            return 0;
        }
    }

    static class ImageSoftRef extends SoftReference<Bitmap> {
        final BitmapInformation info;

        public ImageSoftRef(Request request, Bitmap bitmap) {
            super(bitmap);

            assert request != null;
            assert bitmap != null;
            info = new BitmapInformation(request);
        }

        public Bitmap getValidBitmap() {
            final Bitmap bitmap = get();
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            } else {
                return bitmap;
            }
        }
    }

    static class ImageWeakRef extends WeakReference<Bitmap> {
        final BitmapInformation info;

        public ImageWeakRef(Request request, Bitmap bitmap) {
            super(bitmap);

            assert request != null;
            assert bitmap != null;
            info = new BitmapInformation(request);
        }

        public Bitmap getValidBitmap() {
            final Bitmap bitmap = get();
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            } else {
                return bitmap;
            }
        }
    }

    static class ImageWeakRefPool {
        final List<ImageWeakRef> list = new LinkedList<ImageWeakRef>();
        final ReadWriteLock lock = new ReentrantReadWriteLock();

        public void addNew(final Request request, final Bitmap bitmap) {
            if (request == null || bitmap == null) {
                return;
            }

            lock.writeLock().lock();
            try {
                final Iterator<ImageWeakRef> iterator = list.iterator();
                while (iterator.hasNext()) {
                    final ImageWeakRef ref = iterator.next();
                    if (ref.info.compareTo(request) == 0 || ref.getValidBitmap() == null) {
                        iterator.remove();
                    }
                }

                list.add(new ImageWeakRef(request, bitmap));
            } finally {
                lock.writeLock().unlock();
            }
        }

        public Bitmap get(final Request request) {
            if (request != null) {
                lock.readLock().lock();
                try {
                    for (final ImageWeakRef ref : list) {
                        if (ref.info.compareTo(request) == 0) {
                            final Bitmap bmp = ref.getValidBitmap();
                            if (bmp != null) {
                                return bmp;
                            }
                        }
                    }
                } finally {
                    lock.readLock().unlock();
                }
            }

            return null;
        }
    }

    static class ImageSoftRefPool {
        final List<ImageSoftRef> list = new LinkedList<ImageSoftRef>();
        final ReadWriteLock lock = new ReentrantReadWriteLock();

        public void addNew(final Request request, final Bitmap bitmap) {
            if (request == null || bitmap == null) {
                return;
            }

            lock.writeLock().lock();
            try {
                final Iterator<ImageSoftRef> iterator = list.iterator();
                while (iterator.hasNext()) {
                    final ImageSoftRef ref = iterator.next();
                    if (ref.info.compareTo(request) == 0 || ref.getValidBitmap() == null) {
                        iterator.remove();
                    }
                }

                list.add(new ImageSoftRef(request, bitmap));
            } finally {
                lock.writeLock().unlock();
            }
        }

        public Bitmap get(final Request request) {
            if (request != null) {
                lock.readLock().lock();
                try {
                    for (final ImageSoftRef ref : list) {
                        if (ref.info.compareTo(request) == 0) {
                            final Bitmap bmp = ref.getValidBitmap();
                            if (bmp != null) {
                                return bmp;
                            }
                        }
                    }
                } finally {
                    lock.readLock().unlock();
                }
            }

            return null;
        }
    }

    public static interface Response {
        void success(Bitmap bitmap);
        void failed();
    }

    public static interface ComplexResponse extends Response {
        void foundInMemory();
        void foundInLocalStorage();
        void startDownloading();
        void downloadFailed();
        void downloadSuccess();
    }

    public static abstract class TagResponse<T> implements Response {
        private final T tag;

        public TagResponse(T tag) {
            this.tag = tag;
        }

        @Override
        public final void success(Bitmap bitmap) {
            success(bitmap, tag);
        }

        protected abstract void success(Bitmap bitmap, T tag);
    }

    public static abstract class UiResponse implements Response {
        private static long mainThreadId = 0;
        protected WeakReference<View> toChange = null;

        public UiResponse() {
            toChange = null;
        }

        /**
         * @param toChange 会执行toChange.post方法
         * @deprecated 在某些手机上会不正常（例如dell-d43）
         */
        @Deprecated
        public UiResponse(View toChange) {
            this.toChange = toChange == null ? null : new WeakReference<View>(toChange);
        }

        @Override
        public final void success(final Bitmap bitmap) {
            final long currentThreadId = Thread.currentThread().getId();
            if (currentThreadId == mainThreadId) {
                log.d("UiResponse-success: on ui thread, call success direct");
                uiSuccess(bitmap);
            } else {
                final View toChange = this.toChange == null ? null : this.toChange.get();
                final Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        uiSuccess(bitmap);
                    }
                };
                if (toChange == null) {
                    log.d("UiResponse-success: toChange is null, call success by RenrenChatApplication.getUiHandler()");
                    RenrenChatApplication.getUiHandler().post(task);
                } else {
                    log.d("UiResponse-success: toChange is not null, call success by toChange");
                    toChange.post(task);
                }
            }
        }

        public abstract void uiSuccess(Bitmap bitmap);
    }

    public static abstract class TagUiResponse<T> extends UiResponse {
        private final T tag;

        public TagUiResponse(T tag) {
            super();
            this.tag = tag;
        }

        /**
         * @deprecated 在某些手机上会不正常（例如dell-d43）
         */
        public TagUiResponse(T tag, View toChange) {
            super(toChange);
            this.tag = tag;
        }
    }

    /**
     * 请求的的方式的实现的借口。
     * @author Think
     *
     */
    public static interface Request {
        public final static int TYPE_ASSET = 1;//从asset文件中进行加载。
        public final static int TYPE_RESOURCE = 2;//从资源文件中进行加载。
        public final static int TYPE_FILE = 3;//从file文件中进行加载。
        public final static int TYPE_HTTP = 4;//从网络进行加载。

        int type();
        int resId();
        String path();
        boolean allowDownload();
        //compartor 类的作用是对集合类进行排序，
        public final static Comparator<Request> COMPARATOR = new Comparator<Request>() {
            @Override
            public int compare(Request x, Request y) {
                if (x == null) {
                    return y == null ? 0 : -1;
                } else if (y == null) {
                    return 1;
                }

                final int tx = x.type();
                final int ty = y.type();
                if (tx != ty) {
                    return tx - ty;
                }

                switch (tx) {
                    case Request.TYPE_HTTP:
                        final int ad = (x.allowDownload() ? 1 : 0) - (y.allowDownload() ? 1 : 0);
                        if (ad != 0) {
                            return ad;
                        }
                    case Request.TYPE_ASSET:
                    case Request.TYPE_FILE:
                        final String xp = x.path();
                        final String yp = y.path();
                        final int pd = (xp == null ? 0 : xp.hashCode()) - (yp == null ? 0 : yp.hashCode());
                        if (pd != 0) {
                            return pd;
                        }
                        break;
                    case Request.TYPE_RESOURCE:
                        final int rd = x.resId() - y.resId();
                        if (rd != 0) {
                            return rd;
                        }
                        break;
                }

                return 0;
            }
        };
    }

    public static String requestToString(Request request) {
        if (request == null) {
            return "<null>";
        }

        return String.format("{type=%d, resId=%d, allowDownload=%b, path='%s'}", request.type(), request.resId(), request.allowDownload(), request.path());
    }


    public final static class HttpImageRequest implements Request {
        private final String url;
        private final boolean allowDownload;

        public HttpImageRequest(String url, boolean allowDownload) {
            assert url != null;
            this.url = url;
            this.allowDownload = allowDownload;
        }

        @Override
        public final int type() {
            return TYPE_HTTP;
        }

        @Override
        public final int resId() {
            return 0;
        }

        @Override
        public final String path() {
            return url;
        }

        @Override
        public final boolean allowDownload() {
            return allowDownload;
        }
    }

    private class NetResponse implements INetResponse {
        final String cacheDir;
        final String url;
        final Request request;
        final ImageDAO dao;

        public NetResponse(Request request, String cacheDir, ImageDAO dao) {
            this.cacheDir = cacheDir;
            url = request.path();
            this.request = request;
            this.dao = dao;
        }

        @Override
        public void response(INetRequest req, JSONObject obj) {
            byte[] data = null;
            Bitmap bmp = null;
            if (Methods.checkNoError(req, obj)) {
                try {
                    data = (byte[]) obj.get(IMG_DATA);
                    if (data == null) {
                        log.w("getHttp: data is null");
                    } else {
                        bmp = decodeByteArray(data);
                        if (bmp == null) {
                            log.w("getHttp: parse byte array fail");
                        }
                    }
                } catch (Throwable e) {
                    log.e("getHttp: parse image failed", e);
                }
            } else {
                log.w("download failed: " + obj.toString());
            }

            if (bmp == null) {
                log.d("getHttp: download|parse fail");
            } else {
                log.d("getHttp: download&parse success");
            }

            if (bmp != null) {
                // save cache
                saveGlobalCache(request, bmp);
                saveLocalCache(request, bmp);

                // save file
                final ImageModel model = new ImageModel();
                model.createTime = System.currentTimeMillis();
                model.url = url;
                model.path = String.format("%s%c%s%c%d_%d", cacheDir, File.separatorChar, ImageLoader.this.cacheDir, File.separatorChar, model.createTime, bmp.hashCode());
                model.fileSize = data.length;
                model.counter = 0;
                model.width = bmp.getWidth();
                model.height = bmp.getHeight();
                model.modifiedTime = model.createTime;

                File img = new File(model.path);
                try {
                    if (!img.exists() && !img.createNewFile()) {
                        log.w("getHttp: create cache file failed");
                    } else {
                        if (!img.canWrite()) {
                            log.w("getHttp: cache file not writable");
                        } else {
                            try {
                                final FileOutputStream stream = new FileOutputStream(img);
                                try {
                                    stream.write(data);
                                } finally {
                                    justClose(stream);
                                }
                                dao.saveAnImage(model);
                            } catch (IOException e) {
                                log.w("getHttp: save cache file failed", e);
                            }
                        }
                    }
                } catch (IOException ignored) {
                }
            }

            // callback
            synchronized (downloading) {
                int cnt = 0;
                final Iterator<Pair<String, Response>> iterator = downloading.iterator();
                final String got = request.path();
                while (iterator.hasNext()) {
                    final Pair<String, Response> pair = iterator.next();
                    final String want = pair.first;
                    if (want.compareTo(got) == 0) {
                        log.d(String.format("getHttp: got{%s} = want{%s}", got, want));
                        if (bmp == null) {
                            log.d(String.format("getHttp: exec %d response(failed)", ++cnt));
                            if (pair.second instanceof ComplexResponse) {
                                ((ComplexResponse) pair.second).downloadFailed();
                            }
                            pair.second.failed();
                        } else {
                            log.d(String.format("getHttp: exec %d response(success)", ++cnt));
                            if (pair.second instanceof ComplexResponse) {
                                ((ComplexResponse) pair.second).downloadSuccess();
                            }
                            pair.second.success(bmp);
                        }
                        iterator.remove();
                    } else {
                        log.d(String.format("getHttp: got{%s} != want{%s}", got, want));
                    }
                }
            }
        }
    }

    // class body
    protected final static Logger log = new Logger("ImageLoader");
    private final static ImageWeakRefPool weakRefPool = new ImageWeakRefPool();
    private final static Executor executor = new ThreadPoolExecutor(0, 3, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    private final static List<Pair<String, Response>> downloading = new LinkedList<Pair<String, Response>>();
    private final ImageSoftRefPool softRefPool = new ImageSoftRefPool();
    private final String cacheDir;

    ImageLoader() {
        this("default");
    }

    ImageLoader(String cacheDir) {
        assert cacheDir != null;
        this.cacheDir = cacheDir;
    }

    public Bitmap getMemoryCache(final Request request) {
        if (request == null) {
            return null;
        }

        final Bitmap gotSoft = softRefPool.get(request);
        if (gotSoft != null) {
            log.i("got image at local image pool: request" + requestToString(request));
            return gotSoft;
        }

        // check global ref
        final Bitmap gotWeak = weakRefPool.get(request);
        if (gotWeak != null) {
            log.i("got image at global image pool: request" + requestToString(request));
            softRefPool.addNew(request, gotWeak);
            return gotWeak;
        }

        // no result
        return null;
    }

    public void get(final Request request, final Response response) {
        if (request == null || response == null) {
            log.i("get: null arguments");
            return;
        }

        // check cache
        final Bitmap memoryCache = getMemoryCache(request);
        if (memoryCache != null) {
            if (response instanceof ComplexResponse) {
                ((ComplexResponse) response).foundInMemory();
            }
            response.success(memoryCache);
            return;
        }

        // schedule task
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // real worker
                switch (request.type()) {
                    case Request.TYPE_ASSET:
                        getAsset(request, response);
                        return;
                    case Request.TYPE_RESOURCE:
                        getResource(RenrenChatApplication.getApplication().getResources(), request, response);
                        return;
                    case Request.TYPE_FILE:
                        getFile(request, response);
                        return;
                    case Request.TYPE_HTTP:
                        getHttp(request, response);
                        return;
                    default:
                        log.w("get: unknown request type(" + request.type() + ')');
                        break;
                }
                response.failed();
            }
        });
    }

    private void saveLocalCache(Request request, Bitmap bitmap) {
        if (request != null && bitmap != null) {
            softRefPool.addNew(request, bitmap);
        }
    }

    private void saveGlobalCache(Request request, Bitmap bitmap) {
        if (request != null && bitmap != null) {
            weakRefPool.addNew(request, bitmap);
        }
    }

    public void getAsset(Request request, Response response) {
        assert request != null;
        assert response != null;

        try {
            final Bitmap bmp = decodeInputStream(RenrenChatApplication.getApplication().getAssets().open(request.path()));
            if (bmp != null) {
                response.success(bmp);
                saveGlobalCache(request, bmp);
                saveLocalCache(request, bmp);
                return;
            }
        } catch (Throwable throwable) {
            log.e("getAsset throws:", throwable);
        }
        response.failed();
    }

    private void getResource(Resources resources, Request request, Response response) {
        assert resources != null;
        assert request != null;
        assert response != null;

        try {
            final Bitmap bmp = decodeResource(resources, request.resId());
            if (bmp != null) {
                response.success(bmp);
                saveGlobalCache(request, bmp);
                saveLocalCache(request, bmp);
                return;
            }
        } catch (Throwable throwable) {
            log.e("getResource throws:", throwable);
        }
        response.failed();
    }

    private void getFile(Request request, Response response) {
        assert request != null;
        assert response != null;

        try {
            final Bitmap bmp = decodeFile(request.path());
            if (bmp != null) {
                response.success(bmp);
                saveGlobalCache(request, bmp);
                saveLocalCache(request, bmp);
                return;
            }
        } catch (Throwable throwable) {
            log.e("getFile throws:", throwable);
        }
        response.failed();
    }

    private Bitmap loadLocaleHttpCache(ImageDAO dao, String url) {
        final ImageModel queried = dao.queryByImageUrl(url);
        if (queried != null) {
            log.d(String.format("getHttp: got url{%s}=path{%s}", queried.url, queried.path));
            try {
                final String path = queried.path;
                if (!TextUtils.isEmpty(path)) {
                    final File localFile = new File(path);
                    if (!localFile.exists()) {
                        log.i("getHttp: cached file not exists");
                    } else if (!localFile.isFile()) {
                        log.i("getHttp: cached file is not 'file'");
                    } else if (!localFile.canRead()) {
                        log.i("getHttp: cached file not readable");
                    } else {
                        final Bitmap bmp = decodeFile(path);
                        if (bmp != null) {
                            dao.setModifiedTimeByUrl(url, System.currentTimeMillis());
                            dao.updateImageCounterByUrl(url);
                            return bmp;
                        } else {
                            log.i("getHttp: decode cached file failed");
                        }
                    }
                }
            } catch (Throwable e) {
                log.w("getHttp: get cached image failed", e);
            }
            dao.deleteByImageUrl(url);
        }
        return null;
    }

    private void getHttp(final Request request, final Response response) {
        assert request != null;
        assert response != null;

        final String url = request.path();
        assert url != null;

        // check local
        final ImageDAO dao = DAOFactoryImpl.getInstance().buildDAO(ImageDAO.class);
        assert dao != null;

        Bitmap localCached = loadLocaleHttpCache(dao, url);
        if (localCached != null) {
            saveLocalCache(request, localCached);
            if (response instanceof ComplexResponse) {
                ((ComplexResponse) response).foundInLocalStorage();
            }
            response.success(localCached);
            return;
        }

        // check downloading
        synchronized (downloading) {
            localCached = loadLocaleHttpCache(dao, url);
            if (localCached != null) {
                saveLocalCache(request, localCached);
                if (response instanceof ComplexResponse) {
                    ((ComplexResponse) response).foundInLocalStorage();
                }
                response.success(localCached);
                return;
            }

            if (!request.allowDownload()) {
                log.i("getHttp: not found, quit(downloadIfNotExist is false)");
                response.failed();
                return;
            }

            boolean has = false;
            for (final Pair<String, Response> pair : downloading) {
                if (pair.first.compareTo(url) == 0) {
                    has = true;
                    break;
                }
            }

            downloading.add(new Pair<String, Response>(url, response));
            if (has) {
                log.i("getHttp: got downloading progress, attach.");
                return;
            }


            final Context context = RenrenChatApplication.getApplication();
            if (context == null) {
                log.w("getHttp: get context failed");
                response.failed();
                return;
            }

            File imageBaseCacheDir = null;
            final File externalCacheDir = context.getExternalCacheDir();
            final File cacheDir = context.getCacheDir();
            if (externalCacheDir != null) {
                log.i("getHttp: use external cache dir(on sdcard)");
                imageBaseCacheDir = externalCacheDir;
            } else if (cacheDir != null) {
                log.w("getHttp: use cache dir(on phone memory)");
                imageBaseCacheDir = cacheDir;
            } else {
                log.e("getHttp: get cache dir failed");
                response.failed();
                return;
            }

            // download
            final File imageCacheDir = new File(imageBaseCacheDir.getAbsolutePath() + File.separator + "imgCache" + File.separator + cacheDir);
            if (!imageCacheDir.isDirectory() && !imageCacheDir.mkdirs()) {
                log.w("getHttp: open cache dir failed");
                response.failed();
                return;
            }

            log.d("getHttp: start download => " + url);
            if (response instanceof ComplexResponse) {
                ((ComplexResponse) response).startDownloading();
            }
            HttpMasService.getInstance().getImage(url, new NetResponse(request, imageCacheDir.getAbsolutePath(), dao));
        }
    }

    private static void justClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}

class StrongRefImageLoader extends ImageLoader {
    static interface Limits {
        final static Logger log = new Logger("Limits");
        void newBitmap(Bitmap bitmap);
    }

    final Limits limits;

    StrongRefImageLoader(Limits limits) {
        assert limits != null;
        this.limits = limits;
    }

    StrongRefImageLoader(Limits limits, String cacheDir) {
        super(cacheDir);
        assert limits != null;
        this.limits = limits;
    }

    @Override
    public void get(Request request, final Response response) {
        if (response == null) {
            log.d("StrongRefImageLoader-get: response is null");
            return;
        }

        if (request == null) {
            log.d("StrongRefImageLoader-get: request is null");
            response.failed();
            return;
        }

        super.get(request, new Response() {
            @Override
            public void success(Bitmap bitmap) {
                limits.newBitmap(bitmap);
                if (bitmap.isRecycled()) {
                    log.d("StrongRefImageLoader-get: bitmap is recycled");
                    response.failed();
                } else {
                    log.d("StrongRefImageLoader-get: success!");
                    response.success(bitmap);
                }
            }

            @Override
            public void failed() {
                log.d("StrongRefImageLoader-get: failed!");
                response.failed();
            }
        });
    }
}
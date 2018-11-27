package co.sisu.mobile.controllers;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import java.io.File;

import okhttp3.internal.cache.DiskLruCache;

public class CacheManager {

    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    public CacheManager(Context context) {
        File cachDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
//        new InitDiskCacheTask().execute(cacheDir);
    }

    private LruCache<String, Bitmap> mMemoryCache;



//    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
//        @Override
//        protected Void doInBackground(File... params) {
//            synchronized (mDiskCacheLock) {
//                File cacheDir = params[0];
//                mDiskLruCache.
//                mDiskLruCache = DiskLruCache.open(cacheDir, DISK_CACHE_SIZE);
//                mDiskCacheStarting = false; // Finished initialization
//                mDiskCacheLock.notifyAll(); // Wake any waiting threads
//            }
//            return null;
//        }
//    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir

        return new File(context.getCacheDir().getPath() + File.separator + uniqueName);
    }
}

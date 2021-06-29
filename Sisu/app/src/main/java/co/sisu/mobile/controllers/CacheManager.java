package co.sisu.mobile.controllers;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.nostra13.universalimageloader.cache.memory.MemoryCache;

import java.io.File;

import okhttp3.internal.cache.DiskLruCache;

public class CacheManager {

    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private MemoryCache mMemoryCache;
    private int cacheSize;

    public CacheManager() {
        initCache();
    }

    private void initCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        cacheSize = maxMemory / 4;

//        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//            @Override
//            protected int sizeOf(String key, Bitmap bitmap) {
//                // The cache size will be measured in kilobytes rather than
//                // number of items.
//                return bitmap.getByteCount() / 1024;
//            }
//        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) != null) {
            Log.e("Key already exists", "Replacing " + key);
        }
        mMemoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public CacheManager(Context context) {
        File cachDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
//        new InitDiskCacheTask().execute(cacheDir);
    }




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

    public MemoryCache getLruCache() {
        return mMemoryCache;
    }

    public int getLruCacheSize() {
        return cacheSize;
    }
}

package co.sisu.mobile.controllers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import co.sisu.mobile.R;
import co.sisu.mobile.utils.AndroidUtils;
import co.sisu.mobile.utils.LruCache;

/**
 * Created by Jeff on 6/25/2018.
 */

public class FileIO {

    Context context;

    public FileIO(Context context) {
        this.context = context;
    }

    private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(1024 * 1024 * 2) {
        @Override
        public int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }
    };

    public Bitmap getImage(String profile, int size) {
        String thumbId = ("".equals(profile) ? "__nocover_" : profile) + "_" + size;

        Bitmap thumb = mCache.get(thumbId);

        if (thumb != null) {
            return thumb;
        }

        File thumbFile = context.getDir(profile, Context.MODE_PRIVATE);

        if (thumbFile.exists()) {
            thumb = BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
            mCache.put(thumbId, thumb);
            return thumb;
        }

        if (thumbFile.exists()) {
            return BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
        }

        File originalFile = context.getDir(profile, Context.MODE_PRIVATE);
        Bitmap original = null;

        if ("".equals(profile)) {
            original = ((BitmapDrawable) context.getResources()
                    .getDrawable(R.drawable.contact_icon)).getBitmap();
        } else if (!originalFile.exists()) {
            return getImage("", size);
        } else {
            original = BitmapFactory.decodeFile(originalFile.getAbsolutePath());
        }

        if (original == null) {
            return getImage("", size);
        }

        float scale = Math.min((float) size / original.getWidth(),
                (float) size / original.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        thumb = Bitmap.createBitmap(original, 0, 0,
                original.getWidth(), original.getHeight(), matrix, true);
        mCache.put(thumbId, thumb);
        original.recycle();

        try {
            thumb.compress(Bitmap.CompressFormat.PNG, 80, new FileOutputStream(thumbFile));
        } catch (FileNotFoundException e) {
        }

        return thumb;
    }

    public boolean addImage(final String profile, byte [] bitmapData) {
        // we'll scale the image down to screen size
        // big images waste resources and crash your device

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, o);

        if (o.outHeight < 1 || o.outWidth < 1) {
            return false;
        }

        final int requiredSize = Math.min(AndroidUtils.getDisplayHeight(context),
                AndroidUtils.getDisplayWidth(context));

        int width = o.outWidth;
        int height = o.outHeight;
        int scale = 1;

        while (true) {
            if (width / 2 < requiredSize || height / 2 < requiredSize) {
                break;
            }

            width /= 2;
            height /= 2;
            scale *= 2;
        }


        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap image = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, o2);

        if (image == null) {
            return false;
        }

        try {
            File file = context.getDir(profile, Context.MODE_PRIVATE);
            file.getParentFile().mkdirs();

            File [] oldCovers = new File(context.getDir(profile, Context.MODE_PRIVATE).getAbsolutePath()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(profile);
                }
            });

            for (int i = 0; i < oldCovers.length; i++) {
                oldCovers[i].delete();
            }

            for (String k : mCache.getAvailableKeys()) {
                if (k.startsWith(profile)) {
                    mCache.remove(k);
                }
            }

            image.compress(Bitmap.CompressFormat.PNG, 80, new FileOutputStream(file));

            return true;
        } catch (FileNotFoundException e) {
            // too bad, we have a valid cover but failed to persist
            return false;
        }
    }

    public Bitmap getImageFromCache(String profile) {
        for(String s : context.fileList()) {
            Log.e("FILE", s);
        }
        Bitmap bmp = null;
        //FileInputStream fis = null;
        if(profile != null) {
            try {
                ContextWrapper cw = new ContextWrapper(context);
                File f = new File(cw.getDir(profile, Context.MODE_PRIVATE), profile);

                //fis = cw.openFileInput(profile);
                bmp = BitmapFactory.decodeStream(new FileInputStream(f));
                if(bmp != null) {
                    Log.e("BMP", bmp.toString());
                }
            }
            catch (FileNotFoundException e)
            {
                Log.e("GET IMG ERROR", "file not found");
                return null;
//                e.printStackTrace();
            } finally {
//                try {
//                    if(fis != null) {
//                        fis.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
        return bmp;
    }


    public void saveToInternalStorage(Bitmap bmp, String profile) {
        ContextWrapper cw = new ContextWrapper(context);

        FileOutputStream fos = null;
        //int size = bmp.getByteCount();
       // ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
        try {
            // Use the compress method on the BitMap object to write image to the OutputStream
            if(bmp != null) {
                //byte[] bArr = bos.toByteArray();
                //bos.flush();
                //bos.close();

                //YOU WANT THIS PART
                fos = cw.openFileOutput(profile, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Log.e("FilePath", cw.getDir(profile, Context.MODE_PRIVATE).getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package co.sisu.mobile.controllers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jeff on 6/25/2018.
 */

public class FileIO {

    Context context;

    public FileIO(Context context) {
        this.context = context;
    }

    public Bitmap getImageFromCache(String profile) {
        for(String s : context.fileList()) {
            Log.e("FILE", s);
        }
        Log.e("GET FROM CACHE Profile", profile + "");
        Bitmap bmp = null;
        FileInputStream fis = null;
        if(profile != null) {
            try {
                fis = context.openFileInput(profile);
                bmp = BitmapFactory.decodeStream(fis);
                Log.e("BMP", bmp.toString());
            }
            catch (FileNotFoundException e)
            {
                Log.e("GET IMG ERROR", "file not found");
                return null;
//                e.printStackTrace();
            } finally {
                try {
                    if(fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bmp;
    }


    public void saveToInternalStorage(byte[] bmp, String profile) {
        ContextWrapper cw = new ContextWrapper(context);

        FileOutputStream fos = null;
//        int size = bmp.getByteCount();
//        ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
        try {
            // Use the compress method on the BitMap object to write image to the OutputStream
            if(bmp != null) {
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
//                byte[] bArr = bos.toByteArray();
//                bos.flush();
//                bos.close();

                //YOU WANT THIS PART
//                fos = cw.openFileOutput(profile, Context.MODE_PRIVATE);
//                fos.write(bmp);
//                fos.flush();
//                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

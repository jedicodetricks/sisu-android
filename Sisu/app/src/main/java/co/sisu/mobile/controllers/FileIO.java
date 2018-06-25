package co.sisu.mobile.controllers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
        Bitmap bmp = null;
        FileInputStream fis = null;
        if(profile != null) {
            try {
                fis = context.openFileInput(profile);
                bmp = BitmapFactory.decodeStream(fis);
            }
            catch (FileNotFoundException e)
            {
                Log.e("GET IMG ERROR", "file not found");
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bmp;
    }


    public void saveToInternalStorage(Bitmap bmp, String profile) {
        ContextWrapper cw = new ContextWrapper(context);

        FileOutputStream fos = null;
        try {
            fos = cw.openFileOutput(profile, Context.MODE_PRIVATE);
            // Use the compress method on the BitMap object to write image to the OutputStream
            if(bmp != null) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
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

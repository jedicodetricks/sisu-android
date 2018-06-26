package co.sisu.mobile.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.models.LeaderboardItemsObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 6/25/2018.
 */

public class LeaderboardImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;
    private LeaderboardItemsObject currentChild;
    private String agentId;

    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private String transactionID;
    private String timestamp;
    private String jwtStr;

    public LeaderboardImageTask(LeaderboardItemsObject currentChild, ImageView imageView, String agentId) {
        this.imageView = imageView;
        this.currentChild = currentChild;
        this.agentId = agentId;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    private Bitmap downloadBitmap(String profile) {
        Response response = null;
        getJWT();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://api.sisu.co/api/v1/image/" + profile)
                .get()
                .addHeader("Authorization", jwtStr)
                .addHeader("Client-Timestamp", timestamp)
                .addHeader("Transaction-Id", transactionID)
                .build();
        Bitmap bitmap;
        try {
            response = client.newCall(request).execute();
//            Log.e("PROFILE PIC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
//                AsyncProfileImageJsonObject profileObject = new AsyncProfileImageJsonObject();
                InputStream inputStream = response.body().byteStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
//                profileObject.setData(String.valueOf(response.body().charStream()));
//                Log.e("Profile", profileObject.getData().toString() + "");
//                profileObject.setFilename(profile);
//                callback.onEventCompleted(bitmap, profile);
            }
            else {
                return null;
//                callback.onEventFailed(null, "Leaderboard Image");
            }
        }
        else {
            return null;
//            callback.onEventFailed(null, "Leaderboard Image");
        }

        response.body().close();
        return bitmap;

    }

    public void getJWT() {
        transactionID = UUID.randomUUID().toString();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, -60);
        timestamp = String.valueOf(date.getTimeInMillis());

        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 1);

        jwtStr = Jwts.builder()
                .claim("Client-Timestamp", timestamp)
                .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
                .setIssuedAt(date.getTime())
                .setExpiration(expDate.getTime())
                .claim("Transaction-Id", transactionID)
                .claim("agent_id", agentId)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        Log.e("IMAGEVIEW REFERENCE", imageView.toString());
//        if (currentChild.getImage() != null) {
        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.contact_icon);
        imageView.setImageDrawable(placeholder);
//            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    currentChild.setImage(bitmap);
                } else {
                    imageView.setImageDrawable(placeholder);
                }
//            }
//        }
    }
}

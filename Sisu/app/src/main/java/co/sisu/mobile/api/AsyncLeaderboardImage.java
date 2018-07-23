package co.sisu.mobile.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncProfileImageJsonObject;
import co.sisu.mobile.models.LeaderboardAgentModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jeff on 6/20/2018.
 */

public class AsyncLeaderboardImage extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private String profile;
    private String url;
    private LeaderboardAgentModel leaderboardAgentModel;

    public AsyncLeaderboardImage(AsyncServerEventListener cb, String url, LeaderboardAgentModel leaderboardAgentModel) {
        callback = cb;
        this.profile = profile;
        this.url = url;
        this.leaderboardAgentModel = leaderboardAgentModel;
    }

    @Override
    protected String doInBackground(String... strings) {

        Response response = null;
        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url + "api/v1/image/" + leaderboardAgentModel.getProfile())
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();
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
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                profileObject.setData(String.valueOf(response.body().charStream()));\
                if(bitmap != null) {
                    Log.e("bmp response", bitmap.toString());
                    leaderboardAgentModel.setBitmap(bitmap);
                }

//                profileObject.setFilename(profile);
                callback.onEventCompleted(leaderboardAgentModel, "Leaderboard Image");
            }
            else {
                callback.onEventFailed(null, "Leaderboard Image");
            }
        }
        else {
            callback.onEventFailed(null, "Leaderboard Image");
        }

        response.body().close();
        return null;
    }
}


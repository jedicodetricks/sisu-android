package co.sisu.mobile.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.LeaderboardAgentModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jeff on 6/20/2018.
 */

public class AsyncLeaderboardImage extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private String url;
    private LeaderboardAgentModel leaderboardAgentModel;

    public AsyncLeaderboardImage(AsyncServerEventListener cb, String url, LeaderboardAgentModel leaderboardAgentModel) {
        callback = cb;
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

        Log.e("IMAGE", leaderboardAgentModel.getImageUrl());
        Request request = new Request.Builder()
                .url(url + "api/v1/image/" + leaderboardAgentModel.getImageUrl())
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();
        String responseString = "";
        try {
            response = client.newCall(request).execute();
//            responseString = response.body().string();
//            Log.e("PROFILE PIC", responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if(bitmap != null) {
                    Log.e("bmp response", bitmap.toString());
                    leaderboardAgentModel.setImage(bitmap);
                }

//                profileObject.setFilename(profile);
                callback.onEventCompleted(leaderboardAgentModel, ApiReturnType.GET_LEADERBOARD_IMAGE);
            }
            else {
                callback.onEventFailed(null, ApiReturnType.GET_LEADERBOARD_IMAGE);
            }
        }
        else {
            callback.onEventFailed(null, ApiReturnType.GET_LEADERBOARD_IMAGE);
        }

        response.body().close();
        return null;
    }
}


package co.sisu.mobile.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class AsyncLeaderboardImage {
    private AsyncServerEventListener callback;
    private LeaderboardAgentModel leaderboardAgentModel;

    public AsyncLeaderboardImage(AsyncServerEventListener cb, LeaderboardAgentModel leaderboardAgentModel) {
        callback = cb;
        this.leaderboardAgentModel = leaderboardAgentModel;
    }

    public String execute(String jwt, String time, String trans) {

        Response response = null;
        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://api.sisu.co/api/v1/image/" + leaderboardAgentModel.getProfile())
                .get()
                .addHeader("Authorization", jwt)
                .addHeader("Client-Timestamp", time)
                .addHeader("Transaction-Id", trans)
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
//                profileObject.setData(String.valueOf(response.body().charStream()));
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


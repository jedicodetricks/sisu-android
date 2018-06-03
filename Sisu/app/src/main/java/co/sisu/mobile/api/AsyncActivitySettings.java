package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncActivitySettings extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private String agentId;

    public AsyncActivitySettings(AsyncServerEventListener cb, String agentId) {
        callback = cb;
        this.agentId = agentId;
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
                .url("https://api.sisu.co/api/v1/parameter/edit-parameter/2/"+ agentId +"/record_activities")
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();

        String responseBody = "";
        try {
            response = client.newCall(request).execute();
            responseBody = response.body().string();
            Log.e("ACTIVITY SETTINGS", responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncActivitySettingsJsonObject settings = gson.fromJson(responseBody, AsyncActivitySettingsJsonObject.class);
                callback.onEventCompleted(settings, "Activity Settings");
            }
            else {
                callback.onEventFailed(null, "Activity Settings");
            }
        }
        else {
            callback.onEventFailed(null, "Activity Settings");
        }

//        Log.d("ASYNC PING IS", "NULL");
//        response.body().close();
        return null;
    }
}

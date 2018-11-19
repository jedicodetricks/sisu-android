package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncActivitySettings extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private String agentId;
    private String url;
    private int marketId;

    public AsyncActivitySettings(AsyncServerEventListener cb, String url, String agentId, int marketId) {
        callback = cb;
        this.agentId = agentId;
        this.url = url;
        this.marketId = marketId;
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
                .url(url + "api/v1/agent/record-activities/"+ agentId + "/" + marketId)
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

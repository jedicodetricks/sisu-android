package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncLabelsJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncLabels extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String teamId;
    private String url;

    public AsyncLabels (AsyncServerEventListener cb, String url, String teamId) {
        callback = cb;
        this.teamId = teamId;
        this.url = url;
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
                .url(url + "api/v1/team/market/" + teamId + "/" + Locale.getDefault().toString())
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.d("ASYNC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncLabelsJsonObject labelObject = gson.fromJson(response.body().charStream(), AsyncLabelsJsonObject.class);

                callback.onEventCompleted(labelObject, "Get Labels");
            }
            else {
                callback.onEventFailed(null, "Get Labels");
            }
        }
        else {
            callback.onEventFailed(null, "Get Labels");
        }

//        Log.d("ASYNC PING IS", "NULL");
        response.body().close();
        return null;
    }
}

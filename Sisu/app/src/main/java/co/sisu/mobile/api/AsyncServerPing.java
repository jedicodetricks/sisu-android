package co.sisu.mobile.api;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.JWTObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 3/21/2018.
 */

public class AsyncServerPing extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    JWTObject jwt;

    public AsyncServerPing (AsyncServerEventListener cb) {
       callback = cb;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("http://staging.sisu.co/api/v1/ping")
                .get()
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.d("ASYNC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                callback.onEventCompleted(null, "Server Ping");
            }
            else {
                callback.onEventFailed(null, "Server Ping");
            }
        }
        else {
            callback.onEventFailed(null, "Server Ping");
        }

//        Log.d("ASYNC PING IS", "NULL");
        response.body().close();
        return null;
    }
}

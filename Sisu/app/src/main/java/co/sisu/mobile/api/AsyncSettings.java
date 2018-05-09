package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/17/18.
 */

public class AsyncSettings extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    private String agentId;
    JWTObject jwt;

    public AsyncSettings(AsyncServerEventListener cb, String agentId, JWTObject JwtObject) {
        callback = cb;
        this.agentId = agentId;
        jwt = JwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;
        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("http://staging.sisu.co/api/v1/parameter/get-parameters/2/" + agentId)
                .get()
                .addHeader("Authorization", jwt.getJwt())
                .addHeader("Client-Timestamp", jwt.getTimestamp())
                .addHeader("Transaction-Id", jwt.getTransId())
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.e("SETTINGS", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncSettingsJsonObject settings = gson.fromJson(response.body().charStream(), AsyncSettingsJsonObject.class);
                callback.onEventCompleted(settings, "Settings");
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

package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/17/18.
 */

public class AsyncSettings extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private String agentId;
    private String url;

    public AsyncSettings(AsyncServerEventListener cb, String url, String agentId) {
        callback = cb;
        this.agentId = agentId;
        this.url = url;
    }

    @Override
    protected String doInBackground(String...strings) {
        Response response = null;
        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url + "api/v1/parameter/get-parameters/2/" + agentId)
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
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
                callback.onEventFailed(null, "Settings");
            }
        }
        else {
            callback.onEventFailed(null, "Settings");
        }

//        Log.d("ASYNC PING IS", "NULL");
        response.body().close();
        return null;
    }
}

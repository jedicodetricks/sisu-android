package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncActivitySettings extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    private String agentId;
    private JWTObject jwtObject;

    public AsyncActivitySettings(AsyncServerEventListener cb, String agentId, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agentId;
        this.jwtObject = jwtObject;
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
                .url("http://staging.sisu.co/api/v1/parameter/edit-parameter/2/"+ agentId +"/record_activities")
                .get()
                .addHeader("Authorization", jwtObject.getJwt())
                .addHeader("Client-Timestamp", jwtObject.getTimestamp())
                .addHeader("Transaction-Id", jwtObject.getTransId())
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.e("SETTINGS", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncActivitySettingsJsonObject settings = gson.fromJson(response.body().charStream(), AsyncActivitySettingsJsonObject.class);
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
        response.body().close();
        return null;
    }
}

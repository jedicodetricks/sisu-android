package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncProfileImageJsonObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncProfileImage extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    private String agentId;
    private JWTObject jwtObject;

    public AsyncProfileImage(AsyncServerEventListener cb, String agentId, JWTObject jwtObject) {
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
                .url("http://staging.sisu.co/api/v1/image/3/" + agentId)
                .get()
                .addHeader("Authorization", jwtObject.getJwt())
                .addHeader("Client-Timestamp", jwtObject.getTimestamp())
                .addHeader("Transaction-Id", jwtObject.getTransId())
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.e("PROFILE PIC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncProfileImageJsonObject profileObject = gson.fromJson(response.body().charStream(), AsyncProfileImageJsonObject.class);
                callback.onEventCompleted(profileObject, "Profile Image");
            }
            else {
                callback.onEventFailed(null, "Profile Image");
            }
        }
        else {
            callback.onEventFailed(null, "Profile Image");
        }

        response.body().close();
        return null;
    }
}

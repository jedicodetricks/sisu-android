package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncUpdateProfileImageJsonObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncUpdateProfileImage extends AsyncTask<Void, Void, Void> {

    private AsyncServerEventListener callback;
    JWTObject jwt;
    AsyncUpdateProfileImageJsonObject updateProfileImageModel;

    public AsyncUpdateProfileImage(AsyncServerEventListener cb, AsyncUpdateProfileImageJsonObject asyncUpdateProfileImageJsonObject, JWTObject JwtObject) {
        this.callback = cb;
        this.updateProfileImageModel = asyncUpdateProfileImageJsonObject;
        jwt = JwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = gson.toJson(updateProfileImageModel);
            Log.e("POST IMAGE", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/v1/image")
                    .put(body)
                    .addHeader("Authorization", jwt.getJwt())
                    .addHeader("Client-Timestamp", jwt.getTimestamp())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", jwt.getTransId())
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("UPDATE SETTINGS", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Settings");
                } else {
                    callback.onEventFailed(null, "Server Ping");
                }
            } else {
                callback.onEventFailed(null, "Server Ping");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

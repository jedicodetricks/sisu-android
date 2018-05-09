package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncUpdateClients extends AsyncTask<Void, Void, Void> {

    private AsyncServerEventListener callback;
    private String clientId;
    JWTObject jwt;
    ClientObject clientObject;

    public AsyncUpdateClients(AsyncServerEventListener cb, ClientObject client, JWTObject JwtObject) {
        callback = cb;
        this.clientId = client.getClient_id();
        this.clientObject = client;
        jwt = JwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .create();
            String jsonInString = gson.toJson(clientObject);
            Log.e("POST CLIENT", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/v1/client/edit-client/" + clientId)
                    .put(body)
                    .addHeader("Authorization", jwt.getJwt())
                    .addHeader("Client-Timestamp", jwt.getTimestamp())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", jwt.getTransId())
                    .build();

            try {
                response = client.newCall(request).execute();
                Log.e("UPDATE ACTIVITIES", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Client");
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

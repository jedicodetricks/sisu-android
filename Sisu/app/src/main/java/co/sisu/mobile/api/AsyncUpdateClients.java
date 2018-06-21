package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncUpdateClients extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String clientId;
    ClientObject clientObject;

    public AsyncUpdateClients(AsyncServerEventListener cb, ClientObject client) {
        callback = cb;
        this.clientId = client.getClient_id();
        this.clientObject = client;
    }

    @Override
    protected String doInBackground(String... strings) {

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
                    .url("https://api.sisu.co/api/v1/client/edit-client/" + clientId)
                    .put(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
                Log.e("POST CLIENT", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Client");
                } else {
                    callback.onEventFailed(null, "Update Client");
                }
            } else {
                callback.onEventFailed(null, "Update Client");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

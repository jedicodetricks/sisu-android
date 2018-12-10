package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import co.sisu.mobile.models.ClientObject;
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
    private ClientObject clientObject;
    private String url;

    public AsyncUpdateClients(AsyncServerEventListener cb, String url, ClientObject client) {
        callback = cb;
        this.clientId = client.getClient_id();
        this.clientObject = client;
        this.url = url;
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
            Log.e("PUT CLIENT", jsonInString);
            jsonInString = jsonInString.replace("\"is_priority\":\"1\"", "\"is_priority\":true");
            jsonInString = jsonInString.replace("\"is_priority\":\"0\"", "\"is_priority\":false");

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url(url + "api/v1/client/edit-client/" + clientId)
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

package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncClientJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncClients extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String agentId;
    private String url;
    private int marketId;

    public AsyncClients(AsyncServerEventListener cb, String url, String agentId, int marketId) {
        callback = cb;
        this.agentId = agentId;
        this.url = url;
        this.marketId = marketId;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            Request request = new Request.Builder()
                    .url(url + "api/v2/agent/get-clients/" + agentId + "/" + marketId)
                    .get()
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("CLIENTS", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    AsyncClientJsonObject clientObject = gson.fromJson(response.body().charStream(), AsyncClientJsonObject.class);
                    callback.onEventCompleted(clientObject, "Clients");
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
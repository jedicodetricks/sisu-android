package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncFirebaseDeviceJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncGetFirebaseDevices extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String url;
    private String agentId;

    public AsyncGetFirebaseDevices(AsyncServerEventListener cb, String url, String agentId) {
        callback = cb;
        this.url = url;
        this.agentId = agentId;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url + "api/v1/agent/device/" + agentId)
                    .get()
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("Get Firebase Device", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    AsyncFirebaseDeviceJsonObject asyncFirebaseDeviceJsonObject = gson.fromJson(response.body().charStream(), AsyncFirebaseDeviceJsonObject.class);
                    callback.onEventCompleted(asyncFirebaseDeviceJsonObject, "Get Firebase Device");
                } else {
                    callback.onEventFailed(null, "Get Firebase Device");
                }
            } else {
                callback.onEventFailed(null, "Get Firebase Device");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

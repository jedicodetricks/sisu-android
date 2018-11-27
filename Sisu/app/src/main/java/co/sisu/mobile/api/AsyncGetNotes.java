package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncNotesJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncGetNotes extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String clientId;
    private String url;

    public AsyncGetNotes(AsyncServerEventListener cb, String url, String clientId) {
        callback = cb;
        this.clientId = clientId;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            Request request = new Request.Builder()
                    .url(url + "api/v1/client/logs/" + clientId)
                    .get()
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("GET NOTES", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    AsyncNotesJsonObject asyncNotesJsonObject = gson.fromJson(response.body().charStream(), AsyncNotesJsonObject.class);
                    callback.onEventCompleted(asyncNotesJsonObject, "Get Notes");
                } else {
                    callback.onEventFailed(null, "Get Notes");
                }
            } else {
                callback.onEventFailed(null, "Get Notes");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

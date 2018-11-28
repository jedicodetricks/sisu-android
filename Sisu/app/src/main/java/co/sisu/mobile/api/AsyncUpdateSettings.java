package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/23/2018.
 */

public class AsyncUpdateSettings extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String agentId;
    private AsyncUpdateSettingsJsonObject updateSettingsModel;
    private String url;

    public AsyncUpdateSettings(AsyncServerEventListener cb, String url, String agentId, AsyncUpdateSettingsJsonObject updateSettingsModel) {
        callback = cb;
        this.agentId = agentId;
        this.updateSettingsModel = updateSettingsModel;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = gson.toJson(updateSettingsModel);
            Log.e("POST SETTINGS", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url(url + "api/v1/parameter/edit-parameter")
                    .put(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
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
                    callback.onEventFailed(null, "Update Settings");
                }
            } else {
                callback.onEventFailed(null, "Update Settings");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

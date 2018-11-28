package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncUpdateProfile extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String agentId;
    private HashMap<String, String> changedFields;
    private String url;

    public AsyncUpdateProfile(AsyncServerEventListener cb, String url, String agentId, HashMap<String, String> changedFields) {
        this.callback = cb;
        this.agentId = agentId;
        this.changedFields = changedFields;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            String jsonString = "{";
            Gson gson = new Gson();
            int counter = 0;
            for ( String key : changedFields.keySet() ) {
                String value = changedFields.get(key);
                jsonString += "\"" + key + "\":\"" + value + "\"";
                if(counter < changedFields.size() - 1) {
                    jsonString += ",";
                }
                counter++;
            }
            jsonString += "}";
            Log.e("POST PROFILE", jsonString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonString);

            Request request = new Request.Builder()
                    .url(url + "api/v1/agent/edit-agent/"+ agentId)
                    .put(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
                Log.e("UPDATE SETTINGS", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Profile");
                } else {
                    callback.onEventFailed(null, "Update Profile");
                }
            } else {
                callback.onEventFailed(null, "Update Profile");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

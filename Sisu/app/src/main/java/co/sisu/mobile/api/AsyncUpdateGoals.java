package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;
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

public class AsyncUpdateGoals extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String agentId;
    AsyncUpdateAgentGoalsJsonObject updateAgentGoalsJsonObject;

    public AsyncUpdateGoals(AsyncServerEventListener cb, String agentId, AsyncUpdateAgentGoalsJsonObject updateAgentGoalsJsonObject) {
        callback = cb;
        this.agentId = agentId;
        this.updateAgentGoalsJsonObject = updateAgentGoalsJsonObject;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = gson.toJson(updateAgentGoalsJsonObject);
            Log.e("POST GOALS", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url("https://api.sisu.co/api/v1/agent/goal/" + agentId)
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
                    callback.onEventCompleted(null, "Update Goals");
                } else {
                    callback.onEventFailed(null, "Update Goals");
                }
            } else {
                callback.onEventFailed(null, "Update Goals");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

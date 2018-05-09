package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.JWTObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 5/5/2018.
 */

public class AsyncUpdateAgent extends AsyncTask<Void, Void, Void> {

    private AsyncServerEventListener callback;
    private String agentId;
    private String income;
    private String reason;
    JWTObject jwt;

    public AsyncUpdateAgent(AsyncServerEventListener cb, String agentId, String income, String reason, JWTObject JwtObject) {
        callback = cb;
        this.agentId = agentId;
        this.income = income;
        this.reason = reason;
        jwt = JwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = "";
            if(!income.equals("") && !reason.equals("")) {
                jsonInString = "{\"vision_statement\":\"" + reason + "\", \"desired_income\":\"" + income + "\"}";
            }
            else if(!reason.equals("")) {
                jsonInString = "{\"vision_statement\":\"" + reason + "\"}";
            }
            else if(!income.equals("")){
                jsonInString = "{\"desired_income\":\"" + income + "\"}";
            }

            Log.e("POST AGENT", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/v1/agent/edit-agent/" + agentId)
                    .put(body)
                    .addHeader("Authorization", jwt.getJwt())
                    .addHeader("Client-Timestamp", jwt.getTimestamp())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", jwt.getTransId())
                    .build();

            try {
                response = client.newCall(request).execute();
                Log.e("UPDATE SETTINGS", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Agent");
                } else {
                    callback.onEventFailed(null, "Update Agent");
                }
            } else {
                callback.onEventFailed(null, "Update Agent");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}


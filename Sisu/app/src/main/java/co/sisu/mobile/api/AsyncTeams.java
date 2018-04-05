package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import co.sisu.mobile.models.AsyncTeamsJsonObject;
import co.sisu.mobile.models.TeamJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/4/18.
 */

public class AsyncTeams extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    String agentId;

    public AsyncTeams (AsyncServerEventListener cb, String agentId) {
        callback = cb;
        this.agentId = agentId;
    }

    //Test: bg@test.com asdf123
    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;

//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"email\":\"" + email +"\", \"first_name\":\"" + firstName + "\", \"last_name\":\"" + lastName + "\", \"password\":\""+ password +"\"}");

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url("http://staging.sisu.co/api/agent/get-teams/" + agentId)
                .get()
                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                .addHeader("Client-Timestamp", "1520999095")
                .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.d("ASYNC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response.code() == 200) {
            AsyncTeamsJsonObject teams = gson.fromJson(response.body().charStream(), AsyncTeamsJsonObject.class);
            callback.onEventCompleted(teams);
        }
        else {
            callback.onEventFailed();
        }
        response.body().close();
        return null;
    }
}
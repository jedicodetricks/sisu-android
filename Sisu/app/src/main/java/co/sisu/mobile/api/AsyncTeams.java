package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncTeamsJsonObject;
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

    @Override
    protected Void doInBackground(Void... voids) {
        Response teamsResponse = null;

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request teamsRequest = new Request.Builder()
                .url("http://staging.sisu.co/api/agent/get-teams/" + agentId)
                .get()
                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                .addHeader("Client-Timestamp", "1520999095")
                .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                .build();

        try {
            teamsResponse = client.newCall(teamsRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(teamsResponse.code() == 200) {
            AsyncTeamsJsonObject teams = gson.fromJson(teamsResponse.body().charStream(), AsyncTeamsJsonObject.class);
            callback.onEventCompleted(teams, "Teams");
        }
        else {
            callback.onEventFailed(null, "Teams");
        }
        teamsResponse.body().close();
        return null;
    }
}
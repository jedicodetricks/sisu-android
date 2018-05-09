package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncTeamsJsonObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/4/18.
 */

public class AsyncTeams extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    String agentId;
    JWTObject jwt;

    public AsyncTeams (AsyncServerEventListener cb, String agentId, JWTObject JwtObject) {
        callback = cb;
        this.agentId = agentId;
        jwt = JwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Response teamsResponse = null;

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request teamsRequest = new Request.Builder()
                .url("http://staging.sisu.co/api/v1/agent/get-teams/" + agentId)
                .get()
                .addHeader("Authorization", jwt.getJwt())
                .addHeader("Client-Timestamp", jwt.getTimestamp())
                .addHeader("Transaction-Id", jwt.getTransId())
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
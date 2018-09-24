package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import co.sisu.mobile.models.AsyncTeamsJsonObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/4/18.
 */

public class AsyncTeams extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private String agentId;
    private String url;

    public AsyncTeams (AsyncServerEventListener cb, String url, String agentId) {
        callback = cb;
        this.agentId = agentId;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {
        Response teamsResponse = null;

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request teamsRequest = new Request.Builder()
                .url(url + "api/v1/agent/get-teams/" + agentId)
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();

        try {
            teamsResponse = client.newCall(teamsRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(teamsResponse != null) {
            if(teamsResponse.code() == 200) {
                AsyncTeamsJsonObject teams = gson.fromJson(teamsResponse.body().charStream(), AsyncTeamsJsonObject.class);
                callback.onEventCompleted(teams, "Teams");
            }
            else {
                callback.onEventFailed(null, "Teams");
            }
            teamsResponse.body().close();
        }
        else {
            callback.onEventFailed(null, "Teams");
        }

        return null;
    }
}
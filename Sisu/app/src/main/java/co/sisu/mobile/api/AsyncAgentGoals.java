package co.sisu.mobile.api;

import android.os.AsyncTask;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/13/18.
 */

public class AsyncAgentGoals extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String agentId;
    private String url;
    private int teamId;

    public AsyncAgentGoals(AsyncServerEventListener cb, String url, String agentId, int teamId) {
        callback = cb;
        this.agentId = agentId;
        this.url = url;
        this.teamId = teamId;
    }

    @Override
    protected String doInBackground(String... strings) {
        Response response = null;
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url(url + "api/v2/agent/get-goals/"+ agentId + "/" + teamId)
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.e("RESPONSE", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncGoalsJsonObject goals = gson.fromJson(response.body().charStream(), AsyncGoalsJsonObject.class);
                callback.onEventCompleted(goals, "Goals");
            }
            else {
                callback.onEventFailed(null, "Goals");
            }
        }
        else {
            callback.onEventFailed(null, "Goals");
        }


        return null;
    }
}

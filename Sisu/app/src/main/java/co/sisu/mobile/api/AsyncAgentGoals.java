package co.sisu.mobile.api;

import android.os.AsyncTask;
import com.google.gson.Gson;
import java.io.IOException;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/13/18.
 */

public class AsyncAgentGoals extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    private String agentId;

    public AsyncAgentGoals(AsyncServerEventListener cb, String agentId) {
        callback = cb;
        this.agentId = agentId;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url("http://staging.sisu.co/api/v1/agent/get-goals/"+ agentId)
                .get()
                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                .addHeader("Client-Timestamp", "1520999095")
                .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.e("RESPONSE", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response.code() == 200) {
            AsyncGoalsJsonObject goals = gson.fromJson(response.body().charStream(), AsyncGoalsJsonObject.class);
            callback.onEventCompleted(goals, "Goals");
        }
        else {
            callback.onEventFailed(null, "Goals");
        }

        response.body().close();
        return null;
    }
}

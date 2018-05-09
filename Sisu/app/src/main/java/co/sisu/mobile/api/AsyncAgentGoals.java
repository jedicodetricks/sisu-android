package co.sisu.mobile.api;

import android.os.AsyncTask;
import com.google.gson.Gson;
import java.io.IOException;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/13/18.
 */

public class AsyncAgentGoals extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    private String agentId;
    private JWTObject jwtObject;

    public AsyncAgentGoals(AsyncServerEventListener cb, String agentId, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agentId;
        this.jwtObject = jwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url("http://staging.sisu.co/api/v1/agent/get-goals/"+ agentId)
                .get()
                .addHeader("Authorization", jwtObject.getJwt())
                .addHeader("Client-Timestamp", jwtObject.getTimestamp())
                .addHeader("Transaction-Id", jwtObject.getTransId())
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

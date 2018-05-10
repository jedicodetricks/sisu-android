package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import java.io.IOException;
import co.sisu.mobile.models.AsyncLeaderboardJsonObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncLeaderboardStats extends AsyncTask<Void, Void, Void> {

    private AsyncServerEventListener callback;
    private String teamId;
    private String searchYear;
    private String searchMonth;
    private JWTObject jwtObject;

    public AsyncLeaderboardStats (AsyncServerEventListener cb, String teamId, String searchYear, String searchMonth, JWTObject jwtObject) {
        callback = cb;
        this.teamId = teamId;
        this.searchYear = searchYear;
        this.searchMonth = searchMonth;
        this.jwtObject = jwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            String url = "http://staging.sisu.co/api/v1/team/leaderboards/" + teamId + "/" + searchYear;
            if(!searchMonth.equals("")) {
                url = "http://staging.sisu.co/api/v1/team/leaderboards/" + teamId + "/" + searchYear + "/" + searchMonth;
            }

            Log.e("LEADERBOARD URL", url);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", jwtObject.getJwt())
                    .addHeader("Client-Timestamp", jwtObject.getTimestamp())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", jwtObject.getTransId())
                    .build();

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    AsyncLeaderboardJsonObject leaderboardObject = gson.fromJson(response.body().charStream(), AsyncLeaderboardJsonObject.class);
                    callback.onEventCompleted(leaderboardObject, "Leaderboard");
                } else {
                    callback.onEventFailed(null, "Leaderboard");
                }
            } else {
                callback.onEventFailed(null, "Leaderboard");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import java.io.IOException;
import co.sisu.mobile.models.AsyncLeaderboardJsonObject;
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

    public AsyncLeaderboardStats (AsyncServerEventListener cb, String teamId, String searchYear, String searchMonth) {
        callback = cb;
        this.teamId = teamId;
        this.searchYear = searchYear;
        this.searchMonth = searchMonth;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            String url = "http://staging.sisu.co/api/team/leaderboards/" + teamId + "/" + searchYear;
            if(!searchMonth.equals("")) {
                url = "http://staging.sisu.co/api/team/leaderboards/" + teamId + "/" + searchYear + "/" + searchMonth;
            }

            Log.e("LEADERBOARD URL", url);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                    .addHeader("Client-Timestamp", "1520999095")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
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
                    callback.onEventFailed(null, "Server Ping");
                }
            } else {
                callback.onEventFailed(null, "Server Ping");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
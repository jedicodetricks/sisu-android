package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import co.sisu.mobile.models.AsyncLeaderboardJsonObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncLeaderboardStats extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String teamId;
    private String searchYear;
    private String searchMonth;
    private String url;

    public AsyncLeaderboardStats (AsyncServerEventListener cb, String url, String teamId, String searchYear, String searchMonth) {
        callback = cb;
        this.teamId = teamId;
        this.searchYear = searchYear;
        this.searchMonth = searchMonth;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            String requestUrl = url + "api/v1/team/leaderboards/" + teamId + "/" + searchYear;
            if(!searchMonth.equals("")) {
                requestUrl = url + "api/v1/team/leaderboards/" + teamId + "/" + searchYear + "/" + searchMonth;
            }

//            Log.e("LEADERBOARD URL", requestUrl);

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .get()
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
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
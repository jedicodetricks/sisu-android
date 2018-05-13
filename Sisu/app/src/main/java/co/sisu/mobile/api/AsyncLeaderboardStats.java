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

public class AsyncLeaderboardStats extends AsyncTask<Void, Void, Void> {
    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";


    private AsyncServerEventListener callback;
    private String teamId;
    private String searchYear;
    private String searchMonth;
//    private JWTObject jwtObject;

    public AsyncLeaderboardStats (AsyncServerEventListener cb, String teamId, String searchYear, String searchMonth, JWTObject jwtObject) {
        callback = cb;
        this.teamId = teamId;
        this.searchYear = searchYear;
        this.searchMonth = searchMonth;
//        this.jwtObject = jwtObject;
    }

    public String getJWT(String transactionID, Calendar time, String timestamp, Calendar expTime) {

        String jwtStr = Jwts.builder()
                .claim("Client-Timestamp", timestamp)
                .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
                .setIssuedAt(time.getTime())
                .setExpiration(expTime.getTime())
//                .claim("iat", time)
//                .claim("exp", expTime)
                .claim("Transaction-Id", transactionID)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return jwtStr;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        String transactionID = UUID.randomUUID().toString();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, -60);
        String timestamp = String.valueOf(date.getTimeInMillis());

        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 1);

        String jwt = getJWT(transactionID, date, timestamp, expDate);

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
                    .addHeader("Authorization", jwt)
                    .addHeader("Client-Timestamp", timestamp)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", transactionID)
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
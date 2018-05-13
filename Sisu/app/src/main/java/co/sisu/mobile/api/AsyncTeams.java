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

public class AsyncTeams extends AsyncTask<Void, Void, Void> {
    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private AsyncServerEventListener callback;
    String agentId;
//    JWTObject jwt;

    public AsyncTeams (AsyncServerEventListener cb, String agentId, JWTObject JwtObject) {
        callback = cb;
        this.agentId = agentId;
//        jwt = JwtObject;
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

        Response teamsResponse = null;

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request teamsRequest = new Request.Builder()
                .url("http://staging.sisu.co/api/v1/agent/get-teams/" + agentId)
                .get()
                .addHeader("Authorization", jwt)
                .addHeader("Client-Timestamp", timestamp)
                .addHeader("Transaction-Id", transactionID)
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
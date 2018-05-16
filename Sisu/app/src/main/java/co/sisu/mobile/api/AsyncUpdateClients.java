package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncUpdateClients extends AsyncTask<Void, Void, Void> {
    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private AsyncServerEventListener callback;
    private String clientId;
//    JWTObject jwt;
    ClientObject clientObject;

    public AsyncUpdateClients(AsyncServerEventListener cb, ClientObject client, JWTObject JwtObject) {
        callback = cb;
        this.clientId = client.getClient_id();
        this.clientObject = client;
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

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .create();
            String jsonInString = gson.toJson(clientObject);
            Log.e("POST CLIENT", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url("https://api.sisu.co/api/v1/client/edit-client/" + clientId)
                    .put(body)
                    .addHeader("Authorization", jwt)
                    .addHeader("Client-Timestamp", timestamp)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", transactionID)
                    .build();

            try {
                response = client.newCall(request).execute();
                Log.e("UPDATE ACTIVITIES", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Client");
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

package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncUpdateProfile extends AsyncTask<Void, Void, Void> {
    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private AsyncServerEventListener callback;
//    JWTObject jwt;
    String agentId;
    HashMap<String, String> changedFields;

    public AsyncUpdateProfile(AsyncServerEventListener cb, String agentId, HashMap<String, String> changedFields, JWTObject JwtObject) {
        this.callback = cb;
        this.agentId = agentId;
        this.changedFields = changedFields;
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
            String jsonString = "{";
            Gson gson = new Gson();
            int counter = 0;
            for ( String key : changedFields.keySet() ) {
                String value = changedFields.get(key);
                jsonString += "\"" + key + "\":\"" + value + "\"";
                if(counter < changedFields.size() - 1) {
                    jsonString += ",";
                }
                counter++;
            }
            jsonString += "}";
            Log.e("POST PROFILE", jsonString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonString);

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/v1/agent/edit-agent/"+ agentId)
                    .put(body)
                    .addHeader("Authorization", jwt)
                    .addHeader("Client-Timestamp", timestamp)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", transactionID)
                    .build();

            try {
                response = client.newCall(request).execute();
                Log.e("UPDATE SETTINGS", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Profile");
                } else {
                    callback.onEventFailed(null, "Update Profile");
                }
            } else {
                callback.onEventFailed(null, "Update Profile");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

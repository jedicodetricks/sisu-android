package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

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
 * Created by Brady Groharing on 4/21/2018.
 */

public class AsyncAddClient extends AsyncTask<Void, Void, Void> {

    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";
    private AsyncServerEventListener callback;
    private String agentId;
    ClientObject clientObject;
//    private JWTObject jwtObject;

    public AsyncAddClient(AsyncServerEventListener cb, String agentId, ClientObject clientObject, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agentId;
        this.clientObject = clientObject;
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
            String jsonInString = gson.toJson(clientObject);
            Log.e("POST CLIENT", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url("https://api.sisu.co/api/v1/client/edit-client/" + agentId)
                    .post(body)
                    .addHeader("Authorization", jwt)
                    .addHeader("Client-Timestamp", timestamp)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", transactionID)
                    .build();

            try {
                response = client.newCall(request).execute();
                Log.e("ADD CLIENT", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Add Client");
                } else {
                    callback.onEventFailed(null, "Add Client");
                }
            } else {
                callback.onEventFailed(null, "Add Client");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

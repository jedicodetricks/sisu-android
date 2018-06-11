package co.sisu.mobile.api;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/7/2018.
 */

public class AsyncForgotPassword extends AsyncTask<Void, Void, Void> {
    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private AsyncServerEventListener callback;
    private String email;

    public AsyncForgotPassword (AsyncServerEventListener cb, String email) {
        callback = cb;
        this.email = email;
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

        Response response = null;

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"email\":\"" + email +"\"}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.sisu.co/api/v1/forgot-password")
                .post(body)
                .addHeader("Authorization", jwt)
                .addHeader("Client-Timestamp", timestamp)
                .addHeader("Transaction-Id", transactionID)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                callback.onEventCompleted(null, "Forgot Password");
            }
            else {
                callback.onEventFailed(null, "Forgot Password");
            }
        }
        else {
            callback.onEventFailed(null, "Forgot Password");
        }

        response.body().close();
        return null;
    }
}

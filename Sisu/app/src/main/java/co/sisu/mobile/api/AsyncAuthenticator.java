package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.JWTObject;
import co.sisu.mobile.system.SaveSharedPreference;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Jeff on 3/15/2018.
 */

public class AsyncAuthenticator extends AsyncTask<Void, Void, Void> {
    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";
    private AsyncServerEventListener callback;
    private String email, password;

    public AsyncAuthenticator(AsyncServerEventListener cb, String email, String password) {
        this.callback = cb;
        this.email = email;
        this.password = password;
    }

    public String getJWT(String transactionID, Calendar time, String timestamp, Calendar expTime) {
        String jwtStr = Jwts.builder()
                .claim("Client-Timestamp", timestamp)
                .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
                .setIssuedAt(time.getTime())
                .setExpiration(expTime.getTime())
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

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"email\":\""+ email +"\",\"password\":\""+ password +"\"}");

            Request request = new Request.Builder()
                    .url("https://api.sisu.co/api/v1/agent/authenticate")
                    .post(body)
                    .addHeader("Authorization", jwt)
                    .addHeader("Client-Timestamp", timestamp)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", transactionID)
                    .build();
            String responseBody = "";
            try {
                response = client.newCall(request).execute();
                responseBody = response.body().string();
                Log.e("AUTH RESPONSE", responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    AsyncAgentJsonObject agent = gson.fromJson(responseBody, AsyncAgentJsonObject.class);
                    callback.onEventCompleted(agent, "Authenticator");
                }
                else {
                    callback.onEventFailed(null, "Authenticator");
                }
            } else {
                callback.onEventFailed(null, "Authenticator");
            }

//            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

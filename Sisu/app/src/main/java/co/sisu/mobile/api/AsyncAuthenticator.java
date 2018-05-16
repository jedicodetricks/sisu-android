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
//                .claim("iat", time)
//                .claim("exp", expTime)
                .claim("Transaction-Id", transactionID)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return jwtStr;
    }

//    public String authenticateUser(String transID, String compact, float time, String timestamp, String email, String password) throws UnsupportedEncodingException {
//        return getJsonString(compact, transID, time, timestamp, email, password);
//    }

    public String getJsonString(String auth, String transID, final float time, final String timestamp, String email, String password){
        final String jwt = auth;
        final String finalTrans = transID;
        final String finalTime = timestamp;
        final String loginEmail = email;
        final String loginPassword = password;

        Log.e("jwt", jwt);
        Log.e("finalTrans", finalTrans);
        Log.e("finalTime", String.valueOf(finalTime));
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Response response = null;
                    try {
                        OkHttpClient client = new OkHttpClient();

                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, "{\"email\":\"Brian@sisu.co\",\"password\":\"hellosisu\"}");

//                        RequestBody body = RequestBody.create(mediaType, "{\"email\":\"Brady.Groharing@sisu.co\",\"password\":\"asdf123\"}");

                        Request request = new Request.Builder()
                                .url("https://api.sisu.co/api/v1/agent/authenticate")
                                .post(body)
                                .addHeader("Authorization", jwt)
                                .addHeader("Client-Timestamp", timestamp)
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Transaction-Id", finalTrans)
                                .build();

                        try {
                            response = client.newCall(request).execute();
//                            Log.e("RESPONSE", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (response != null) {
                            if (response.code() == 200) {

                                callback.onEventCompleted(null, "Authenticator");
                            } else {
                                callback.onEventFailed(null, "Authenticator");
                            }
                        } else {
                            callback.onEventFailed(null, "Authenticator");
                        }

                        response.body().close();

//                        System.out.println(response.body().string());
//                        System.out.println(response.code());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        return "1570";
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
                Log.e("RESPOSNE", responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    AsyncAgentJsonObject agent = gson.fromJson(responseBody, AsyncAgentJsonObject.class);
                    callback.onEventCompleted(new JWTObject(jwt, timestamp, transactionID), "JWT");
                    callback.onEventCompleted(agent, "Authenticator");

                } else {
                    callback.onEventFailed(null, "Authenticator");
                }
            } else {
                callback.onEventFailed(null, "Authenticator");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

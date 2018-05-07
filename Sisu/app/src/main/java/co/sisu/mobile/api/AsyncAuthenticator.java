package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import co.sisu.mobile.models.AsyncAgentJsonObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    public String test(){

        byte[] secretArray = new byte[0];
//        String secretKey = "33SnhbgJaXFp6fYYd1Ru";
//        try {
//            secretArray = "33SnhbgJaXFp6fYYd1Ru".getBytes("UTF-8");
//            secretKey = new String(secretArray, "ISO-8859-1");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(secretKey);
//
//        String formattedKey = new String(byteBuffer, StandardCharsets.UTF_8);
        String newString = null;
        try {
            newString = new String(secretKey.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//        time = time.replace(".","");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        Date date = cal.getTime(); // get back a Date object
        Log.e("EPOCH", String.valueOf(cal.getTime()));
        Date d = new Date();
        String time = String.valueOf(date.getTime());

        String transactionID = UUID.randomUUID().toString();
        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 1);


        String compact = null;
        try {
//            Claims claims = Jwts.claims();
//            claims.put("Client-Timestamp", time);
//            claims.put("Transaction-Id", transactionID);
            Log.e("SECRET KEYS", secretKey + " " + newString);
            compact = Jwts.builder()
                    .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
//                    .setIssuedAt(d)
                    .claim("Client-Timestamp", "11111")
                    .claim("iat", 12345.6)
                    .claim("Transaction-Id", "2a029d3d-b7f9-4790-b680-a1c554a3b416")
                    .claim("exp", 12345.6)
//                    .setExpiration(expDate.getTime())
                    .signWith(SignatureAlgorithm.HS256, newString)
                    .compact();
            return authenticateUser("2a029d3d-b7f9-4790-b680-a1c554a3b416", compact, "12345.6", secretKey, email, password);
//            return authenticateUser(transactionID, compact, time, secretKey, email, password);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String authenticateUser(String transID, String compact, String time, String secretKey, String email, String password) throws UnsupportedEncodingException {
//        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(compact).getBody();

//        compact = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg";
//        transID = "E958DC02-8F61-4E97-90B7-F266DCC597E9";
//        time = "1520999095";
        return getJsonString(compact, transID, time, email, password);
    }

    public String getJsonString(String auth, String transID, String timeStamp, String email, String password){
//        System.out.println("AUTH: " + auth);
//        System.out.println("TRANS ID: " + transID);
//        System.out.println("TIMESTAMP: " + timeStamp);
        final String jwt = auth;
        final String finalTrans = transID;
        final String finalTime = timeStamp;
        final String loginEmail = email;
        final String loginPassword = password;

        Log.e("jwt", jwt);
        Log.e("finalTrans", finalTrans);
        Log.e("finalTime", finalTime);
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
                                .url("http://staging.sisu.co/api/v1/agent/authenticate")
                                .post(body)
                                .addHeader("Authorization", jwt)
                                .addHeader("Client-Timestamp", finalTime)
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Transaction-Id", finalTrans)
                                .build();

                        try {
                            response = client.newCall(request).execute();
                            Log.e("RESPONSE", response.body().string());
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
        //TODO: remember to unmess this up
//        test();
        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"email\":\""+ email +"\",\"password\":\""+ password +"\"}");

//                        RequestBody body = RequestBody.create(mediaType, "{\"email\":\"Brady.Groharing@sisu.co\",\"password\":\"asdf123\"}");

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/v1/agent/authenticate")
                    .post(body)
                    .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                    .addHeader("Client-Timestamp", "1520999095")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("AUTH AWAY", "GO GO GO");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    AsyncAgentJsonObject agent = gson.fromJson(response.body().charStream(), AsyncAgentJsonObject.class);
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


//    let client_timestamp:String = "\(Int(Date().timeIntervalSince1970))"
//        let transaction_id:String = UUID().uuidString
//        request?.setValue(client_timestamp, forHTTPHeaderField: "Client-Timestamp")
//        request?.setValue(transaction_id, forHTTPHeaderField: "Transaction-Id")
//        request?.setValue("application/json", forHTTPHeaderField: "Content-Type")
//
//        let jwt = self.encodeJWT(timestamp:client_timestamp,transaction:transaction_id)
//        request?.setValue(jwt, forHTTPHeaderField: "Authorization")
//
//
//        func encodeJWT(timestamp:String,transaction:String) -> String{
//
//        var claims = ClaimSet()
//        claims.issuer = "sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d"
//        claims.issuedAt = Date()
//        // currently make tokens good for 1 day
//        claims.expiration = Calendar.current.date(byAdding: .day, value: 1, to: Date())
//        claims["Client-Timestamp"] = timestamp
//        claims["Transaction-Id"] = transaction
//
//        //TODO: Put key somewhere encrypted
//        return JWT.encode(claims: claims, algorithm: .hs256(“SECRET KEY HERE“.data(using: .utf8)!))
//        }

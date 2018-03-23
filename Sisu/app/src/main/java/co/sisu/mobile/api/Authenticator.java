package co.sisu.mobile.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Handler;

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

public class Authenticator {

    public void test(String email, String password){

        byte[] secretArray = new byte[0];
        String secretKey = "33SnhbgJaXFp6fYYd1Ru";
//        try {
//            secretArray = "33SnhbgJaXFp6fYYd1Ru".getBytes("UTF-8");
//            secretKey = new String(secretArray, "ISO-8859-1");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(secretKey);

        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        time = time.replace(".","");
        String transactionID = UUID.randomUUID().toString();
        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 1);


        String compact = null;
        try {
//            Claims claims = Jwts.claims();
//            claims.put("Client-Timestamp", time);
//            claims.put("Transaction-Id", transactionID);

            compact = Jwts.builder()
                    .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
                    .setIssuedAt(new Date())
                    .claim("Client-Timestamp", time)
                    .claim("Transaction-Id", transactionID)
                    .setExpiration(expDate.getTime())
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
            authenticateUser(transactionID, compact, time, secretKey, email, password);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void authenticateUser(String transID, String compact, String time, String secretKey, String email, String password) throws UnsupportedEncodingException {
//        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(compact).getBody();

        compact = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg";
        transID = "E958DC02-8F61-4E97-90B7-F266DCC597E9";
        time = "1520999095";
        getJsonString(compact, transID, time, email, password);
    }

    public void getJsonString(String auth, String transID, String timeStamp, String email, String password){
//        System.out.println("AUTH: " + auth);
//        System.out.println("TRANS ID: " + transID);
//        System.out.println("TIMESTAMP: " + timeStamp);
        final String jwt = auth;
        final String finalTrans = transID;
        final String finalTime = timeStamp;
        final String loginEmail = email;
        final String loginPassword = password;

        Log.d("EMAIL", loginEmail);
        Log.d("PASSWORD", loginPassword);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
//                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {

//                        url = new URL("http://staging.sisu.co/api/agent/authenticate");
//                        urlConnection = (HttpURLConnection) url.openConnection();
//                        urlConnection.setReadTimeout(15000 /* milliseconds */);
//                        urlConnection.setConnectTimeout(15000 /* milliseconds */);
//
//                        urlConnection.setRequestMethod("POST");
//                        urlConnection.setRequestProperty("Authorization", jwt);
//                        urlConnection.setRequestProperty("Transaction-Id", finalTrans);
//                        urlConnection.setRequestProperty("Client-Timestamp", finalTime);
//                        urlConnection.setRequestProperty("Content-Type", "application/json");
//                        urlConnection.setDoInput(true);
//                        urlConnection.setDoOutput(true);
//
//                        JSONObject postDataParams = new JSONObject();
//                        postDataParams.put("email", loginEmail);
//                        postDataParams.put("password", loginPassword);
//
//
//                        OutputStream os = urlConnection.getOutputStream();
//                        BufferedWriter writer = new BufferedWriter(
//                                new OutputStreamWriter(os, "UTF-8"));
//                        writer.write(getPostDataString(postDataParams));
//
//                        writer.flush();
//                        writer.close();
//                        os.close();



                        OkHttpClient client = new OkHttpClient();

                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, "{\"email\":\"Brady.Groharing@sisu.co\",\"password\":\"asdf123\"}");

                        Request request = new Request.Builder()
                                .url("http://staging.sisu.co/api/agent/authenticate")
                                .post(body)
                                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                                .addHeader("Client-Timestamp", "1520999095")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                                .build();

                        Response response = client.newCall(request).execute();

                        if(response.code() == 200) {

                        }
                        else {

                        }

                        System.out.println(response.body().string());
                        System.out.println(response.code());


//                        int status = urlConnection.getResponseCode();
//                        InputStream in;
//                        if(status >= 400)
//                            in = urlConnection.getErrorStream();
//                        else
//                            in = urlConnection.getInputStream();
//
//                        InputStreamReader isw = new InputStreamReader(in);
//
//                        int data = isw.read();
//                        while (data != -1) {
//                            char current = (char) data;
//                            data = isw.read();
//                            System.out.print(current);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
//                    //Your code goes here
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }
        });
        thread.start();
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public void pingServer() {
        Boolean test = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = null;
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("http://staging.sisu.co/api/ping")
                            .get()
                            .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                            .addHeader("Client-Timestamp", "1520999095")
                            .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                            .build();
                    response = client.newCall(request).execute();
                    System.out.println(response.body().string());
                } catch (IOException e) {
                }
            }
        }).start();

        Runnable runnable;
        Handler handler;
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Response response = null;
//                    OkHttpClient client = new OkHttpClient();
//
//                    Request request = new Request.Builder()
//                            .url("http://staging.sisu.co/api/ping")
//                            .get()
//                            .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
//                            .addHeader("Client-Timestamp", "1520999095")
//                            .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
//                            .build();
//                    response = client.newCall(request).execute();
//                    System.out.println(response.body().string());
//                    return true;
//                } catch (IOException e) {
//                    return false;
//                }
//            }
//        };
//        runnable.run();

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

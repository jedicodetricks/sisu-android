package co.sisu.mobile.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by Jeff on 3/15/2018.
 */

public class Authenticator {

    public void test(){

        String secretKey = "33SnhbgJaXFp6fYYd1Ru";

        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        time = time.replace(".","");
        String transactionID = UUID.randomUUID().toString();
        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 1);


        String compact = null;
        try {
            compact = Jwts.builder()
                    .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
                    .setIssuedAt(new Date())
                    .setExpiration(expDate.getTime())
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
            authenticateUser(transactionID, compact, time, secretKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void authenticateUser(String transID, String compact, String time, String secretKey) throws UnsupportedEncodingException {
//        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(compact).getBody();
        compact = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg";
        transID = "E958DC02-8F61-4E97-90B7-F266DCC597E9";
        time = "1520999095";
        getJsonString(compact, transID, time);
    }

    public void getJsonString(String auth, String transID, String timeStamp){
        System.out.println(auth);
        System.out.println(transID);
        System.out.println(timeStamp);
        final String finalAuth = auth;
        final String finalTrans = transID;
        final String finalTime = "1520999095";
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
//                        url = new URL("http://staging.sisu.co/api/agent/authenticate");
                        url = new URL("http://staging.sisu.co/api/agent/edit-agent?agent_id=300");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setReadTimeout(15000 /* milliseconds */);
                        urlConnection.setConnectTimeout(15000 /* milliseconds */);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setRequestProperty("Authorization", finalAuth);
                        urlConnection.setRequestProperty("Transaction-Id", finalTrans);
                        urlConnection.setRequestProperty("Client-Timestamp", finalTime);
//                        urlConnection.setRequestProperty("Content-Type", "application/json");
//                        urlConnection.setDoInput(true);
//                        urlConnection.setDoOutput(true);

                        JSONObject postDataParams = new JSONObject();
                        postDataParams.put("email", "brian@sisu.co");
                        postDataParams.put("password", "asdf");


//                        OutputStream os = urlConnection.getOutputStream();
//                        BufferedWriter writer = new BufferedWriter(
//                                new OutputStreamWriter(os, "UTF-8"));
//                        writer.write(getPostDataString(postDataParams));
//
//                        writer.flush();
//                        writer.close();
//                        os.close();

                        int status = urlConnection.getResponseCode();
                        InputStream in;
                        if(status >= 400)
                            in = urlConnection.getErrorStream();
                        else
                            in = urlConnection.getInputStream();

                        InputStreamReader isw = new InputStreamReader(in);

                        int data = isw.read();
                        while (data != -1) {
                            char current = (char) data;
                            data = isw.read();
                            System.out.print(current);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                    //Your code goes here
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
}

package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncActivities extends AsyncTask<Void, Void, Void> {

    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private AsyncServerEventListener callback;
    private String agentId;
    private String startDate;
    private String endDate;
//    private JWTObject jwt;

//    public AsyncActivities(AsyncServerEventListener cb, String agent_id, String formattedStartTime, String agentId, JWTObject jwtObject) {
//        callback = cb;
//        this.agentId = agentId;
//        jwt = jwtObject;
//    }

    public AsyncActivities (AsyncServerEventListener cb, String agentId, Date startDate, Date endDate, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agentId;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        this.startDate = formatter.format(startDate);
        this.endDate = formatter.format(endDate);
//        jwt = jwtObject;
    }

    public AsyncActivities(AsyncServerEventListener cb, String agent_id, String formattedStartTime, String formattedEndTime, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agent_id;
        this.startDate = formattedStartTime;
        this.endDate = formattedEndTime;
//        jwt = jwtObject;
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
            Log.e("SENDING GET ACTIVITY", startDate + " ||| " + endDate);
            RequestBody body = RequestBody.create(mediaType, "{\"start_date\": \"" + startDate + "\",\"end_date\": \"" + endDate + "\",\"include_counts\":1,\"include_activities\":0}");

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/v1/agent/activity/" + agentId)
                    .post(body)
                    .addHeader("Authorization", jwt)
                    .addHeader("Client-Timestamp", timestamp)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", transactionID)
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("ACTIVITIES", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    AsyncActivitiesJsonObject activities = gson.fromJson(response.body().charStream(), AsyncActivitiesJsonObject.class);
                    callback.onEventCompleted(activities, "Activities");
                } else {
                    callback.onEventFailed(null, "Activities");
                }
            } else {
                callback.onEventFailed(null, "Activities");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

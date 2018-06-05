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

public class AsyncActivities extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String agentId;
    private String startDate;
    private String endDate;

    public AsyncActivities (AsyncServerEventListener cb, String agentId, Date startDate, Date endDate) {
        callback = cb;
        this.agentId = agentId;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        this.startDate = formatter.format(startDate);
        this.endDate = formatter.format(endDate);
    }

    public AsyncActivities(AsyncServerEventListener cb, String agent_id, String formattedStartTime, String formattedEndTime) {
        callback = cb;
        this.agentId = agent_id;
        this.startDate = formattedStartTime;
        this.endDate = formattedEndTime;
    }

    @Override
    protected String doInBackground(String... strings) {
//        Log.e("PARAMS", strings[0] + " " + strings[1] + " " + strings[2] + " ||END");
        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            MediaType mediaType = MediaType.parse("application/json");
            Log.e("SENDING GET ACTIVITY", startDate + " ||| " + endDate);
            RequestBody body = RequestBody.create(mediaType, "{\"start_date\": \"" + startDate + "\",\"end_date\": \"" + endDate + "\",\"include_counts\":1,\"include_activities\":0}");

            Request request = new Request.Builder()
                    .url("https://api.sisu.co/api/v1/agent/activity/" + agentId)
                    .post(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

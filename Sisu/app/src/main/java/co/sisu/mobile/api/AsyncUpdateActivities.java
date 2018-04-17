package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/17/18.
 */

public class AsyncUpdateActivities extends AsyncTask<Void, Void, Void> {

    private AsyncServerEventListener callback;
    private String agentId;
    private String startDate;
    private String endDate;

    public AsyncUpdateActivities (AsyncServerEventListener cb, String agentId, Date startDate, Date endDate) {
        callback = cb;
        this.agentId = agentId;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        this.startDate = formatter.format(startDate);
        this.endDate = formatter.format(endDate);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            MediaType mediaType = MediaType.parse("application/json");
//            startDate = "2017-02-01";
//            endDate = "2018-10-05";
            RequestBody body = RequestBody.create(mediaType, "{\"start_date\": \"" + startDate + "\",\"end_date\": \"" + endDate + "\",\"include_counts\":1,\"include_activities\":0}");

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/agent/activity/" + agentId)
                    .post(body)
                    .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                    .addHeader("Client-Timestamp", "1520999095")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
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
                    callback.onEventCompleted(activities, "Update Activities");
                } else {
                    callback.onEventFailed();
                }
            } else {
                callback.onEventFailed();
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

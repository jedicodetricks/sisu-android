package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncActivities extends AsyncTask<Void, Void, Void> {

    private AsyncServerEventListener callback;
    private String agentId;
    private String startDate;
    private String endDate;
    private JWTObject jwt;

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
        jwt = jwtObject;
    }

    public AsyncActivities(AsyncServerEventListener cb, String agent_id, String formattedStartTime, String formattedEndTime, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agent_id;
        this.startDate = formattedStartTime;
        this.endDate = formattedEndTime;
        jwt = jwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {

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
                    .addHeader("Authorization", jwt.getJwt())
                    .addHeader("Client-Timestamp", jwt.getTimestamp())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", jwt.getTransId())
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

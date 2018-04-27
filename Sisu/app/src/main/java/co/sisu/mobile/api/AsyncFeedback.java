package co.sisu.mobile.api;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/21/2018.
 */

public class AsyncFeedback extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    String agentId, feedback;

    public AsyncFeedback (AsyncServerEventListener cb, String agentId, String feedback) {
        callback = cb;
        this.agentId = agentId;
        this.feedback = feedback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"feedback\":\"" + feedback +"\"}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://staging.sisu.co/api/feedback/add-feedback/" + agentId)
                .post(body)
                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                .addHeader("Client-Timestamp", "1520999095")
                .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response.code() == 200) {
            callback.onEventCompleted(null, "Feedback");
        }
        else {
            callback.onEventFailed(null, "Server Ping");
        }
        response.body().close();
        return null;
    }
}
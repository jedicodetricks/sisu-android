package co.sisu.mobile.api;

import android.os.AsyncTask;

import java.io.IOException;

import co.sisu.mobile.models.JWTObject;
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
    private JWTObject jwtObject;

    public AsyncFeedback (AsyncServerEventListener cb, String agentId, String feedback, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agentId;
        this.feedback = feedback;
        this.jwtObject = jwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"feedback\":\"" + feedback +"\"}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://staging.sisu.co/api/v1/feedback/add-feedback/" + agentId)
                .post(body)
                .addHeader("Authorization", jwtObject.getJwt())
                .addHeader("Client-Timestamp", jwtObject.getTimestamp())
                .addHeader("Transaction-Id", jwtObject.getTransId())
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
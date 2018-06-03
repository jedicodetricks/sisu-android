package co.sisu.mobile.api;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/21/2018.
 */

public class AsyncFeedback extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    String agentId, feedback;

    public AsyncFeedback (AsyncServerEventListener cb, String agentId, String feedback) {
        callback = cb;
        this.agentId = agentId;
        this.feedback = feedback;
    }

    @Override
    protected String doInBackground(String... strings) {
        Response response = null;

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"feedback\":\"" + feedback +"\"}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.sisu.co/api/v1/feedback/add-feedback/" + agentId)
                .post(body)
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
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
            callback.onEventFailed(null, "Feedback");
        }
        response.body().close();
        return null;
    }
}
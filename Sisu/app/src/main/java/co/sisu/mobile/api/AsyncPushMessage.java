package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/21/2018.
 */

public class AsyncPushMessage extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String teamId, message, url;
    private boolean isSlackCall;

    public AsyncPushMessage(AsyncServerEventListener cb, String url, String teamId, String message, boolean isSlackCall) {
        callback = cb;
        this.teamId = teamId;
        this.message = message;
        this.url = url;
        this.isSlackCall = isSlackCall;
    }

    @Override
    protected String doInBackground(String... strings) {
        Response response = null;

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"body\":\"" + message +"\", \"title\":\"Message from Team Administrator\"}");

        OkHttpClient client = new OkHttpClient();
//        {{url}}/api/{{version}}team/push-message/{{team_id}}

        String fullUrl = url + "api/v1/team/push-message/" + teamId;

        Request request = new Request.Builder()
                .url(fullUrl)
                .post(body)
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();

//        if(isSlackCall) {
//            fullUrl = url;
//            body = RequestBody.create(mediaType, "{\"text\":\"" + feedback +"\"}");
//            request = new Request.Builder()
//                    .url(fullUrl)
//                    .post(body)
//                    .build();
//        }

        Log.e("FULL URL", url);


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
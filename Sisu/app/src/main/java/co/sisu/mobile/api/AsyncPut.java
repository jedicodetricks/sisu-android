 package co.sisu.mobile.api;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.enums.ApiReturnType;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 5/5/2018.
 */

public class AsyncPut extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String url;
    private ApiReturnType returnType;
    private String jsonInString;

    public AsyncPut(AsyncServerEventListener cb, String url, ApiReturnType returnType, String jsonInString) {
        callback = cb;
        this.url = url;
        this.returnType = returnType;
        this.jsonInString = jsonInString;
    }

    @Override
    protected String doInBackground(String... strings) {
        Response response = null;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonInString);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Content-Type", "application/json")
                .addHeader("Transaction-Id", strings[2])
                .build();

        try {
            response = client.newCall(request).execute();
//            Log.d("ASYNC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                callback.onEventCompleted(response, returnType);
            }
            else {
                callback.onEventFailed(null, returnType);
            }
        }
        else {
            callback.onEventFailed(null, returnType);
        }

        return null;
    }
}

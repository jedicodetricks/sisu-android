package co.sisu.mobile.api;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.ApiReturnTypes;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 5/5/2018.
 */

public class AsyncGet extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String url;
    private ApiReturnTypes returnType;

    public AsyncGet (AsyncServerEventListener cb, String url, ApiReturnTypes returnType) {
        callback = cb;
        this.url = url;
        this.returnType = returnType;
    }

    @Override
    protected String doInBackground(String... strings) {
        Response response = null;


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
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
//                AsyncAgentJsonObject agentObject = gson.fromJson(response.body().charStream(), AsyncAgentJsonObject.class);

                callback.onEventCompleted(response, returnType);
            }
            else {
                callback.onEventFailed(null, returnType);
            }
        }
        else {
            callback.onEventFailed(null, returnType);
        }

        response.body().close();
        return null;
    }
}

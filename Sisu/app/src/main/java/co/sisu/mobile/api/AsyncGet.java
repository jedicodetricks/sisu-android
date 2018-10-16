package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 5/5/2018.
 */

public class AsyncGet extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String url;
    private String returnString;

    public AsyncGet (AsyncServerEventListener cb, String url, String returnString) {
        callback = cb;
        this.url = url;
        this.returnString = returnString;
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

                callback.onEventCompleted(response, returnString);
            }
            else {
                callback.onEventFailed(null, returnString);
            }
        }
        else {
            callback.onEventFailed(null, returnString);
        }

        response.body().close();
        return null;
    }
}

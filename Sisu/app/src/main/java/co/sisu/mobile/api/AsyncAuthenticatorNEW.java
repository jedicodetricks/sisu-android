package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import co.sisu.mobile.models.AsyncAgentJsonObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Jeff on 3/15/2018.
 */

public class AsyncAuthenticatorNEW extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private String email, password;

    public AsyncAuthenticatorNEW(AsyncServerEventListener cb, String email, String password) {
        this.callback = cb;
        this.email = email;
        this.password = password;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"email\":\""+ email +"\",\"password\":\""+ password +"\"}");

            Request request = new Request.Builder()
                    .url("https://api.sisu.co/api/v1/agent/authenticate")
                    .post(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();
            String responseBody = "";
            try {
                response = client.newCall(request).execute();
                responseBody = response.body().string();
                Log.e("AUTH RESPONSE", responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    AsyncAgentJsonObject agent = gson.fromJson(responseBody, AsyncAgentJsonObject.class);
                    callback.onEventCompleted(agent, "Authenticator");
                }
                else {
                    callback.onEventFailed(null, "Authenticator");
                }
            } else {
                callback.onEventFailed(null, "Authenticator");
            }

//            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.JWTObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/21/2018.
 */

public class AsyncAddClient extends AsyncTask<Void, Void, Void> {

    private AsyncServerEventListener callback;
    private String agentId;
    ClientObject clientObject;
    private JWTObject jwtObject;

    public AsyncAddClient(AsyncServerEventListener cb, String agentId, ClientObject clientObject, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agentId;
        this.clientObject = clientObject;
        this.jwtObject = jwtObject;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = gson.toJson(clientObject);
            Log.e("POST CLIENT", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/v1/client/edit-client/" + agentId)
                    .post(body)
                    .addHeader("Authorization", jwtObject.getJwt())
                    .addHeader("Client-Timestamp", jwtObject.getTimestamp())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", jwtObject.getTransId())
                    .build();

            try {
                response = client.newCall(request).execute();
                Log.e("ADD CLIENT", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Add Client");
                } else {
                    callback.onEventFailed(null, "Add Client");
                }
            } else {
                callback.onEventFailed(null, "Add Client");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

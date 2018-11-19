package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.JWTObject;
import co.sisu.mobile.models.UpdateSettingsObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncUpdateActivitySettings extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String updateSettingsModel;
    private String url;
    private String agentId;
    private int marketId;

    public AsyncUpdateActivitySettings(AsyncServerEventListener cb, String url, String updateSettingsModel, String agentId, int marketId) {
        callback = cb;
        this.updateSettingsModel = updateSettingsModel;
        this.url = url;
        this.agentId = agentId;
        this.marketId = marketId;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = updateSettingsModel;
            Log.e("POST ACTIVITY SETTINGS", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url(url + "api/v1/agent/record-activities/"+ agentId + "/" + marketId)
                    .post(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("UPDATE ACTIVITY SETTINGS", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Settings");
                } else {
                    callback.onEventFailed(null, "Update Settings");
                }
            } else {
                callback.onEventFailed(null, "Update Settings");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

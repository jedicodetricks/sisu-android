package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncTeamColorSchemeObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncGetTeamColorScheme extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String url;
    private int teamId;
    private String isLightTheme;

    public AsyncGetTeamColorScheme(AsyncServerEventListener cb, String url, int teamId, String isLightTheme) {
        callback = cb;
        this.url = url;
        this.teamId = teamId;
        this.isLightTheme = isLightTheme;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            //TODO: Get rid of this
            teamId = 715;
            Request request = new Request.Builder()
                    .url(url + "api/v1/team/theme/" + teamId + "/" + isLightTheme)
                    .get()
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("Get Color Scheme", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    AsyncTeamColorSchemeObject asyncTeamColorSchemeObject = gson.fromJson(response.body().charStream(), AsyncTeamColorSchemeObject.class);
                    callback.onEventCompleted(asyncTeamColorSchemeObject, "Get Color Scheme");
                } else {
                    callback.onEventFailed(null, "Get Color Scheme");
                }
            } else {
                callback.onEventFailed(null, "Get Color Scheme");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

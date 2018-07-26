package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncAddNotes extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String clientId;
    private String url;
    private String note;
    private String noteType;

    public AsyncAddNotes(AsyncServerEventListener cb, String url, String clientId, String note, String noteType) {
        callback = cb;
        this.clientId = clientId;
        this.note = note;
        this.url = url;
        this.noteType = noteType;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            String jsonInString = "{\"log_type_id\":\"" + noteType + "\", \"note\":\"" + note + "\"}";
            Log.e("POST NOTES", jsonInString);
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url(url + "api/v1/client/logs/" + clientId)
                    .post(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("ADD NOTES", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Add Notes");
                } else {
                    callback.onEventFailed(null, "Add Notes");
                }
            } else {
                callback.onEventFailed(null, "Add Notes");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

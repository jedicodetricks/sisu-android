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
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncDeleteNotes extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String url;
    private String noteId;

    public AsyncDeleteNotes(AsyncServerEventListener cb, String url, String noteId) {
        callback = cb;
        this.url = url;
        this.noteId = noteId;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url + "api/v1/client/logs/" + noteId)
                    .delete()
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("UPDATE NOTES", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Delete Notes");
                } else {
                    callback.onEventFailed(null, "Delete Notes");
                }
            } else {
                callback.onEventFailed(null, "Delete Notes");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

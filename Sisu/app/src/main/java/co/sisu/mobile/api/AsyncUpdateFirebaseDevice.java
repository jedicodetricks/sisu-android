package co.sisu.mobile.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.system.SaveSharedPreference;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncUpdateFirebaseDevice extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String url;
    private AgentModel agent;
    private String fcmToken;
    private Context context;
    private FirebaseDeviceObject currentDevice;

    public AsyncUpdateFirebaseDevice(AsyncServerEventListener cb, String url, Context context, AgentModel agent, String token, FirebaseDeviceObject currentDevice) {
        callback = cb;
        this.url = url;
        this.agent = agent;
        this.fcmToken = token;
        this.context = context;
        this.currentDevice = currentDevice;
    }

    @Override
    protected String doInBackground(String... strings) {
        if(currentDevice != null) {
            FirebaseDeviceObject firebaseDeviceObject = new FirebaseDeviceObject(currentDevice.getDevice_type(), currentDevice.getDevice_id(), currentDevice.getDevice_name(), fcmToken);

            try {
                Response response = null;
                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();
                String jsonInString = gson.toJson(firebaseDeviceObject);
                Log.e("PUT FIREBASE DEVICE", jsonInString);
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonInString);

                Request request = new Request.Builder()
                        .url(url + "api/v1/agent/device/" + agent.getAgent_id())
                        .put(body)
                        .addHeader("Authorization", strings[0])
                        .addHeader("Client-Timestamp", strings[1])
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Transaction-Id", strings[2])
                        .build();

                try {
                    response = client.newCall(request).execute();
//                Log.e("Update Firebase Device", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    if (response.code() == 200) {
                        callback.onEventCompleted(null, "Update Firebase Device");
                    } else {
                        callback.onEventFailed(null, "Update Firebase Device");
                    }
                } else {
                    callback.onEventFailed(null, "Update Firebase Device");
                }

                response.body().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}

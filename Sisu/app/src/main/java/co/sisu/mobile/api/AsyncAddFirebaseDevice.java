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

public class AsyncAddFirebaseDevice extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String url;
    private AgentModel agent;
    private String fcmToken;
    private Context context;

    public AsyncAddFirebaseDevice(AsyncServerEventListener cb, String url, Context context, AgentModel agent, String token) {
        callback = cb;
        this.url = url;
        this.agent = agent;
        this.fcmToken = token;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        FirebaseDeviceObject firebaseDeviceObject = generateFirebaseObject();

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = gson.toJson(firebaseDeviceObject);
            Log.e("POST FIREBASE DEVICE", jsonInString);
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url(url + "api/v1/agent/device/" + agent.getAgent_id())
                    .post(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("Add Firebase Device", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Add Firebase Device");
                } else {
                    callback.onEventFailed(null, "Add Firebase Device");
                }
            } else {
                callback.onEventFailed(null, "Add Firebase Device");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private FirebaseDeviceObject generateFirebaseObject() {
        String uuid = UUID.randomUUID().toString();
        String deviceId = agent.getAgent_id() + "-" + uuid;
        String deviceName = agent.getAgent_id() + "'s android";
        if(agent.getFirst_name() != null && agent.getLast_name() != null) {
            deviceName = agent.getAgent_id() + "-" + agent.getFirst_name() + " " + agent.getLast_name() + " android " + uuid;
        }
        else if(agent.getLast_name() != null) {
            deviceName = agent.getAgent_id() + " " + agent.getLast_name() + "s android " + uuid;
        }
        SaveSharedPreference.setFirebaseDeviceId(context, deviceId);
        FirebaseDeviceObject firebaseDeviceObject = new FirebaseDeviceObject("Android", deviceId, deviceName, fcmToken);

        return firebaseDeviceObject;
    }
}

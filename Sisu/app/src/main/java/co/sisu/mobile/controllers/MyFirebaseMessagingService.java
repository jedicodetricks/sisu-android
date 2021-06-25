package co.sisu.mobile.controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.sisu.mobile.activities.NotificationActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.FirebaseDeviceObject;

/**
 * Created by bradygroharing on 7/24/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements AsyncServerEventListener {

    private Context context;
    private ApiManager apiManager;
    private AgentModel agent;
    private FirebaseDeviceObject currentDevice;

    public MyFirebaseMessagingService() {
        // This exists for the AndroidManifest.xml. It was mad at me.
    }

    public MyFirebaseMessagingService(ApiManager apiManager, AgentModel agent, Context context, FirebaseDeviceObject currentDevice) {
        this.apiManager = apiManager;
        this.agent = agent;
        this.context = context;
        this.currentDevice = currentDevice;
    }

    public void initFirebase() {
        Log.e("Firebase", FirebaseMessaging.getInstance().getToken().toString());
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Firebase", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult();

                    // Log and toast
                    apiManager.sendFirebaseToken(MyFirebaseMessagingService.this, context, agent, token);
                    Log.e("Firebase TOKEN", token);
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("Firebase", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("Firebase", "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e("Firebase", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.e("Firebase", "Message Notification Title: " + remoteMessage.getNotification().getTitle());

            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonBody = (JsonObject) parser.parse(remoteMessage.getNotification().getBody());
                Log.e("Firebase", "Message Notification has_html: " + jsonBody.get("has_html"));
            } catch(Exception e) {

            }

            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("title", remoteMessage.getNotification().getTitle());
            intent.putExtra("body", remoteMessage.getNotification().getBody());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onDeletedMessages() {
        //This should do a sync with the server
    }

    public void refreshToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Firebase", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult();

                    // Log and toast
//                if(currentDevice != null) {
                    apiManager.refreshFirebaseToken(MyFirebaseMessagingService.this, context, agent, token, currentDevice);
                    Log.e("Firebase TOKEN REFRESH", token);
//                }

                });
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.e("Firebase", "Refreshed token: " + token);
        if(apiManager != null) {
            apiManager.refreshFirebaseToken(MyFirebaseMessagingService.this, context, agent, token, currentDevice);
        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        Log.e("FIREBASE COMPLETE", asyncReturnType);
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.SEND_FIREBASE_TOKEN) {
            Log.e("FIREBASE COMPLETE", String.valueOf(returnType));
        }
        else if(returnType == ApiReturnType.UPDATE_FIREBASE) {
            Log.e("FIREBASE COMPLETE", String.valueOf(returnType));
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        Log.e("FIREBASE FAILURE", asyncReturnType);
    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }
}

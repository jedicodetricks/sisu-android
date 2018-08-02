package co.sisu.mobile.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.utils.Utils;

/**
 * Created by bradygroharing on 7/24/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements AsyncServerEventListener {

    private Context context;
    private ApiManager apiManager;
    private AgentModel agent;
    private FirebaseDeviceObject currentDevice;

    public MyFirebaseMessagingService() {
    }

    public MyFirebaseMessagingService(ApiManager apiManager, AgentModel agent, Context context, FirebaseDeviceObject currentDevice) {
        this.apiManager = apiManager;
        this.agent = agent;
        this.context = context;
        this.currentDevice = currentDevice;
    }

    public void initFirebase() {
        Log.e("Firebase", FirebaseInstanceId.getInstance().getInstanceId().toString());
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("Firebase", "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();

                // Log and toast
                apiManager.sendFirebaseToken(MyFirebaseMessagingService.this, context, agent, token);
                Log.e("Firebase TOKEN", token);
            }
        });

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
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
            Utils.generateNotification(getApplicationContext(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onDeletedMessages() {
        //This should do a sync with the server
    }

    public void refreshToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("Firebase", "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();

                // Log and toast
                apiManager.refreshFirebaseToken(MyFirebaseMessagingService.this, context, agent, token, currentDevice);
                Log.e("Firebase TOKEN REFRESH", token);
            }
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

    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }
}

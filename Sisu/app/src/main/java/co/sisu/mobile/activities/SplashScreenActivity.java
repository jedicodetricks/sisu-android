package co.sisu.mobile.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncAuthenticator;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Jeff on 3/7/2018.
 */

public class SplashScreenActivity extends AppCompatActivity implements AsyncServerEventListener {

    static boolean loaded = false;
    private boolean pingRetry = false;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded = false;
        login();
    }

    private void login() {
        if(SaveSharedPreference.getUserName(SplashScreenActivity.this).length() == 0) {
            intent = new Intent(this, MainActivity.class);
            launchActivity();
        }
        else {
            String userName = SaveSharedPreference.getUserName(SplashScreenActivity.this);
            String userPassword = SaveSharedPreference.getUserPassword(SplashScreenActivity.this);
            new AsyncAuthenticator(this, userName, userPassword).execute();
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        // TODO: Move these to the new format
        if(asyncReturnType.equals("Authenticator")) {
            //TODO: This is being used. Transition it.
            AsyncAgentJsonObject agentObject = (AsyncAgentJsonObject) returnObject;
            if(agentObject.getStatus_code().equals("-1")) {
                showToast("Incorrect username or password");
                intent = new Intent(this, MainActivity.class);
                launchActivity();
            }
            else {
                AgentModel agent = agentObject.getAgent();
                Bundle bundle = getIntent().getExtras();
                String title = "";
                String body = "";

                if(bundle != null) {
                    title = bundle.getString("title");
                    body =  bundle.getString("body");
                }

                intent = new Intent(this, ParentActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("Agent", agent);
                extras.putString("title", title);
                extras.putString("body", body);
                intent.putExtras(extras);
                launchActivity();
            }
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {

    }

    private void launchActivity() {
        createNotificationChannel();
        startActivity(intent);
        finish();
    }


    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("SisuChannel");
            String description = ("SisuNotifications");
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("420", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Authenticator")) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("Network", false);
            launchActivity();
        }
    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }

    public void showToast(final CharSequence msg){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(SplashScreenActivity.this, msg,Toast.LENGTH_SHORT);
                View view = toast.getView();
                TextView text = (TextView) view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.sisuOrange));
                view.setBackgroundResource(R.color.sisuOrange);
                text.setPadding(20, 8, 20, 8);
                toast.show();
            }
        });
    }
}

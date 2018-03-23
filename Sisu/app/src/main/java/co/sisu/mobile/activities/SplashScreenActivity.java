package co.sisu.mobile.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import co.sisu.mobile.api.AsyncPingEventListener;
import co.sisu.mobile.api.AsyncServerPing;
import co.sisu.mobile.api.Authenticator;

/**
 * Created by Jeff on 3/7/2018.
 */

public class SplashScreenActivity extends AppCompatActivity implements AsyncPingEventListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pingServer();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void pingServer() {
        new AsyncServerPing(this).execute();
//        Authenticator authenticator = new Authenticator();
//        authenticator.pingServer();
    }

    @Override
    public void onEventCompleted() {
        Log.d("COMPLETE", "COMPLETE");
    }

    @Override
    public void onEventFailed() {
        Log.d("FAILED", "FAILED");
    }
}

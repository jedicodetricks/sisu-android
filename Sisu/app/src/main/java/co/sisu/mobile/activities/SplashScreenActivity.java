package co.sisu.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncServerPing;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Jeff on 3/7/2018.
 */

public class SplashScreenActivity extends AppCompatActivity implements AsyncServerEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pingServer();

    }

    private void pingServer() {
        new AsyncServerPing(this).execute();
    }

    @Override
    public void onEventCompleted() {
        Log.d("COMPLETE", "COMPLETE");
        Intent intent;
        if(SaveSharedPreference.getUserName(SplashScreenActivity.this).length() == 0)
        {
            Log.d("LOGGED IN", "FALSE");
            // call Login Activity
            intent = new Intent(this, MainActivity.class);
        }
        else
        {
            Log.d("LOGGED IN", "TRUE");
            // Already logged in. Enter app.
            intent = new Intent(this, ParentActivity.class);
        }

        startActivity(intent);
        finish();
    }

    @Override
    public void onEventFailed() {
        Log.d("FAILED", "FAILED");
    }
}

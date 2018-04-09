package co.sisu.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncServerPing;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Jeff on 3/7/2018.
 */

public class SplashScreenActivity extends AppCompatActivity implements AsyncServerEventListener {

    int WAIT_AMOUNT = 1000;
    static boolean loaded = false;
    private boolean pingRetry = false;
    private CountDownTimer cdt;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded = false;

        pingServer();

    }

    private void pingServer() {
        new AsyncServerPing(this).execute();
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

        if(SaveSharedPreference.getUserName(SplashScreenActivity.this).length() == 0) {
            // call Login Activity
//            while(!loaded) {
//                // We apparently need this log or Android decides to just not work.
//                Log.v("Splash", "loading");
//                if(loaded) {
                    intent = new Intent(this, MainActivity.class);
                    launchActivity();
//                    break;
//                }
//            }

        }
        else
        {
            // Already logged in. Enter app.
//            while(!loaded) {
//                // We apparently need this log or Android decides to just not work.
//                Log.v("Splash", "loading");
//                if(loaded) {
                    //TODO: We're going to need to authenticate somehow if we go straight into the ParentActivity
                    intent = new Intent(this, ParentActivity.class);
                    launchActivity();
//                    break;
//                }
//            }

        }


    }

    private void launchActivity() {
        startActivity(intent);
        finish();
    }

    @Override
    public void onEventFailed() {
        Log.d("FAILED", "FAILED");
        if(!pingRetry) {
            pingServer();
        }
    }
}

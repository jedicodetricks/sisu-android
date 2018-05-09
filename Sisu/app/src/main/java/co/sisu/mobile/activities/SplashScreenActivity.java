package co.sisu.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import co.sisu.mobile.api.AsyncAuthenticator;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncServerPing;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.JWTObject;
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
//        TestFairy.begin(this, "9eb176da8ee4fd4ce62ad1275210bbc526800c0c");
        loaded = false;
        pingServer();
    }

    // TODO: 5/8/2018 make parentActivity available here
    private void pingServer() {
        new AsyncServerPing(this).execute();
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Server Ping")) {
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
        else if(asyncReturnType.equals("JWT")) {
            JWTObject jwt = (JWTObject) returnObject;
            SaveSharedPreference.setJWT(this, jwt.getJwt());
            SaveSharedPreference.setClientTimestamp(this, jwt.getTimestamp());
            SaveSharedPreference.setTransId(this, jwt.getTransId());
        }
        else if(asyncReturnType.equals("Authenticator")) {
            AsyncAgentJsonObject agentObject = (AsyncAgentJsonObject) returnObject;
            AgentModel agent = agentObject.getAgent();
            intent = new Intent(this, ParentActivity.class);
            intent.putExtra("Agent", agent);
            launchActivity();
        }

    }

    private void launchActivity() {
        startActivity(intent);
        finish();
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        Log.d("FAILED", "FAILED");
        if(asyncReturnType.equals("Server Ping")) {
            if(!pingRetry) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pingRetry = true;
                pingServer();
            }
            else {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("Network", false);
                launchActivity();
            }
        }

    }
}

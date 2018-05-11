package co.sisu.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncAuthenticator;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.JWTObject;
import co.sisu.mobile.system.SaveSharedPreference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncServerEventListener {

    String emailAddress;
    String password;
//    byte[] key = "SisuRocks".getBytes();
    boolean networkActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_sign_in_layout);
        getSupportActionBar().setElevation(0);
        initializeButtons();
        final EditText password = findViewById(R.id.passwordInput);
        password.setTransformationMethod(new PasswordTransformationMethod()); //this is needed to set the input type to Password. if we do it in the xml we lose styling.
        networkActive = getIntent().getBooleanExtra("Network", true);

        if(!networkActive) {
            showToast("The server is experiencing issues, please try again later.");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgotPassword:
                Intent intent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.signInButton:
                attemptLogin();
                break;
            default:
                break;
        }
    }

    private void attemptLogin() {
        final EditText emailAddressEntry = findViewById(R.id.emailInput);
        final EditText passwordEntry = findViewById(R.id.passwordInput);
        emailAddress = emailAddressEntry.getText().toString().replaceAll(" ", "");
        password = passwordEntry.getText().toString().replaceAll(" ", "");
        new AsyncAuthenticator(this, emailAddress, password).execute();
    }

    private void initializeButtons(){
        TextView forgotButton = findViewById(R.id.forgotPassword);
        forgotButton.setOnClickListener(this);

        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
    }

    private void showToast(CharSequence msg){

        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("JWT")) {
            JWTObject jwt = (JWTObject) returnObject;
            SaveSharedPreference.setJWT(this, jwt.getJwt());
            SaveSharedPreference.setClientTimestamp(this, jwt.getTimestamp());
            SaveSharedPreference.setTransId(this, jwt.getTransId());
        }
        else {
            AsyncAgentJsonObject agentObject = (AsyncAgentJsonObject) returnObject;
            AgentModel agent = agentObject.getAgent();
            if (agentObject.getStatus_code().equals("-1")) {
                showToast("Incorrect username or password");
            } else {
                Log.e("AGENT OBJECT", agent.getAgent_id());
                SaveSharedPreference.setUserId(this, agent.getAgent_id());
                SaveSharedPreference.setUserName(this, emailAddress);
                try {
                    //TODO: We need to encrypt this in some way
                    SaveSharedPreference.setUserPassword(this, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, ParentActivity.class);
                intent.putExtra("Agent", agent);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast("The server is experiencing issues, please try again later.");
            }
        });
    }

}

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
import co.sisu.mobile.system.SaveSharedPreference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncServerEventListener {

    static final String PREF_USER_NAME= "username";
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
        final EditText emailAddress = findViewById(R.id.emailInput);
        final EditText password = findViewById(R.id.passwordInput);
        new AsyncAuthenticator(this, emailAddress.getText().toString().replaceAll(" ", ""), password.getText().toString().replaceAll(" ", "")).execute();
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
        AsyncAgentJsonObject agentObject = (AsyncAgentJsonObject) returnObject;
        AgentModel agent = agentObject.getAgent();
        Log.e("AGENT OBJECT", agent.getAgent_id());
        SaveSharedPreference.setUserName(this, agent.getAgent_id());
        Intent intent = new Intent(this, ParentActivity.class);
        intent.putExtra("Agent", agent);
        startActivity(intent);
        finish();
    }

    @Override
    public void onEventFailed() {

    }
}

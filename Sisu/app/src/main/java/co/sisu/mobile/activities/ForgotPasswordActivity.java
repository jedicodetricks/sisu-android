package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncForgotPassword;
import co.sisu.mobile.api.AsyncServerEventListener;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, AsyncServerEventListener {

    private EditText emailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_sign_in_layout);
        getSupportActionBar().setElevation(0);
        initUI();
    }

    public void initUI() {
        emailAddress = findViewById(R.id.forgotEmail);
        Button submitButton = findViewById(R.id.forgotPasswordSubmitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.forgotPasswordSubmitButton) {
            new AsyncForgotPassword(this, emailAddress.getText().toString()).execute();
        }
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        finish();
    }

    @Override
    public void onEventFailed(Object o, String s) {
        showToast("There was an error sending that request. Please try again.");
    }
}

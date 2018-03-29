package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncSignUp;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, AsyncServerEventListener {

    private EditText email, phone, firstName, lastName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_sign_in_layout);
        getSupportActionBar().setElevation(0);
        initInputFields();
        initButtons();
    }

    private void initButtons() {
        Button signUp = findViewById(R.id.signUpButton);
        signUp.setOnClickListener(this);
    }

    private void initInputFields() {
        email = findViewById(R.id.emailAddress);
        phone = findViewById(R.id.phoneNumber);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);

        password = findViewById(R.id.password);
        password.setTransformationMethod(new PasswordTransformationMethod());//this is needed to set the input type to Password. if we do it in the xml we lose styling.
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signUpButton) {
            new AsyncSignUp(this, email.getText().toString(), phone.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), password.getText().toString()).execute();
            Log.d("info", email.getText() + " " + phone.getText() + " " + firstName.getText() + " " + lastName.getText() + " " + password.getText());
        }
    }

    @Override
    public void onEventCompleted() {

    }

    @Override
    public void onEventFailed() {

    }
}
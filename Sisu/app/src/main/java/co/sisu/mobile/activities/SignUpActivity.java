package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import co.sisu.mobile.R;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_sign_in_layout);
        getSupportActionBar().setElevation(0);
        final EditText password = findViewById(R.id.password);
        password.setTransformationMethod(new PasswordTransformationMethod());//this is needed to set the input type to Password. if we do it in the xml we lose styling.
    }
}
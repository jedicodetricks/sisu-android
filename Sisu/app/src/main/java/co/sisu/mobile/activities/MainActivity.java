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
import co.sisu.mobile.api.Authenticator;

// TODO: 2/20/2018 remove Toasts with links/buttons when proper functionality replaces them

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        password.setTransformationMethod(new PasswordTransformationMethod());//this is needed to set the input type to Password. if we do it in the xml we lose styling.
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgotPassword:
                //do Stuff
                Intent intent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent);
//                showToast("forgot password clicked");
                break;
            case R.id.signUp:
            case R.id.needAccount:
                //do Stuff
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
//                showToast("sign up or need account clicked");
                break;
            case R.id.signInButton:
                attemptLogin();
                break;
            default:
                showToast("Oops!");
                break;
        }
    }

    private void attemptLogin() {
        final EditText emailAddress = findViewById(R.id.emailInput);
        final EditText password = findViewById(R.id.passwordInput);
        Authenticator authenticator = new Authenticator();
        authenticator.test(emailAddress.getText().toString().replaceAll(" ", ""), password.getText().toString().replaceAll(" ", ""));
        Intent intent = new Intent(this, ParentActivity.class);
        startActivity(intent);
    }

    private void initializeButtons(){
        TextView forgotButton = findViewById(R.id.forgotPassword);
        forgotButton.setOnClickListener(this);

        TextView signUpButton = findViewById(R.id.signUp);
        signUpButton.setOnClickListener(this);

        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        TextView needAccountButton = findViewById(R.id.needAccount);
        needAccountButton.setOnClickListener(this);
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}

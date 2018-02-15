package co.sisu.mobile.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import co.sisu.mobile.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSignInButton();
    }

    private void setupSignInButton() {
        Button signInButton = findViewById(R.id.signInButton);
        final EditText emailAddress = findViewById(R.id.emailInput);
        final EditText password = findViewById(R.id.passwordInput);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        startActivity(intent);

    }


}

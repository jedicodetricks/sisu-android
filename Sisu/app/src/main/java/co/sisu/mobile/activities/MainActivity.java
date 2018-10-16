package co.sisu.mobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import co.sisu.mobile.ApiReturnTypes;
import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncAuthenticator;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.AsyncTeamColorSchemeObject;
import co.sisu.mobile.models.JWTObject;
import co.sisu.mobile.models.TeamColorSchemeObject;
import co.sisu.mobile.system.SaveSharedPreference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncServerEventListener {

    private String emailAddress;
    private String passwordText;
    private boolean networkActive = true;
    private Button signInButton;
    private ProgressBar loader;
    private AgentModel agent;
    private TeamColorSchemeObject[] colorScheme;
    ConstraintLayout layout;
    TextInputEditText emailAddressEntry, passwordEntry;
    TextInputLayout emailSignInLayout, passwordSignInLayout;
    TextView forgotPassword, legal;
    ImageView logo, sisuPowerLogo;
    int authRetry = 0;
    ApiManager apiManager;
    ColorSchemeManager colorSchemeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiManager = new ApiManager(this);
        colorSchemeManager = new ColorSchemeManager();
        setContentView(R.layout.activity_main);
        Log.e("Screen Density: ", getDeviceDensity(this));
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.action_bar_sign_in_layout);
//        getSupportActionBar().setElevation(0);
        loader = findViewById(R.id.signInLoader);
        initializeButtons();
        initFields();
        getSavedData();
        passwordEntry.setTransformationMethod(new PasswordTransformationMethod()); //this is needed to set the input type to Password. if we do it in the xml we lose styling.
        passwordEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    handled = true;
                }
                return handled;
            }
        });
        networkActive = getIntent().getBooleanExtra("Network", true);

    }

    private void setColors() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                emailAddressEntry.setTextColor(colorSchemeManager.getDarkerTextColor());
                passwordEntry.setTextColor(colorSchemeManager.getDarkerTextColor());
                forgotPassword.setTextColor(colorSchemeManager.getDarkerTextColor());
                legal.setTextColor(colorSchemeManager.getDarkerTextColor());

                setInputTextLayoutColor(emailSignInLayout, colorSchemeManager.getIconActive());
                setInputTextLayoutColor(passwordSignInLayout, colorSchemeManager.getIconActive());

                signInButton.setHighlightColor(colorSchemeManager.getButtonSelected());
                signInButton.setBackgroundResource(R.drawable.rounded_button);
                GradientDrawable drawable = (GradientDrawable) signInButton.getBackground();
                drawable.setColor(colorSchemeManager.getButtonBackground());

                layout.setBackgroundColor(colorSchemeManager.getAppBackground());
            }
        });
    }

    private void setInputTextLayoutColor(TextInputLayout layout, int color) {
        try {

            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(layout, new ColorStateList(new int[][]{{0}}, new int[]{ color }));

            //Field fDefaultLineColor = TextInputLayout.class.getDeclaredField("")

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(layout, new ColorStateList(new int[][]{{0}}, new int[]{ color }));

            Method method = layout.getClass().getDeclaredMethod("updateLabelState", boolean.class);
            method.setAccessible(true);
            method.invoke(layout, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSavedData() {
        if(SaveSharedPreference.getUserId(this).length() > 0) {
            Log.e("SAVE DATA", "GOT EEM");
            apiManager.getColorScheme(this, SaveSharedPreference.getUserId(this), Integer.parseInt(SaveSharedPreference.getTeam(this)), SaveSharedPreference.getLights(this));
            if(SaveSharedPreference.getLogo(this).length() > 0) {
                Picasso.with(this).load(Uri.parse(SaveSharedPreference.getLogo(this))).into(logo);
                sisuPowerLogo.setVisibility(View.VISIBLE);
            }
        }
    }

    public String getDeviceDensity(Context context){
        String deviceDensity = "";
        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                deviceDensity =  0.75 + " ldpi";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                deviceDensity =  1.0 + " mdpi";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                deviceDensity =  1.5 + " hdpi";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                deviceDensity =  2.0 + " xhdpi";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                deviceDensity =  3.0 + " xxhdpi";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                deviceDensity =  4.0 + " xxxhdpi";
                break;
            default:
                deviceDensity = "Not found";
        }
        return deviceDensity;
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

    private void initFields() {
        emailAddressEntry = findViewById(R.id.emailInput);
        emailAddressEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        passwordEntry = findViewById(R.id.passwordInput);
        passwordEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        forgotPassword = findViewById(R.id.forgotPassword);
        legal = findViewById(R.id.legal);
        logo = findViewById(R.id.logo);
        emailSignInLayout = findViewById(R.id.emailSignInLayout);
        passwordSignInLayout = findViewById(R.id.passwordSignInLayout);
        sisuPowerLogo = findViewById(R.id.sisuPowerLogo);
        layout = findViewById(R.id.logIn);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void attemptLogin() {
        loader.setVisibility(View.VISIBLE);
        final EditText emailAddressEntry = findViewById(R.id.emailInput);
        final EditText passwordEntry = findViewById(R.id.passwordInput);
        emailAddress = emailAddressEntry.getText().toString().replaceAll(" ", "");
        passwordText = passwordEntry.getText().toString().replaceAll(" ", "");
        new AsyncAuthenticator(this, emailAddress, passwordText).execute();
    }

    private void initializeButtons(){
        TextView forgotButton = findViewById(R.id.forgotPassword);
        forgotButton.setOnClickListener(this);

        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
    }

    private void showToast(final CharSequence msg){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this, msg,Toast.LENGTH_SHORT);
                View view = toast.getView();
                TextView text = (TextView) view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorCorporateOrange));
                view.setBackgroundResource(R.color.colorCorporateOrange);
                text.setPadding(20, 8, 20, 8);
                toast.show();
            }
        });

    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("JWT")) {
            JWTObject jwt = (JWTObject) returnObject;
            SaveSharedPreference.setJWT(this, jwt.getJwt());
            SaveSharedPreference.setClientTimestamp(this, jwt.getTimestamp());
            SaveSharedPreference.setTransId(this, jwt.getTransId());
        }
        else if(asyncReturnType.equals("Get Color Scheme")) {
            AsyncTeamColorSchemeObject colorJson = (AsyncTeamColorSchemeObject) returnObject;
            colorScheme = colorJson.getTheme();
            colorSchemeManager.setColorScheme(colorScheme, SaveSharedPreference.getLights(this));
            setColors();
        }
        else {
            AsyncAgentJsonObject agentObject = (AsyncAgentJsonObject) returnObject;
            agent = agentObject.getAgent();
            if (agentObject.getStatus_code().equals("-1")) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                        showToast("Incorrect username or password");
                    }
                });
            }
            else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                    }
                });
                Log.e("AGENT OBJECT", agent.getAgent_id());
                SaveSharedPreference.setUserId(this, agent.getAgent_id());
                SaveSharedPreference.setUserName(this, emailAddress);
                try {
                    //TODO: We need to encrypt this in some way
                    SaveSharedPreference.setUserPassword(this, passwordText);
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
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

        if(asyncReturnType.equals("Authenticator")) {
            if(authRetry == 2) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("The server is experiencing issues. Please try again later.");
                    }
                });
            }
            else {
                new AsyncAuthenticator(this, emailAddress, passwordText).execute();
                authRetry++;
            }
        }
        else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("The server experienced an issue, please try again.");
                }
            });
        }

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }

}

package co.sisu.mobile.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.NavigationManager;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class ChangePasswordFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private EditText oldPassword, newPassword, confirmPassword;
    private ParentActivity parentActivity;
    private NavigationManager navigationManager;
    private ApiManager apiManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.activity_change_password, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        initUI();
    }

    public void initUI() {
        oldPassword = getView().findViewById(R.id.oldPassword);
        oldPassword.setTransformationMethod(new PasswordTransformationMethod()); //this is needed to set the input type to Password. if we do it in the xml we lose styling.

        newPassword = getView().findViewById(R.id.newPassword);
        newPassword.setTransformationMethod(new PasswordTransformationMethod()); //this is needed to set the input type to Password. if we do it in the xml we lose styling.

        confirmPassword = getView().findViewById(R.id.confirmPassword);
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod()); //this is needed to set the input type to Password. if we do it in the xml we lose styling.

        Button submitButton = getView().findViewById(R.id.changePasswordSubmitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.changePasswordSubmitButton) {
            if(areFieldsValid()) {
                Log.e("SAME", "SAME");
                //TODO: API call for password changing.
            }
        }
    }

    private boolean areFieldsValid() {
        if(oldPassword.getText().toString().equals("")) {
            parentActivity.showToast("You need to enter your old password");
            return false;
        }
        else if(newPassword.getText().toString().equals("") || confirmPassword.getText().toString().equals("")) {
            parentActivity.showToast("You need to enter a new password");
            return false;
        }
        else if(!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            parentActivity.showToast("New password must match. Please try again.");
            return false;
        }

        return true;
    }

    private boolean arePasswordsTheSame() {
        if(!newPassword.getText().toString().equals("") && !confirmPassword.getText().toString().equals("")) {
            if(newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        Log.e("COMPLETE", "YES");
        //TODO: This needs to go back to My Profile
    }

    @Override
    public void onEventFailed(Object o, String s) {
        //showToast("There was an error sending that request. Please try again.");
    }
}

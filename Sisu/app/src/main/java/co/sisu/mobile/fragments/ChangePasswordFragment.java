package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.system.SaveSharedPreference;
import co.sisu.mobile.utils.Utils;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class ChangePasswordFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private EditText oldPassword, newPassword, confirmPassword;
    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private Utils utils;
    private Button submitButton;

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
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        utils = parentActivity.getUtils();
        initUI();
        setColorScheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "ChangePasswordFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ParentActivity");
        FirebaseAnalytics.getInstance(parentActivity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void setColorScheme() {
        submitButton.setTextColor(colorSchemeManager.getButtonText());
    }

    public void initUI() {
        oldPassword = getView().findViewById(R.id.oldPassword);
        oldPassword.setTransformationMethod(new PasswordTransformationMethod()); //this is needed to set the input type to Password. if we do it in the xml we lose styling.

        newPassword = getView().findViewById(R.id.newPassword);
        newPassword.setTransformationMethod(new PasswordTransformationMethod()); //this is needed to set the input type to Password. if we do it in the xml we lose styling.

        confirmPassword = getView().findViewById(R.id.confirmPassword);
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod()); //this is needed to set the input type to Password. if we do it in the xml we lose styling.

        submitButton = getView().findViewById(R.id.changePasswordSubmitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.changePasswordSubmitButton) {
            if(areFieldsValid()) {
                String userName = SaveSharedPreference.getUserName(parentActivity);
                apiManager.sendAuth(this, dataController.getAgent().getAgent_id(), userName, oldPassword.getText().toString());
            }
        }
    }

    private boolean areFieldsValid() {
        if(oldPassword.getText().toString().equals("")) {
            utils.showToast("You need to enter your old password", parentActivity);
            return false;
        }
        else if(newPassword.getText().toString().equals("") || confirmPassword.getText().toString().equals("")) {
            utils.showToast("You need to enter a new password", parentActivity);
            return false;
        }
        else if(!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            utils.showToast("New password must match. Please try again.", parentActivity);
            return false;
        }

        return true;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        // TODO: Move these to the new format
        if(asyncReturnType.equals("Authenticator")) {
            AsyncAgentJsonObject agentObject = (AsyncAgentJsonObject) returnObject;
            if(agentObject.getStatus_code().equals("-1")) {
                utils.showToast("Incorrect password", parentActivity);
            }
            else {
                HashMap<String, String> changedFields = new HashMap<>();
                changedFields.put("password", confirmPassword.getText().toString());
                apiManager.sendAsyncUpdateProfile(this, dataController.getAgent().getAgent_id(), changedFields);
            }
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.UPDATE_PROFILE) {
            utils.showToast("Password successfully changed", parentActivity);
            navigationManager.onBackPressed();
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Update Profile")) {
            utils.showToast("Issue with password change. Please try again later.", parentActivity);
        }
    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }
}

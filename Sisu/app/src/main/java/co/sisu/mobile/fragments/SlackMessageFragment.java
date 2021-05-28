package co.sisu.mobile.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.NotesObject;
import co.sisu.mobile.utils.Utils;

/**
 * Created by bradygroharing on 7/18/18.
 */

public class SlackMessageFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private Utils utils;
    private EditText noteText;
    private TextView sendSlackButton, cancelButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_add_note, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        utils = parentActivity.getUtils();
        apiManager = parentActivity.getApiManager();
        initForm();
        initUpdateOrAdd();
        setColorScheme();
    }

    private void setColorScheme() {
        //TODO: This shouldn't work like this. Discuss current design with Rick.
        if(colorSchemeManager.getAppBackground() == Color.WHITE) {
            noteText.setBackgroundResource(R.drawable.light_input_text_box);
        } else {
            noteText.setBackgroundResource(R.drawable.input_text_box);
        }
        noteText.setTextColor(colorSchemeManager.getDarkerTextColor());
    }

    private void initUpdateOrAdd() {
        NotesObject notesObject = parentActivity.getSelectedNote();
        if(notesObject != null) {
            noteText.setText(notesObject.getNote());
        }
    }

    private void initForm() {
        noteText = getView().findViewById(R.id.addNoteEditText);
        sendSlackButton = parentActivity.findViewById(R.id.saveButton);
        if(sendSlackButton != null) {
            if(!parentActivity.getIsNoteFragment()) {
                sendSlackButton.setText("Send");
            }
            sendSlackButton.setOnClickListener(this);
        }
        cancelButton = parentActivity.findViewById(R.id.cancelButton);
        if(cancelButton != null) {
            cancelButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                if(!noteText.getText().toString().equals("")) {
                    if(parentActivity.getIsNoteFragment()) {
                        apiManager.sendAsyncFeedback(this, dataController.getAgent().getAgent_id(), noteText.getText().toString(), dataController.getSlackInfo());
                        utils.showToast("Sending message to your Slack channel...", parentActivity, colorSchemeManager);
                    }
                    else {
                        apiManager.sendPushNotification(this, dataController.getAgent().getAgent_id(), String.valueOf(dataController.getCurrentSelectedTeamId()), noteText.getText().toString());
                        utils.showToast("Sending push notification to your team...", parentActivity, colorSchemeManager);
                    }
                    hideKeyboard(getView());
                    navigationManager.onBackPressed();
                }
                else {
                    utils.showToast("Please enter some text.", parentActivity, colorSchemeManager);
                }
                break;
            case R.id.cancelButton:
                navigationManager.onBackPressed();
                break;
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        // TODO: Move these to the new format
        if(asyncReturnType.equals("Add Notes")) {
            hideKeyboard(getView());
            utils.showToast("Added note", parentActivity, colorSchemeManager);
            navigationManager.onBackPressed();
        }
        else if(asyncReturnType.equals("Update Notes")) {
            hideKeyboard(getView());
            utils.showToast("Updated note", parentActivity, colorSchemeManager);
            navigationManager.onBackPressed();
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.SEND_FEEDBACK) {

        }
        else if(returnType == ApiReturnType.SEND_PUSH_NOTIFICATION) {

        }
        else if(returnType == ApiReturnType.CREATE_NOTE) {

        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }
}

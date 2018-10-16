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

import co.sisu.mobile.ApiReturnTypes;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.NotesObject;

/**
 * Created by bradygroharing on 7/18/18.
 */

public class SlackMessageFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private EditText noteText;
    private TextView sendSlackButton, cancelButton;
    private boolean isUpdate = false;


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
        apiManager = parentActivity.getApiManager();
        initForm();
        initUpdateOrAdd();
        setColorScheme();
    }

    private void setColorScheme() {
        //TODO: This shouldn't work like this. Discuss current design with Rick.
        if(parentActivity.colorSchemeManager.getAppBackground() == Color.WHITE) {
            noteText.setBackgroundResource(R.drawable.light_input_text_box);
        } else {
            noteText.setBackgroundResource(R.drawable.input_text_box);
        }
        noteText.setTextColor(parentActivity.colorSchemeManager.getDarkerTextColor());
    }

    private void initUpdateOrAdd() {
        NotesObject notesObject = parentActivity.getSelectedNote();
        if(notesObject != null) {
            noteText.setText(notesObject.getNote());
            isUpdate = true;
        }
    }

    private void initForm() {
        noteText = getView().findViewById(R.id.addNoteEditText);
        sendSlackButton = parentActivity.findViewById(R.id.addClientSaveButton);
        if(sendSlackButton != null) {
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
            case R.id.addClientSaveButton:
                if(!noteText.getText().toString().equals("")) {
                    apiManager.sendAsyncFeedback(this, dataController.getAgent().getAgent_id(), noteText.getText().toString(), dataController.getSlackInfo());
                    parentActivity.showToast("Sending message to your Slack channel.");
                    hideKeyboard(getView());
                    navigationManager.onBackPressed();
                }
                else {
                    parentActivity.showToast("Please enter some text.");
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
        if(asyncReturnType.equals("Add Notes")) {
            hideKeyboard(getView());
            parentActivity.showToast("Added note");
            navigationManager.onBackPressed();
        }
        else if(asyncReturnType.equals("Update Notes")) {
            hideKeyboard(getView());
            parentActivity.showToast("Updated note");
            navigationManager.onBackPressed();
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }
}

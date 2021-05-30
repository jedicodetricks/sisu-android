package co.sisu.mobile.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class AddNoteFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private Utils utils;
    private EditText noteText;
    private TextView addNoteButton;
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
        colorSchemeManager = parentActivity.getColorSchemeManager();
        utils = parentActivity.getUtils();
        initForm();
        setColors();
        initUpdateOrAdd();
    }

    private void setColors() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(colorSchemeManager.getAppBackground() == Color.WHITE) {
                    Log.e("NOTE", "WHITE");
                    noteText.setBackground(parentActivity.getResources().getDrawable(R.drawable.light_input_text_box));
                } else {
                    noteText.setBackground(parentActivity.getResources().getDrawable(R.drawable.input_text_box));
                }
                noteText.setTextColor(colorSchemeManager.getDarkerText());
            }
        });
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
        addNoteButton = parentActivity.findViewById(R.id.saveButton);
        if(addNoteButton != null) {
            addNoteButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                Log.e("ADD", noteText.getText().toString());
                if(!noteText.getText().toString().equals("")) {
                    if(isUpdate) {
                        NotesObject note = parentActivity.getSelectedNote();
                        apiManager.updateNote(this, dataController.getAgent().getAgent_id(), note.getId(), noteText.getText().toString(), note.getLog_type_id());
                    }
                    else {
                        apiManager.addNote(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id(), noteText.getText().toString(), "NOTES");
                    }
                }
                else {
                    utils.showToast("Please enter some text in the note field.", parentActivity, colorSchemeManager);
                }
                break;
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.CREATE_NOTE) {
            hideKeyboard(getView());
            utils.showToast("Added note", parentActivity, colorSchemeManager);
            navigationManager.onBackPressed();
        }
        else if(returnType == ApiReturnType.UPDATE_NOTE) {
            hideKeyboard(getView());
            utils.showToast("Updated note", parentActivity, colorSchemeManager);
            navigationManager.onBackPressed();
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }
}

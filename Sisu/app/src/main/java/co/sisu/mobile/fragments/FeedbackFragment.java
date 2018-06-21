package co.sisu.mobile.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncFeedback;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.DataController;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private EditText feedback;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_feedback, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);

        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        Button feedbackButton = view.findViewById(R.id.submitFeedbackButton);
        feedbackButton.setOnClickListener(this);
        feedback = view.findViewById(R.id.feedbackEditText);
        feedback.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.submitFeedbackButton){
            sendFeedback(feedback.getText().toString());
            //do animation thank you
        }
    }

    private void sendFeedback(String feedback){
        apiManager.sendAsyncFeedback(this, dataController.getAgent().getAgent_id(), feedback);
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        feedback.setText("");
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.showToast("Thank you for your feedback");
            }
        });
    }

    @Override
    public void onEventFailed(Object o, String s) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.showToast("We had an issue recording your feedback. Please try again later.");
            }
        });
    }
}

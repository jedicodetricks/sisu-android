package co.sisu.mobile.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncFeedback;
import co.sisu.mobile.api.AsyncServerEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    EditText feedback;
    ParentActivity parentActivity;

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
        Button feedbackButton = view.findViewById(R.id.submitFeedbackButton);
        feedbackButton.setOnClickListener(this);
        feedback = view.findViewById(R.id.feedbackEditText);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.submitFeedbackButton){
            sendFeedback(feedback.getText().toString());
            //do animation thank you
        }
    }

    private void sendFeedback(String feedback){
        new AsyncFeedback(this, parentActivity.getAgentInfo().getAgent_id(), feedback).execute();
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Thank you for your feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEventFailed() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "We had an issue recording your feedback. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

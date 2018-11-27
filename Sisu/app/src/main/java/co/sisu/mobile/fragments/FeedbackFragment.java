package co.sisu.mobile.fragments;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import co.sisu.mobile.ApiReturnTypes;
import com.squareup.picasso.Picasso;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private EditText feedback;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private TextView feedbackHelpTextTop, feedbackHelpTextBottom;
    private Button feedbackButton;
    private ImageView sisuPowerLogo, sisuLogo;

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
        colorSchemeManager = parentActivity.getColorSchemeManager();
        feedbackButton = view.findViewById(R.id.submitFeedbackButton);
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
        initFields();
        setColorScheme();
    }

    private void setColorScheme() {
        feedbackHelpTextTop.setTextColor(colorSchemeManager.getDarkerTextColor());
        feedbackHelpTextBottom.setTextColor(colorSchemeManager.getDarkerTextColor());

        feedbackButton.setTextColor(colorSchemeManager.getButtonText());
        feedbackButton.setBackgroundResource(R.drawable.rounded_button);
        GradientDrawable drawable = (GradientDrawable) feedbackButton.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        feedback.setTextColor(colorSchemeManager.getDarkerTextColor());
        if(colorSchemeManager.getLogo() != null && !colorSchemeManager.getLogo().equals("sisu-logo-lg")) {
            Picasso.with(parentActivity).load(Uri.parse(colorSchemeManager.getLogo())).into(sisuLogo);
            SaveSharedPreference.setLogo(parentActivity, colorSchemeManager.getLogo());
            sisuPowerLogo.setVisibility(View.VISIBLE);
        }
        //TODO: This shouldn't work like this. Discuss current design with Rick.
        if(colorSchemeManager.getAppBackground() == Color.WHITE) {
            feedback.setBackgroundResource(R.drawable.light_input_text_box);
        } else {
            feedback.setBackgroundResource(R.drawable.input_text_box);
        }

    }

    private void initFields() {
        feedbackHelpTextTop = getView().findViewById(R.id.feedbackHelpTextTop);
        feedbackHelpTextBottom = getView().findViewById(R.id.feedbackHelpTextBottom);
        sisuPowerLogo = getView().findViewById(R.id.sisuPowerLogo);
        sisuLogo = getView().findViewById(R.id.sisuLogo);
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
        apiManager.sendAsyncFeedback(this, dataController.getAgent().getAgent_id(), feedback, null);
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
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {

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

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }
}

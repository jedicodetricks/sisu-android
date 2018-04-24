package co.sisu.mobile.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;
import co.sisu.mobile.models.UpdateAgentGoalsObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalSetupFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener, View.OnClickListener {

    EditText desiredIncome, trackingReasons, contacts, bAppointments, sAppointments, bSigned, sSigned, bContract, sContract, bClosed, sClosed;
    ParentActivity parentActivity;
    Switch timelineSwitch;
    TextView activityTitle;
    AsyncUpdateAgentGoalsJsonObject updateAgentGoalsJsonObject;
    UpdateAgentGoalsObject currentGoals;

    public GoalSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ScrollView contentView = (ScrollView) inflater.inflate(R.layout.fragment_setup, container, false);
        ScrollView.LayoutParams viewLayout = new ScrollView.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        initFields();
        initSwitch();
        setupFieldsWithGoalData(true);
    }

    private void initSwitch() {
        timelineSwitch = getView().findViewById(R.id.goalsTimelineSelector);
        timelineSwitch.setChecked(true);
        timelineSwitch.setOnCheckedChangeListener(this);
    }

    private void setupFieldsWithGoalData(boolean isAnnual) {
        AgentModel agent = parentActivity.getAgentInfo();

        if(isAnnual) {
            activityTitle.setText(R.string.yearlyTitle);
            desiredIncome.setText(agent.getDesired_income());
        }
        else {
            activityTitle.setText(R.string.monthlyTitle);
            String formattedIncome = agent.getDesired_income().replace(".0", "");
            int toDisplay = Integer.valueOf(formattedIncome) / 12;
            desiredIncome.setText(String.valueOf(toDisplay));
        }


        trackingReasons.setText(agent.getVision_statement());
        for(AgentGoalsObject go : agent.getAgentGoalsObject()) {
//            Log.e("Goals Setup", go.getName() + " " + go.getValue());
            String value = go.getValue();
            if(!isAnnual) {
                value = String.valueOf(Integer.valueOf(go.getValue()) / 12);
            }
            switch (go.getName()) {
                case "Contacts":
                    contacts.setText(value);
                    break;
                case "Sellers Closed":
                    sClosed.setText(value);
                    break;
                case "Sellers Under Contract":
                    sContract.setText(value);
                    break;
                case "Seller Appointments":
                    sAppointments.setText(value);
                    break;
                case "Sellers Signed":
                    sSigned.setText(value);
                    break;
                case "Buyers Signed":
                    bSigned.setText(value);
                    break;
                case "Buyer Appointments":
                    bAppointments.setText(value);
                    break;
                case "Buyers Under Contract":
                    bContract.setText(value);
                    break;
                case "Buyers Closed":
                    bClosed.setText(value);
                    break;
            }
        }

    }

    private void initFields() {
        desiredIncome = getView().findViewById(R.id.desiredIncome);
        desiredIncome.setOnFocusChangeListener(this);
        trackingReasons = getView().findViewById(R.id.goalsReason);
        trackingReasons.setOnFocusChangeListener(this);
        contacts = getView().findViewById(R.id.contacts);
        contacts.setOnFocusChangeListener(this);
        bAppointments = getView().findViewById(R.id.buyerAppts);
        bAppointments.setOnFocusChangeListener(this);
        sAppointments = getView().findViewById(R.id.sellerAppts);
        sAppointments.setOnFocusChangeListener(this);
        bSigned = getView().findViewById(R.id.signedBuyers);
        bSigned.setOnFocusChangeListener(this);
        sSigned = getView().findViewById(R.id.signedSellers);
        sSigned.setOnFocusChangeListener(this);
        bContract = getView().findViewById(R.id.buyersUnderContract);
        bContract.setOnFocusChangeListener(this);
        sContract = getView().findViewById(R.id.sellersUnderContract);
        sContract.setOnFocusChangeListener(this);
        bClosed = getView().findViewById(R.id.buyersClosed);
        bClosed.setOnFocusChangeListener(this);
        sClosed = getView().findViewById(R.id.sellersClosed);
        sClosed.setOnFocusChangeListener(this);
        activityTitle = getView().findViewById(R.id.activityTitle);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setupFieldsWithGoalData(isChecked);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.e("FOCUS", String.valueOf(v.getId()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton://notify of success update api
                //TODO: I assume we just want to go back to the client page, not the scoreboard
                updateCurrentGoals();
                saveGoals();
                parentActivity.stackReplaceFragment(MoreFragment.class);
                parentActivity.swapToBacktionBar("More");
                break;
        }
    }

    private void updateCurrentGoals() {
        //set all currentGoals object with data from user
    }

    private void saveGoals() {
        // TODO: 4/24/2018 async call
    }
}

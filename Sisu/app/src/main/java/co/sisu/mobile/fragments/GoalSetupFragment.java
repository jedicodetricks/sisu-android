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
public class GoalSetupFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    EditText desiredIncome, trackingReasons, contacts, bAppointments, sAppointments, bSigned, sSigned, bContract, sContract, bClosed, sClosed;
    ParentActivity parentActivity;
    Switch timelineSwitch;
    TextView activityTitle;
    AsyncUpdateAgentGoalsJsonObject updateAgentGoalsJsonObject;

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
        if(isAnnual) {
            activityTitle.setText(R.string.yearlyTitle);
        }
        else {
            activityTitle.setText(R.string.monthlyTitle);
        }

        AgentModel agent = parentActivity.getAgentInfo();
        desiredIncome.setText(agent.getDesired_income());
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
        trackingReasons = getView().findViewById(R.id.goalsReason);
        contacts = getView().findViewById(R.id.contacts);
        bAppointments = getView().findViewById(R.id.buyerAppts);
        sAppointments = getView().findViewById(R.id.sellerAppts);
        bSigned = getView().findViewById(R.id.signedBuyers);
        sSigned = getView().findViewById(R.id.signedSellers);
        bContract = getView().findViewById(R.id.buyersUnderContract);
        sContract = getView().findViewById(R.id.sellersUnderContract);
        bClosed = getView().findViewById(R.id.buyersClosed);
        sClosed = getView().findViewById(R.id.sellersClosed);
        activityTitle = getView().findViewById(R.id.monthlyTitle);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setupFieldsWithGoalData(isChecked);

    }
}

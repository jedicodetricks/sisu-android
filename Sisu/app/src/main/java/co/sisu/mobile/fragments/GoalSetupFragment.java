package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncAgent;
import co.sisu.mobile.api.AsyncAgentGoals;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncUpdateAgent;
import co.sisu.mobile.api.AsyncUpdateGoals;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;
import co.sisu.mobile.models.UpdateAgentGoalsObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalSetupFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener, AsyncServerEventListener {

    EditText desiredIncome, trackingReasons, contacts, bAppointments, sAppointments, bSigned, sSigned, bContract, sContract, bClosed, sClosed;
    ParentActivity parentActivity;
    //Switch timelineSwitch;
    TextView activityTitle, saveButton;
    private boolean dateSwap;
    private boolean isAnnualChecked = false;
    private List<EditText> fieldsObject;
    private HashMap<String, UpdateAgentGoalsObject> updatedGoals;
    private AgentModel agent;
    private AgentGoalsObject[] currentGoalsObject;
    private String income = "";
    private String reason = "";

    private boolean agentUpdating = false;
    private boolean goalsUpdated;
    private boolean agentUpdated;

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
        updatedGoals = new HashMap<>();
        initFields();
        initSwitchAndButtons();
        agent = parentActivity.getAgentInfo();
//        TODO: We need to go and get agent as well
        goalsUpdated = false;
        agentUpdated = false;
        income = "";
        reason = "";
        new AsyncAgentGoals(this, agent.getAgent_id(), parentActivity.getSelectedTeamId()).execute();
        new AsyncAgent(this, agent.getAgent_id()).execute();
    }

    private void initSwitchAndButtons() {
//        timelineSwitch = getView().findViewById(R.id.goalsTimelineSelector);
//        timelineSwitch.setChecked(true);
//        timelineSwitch.setOnCheckedChangeListener(this);

        saveButton = parentActivity.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
    }

    private void setupFieldsWithGoalData(final boolean isAnnual) {
        dateSwap = true;
        currentGoalsObject = agent.getAgentGoalsObject();

        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isAnnual) {
                    activityTitle.setText(R.string.yearlyTitle);
                    if(income.equals("")) {
                        desiredIncome.setText(agent.getDesired_income() == null ? "" : agent.getDesired_income().replace(".0", ""));
                    }
                    else {
                        desiredIncome.setText(income);

                    }
                }
                else {
                    activityTitle.setText(R.string.monthlyTitle);
                    String formattedIncome = "";
                    if(income.equals("")) {
                        if(agent.getDesired_income() != null) {
                            formattedIncome = agent.getDesired_income().replace(".0", "");
                        }
                    }
                    else {
                        formattedIncome = income;
                    }
                    String toDisplay = "";
                    if(!formattedIncome.equals("")) {
                        toDisplay = String.valueOf(Integer.valueOf(formattedIncome) / 12);
                    }
                    desiredIncome.setText(String.valueOf(toDisplay));
                }

                if(reason.equals("")) {
                    trackingReasons.setText(agent.getVision_statement());
                }
                else {
                    trackingReasons.setText(reason);
                }

                for(AgentGoalsObject go : currentGoalsObject) {
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
                dateSwap = false;
            }
        });

    }

    private void initFields() {
        fieldsObject = new ArrayList<>();
        desiredIncome = getView().findViewById(R.id.desiredIncome);
        desiredIncome.addTextChangedListener(this);
        trackingReasons = getView().findViewById(R.id.goalsReason);
        trackingReasons.addTextChangedListener(this);

        contacts = getView().findViewById(R.id.contacts);
        contacts.addTextChangedListener(this);
        fieldsObject.add(contacts);
        bAppointments = getView().findViewById(R.id.buyerAppts);
        bAppointments.addTextChangedListener(this);
        fieldsObject.add(bAppointments);
        sAppointments = getView().findViewById(R.id.sellerAppts);
        sAppointments.addTextChangedListener(this);
        fieldsObject.add(sAppointments);
        bSigned = getView().findViewById(R.id.signedBuyers);
        bSigned.addTextChangedListener(this);
        fieldsObject.add(bSigned);
        sSigned = getView().findViewById(R.id.signedSellers);
        sSigned.addTextChangedListener(this);
        fieldsObject.add(sSigned);
        bContract = getView().findViewById(R.id.buyersUnderContract);
        bContract.addTextChangedListener(this);
        fieldsObject.add(bContract);
        sContract = getView().findViewById(R.id.sellersUnderContract);
        sContract.addTextChangedListener(this);
        fieldsObject.add(sContract);
        bClosed = getView().findViewById(R.id.buyersClosed);
        bClosed.addTextChangedListener(this);
        fieldsObject.add(bClosed);
        sClosed = getView().findViewById(R.id.sellersClosed);
        sClosed.addTextChangedListener(this);
        fieldsObject.add(sClosed);
        activityTitle = getView().findViewById(R.id.activityTitle);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isAnnualChecked = isChecked;
        setupFieldsWithGoalData(isChecked);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if(!dateSwap && !s.toString().equals("")) {
            if(contacts.getText().hashCode() == s.hashCode())
            {
                updateField("CONTA", Integer.valueOf(String.valueOf(s)));
            }
            else if(bAppointments.getText().hashCode() == s.hashCode())
            {
                updateField("BAPPT", Integer.valueOf(String.valueOf(s)));
            }
            else if(sAppointments.getText().hashCode() == s.hashCode())
            {
                updateField("SAPPT", Integer.valueOf(String.valueOf(s)));
            }
            else if(bSigned.getText().hashCode() == s.hashCode())
            {
                updateField("BSGND", Integer.valueOf(String.valueOf(s)));
            }
            else if(sSigned.getText().hashCode() == s.hashCode())
            {
                updateField("SSGND", Integer.valueOf(String.valueOf(s)));
            }
            else if(bContract.getText().hashCode() == s.hashCode())
            {
                updateField("BUNDC", Integer.valueOf(String.valueOf(s)));
            }
            else if(sContract.getText().hashCode() == s.hashCode())
            {
                updateField("SUNDC", Integer.valueOf(String.valueOf(s)));
            }
            else if(bClosed.getText().hashCode() == s.hashCode())
            {
                updateField("BCLSD", Integer.valueOf(String.valueOf(s)));
            }
            else if(sClosed.getText().hashCode() == s.hashCode())
            {
                updateField("SCLSD", Integer.valueOf(String.valueOf(s)));
            }
            else if (desiredIncome.getText().hashCode() == s.hashCode()) {
                updateProfile("Income", s.toString());
//                Log.e("DESIRED INCOME", s.toString());
            }
            else if(trackingReasons.getText().hashCode() == s.hashCode()) {
                updateProfile("Reasons", s.toString());
//                Log.e("TRACKING REASONS", s.toString());
            }
        }
    }

    private void updateProfile(String type, String value) {
        if(type.equals("Income")) {
            income = value;
        }
        else {
            reason = value;
        }
    }

    private void updateField(String fieldName, int value) {
        if(!isAnnualChecked) {
            value = value * 12;
        }
        AgentGoalsObject selectedGoal = null;

        for(AgentGoalsObject ago : currentGoalsObject) {
            if(ago.getGoal_id().equals(fieldName)) {
                selectedGoal = ago;
                break;
            }
        }
        UpdateAgentGoalsObject toUpdate = new UpdateAgentGoalsObject(fieldName, String.valueOf(value));

        if(selectedGoal == null) {
            currentGoalsObject[currentGoalsObject.length] = selectedGoal;
            updatedGoals.put(fieldName, toUpdate);
        }
        else {
            selectedGoal.setValue(String.valueOf(value));
            updatedGoals.put(fieldName, toUpdate);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                UpdateAgentGoalsObject[] array = new UpdateAgentGoalsObject[updatedGoals.size()];
                int counter = 0;
                for ( String key : updatedGoals.keySet() ) {
                    UpdateAgentGoalsObject value = updatedGoals.get(key);
                    array[counter] = value;
                    counter++;
                }
                new AsyncUpdateGoals(this, agent.getAgent_id(), new AsyncUpdateAgentGoalsJsonObject(array)).execute();
                if(!income.equals("") || !reason.equals("")) {
                    agentUpdating = true;
                    new AsyncUpdateAgent(this, agent.getAgent_id(), income, reason).execute();
                }
                break;
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Update Goals")) {
            if(!agentUpdating) {
                updatedGoals = new HashMap<>();
                parentActivity.showToast("Goals have been updated");
                parentActivity.stackReplaceFragment(MoreFragment.class);
                parentActivity.swapToTitleBar("More");
            }
        }
        else if(asyncReturnType.equals("Goals")) {
            AsyncGoalsJsonObject goals = (AsyncGoalsJsonObject) returnObject;
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            parentActivity.setAgentGoals(agentGoalsObject);
            goalsUpdated = true;
            if(agentUpdated) {
                setupFieldsWithGoalData(isAnnualChecked);
            }
        }
        else if(asyncReturnType.equals("Update Agent")) {
            agentUpdating = false;
            updatedGoals = new HashMap<>();
            parentActivity.showToast("Goals have been updated");
            parentActivity.stackReplaceFragment(MoreFragment.class);
            parentActivity.swapToTitleBar("More");
        }
        else if(asyncReturnType.equals("Get Agent")) {
            AsyncAgentJsonObject agentJsonObject = (AsyncAgentJsonObject) returnObject;
            AgentModel agentModel = agentJsonObject.getAgent();
            parentActivity.setAgentIncomeAndReason(agentModel);
            agent = parentActivity.getAgentInfo();
            agentUpdated = true;
            if(goalsUpdated) {
                setupFieldsWithGoalData(isAnnualChecked);
            }
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }
}

package co.sisu.mobile.fragments;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;
import co.sisu.mobile.models.UpdateAgentGoalsObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalSetupFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener, AsyncServerEventListener, View.OnFocusChangeListener {

    private EditText desiredIncome, trackingReasons, contacts, bAppointments, sAppointments, bSigned, sSigned, bContract, sContract, bClosed, sClosed, unitGoal, volumeGoal;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private TextView activityTitle, goalsLabel, saveButton;
    private boolean dateSwap;
    private List<EditText> fieldsObject;
    private HashMap<String, UpdateAgentGoalsObject> updatedGoals;
    private TextInputLayout desiredIncomeLayout, trackingReasonsLayout, sClosedLayout, bClosedLayout, bAppointmentsLayout, sAppointmentsLayout, bSignedLayout, sSignedLayout,
                            bContractLayout, sContractLayout, contactsLayout;
    private AgentModel agent;
    private AgentGoalsObject[] currentGoalsObject;
    private String income = "";
    private String reason = "";
    private ProgressBar loader;
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
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        updatedGoals = new HashMap<>();
        initFields();
        initEditText();
        initSwitchAndButtons();
        loader = view.findViewById(R.id.goalLoader);
        loader.setVisibility(View.VISIBLE);
        agent = dataController.getAgent();
        goalsUpdated = false;
        agentUpdated = false;
        income = "";
        reason = "";
        apiManager.sendAsyncAgentGoals(this, agent.getAgent_id(), parentActivity.getCurrentTeam().getId());
        apiManager.sendAsyncAgent(this, agent.getAgent_id());
        setLabels();
        setColorScheme();
    }

    private void setLabels() {
        activityTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.monthlyTitle)));
        goalsLabel.setText(parentActivity.localizeLabel(getResources().getString(R.string.goals)));
        desiredIncomeLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.desired_income_hint)));
        trackingReasonsLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.goals_reason_hint)));
        sClosedLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.sellers_closed_hint)));
        bClosedLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.buyers_closed_hint)));
        sContractLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.sellers_under_contract_hint)));
        bContractLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.buyers_under_contract_hint)));
        sSignedLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.signed_sellers_hint)));
        bSignedLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.signed_buyers_hint)));
        sAppointmentsLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.seller_appt_hint)));
        bAppointmentsLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.buyer_appt_hint)));
        contactsLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.contacts)));

    }

    private void setColorScheme() {
        desiredIncome.setTextColor(colorSchemeManager.getDarkerTextColor());
        trackingReasons.setTextColor(colorSchemeManager.getDarkerTextColor());
        contacts.setTextColor(colorSchemeManager.getDarkerTextColor());
        bAppointments.setTextColor(colorSchemeManager.getDarkerTextColor());
        sAppointments.setTextColor(colorSchemeManager.getDarkerTextColor());
        bSigned.setTextColor(colorSchemeManager.getDarkerTextColor());
        sSigned.setTextColor(colorSchemeManager.getDarkerTextColor());
        bContract.setTextColor(colorSchemeManager.getDarkerTextColor());
        sContract.setTextColor(colorSchemeManager.getDarkerTextColor());
        bClosed.setTextColor(colorSchemeManager.getDarkerTextColor());
        sClosed.setTextColor(colorSchemeManager.getDarkerTextColor());
        activityTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        goalsLabel.setTextColor(colorSchemeManager.getDarkerTextColor());

        setInputTextLayoutColor(desiredIncomeLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(trackingReasonsLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(sClosedLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(bClosedLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(bAppointmentsLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(sAppointmentsLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(bSignedLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(sSignedLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(bContractLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(sContractLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(contactsLayout, colorSchemeManager.getIconActive());

        if(colorSchemeManager.getAppBackground() == Color.WHITE) {
            Rect bounds = loader.getIndeterminateDrawable().getBounds();
            loader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark));
            loader.getIndeterminateDrawable().setBounds(bounds);
        } else {
            Rect bounds = loader.getIndeterminateDrawable().getBounds();
            loader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            loader.getIndeterminateDrawable().setBounds(bounds);
        }
    }


    private void setInputTextLayoutColor(TextInputLayout layout, int color) {
        try {
            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(layout, new ColorStateList(new int[][]{{0}}, new int[]{ color }));

//            Field fDefaultLineColor = TextInputLayout.class.getDeclaredField("")

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(layout, new ColorStateList(new int[][]{{0}}, new int[]{ color }));

            Method method = layout.getClass().getDeclaredMethod("updateLabelState", boolean.class);
            method.setAccessible(true);
            method.invoke(layout, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSwitchAndButtons() {
        saveButton = parentActivity.findViewById(R.id.saveButton);
        if(saveButton != null) {
            saveButton.setOnClickListener(this);
        }
    }

    private void setupFieldsWithGoalData() {
        dateSwap = true;
        currentGoalsObject = agent.getAgentGoalsObject();

        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                        toDisplay = String.valueOf(formattedIncome);
                    }
                    desiredIncome.setText(String.valueOf(toDisplay));
//                }

                if(reason.equals("")) {
                    trackingReasons.setText(agent.getVision_statement());
                }
                else {
                    trackingReasons.setText(reason);
                }

                for(AgentGoalsObject go : currentGoalsObject) {
                    String value = go.getValue();

                    switch (go.getGoal_id()) {
                        case "CONTA":
                            contacts.setText(value);
                            break;
                        case "SCLSD":
                            sClosed.setText(value);
                            break;
                        case "SUNDC":
                            sContract.setText(value);
                            break;
                        case "SAPPT":
                            sAppointments.setText(value);
                            break;
                        case "SSGND":
                            sSigned.setText(value);
                            break;
                        case "BSGND":
                            bSigned.setText(value);
                            break;
                        case "BAPPT":
                            bAppointments.setText(value);
                            break;
                        case "BUNDC":
                            bContract.setText(value);
                            break;
                        case "BCLSD":
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
        goalsLabel = getView().findViewById(R.id.goalsLabel);

        desiredIncomeLayout = getView().findViewById(R.id.desiredIncomeLayout);
        trackingReasonsLayout = getView().findViewById(R.id.goalsReasonLayout);
        sClosedLayout = getView().findViewById(R.id.sellersClosedLayout);
        bClosedLayout = getView().findViewById(R.id.buyersClosedLayout);
        bAppointmentsLayout = getView().findViewById(R.id.buyerApptsLayout);
        sAppointmentsLayout = getView().findViewById(R.id.sellerApptsLayout);
        bSignedLayout = getView().findViewById(R.id.signedBuyersLayout);
        sSignedLayout = getView().findViewById(R.id.signedSellersLayout);
        bContractLayout = getView().findViewById(R.id.buyersUnderContractLayout);
        sContractLayout = getView().findViewById(R.id.sellersUnderContractLayout);
        contactsLayout = getView().findViewById(R.id.contactsLayout);

        //unitGoal = getView().findViewById(R.id.unitGoal);
        //volumeGoal = getView().findViewById(R.id.volumeGoal);
    }

    private void initEditText() {
        for (int i = 0; i < fieldsObject.size(); i++) {
            fieldsObject.get(i).setOnFocusChangeListener(this);
        }

        desiredIncome.setOnFocusChangeListener(this);
        trackingReasons.setOnFocusChangeListener(this);
        //unitGoal.setOnFocusChangeListener(this);
        //volumeGoal.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setupFieldsWithGoalData();
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
            }
            else if(trackingReasons.getText().hashCode() == s.hashCode()) {
                updateProfile("Reasons", s.toString());
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
        AgentGoalsObject selectedGoal = null;

        int currentGoalsLength = currentGoalsObject.length;
        Log.e("LENGTH", String.valueOf(currentGoalsObject.length));

        for(AgentGoalsObject ago : currentGoalsObject) {
            if(ago.getGoal_id().equals(fieldName)) {
                selectedGoal = ago;
                break;
            }
        }
        UpdateAgentGoalsObject toUpdate = new UpdateAgentGoalsObject(fieldName, String.valueOf(value));

        if(selectedGoal == null) {

            currentGoalsObject[currentGoalsLength] = selectedGoal;
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
                apiManager.sendAsyncUpdateGoals(this, agent.getAgent_id(), parentActivity.getCurrentTeam().getId(), new AsyncUpdateAgentGoalsJsonObject(array));
                if(!income.equals("") || !reason.equals("")) {
                    agentUpdating = true;
                    apiManager.sendAsyncUpdateAgent(this, agent.getAgent_id(), parentActivity.getCurrentTeam().getId(), income, reason);
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
                navigationManager.clearStackReplaceFragment(MoreFragment.class);
            }
        }
        else if(asyncReturnType.equals("Goals")) {
            AsyncGoalsJsonObject goals = (AsyncGoalsJsonObject) returnObject;
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            dataController.setAgentGoals(agentGoalsObject);
            goalsUpdated = true;
            if(agentUpdated) {
                setupFieldsWithGoalData();
            }
        }
        else if(asyncReturnType.equals("Update Agent")) {
            agentUpdating = false;
            updatedGoals = new HashMap<>();
            parentActivity.showToast("Goals have been updated");
            navigationManager.clearStackReplaceFragment(MoreFragment.class);
//            navigationManager.swapToTitleBar("More");
        }
        else if(asyncReturnType.equals("Get Agent")) {
            AsyncAgentJsonObject agentJsonObject = (AsyncAgentJsonObject) returnObject;
            AgentModel agentModel = agentJsonObject.getAgent();
            dataController.setAgentIncomeAndReason(agentModel);
            agent = dataController.getAgent();
            agentUpdated = true;
            if(goalsUpdated) {
                setupFieldsWithGoalData();
            }
        }
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }
}

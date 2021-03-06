package co.sisu.mobile.fragments;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.fragment.app.Fragment;
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

import java.io.IOException;
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
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.fragments.main.MoreFragment;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.AsyncAgentJsonStringSuperUserObject;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;
import co.sisu.mobile.models.UpdateAgentGoalsObject;
import co.sisu.mobile.utils.Utils;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalSetupFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener, AsyncServerEventListener, View.OnFocusChangeListener {

    private EditText desiredIncome, trackingReasons, contacts, bAppointments, sAppointments, bSigned, sSigned, bContract, sContract, bClosed, sClosed, unitGoal, closedVolumeGoal, underContractVolumeGoal;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private Utils utils;
    private TextView activityTitle, goalsLabel, saveButton;
    private boolean dateSwap;
    private List<EditText> fieldsObject;
    private HashMap<String, UpdateAgentGoalsObject> updatedGoals;
    private TextInputLayout desiredIncomeLayout, trackingReasonsLayout, sClosedLayout, bClosedLayout, bAppointmentsLayout, sAppointmentsLayout, bSignedLayout, sSignedLayout,
                            bContractLayout, sContractLayout, contactsLayout, closedVolumeLayout, underContractVolumeLayout;
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
        apiManager.getAgentGoals(this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
        apiManager.getAgent(this, agent.getAgent_id());
        setLabels();
        setColorScheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "GoalSetupFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ParentActivity");
        FirebaseAnalytics.getInstance(parentActivity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
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
        closedVolumeLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.closed_volume_hint)));
        underContractVolumeLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.under_contract_volume_hint)));

    }

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.goalSetupParentLayout);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());

        desiredIncome.setTextColor(colorSchemeManager.getDarkerText());
        trackingReasons.setTextColor(colorSchemeManager.getDarkerText());
        contacts.setTextColor(colorSchemeManager.getDarkerText());
        bAppointments.setTextColor(colorSchemeManager.getDarkerText());
        sAppointments.setTextColor(colorSchemeManager.getDarkerText());
        bSigned.setTextColor(colorSchemeManager.getDarkerText());
        sSigned.setTextColor(colorSchemeManager.getDarkerText());
        bContract.setTextColor(colorSchemeManager.getDarkerText());
        sContract.setTextColor(colorSchemeManager.getDarkerText());
        bClosed.setTextColor(colorSchemeManager.getDarkerText());
        sClosed.setTextColor(colorSchemeManager.getDarkerText());
        activityTitle.setTextColor(colorSchemeManager.getDarkerText());
        goalsLabel.setTextColor(colorSchemeManager.getDarkerText());
        closedVolumeGoal.setTextColor(colorSchemeManager.getDarkerText());
        underContractVolumeGoal.setTextColor(colorSchemeManager.getDarkerText());

        setInputTextLayoutColor(desiredIncomeLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(trackingReasonsLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(sClosedLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(bClosedLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(bAppointmentsLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(sAppointmentsLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(bSignedLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(sSignedLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(bContractLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(sContractLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(contactsLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(closedVolumeLayout, colorSchemeManager.getLighterText());
        setInputTextLayoutColor(underContractVolumeLayout, colorSchemeManager.getLighterText());

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
        else {
            Log.e("NULL SAVE", "FARTS");
        }
    }

    private void setupFieldsWithGoalData() {
        dateSwap = true;
        currentGoalsObject = agent.getAgentGoalsObject();

        parentActivity.runOnUiThread(() -> {
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
                if(go != null) {
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
                        case "CLSDV":
                            closedVolumeGoal.setText(value);
                            break;
                        case "UNCTV":
                            underContractVolumeGoal.setText(value);
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
        closedVolumeGoal = getView().findViewById(R.id.closedVolume);
        closedVolumeGoal.addTextChangedListener(this);
        fieldsObject.add(closedVolumeGoal);
        underContractVolumeGoal = getView().findViewById(R.id.underContractVolume);
        underContractVolumeGoal.addTextChangedListener(this);
        fieldsObject.add(underContractVolumeGoal);

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
        closedVolumeLayout = getView().findViewById(R.id.closedVolumeLayout);
        underContractVolumeLayout = getView().findViewById(R.id.underContractVolumeLayout);

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
                updateField("CONTA", (String.valueOf(s)));
            }
            else if(bAppointments.getText().hashCode() == s.hashCode())
            {
                updateField("BAPPT", (String.valueOf(s)));
            }
            else if(sAppointments.getText().hashCode() == s.hashCode())
            {
                updateField("SAPPT", (String.valueOf(s)));
            }
            else if(bSigned.getText().hashCode() == s.hashCode())
            {
                updateField("BSGND", (String.valueOf(s)));
            }
            else if(sSigned.getText().hashCode() == s.hashCode())
            {
                updateField("SSGND", (String.valueOf(s)));
            }
            else if(bContract.getText().hashCode() == s.hashCode())
            {
                updateField("BUNDC", (String.valueOf(s)));
            }
            else if(sContract.getText().hashCode() == s.hashCode())
            {
                updateField("SUNDC", (String.valueOf(s)));
            }
            else if(bClosed.getText().hashCode() == s.hashCode())
            {
                updateField("BCLSD", (String.valueOf(s)));
            }
            else if(sClosed.getText().hashCode() == s.hashCode())
            {
                updateField("SCLSD", (String.valueOf(s)));
            }
            else if(closedVolumeGoal.getText().hashCode() == s.hashCode())
            {
                updateField("CLSDV", (String.valueOf(s)));
            }
            else if(underContractVolumeGoal.getText().hashCode() == s.hashCode())
            {
                updateField("UNCTV", (String.valueOf(s)));
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

    private void updateField(String fieldName, String value) {
        AgentGoalsObject selectedGoal = null;

        int currentGoalsLength = currentGoalsObject.length;

        for(AgentGoalsObject ago : currentGoalsObject) {
            if(ago.getGoal_id().equals(fieldName)) {
                selectedGoal = ago;
                break;
            }
        }
        UpdateAgentGoalsObject toUpdate = new UpdateAgentGoalsObject(fieldName, (value));

        if(selectedGoal == null) {
            currentGoalsObject[currentGoalsLength] = selectedGoal;
            updatedGoals.put(fieldName, toUpdate);
        }
        else {
            selectedGoal.setValue((value));
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
                apiManager.sendAsyncUpdateGoals(this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), new AsyncUpdateAgentGoalsJsonObject(array));
                if(!income.equals("") || !reason.equals("")) {
                    agentUpdating = true;
                    apiManager.sendAsyncUpdateAgent(this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), income, reason);
                }
                break;
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.GET_AGENT_GOALS) {
            AsyncGoalsJsonObject goals = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncGoalsJsonObject.class);
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            dataController.setAgentGoals(agentGoalsObject, parentActivity.isRecruiting());
            goalsUpdated = true;
            if(agentUpdated) {
                setupFieldsWithGoalData();
            }
        }
        else if(returnType == ApiReturnType.GET_AGENT) {
            AsyncAgentJsonObject agentJsonObject = null;
            String r = null;
            try {
                r = ((Response) returnObject).body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                agentJsonObject = parentActivity.getGson().fromJson(r, AsyncAgentJsonObject.class);
            } catch(Exception e) {
                AsyncAgentJsonStringSuperUserObject tempAgent = parentActivity.getGson().fromJson(r, AsyncAgentJsonStringSuperUserObject.class);
                agentJsonObject = new AsyncAgentJsonObject(tempAgent);
            }
            AgentModel agentModel = agentJsonObject.getAgent();
            dataController.setAgentIncomeAndReason(agentModel);
            agent = dataController.getAgent();
            agentUpdated = true;
            if(goalsUpdated) {
                setupFieldsWithGoalData();
            }
        }
        else if(returnType == ApiReturnType.UPDATE_GOALS) {
            if(!agentUpdating) {
                updatedGoals = new HashMap<>();
                utils.showToast("Goals have been updated", parentActivity);
                navigationManager.clearStackReplaceFragment(MoreFragment.class);
            }
        }
        else if(returnType == ApiReturnType.UPDATE_AGENT) {
            agentUpdating = false;
            updatedGoals = new HashMap<>();
            utils.showToast("Goals have been updated", parentActivity);
            navigationManager.clearStackReplaceFragment(MoreFragment.class);
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

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }
}

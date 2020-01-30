package co.sisu.mobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.AppointmentListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.AsyncLeadSourcesJsonObject;
import co.sisu.mobile.models.AsyncNotesJsonObject;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.NotesObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.UpdateSettingsObject;
import okhttp3.Response;

public class ClientManageFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AsyncServerEventListener, View.OnFocusChangeListener {

    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private ProgressBar loader;
    private ClientObject currentClient;
    private EditText firstNameText, lastNameText, leadSource, emailText, phoneText, transAmount, paidIncome, gci, noteText, incomePercent, gciPercent, archivedReason;
    private ImageView lock, archiveButton;
    private TextView signedDisplay, contractDisplay, settlementDisplay, appointmentDisplay, setAppointmentDisplay, addAppointmentDisplay;
    private TextView pipelineStatus, signedStatus, underContractStatus, closedStatus, archivedStatus, buyer, seller, saveButton, cancelButton,
                     appointmentDateTitle, setAppointmentDateTitle, addAppointmentDateTitle, signedDateTitle, underContractDateTitle, settlementDateTitle, dollarSign1, dollarSign2, commissionEquals, gciEquals,
                     percentSign1, percentSign2, statusLabel, priorityText, otherAppointmentsTitle;
    private Button signedClear, contractClear, settlementClear, appointmentClear, appointmentSetClear, exportContact, deleteButton,
                    noteButton, calculateGciPercent, calculateIncomePercent, activateButton, addAppointmentButton;
    private Switch prioritySwitch;
    private ListView mListView;
    private TextInputLayout archivedLayout, firstNameLayout, lastNameLayout, emailLayout, phoneLayout, transAmountLayout, paidIncomeLayout, gciLayout, noteLayout, gciPercentLayout, commissionInputLayout, leadSourceInputLayout;
    private int signedSelectedYear, signedSelectedMonth, signedSelectedDay;
    private int contractSelectedYear, contractSelectedMonth, contractSelectedDay;
    private int settlementSelectedYear, settlementSelectedMonth, settlementSelectedDay;
    private int appointmentSelectedYear, appointmentSelectedMonth, appointmentSelectedDay;
    private int appointmentSetSelectedYear, appointmentSetSelectedMonth, appointmentSetSelectedDay;
    private int addAppointmentSelectedYear, addAppointmentSelectedMonth, addAppointmentSelectedDay;
    private String typeSelected, clientStatus, m_Text;
    private String statusList = "pipeline";
    private int counter;
    private LinkedHashMap leadSources;
    private String currentStatus = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ScrollView contentView = (ScrollView) inflater.inflate(R.layout.activity_client_layout, container, false);
        ScrollView.LayoutParams viewLayout = new ScrollView.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        navigationManager = parentActivity.getNavigationManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        loader = view.findViewById(R.id.clientLoader);

        counter = 1;
        view.clearFocus();
        initCommonButtons();
        initCommonForm();
        initializeCalendar();

        if(parentActivity.getSelectedClient() == null) {
            // This is the flow to add a client
            loader.setVisibility(View.GONE);
            initAddClientButtons();
            initAddClientForm();
            apiManager.getLeadSources(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId());
        }
        else {
            // This is the flow to edit a client
            currentClient = parentActivity.getSelectedClient();
            if (parentActivity.getAgent().getLast_name().equalsIgnoreCase("Groharing")) {
                parentActivity.showToast("Client ID: " + parentActivity.getSelectedClient().getClient_id());
            }
            initEditClientForm();
            initEditClientButtons();
            setupEditClientLabels();
            setEditClientColorScheme();
            if(currentClient.getStatus().equals("D")) {
                apiManager.getClientParams(this, dataController.getAgent().getAgent_id(), currentClient.getClient_id());
            }
            else {
                apiManager.getLeadSources(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId());
                loader.setVisibility(View.GONE);
            }

            if(parentActivity.isRecruiting()) {
                initRecruitingAppointmentsListView();
                apiManager.getClientNotes(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id());
            }

            if(currentClient.getIs_locked() == 1) {
                parentActivity.showToast("This client account has been locked by your team administrator.");
            }
        }

        setupCommonLabels();
        setCommonColorScheme();

    }

    private void setupEditClientLabels() {
        activateButton.setText(parentActivity.localizeLabel(getResources().getString(R.string.activate)));
        archivedLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.archived_reason)));
    }

    private void setupCommonLabels() {
        buyer.setText(parentActivity.localizeLabel(getResources().getString(R.string.buyer)));
        seller.setText(parentActivity.localizeLabel(getResources().getString(R.string.seller)));

        firstNameLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.first_name_hint_non_req)));
        lastNameLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.last_name_hint_non_req)));
        transAmountLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.transaction_amount_hint)));
        gciLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.gci_hint)));
        if(parentActivity.isRecruiting()) {
            gciLayout.setHint("Recruit GCI:");
        }
        commissionInputLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.commission_hint)));
        leadSourceInputLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.lead_source_hint)));
        phoneLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.phone_hint)));
        emailLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.email_hint)));
        statusLabel.setText(parentActivity.localizeLabel(getResources().getString(R.string.status)));
        pipelineStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.pipeline)));
        signedStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.signed)));
        underContractStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.contract)));
        closedStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.closed)));
        archivedStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.archived)));
        appointmentDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.appointmentDateTitle)));
        setAppointmentDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.appointmentSetDateTitle)));
        addAppointmentDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.addAppointmentDateTitle)));
        signedDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.signedDateTitle)));
        underContractDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.underContractTitle)));
        settlementDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.settlementDateTitle)));
        noteLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.notes)));
    }

    private void initializeCalendar() {
        getView().findViewById(R.id.signedDatePicker).setOnClickListener(this);
        signedDisplay = getView().findViewById(R.id.signedDateDisplay);
        signedDisplay.setOnClickListener(this);
        signedDateTitle = getView().findViewById(R.id.signedDateTitle);
        signedDateTitle.setOnClickListener(this);

        getView().findViewById(R.id.underContractDatePicker).setOnClickListener(this);
        contractDisplay = getView().findViewById(R.id.underContractDateDisplay);
        contractDisplay.setOnClickListener(this);
        underContractDateTitle = getView().findViewById(R.id.underContractDateTitle);
        underContractDateTitle.setOnClickListener(this);

        getView().findViewById(R.id.settlementDatePicker).setOnClickListener(this);
        settlementDisplay = getView().findViewById(R.id.settlementDateDisplay);
        settlementDisplay.setOnClickListener(this);
        settlementDateTitle = getView().findViewById(R.id.settlementDateTitle);
        settlementDateTitle.setOnClickListener(this);

        getView().findViewById(R.id.appointmentDatePicker).setOnClickListener(this);
        appointmentDisplay = getView().findViewById(R.id.appointmentDateDisplay);
        appointmentDisplay.setOnClickListener(this);
        appointmentDateTitle = getView().findViewById(R.id.appointmentDateTitle);
        appointmentDateTitle.setOnClickListener(this);

        getView().findViewById(R.id.appointmentSetDatePicker).setOnClickListener(this);
        setAppointmentDisplay = getView().findViewById(R.id.appointmentSetDateDisplay);
        setAppointmentDisplay.setOnClickListener(this);
        setAppointmentDateTitle = getView().findViewById(R.id.appointmentSetDateTitle);
        setAppointmentDateTitle.setOnClickListener(this);

        getView().findViewById(R.id.addAppointmentDatePicker).setOnClickListener(this);
        addAppointmentDisplay = getView().findViewById(R.id.addAppointmentDateDisplay);
        addAppointmentDisplay.setOnClickListener(this);
        addAppointmentDateTitle = getView().findViewById(R.id.addAppointmentDateTitle);
        addAppointmentDateTitle.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        signedSelectedYear = year;
        signedSelectedMonth = month;
        signedSelectedDay = day;

        contractSelectedYear = year;
        contractSelectedMonth = month;
        contractSelectedDay = day;

        settlementSelectedYear = year;
        settlementSelectedMonth = month;
        settlementSelectedDay = day;

        appointmentSelectedYear = year;
        appointmentSelectedMonth = month;
        appointmentSelectedDay = day;

        appointmentSetSelectedYear = year;
        appointmentSetSelectedMonth = month;
        appointmentSetSelectedDay = day;

        addAppointmentSelectedYear = year;
        addAppointmentSelectedMonth = month;
        addAppointmentSelectedDay = day;
    }

    private void initAddClientForm() {
        typeSelected = "";
    }

    private void initEditClientForm() {
        archivedLayout = getView().findViewById(R.id.archiveReasonLayout);
        archivedLayout.setClickable(false);
        archivedReason = getView().findViewById(R.id.archiveReason);
        archivedReason.setFocusable(false);
    }

    private void initCommonForm() {
        firstNameText = getView().findViewById(R.id.editFirstName);
        firstNameText.setOnFocusChangeListener(this);
        lastNameText = getView().findViewById(R.id.editLastName);
        lastNameText.setOnFocusChangeListener(this);
        emailText = getView().findViewById(R.id.editEmail);
        emailText.setOnFocusChangeListener(this);
        phoneText = getView().findViewById(R.id.editPhone);
        phoneText.setOnFocusChangeListener(this);
        transAmount = getView().findViewById(R.id.editTransAmount);
        transAmount.setOnFocusChangeListener(this);
        transAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        paidIncome = getView().findViewById(R.id.editPaidIncome);
        paidIncome.setOnFocusChangeListener(this);
        paidIncome.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        gci = getView().findViewById(R.id.editGci);
        gci.setOnFocusChangeListener(this);
        gci.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        gciPercent = getView().findViewById(R.id.editGciPercent);
        gciPercent.setOnFocusChangeListener(this);
        incomePercent = getView().findViewById(R.id.editPaidIncomePercent);
        incomePercent.setOnFocusChangeListener(this);
        leadSource = getView().findViewById(R.id.leadSource);
        leadSource.setOnClickListener(this);

        pipelineStatus = getView().findViewById(R.id.pipelineButton);
        signedStatus = getView().findViewById(R.id.signedButton);
        underContractStatus = getView().findViewById(R.id.contractButton);
        closedStatus = getView().findViewById(R.id.closedButton);
        archivedStatus = getView().findViewById(R.id.archivedButton);
        noteText = getView().findViewById(R.id.editNotes);

        firstNameLayout = getView().findViewById(R.id.firstNameInputLayout);
        lastNameLayout = getView().findViewById(R.id.lastNameInputLayout);
        emailLayout = getView().findViewById(R.id.emailInputLayout);
        phoneLayout = getView().findViewById(R.id.phoneInputLayout);
        transAmountLayout = getView().findViewById(R.id.transAmountInputLayout);
        paidIncomeLayout = getView().findViewById(R.id.paidIncomePercentLayout);
        gciLayout = getView().findViewById(R.id.gciInputLayout);
        noteLayout = getView().findViewById(R.id.notesInputLayout);
        gciPercentLayout = getView().findViewById(R.id.gciInputLayout);
        commissionInputLayout = getView().findViewById(R.id.commissionInputLayout);
        leadSourceInputLayout = getView().findViewById(R.id.leadSourceInputLayout);
        leadSourceInputLayout.setOnClickListener(this);

        dollarSign1 = getView().findViewById(R.id.dollarSign);
        dollarSign2 = getView().findViewById(R.id.dollarSign2);
        percentSign1 = getView().findViewById(R.id.percentSign);
        percentSign2 = getView().findViewById(R.id.percentSign2);
        statusLabel = getView().findViewById(R.id.statusLabel);

        commissionEquals = getView().findViewById(R.id.commissionEquals);
        gciEquals = getView().findViewById(R.id.gciEquals);
        priorityText = getView().findViewById(R.id.priorityText);
        otherAppointmentsTitle = getView().findViewById(R.id.otherAppointmentsTitle);
        prioritySwitch = getView().findViewById(R.id.prioritySwitch);

        RelativeLayout statusGroup = getView().findViewById(R.id.statusRadioGroup);
        ConstraintLayout underContractSection = getView().findViewById(R.id.underContractSection);
        ConstraintLayout signedDateSection = getView().findViewById(R.id.signedDateSection);
        ConstraintLayout addAppointmentDateSection = getView().findViewById(R.id.addAppointmentDateSection);

        if(parentActivity.isRecruiting()) {
            paidIncomeLayout.setVisibility(View.GONE);
            commissionInputLayout.setVisibility(View.GONE);
            calculateIncomePercent.setVisibility(View.GONE);
            underContractSection.setVisibility(View.GONE);
            signedDateSection.setVisibility(View.GONE);
            percentSign2.setVisibility(View.GONE);
            commissionEquals.setVisibility(View.GONE);
            if(currentClient != null) {
                otherAppointmentsTitle.setVisibility(View.VISIBLE);
                addAppointmentDateSection.setVisibility(View.VISIBLE);
            }
            else {
                otherAppointmentsTitle.setVisibility(View.GONE);
                addAppointmentDateSection.setVisibility(View.GONE);
            }

        }
        else {
            otherAppointmentsTitle.setVisibility(View.GONE);
            addAppointmentDateSection.setVisibility(View.GONE);
        }
    }

    private void setEditClientColorScheme() {
        archivedReason.setTextColor(colorSchemeManager.getDarkerTextColor());
        Drawable imageDraw = getResources().getDrawable(R.drawable.trash_icon).mutate();
        imageDraw.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
        archiveButton.setImageDrawable(imageDraw);

        setInputTextLayoutColor(archivedLayout, colorSchemeManager.getDarkerTextColor());

        noteButton.setTextColor(colorSchemeManager.getButtonText());
        noteButton.setBackgroundResource(R.drawable.rounded_button);
        GradientDrawable drawable = (GradientDrawable) noteButton.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        typeSelected = currentClient.getType_id();
        if(typeSelected.equals("b")) {
            buyer.setBackgroundResource(R.drawable.rounded_button);
            drawable = (GradientDrawable) buyer.getBackground();
            drawable.setColor(colorSchemeManager.getButtonSelected());

            seller.setBackgroundResource(R.drawable.rounded_button);
            drawable = (GradientDrawable) seller.getBackground();
            drawable.setColor(colorSchemeManager.getButtonBackground());
        } else {
            seller.setBackgroundResource(R.drawable.rounded_button);
            drawable = (GradientDrawable) seller.getBackground();
            drawable.setColor(colorSchemeManager.getButtonSelected());

            buyer.setBackgroundResource(R.drawable.rounded_button);
            drawable = (GradientDrawable) buyer.getBackground();
            drawable.setColor(colorSchemeManager.getButtonBackground());
        }

        buyer.setTextColor(colorSchemeManager.getButtonText());
        seller.setTextColor(colorSchemeManager.getButtonText());

        activateButton.setTextColor(colorSchemeManager.getButtonText());
        activateButton.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) activateButton.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        updateStatus();

    }

    private void setCommonColorScheme() {
        firstNameText.setTextColor(colorSchemeManager.getDarkerTextColor());
        lastNameText.setTextColor(colorSchemeManager.getDarkerTextColor());
        emailText.setTextColor(colorSchemeManager.getDarkerTextColor());
        phoneText.setTextColor(colorSchemeManager.getDarkerTextColor());
        transAmount.setTextColor(colorSchemeManager.getDarkerTextColor());
        paidIncome.setTextColor(colorSchemeManager.getDarkerTextColor());
        gci.setTextColor(colorSchemeManager.getDarkerTextColor());
        noteText.setTextColor(colorSchemeManager.getDarkerTextColor());
        gciPercent.setTextColor(colorSchemeManager.getDarkerTextColor());
        incomePercent.setTextColor(colorSchemeManager.getDarkerTextColor());
        leadSource.setTextColor(colorSchemeManager.getDarkerTextColor());

        signedDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
        contractDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
        settlementDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
        appointmentDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
        setAppointmentDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
        addAppointmentDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());

        signedDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
        contractDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
        settlementDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
        appointmentDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
        setAppointmentDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
        addAppointmentDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());

        pipelineStatus.setTextColor(colorSchemeManager.getButtonText());
        pipelineStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        signedStatus.setTextColor(colorSchemeManager.getButtonText());
        signedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        underContractStatus.setTextColor(colorSchemeManager.getButtonText());
        underContractStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        closedStatus.setTextColor(colorSchemeManager.getButtonText());
        closedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        archivedStatus.setTextColor(colorSchemeManager.getButtonText());
        archivedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());

        appointmentDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        setAppointmentDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        addAppointmentDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        signedDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        underContractDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        settlementDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());

        dollarSign1.setTextColor(colorSchemeManager.getDarkerTextColor());
        dollarSign2.setTextColor(colorSchemeManager.getDarkerTextColor());
        percentSign1.setTextColor(colorSchemeManager.getDarkerTextColor());
        percentSign2.setTextColor(colorSchemeManager.getDarkerTextColor());

        commissionEquals.setTextColor(colorSchemeManager.getDarkerTextColor());
        gciEquals.setTextColor(colorSchemeManager.getDarkerTextColor());
        statusLabel.setTextColor(colorSchemeManager.getDarkerTextColor());

        pipelineStatus.setTextColor(colorSchemeManager.getButtonText());
        signedStatus.setTextColor(colorSchemeManager.getButtonText());
        underContractStatus.setTextColor(colorSchemeManager.getButtonText());
        closedStatus.setTextColor(colorSchemeManager.getButtonText());
        archivedStatus.setTextColor(colorSchemeManager.getButtonText());

        setInputTextLayoutHintColor(firstNameLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutHintColor(lastNameLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutHintColor(emailLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(phoneLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutHintColor(transAmountLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutHintColor(paidIncomeLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutHintColor(gciLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(noteLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutHintColor(gciPercentLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutHintColor(commissionInputLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(leadSourceInputLayout, colorSchemeManager.getIconActive());




        // TODO: This is the stuff I'm unsure of
//        Drawable imageDraw = getResources().getDrawable(R.drawable.trash_icon).mutate();
//        imageDraw.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
//        archiveButton.setImageDrawable(imageDraw);
//
//        exportContact.setHighlightColor(colorSchemeManager.getButtonSelected());
//        exportContact.setBackgroundResource(R.drawable.rounded_button);
//        exportContact.setTextColor(colorSchemeManager.getButtonText());
//        GradientDrawable drawable = (GradientDrawable) exportContact.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        typeSelected = currentClient.getType_id();
//        if(typeSelected.equals("b")) {
//            buyer.setBackgroundResource(R.drawable.rounded_button);
//            drawable = (GradientDrawable) buyer.getBackground();
//            drawable.setColor(colorSchemeManager.getButtonSelected());
//
//            seller.setBackgroundResource(R.drawable.rounded_button);
//            drawable = (GradientDrawable) seller.getBackground();
//            drawable.setColor(colorSchemeManager.getButtonBackground());
//        } else {
//            seller.setBackgroundResource(R.drawable.rounded_button);
//            drawable = (GradientDrawable) seller.getBackground();
//            drawable.setColor(colorSchemeManager.getButtonSelected());
//
//            buyer.setBackgroundResource(R.drawable.rounded_button);
//            drawable = (GradientDrawable) buyer.getBackground();
//            drawable.setColor(colorSchemeManager.getButtonBackground());
//        }
//
//        buyer.setTextColor(colorSchemeManager.getButtonText());
//        seller.setTextColor(colorSchemeManager.getButtonText());
//
//        activateButton.setTextColor(colorSchemeManager.getButtonText());
//        activateButton.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) activateButton.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());

        signedClear.setTextColor(colorSchemeManager.getButtonText());
        signedClear.setBackgroundResource(R.drawable.rounded_button);
        GradientDrawable drawable = (GradientDrawable) signedClear.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        contractClear.setTextColor(colorSchemeManager.getButtonText());
        contractClear.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) contractClear.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        settlementClear.setTextColor(colorSchemeManager.getButtonText());
        settlementClear.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) settlementClear.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        appointmentClear.setTextColor(colorSchemeManager.getButtonText());
        appointmentClear.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) appointmentClear.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        appointmentSetClear.setTextColor(colorSchemeManager.getButtonText());
        appointmentSetClear.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) appointmentSetClear.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        addAppointmentButton.setTextColor(colorSchemeManager.getButtonText());
        addAppointmentButton.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) addAppointmentButton.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        calculateGciPercent.setTextColor(colorSchemeManager.getButtonText());
        calculateGciPercent.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) calculateGciPercent.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        calculateIncomePercent.setTextColor(colorSchemeManager.getButtonText());
        calculateIncomePercent.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) calculateIncomePercent.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        priorityText.setTextColor(colorSchemeManager.getDarkerTextColor());
        otherAppointmentsTitle.setTextColor(colorSchemeManager.getDarkerTextColor());

        buyer.setTextColor(colorSchemeManager.getButtonText());
        buyer.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) buyer.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        seller.setTextColor(colorSchemeManager.getButtonText());
        seller.setBackgroundResource(R.drawable.rounded_button);
        drawable = (GradientDrawable) seller.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());


        exportContact.setHighlightColor(colorSchemeManager.getButtonSelected());
        exportContact.setBackgroundResource(R.drawable.rounded_button);
        exportContact.setTextColor(colorSchemeManager.getButtonText());
        drawable = (GradientDrawable) exportContact.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getSegmentSelected()
        };

        int[] trackColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getSegmentSelected()
        };

        DrawableCompat.setTintList(DrawableCompat.wrap(prioritySwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(prioritySwitch.getTrackDrawable()), new ColorStateList(states, trackColors));

        //TODO: Figure out why you did this (looks like loader stuff but I don't think you use that anymore)
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
        // TODO: This doesn't work
        try {

            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(layout, new ColorStateList(new int[][]{{0}}, new int[]{ color }));

            //Field fDefaultLineColor = TextInputLayout.class.getDeclaredField("")

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

    private void setInputTextLayoutHintColor(TextInputLayout textInputLayout, int color) {
        try {
            Field field = textInputLayout.getClass().getDeclaredField("mFocusedTextColor");
            field.setAccessible(true);
            int[][] states = new int[][]{
                    new int[]{}
            };
            int[] colors = new int[]{
                    color
            };
            ColorStateList myList = new ColorStateList(states, colors);
            field.set(textInputLayout, myList);

            Method method = textInputLayout.getClass().getDeclaredMethod("updateLabelState", boolean.class);
            method.setAccessible(true);
            method.invoke(textInputLayout, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popReasonDialog(String message, String text) {
        AlertDialog.Builder builder;
        final EditText input = new EditText(parentActivity);
            builder = new AlertDialog.Builder(parentActivity, R.style.lightDialog);
            input.setTextColor(Color.BLACK);

        builder.setTitle(message);
        final String label = text;
        // Set up the input
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if(!m_Text.equals("")) {
                    apiManager.addNote(ClientManageFragment.this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id(), label + ": " + m_Text, "NOTES");
                    if(label.equalsIgnoreCase("Archived")) {
                        updateCurrentClient(true);
                    }
                    else {
                        updateCurrentClient(false);
                    }
                    saveClient();
                }
                else {
                    parentActivity.showToast("Please enter some text in the note field.");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private AsyncUpdateSettingsJsonObject createActivateClientObject(String clientId, String reasonText) {
//        String valueString = "{\"type\":3,\"id\":"+ clientId +",\"parameters\":[{\"name\":\"activate_client\",\"value\":\""+ reasonText +"\"}]}";
        List<UpdateSettingsObject> list = new ArrayList<>();
        list.add(new UpdateSettingsObject("activate_client", reasonText));
        AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject = new AsyncUpdateSettingsJsonObject(3, Integer.valueOf(parentActivity.getSelectedClient().getClient_id()), list);
        return asyncUpdateSettingsJsonObject;
    }

    private void calculateTransPercentage(EditText editPercent, EditText editDollar) {
        float percent;
        if(!transAmount.getText().toString().isEmpty() && !editPercent.getText().toString().isEmpty()) {
            percent = Float.parseFloat(editPercent.getText().toString());
            convertPercentToDollar(percent / 100f, editDollar, transAmount);
        } else {
            parentActivity.showToast(getText(R.string.percent_help_text));
//            if(!editDollar.getText().toString().isEmpty() && !editPercent.getText().toString().isEmpty()) {
//                dollar = Integer.parseInt(editDollar.getText().toString());
//                convertDollarToPercent(dollar * 100f, editPercent);
//            }
        }
    }

    private void calculateGciPercentage(EditText editPercent, EditText editDollar) {
        float percent;
        if(!gci.getText().toString().isEmpty() && !editPercent.getText().toString().isEmpty()) {
            percent = Float.parseFloat(editPercent.getText().toString());
            convertPercentToDollar(percent / 100f, editDollar, gci);
        } else {
            parentActivity.showToast("GCI and Percent Required");
//            if(!editDollar.getText().toString().isEmpty() && !editPercent.getText().toString().isEmpty()) {
//                dollar = Integer.parseInt(editDollar.getText().toString());
//                convertDollarToPercent(dollar * 100f, editPercent);
//            }
        }
    }

    private void convertPercentToDollar(float percent, EditText dollarText, EditText transAmount) {
        float dollar;
        int transTotal = 0;
        float transTotalDouble = 0;
        if(transAmount.getText().toString().contains(".")) {
            // That means that this is a double
            transTotalDouble = Float.parseFloat(transAmount.getText().toString());
        }
        else {
            transTotal = Integer.parseInt(transAmount.getText().toString());
        }
        if(transTotal != 0 && percent <= 100 && percent > 0) {
            dollar = percent * transTotal;
            if(dollar <= transTotal) {
                dollarText.setText(String.valueOf(dollar).substring(0,String.valueOf(dollar).indexOf('.')));
            }
        }
        else if(transTotalDouble != 0 && percent <= 100 && percent > 0) {
            dollar = percent * transTotalDouble;
            if(dollar <= transTotalDouble) {
                dollarText.setText(String.valueOf(dollar).substring(0,String.valueOf(dollar).indexOf('.')));
            }
        }
    }

    private void initializeClient() {
        typeSelected = currentClient.getType_id();
        if(typeSelected.equals("b")) {
            buyer.setTextColor(colorSchemeManager.getButtonText());
            buyer.setBackgroundResource(R.drawable.rounded_button);
            GradientDrawable drawable = (GradientDrawable) buyer.getBackground();
            drawable.setColor(colorSchemeManager.getButtonSelected());
        } else {
            seller.setTextColor(colorSchemeManager.getButtonText());
            seller.setBackgroundResource(R.drawable.rounded_button);
            GradientDrawable drawable = (GradientDrawable) seller.getBackground();
            drawable.setColor(colorSchemeManager.getButtonSelected());
        }

        firstNameText.setText(currentClient.getFirst_name());
        lastNameText.setText(currentClient.getLast_name());
        transAmount.setText(currentClient.getTrans_amt());
        paidIncome.setText(currentClient.getCommission_amt());
        gci.setText(currentClient.getGross_commission_amt());
        if(currentClient.getActivate_client() != null) {
            Log.e("REASONNNN", currentClient.getActivate_client());
            archivedReason.setText(currentClient.getActivate_client());
        }
        if(currentClient.getMobile_phone() != null){
            phoneText.setText(PhoneNumberUtils.formatNumber(currentClient.getMobile_phone(), Locale.getDefault().getCountry()));
        } else if (currentClient.getHome_phone() != null){
            phoneText.setText(PhoneNumberUtils.formatNumber(currentClient.getHome_phone(), Locale.getDefault().getCountry()));
        }
        emailText.setText(currentClient.getEmail());
        if(currentClient.getStatus() != null) {
            clientStatus = currentClient.getStatus();
            if(clientStatus.equals("D")) {
                archiveButton.setVisibility(View.INVISIBLE);
                activateButton.setVisibility(View.VISIBLE);
                archivedLayout.setVisibility(View.VISIBLE);
            }
        }

        String formattedApptDt = getFormattedDateFromApiReturn(currentClient.getAppt_dt());
        String formattedSetApptDt = getFormattedDateFromApiReturn(currentClient.getAppt_set_dt());
        String formattedSignedDt = getFormattedDateFromApiReturn(currentClient.getSigned_dt());
        String formattedContractDt = getFormattedDateFromApiReturn(currentClient.getUc_dt());
        String formattedClosedDt = getFormattedDateFromApiReturn(currentClient.getClosed_dt());

        if(leadSources != null) {
            for ( Object key : leadSources.keySet() ) {
                int formattedKey = Integer.valueOf((String) key);
                if(formattedKey == currentClient.getLead_type_id()) {
                    leadSource.setText(leadSources.get(key).toString());
                    break;
                }
            }
        }


        appointmentDisplay.setText(formattedApptDt);
        setAppointmentDisplay.setText(formattedSetApptDt);
        signedDisplay.setText(formattedSignedDt);
        contractDisplay.setText(formattedContractDt);
        settlementDisplay.setText(formattedClosedDt);
        noteText.setText(currentClient.getNote());
        prioritySwitch.setChecked(currentClient.getIs_priority() == 1 ? true : false);
        updateStatus();
        //calculatePercentage();
        if(currentClient.getIs_locked() == 1) {
            lockClient();
        }
    }

    private void lockClient() {
        saveButton.setOnClickListener(null);
        archiveButton.setOnClickListener(null);
        saveButton.setVisibility(View.GONE);
        lock = parentActivity.findViewById(R.id.lock);
        lock.setVisibility(View.VISIBLE);
        lock.setOnClickListener(this);
        ConstraintLayout c = (ConstraintLayout) parentActivity.findViewById(R.id.editClientLayout);
        for(View v : c.getTouchables()) {
            v.setEnabled(false);
            v.setFocusable(false);
            v.setFocusableInTouchMode(false);
        }
        noteButton.setEnabled(true);
        noteButton.setClickable(true);
    }

    private String getFormattedDateFromApiReturn(String dateString) {
        if(dateString != null) {
            dateString = dateString.replace("00:00:00 GMT", "");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
            Date d = null;
            try {
                d = sdf.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
            return format.format(calendar.getTime());
        }
        return "";
    }

    private void updateCurrentClient(boolean deleteClient) {
        //These can never be null
        currentClient.setFirst_name(firstNameText.getText().toString());
        currentClient.setLast_name(lastNameText.getText().toString());
        currentClient.setTrans_amt(transAmount.getText().toString());
        if(!parentActivity.isRecruiting()) {
            currentClient.setCommission_amt(paidIncome.getText().toString());
        }

        //These need to be checked for null
        currentClient.setGross_commission_amt(gci.getText().toString().equals("") ? null : gci.getText().toString());
        currentClient.setMobile_phone(phoneText.getText().toString().equals("") ? null : phoneText.getText().toString());
        currentClient.setEmail(emailText.getText().toString().equals("") ? null : emailText.getText().toString());
        currentClient.setAppt_dt(null);
        currentClient.setAppt_set_dt(null);
        currentClient.setSigned_dt(null);
        currentClient.setUc_dt(null);
        currentClient.setClosed_dt(null);
        currentClient.setType_id(typeSelected);
        currentClient.setNote(noteText.getText().toString().equals("") ? null : noteText.getText().toString());
        currentClient.setIs_priority(prioritySwitch.isChecked() ? 1 : 0);
        currentClient.setMarket_id(String.valueOf(parentActivity.getSelectedTeamMarketId()));
        currentClient.setTeam_id(parentActivity.getSelectedTeamId());
        if(!appointmentDisplay.getText().toString().equals("")) {
            currentClient.setAppt_dt(getFormattedDate(appointmentDisplay.getText().toString()));
        }
        else {
            currentClient.setAppt_dt(null);
        }

        if(!setAppointmentDisplay.getText().toString().equals("")) {
            currentClient.setAppt_set_dt(getFormattedDate(setAppointmentDisplay.getText().toString()));
        }
        else {
            currentClient.setAppt_dt(null);
        }

        if(!signedDisplay.getText().toString().equals("")) {
            currentClient.setSigned_dt(getFormattedDate(signedDisplay.getText().toString()));
            statusList = "signed";
        }
        else {
            currentClient.setSigned_dt(null);
        }
        if(!contractDisplay.getText().toString().equals("")) {
            currentClient.setUc_dt(getFormattedDate(contractDisplay.getText().toString()));
            statusList = "contract";
        }
        else {
            currentClient.setUc_dt(null);
        }
        if(!settlementDisplay.getText().toString().equals("")) {
            currentClient.setClosed_dt(getFormattedDate(settlementDisplay.getText().toString()));
            statusList = "closed";
        }
        else {
            currentClient.setClosed_dt(null);
        }

        if(leadSources != null) {
            for ( Object key : leadSources.keySet() ) {
                if(leadSources.get(key).equals(leadSource.getText().toString())) {
                    //TODO: Should probably wrap this in a try catch
                    currentClient.setLead_type_id(Integer.valueOf((String) key));
                    break;
                }
            }
        }

        if(deleteClient) {
            currentClient.setStatus("D");
            currentClient.setActivate_client(m_Text);
            statusList = "archived";
            if(parentActivity.isRecruiting()) {
                currentClient.setClosed_dt(null);
            }
        } else {
            if(currentClient.getStatus().equals("D")) {
                currentClient.setStatus("N");
                statusList = "pipeline";
            }
        }
    }

    private String getFormattedDate(String incomingDate) {
        String returnString = "";
        Date d;

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

        Calendar calendar = Calendar.getInstance();
        try {
            d = sdf.parse(incomingDate);
            calendar.setTime(d);

            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy");

            returnString = format.format(calendar.getTime()) + " 00:00:00 GMT";

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnString;
    }

    private String getFormattedForAppointmentDate(String incomingDate) {
        String returnString = "";
        Date d;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar calendar = Calendar.getInstance();
        try {
            d = sdf.parse(incomingDate);
            calendar.setTime(d);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            returnString = format.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnString;
    }

    private void setStatus() {
        if(!currentClient.getStatus().equalsIgnoreCase("D")) {
            if(currentClient.getClosed_dt() != null) {
                changeStatusColor(closedStatus);
            } else if(currentClient.getUc_dt() != null) {
                changeStatusColor(underContractStatus);
            } else if(currentClient.getSigned_dt() != null) {
                changeStatusColor(signedStatus);
            } else if(currentClient.getAppt_dt() != null) {
                changeStatusColor(pipelineStatus);
            }
        } else {
            changeStatusColor(archivedStatus);
        }
        if(currentClient.getType_id().equalsIgnoreCase("b")) {
            changeStatusColor(buyer);
        } else{
            changeStatusColor(seller);
        }
    }

    private void changeStatusColor(TextView status) {
        status.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
        status.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorLightGrey));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private boolean verifyInputFields() {
        boolean isVerified = true;

        if(parentActivity.isRecruiting()) {
            isVerified = verifyRecruiting();
        }
        else {
            isVerified = verifyMortgage();
        }

        return isVerified;
    }

    private boolean verifyMortgage() {
        boolean isVerified = true;

        if(typeSelected.equals("")) {
            parentActivity.showToast("Buyer or Seller is required");
            isVerified = false;
        }
        else if(firstNameText.getText().toString().equals("")) {
            parentActivity.showToast("First Name is required");
            isVerified = false;
        }
        else if(lastNameText.getText().toString().equals("")) {
            parentActivity.showToast("Last Name is required");
            isVerified = false;
        }
        else if(transAmount.getText().toString().equals("")) {
            parentActivity.showToast("Transaction Amount is required");
            isVerified = false;
        }
        else if(paidIncome.getText().toString().equals("")) {
            parentActivity.showToast("Paid Income is required");
            isVerified = false;
        }
        else if(gci.getText().toString().equals("")) {
            parentActivity.showToast("GCI is required");
            isVerified = false;
        }
        else if(!contractDisplay.getText().toString().equals("") && settlementDisplay.getText().toString().equals("")) {
            if(counter == 1) {
                parentActivity.showToast("You may want to add your Settlement Date");
                isVerified = false;
                counter+=1;
            } else {
                isVerified = true;
            }

        }

        return isVerified;

    }

    private boolean verifyRecruiting() {
        boolean isVerified = true;

        if(typeSelected.equals("")) {
            parentActivity.showToast("New or Experienced Recruit is required");
            isVerified = false;
        }
        else if(firstNameText.getText().toString().equals("")) {
            parentActivity.showToast("First Name is required");
            isVerified = false;
        }
        else if(lastNameText.getText().toString().equals("")) {
            parentActivity.showToast("Last Name is required");
            isVerified = false;
        }
        else if(transAmount.getText().toString().equals("")) {
            parentActivity.showToast("Closed Volume is required");
            isVerified = false;
        }
        else if(gci.getText().toString().equals("")) {
            parentActivity.showToast("Recruit Income is required");
            isVerified = false;
        }
        else if(!contractDisplay.getText().toString().equals("") && settlementDisplay.getText().toString().equals("")) {
            if(counter == 1) {
                parentActivity.showToast("You may want to add your Settlement Date");
                isVerified = false;
                counter+=1;
            } else {
                isVerified = true;
            }

        }
        return isVerified;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buyerButton:
                buyer.setTextColor(colorSchemeManager.getButtonText());
                buyer.setBackgroundResource(R.drawable.rounded_button);
                GradientDrawable drawable = (GradientDrawable) buyer.getBackground();
                drawable.setColor(colorSchemeManager.getButtonSelected());

                seller.setTextColor(colorSchemeManager.getButtonText());
                seller.setBackgroundResource(R.drawable.rounded_button);
                drawable = (GradientDrawable) seller.getBackground();
                drawable.setColor(colorSchemeManager.getButtonBackground());
                typeSelected = "b";
                break;
            case R.id.sellerButton:
                seller.setTextColor(colorSchemeManager.getButtonText());
                seller.setBackgroundResource(R.drawable.rounded_button);
                drawable = (GradientDrawable) seller.getBackground();
                drawable.setColor(colorSchemeManager.getButtonSelected());

                buyer.setTextColor(colorSchemeManager.getButtonText());
                buyer.setBackgroundResource(R.drawable.rounded_button);
                drawable = (GradientDrawable) buyer.getBackground();
                drawable.setColor(colorSchemeManager.getButtonBackground());
                typeSelected = "s";
                break;
            case R.id.calculateGciPercent:
                calculateTransPercentage(gciPercent, gci);
                break;
            case R.id.calculateIncomePercent:
                calculateGciPercentage(incomePercent, paidIncome);
                break;
            case R.id.lock:
                parentActivity.showToast("This client account has been locked by your team administrator.");
                break;
            case R.id.saveButton://notify of success update api
                if(parentActivity.isAdminMode()) {
                    popAdminConfirmDialog();
                }
                else {
                    if(verifyInputFields()) {
                        updateCurrentClient(false);
                        saveClient();
                    }
                }

                break;
            case R.id.addClientSaveButton://notify of success update api
                if(parentActivity.isAdminMode()) {
                    popAdminConfirmDialog();
                }
                else {
                    if(verifyInputFields()) {
                        saveNewClient();
                    }
                }

                break;
            case R.id.signedDatePicker:
            case R.id.signedDateDisplay:
            case R.id.signedDateTitle:
                showDatePickerDialog(signedSelectedYear, signedSelectedMonth, signedSelectedDay, "signed");
                break;
            case R.id.underContractDatePicker:
            case R.id.underContractDateDisplay:
            case R.id.underContractDateTitle:
                showDatePickerDialog(contractSelectedYear, contractSelectedMonth, contractSelectedDay, "contract");
                break;
            case R.id.settlementDatePicker:
            case R.id.settlementDateDisplay:
            case R.id.settlementDateTitle:
                showDatePickerDialog(settlementSelectedYear, settlementSelectedMonth, settlementSelectedDay, "settlement");
                break;
            case R.id.appointmentDatePicker:
            case R.id.appointmentDateDisplay:
            case R.id.appointmentDateTitle:
                showDatePickerDialog(appointmentSelectedYear, appointmentSelectedMonth, appointmentSelectedDay, "appointment");
                break;
            case R.id.appointmentSetDatePicker:
            case R.id.appointmentSetDateDisplay:
            case R.id.appointmentSetDateTitle:
                showDatePickerDialog(appointmentSetSelectedYear, appointmentSetSelectedMonth, appointmentSetSelectedDay, "setAppointment");
                break;
            case R.id.addAppointmentDatePicker:
            case R.id.addAppointmentDateDisplay:
            case R.id.addAppointmentDateTitle:
                showDatePickerDialog(addAppointmentSelectedYear, addAppointmentSelectedMonth, addAppointmentSelectedDay, "addAppointment");
                break;
            case R.id.signedDateButton:
                clearDisplayDate("signed");
                removeStatusColor(signedStatus);
                break;
            case R.id.underContractDateButton:
                clearDisplayDate("contract");
                removeStatusColor(underContractStatus);
                break;
            case R.id.settlementDateButton:
                clearDisplayDate("settlement");
                removeStatusColor(closedStatus);
                break;
            case R.id.appointmentDateButton:
                clearDisplayDate("appointment");
                removeStatusColor(pipelineStatus);
                break;
            case R.id.appointmentSetDateButton:
                clearDisplayDate("setAppointment");
                break;
            case R.id.addAppointmentDateButton:
                String formattedDay = null;
                String formattedMonth = null;
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                if(addAppointmentSelectedMonth == month && addAppointmentSelectedDay == day && addAppointmentSelectedYear == year) {
                    month += 1;
                    String formattedDate = getFormattedForAppointmentDate(month + "/" + day +"/" + year);
                    apiManager.addAppointmentNote(ClientManageFragment.this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id(), "Added from Mobile APP", "APPTS", formattedDate);
                }
                else {
                    if(addAppointmentSelectedDay < 10) {
                        formattedDay = "0" + addAppointmentSelectedDay;
                    }
                    else {
                        formattedDay = "" + addAppointmentSelectedDay;
                    }

                    if(addAppointmentSelectedMonth < 10) {
                        formattedMonth = "0" + addAppointmentSelectedMonth;
                    }
                    else {
                        formattedMonth = "" + addAppointmentSelectedMonth;
                    }
                    addAppointmentSelectedMonth += 1;
                    String formattedDate =  getFormattedForAppointmentDate(formattedMonth + "/" + formattedDay +"/" + addAppointmentSelectedYear);
                    apiManager.addAppointmentNote(ClientManageFragment.this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id(), "Added from Mobile APP", "APPTS", formattedDate);
                }

                break;
            case R.id.exportContactButton:
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                String phone = "";
                int phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

                if(currentClient != null) {
                    if(currentClient.getMobile_phone() != null) {
                        phone = currentClient.getMobile_phone();
                    } else {
                        phone = currentClient.getHome_phone();
                        phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
                    }
                }

                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, currentClient.getFirst_name() != null ? currentClient.getFirst_name() : "" + " " + currentClient.getLast_name() != null ? currentClient.getLast_name() : "")
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, currentClient.getEmail())
                        .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, phoneType);


                startActivityForResult(contactIntent, 1);
                break;
            case R.id.archiveButton:
                if(parentActivity.isRecruiting()) {
                    popReasonDialog("Reason for Losing Recruit?", "Archived");
                }
                else {
                    popReasonDialog("Reason for Archiving Client?", "Archived");
                }
                break;
            case R.id.activateButton:
                popReasonDialog("Reason for Activating Client?", "Activated");
                break;
            case R.id.clientNotesButton:
                parentActivity.setNoteOrMessage("Note");
                navigationManager.stackReplaceFragment(ClientNoteFragment.class);
                break;
            case R.id.leadSource:
            case R.id.leadSourceInputLayout:
                // setup the alert builder
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(parentActivity);
                builder.setTitle("Choose a lead source");

                // add a list
                if(leadSources != null) {
                    final String[] sources = new String[leadSources.size()];
                    int counter = 0;
                    for ( Object key : leadSources.keySet() ) {
                        sources[counter] = (String) leadSources.get(key);
                        counter++;
                    }

                    builder.setItems(sources, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leadSource.setText(sources[which]);
                        }
                    });
                }

                // create and show the alert dialog
                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                break;
        }
    }

    private void popAdminConfirmDialog() {
        String message = "Admin. Do you want to save?";
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(parentActivity);
        builder.setMessage(message)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(parentActivity.getSelectedClient() != null) {
                            if(verifyInputFields()) {
                                updateCurrentClient(false);
                                saveClient();
                            }
                        }
                        else {
                            saveNewClient();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dataController.clearUpdatedRecords();
                    }
                });
        builder.create();
        builder.show();
    }

    private void saveClient(){
        apiManager.sendAsyncUpdateClients(this, dataController.getAgent().getAgent_id(), currentClient);
    }

    private void saveNewClient() {
        ClientObject newClient = new ClientObject();
        if(verifyInputFields()) {
            initializeNewClient(newClient);
        }
    }

    private void initializeNewClient(ClientObject newClient) {
        //These can never be null
        newClient.setFirst_name(firstNameText.getText().toString());
        newClient.setLast_name(lastNameText.getText().toString());
        newClient.setTrans_amt(transAmount.getText().toString());
        if(!parentActivity.isRecruiting()) {
            newClient.setCommission_amt(paidIncome.getText().toString());
        }

        //These need to be checked for null
        newClient.setGross_commission_amt(gci.getText().toString().equals("") ? null : gci.getText().toString());
        newClient.setMobile_phone(phoneText.getText().toString().equals("") ? null : phoneText.getText().toString());
        newClient.setEmail(emailText.getText().toString().equals("") ? null : emailText.getText().toString());
        newClient.setNote(noteText.getText().toString().equals("") ? null : noteText.getText().toString());
        newClient.setAppt_dt(null);
        newClient.setSigned_dt(null);
        newClient.setUc_dt(null);
        newClient.setClosed_dt(null);
        newClient.setType_id(typeSelected);
        newClient.setIs_priority(prioritySwitch.isChecked() ? 1 : 0);
        newClient.setMarket_id(String.valueOf(parentActivity.getSelectedTeamMarketId()));

        if(!appointmentDisplay.getText().equals("")) {
            newClient.setAppt_dt(getFormattedDate(appointmentDisplay.getText().toString()));
        }
        if(!setAppointmentDisplay.getText().equals("")) {
            newClient.setAppt_set_dt(getFormattedDate(setAppointmentDisplay.getText().toString()));
        }
        if(!signedDisplay.getText().equals("")) {
            newClient.setSigned_dt(getFormattedDate(signedDisplay.getText().toString()));
        }
        if(!contractDisplay.getText().equals("")) {
            newClient.setUc_dt(getFormattedDate(contractDisplay.getText().toString()));
        }
        if(!settlementDisplay.getText().equals("")) {
            newClient.setClosed_dt(getFormattedDate(settlementDisplay.getText().toString()));
        }

        newClient.setMarket_id(String.valueOf(parentActivity.getSelectedTeamMarketId()));

        for ( Object key : leadSources.keySet() ) {
            System.out.println(leadSource.getText().toString());
            if(leadSources.get(key).equals(leadSource.getText().toString())) {
                newClient.setLead_type_id(Integer.valueOf((String) key));
                break;
            }
        }

        apiManager.sendAsyncAddClient(this, dataController.getAgent().getAgent_id(), newClient);
    }

    private void initCommonButtons() {
        signedClear = getView().findViewById(R.id.signedDateButton);
        signedClear.setOnClickListener(this);
        contractClear = getView().findViewById(R.id.underContractDateButton);
        contractClear.setOnClickListener(this);
        settlementClear = getView().findViewById(R.id.settlementDateButton);
        settlementClear.setOnClickListener(this);
        appointmentClear = getView().findViewById(R.id.appointmentDateButton);
        appointmentClear.setOnClickListener(this);
        appointmentSetClear = getView().findViewById(R.id.appointmentSetDateButton);
        appointmentSetClear.setOnClickListener(this);
        addAppointmentButton = getView().findViewById(R.id.addAppointmentDateButton);
        addAppointmentButton.setOnClickListener(this);
        exportContact = getView().findViewById(R.id.exportContactButton);
        exportContact.setOnClickListener(this);

        buyer = parentActivity.findViewById(R.id.buyerButton);
        buyer.setOnClickListener(this);
        seller = parentActivity.findViewById(R.id.sellerButton);
        seller.setOnClickListener(this);

        calculateGciPercent = getView().findViewById(R.id.calculateGciPercent);
        calculateGciPercent.setOnClickListener(this);
        calculateIncomePercent = getView().findViewById(R.id.calculateIncomePercent);
        calculateIncomePercent.setOnClickListener(this);

        noteButton = getView().findViewById(R.id.clientNotesButton);
        noteButton.setOnClickListener(this);

        if(parentActivity.getSelectedClient() == null) {
            noteButton.setVisibility(View.GONE);
            exportContact.setText("Import");
        }

    }

    private void initAddClientButtons() {
        cancelButton = parentActivity.findViewById(R.id.cancelButton);
        if(cancelButton != null) {
            cancelButton.setOnClickListener(this);
        }
        saveButton = parentActivity.findViewById(R.id.addClientSaveButton);
        if(saveButton != null) {
            saveButton.setOnClickListener(this);
        }
    }

    private void initEditClientButtons() {

        archiveButton = parentActivity.findViewById(R.id.archiveButton);
        if(archiveButton != null) {
            archiveButton.setOnClickListener(this);
        }
        activateButton = parentActivity.findViewById(R.id.activateButton);
        if(activateButton != null) {
            activateButton.setOnClickListener(this);
        }

        saveButton = parentActivity.findViewById(R.id.saveButton);
        if(saveButton != null) {
            saveButton.setOnClickListener(this);
        }
    }

    private void showDatePickerDialog(final int selectedYear, final int selectedMonth, final int selectedDay, final String calendarCaller) {
        DatePickerDialog dialog = new DatePickerDialog(parentActivity, android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                updateDisplayDate(year, month, day, calendarCaller);
            }
        }, selectedYear, selectedMonth, selectedDay);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    private void clearDisplayDate(String calendarCaller) {
        String setText = "Tap To Select";
        switch (calendarCaller) {
            case "signed":
                signedDisplay.setText("");
                signedDisplay.setHint(setText);
                break;
            case "contract":
                contractDisplay.setText("");
                contractDisplay.setHint(setText);
                break;
            case "settlement":
                settlementDisplay.setText("");
                settlementDisplay.setHint(setText);
                break;
            case "appointment":
                appointmentDisplay.setText("");
                appointmentDisplay.setHint(setText);
                break;
            case "setAppointment":
                setAppointmentDisplay.setText("");
                setAppointmentDisplay.setHint(setText);
                break;
            case "addAppointment":
                addAppointmentDisplay.setText("");
                addAppointmentDisplay.setHint(setText);
                break;
        }
        updateStatus();
    }

    private void updateStatus() {
        Date d = null;
        Calendar currentTime = Calendar.getInstance();
        Calendar updatedTime = Calendar.getInstance();

        if(appointmentDisplay.getText().toString().matches(".*\\d+.*")){
            getTime(d, updatedTime, appointmentDisplay);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                resetAllStatusColors();
                activateStatusColor(pipelineStatus);
                currentStatus = "pipeline";
            }
        }
        if(signedDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, signedDisplay);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                resetAllStatusColors();
                activateStatusColor(signedStatus);
                currentStatus = "signed";
            }
        }
        if(contractDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, contractDisplay);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                resetAllStatusColors();
                activateStatusColor(underContractStatus);
                currentStatus = "contract";
            }
        }
        if(settlementDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, settlementDisplay);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                resetAllStatusColors();
                activateStatusColor(closedStatus);
                currentStatus = "closed";
            }
        }
        if(clientStatus != null && clientStatus.equals("D")) {
            resetAllStatusColors();
            activateStatusColor(archivedStatus);
        }
    }

    private void getTime(Date d, Calendar updatedTime, TextView displayView) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");

        try {
            d = sdf.parse(displayView.getText().toString());
            updatedTime.setTime(d);
        } catch (ParseException e) {
            parentActivity.showToast("Error parsing selected date");
            e.printStackTrace();
        }
    }

    private void resetAllStatusColors() {
        pipelineStatus.setTextColor(colorSchemeManager.getButtonText());
        pipelineStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        underContractStatus.setTextColor(colorSchemeManager.getButtonText());
        underContractStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        closedStatus.setTextColor(colorSchemeManager.getButtonText());
        closedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        signedStatus.setTextColor((colorSchemeManager.getButtonText()));
        signedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        archivedStatus.setTextColor(colorSchemeManager.getButtonText());
        archivedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
    }

    private void activateStatusColor(TextView status) {
        status.setTextColor(colorSchemeManager.getButtonText());
        status.setBackgroundColor(colorSchemeManager.getButtonSelected());
    }

    private void removeStatusColor(TextView status) {
        status.setTextColor(colorSchemeManager.getButtonText());
        status.setBackgroundColor(colorSchemeManager.getButtonBackground());
    }

    private void updateDisplayDate(int year, int month, int day, String calendarCaller) {

        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        String formatDate = year + "/" + month + "/" + day;
        Calendar updatedTime = Calendar.getInstance();

        try {
            d = formatter.parse(formatDate);
            updatedTime.setTime(d);
        } catch (ParseException e) {
            parentActivity.showToast("Error parsing selected date");
            e.printStackTrace();
        }

        switch (calendarCaller) {
            case "signed":
                signedSelectedYear = year;
                signedSelectedMonth = month - 1;
                signedSelectedDay = day;
                signedDisplay.setText(sdf.format(updatedTime.getTime()));
                updateStatus();
                break;
            case "contract":
                contractSelectedYear = year;
                contractSelectedMonth = month - 1;
                contractSelectedDay = day;
                contractDisplay.setText(sdf.format(updatedTime.getTime()));
                updateStatus();
                break;
            case "settlement":
                settlementSelectedYear = year;
                settlementSelectedMonth = month - 1;
                settlementSelectedDay = day;
                settlementDisplay.setText(sdf.format(updatedTime.getTime()));
                updateStatus();
                break;
            case "appointment":
                appointmentSelectedYear = year;
                appointmentSelectedMonth = month - 1;
                appointmentSelectedDay = day;
                appointmentDisplay.setText(sdf.format(updatedTime.getTime()));
                updateStatus();
                break;
            case "setAppointment":
                appointmentSetSelectedYear = year;
                appointmentSetSelectedMonth = month - 1;
                appointmentSetSelectedDay = day;
                setAppointmentDisplay.setText(sdf.format(updatedTime.getTime()));
                updateStatus();
                break;
            case "addAppointment":
                addAppointmentSelectedYear = year;
                addAppointmentSelectedMonth = month;
                addAppointmentSelectedDay = day;
                addAppointmentDisplay.setText(sdf.format(updatedTime.getTime()));
                break;

        }


    }

    private void initRecruitingAppointmentsListView() {
        mListView = getView().findViewById(R.id.appointmentList);
    }

    private void fillListViewWithData(ArrayList<NotesObject> noteList) {
        if(getContext() != null) {
            AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), noteList, parentActivity.getColorSchemeManager());
            mListView.setAdapter(adapter);
            int height = 0;
            for(NotesObject note : noteList) {
                height += 130;
            }
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mListView.getLayoutParams();
            lp.height = height;
            mListView.setLayoutParams(lp);

        }

    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_CLIENT_SETTINGS) {
            AsyncParameterJsonObject settingsJson = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncParameterJsonObject.class);
            ParameterObject settings = settingsJson.getParameter();
            if(settings != null) {
                currentClient.setActivate_client(settings.getValue());
            }
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initializeClient();
                    loader.setVisibility(View.GONE);
                }
            });
        }
        else if(returnType == ApiReturnTypes.CREATE_NOTE) {
//            hideKeyboard(getView());
//            parentActivity.showToast("Added Appointment");
            // TODO: Why do I do this?
//            updateCurrentClient(!currentClient.getStatus().equals("D"));
//            saveClient();
        }
        else if(returnType == ApiReturnTypes.CREATE_APPT_NOTE) {
            apiManager.getClientNotes(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id());
            hideKeyboard(getView());
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clearDisplayDate("addAppointment");
                    parentActivity.showToast("Added Appointment");
                }
            });
        }
        else if(returnType == ApiReturnTypes.UPDATE_CLIENT) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parentActivity.onBackPressed();
                    parentActivity.showToast("Client updates saved");
                }
            });
        }
        else if(returnType == ApiReturnTypes.UPDATE_SETTINGS) {
            loader.setVisibility(View.GONE);
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parentActivity.onBackPressed();
                    parentActivity.showToast("Client has been archived");
                }
            });
        }
        else if(returnType == ApiReturnTypes.GET_LEAD_SOURCES) {
            AsyncLeadSourcesJsonObject leadSourcesObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncLeadSourcesJsonObject.class);
            leadSources = leadSourcesObject.getLead_sources();
            if(parentActivity.getSelectedClient() != null) {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initializeClient();
                    }
                });
            }
        }
        else if(returnType == ApiReturnTypes.GET_NOTES) {
            AsyncNotesJsonObject asyncNotesJsonObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncNotesJsonObject.class);
            final NotesObject[] allNotes = asyncNotesJsonObject.getClient_logs();
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillListViewWithData(new ArrayList<>(Arrays.asList(allNotes)));
                }
            });
        }
        else if(returnType == ApiReturnTypes.CREATE_CLIENT) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parentActivity.showToast("Client Saved");
                    navigationManager.navigateToClientListAndClearStack(currentStatus);
                }
            });
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {}

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_LEAD_SOURCES) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initializeClient();
                }
            });
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus) {
            hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }
}

package co.sisu.mobile.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.AsyncLeadSourcesJsonObject;
import co.sisu.mobile.models.ClientObject;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Brady Groharing on 3/5/2018.
 */

public class AddClientFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener, View.OnFocusChangeListener {

    public final int PICK_CONTACT = 2015;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private TextInputEditText firstNameText, lastNameText, emailText, phoneText, transAmount, paidIncome, gci, noteText, gciPercent, incomePercent;
    private TextView signedDisplay, contractDisplay, settlementDisplay, appointmentDisplay, setAppointmentDisplay, pipelineStatus, signedStatus, underContractStatus, closedStatus, archivedStatus,
                     appointmentDateTitle, setAppointmentDateTitle, signedDateTitle, underContractDateTitle, settlementDateTitle, dollarSign1, dollarSign2, commissionEquals, gciEquals,
                     percentSign1, percentSign2, statusLabel, priorityText, leadSource, otherAppointmentsTitle;
    private Button signedClear, contractClear, settlementClear, appointmentClear, setAppointmentClear, calculateGciPercent, calculateIncomePercent, importContactButton, buyerButton, sellerButton, addAppointmentButton;
    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, phoneLayout, transAmountLayout, paidIncomeLayout, gciLayout, noteLayout, gciPercentLayout, commissionInputLayout, leadSourceInputLayout;
    private String typeSelected;
    private int signedSelectedYear, signedSelectedMonth, signedSelectedDay;
    private int contractSelectedYear, contractSelectedMonth, contractSelectedDay;
    private int settlementSelectedYear, settlementSelectedMonth, settlementSelectedDay;
    private int appointmentSelectedYear, appointmentSelectedMonth, appointmentSelectedDay;
    private int setAppointmentSelectedYear, setAppointmentSelectedMonth, setAppointmentSelectedDay;
    private int counter;
    private Switch prioritySwitch;
    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private String currentStatus;
    private ColorSchemeManager colorSchemeManager;
    private LinkedHashMap leadSources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView contentView = (ScrollView) inflater.inflate(R.layout.activity_add_client, container, false);
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
        counter = 1;
        initializeButtons();
        initializeForm();
        initializeCalendar();
        initActionBar();
        setupLabels();
        setupColorScheme();
        apiManager.getLeadSources(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId());
    }

    private void setupLabels() {
//        buyerButton.setText(parentActivity.localizeLabel(getResources().getString(R.string.buyer)));
//        sellerButton.setText(parentActivity.localizeLabel(getResources().getString(R.string.seller)));
//
//        firstNameLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.first_name_hint_non_req)));
//        lastNameLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.last_name_hint_non_req)));
//        transAmountLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.transaction_amount_hint)));
//        gciLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.gci_hint)));
//        if(parentActivity.isRecruiting()) {
//            gciLayout.setHint("Recruit Income");
//        }
//        commissionInputLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.commission_hint)));
//        leadSourceInputLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.lead_source_hint)));
//        phoneLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.phone_hint)));
//        emailLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.email_hint)));
//        statusLabel.setText(parentActivity.localizeLabel(getResources().getString(R.string.status)));
//        pipelineStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.pipeline)));
//        signedStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.signed)));
//        underContractStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.contract)));
//        closedStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.closed)));
//        archivedStatus.setText(parentActivity.localizeLabel(getResources().getString(R.string.archived)));
//        appointmentDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.appointmentDateTitle)));
//        setAppointmentDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.appointmentSetDateTitle)));
//        signedDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.signedDateTitle)));
//        underContractDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.underContractTitle)));
//        settlementDateTitle.setText(parentActivity.localizeLabel(getResources().getString(R.string.settlementDateTitle)));
//        noteLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.notes)));
    }

    private void setupColorScheme() {
//        firstNameText.setTextColor(colorSchemeManager.getDarkerTextColor());
//        lastNameText.setTextColor(colorSchemeManager.getDarkerTextColor());
//        emailText.setTextColor(colorSchemeManager.getDarkerTextColor());
//        phoneText.setTextColor(colorSchemeManager.getDarkerTextColor());
//        transAmount.setTextColor(colorSchemeManager.getDarkerTextColor());
//        paidIncome.setTextColor(colorSchemeManager.getDarkerTextColor());
//        gci.setTextColor(colorSchemeManager.getDarkerTextColor());
//        noteText.setTextColor(colorSchemeManager.getDarkerTextColor());
//        gciPercent.setTextColor(colorSchemeManager.getDarkerTextColor());
//        incomePercent.setTextColor(colorSchemeManager.getDarkerTextColor());
//        leadSource.setTextColor(colorSchemeManager.getDarkerTextColor());
//
//        signedDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
//        contractDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
//        settlementDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
//        appointmentDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
//        setAppointmentDisplay.setHintTextColor(colorSchemeManager.getDarkerTextColor());
//
//        signedDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
//        contractDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
//        settlementDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
//        appointmentDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
//        setAppointmentDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
//
//        pipelineStatus.setTextColor(colorSchemeManager.getButtonText());
//        pipelineStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
//        signedStatus.setTextColor(colorSchemeManager.getButtonText());
//        signedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
//        underContractStatus.setTextColor(colorSchemeManager.getButtonText());
//        underContractStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
//        closedStatus.setTextColor(colorSchemeManager.getButtonText());
//        closedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
//        archivedStatus.setTextColor(colorSchemeManager.getButtonText());
//        archivedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
//
//        appointmentDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
//        setAppointmentDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
//        signedDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
//        underContractDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
//        settlementDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
//
//        dollarSign1.setTextColor(colorSchemeManager.getDarkerTextColor());
//        dollarSign2.setTextColor(colorSchemeManager.getDarkerTextColor());
//        percentSign1.setTextColor(colorSchemeManager.getDarkerTextColor());
//        percentSign2.setTextColor(colorSchemeManager.getDarkerTextColor());
//
//        commissionEquals.setTextColor(colorSchemeManager.getDarkerTextColor());
//        gciEquals.setTextColor(colorSchemeManager.getDarkerTextColor());
//        statusLabel.setTextColor(colorSchemeManager.getDarkerTextColor());
//
//        setInputTextLayoutColor(firstNameLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(lastNameLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(emailLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(phoneLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(transAmountLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(paidIncomeLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(gciLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(noteLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(gciPercentLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(commissionInputLayout, colorSchemeManager.getIconActive());
//        setInputTextLayoutColor(leadSourceInputLayout, colorSchemeManager.getIconActive());

        importContactButton.setHighlightColor(colorSchemeManager.getButtonSelected());
        importContactButton.setBackgroundResource(R.drawable.rounded_button);
        importContactButton.setTextColor(colorSchemeManager.getButtonText());
        GradientDrawable drawable = (GradientDrawable) importContactButton.getBackground();
        drawable.setColor(colorSchemeManager.getButtonBackground());

//        buyerButton.setTextColor(colorSchemeManager.getButtonText());
//        buyerButton.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) buyerButton.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        sellerButton.setTextColor(colorSchemeManager.getButtonText());
//        sellerButton.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) sellerButton.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());

//        signedClear.setTextColor(colorSchemeManager.getButtonText());
//        signedClear.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) signedClear.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        contractClear.setTextColor(colorSchemeManager.getButtonText());
//        contractClear.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) contractClear.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        settlementClear.setTextColor(colorSchemeManager.getButtonText());
//        settlementClear.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) settlementClear.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        appointmentClear.setTextColor(colorSchemeManager.getButtonText());
//        appointmentClear.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) appointmentClear.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        setAppointmentClear.setTextColor(colorSchemeManager.getButtonText());
//        setAppointmentClear.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) setAppointmentClear.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        addAppointmentButton.setTextColor(colorSchemeManager.getButtonText());
//        addAppointmentButton.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) addAppointmentButton.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        calculateGciPercent.setTextColor(colorSchemeManager.getButtonText());
//        calculateGciPercent.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) calculateGciPercent.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());
//
//        calculateIncomePercent.setTextColor(colorSchemeManager.getButtonText());
//        calculateIncomePercent.setBackgroundResource(R.drawable.rounded_button);
//        drawable = (GradientDrawable) calculateIncomePercent.getBackground();
//        drawable.setColor(colorSchemeManager.getButtonBackground());

//        priorityText.setTextColor(colorSchemeManager.getDarkerTextColor());
//        otherAppointmentsTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
//
//        int[][] states = new int[][] {
//                new int[] {-android.R.attr.state_checked},
//                new int[] {android.R.attr.state_checked},
//        };
//
//        int[] thumbColors = new int[] {
//                Color.GRAY,
//                colorSchemeManager.getSegmentSelected()
//        };
//
//        int[] trackColors = new int[] {
//                Color.GRAY,
//                colorSchemeManager.getSegmentSelected()
//        };
//
//        DrawableCompat.setTintList(DrawableCompat.wrap(prioritySwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
//        DrawableCompat.setTintList(DrawableCompat.wrap(prioritySwitch.getTrackDrawable()), new ColorStateList(states, trackColors));
    }

    private void setInputTextLayoutColor(TextInputLayout layout, int color) {
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

    private void initActionBar() {
//        TextView cancelButton = parentActivity.findViewById(R.id.cancelButton);
//        TextView saveButton = parentActivity.findViewById(R.id.addClientSaveButton);
//        if(cancelButton != null) {
//            cancelButton.setOnClickListener(this);
//        }
//
//        if(saveButton != null) {
//            saveButton.setOnClickListener(this);
//        }
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

    private void convertPercentToDollar(float percent, EditText dollarText, EditText parent) {
        float dollar;
        int transTotal = Integer.parseInt(parent.getText().toString());
        if(transTotal != 0 && percent <= 100 && percent > 0) {
            dollar = percent * transTotal;
            if(dollar <= transTotal && dollar > 0) {
                dollarText.setText(String.valueOf(dollar).substring(0,String.valueOf(dollar).indexOf('.')));
            }
        }
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

        setAppointmentSelectedYear = year;
        setAppointmentSelectedMonth = month;
        setAppointmentSelectedDay = day;
    }

    private void initializeForm() {
        typeSelected = "";
//        firstNameText = getView().findViewById(R.id.editFirstName);
//        firstNameText.setOnFocusChangeListener(this);
//        lastNameText = getView().findViewById(R.id.addClientEditLastName);
//        lastNameText.setOnFocusChangeListener(this);
//        emailText = getView().findViewById(R.id.editEmail);
//        emailText.setOnFocusChangeListener(this);
//        phoneText = getView().findViewById(R.id.editPhone);
//        phoneText.setOnFocusChangeListener(this);
//        transAmount = getView().findViewById(R.id.editTransAmount);
//        transAmount.setOnFocusChangeListener(this);
//        paidIncome = getView().findViewById(R.id.editPaidIncome);
//        paidIncome.setOnFocusChangeListener(this);
//        gci = getView().findViewById(R.id.editGci);
//        gci.setOnFocusChangeListener(this);
//        gciPercent = getView().findViewById(R.id.editGciPercent);
//        gciPercent.setOnFocusChangeListener(this);
//        incomePercent = getView().findViewById(R.id.editPaidIncomePercent);
//        incomePercent.setOnFocusChangeListener(this);
//        leadSource = getView().findViewById(R.id.leadSource);
//        leadSource.setOnClickListener(this);

//        pipelineStatus = getView().findViewById(R.id.pipelineButton);
//        signedStatus = getView().findViewById(R.id.signedButton);
//        underContractStatus = getView().findViewById(R.id.contractButton);
//        closedStatus = getView().findViewById(R.id.closedButton);
//        archivedStatus = getView().findViewById(R.id.archivedButton);
//        noteText = getView().findViewById(R.id.editNotes);

//        firstNameLayout = getView().findViewById(R.id.firstNameInputLayoutAdd);
//        lastNameLayout = getView().findViewById(R.id.lastNameInputLayout);
//        emailLayout = getView().findViewById(R.id.emailInputLayout);
//        phoneLayout = getView().findViewById(R.id.phoneInputLayout);
//        transAmountLayout = getView().findViewById(R.id.transAmountInputLayout);
//        paidIncomeLayout = getView().findViewById(R.id.commissionInputLayout);
//        gciLayout = getView().findViewById(R.id.gciInputLayout);
//        noteLayout = getView().findViewById(R.id.notesInputLayout);
//        gciPercentLayout = getView().findViewById(R.id.gciInputLayout);
//        commissionInputLayout = getView().findViewById(R.id.commissionInputLayout);
//        leadSourceInputLayout = getView().findViewById(R.id.leadSourceInputLayout);
//        leadSourceInputLayout.setOnClickListener(this);

//        dollarSign1 = getView().findViewById(R.id.dollarSign);
//        dollarSign2 = getView().findViewById(R.id.dollarSign2);
//        percentSign1 = getView().findViewById(R.id.percentSign);
//        percentSign2 = getView().findViewById(R.id.percentSign2);
//        statusLabel = getView().findViewById(R.id.statusLabel);

//        commissionEquals = getView().findViewById(R.id.commissionEquals);
//        gciEquals = getView().findViewById(R.id.gciEquals);
//        priorityText = getView().findViewById(R.id.priorityText);
//        otherAppointmentsTitle = getView().findViewById(R.id.otherAppointmentsTitle);

//        RelativeLayout statusGroup = getView().findViewById(R.id.statusRadioGroup);
//        ConstraintLayout underContractSection = getView().findViewById(R.id.underContractSection);
//        ConstraintLayout signedDateSection = getView().findViewById(R.id.signedDateSection);
//        ConstraintLayout addAppointmentDateSection = getView().findViewById(R.id.addAppointmentDateSection);

//        if(parentActivity.isRecruiting()) {
//            paidIncomeLayout.setVisibility(View.GONE);
//            commissionInputLayout.setVisibility(View.GONE);
//            calculateIncomePercent.setVisibility(View.GONE);
//            statusLabel.setVisibility(View.GONE);
//            statusGroup.setVisibility(View.GONE);
//            underContractSection.setVisibility(View.GONE);
//            signedDateSection.setVisibility(View.GONE);
//            percentSign2.setVisibility(View.GONE);
//            commissionEquals.setVisibility(View.GONE);
//            noteLayout.setVisibility(View.GONE);
//            otherAppointmentsTitle.setVisibility(View.VISIBLE);
//            addAppointmentDateSection.setVisibility(View.VISIBLE);
//        }
//        else {
//            otherAppointmentsTitle.setVisibility(View.GONE);
//            addAppointmentDateSection.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onClick(View v) {
        Drawable active = getResources().getDrawable(R.drawable.rounded_button_active);
        Drawable inactive = getResources().getDrawable(R.drawable.rounded_button);
        switch (v.getId()) {
            case R.id.cancelButton:
                parentActivity.onBackPressed();
                break;
            case R.id.addClientSaveButton:
                if(parentActivity.isAdminMode()) {
                    popAdminConfirmDialog();
                }
                else {
                    saveClient();
                }
                    //animation of confirmation
//                    onBackPressed();
                break;
            case R.id.buyerButton:
                buyerButton.setTextColor(colorSchemeManager.getButtonText());
                buyerButton.setBackgroundResource(R.drawable.rounded_button);
                GradientDrawable drawable = (GradientDrawable) buyerButton.getBackground();
                drawable.setColor(colorSchemeManager.getButtonSelected());

                sellerButton.setTextColor(colorSchemeManager.getButtonText());
                sellerButton.setBackgroundResource(R.drawable.rounded_button);
                drawable = (GradientDrawable) sellerButton.getBackground();
                drawable.setColor(colorSchemeManager.getButtonBackground());
//                buyerButton.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
//                buyerButton.setBackground(active);
//                sellerButton.setBackground(inactive);
//                sellerButton.setTextColor(ContextCompat.getColor(parentActivity,R.color.colorLightGrey));
                typeSelected = "b";
                break;
            case R.id.sellerButton:
                sellerButton.setTextColor(colorSchemeManager.getButtonText());
                sellerButton.setBackgroundResource(R.drawable.rounded_button);
                drawable = (GradientDrawable) sellerButton.getBackground();
                drawable.setColor(colorSchemeManager.getButtonSelected());

                buyerButton.setTextColor(colorSchemeManager.getButtonText());
                buyerButton.setBackgroundResource(R.drawable.rounded_button);
                drawable = (GradientDrawable) buyerButton.getBackground();
                drawable.setColor(colorSchemeManager.getButtonBackground());

//                sellerButton.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
//                sellerButton.setBackground(active);
//                buyerButton.setBackground(inactive);
//                buyerButton.setTextColor(ContextCompat.getColor(parentActivity,R.color.colorLightGrey));
                typeSelected = "s";
                break;
            case R.id.calculateGciPercent:
                calculateTransPercentage(gciPercent, gci);
                break;
            case R.id.calculateIncomePercent:
                calculateGciPercentage(incomePercent, paidIncome);
                break;
            case R.id.importContactButton:
                //do stuff for import
                if (ContextCompat.checkSelfPermission(parentActivity,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(parentActivity,
                            Manifest.permission.READ_CONTACTS)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(parentActivity,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
                else {
                    launchContactPicker();
                }
                break;
            case R.id.signedDatePicker:
            case R.id.signedDateDisplay:
            case R.id.signedDateTitle:
//                Toast.makeText(AddClientActivity.this, "SIGNED DATE", Toast.LENGTH_SHORT).show();
                showDatePickerDialog(signedSelectedYear, signedSelectedMonth, signedSelectedDay, "signed");
                break;
            case R.id.underContractDatePicker:
            case R.id.underContractDateDisplay:
            case R.id.underContractDateTitle:
//                Toast.makeText(AddClientActivity.this, "UNDER CONTRACT DATE", Toast.LENGTH_SHORT).show();
                showDatePickerDialog(contractSelectedYear, contractSelectedMonth, contractSelectedDay, "contract");
                break;
            case R.id.settlementDatePicker:
            case R.id.settlementDateDisplay:
            case R.id.settlementDateTitle:
//                Toast.makeText(AddClientActivity.this, "SETTLEMENT DATE", Toast.LENGTH_SHORT).show();
                showDatePickerDialog(settlementSelectedYear, settlementSelectedMonth, settlementSelectedDay, "settlement");
                break;
            case R.id.appointmentDatePicker:
            case R.id.appointmentDateDisplay:
            case R.id.appointmentDateTitle:
//                Toast.makeText(AddClientActivity.this, "SETTLEMENT DATE", Toast.LENGTH_SHORT).show();
                showDatePickerDialog(appointmentSelectedYear, appointmentSelectedMonth, appointmentSelectedDay, "appointment");
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
            case R.id.leadSource:
            case R.id.leadSourceInputLayout:
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                builder.setTitle("Choose a lead source");

                // add a list
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

            // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.appointmentSetDatePicker:
            case R.id.appointmentSetDateDisplay:
            case R.id.appointmentSetDateTitle:
                showDatePickerDialog(setAppointmentSelectedYear, setAppointmentSelectedMonth, setAppointmentSelectedDay, "setAppointment");
                break;
            default:
                break;
        }
    }

    private void popAdminConfirmDialog() {
        String message = "Admin. Do you want to save?";
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setMessage(message)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveClient();
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



    private boolean saveClient(){
        boolean isSaved;
        ClientObject newClient = new ClientObject();
        if(verifyInputFields()) {
            initializeNewClient(newClient);
            isSaved = true;
        } else {
            //don't save
            isSaved = false;
        }
        //check for dates to select status, perhaps this should be done within the select dates...
        return isSaved;
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
                newClient.setLead_type_id((String) key);
                break;
            }
        }

        apiManager.sendAsyncAddClient(this, dataController.getAgent().getAgent_id(), newClient);
    }

    private String getFormattedDate(String incomingDate) {
        String returnString = "";
        Date d;

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

        Calendar calendar = Calendar.getInstance();
        try {
            d = sdf.parse(incomingDate);
            calendar.setTime(d);

            SimpleDateFormat format1 = new SimpleDateFormat("EEE, dd MMM yyyy");

            returnString = format1.format(calendar.getTime()) + " 00:00:00 GMT";

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  returnString;
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

    private void getTime(Date d, Calendar updatedTime, TextView displayView) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");

        try {
            d = sdf.parse(displayView.getText().toString());
            updatedTime.setTime(d);
        } catch (ParseException e) {
//            Toast.makeText(this, "Error parsing selected date", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void activateStatusColor(TextView status) {
        status.setTextColor(colorSchemeManager.getButtonText());
        status.setBackgroundColor(colorSchemeManager.getButtonSelected());
    }

    private void removeStatusColor(TextView status) {
        status.setTextColor(colorSchemeManager.getButtonText());
        status.setBackgroundColor(colorSchemeManager.getButtonBackground());
    }

    public void launchContactPicker() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchContactPicker();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_CONTACT:
                    Cursor cursor = null;
                    String email = "";
                    String phone = "";
                    String firstName = "";
                    String lastName = "";
                    try {
                        Uri result = data.getData();
                        Log.v("TEST", "Got a contact result: "
                                + result.toString());

                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();

                        cursor = parentActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id },
                                null);

                        int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                        if(cursor.moveToFirst()) {
                            phone = cursor.getString(phoneIdx);
                            Log.v("TEST", "Got phone: " + phone);
                            Log.v("TEST", "Got phone: " + cursor.getCount());
                        } else {
                            Log.w("TEST", "No results");
                        }

                        int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                        if(cursor.moveToFirst()) {
                            String name = cursor.getString(nameIdx);
                            Log.v("TEST", "Got name: " + name);
                            Log.v("TEST", "Got name: " + cursor.getCount());
                            String[] nameList = name.split(" ");
                            firstName = nameList[0];
                            lastName = nameList[1];
                        } else {
                            Log.w("TEST", "No results");
                        }

                        // query for everything email
                        cursor = parentActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[] { id },
                                null);



                        int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                        // let's just get the first email
                        if (cursor.moveToFirst()) {
                            email = cursor.getString(emailIdx);

                            Log.v("TEST", "Got email: " + email);
                            Log.v("TEST", "Got email: " + cursor.getCount());
                        } else {
                            Log.w("TEST", "No results");
                        }


                        firstNameText.setText(firstName);
                        lastNameText.setText(lastName);
                        emailText.setText(email);
                        phoneText.setText(PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry()));
                    } catch (Exception e) {
                        Log.e("TEST", "Failed to get data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }

                    }
                    break;
            }

        } else {
            Log.w("TEST", "Warning: parentActivity result not ok");
        }
    }

    private void initializeButtons(){

//        buyerButton= getView().findViewById(R.id.buyerButton);
//        buyerButton.setOnClickListener(this);
//
//        sellerButton= getView().findViewById(R.id.sellerButton);
//        sellerButton.setOnClickListener(this);

        importContactButton = getView().findViewById(R.id.importContactButton);
        importContactButton.setOnClickListener(this);

//        signedClear = getView().findViewById(R.id.signedDateButton);
//        signedClear.setOnClickListener(this);
//        contractClear = getView().findViewById(R.id.underContractDateButton);
//        contractClear.setOnClickListener(this);
//        settlementClear = getView().findViewById(R.id.settlementDateButton);
//        settlementClear.setOnClickListener(this);
//        appointmentClear = getView().findViewById(R.id.appointmentDateButton);
//        appointmentClear.setOnClickListener(this);
//        setAppointmentClear = getView().findViewById(R.id.appointmentSetDateButton);
//        setAppointmentClear.setOnClickListener(this);
//        addAppointmentButton = getView().findViewById(R.id.addAppointmentDateButton);
//        addAppointmentButton.setOnClickListener(this);
//        calculateGciPercent = getView().findViewById(R.id.calculateGciPercent);
//        calculateGciPercent.setOnClickListener(this);
//        calculateIncomePercent = getView().findViewById(R.id.calculateIncomePercent);
//        calculateIncomePercent.setOnClickListener(this);
        prioritySwitch = getView().findViewById(R.id.prioritySwitch);

    }

    private void showDatePickerDialog(final int selectedYear, final int selectedMonth, final int selectedDay, final String calendarCaller) {
        DatePickerDialog dialog = new DatePickerDialog(parentActivity, android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                updateDisplayDate(year, month, day, calendarCaller);
            }
        }, selectedYear, selectedMonth, selectedDay);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
        }
        updateStatus();
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
//            Toast.makeText(this, "Error parsing selected date", Toast.LENGTH_SHORT).show();
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
                setAppointmentSelectedYear = year;
                setAppointmentSelectedMonth = month - 1;
                setAppointmentSelectedDay = day;
                setAppointmentDisplay.setText(sdf.format(updatedTime.getTime()));
                updateStatus();
                break;
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.CREATE_CLIENT) {
            parentActivity.runOnUiThread(() -> {
                parentActivity.showToast("Client Saved");
                navigationManager.onBackPressed();

            });
        }
        else if(returnType == ApiReturnTypes.GET_LEAD_SOURCES) {
            AsyncLeadSourcesJsonObject leadSourcesObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncLeadSourcesJsonObject.class);
            leadSources = leadSourcesObject.getLead_sources();
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus) {
            hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}



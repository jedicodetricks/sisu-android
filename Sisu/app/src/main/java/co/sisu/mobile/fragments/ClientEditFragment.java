package co.sisu.mobile.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncUpdateClients;
import co.sisu.mobile.models.ClientObject;

public class ClientEditFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AsyncServerEventListener, View.OnFocusChangeListener {

    ParentActivity parentActivity;
    ProgressBar loader;
    ClientObject currentClient;
    private EditText firstNameText, lastNameText, emailText, phoneText, transAmount, paidIncome, gci;
    private TextView signedDisplay, contractDisplay, settlementDisplay, appointmentDisplay;
    private TextView pipelineStatus, signedStatus, underContractStatus, closedStatus, archivedStatus, buyer, seller, saveButton;
    Button signedClear, contractClear, settlementClear, appointmentClear, exportContact, deleteButton;
    int signedSelectedYear, signedSelectedMonth, signedSelectedDay;
    int contractSelectedYear, contractSelectedMonth, contractSelectedDay;
    int settlementSelectedYear, settlementSelectedMonth, settlementSelectedDay;
    int appointmentSelectedYear, appointmentSelectedMonth, appointmentSelectedDay;
    String typeSelected;
    String statusList = "pipeline";
    int counter;

    public ClientEditFragment() {
        // Required empty public constructor
    }

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
        loader = view.findViewById(R.id.clientLoader);
        parentActivity = (ParentActivity) getActivity();
        counter = 1;
        currentClient = parentActivity.getSelectedClient();
        view.clearFocus();
//        loader.setVisibility(View.VISIBLE);
        initializeForm();
        initializeButtons();
        initializeCalendar();
        initializeClient();
        loader.setVisibility(View.GONE);
    }

    private void initializeClient() {
        typeSelected = currentClient.getType_id();

        firstNameText.setText(currentClient.getFirst_name());
        lastNameText.setText(currentClient.getLast_name());
        transAmount.setText(currentClient.getTrans_amt());
        paidIncome.setText(currentClient.getCommission_amt());
        gci.setText(currentClient.getGross_commission_amt());
        if(currentClient.getMobile_phone() != null){
            phoneText.setText(PhoneNumberUtils.formatNumber(currentClient.getMobile_phone(), Locale.getDefault().getCountry()));
        } else if (currentClient.getHome_phone() != null){
            phoneText.setText(PhoneNumberUtils.formatNumber(currentClient.getHome_phone(), Locale.getDefault().getCountry()));
        }
        emailText.setText(currentClient.getEmail());

        String formattedApptDt = getFormattedDateFromApiReturn(currentClient.getAppt_dt());
        String formattedSignedDt = getFormattedDateFromApiReturn(currentClient.getSigned_dt());
        String formattedContractDt = getFormattedDateFromApiReturn(currentClient.getUc_dt());
        String formattedClosedDt = getFormattedDateFromApiReturn(currentClient.getClosed_dt());

        appointmentDisplay.setText(formattedApptDt);
        signedDisplay.setText(formattedSignedDt);
        contractDisplay.setText(formattedContractDt);
        settlementDisplay.setText(formattedClosedDt);
        updateStatus();
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
        currentClient.setCommission_amt(paidIncome.getText().toString());

        //These need to be checked for null
        currentClient.setGross_commission_amt(gci.getText().toString().equals("") ? null : gci.getText().toString());
        currentClient.setMobile_phone(phoneText.getText().toString().equals("") ? null : phoneText.getText().toString());
        currentClient.setEmail(emailText.getText().toString().equals("") ? null : emailText.getText().toString());
        currentClient.setAppt_dt(null);
        currentClient.setSigned_dt(null);
        currentClient.setUc_dt(null);
        currentClient.setClosed_dt(null);
        currentClient.setType_id(typeSelected);

        if(!appointmentDisplay.getText().toString().equals("")) {
            currentClient.setAppt_dt(getFormattedDate(appointmentDisplay.getText().toString()));
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
        if(deleteClient) {
            currentClient.setStatus("D");
            statusList = "archived";
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

        return  returnString;
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

    private void initializeCalendar() {
        getView().findViewById(R.id.signedDatePicker).setOnClickListener(this);
        signedDisplay = getView().findViewById(R.id.signedDateDisplay);
        signedDisplay.setOnClickListener(this);
        getView().findViewById(R.id.signedDateTitle).setOnClickListener(this);

        getView().findViewById(R.id.underContractDatePicker).setOnClickListener(this);
        contractDisplay = getView().findViewById(R.id.underContractDateDisplay);
        contractDisplay.setOnClickListener(this);
        getView().findViewById(R.id.underContractDateTitle).setOnClickListener(this);

        getView().findViewById(R.id.settlementDatePicker).setOnClickListener(this);
        settlementDisplay = getView().findViewById(R.id.settlementDateDisplay);
        settlementDisplay.setOnClickListener(this);
        getView().findViewById(R.id.settlementDateTitle).setOnClickListener(this);

        getView().findViewById(R.id.appointmentDatePicker).setOnClickListener(this);
        appointmentDisplay = getView().findViewById(R.id.appointmentDateDisplay);
        appointmentDisplay.setOnClickListener(this);
        getView().findViewById(R.id.appointmentDateTitle).setOnClickListener(this);

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
    }

    private void initializeForm() {
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
        paidIncome = getView().findViewById(R.id.editPaidIncome);
        paidIncome.setOnFocusChangeListener(this);
        gci = getView().findViewById(R.id.editGci);
        gci.setOnFocusChangeListener(this);
        pipelineStatus = getView().findViewById(R.id.pipelineButton);
        signedStatus = getView().findViewById(R.id.signedButton);
        underContractStatus = getView().findViewById(R.id.contractButton);
        closedStatus = getView().findViewById(R.id.closedButton);
        archivedStatus = getView().findViewById(R.id.archivedButton);
        buyer = getView().findViewById(R.id.buyerButton);
        seller = getView().findViewById(R.id.sellerButton);
        exportContact = getView().findViewById(R.id.exportContactButton);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private boolean verifyInputFields() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buyerButton:
                buyer.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
                buyer.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorLightGrey));
                seller.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
                seller.setTextColor(ContextCompat.getColor(parentActivity,R.color.colorLightGrey));
                typeSelected = "b";
                break;
            case R.id.sellerButton:
                buyer.setTextColor(ContextCompat.getColor(parentActivity,R.color.colorLightGrey));
                buyer.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
                seller.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorLightGrey));
                seller.setTextColor(ContextCompat.getColor(parentActivity,R.color.colorCorporateOrange));
                typeSelected = "s";
                break;
            case R.id.saveButton://notify of success update api
                if(verifyInputFields()) {
                    updateCurrentClient(false);
                    saveClient();
                }
//                parentActivity.stackReplaceFragment(ClientListFragment.class);
//                parentActivity.swapToClientListBar();
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
                break;
            case R.id.exportContactButton:
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                String phone = "";
                int phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
                if(currentClient.getMobile_phone() != null) {
                    phone = currentClient.getMobile_phone();
                } else {
                    phone = currentClient.getHome_phone();
                    phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
                }
                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, currentClient.getFirst_name() + " " + currentClient.getLast_name())
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, currentClient.getEmail())
                        .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, phoneType);


                startActivityForResult(contactIntent, 1);
                break;
            case R.id.clientDeleteButton:
                updateCurrentClient(true);
                saveClient();
                break;
            default:
                break;
        }
    }

    private void saveClient(){
        new AsyncUpdateClients(this, currentClient, parentActivity.getJwtObject()).execute();
    }

    private void initializeButtons(){
        signedClear = getView().findViewById(R.id.signedDateButton);
        signedClear.setOnClickListener(this);
        contractClear = getView().findViewById(R.id.underContractDateButton);
        contractClear.setOnClickListener(this);
        settlementClear = getView().findViewById(R.id.settlementDateButton);
        settlementClear.setOnClickListener(this);
        appointmentClear = getView().findViewById(R.id.appointmentDateButton);
        appointmentClear.setOnClickListener(this);
        exportContact.setOnClickListener(this);
        saveButton = parentActivity.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        buyer = parentActivity.findViewById(R.id.buyerButton);
        buyer.setOnClickListener(this);
        seller = parentActivity.findViewById(R.id.sellerButton);
        seller.setOnClickListener(this);
        deleteButton = getView().findViewById(R.id.clientDeleteButton);
        deleteButton.setOnClickListener(this);
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
            }
        }
        if(signedDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, signedDisplay);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                resetAllStatusColors();
                activateStatusColor(signedStatus);
            }
        }
        if(contractDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, contractDisplay);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                resetAllStatusColors();
                activateStatusColor(underContractStatus);
            }
        }
        if(settlementDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, settlementDisplay);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                resetAllStatusColors();
                activateStatusColor(closedStatus);
            }
        }
    }

//    private void updateStatus() {
//        Date d = null;
//        Calendar currentTime = Calendar.getInstance();
//        Calendar updatedTime = Calendar.getInstance();
//
//        if(settlementDisplay.getText().toString().matches(".*\\d+.*")) {
//            getTime(d, updatedTime, settlementDisplay);
//            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
//                resetAllStatusColors();
//                activateStatusColor(closedStatus);
////                removeStatusColor(underContractStatus);
//            }
//            else {
////                removeStatusColor(closedStatus);
//                //resetAllStatusColors();
//            }
//
//        } else if(contractDisplay.getText().toString().matches(".*\\d+.*")) {
//            getTime(d, updatedTime, contractDisplay);
//            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
//                resetAllStatusColors();
//                activateStatusColor(underContractStatus);
////                removeStatusColor(signedStatus);
//            }
//            else {
////                removeStatusColor(underContractStatus);
//                //resetAllStatusColors();
//            }
//
//        } else if(signedDisplay.getText().toString().matches(".*\\d+.*")) {
//            getTime(d, updatedTime, signedDisplay);
//            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
//                resetAllStatusColors();
//                activateStatusColor(signedStatus);
////                removeStatusColor(pipelineStatus);
//            }
//            else {
////                removeStatusColor(signedStatus);
//                //resetAllStatusColors();
//            }
//
//        } else if(appointmentDisplay.getText().toString().matches(".*\\d+.*")){
//            getTime(d, updatedTime, appointmentDisplay);
//            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
//                activateStatusColor(pipelineStatus);
//            }
//            else {
////                removeStatusColor(pipelineStatus);
//                //resetAllStatusColors();
//            }
//        }
//    }

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

    private void activateStatusColor(TextView status) {
        status.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
        status.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorLightGrey));
    }

    private void resetAllStatusColors() {
        pipelineStatus.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorWhite));
        pipelineStatus.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
        underContractStatus.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorWhite));
        underContractStatus.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
        closedStatus.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorWhite));
        closedStatus.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
        signedStatus.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorWhite));
        signedStatus.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
    }
    private void removeStatusColor(TextView status) {
        status.setTextColor(ContextCompat.getColor(parentActivity, R.color.colorWhite));
        status.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
    }

    private void updateDisplayDate(int year, int month, int day, String calendarCaller) {

        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        Log.e("MONTH", String.valueOf(month));
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
        }


    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        //initializeClient();
        loader.setVisibility(View.GONE);
        parentActivity.navigateToClientList(statusList, null);
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.showToast("Client updates saved");
            }
        });
    }

    @Override
    public void onEventFailed(Object o, String s) {

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

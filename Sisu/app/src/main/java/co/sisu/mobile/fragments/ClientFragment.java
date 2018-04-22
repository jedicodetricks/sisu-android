package co.sisu.mobile.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.ClientObject;

public class ClientFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AsyncServerEventListener {

    ParentActivity parentActivity;
    ProgressBar loader;
    ClientObject currentClient;
    private EditText firstNameText, lastNameText, emailText, phoneText, transAmount, paidIncome, gci;
    private TextView signedDisplay, contractDisplay, settlementDisplay, appointmentDisplay;
    private TextView pipelineStatus, signedStatus, underContractStatus, closedStatus, archivedStatus, buyer, seller;
    Button signedClear, contractClear, settlementClear, appointmentClear, exportContact;
    int signedSelectedYear, signedSelectedMonth, signedSelectedDay;
    int contractSelectedYear, contractSelectedMonth, contractSelectedDay;
    int settlementSelectedYear, settlementSelectedMonth, settlementSelectedDay;
    int appointmentSelectedYear, appointmentSelectedMonth, appointmentSelectedDay;
    private List<Integer> statusButtons = new ArrayList<>();

    public ClientFragment() {
        // Required empty public constructor
    }

    private void initializeClient() {
        firstNameText.setText(currentClient.getFirst_name().toString());
        lastNameText.setText(currentClient.getLast_name());
        transAmount.setText(currentClient.getTrans_amt());
        paidIncome.setText(currentClient.getCommission_amt());
        gci.setText(currentClient.getGross_commission_amt());
        if(currentClient.getMobile_phone() != null){
            phoneText.setText(currentClient.getMobile_phone());
        } else {
            phoneText.setText(currentClient.getHome_phone());
        }
        setStatus();
        emailText.setText(currentClient.getEmail());
        appointmentDisplay.setText(currentClient.getAppt_dt());
        signedDisplay.setText(currentClient.getSigned_dt());
        contractDisplay.setText(currentClient.getUc_dt());
        settlementDisplay.setText(currentClient.getClosed_dt());//is this RIGHT?! closed date == settlement date?

    }

    private void setStatus() {
        if(!currentClient.getStatus().equalsIgnoreCase("D")) {
            if(currentClient.getClosed_dt() != null) {
                changeStatusColor(closedStatus);
            } else if(currentClient.getUc_dt() != null) {
                changeStatusColor(underContractStatus);
            } else if(currentClient.getSigned_dt() != null) {
                changeStatusColor(signedStatus);
            } else {
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
        currentClient = parentActivity.getSelectedClient();
        view.clearFocus();
        loader.setVisibility(View.VISIBLE);
        initializeForm();
        initializeButtons();
        initializeCalendar();
        initializeClient();
        loader.setVisibility(View.GONE);
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
        lastNameText = getView().findViewById(R.id.editLastName);
        emailText = getView().findViewById(R.id.editEmail);
        phoneText = getView().findViewById(R.id.editPhone);
        transAmount = getView().findViewById(R.id.editTransAmount);
        paidIncome = getView().findViewById(R.id.editPaidIncome);
        gci = getView().findViewById(R.id.editGci);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                parentActivity.setRecordSaved(false);
                parentActivity.stackReplaceFragment(RecordFragment.class);
                parentActivity.swapToBacktionBar(parentActivity.getResources().getString(R.string.record));
                break;
            case R.id.saveButton://notify of success update api
                parentActivity.setRecordSaved(saveClient());
                parentActivity.stackReplaceFragment(ScoreboardFragment.class);
                parentActivity.swapToBacktionBar(parentActivity.getResources().getString(R.string.scoreboard));
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
                break;
            case R.id.underContractDateButton:
                clearDisplayDate("contract");
                break;
            case R.id.settlementDateButton:
                clearDisplayDate("settlement");
                break;
            case R.id.appointmentDateButton:
                clearDisplayDate("appointment");
                break;
            case R.id.exportContactButton:
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, currentClient.getFirst_name() + " " + currentClient.getLast_name())
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, currentClient.getEmail())
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, "m")
                        .putExtra(ContactsContract.Intents.Insert.PHONE, currentClient.getMobile_phone())
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, "h")
                        .putExtra(ContactsContract.Intents.Insert.PHONE, currentClient.getHome_phone());

                startActivityForResult(contactIntent, 1);
                //add contact to phone
                break;
            default:
                break;
        }
    }
    //TODO do stuff for this
    private boolean saveClient(){
        return true; //return status of api success or failure
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
    }

    private void showDatePickerDialog(final int selectedYear, final int selectedMonth, final int selectedDay, final String calendarCaller) {
        DatePickerDialog dialog = new DatePickerDialog(parentActivity, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
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
                signedDisplay.setText(setText);
                break;
            case "contract":
                contractDisplay.setText(setText);
                break;
            case "settlement":
                settlementDisplay.setText(setText);
                break;
            case "appointment":
                appointmentDisplay.setText(setText);
        }
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
            Toast.makeText(parentActivity, "Error parsing selected date", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        switch (calendarCaller) {
            case "signed":
                signedSelectedYear = year;
                signedSelectedMonth = month;
                signedSelectedDay = day;
                signedDisplay.setText(sdf.format(updatedTime.getTime()));
                break;
            case "contract":
                contractSelectedYear = year;
                contractSelectedMonth = month;
                contractSelectedDay = day;
                contractDisplay.setText(sdf.format(updatedTime.getTime()));
                break;
            case "settlement":
                settlementSelectedYear = year;
                settlementSelectedMonth = month;
                settlementSelectedDay = day;
                settlementDisplay.setText(sdf.format(updatedTime.getTime()));
                break;
            case "appointment":
                appointmentSelectedYear = year;
                appointmentSelectedMonth = month;
                appointmentSelectedDay = day;
                appointmentDisplay.setText(sdf.format(updatedTime.getTime()));
                break;
        }


    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        //initializeClient();
        loader.setVisibility(View.GONE);
//        parentActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                loader.setVisibility(View.GONE);
//                currentClient = parentActivity.getSelectedClient();
//            }
//        });
    }

    @Override
    public void onEventFailed() {

    }
}

package co.sisu.mobile.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncAddClient;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.ClientObject;

/**
 * Created by Brady Groharing on 3/5/2018.
 */

public class AddClientActivity extends AppCompatActivity implements View.OnClickListener, AsyncServerEventListener {

    public final int PICK_CONTACT = 2015;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private EditText firstNameText, lastNameText, emailText, phoneText, transAmount, paidIncome, gci;
    private TextView signedDisplay, contractDisplay, settlementDisplay, appointmentDisplay, pipelineStatus, signedStatus, underContractStatus, closedStatus;
    Button signedClear, contractClear, settlementClear, appointmentClear;
    String typeSelected;
    int signedSelectedYear, signedSelectedMonth, signedSelectedDay;
    int contractSelectedYear, contractSelectedMonth, contractSelectedDay;
    int settlementSelectedYear, settlementSelectedMonth, settlementSelectedDay;
    int appointmentSelectedYear, appointmentSelectedMonth, appointmentSelectedDay;
    AgentModel agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        initializeActionBar();
        getSupportActionBar().setElevation(0);
        agent = getIntent().getParcelableExtra("Agent");
        initializeButtons();
        initializeForm();
        initializeCalendar();
    }

    private void initializeCalendar() {
        findViewById(R.id.signedDatePicker).setOnClickListener(this);
        signedDisplay = findViewById(R.id.signedDateDisplay);
        signedDisplay.setOnClickListener(this);
        findViewById(R.id.signedDateTitle).setOnClickListener(this);

        findViewById(R.id.underContractDatePicker).setOnClickListener(this);
        contractDisplay = findViewById(R.id.underContractDateDisplay);
        contractDisplay.setOnClickListener(this);
        findViewById(R.id.underContractDateTitle).setOnClickListener(this);

        findViewById(R.id.settlementDatePicker).setOnClickListener(this);
        settlementDisplay = findViewById(R.id.settlementDateDisplay);
        settlementDisplay.setOnClickListener(this);
        findViewById(R.id.settlementDateTitle).setOnClickListener(this);

        findViewById(R.id.appointmentDatePicker).setOnClickListener(this);
        appointmentDisplay = findViewById(R.id.appointmentDateDisplay);
        appointmentDisplay.setOnClickListener(this);
        findViewById(R.id.appointmentDateTitle).setOnClickListener(this);

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
        typeSelected = "";
        firstNameText = findViewById(R.id.editFirstName);
        lastNameText = findViewById(R.id.addClientEditLastName);
        emailText = findViewById(R.id.editEmail);
        phoneText = findViewById(R.id.editPhone);
        transAmount = findViewById(R.id.editTransAmount);
        paidIncome = findViewById(R.id.editPaidIncome);
        gci = findViewById(R.id.editGci);
        pipelineStatus = findViewById(R.id.pipelineButton);
        signedStatus = findViewById(R.id.signedButton);
        underContractStatus = findViewById(R.id.contractButton);
        closedStatus = findViewById(R.id.closedButton);
    }

    @Override
    public void onClick(View v) {
        Button buyerButton = (Button) findViewById(R.id.buyerButton);
        Button sellerButton= (Button) findViewById(R.id.sellerButton);
        switch (v.getId()) {
            case R.id.cancelButton:
                onBackPressed();
                break;
            case R.id.saveButton:
                if(saveClient()){
                    //do save in api call to add new client
                    //animation of confirmation
                    onBackPressed();
                }
                break;
            case R.id.buyerButton:
                buyerButton.setTextColor(ContextCompat.getColor(this, R.color.colorCorporateOrange));
                buyerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGrey));
                sellerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
                sellerButton.setTextColor(ContextCompat.getColor(this,R.color.colorLightGrey));
                typeSelected = "b";
                break;
            case R.id.sellerButton:
                buyerButton.setTextColor(ContextCompat.getColor(this,R.color.colorLightGrey));
                buyerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
                sellerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGrey));
                sellerButton.setTextColor(ContextCompat.getColor(this,R.color.colorCorporateOrange));
                typeSelected = "s";
                break;
            case R.id.importContactButton:
                //do stuff for import
                if (ContextCompat.checkSelfPermission(AddClientActivity.this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddClientActivity.this,
                            Manifest.permission.READ_CONTACTS)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(AddClientActivity.this,
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
            default:
                break;
        }
    }

    //TODO do stuff for this
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
        if(typeSelected.equals("")) {
            Toast.makeText(this, "Buyer or Seller is required", Toast.LENGTH_SHORT).show();
            isVerified = false;
        }
        else if(firstNameText.getText().toString().equals("")) {
            Toast.makeText(this, "First Name is required", Toast.LENGTH_SHORT).show();
            isVerified = false;
        }
        else if(lastNameText.getText().toString().equals("")) {
            Toast.makeText(this, "Last Name is required", Toast.LENGTH_SHORT).show();
            isVerified = false;
        }
        else if(transAmount.getText().toString().equals("")) {
            Toast.makeText(this, "Transaction Amount is required", Toast.LENGTH_SHORT).show();
            isVerified = false;
        }
        else if(paidIncome.getText().toString().equals("")) {
            Toast.makeText(this, "Paid Income is required", Toast.LENGTH_SHORT).show();
            isVerified = false;
        }
        return isVerified;
    }

    private void initializeNewClient(ClientObject newClient) {
        //These can never be null
        newClient.setFirst_name(firstNameText.getText().toString());
        newClient.setLast_name(lastNameText.getText().toString());
        newClient.setTrans_amt(transAmount.getText().toString());
        newClient.setCommission_amt(paidIncome.getText().toString());

        //These need to be checked for null
        newClient.setGross_commission_amt(gci.getText().toString().equals("") ? null : gci.getText().toString());
        newClient.setMobile_phone(phoneText.getText().toString().equals("") ? null : phoneText.getText().toString());
        newClient.setEmail(emailText.getText().toString().equals("") ? null : emailText.getText().toString());
        newClient.setAppt_dt(null);
        newClient.setSigned_dt(null);
        newClient.setUc_dt(null);
        newClient.setClosed_dt(null);
        newClient.setType_id(typeSelected);

        if(!appointmentDisplay.getText().equals("")) {
            newClient.setAppt_dt(getFormattedDate(appointmentDisplay.getText().toString()));
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

        new AsyncAddClient(this, agent.getAgent_id(), newClient).execute();
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

        if(settlementDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, settlementDisplay);
            if(updatedTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                activateStatusColor(closedStatus);
                removeStatusColor(underContractStatus);
            }
            else {
                removeStatusColor(closedStatus);
            }
        } else if(contractDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, contractDisplay);
            if(updatedTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                activateStatusColor(underContractStatus);
                removeStatusColor(signedStatus);
            }
            else {
                removeStatusColor(underContractStatus);
            }
        } else if(signedDisplay.getText().toString().matches(".*\\d+.*")) {
            getTime(d, updatedTime, signedDisplay);
            if(updatedTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                activateStatusColor(signedStatus);
                removeStatusColor(pipelineStatus);
            }
            else {
                removeStatusColor(signedStatus);
            }
        } else if(appointmentDisplay.getText().toString().matches(".*\\d+.*")){
            getTime(d, updatedTime, appointmentDisplay);
            if(updatedTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                activateStatusColor(pipelineStatus);
            }
            else {
                removeStatusColor(pipelineStatus);
            }
        }
    }

    private void getTime(Date d, Calendar updatedTime, TextView displayView) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");

        try {
            d = sdf.parse(displayView.getText().toString());
            updatedTime.setTime(d);
        } catch (ParseException e) {
            Toast.makeText(this, "Error parsing selected date", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void activateStatusColor(TextView status) {
        status.setTextColor(ContextCompat.getColor(this, R.color.colorCorporateOrange));
        status.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGrey));
    }

    private void removeStatusColor(TextView status) {
        status.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        status.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
    }

    private void initializeActionBar() {
        getSupportActionBar().setCustomView(R.layout.action_bar_add_client_layout);

        TextView cancelButton = findViewById(R.id.cancelButton);
        TextView saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
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
                        phoneText.setText(phone);
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
            Log.w("TEST", "Warning: activity result not ok");
        }
    }

    private void initializeButtons(){

        Button buyerButton= findViewById(R.id.buyerButton);
        buyerButton.setOnClickListener(this);

        Button sellerButton= findViewById(R.id.sellerButton);
        sellerButton.setOnClickListener(this);

        Button importContactButton = findViewById(R.id.importContactButton);
        importContactButton.setOnClickListener(this);

        signedClear = findViewById(R.id.signedDateButton);
        signedClear.setOnClickListener(this);
        contractClear = findViewById(R.id.underContractDateButton);
        contractClear.setOnClickListener(this);
        settlementClear = findViewById(R.id.settlementDateButton);
        settlementClear.setOnClickListener(this);
        appointmentClear = findViewById(R.id.appointmentDateButton);
        appointmentClear.setOnClickListener(this);
    }

    private void showDatePickerDialog(final int selectedYear, final int selectedMonth, final int selectedDay, final String calendarCaller) {
        DatePickerDialog dialog = new DatePickerDialog(AddClientActivity.this, android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
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
            Toast.makeText(AddClientActivity.this, "Error parsing selected date", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onEventFailed(Object o, String s) {

    }
}



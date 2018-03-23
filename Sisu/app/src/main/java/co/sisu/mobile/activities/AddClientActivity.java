package co.sisu.mobile.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.Iterator;
import java.util.Set;

import co.sisu.mobile.R;

/**
 * Created by Brady Groharing on 3/5/2018.
 */

public class AddClientActivity extends AppCompatActivity implements View.OnClickListener {

    public final int PICK_CONTACT = 2015;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private EditText nameText, emailText, phoneText;
    private TextView signedDisplay, contractDisplay, settlementDisplay;
    Button signedClear, contractClear, settlementClear;
    int signedSelectedYear, signedSelectedMonth, signedSelectedDay;
    int contractSelectedYear, contractSelectedMonth, contractSelectedDay;
    int settlementSelectedYear, settlementSelectedMonth, settlementSelectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
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

        signedSelectedYear = Calendar.getInstance().get(Calendar.YEAR);
        signedSelectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        signedSelectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        contractSelectedYear = Calendar.getInstance().get(Calendar.YEAR);
        contractSelectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        contractSelectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        settlementSelectedYear = Calendar.getInstance().get(Calendar.YEAR);
        settlementSelectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        settlementSelectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    private void initializeForm() {
        nameText = findViewById(R.id.editName);
        emailText = findViewById(R.id.editEmail);
        phoneText = findViewById(R.id.editPhone);
    }

    @Override
    public void onClick(View v) {
        Button buyerButton = (Button) findViewById(R.id.buyerButton);
        Button sellerButton= (Button) findViewById(R.id.sellerButton);
        switch (v.getId()) {
            case R.id.buyerButton:
                buyerButton.setTextColor(ContextCompat.getColor(this, R.color.colorCorporateOrange));
                //buyerButton.setBackgroundColor(ContextCompat.getColor(this,R.color.colorCorporateOrange));
                sellerButton.setTextColor(ContextCompat.getColor(this,R.color.colorLightGrey));
                break;
            case R.id.sellerButton:
                buyerButton.setTextColor(ContextCompat.getColor(this,R.color.colorLightGrey));
                sellerButton.setTextColor(ContextCompat.getColor(this,R.color.colorCorporateOrange));
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
            case R.id.signedDateButton:
                clearDisplayDate("signed");
                break;
            case R.id.underContractDateButton:
                clearDisplayDate("contract");
                break;
            case R.id.settlementDateButton:
                clearDisplayDate("settlement");
                break;
            default:
                break;
        }
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
                    String name = "";
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
                            name = cursor.getString(nameIdx);
                            Log.v("TEST", "Got name: " + name);
                            Log.v("TEST", "Got name: " + cursor.getCount());
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


                        nameText.setText(name);
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


    }

    private void showDatePickerDialog(final int selectedYear, final int selectedMonth, final int selectedDay, final String calendarCaller) {
        DatePickerDialog dialog = new DatePickerDialog(AddClientActivity.this, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                updateDisplayDate(year, month, day, calendarCaller);
            }
        }, selectedYear, selectedMonth, selectedDay);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void clearDisplayDate(String calendarCaller) {

        switch (calendarCaller) {
            case "signed":
                signedDisplay.setText("Tap To Select");
                break;
            case "contract":
                contractDisplay.setText("Tap To Select");
                break;
            case "settlement":
                settlementDisplay.setText("Tap To Select");
                break;
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
            Toast.makeText(AddClientActivity.this, "Error parsing selected date", Toast.LENGTH_SHORT).show();
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
        }


    }
}



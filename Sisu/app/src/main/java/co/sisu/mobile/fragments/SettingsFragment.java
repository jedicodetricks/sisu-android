package co.sisu.mobile.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.MainActivity;
import co.sisu.mobile.controllers.NotificationReceiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    Switch notificationSwitch, reminderSwitch, lightsSwitch, idSwitch;
    ImageView timeSelector;
    TextView reminderTimeTitle, displayTime;
    int currentSelectedHour, currentSelectedMinute;
    int alarmId = 1412;
    String selectedPeriod;

    private PendingIntent pendingIntent;

    public SettingsFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_settings, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initSwitches();
        initTimeSelector();
        initNotificationAlarm();
    }

    private void initNotificationAlarm() {
        Intent myIntent = new Intent(getContext(), NotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, myIntent, 0);
    }

    private void initTimeSelector() {
        timeSelector = getView().findViewById(R.id.timeButton);
        timeSelector.setOnClickListener(this);
        reminderTimeTitle = getView().findViewById(R.id.timeLabel);
        reminderTimeTitle.setOnClickListener(this);
        displayTime = getView().findViewById(R.id.timeDisplay);
        displayTime.setOnClickListener(this);

        Calendar mcurrentTime = Calendar.getInstance();
        currentSelectedHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        currentSelectedMinute = mcurrentTime.get(Calendar.MINUTE);
    }

    private void initSwitches() {
        notificationSwitch = getView().findViewById(R.id.notificationSwitch);
        notificationSwitch.setOnCheckedChangeListener(this);
        reminderSwitch = getView().findViewById(R.id.reminderSwitch);
        reminderSwitch.setOnCheckedChangeListener(this);
        lightsSwitch = getView().findViewById(R.id.lightsSwitch);
        lightsSwitch.setOnCheckedChangeListener(this);
        idSwitch = getView().findViewById(R.id.idSwitch);
        idSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.notificationSwitch:
                Log.d("CHECK LISTENER", "Notify");
                break;
            case R.id.reminderSwitch:
                if(!reminderSwitch.isChecked()) {
                    AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);
                }
                Log.d("CHECK LISTENER", "REMINDER");
                break;
            case R.id.lightsSwitch:
                Log.d("CHECK LISTENER", "LIGHTS");
                break;
            case R.id.idSwitch:
                Log.d("CHECK LISTENER", "ID");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeButton:
            case R.id.timeDisplay:
            case R.id.timeLabel:
                Log.d("CHECK LISTENER", "Launch Time");
                if(reminderSwitch.isChecked()) {
                    launchTimePicker();
                }

        }
    }

    private void launchTimePicker() {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                currentSelectedHour = selectedHour;
                currentSelectedMinute = selectedMinute;
                selectedPeriod = "AM";
                String timePrepend = "";
                if(selectedHour > 12) {
                    selectedPeriod = "PM";
                    selectedHour = selectedHour - 12;
                }

                if(currentSelectedMinute < 10) {
                    timePrepend = "0";
                }
                displayTime.setText( "" + selectedHour + ":" + timePrepend + selectedMinute + " " + selectedPeriod);
                createNotificationAlarm();
            }
        }, currentSelectedHour, currentSelectedMinute, false);

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void createNotificationAlarm() {
        Calendar calendar = Calendar.getInstance();
        int interval = 1000 * 60 * 60 * 24; // One day

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, currentSelectedMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, currentSelectedHour);

//        if(selectedPeriod.equals("AM")) {
//            calendar.set(Calendar.AM_PM,Calendar.AM);
//        }
//        else {
//            calendar.set(Calendar.AM_PM,Calendar.PM);
//            calendar.set(Calendar.HOUR_OF_DAY, currentSelectedHour);
//        }
        Log.d("CALENDAR SET", calendar.getTime().toString());
        Log.d("CALENDAR CURRENT TIME", Calendar.getInstance().getTime().toString());

        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
    }
}

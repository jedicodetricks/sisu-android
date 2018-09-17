package co.sisu.mobile.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import co.sisu.mobile.BuildConfig;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.controllers.NotificationReceiver;
import co.sisu.mobile.models.AsyncTeamColorSchemeObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.TeamColorSchemeObject;
import co.sisu.mobile.models.UpdateSettingsObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AsyncServerEventListener {

    private Switch notificationSwitch, reminderSwitch, lightsSwitch, idSwitch;
    private ImageView timeSelector;
    private TextView reminderTimeTitle, displayTime, version, timeZoneDisplay, reminderLabel, lightsLabel, timeZoneTitle;
    private int currentSelectedHour, currentSelectedMinute;
    private int alarmId = 1412;
    private String selectedPeriod;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private PendingIntent pendingIntent;
    private List<ParameterObject> settings;
    private boolean settingsFinished = false;
    private boolean colorFinished = false;
    private TeamColorSchemeObject[] colorScheme;

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
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        navigationManager = parentActivity.getNavigationManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        initAdditionalFields();
        initTimeSelector();
        initNotificationAlarm();
        initSwitches();
        TextView b = parentActivity.findViewById(R.id.saveButton);
        if(b != null) {
            b.setOnClickListener(this);
        }

        setColorScheme();
    }

    private void setColorScheme() {

        reminderTimeTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        displayTime.setTextColor(colorSchemeManager.getDarkerTextColor());
        version.setTextColor(colorSchemeManager.getDarkerTextColor());
        timeZoneDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());
        reminderLabel.setTextColor(colorSchemeManager.getDarkerTextColor());
        lightsLabel.setTextColor(colorSchemeManager.getDarkerTextColor());
        timeZoneTitle.setTextColor(colorSchemeManager.getDarkerTextColor());

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

        DrawableCompat.setTintList(DrawableCompat.wrap(reminderSwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(reminderSwitch.getTrackDrawable()), new ColorStateList(states, trackColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(lightsSwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(lightsSwitch.getTrackDrawable()), new ColorStateList(states, trackColors));
    }


    private void initAdditionalFields() {
        timeZoneDisplay = getView().findViewById(R.id.timeZoneDisplay);
        version = getView().findViewById(R.id.versionLabel);
        version.setText("Version: " + BuildConfig.VERSION_NAME + " | Build: " + BuildConfig.VERSION_CODE);
        reminderLabel = getView().findViewById(R.id.reminderLabel);
        lightsLabel = getView().findViewById(R.id.lightsLabel);
        timeZoneTitle = getView().findViewById(R.id.timeZoneTitle);
    }

    private void fillFieldsWithData() {
        settings = dataController.getSettings();

        for (ParameterObject s : settings) {
            Log.e(s.getName(), s.getValue());
            switch (s.getName()) {
                case "local_timezone":
                    if(s.getValue().equals("")) {
                        timeZoneDisplay.setText(TimeZone.getDefault().getID().toString());
                    }
                    else {
                        timeZoneDisplay.setText(s.getValue());
                    }
                    break;
                case "daily_reminder_time":
                        displayTime.setText(formatTimeFrom24H(s.getValue()));
                    break;
                //Keep these, we'll need them for V2
                case "lights":
                    lightsSwitch.setChecked(isChecked(s));
                    break;
//                case "biometrics":
//                    idSwitch.setChecked(isChecked(s));
//                    break;
                case "daily_reminder":
                    reminderSwitch.setChecked(isChecked(s));
                    break;
            }
        }
    }

    private boolean isChecked(ParameterObject s) {
        if(s.getValue().equals("0")) {
            return false;
        }
        return true;
    }

    private String isCheckedBinaryValue(ParameterObject s) {
        if(s.getValue().equals("0")) {
            return "1";
        }
        return "0";
    }

    private String isCheckedBinaryValue(boolean isChecked) {
        if(isChecked) {
            return "1";
        }
        return "0";
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
        //Keep these, we'll need them for V2

        reminderSwitch = getView().findViewById(R.id.reminderSwitch);
        lightsSwitch = getView().findViewById(R.id.lightsSwitch);
//        idSwitch = getView().findViewById(R.id.idSwitch);

        fillFieldsWithData();

        //idSwitch.setOnCheckedChangeListener(this);
        reminderSwitch.setOnCheckedChangeListener(this);
        lightsSwitch.setOnCheckedChangeListener(this);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.reminderSwitch:
                if(!reminderSwitch.isChecked()) {
                    Log.e("CANCELING", "ALARM");
                    AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);
                }
                for(ParameterObject so : settings) {
                    if(so.getName().equals("daily_reminder")) {
                        so.setValue(isCheckedBinaryValue(so));
                    }
                }
                break;
            case R.id.lightsSwitch:
                activateLights(isChecked);
                for(ParameterObject so : settings) {
                    if(so.getName().equalsIgnoreCase("lights")) {
                        so.setValue(isCheckedBinaryValue(so));
                    }
                }

                break;
            //Keep this, we'll need it for V2
//            case R.id.idSwitch:
//                for(ParameterObject so : settings) {
//                    if(so.getName().equals("biometrics")) {
//                        so.setValue(isCheckedBinaryValue(so));
//                    }
//                }
//                Log.d("CHECK LISTENER", "ID");
//                break;
        }


    }

    private void activateLights(Boolean isChecked) {
        String lightsOn = "0";
        if(isChecked) {
            lightsOn = "1";
        }
        apiManager.getColorScheme(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), lightsOn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeButton:
            case R.id.timeDisplay:
            case R.id.timeLabel:
                if(reminderSwitch.isChecked()) {
                    launchTimePicker();
                }
                break;
            case R.id.saveButton:
                updateSettingsObject();
                saveSettingsObject();
        }
    }

    private void updateSettingsObject() {
        for (ParameterObject s : settings) {
            switch (s.getName()) {
                case "local_timezone":
                    s.setValue(timeZoneDisplay.getText().toString());
                    break;
                case "daily_reminder_time":
                    s.setValue(formatTimeTo24H(displayTime.getText().toString()));
                    break;
                case "lights":
                    lightsSwitch.setChecked(isChecked(s));
                    break;
                //Keep this, we'll need them for V2
//                case "biometrics":
//                    idSwitch.setChecked(isChecked(s));
//                    break;
                case "daily_reminder":
                    s.setValue(isCheckedBinaryValue(reminderSwitch.isChecked()));
                    break;
            }
        }
    }

    private String formatTimeTo24H(String displayTime) {
        String[] timeSplit = displayTime.split(" ");
        String militaryTime = "";
        if(timeSplit[1].equals("AM")) {
            militaryTime = timeSplit[0];
        }
        else if(timeSplit[1].equals("PM")) {
            String[] milTimeSplit = timeSplit[0].split(":");
            int hour = Integer.parseInt(milTimeSplit[0]) + 12;
            militaryTime = String.valueOf(hour) + ":" + milTimeSplit[1];
        }

        return militaryTime;
    }

    private String formatTimeFrom24H(String displayTime) {
        String standardTime = "";
        if(!displayTime.equals("")) {
            String timePeriod = "AM";
            String[] milTimeSplit = displayTime.split(":");
            int hour = Integer.parseInt(milTimeSplit[0]);
            if(hour > 11) {
                timePeriod = "PM";
                hour -= 12;
            }


            standardTime = String.valueOf(hour) + ":" + milTimeSplit[1] + " " + timePeriod;
        }


        return standardTime;
    }

    private void saveSettingsObject() {
        List<UpdateSettingsObject> settingsObjects = new ArrayList<>();

        for(ParameterObject so : settings) {
            settingsObjects.add(new UpdateSettingsObject(so.getName(), so.getValue(), Integer.valueOf(so.getParameter_type_id())));
        }

        AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject = new AsyncUpdateSettingsJsonObject(2, Integer.valueOf(dataController.getAgent().getAgent_id()), settingsObjects);
        apiManager.sendAsyncUpdateSettings(this, dataController.getAgent().getAgent_id(), asyncUpdateSettingsJsonObject);

        parentActivity.createNotificationAlarm(currentSelectedHour,currentSelectedMinute,pendingIntent);

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
                if(selectedHour > 11) {
                    selectedPeriod = "PM";
                    if(selectedHour > 12) {
                        selectedHour = selectedHour - 12;
                    }
                } else if(selectedHour == 0) {
                    selectedHour = 12;
                }

                if(currentSelectedMinute < 10) {
                    timePrepend = "0";
                }
                displayTime.setText( "" + selectedHour + ":" + timePrepend + selectedMinute + " " + selectedPeriod);
//                createNotificationAlarm();
            }
        }, currentSelectedHour, currentSelectedMinute, false);

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

//    private void createNotificationAlarm() {
//        Calendar calendar = Calendar.getInstance();
//        int interval = 1000 * 60 * 60 * 24; // One day
//
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.MINUTE, currentSelectedMinute);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.HOUR_OF_DAY, currentSelectedHour);
//
//        Log.e("CALENDAR SET", calendar.getTime().toString());
//        Log.e("CALENDAR CURRENT TIME", Calendar.getInstance().getTime().toString());
//
//        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
//    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Update Settings")) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ParameterObject[] array = new ParameterObject[settings.size()];
                    dataController.setSettings(settings.toArray(array));
                    colorSchemeManager.setColorScheme(colorScheme);
                    parentActivity.setActivityColors();
                    setColorScheme();
                    if(colorFinished) {
                        navigationManager.onBackPressed();
                        parentActivity.showToast("Your settings have been updated");
                    }
                    settingsFinished = true;
                }
            });
        }
        else if(asyncReturnType.equals("Get Color Scheme")) {
            AsyncTeamColorSchemeObject colorJson = (AsyncTeamColorSchemeObject) returnObject;
            colorScheme = colorJson.getTheme();
            if(settingsFinished) {
                parentActivity.showToast("Your settings have been updated");
            }
            colorFinished = true;
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }
}

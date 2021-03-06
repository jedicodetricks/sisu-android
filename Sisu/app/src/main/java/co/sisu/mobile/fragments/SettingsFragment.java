package co.sisu.mobile.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

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
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.AsyncTeamColorSchemeObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.TeamColorSchemeObject;
import co.sisu.mobile.models.UpdateSettingsObject;
import co.sisu.mobile.system.SaveSharedPreference;
import co.sisu.mobile.utils.Utils;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AsyncServerEventListener {

    private Switch notificationSwitch, reminderSwitch, lightsSwitch, idSwitch;
    private ImageView timeSelector, settingsLogo, sisuPowerLogo;
    private TextView reminderTimeTitle, displayTime, version, timeZoneDisplay, reminderLabel, lightsLabel, timeZoneTitle;
    private int currentSelectedHour, currentSelectedMinute;
    private int alarmId = 1412;
    private String selectedPeriod;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private Utils utils;
    private PendingIntent pendingIntent;
    private List<ParameterObject> settings;
    private boolean settingsFinished = false;
    private boolean colorFinished = false;
    private boolean colorSchemeChanged = false;
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
        // TODO: This needs to save on any change now, there is no save button (I think I did this and didn't delete the todo)
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        navigationManager = parentActivity.getNavigationManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        utils = parentActivity.getUtils();
        apiManager.getSettings(this, dataController.getAgent().getAgent_id());
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

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "SettingsFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ParentActivity");
        FirebaseAnalytics.getInstance(parentActivity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.settingsFragment);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());

        reminderTimeTitle.setTextColor(colorSchemeManager.getDarkerText());
        displayTime.setTextColor(colorSchemeManager.getDarkerText());
        version.setTextColor(colorSchemeManager.getDarkerText());
        timeZoneDisplay.setTextColor(colorSchemeManager.getDarkerText());
        reminderLabel.setTextColor(colorSchemeManager.getDarkerText());
        lightsLabel.setTextColor(colorSchemeManager.getDarkerText());
        timeZoneTitle.setTextColor(colorSchemeManager.getDarkerText());
        if(colorSchemeManager.getLogo() != null && !colorSchemeManager.getLogo().equals("sisu-logo-lg")) {
            //Picasso.with(parentActivity).load(Uri.parse("https://s3-us-west-2.amazonaws.com/sisu-shared-storage/team_logo/Better_Homes_and_Gardens_Real_Estate_Logo.jpg")).into(settingsLogo);
            Picasso.with(parentActivity).load(Uri.parse(colorSchemeManager.getLogo())).into(settingsLogo);
            SaveSharedPreference.setLogo(parentActivity, colorSchemeManager.getLogo());
            sisuPowerLogo.setVisibility(View.VISIBLE);
        }

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getMenuIcon()
        };

        int[] trackColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getMenuIcon()
        };

        DrawableCompat.setTintList(DrawableCompat.wrap(reminderSwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(reminderSwitch.getTrackDrawable()), new ColorStateList(states, trackColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(lightsSwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(lightsSwitch.getTrackDrawable()), new ColorStateList(states, trackColors));
    }

    private void initAdditionalFields() {
        timeZoneDisplay = getView().findViewById(R.id.timeZoneDisplay);
        settingsLogo = getView().findViewById(R.id.settingsLogo);
        sisuPowerLogo = getView().findViewById(R.id.sisuPowerLogo);
        version = getView().findViewById(R.id.versionLabel);
        version.setText("Version: " + BuildConfig.VERSION_NAME + " | Build: " + BuildConfig.VERSION_CODE);
        reminderLabel = getView().findViewById(R.id.reminderLabel);
        lightsLabel = getView().findViewById(R.id.lightsLabel);
        timeZoneTitle = getView().findViewById(R.id.timeZoneTitle);
    }

    private void fillFieldsWithData() {
        settings = dataController.getSettings();
        if(settings != null) {
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
//                colorSchemeChanged = true;
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
        updateSettingsObject();
        saveSettingsObject();
    }

    private void activateLights(Boolean isChecked) {
        String lightsOn = "0";
        if(isChecked) {
            lightsOn = "1";
        }
        apiManager.getColorScheme(this, dataController.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), lightsOn);
        SaveSharedPreference.setLights(parentActivity, lightsOn);
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
        // TODO: This should probably be in DateManager
        String militaryTime = "";
        if(displayTime != null && !displayTime.equals("")) {
            String[] timeSplit = displayTime.split(" ");
            if(timeSplit != null) {
                if(timeSplit[1].equals("AM")) {
                    militaryTime = timeSplit[0];
                }
                else if(timeSplit[1].equals("PM")) {
                    String[] milTimeSplit = timeSplit[0].split(":");
                    int hour = Integer.parseInt(milTimeSplit[0]) + 12;
                    militaryTime = hour + ":" + milTimeSplit[1];
                }
            }
            else {
                militaryTime = "09:00";
            }
        }
        else {
            militaryTime = "09:00";
            utils.showToast("Sorry, there was an issue setting your time and we've set it to 9am. Please try again.", parentActivity);
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
            standardTime = hour + ":" + milTimeSplit[1] + " " + timePeriod;
        }

        return standardTime;
    }

    private void saveSettingsObject() {
        List<UpdateSettingsObject> settingsObjects = new ArrayList<>();

        for(ParameterObject so : settings) {
            settingsObjects.add(new UpdateSettingsObject(so.getName(), so.getValue(), Integer.valueOf(so.getParameter_type_id())));
        }

//        AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject = new AsyncUpdateSettingsJsonObject(2, Integer.valueOf(dataController.getAgent().getAgent_id()), settingsObjects);
        apiManager.sendAsyncUpdateSettings(this, dataController.getAgent().getAgent_id(), 2, settingsObjects);

        utils.createNotificationAlarm(currentSelectedHour, currentSelectedMinute, pendingIntent, parentActivity);
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
                updateSettingsObject();
                saveSettingsObject();
            }
        }, currentSelectedHour, currentSelectedMinute, false);

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.GET_COLOR_SCHEME) {
            AsyncTeamColorSchemeObject colorJson = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncTeamColorSchemeObject.class);
            colorScheme = colorJson.getTheme();
            if(settingsFinished) {
                utils.showToast("Your settings have been updated", parentActivity);
            }
            colorFinished = true;
        }
        else if(returnType == ApiReturnType.UPDATE_SETTINGS) {
            parentActivity.runOnUiThread(() -> {
                dataController.setSettings(settings);
                if(colorScheme != null) {
                    colorSchemeManager.setColorScheme(colorScheme, parentActivity);
                    parentActivity.setActivityColors();
                    setColorScheme();
                }
                else {
                    colorFinished = true;
                }
                if(colorFinished) {
//                        navigationManager.onBackPressed();
                    utils.showToast("Your settings have been updated", parentActivity);
                }
                settingsFinished = true;
            });
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }
}

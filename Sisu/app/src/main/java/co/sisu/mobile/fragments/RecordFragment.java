package co.sisu.mobile.fragments;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.RecordListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.controllers.RecordEventHandler;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.Metric;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener, RecordEventHandler, AsyncServerEventListener {


    private ListView mListView;
    private int selectedYear, selectedMonth, selectedDay;
    private List<Metric> metricList;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private Calendar calendar = Calendar.getInstance();
    private ProgressBar loader;
    private TextView dateDisplay, otherLabel;
    private ImageView calendarLauncher;
    private AsyncActivitySettingsObject[] allActivities;

    public RecordFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
        loader.setVisibility(View.VISIBLE);
        Date d = calendar.getTime();
        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), d, d, parentActivity.getSelectedTeamMarketId());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        calendar = Calendar.getInstance();
        apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), parentActivity.getSelectedTeamMarketId());

//        Date d = calendar.getTime();
//        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), d, d, parentActivity.getSelectedTeamMarketId());
        loader = parentActivity.findViewById(R.id.parentLoader);
        loader.setVisibility(View.VISIBLE);

        initializeCalendarHandler();
        TextView save = parentActivity.findViewById(R.id.saveButton);
        if(save != null) {
            save.setOnClickListener(this);
        }
        setColorScheme();
    }

    private void setLabels() {
        for(Metric metric: metricList) {
            metric.setTitle(parentActivity.localizeLabel(metric.getTitle()));
        }
    }

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.record_list_parent_layout);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());

        dateDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());

        Drawable drawable = getResources().getDrawable(R.drawable.appointment_icon).mutate();
        drawable.setColorFilter(colorSchemeManager.getIconActive(), PorterDuff.Mode.SRC_ATOP);
        calendarLauncher.setImageDrawable(drawable);

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

    private void initializeCalendarHandler() {
        calendarLauncher = getView().findViewById(R.id.calender_date_picker);
        dateDisplay = getView().findViewById(R.id.record_date);

        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        updateDisplayDate(selectedYear, selectedMonth, selectedDay);

        calendarLauncher.setOnClickListener(this);
        dateDisplay.setOnClickListener(this);

    }

    private void updateDisplayDate(int year, int month, int day) {
        selectedYear = year;
        selectedMonth = month;
        selectedDay = day;

        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        String formatDate = year + "/" + month + "/" + day;

        try {
            d = formatter.parse(formatDate);
            Calendar updatedTime = Calendar.getInstance();
            updatedTime.setTime(d);

            TextView dateDisplay = getView().findViewById(R.id.record_date);
            dateDisplay.setText(sdf.format(updatedTime.getTime()));
        } catch (ParseException e) {
            parentActivity.showToast("Error parsing selected date");
            e.printStackTrace();
        }
    }

    private void initializeListView(List<Metric> metricList) {
        if(getView() != null) {
            mListView = getView().findViewById(R.id.record_list_view);
            mListView.setDivider(null);
            mListView.setDividerHeight(30);

            RecordListAdapter adapter = new RecordListAdapter(getContext(), metricList, this, colorSchemeManager, dataController.getFirstOtherActivity(), parentActivity);
            mListView.setAdapter(adapter);
        }

    }

    private void recordMetric(String tab) {
        String switchTab = "";
        switch(tab) {
            case "appts":
                switchTab = "pipeline";
                break;
            case "signed":
                switchTab = "pipeline";
                break;
            case "contract":
                switchTab= "signed";
                break;
            case "closed":
                switchTab = "contract";
                break;
        }
        navigationManager.navigateToClientList(switchTab);
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if(year != selectedYear || month != selectedMonth || day != selectedDay) {
                    updateDisplayDate(year, month, day);
                    updateRecordInfo();
                }
                else {
                    parentActivity.showToast("You have selected the same day");
                }

            }
        }, selectedYear, selectedMonth, selectedDay);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateRecordInfo() {
        String formattedMonth = String.valueOf(selectedMonth + 1);
        if(selectedMonth + 1 < 10) {
            formattedMonth = "0" + formattedMonth;
        }
        String formattedDay = String.valueOf(selectedDay);
        if(selectedDay < 10) {
            formattedDay = "0" + formattedDay;
        }

        String formattedDate = selectedYear + "-" + formattedMonth + "-" + formattedDay;
        parentActivity.updateSelectedRecordDate(formattedDate);
        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), formattedDate, formattedDate, parentActivity.getSelectedTeamMarketId());
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calender_date_picker:
            case R.id.record_date:
                showDatePickerDialog();
                break;
            case R.id.saveButton:
                if(parentActivity.isAdminMode()) {
                    popAdminConfirmDialog();
                }
                else {
                    saveRecords();
                }
                break;
            default:
                break;
        }
    }

    private void saveRecords() {
        if(dataController.getUpdatedRecords().size() > 0) {
            parentActivity.updateRecordedActivities();
            parentActivity.showToast("Records Saved");
        }
        else {
            parentActivity.showToast("There are no changes to save");
        }
    }

    private void popAdminConfirmDialog() {
        String message = "Admin. Do you want to save?";
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setMessage(message)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveRecords();
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

    @Override
    public void onNumberChanged(Metric metric, int newNum) {
        metric.setCurrentNum(newNum);
        dataController.setRecordUpdated(metric);
    }

    @Override
    public void onClientDirectorClicked(Metric metric) {
        switch(metric.getType()) {
            case "1TAPT":
                recordMetric("appts");
                break;
            case "SGND":
                recordMetric("signed");
                break;
            case "UCNTR":
                recordMetric("contract");
                break;
            case "CLSD":
                recordMetric("closed");
                break;
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_ACTIVITIES) {
            AsyncActivitiesJsonObject activitiesObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncActivitiesJsonObject.class);
            dataController.setActivitiesObject(activitiesObject, parentActivity.isRecruiting());
            dataController.setRecordActivities(activitiesObject, parentActivity.isRecruiting());
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    metricList = dataController.getRecordActivities();
                    setLabels();
                    initializeListView(metricList);
                }
            });
//            apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), parentActivity.getSelectedTeamMarketId());
        }
        else if(returnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {
            AsyncActivitySettingsJsonObject settingsJson = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncActivitySettingsJsonObject.class);
            AsyncActivitySettingsObject[] settings = settingsJson.getRecord_activities();
            dataController.setActivitiesSelected(settings);

//            currentActivitiesSorting = dataController.getActivitiesSelected();
//            parentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    loader.setVisibility(View.GONE);
//                    metricList = dataController.getRecordActivities();
//                    setLabels();
//                    initializeListView(metricList);
////                    setupFieldsWithData();
////                    fillListViewWithData(dataController.getActivitiesSelected());
//                }
//            });
            Date d = calendar.getTime();
            apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), d, d, parentActivity.getSelectedTeamMarketId());
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {}

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }
}

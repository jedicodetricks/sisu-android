package co.sisu.mobile.fragments;


import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import co.sisu.mobile.api.AsyncActivities;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.controllers.RecordEventHandler;
import co.sisu.mobile.models.Metric;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener, RecordEventHandler, AsyncServerEventListener {


    private ListView mListView;
    int selectedYear, selectedMonth, selectedDay;
    List<Metric> metricList;
    ParentActivity parentActivity;
    NavigationManager navigationManager;
    Calendar calendar = Calendar.getInstance();
    ProgressBar loader;

    public RecordFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
//        initializeListView(dataController.updateRecordMetrics());
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
        calendar = Calendar.getInstance();
        Date d = calendar.getTime();
        new AsyncActivities(this, parentActivity.getAgentInfo().getAgent_id(), d, d, parentActivity.getJwtObject()).execute();
        loader = parentActivity.findViewById(R.id.parentLoader);
        loader.setVisibility(View.VISIBLE);

        initializeCalendarHandler();
        TextView save = parentActivity.findViewById(R.id.saveButton);
        save.setOnClickListener(this);
    }

    private void initializeCalendarHandler() {
        final ImageView calendarLauncher = getView().findViewById(R.id.calender_date_picker);
        final TextView dateDisplay = getView().findViewById(R.id.record_date);

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

            RecordListAdapter adapter = new RecordListAdapter(getContext(), metricList, this);
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

        dialog.show();
    }

    private void updateRecordInfo() {
        String formattedMonth = String.valueOf(selectedMonth + 1);
        if(selectedMonth < 10) {
            formattedMonth = "0" + formattedMonth;
        }
        String formattedDay = String.valueOf(selectedDay);
        if(selectedDay < 10) {
            formattedDay = "0" + formattedDay;
        }

        String formattedDate = selectedYear + "-" + formattedMonth + "-" + formattedDay;
        parentActivity.updateSelectedRecordDate(formattedDate);
        new AsyncActivities(this, parentActivity.getAgentInfo().getAgent_id(), formattedDate, formattedDate, parentActivity.getJwtObject()).execute();
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
                updateRecords();
                saveRecords();
                break;
            default:
                break;
        }
    }

    private void updateRecords() {
        // TODO: 4/30/2018 set activities object model with current stats
    }

    private void saveRecords() {
        if(parentActivity.getUpdatedRecords().size() > 0) {
            parentActivity.updateRecordedActivities();
            parentActivity.showToast("Records Saved");
        }
        else {
            parentActivity.showToast("There are no changes to save");
        }

    }

    @Override
    public void onNumberChanged(Metric metric, int newNum) {
                metric.setCurrentNum(newNum);
                parentActivity.setRecordUpdated(metric);
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
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Activities")) {
            parentActivity.setActivitiesObject(returnObject);
            parentActivity.setRecordObject(returnObject);
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    metricList = parentActivity.getRecordObject();
                    initializeListView(metricList);
                }
            });
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }
}
